package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.dto.AssignTicketRequest;
import com.example.demo.service.TicketService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/ticket")
public class AdminTicketController {

    private final TicketService ticketService;

    public AdminTicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping("/assign")
    public Result<Boolean> assignHandler(@RequestBody AssignTicketRequest request) {
        return Result.success(ticketService.assignHandler(request));
    }
}