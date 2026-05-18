package com.example.demo.dto;

public class CreateTicketRequest {
    private String title;
    private String description;
    // 移除了前端直接传的 priority
    private String category;
    private String urgency;
    private String impactScope;
    private String requestId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    public String getImpactScope() {
        return impactScope;
    }

    public void setImpactScope(String impactScope) {
        this.impactScope = impactScope;
    }

    public String getRequestId() { return requestId; }

    public void setRequestId(String requestId) { this.requestId = requestId; }
}
