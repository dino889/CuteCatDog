package com.cutecatdog.service;

import java.util.List;

import com.cutecatdog.mapper.LikeMapper;
import com.cutecatdog.model.like.LikeDeleteDto;
import com.cutecatdog.model.like.LikeRequestDto;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl implements LikeService{

  @Autowired
  private SqlSession sqlSession;

  @Override
  public boolean addLike(LikeRequestDto likeRequestDto) throws Exception {
    // if(sqlSession.getMapper(LikeMapper.class).selectLike(likeRequestDto.getUserId()) != null){
    //   return false;
    // }
    return sqlSession.getMapper(LikeMapper.class).insertLike(likeRequestDto);
  }

  @Override
  public boolean removeLike(LikeDeleteDto dto) throws Exception {
    return sqlSession.getMapper(LikeMapper.class).deleteLike(dto);
  }

  @Override
  public boolean findBoardLikeByUID(LikeRequestDto likeRequestDto) throws Exception {
    if(sqlSession.getMapper(LikeMapper.class).selectBoardLike(likeRequestDto) != null){
      return false;
    }else{
      return true;
    }
  }

  @Override
  public boolean findLike(LikeRequestDto likeRequestDto) throws Exception {
    if(sqlSession.getMapper(LikeMapper.class).selectBoardLike(likeRequestDto) != null){
      return false;
    }else{
      return true;
    }
  }

  @Override
  public List<Integer> findUserBoard(int userId) throws Exception {
    return sqlSession.getMapper(LikeMapper.class).selectUserBoard(userId);
  }

  
  
}
