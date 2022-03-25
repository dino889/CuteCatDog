package com.cutecatdog.service;

import java.util.List;

import com.cutecatdog.model.Calendar.ScheduleDto;

public interface ScheduleService {

    boolean addSchedule(ScheduleDto scheduleDto) throws Exception;

    List<ScheduleDto> findSchedule(int userId) throws Exception;
    
    List<ScheduleDto> findSchedulePet(int petId) throws Exception;
    
    List<ScheduleDto> findScheduleIno(int petId) throws Exception;
    
    List<ScheduleDto> findScheduleWalk(int petId) throws Exception;

    List<ScheduleDto> findScheduleDetail(ScheduleDto scheduleDto) throws Exception;

    boolean modifySchedule(ScheduleDto scheduleDto) throws Exception;

    boolean removeSchedule(int id) throws Exception;

    List<ScheduleDto> findScheduleWeek(int userId) throws Exception;

}
