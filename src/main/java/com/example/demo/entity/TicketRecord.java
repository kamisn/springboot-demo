package com.example.demo.entity;

import java.time.LocalDateTime;

public class TicketRecord {
    private Long id;
    private Long ticketId;      // The ID of the ticket being operated on
    private Long operatorId;    // The ID of the user who performed the action
    private String actionType;  // e.g., "CREATE", "UPDATE_STATUS", "ASSIGN"
    private String content;     // Description of the action, e.g., "Status changed from OPEN to IN_PROGRESS"
    private LocalDateTime createTime; // When the action occurred

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
