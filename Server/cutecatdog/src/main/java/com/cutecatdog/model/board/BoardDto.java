package com.cutecatdog.model.board;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardDto {
  Integer id;
  Integer userId;
  String title;
  String author;
  Integer typeId;
  String content;
  Integer count;
  String time;
  String photoPath;
  Integer commentCnt;
}
