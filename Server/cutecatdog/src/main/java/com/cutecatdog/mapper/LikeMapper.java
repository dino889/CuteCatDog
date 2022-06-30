package com.cutecatdog.mapper;

import java.sql.SQLException;
import java.util.List;

import com.cutecatdog.model.like.LikeDeleteDto;
import com.cutecatdog.model.like.LikeRequestDto;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LikeMapper {

  public boolean insertLike(LikeRequestDto likeRequestDto) throws SQLException;

  public boolean deleteLike(LikeDeleteDto dto) throws SQLException;

  public Integer selectLike(Integer userId) throws SQLException;

  public Integer selectBoardLike(LikeRequestDto likeRequestDto) throws SQLException;
  
  public List<Integer> selectUserBoard(int userId) throws SQLException;

}
