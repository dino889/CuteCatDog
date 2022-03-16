package com.cutecatdog.service;

import java.util.List;

import com.cutecatdog.model.pet.PetDto;

public interface PetService {
  public List<PetDto> findPet() throws Exception;

  public boolean addPet(PetDto petDto) throws Exception;

  public PetDto findPetDetail(int id) throws Exception;

  public boolean modifyPet(PetDto petDto) throws Exception;

  public boolean removePet(int id) throws Exception;

  public List<PetDto> findMyPetDetail(int userId) throws Exception;
}
