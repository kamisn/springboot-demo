package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.dto.CreateTicketRequest;
import com.example.demo.dto.TicketQueryRequest;
import com.example.demo.dto.UpdateTicketPriorityRequest;
import com.example.demo.dto.UpdateTicketStatusRequest;
import com.example.demo.entity.Ticket;
import com.example.demo.service.TicketService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ticket")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping("/create")
    public Result<Long> createTicket(@RequestBody CreateTicketRequest request) {
        Long ticketId = ticketService.createTicket(request);
        return Result.success(ticketId);
//        前端传 title、description、priority、creatorId、handlerId
//        Controller 调 Service
//        Service 校验参数，设置默认状态 OPEN
//        Mapper 插入 ticket 表
//        返回新工单 id
    }
    @GetMapping("/{id}")
    public Result<Ticket> getTicketDetail(@PathVariable Long id) {
        Ticket ticket = ticketService.getTicketDetail(id);
        return Result.success(ticket);
    }

    @GetMapping("/list")
    public Result<List<Ticket>> listTickets(TicketQueryRequest request) {
        List<Ticket> tickets = ticketService.listTickets(request);
        return Result.success(tickets);
    }

    @PostMapping("/status")
    public Result<Boolean> updateTicketStatus(@RequestBody UpdateTicketStatusRequest request) {
        Boolean result = ticketService.updateTicketStatus(request);
        return Result.success(result);
    }
    @PostMapping("/priority")
    public Result<Boolean> updateTicketPriority(@RequestBody UpdateTicketPriorityRequest request) {
        Boolean result = ticketService.updateTicketPriority(request);
        return Result.success(result);
    }
}