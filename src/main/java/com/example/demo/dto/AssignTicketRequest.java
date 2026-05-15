package com.example.demo.dto;

public class AssignTicketRequest {

    private Long ticketId;
    private Long handlerId;
    private String expectedStatus;

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public Long getHandlerId() {
        return handlerId;
    }

    public String getExpectedStatus() {
        return expectedStatus;
    }

    public void setExpectedStatus(String expectedStatus) {
        this.expectedStatus = expectedStatus;
    }

    public void setHandlerId(Long handlerId) {
        this.handlerId = handlerId;
    }
}