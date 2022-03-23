package com.cutecatdog.service;

import java.util.List;

import com.cutecatdog.model.like.LikeDeleteDto;
import com.cutecatdog.model.like.LikeRequestDto;

public interface LikeService {

  public boolean addLike(LikeRequestDto likeRequestDto) throws Exception;

  public boolean removeLike(LikeDeleteDto dto) throws Exception;

  public boolean findBoardLikeByUID(LikeRequestDto likeRequestDto) throws Exception;

  public boolean findLike(LikeRequestDto likeRequestDto) throws Exception;
  
  public List<Integer> findUserBoard(int userId) throws Exception;

}
