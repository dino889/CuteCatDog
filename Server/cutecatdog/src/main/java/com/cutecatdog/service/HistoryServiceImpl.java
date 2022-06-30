package com.cutecatdog.service;

import java.util.List;

import com.cutecatdog.mapper.HistoryMapper;
import com.cutecatdog.model.history.HistoryDto;
import com.cutecatdog.model.history.HistoryRequestDto;
import com.cutecatdog.model.history.HistoryTimeDto;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HistoryServiceImpl implements HistoryService{

  @Autowired
  private SqlSession sqlSession;

  @Override
  public List<HistoryDto> findHistory(int petId) throws Exception {
    return sqlSession.getMapper(HistoryMapper.class).selectHistory(petId);
  }

  @Override
  public List<HistoryDto> findHistoryTime(HistoryTimeDto historyTimeDto) throws Exception {
    return sqlSession.getMapper(HistoryMapper.class).selectHistoryTime(historyTimeDto);
  }

  @Override
  public boolean addHistroy(HistoryRequestDto historyRequestDto) throws Exception {
    return sqlSession.getMapper(HistoryMapper.class).insertHistory(historyRequestDto);
  }

  @Override
  public boolean removeHistory(int id) throws Exception{
    return sqlSession.getMapper(HistoryMapper.class).deleteHistory(id);
  }

  @Override
  public HistoryDto findHistoryDetail(int id) throws Exception {
    return sqlSession.getMapper(HistoryMapper.class).selectHistoryDetail(id);
  }
  
}
