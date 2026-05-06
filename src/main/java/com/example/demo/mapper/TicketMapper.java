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
                     @Param("status") String status);
    //#{ticketId}
    //#{status}
    //如果不写 @Param，后面容易出现参数名对不上的问题
    int updatePriority(@Param("ticketId") Long ticketId,
                       @Param("priority") String priority);
}