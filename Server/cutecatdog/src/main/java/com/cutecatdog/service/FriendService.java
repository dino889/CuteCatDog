package com.cutecatdog.service;

import java.util.List;

import com.cutecatdog.model.friend.FriendDto;
import com.cutecatdog.model.friend.FriendResponseDto;

public interface FriendService {

  public boolean addFriend(FriendDto friendDto) throws Exception;

  public boolean removeFriend(FriendDto dto) throws Exception;

  public List<FriendResponseDto> findFriend(int userId) throws Exception;

  public List<FriendResponseDto> findByEmailFriend(String email) throws Exception;

  
}
