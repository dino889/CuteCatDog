package com.cutecatdog.service;

import java.util.List;

import com.cutecatdog.mapper.PetMapper;
import com.cutecatdog.model.pet.PetDto;
import com.cutecatdog.model.pet.PetWithKind;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PetServiceImpl implements PetService{
  @Autowired
  private SqlSession sqlSession;

  @Override
  public List<PetDto> findPet() throws Exception {
    return sqlSession.getMapper(PetMapper.class).selectPet();
  }

  @Override
  public boolean addPet(PetDto petDto) throws Exception {
    return sqlSession.getMapper(PetMapper.class).insertPet(petDto);
  }

  @Override
  public PetWithKind findPetDetail(int id) throws Exception {
    return sqlSession.getMapper(PetMapper.class).selectPetDetail(id);
  }

  @Override
  public boolean modifyPet(PetDto petDto) throws Exception {
    return sqlSession.getMapper(PetMapper.class).updatePet(petDto);
  }

  @Override
  public boolean removePet(int id) throws Exception {
    return sqlSession.getMapper(PetMapper.class).deletePet(id) == 1;
  }

  @Override
  public List<PetDto> findMyPetDetail(int userId) throws Exception {
    return sqlSession.getMapper(PetMapper.class).selectMyPetDetail(userId);
  }
  
  
}
