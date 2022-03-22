package com.cutecatdog.service;

import com.cutecatdog.model.like.LikeRequestDto;

public interface LikeService {

  public boolean addLike(LikeRequestDto likeRequestDto) throws Exception;
  
}
