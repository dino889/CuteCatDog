package com.cutecatdog.service;

import java.util.List;

import com.cutecatdog.model.history.HistoryDto;
import com.cutecatdog.model.history.HistoryRequestDto;
import com.cutecatdog.model.history.HistoryTimeDto;

public interface HistoryService {

  public List<HistoryDto> findHistory(int petId) throws Exception;

  public HistoryDto findHistoryDetail(int id) throws Exception;

  public List<HistoryDto> findHistoryTime(HistoryTimeDto historyTimeDto) throws Exception;

  public boolean addHistroy(HistoryRequestDto historyRequestDto) throws Exception;

  public boolean removeHistory(int id) throws Exception;
  
}
