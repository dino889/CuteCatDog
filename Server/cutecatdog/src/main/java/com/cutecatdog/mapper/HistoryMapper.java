package com.cutecatdog.mapper;

import java.sql.SQLException;
import java.util.List;

import com.cutecatdog.model.history.HistoryDto;
import com.cutecatdog.model.history.HistoryRequestDto;
import com.cutecatdog.model.history.HistoryTimeDto;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HistoryMapper {

  public List<HistoryDto> selectHistory(int petId) throws SQLException;

  public List<HistoryDto> selectHistoryTime(HistoryTimeDto historyTimeDto) throws SQLException;

  public boolean insertHistory(HistoryRequestDto historyRequestDto) throws SQLException;

  public boolean deleteHistory(int id) throws SQLException;

  public HistoryDto selectHistoryDetail(int id) throws SQLException;
  
}
