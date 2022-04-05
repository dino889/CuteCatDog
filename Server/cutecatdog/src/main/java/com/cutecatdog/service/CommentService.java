package com.cutecatdog.service;

import com.cutecatdog.model.comment.CommentAddShowRequestDto;
import com.cutecatdog.model.comment.CommentModifyRequestDto;
import com.cutecatdog.model.comment.CommentRequestDto;

import org.springframework.stereotype.Service;

@Service
public interface CommentService {

  boolean addComment(CommentAddShowRequestDto commentAddShowRequestDto) throws Exception;

  boolean modifyComment(CommentModifyRequestDto commentModifyRequestDto) throws Exception;

  boolean removeComment(int id) throws Exception;

  boolean addRealComment(CommentRequestDto commentRequestDto) throws Exception;

  int findUser(int id) throws Exception;
  
}
