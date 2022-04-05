package com.cutecatdog.mapper;

import java.sql.SQLException;
import java.util.List;

import com.cutecatdog.model.comment.CommentAddRequestDto;
import com.cutecatdog.model.comment.CommentModifyRequestDto;
import com.cutecatdog.model.comment.CommentRequestDto;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper {

  boolean insertComment(CommentAddRequestDto commentAddRequestDto) throws SQLException;

  boolean updateComment(CommentModifyRequestDto commentModifyRequestDto) throws SQLException;

  boolean deleteComment(int id) throws SQLException;

  List<Integer> selectCommentUnder(int id) throws SQLException;

  boolean insertRealComment(CommentRequestDto commentRequestDto) throws SQLException;

  int selectUser(int id) throws SQLException;
  
}
