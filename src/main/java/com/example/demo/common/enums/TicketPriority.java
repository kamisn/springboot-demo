package com.example.demo.common.enums;

public enum TicketPriority {

    P0, // 最高优先级（比如：影响全体用户的严重故障，2小时 SLA）
    P1, // 高优先级（比如：部门级影响且紧急，8小时 SLA）
    P2, // 中优先级（比如：用户紧急但影响范围有限，1天 SLA）
    P3; // 低优先级（比如：普通咨询或建议，3天 SLA）

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
