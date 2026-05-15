package com.example.demo.mapper;

import com.example.demo.dto.TicketQueryRequest;
import com.example.demo.entity.Ticket;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TicketMapper {

    int insertTicket(Ticket ticket);

    Ticket selectById(Long id);

    List<Ticket> selectList(TicketQueryRequest request);

    int updateStatus(@Param("ticketId") Long ticketId,
                     @Param("status") String status,
                     @Param("handlerId") Long handlerId,
                     @Param("expectedStatus") String expectedStatus);

    int assignHandler(@Param("ticketId") Long ticketId,
                      @Param("handlerId") Long handlerId,
                      @Param("expectedStatus") String expectedStatus);
    int updatePriority(@Param("ticketId") Long ticketId,
                       @Param("priority") String priority);
}