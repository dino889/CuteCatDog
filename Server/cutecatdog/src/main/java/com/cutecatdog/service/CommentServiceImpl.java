package com.cutecatdog.service;

import java.util.List;

import com.cutecatdog.mapper.CommentMapper;
import com.cutecatdog.model.comment.CommentAddRequestDto;
import com.cutecatdog.model.comment.CommentAddShowRequestDto;
import com.cutecatdog.model.comment.CommentModifyRequestDto;
import com.cutecatdog.model.comment.CommentRequestDto;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService{

  @Autowired
  private SqlSession sqlSession;
  

  @Override
  public boolean addComment(CommentAddShowRequestDto commentAddShowRequestDto)  throws Exception{
    CommentAddRequestDto dto = new CommentAddRequestDto();
    dto.setBoardId(commentAddShowRequestDto.getBoardId());  
    dto.setComment(commentAddShowRequestDto.getComment());
    dto.setParent(commentAddShowRequestDto.getParent());
    dto.setUserId(commentAddShowRequestDto.getUserId());
    return sqlSession.getMapper(CommentMapper.class).insertComment(dto);
  }


  @Override
  public boolean modifyComment(CommentModifyRequestDto commentModifyRequestDto) throws Exception {
    return sqlSession.getMapper(CommentMapper.class).updateComment(commentModifyRequestDto);
  }


  @Override
  public boolean removeComment(int id) throws Exception {
    List<Integer> list = sqlSession.getMapper(CommentMapper.class).selectCommentUnder(id);
    list.add(id);
    while(!list.isEmpty()){
      sqlSession.getMapper(CommentMapper.class).deleteComment(list.get(list.size() - 1));
      list.remove(list.size()-1);
    }
    return true;
  }


  @Override
  public boolean addRealComment(CommentRequestDto commentRequestDto) throws Exception {
    return sqlSession.getMapper(CommentMapper.class).insertRealComment(commentRequestDto);
  }


  @Override
  public int findUser(int id) throws Exception {
    return sqlSession.getMapper(CommentMapper.class).selectUser(id);
  }
  
}
