package com.example.demo.service;

import com.example.demo.common.BusinessException;
import com.example.demo.common.ErrorCode;
import com.example.demo.common.enums.TicketPriority;
import com.example.demo.common.enums.TicketStatus;
import com.example.demo.dto.CreateTicketRequest;
import com.example.demo.dto.TicketQueryRequest;
import com.example.demo.dto.UpdateTicketPriorityRequest;
import com.example.demo.dto.UpdateTicketStatusRequest;
import com.example.demo.entity.Ticket;
import com.example.demo.mapper.TicketMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class TicketService {

    private final TicketMapper ticketMapper;

    public TicketService(TicketMapper ticketMapper) {
        this.ticketMapper = ticketMapper;
    }

    public Long createTicket(CreateTicketRequest request) {
        if (request == null
                || !StringUtils.hasText(request.getTitle())
                || !StringUtils.hasText(request.getDescription())
                || request.getCreatorId() == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        Ticket ticket = new Ticket();
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setCreatorId(request.getCreatorId());
        ticket.setHandlerId(request.getHandlerId());

        ticket.setStatus("OPEN");//上面这几条都是用户输入时影响不大的的没必要写枚举值

        if (StringUtils.hasText(request.getPriority())) {
            if (!TicketPriority.isValid(request.getPriority())) {
                throw new BusinessException(ErrorCode.BAD_REQUEST);//判断优先级是否不存在不存在就抛业务异常。
            }
            ticket.setPriority(request.getPriority());
        } else {
            ticket.setPriority("NORMAL");
        }

        ticketMapper.insertTicket(ticket);

        return ticket.getId();
    }

    public Ticket getTicketDetail(Long id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        Ticket ticket = ticketMapper.selectById(id);

        if (ticket == null) {
            throw new BusinessException(ErrorCode.TICKET_NOT_FOUND);
        }

        return ticket;
    }

    public List<Ticket> listTickets(TicketQueryRequest request) {
        if (request == null) {
            request = new TicketQueryRequest();
        }

        return ticketMapper.selectList(request);
    }

    public Boolean updateTicketStatus(UpdateTicketStatusRequest request) {
        if (request == null
                || request.getTicketId() == null
                || !StringUtils.hasText(request.getStatus())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        if (!TicketStatus.isValid(request.getStatus())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        Ticket ticket = ticketMapper.selectById(request.getTicketId());

        if (ticket == null) {
            throw new BusinessException(ErrorCode.TICKET_NOT_FOUND);
        }

        int rows = ticketMapper.updateStatus(request.getTicketId(), request.getStatus());

        return rows > 0;
    }
    public Boolean updateTicketPriority(UpdateTicketPriorityRequest request) {
        if (request == null
                || request.getTicketId() == null
                || !StringUtils.hasText(request.getPriority())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
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
}