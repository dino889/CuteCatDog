package com.cutecatdog.service;

import com.cutecatdog.mapper.LikeMapper;
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
    // TODO Auto-generated method stub
    return sqlSession.getMapper(LikeMapper.class).insertLike(likeRequestDto);
  }
  
}
