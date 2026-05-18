package com.example.demo.service;

import com.example.demo.common.BusinessException;
import com.example.demo.common.CurrentUserContext;
import com.example.demo.common.ErrorCode;
import com.example.demo.common.enums.TicketPriority;
import com.example.demo.common.enums.TicketStatus;
import com.example.demo.dto.*;
import com.example.demo.entity.Ticket;
import com.example.demo.entity.TicketRecord;
import com.example.demo.entity.User;
import com.example.demo.mapper.TicketMapper;

import com.example.demo.mapper.TicketRecordMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.vo.TicketDetailVO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TicketService {

    private final TicketMapper ticketMapper;
    private final UserMapper userMapper;
    private final TicketRecordMapper ticketRecordMapper;
    private final StringRedisTemplate stringRedisTemplate;


    public TicketService(TicketMapper ticketMapper, UserMapper userMapper, TicketRecordMapper ticketRecordMapper, StringRedisTemplate stringRedisTemplate) {
        this.ticketMapper = ticketMapper;
        this.userMapper = userMapper;
        this.ticketRecordMapper = ticketRecordMapper;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 创建工单核心方法
     */
    public Long createTicket(CreateTicketRequest request) {
        // 1. 获取当前登录用户
        Long currentUserId = CurrentUserContext.getUserId();
        // 幂等：Redis SETNX 防重复提交
        if (StringUtils.hasText(request.getRequestId())) {
            String redisKey = "ticket:request:" + request.getRequestId();
            Boolean success = stringRedisTemplate.opsForValue().setIfAbsent(redisKey, "1");
            if (Boolean.FALSE.equals(success)) {
                throw new BusinessException(ErrorCode.DUPLICATE_REQUEST);
            }
            stringRedisTemplate.expire(redisKey, 5, java.util.concurrent.TimeUnit.MINUTES);
        }
        // 2. 参数校验
        if (request == null
                || !StringUtils.hasText(request.getTitle())
                || !StringUtils.hasText(request.getDescription())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }
        if (currentUserId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        // 3. 构建工单对象
        Ticket ticket = new Ticket();
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setCategory(request.getCategory());
        ticket.setImpactScope(request.getImpactScope());
        ticket.setUrgency(request.getUrgency());
        ticket.setCreatorId(currentUserId);
        ticket.setStatus(TicketStatus.OPEN.name());

        // 4. 核心：自动计算优先级（后端计算，不依赖前端）
        String calculatedPriority = calculatePriority(request.getCategory(), request.getUrgency(), request.getImpactScope());
        ticket.setPriority(calculatedPriority);

        String reason = generatePriorityReason(request.getCategory(), request.getImpactScope(), request.getUrgency());
        ticket.setPriorityReason(reason);

        // 5. 核心：根据优先级计算 SLA 截止时间
        LocalDateTime deadline = calculateSLA(calculatedPriority);
        ticket.setDeadlineTime(deadline);

        // 6. 保存工单
        ticketMapper.insertTicket(ticket);

        // 7. 核心：记录工单操作日志（流转记录）
        TicketRecord record = new TicketRecord();
        record.setTicketId(ticket.getId());
        record.setOperatorId(currentUserId);
        record.setActionType("CREATE");
        record.setContent("用户提交了工单，系统自动定级为: " + calculatedPriority + "。定级原因: " + reason);
        ticketRecordMapper.insertRecord(record);

        return ticket.getId();
    }

    /**
     * Impact × Urgency 矩阵 + 服务关键性修正
     * 将 category / impactScope / urgency 映射为 P0~P3
     */
    private String calculatePriority(String category, String urgency, String impactScope) {
        // 1. impactScope → impact 等级
        String impact;
        if ("ALL".equals(impactScope)) {
            impact = "HIGH";
        } else if ("DEPARTMENT".equals(impactScope) || "MULTIPLE_USERS".equals(impactScope)) {
            impact = "MEDIUM";
        } else {
            impact = "LOW";
        }

        // 2. 矩阵判断
        if ("HIGH".equals(impact) && "HIGH".equals(urgency))   return TicketPriority.P0.name();
        if ("HIGH".equals(impact) && !"LOW".equals(urgency))   return TicketPriority.P1.name();
        if ("MEDIUM".equals(impact) && "HIGH".equals(urgency)) return TicketPriority.P1.name();
        if ("LOW".equals(impact) && "HIGH".equals(urgency))    return TicketPriority.P2.name();
        if ("MEDIUM".equals(impact) && "MEDIUM".equals(urgency)) return TicketPriority.P2.name();
        return TicketPriority.P3.name();
    }

    private String generatePriorityReason(String category, String impactScope, String urgency) {
        String impact;
        if ("ALL".equals(impactScope)) impact = "全体用户";
        else if ("DEPARTMENT".equals(impactScope) || "MULTIPLE_USERS".equals(impactScope)) impact = "部门/多人";
        else impact = "个人";

        boolean critical = "LOGIN".equals(category) || "PAYMENT".equals(category) || "CORE_API".equals(category);
        StringBuilder sb = new StringBuilder();
        sb.append("影响范围=").append(impact);
        sb.append(", 紧急度=").append(urgency);
        if (critical) sb.append(", 核心服务");
        return sb.toString();
    }

    /**
     * 核心：根据优先级计算 SLA 超时时间
     */
    private LocalDateTime calculateSLA(String priority) {
        LocalDateTime now = LocalDateTime.now();
        switch (priority) {
            case "P0": return now.plusHours(2);
            case "P1": return now.plusHours(8);
            case "P2": return now.plusDays(1);
            case "P3": return now.plusDays(3);
            default: return now.plusDays(3);
        }
    }

    public Ticket getTicketDetail(Long id) {

        if (id == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        Ticket ticket = ticketMapper.selectById(id);

        if (ticket == null) {
            throw new BusinessException(ErrorCode.TICKET_NOT_FOUND);
        }
        if ("HANDLER".equals(CurrentUserContext.getRole())&& !CurrentUserContext.getUserId().equals(ticket.getHandlerId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        if ("USER".equals(CurrentUserContext.getRole())&& !CurrentUserContext.getUserId().equals(ticket.getCreatorId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        return ticket;
    }

    public List<Ticket> listTickets(TicketQueryRequest request) {
        if (request == null) {
            request = new TicketQueryRequest();
        }

        return ticketMapper.selectList(request);
    }

    public Boolean handlerUpdateStatus(UpdateTicketStatusRequest request) {
        if (request == null
                || request.getTicketId() == null
                || !StringUtils.hasText(request.getStatus())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        if (!"HANDLER".equals(CurrentUserContext.getRole())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        Ticket ticket = ticketMapper.selectById(request.getTicketId());
        if (ticket == null) {
            throw new BusinessException(ErrorCode.TICKET_NOT_FOUND);
        }
        if (!CurrentUserContext.getUserId().equals(ticket.getHandlerId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        TicketStatus current = TicketStatus.fromString(ticket.getStatus());
        TicketStatus target = TicketStatus.fromString(request.getStatus());

        if (current == null || target == null || !current.canTransitionTo(target)) {
            throw new BusinessException(ErrorCode.INVALID_STATUS_TRANSITION,
                    "当前状态 " + ticket.getStatus() + " 不允许变更为 " + request.getStatus());
        }

        int rows = ticketMapper.updateStatus(
                request.getTicketId(),
                target.name(),
                ticket.getHandlerId(),
                current.name()
        );
        if (rows == 0) {
            throw new BusinessException(ErrorCode.TICKET_STATUS_CHANGED);
        }

        TicketRecord record = new TicketRecord();
        record.setTicketId(ticket.getId());
        record.setOperatorId(CurrentUserContext.getUserId());
        record.setActionType("STATUS_CHANGE");
        record.setContent("处理人将工单状态从 " + current.name() + " 变更为 " + target.name());
        ticketRecordMapper.insertRecord(record);

        return true;
    }

    public Boolean closeOrReopenTicket(Long ticketId, String action) {
        if (ticketId == null || !StringUtils.hasText(action)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        String role = CurrentUserContext.getRole();
        if (!"ADMIN".equals(role) && !"USER".equals(role)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        Ticket ticket = ticketMapper.selectById(ticketId);
        if (ticket == null) {
            throw new BusinessException(ErrorCode.TICKET_NOT_FOUND);
        }

        if ("USER".equals(role) && !CurrentUserContext.getUserId().equals(ticket.getCreatorId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        TicketStatus current = TicketStatus.fromString(ticket.getStatus());
        TicketStatus target;
        String operatorName;

        if ("CLOSE".equals(action)) {
            target = TicketStatus.CLOSED;
            operatorName = "关闭了工单";
        } else if ("REOPEN".equals(action)) {
            target = TicketStatus.OPEN;
            operatorName = "重开了工单";
        } else {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        if (current == null || !current.canTransitionTo(target)) {
            throw new BusinessException(ErrorCode.INVALID_STATUS_TRANSITION,
                    "当前状态 " + ticket.getStatus() + " 不允许变更为 " + target.name());
        }

        int rows = ticketMapper.updateStatus(ticketId, target.name(), null, current.name());
        if (rows == 0) {
            throw new BusinessException(ErrorCode.TICKET_STATUS_CHANGED);
        }

        TicketRecord record = new TicketRecord();
        record.setTicketId(ticket.getId());
        record.setOperatorId(CurrentUserContext.getUserId());
        record.setActionType("STATUS_CHANGE");
        record.setContent(role + " " + operatorName + "（" + current.name() + " → " + target.name() + "）");
        ticketRecordMapper.insertRecord(record);

        return true;
    }

    public Boolean updateTicketPriority(UpdateTicketPriorityRequest request) {

        if (request == null
                || request.getTicketId() == null
                || !StringUtils.hasText(request.getPriority())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }
        if (!"ADMIN".equals(CurrentUserContext.getRole())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        if (!TicketPriority.isValid(request.getPriority())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        Ticket ticket = ticketMapper.selectById(request.getTicketId());

        if (ticket == null) {
            throw new BusinessException(ErrorCode.TICKET_NOT_FOUND);
        }

        int rows = ticketMapper.updatePriority(request.getTicketId(), request.getPriority());

        return rows > 0;
    }
    public Boolean assignHandler(AssignTicketRequest request)  {
        if (request == null
                || request.getTicketId() == null
                || request.getHandlerId() == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        } // 这一步是判断前端传来的参数是否合法。

        // Service 层兜底校验。即使前面有 AdminInterceptor，这里也保留。
        if (!"ADMIN".equals(CurrentUserContext.getRole())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        // 1. 工单是否存在
        Ticket ticket = ticketMapper.selectById(request.getTicketId());

        if (ticket == null) {
            throw new BusinessException(ErrorCode.TICKET_NOT_FOUND);
        }

        // 只能分配 OPEN 状态的工单
        TicketStatus currentStatus = TicketStatus.fromString(ticket.getStatus());
        if (currentStatus == null || !currentStatus.canTransitionTo(TicketStatus.PROCESSING)) {
            throw new BusinessException(ErrorCode.INVALID_STATUS_TRANSITION,
                    "当前状态 " + ticket.getStatus() + " 不允许分配处理人");
        }

        // 2. 处理人是否存在
        User handler = userMapper.selectById(request.getHandlerId());
        if (handler == null || !"HANDLER".equals(handler.getRole())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        // 3. 执行分配更新
        // 「"OPEN"」是你写的业务规则，不是从 request 里来的
        int rows = ticketMapper.assignHandler(
                request.getTicketId(),    // ← 用户传的
                request.getHandlerId(),   // ← 用户传的
                "OPEN"                    // ← 代码里写死的业务规则
        );

        // 【新增核心逻辑 2】：更新成功后，立即写入一条流转记录
        if (rows > 0) {
            TicketRecord record = new TicketRecord();
            record.setTicketId(ticket.getId());
            // 记录当前操作人（管理员的ID）
            record.setOperatorId(CurrentUserContext.getUserId());
            record.setActionType("ASSIGN");
            // 记录有业务价值的文案，后续在前端工单详情的时间轴里展示
            record.setContent("管理员将工单指派给了处理人: " + handler.getUsername());

            ticketRecordMapper.insertRecord(record);
        }

        return rows > 0;
    }

    public TicketDetailVO getTicketWithRecords(Long id) {
        Ticket ticket = getTicketDetail(id);
        List<TicketRecord> records = ticketRecordMapper.selectByTicketId(id);

        TicketDetailVO vo = new TicketDetailVO();
        vo.setTicket(ticket);
        vo.setRecords(records);
        return vo;

    }

}
