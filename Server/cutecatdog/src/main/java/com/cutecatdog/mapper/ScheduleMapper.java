package com.cutecatdog.mapper;

import java.sql.SQLException;
import java.util.List;

import com.cutecatdog.model.Calendar.ScheduleDto;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ScheduleMapper {

    boolean insertSchedule(ScheduleDto scheduleDto) throws SQLException;

    List<ScheduleDto> selectSchedule(int userId) throws SQLException;
    
    List<ScheduleDto> selectSchedulePet(int petId) throws SQLException;

    List<ScheduleDto> selectScheduleIno(int petId) throws SQLException;
    
    List<ScheduleDto> selectScheduleWalk(int petId) throws SQLException;

    boolean updateSchedule(ScheduleDto scheduleDto) throws SQLException;

    boolean deleteSchedule(int id) throws SQLException;

    ScheduleDto selectScheduleDetail(int id) throws SQLException;

    List<ScheduleDto> selectScheduleDate(ScheduleDto scheduleDto) throws SQLException;

    List<ScheduleDto> selectScheduleHour() throws SQLException;

    List<ScheduleDto> selectScheduleWeek(int userId) throws SQLException;
    
}
