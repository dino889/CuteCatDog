package com.cutecatdog.model.board;

import java.util.List;

import com.cutecatdog.model.comment.commentDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardDetailDto {
  Integer userId;
  String title;
  String content;
  String author;
  Integer typeId;
  Integer id;
  Integer count;
  String photoPath;
  String time;
  List<commentDto> commentDto;
  
}
