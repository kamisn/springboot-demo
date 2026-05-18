package com.example.demo.vo;

import com.example.demo.entity.Ticket;
import com.example.demo.entity.TicketRecord;

import java.util.List;

public class TicketDetailVO {
    private Ticket ticket;
    private List<TicketRecord> records;

    public Ticket getTicket() { return ticket; }
    public void setTicket(Ticket ticket) { this.ticket = ticket; }
    public List<TicketRecord> getRecords() { return records; }
    public void setRecords(List<TicketRecord> records) { this.records = records; }
}
