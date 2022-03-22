package com.cutecatdog.model.comment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class commentDto {
  String nickname;
  String comment;
  Integer parent;
  Integer dept;
  Integer seq;
  Integer id;
  Integer boardId;
  Integer userId;
}
