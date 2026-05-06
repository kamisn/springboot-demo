package com.example.demo.common.enums;

public enum TicketStatus {

    OPEN,
    PROCESSING,
    RESOLVED,
    CLOSED;

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