package com.cutecatdog.model.board;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardAddRequestDto {
  Integer userId;
  String title;
  String content;
  String author;
  Integer typeId;
  String time;
  String photoPath;
}
