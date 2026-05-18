package com.example.demo.scheduler;

import com.example.demo.entity.Ticket;
import com.example.demo.entity.TicketRecord;
import com.example.demo.mapper.TicketMapper;
import com.example.demo.mapper.TicketRecordMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class TicketScheduler {

    private final TicketMapper ticketMapper;
    private final TicketRecordMapper ticketRecordMapper;

    public TicketScheduler(TicketMapper ticketMapper, TicketRecordMapper ticketRecordMapper) {
        this.ticketMapper = ticketMapper;
        this.ticketRecordMapper = ticketRecordMapper;
    }

    @Scheduled(cron = "0 */10 * * * ?")
    public void escalateOverdueTickets() {
        // 1. 扫出所有超时工单
        List<Ticket> overdueTickets = ticketMapper.selectOverdueTickets(LocalDateTime.now());

        // 2. 逐条处理
        for (Ticket ticket : overdueTickets) {
            ticketMapper.markOverdue(ticket.getId());

            TicketRecord record = new TicketRecord();
            record.setTicketId(ticket.getId());
            record.setOperatorId(0L);
            record.setActionType("OVERDUE_ESCALATED");
            record.setContent("工单超时自动升级，SLA截止时间: " + ticket.getDeadlineTime()
                    + "，当前升级次数: " + (ticket.getEscalationLevel() + 1));
            ticketRecordMapper.insertRecord(record);
        }
    }
}