package com.example.demo.common.enums;

public enum TicketStatus {

    OPEN,
    PROCESSING,
    RESOLVED,
    CLOSED,
    OVERDUE,
    ESCALATED;

    /**
     * 状态机：当前状态是否允许变更为目标状态
     */
    public boolean canTransitionTo(TicketStatus target) {
        if (target == null) return false;
        return switch (this) {
            case OPEN       -> target == PROCESSING;
            case PROCESSING -> target == RESOLVED || target == PROCESSING;
            case RESOLVED   -> target == PROCESSING || target == CLOSED;
            case CLOSED     -> target == OPEN;
            case OVERDUE    -> target == PROCESSING;
            case ESCALATED  -> target == PROCESSING;
        };
    }

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

    public static TicketStatus fromString(String status) {
        if (status == null) return null;
        try {
            return valueOf(status);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
