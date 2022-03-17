package com.cutecatdog.model.board;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardDto {
  Integer id;
  Integer userId;
  String title;
  String content;
  String author;
}
