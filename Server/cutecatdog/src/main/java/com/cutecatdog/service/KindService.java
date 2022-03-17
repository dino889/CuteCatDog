package com.cutecatdog.service;

import java.util.List;

import com.cutecatdog.model.kind.KindAddRequestDto;
import com.cutecatdog.model.kind.KindDetailResponseDto;
import com.cutecatdog.model.kind.KindDto;
import com.cutecatdog.model.kind.KindmodifyRequestDto;

public interface KindService {

  public List<KindDto> findKind() throws Exception;

  public boolean addKind(KindAddRequestDto kindAddRequestDto) throws Exception;

  public boolean modifyKind(KindmodifyRequestDto KindmodifyRequestDto) throws Exception;

  public boolean removeKind(int id) throws Exception;

  public KindDetailResponseDto findKindDetail(int kindId) throws Exception;

  
}
