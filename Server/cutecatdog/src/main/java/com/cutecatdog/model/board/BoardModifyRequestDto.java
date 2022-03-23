package com.cutecatdog.model.board;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardModifyRequestDto {
  Integer id;
  String title;
  String content;
  Integer typeId;
  String photoPath;
}
