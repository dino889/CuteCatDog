package com.cutecatdog.mapper;

import java.sql.SQLException;

import com.cutecatdog.model.like.LikeRequestDto;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LikeMapper {

  public boolean insertLike(LikeRequestDto likeRequestDto) throws SQLException;
  
}
