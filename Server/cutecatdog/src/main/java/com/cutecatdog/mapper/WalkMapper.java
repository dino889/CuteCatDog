package com.cutecatdog.mapper;

import java.sql.SQLException;
import java.util.List;

import com.cutecatdog.model.walk.WalkRequestDto;
import com.cutecatdog.model.walk.WalkInsertRequestDto;
import com.cutecatdog.model.walk.WalkResponseDto;

public interface WalkMapper {

  public List<WalkResponseDto> selectWalk(WalkRequestDto dto) throws SQLException;

  public boolean insertWalk(WalkInsertRequestDto walkInsertRequestDto) throws SQLException;
}
