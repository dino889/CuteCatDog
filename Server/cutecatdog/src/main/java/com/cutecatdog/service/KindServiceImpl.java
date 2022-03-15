package com.cutecatdog.service;

import java.util.List;

import com.cutecatdog.mapper.KindMapper;
import com.cutecatdog.model.kind.KindDto;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KindServiceImpl implements KindService{

  @Autowired
  private SqlSession sqlSession;
  
  @Override
  public List<KindDto> findKind() throws Exception {
    return sqlSession.getMapper(KindMapper.class).selectKind();
  }

  @Override
  public boolean addKind(String name) throws Exception {
    if(sqlSession.getMapper(KindMapper.class).selectKindName(name) != null){
      return false;
    }else{
      return sqlSession.getMapper(KindMapper.class).insertKind(name);
    }
  }

  @Override
  public boolean modifyKind(KindDto kindDto) throws Exception {
    return sqlSession.getMapper(KindMapper.class).updateKind(kindDto);
  }

  @Override
  public boolean removeKind(int id) throws Exception {
    return sqlSession.getMapper(KindMapper.class).deleteKind(id) == 1;
  }
  
}
