package com.example.demo.common.enums;

public enum TicketPriority {

    LOW,
    NORMAL,
    HIGH,
    URGENT;

    public static boolean isValid(String priority) {
        if (priority == null) {
            return false;
        }

        for (TicketPriority ticketPriority : TicketPriority.values()) {
            if (ticketPriority.name().equals(priority)) {
                return true;
            }
        }

        return false;
    }
}