package com.cutecatdog.mapper;

import java.sql.SQLException;
import java.util.List;

import com.cutecatdog.model.kind.KindAddRequestDto;
import com.cutecatdog.model.kind.KindDetailResponseDto;
import com.cutecatdog.model.kind.KindDto;
import com.cutecatdog.model.kind.KindmodifyRequestDto;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface KindMapper {

  public List<KindDto> selectKind() throws SQLException;

  public boolean insertKind(KindAddRequestDto kindAddRequestDto) throws SQLException;

  public Integer selectKindName(String name) throws SQLException;

  public boolean updateKind(KindmodifyRequestDto KindmodifyRequestDto) throws SQLException;

  public int deleteKind(int id) throws SQLException;

  public KindDetailResponseDto selectKindId(int kindId) throws SQLException;
  
}
