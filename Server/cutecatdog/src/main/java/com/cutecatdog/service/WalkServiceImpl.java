package com.cutecatdog.service;

import java.util.ArrayList;
import java.util.List;

import com.cutecatdog.mapper.WalkMapper;
import com.cutecatdog.model.walk.WalkIdRequestDto;
import com.cutecatdog.model.walk.WalkInsertRequestDto;
import com.cutecatdog.model.walk.WalkRequestDto;
import com.cutecatdog.model.walk.WalkResponseDto;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WalkServiceImpl implements WalkService{
  
  @Autowired
  private SqlSession sqlSession;
  @Override
  public List<WalkResponseDto> findWalk(WalkRequestDto walkRequestdto) throws Exception {
    return sqlSession.getMapper(WalkMapper.class).selectWalk(walkRequestdto);
  }
  @Override
  public boolean addWalk(WalkInsertRequestDto walkInsertRequestDto) throws Exception {
    return sqlSession.getMapper(WalkMapper.class).insertWalk(walkInsertRequestDto);
  }
}
