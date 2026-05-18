package com.example.demo.controller;

import com.example.demo.common.BusinessException;
import com.example.demo.common.CurrentUserContext;
import com.example.demo.common.ErrorCode;
import com.example.demo.common.Result;
import com.example.demo.dto.CreateTicketRequest;
import com.example.demo.dto.TicketQueryRequest;
import com.example.demo.service.TicketService;
import com.example.demo.vo.TicketDetailVO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public Result<Long> createTicket(@RequestBody CreateTicketRequest request) {
        Long ticketId = ticketService.createTicket(request);
        return Result.success(ticketId);
    }
    @GetMapping("/{id}")
    public Result<TicketDetailVO> getTicketDetail(@PathVariable Long id) {
        return Result.success(ticketService.getTicketWithRecords(id));
    }

    @GetMapping
    public Result<?> listTickets(TicketQueryRequest request) {
        if (!"ADMIN".equals(CurrentUserContext.getRole())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        return Result.success(ticketService.listTickets(request));
    }

    @GetMapping("/my")
    public Result<?> myTickets(TicketQueryRequest request) {
        request.setCreatorId(CurrentUserContext.getUserId());
        return Result.success(ticketService.listTickets(request));
    }

    @PutMapping("/{id}/close")
    public Result<Boolean> closeTicket(@PathVariable Long id) {
        return Result.success(ticketService.closeOrReopenTicket(id, "CLOSE"));
    }

    @PutMapping("/{id}/reopen")
    public Result<Boolean> reopenTicket(@PathVariable Long id) {
        return Result.success(ticketService.closeOrReopenTicket(id, "REOPEN"));
    }
}
