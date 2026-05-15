package com.example.demo.common.enums;

public enum TicketStatus {

    OPEN,
    PROCESSING,
    RESOLVED,
    CLOSED,
    OVERDUE,   // 新增：超时
    ESCALATED; // 新增：已升级

    public static boolean isValid(String status) {
        if (status == null) {
            return false;
        }

        for (TicketStatus ticketStatus : TicketStatus.values()) {
            if (ticketStatus.name().equals(status)) {
                return true;
            }
        }

        return false;
    }
}
