package com.cutecatdog.service;

import java.util.List;

import com.cutecatdog.mapper.KindMapper;
import com.cutecatdog.model.kind.KindAddRequestDto;
import com.cutecatdog.model.kind.KindDetailResponseDto;
import com.cutecatdog.model.kind.KindDto;
import com.cutecatdog.model.kind.KindmodifyRequestDto;

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
  public boolean addKind(KindAddRequestDto kindAddRequestDto) throws Exception {
    if(sqlSession.getMapper(KindMapper.class).selectKindName(kindAddRequestDto.getName()) != null){
      return false;
    }else{
      return sqlSession.getMapper(KindMapper.class).insertKind(kindAddRequestDto);
    }
  }

  @Override
  public boolean modifyKind(KindmodifyRequestDto KindmodifyRequestDto) throws Exception {
    return sqlSession.getMapper(KindMapper.class).updateKind(KindmodifyRequestDto);
  }

  @Override
  public boolean removeKind(int id) throws Exception {
    return sqlSession.getMapper(KindMapper.class).deleteKind(id) == 1;
  }

  @Override
  public KindDetailResponseDto findKindDetail(int kindId) throws Exception {
    return sqlSession.getMapper(KindMapper.class).selectKindId(kindId);
  }
  
}
