package com.cutecatdog.service;

import java.util.List;

import com.cutecatdog.mapper.ScheduleMapper;
import com.cutecatdog.model.Calendar.ScheduleDto;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScheduleServiceImpl implements ScheduleService{

    @Autowired
    SqlSession sqlSession;

    @Override
    public boolean addSchedule(ScheduleDto scheduleDto) throws Exception {
        return sqlSession.getMapper(ScheduleMapper.class).insertSchedule(scheduleDto);
    }

    @Override
    public List<ScheduleDto> findSchedule(int userId) throws Exception {
        return sqlSession.getMapper(ScheduleMapper.class).selectSchedule(userId);
    }

    @Override
    public List<ScheduleDto> findSchedulePet(int petId) throws Exception {
        return sqlSession.getMapper(ScheduleMapper.class).selectSchedulePet(petId);
    }

    @Override
    public List<ScheduleDto> findScheduleIno(int petId) throws Exception {
        return sqlSession.getMapper(ScheduleMapper.class).selectScheduleIno(petId);
    }

    @Override
    public List<ScheduleDto> findScheduleWalk(int petId) throws Exception {
        return sqlSession.getMapper(ScheduleMapper.class).selectScheduleWalk(petId);
    }

    @Override
    public boolean modifySchedule(ScheduleDto scheduleDto) throws Exception {
        return sqlSession.getMapper(ScheduleMapper.class).updateSchedule(scheduleDto);
    }

    @Override
    public boolean removeSchedule(int id) throws Exception {
        return sqlSession.getMapper(ScheduleMapper.class).deleteSchedule(id);
    }

    @Override
    public ScheduleDto findScheduleDetail(int id) throws Exception {
        return sqlSession.getMapper(ScheduleMapper.class).selectScheduleDetail(id);
    }

    @Override
    public List<ScheduleDto> findScheduleDate(ScheduleDto scheduleDto) throws Exception {
        return sqlSession.getMapper(ScheduleMapper.class).selectScheduleDate(scheduleDto);
    }

    @Override
    public List<ScheduleDto> findScheduleWeek(int userId) throws Exception {
        return sqlSession.getMapper(ScheduleMapper.class).selectScheduleWeek(userId);
    }

    @Override
    public List<ScheduleDto> findScheduleHour() throws Exception {
        return sqlSession.getMapper(ScheduleMapper.class).selectScheduleHour();
    }
    
}
