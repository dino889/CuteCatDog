package com.cutecatdog.service;

import java.util.List;


import com.cutecatdog.model.PetDto;

public interface PetService {
  public List<PetDto> findPet() throws Exception;

  public boolean addPet(PetDto petDto) throws Exception;

  public List<PetDto> findPetDetail(int id) throws Exception;

  public boolean modifyPet(PetDto petDto) throws Exception;
}