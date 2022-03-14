package com.cutecatdog.mapper;

import java.sql.SQLException;
import java.util.List;

import com.cutecatdog.model.PetDto;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PetMapper {
  public List<PetDto> selectPet() throws SQLException;

  public boolean insertPet(PetDto petDto) throws SQLException;

  public List<PetDto> selectPetDetail(int id) throws SQLException;

  public boolean updatePet(PetDto petDto) throws SQLException;

  public int deletePet(int id) throws SQLException;

  public List<PetDto> selectMyPetDetail(int userId) throws SQLException;

}
