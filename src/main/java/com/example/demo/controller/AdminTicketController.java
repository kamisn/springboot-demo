package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.dto.AssignTicketRequest;
import com.example.demo.dto.UpdateTicketPriorityRequest;
import com.example.demo.service.TicketService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/tickets")
public class AdminTicketController {

    private final TicketService ticketService;

    public AdminTicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PutMapping("/{id}/assign")
    public Result<Boolean> assignHandler(@PathVariable Long id,
                                         @RequestBody AssignTicketRequest request) {
        request.setTicketId(id);
        return Result.success(ticketService.assignHandler(request));
    }

    @PutMapping("/{id}/close")
    public Result<Boolean> closeTicket(@PathVariable Long id) {
        return Result.success(ticketService.closeOrReopenTicket(id, "CLOSE"));
    }

    @PutMapping("/{id}/priority")
    public Result<Boolean> updatePriority(@PathVariable Long id,
                                          @RequestBody UpdateTicketPriorityRequest request) {
        request.setTicketId(id);
        return Result.success(ticketService.updateTicketPriority(request));
    }
}
