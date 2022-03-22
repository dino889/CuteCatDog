package com.cutecatdog.model.board;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardResponsDto {
  Integer id;
  Integer userId;
  String title;
  String author;
  Integer typeId;
  String content;
}
