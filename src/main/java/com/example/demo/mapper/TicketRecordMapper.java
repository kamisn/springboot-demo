package com.example.demo.mapper;

import com.example.demo.entity.TicketRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TicketRecordMapper {

    // Insert a new ticket flow record
    int insertRecord(TicketRecord record);

    // Retrieve all flow records for a specific ticket
    List<TicketRecord> selectByTicketId(Long ticketId);
}
