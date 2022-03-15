package com.cutecatdog.mapper;

import java.sql.SQLException;
import java.util.List;

import com.cutecatdog.model.kind.KindDto;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface KindMapper {

  public List<KindDto> selectKind() throws SQLException;

  public boolean insertKind(String name) throws SQLException;

  public Integer selectKindName(String name) throws SQLException;

  public boolean updateKind(KindDto kindDto) throws SQLException;

  public int deleteKind(int id) throws SQLException;
  
}
