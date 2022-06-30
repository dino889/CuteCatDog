package com.cutecatdog.service;

import java.util.List;

import com.cutecatdog.model.walk.WalkInsertRequestDto;
import com.cutecatdog.model.walk.WalkRequestDto;
import com.cutecatdog.model.walk.WalkResponseDto;

public interface WalkService {

  public List<WalkResponseDto> findWalk(WalkRequestDto dto) throws Exception;

  public boolean addWalk(WalkInsertRequestDto walkInsertRequestDto) throws Exception;
  
}
