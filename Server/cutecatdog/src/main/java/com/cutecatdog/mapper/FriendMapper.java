package com.cutecatdog.mapper;

import java.sql.SQLException;
import java.util.List;

import com.cutecatdog.model.friend.FriendDto;
import com.cutecatdog.model.friend.FriendResponseDto;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FriendMapper {

  public boolean insertFriend(FriendDto friendDto) throws SQLException;

  public Integer selectOverlabFriend(FriendDto friendDto) throws SQLException;
  
  public Integer selectMyFriend(FriendDto friendDto) throws SQLException;

  public boolean deleteFriend(FriendDto dto) throws SQLException;

  public List<FriendResponseDto> selectFriendList(int userId) throws SQLException;

  public List<FriendResponseDto> selectFriendByEmail(String email) throws SQLException;

}
