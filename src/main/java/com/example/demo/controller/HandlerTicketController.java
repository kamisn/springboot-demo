package com.example.demo.controller;

import com.example.demo.common.CurrentUserContext;
import com.example.demo.common.Result;
import com.example.demo.dto.TicketQueryRequest;
import com.example.demo.dto.UpdateTicketStatusRequest;
import com.example.demo.entity.Ticket;
import com.example.demo.service.TicketService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/handler/tickets")
public class HandlerTicketController {

    private final TicketService ticketService;

    public HandlerTicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/my")
    public Result<List<Ticket>> myTickets(TicketQueryRequest request) {
        request.setHandlerId(CurrentUserContext.getUserId());
        return Result.success(ticketService.listTickets(request));
    }

    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(@PathVariable Long id,
                                        @RequestBody UpdateTicketStatusRequest request) {
        request.setTicketId(id);
        return Result.success(ticketService.handlerUpdateStatus(request));
    }
}
