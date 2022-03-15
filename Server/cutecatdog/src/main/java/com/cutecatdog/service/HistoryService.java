package com.cutecatdog.service;

import java.util.List;

import com.cutecatdog.model.history.HistoryDto;
import com.cutecatdog.model.history.HistoryTimeDto;

public interface HistoryService {

  public List<HistoryDto> findHistory(int petId) throws Exception;

  public List<HistoryDto> findHistoryTime(HistoryTimeDto historyTimeDto) throws Exception;
  
}
