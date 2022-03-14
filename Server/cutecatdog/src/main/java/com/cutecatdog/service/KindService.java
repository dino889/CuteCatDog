package com.cutecatdog.service;

import java.util.List;

import com.cutecatdog.model.KindDto;

public interface KindService {

  public List<KindDto> findKind() throws Exception;

  public boolean addKind(String name) throws Exception;

  public boolean modifyKind(KindDto kindDto) throws Exception;

  public boolean removeKind(int id) throws Exception;

  
}
