package com.cutecatdog.service;

import com.cutecatdog.model.comment.CommentAddShowRequestDto;
import com.cutecatdog.model.comment.CommentModifyRequestDto;
import com.cutecatdog.model.comment.CommentRequestDto;


public interface CommentService {

  boolean addComment(CommentAddShowRequestDto commentAddShowRequestDto) throws Exception;

  boolean modifyComment(CommentModifyRequestDto commentModifyRequestDto) throws Exception;

  boolean removeComment(int id) throws Exception;

  boolean addRealComment(CommentRequestDto commentRequestDto) throws Exception;
  
}
