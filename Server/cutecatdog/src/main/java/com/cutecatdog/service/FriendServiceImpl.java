package com.cutecatdog.service;

import java.util.List;

import com.cutecatdog.mapper.FriendMapper;
import com.cutecatdog.model.friend.FriendDto;
import com.cutecatdog.model.friend.FriendResponseDto;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FriendServiceImpl implements FriendService{

  @Autowired
  private SqlSession sqlSession;

  @Override
  public boolean addFriend(FriendDto friendDto) throws Exception {
    if(sqlSession.getMapper(FriendMapper.class).selectMyFriend(friendDto) != null){
      return false;
    }else{
      if(sqlSession.getMapper(FriendMapper.class).selectOverlabFriend(friendDto) != null){
        return false;
      }else{
        return sqlSession.getMapper(FriendMapper.class).insertFriend(friendDto);
      }
    }
  }

  @Override
  public boolean removeFriend(FriendDto dto) throws Exception {
    return sqlSession.getMapper(FriendMapper.class).deleteFriend(dto);
  }

  @Override
  public List<FriendResponseDto> findFriend(int userId) throws Exception {
    return sqlSession.getMapper(FriendMapper.class).selectFriendList(userId);
  }

  @Override
  public List<FriendResponseDto> findByEmailFriend(String email) throws Exception {
    return sqlSession.getMapper(FriendMapper.class).selectFriendByEmail(email);
  }
  
}
