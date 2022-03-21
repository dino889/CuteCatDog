package com.cutecatdog.model.comment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentAddRequestDto {
  Integer boardId;
  Integer userId;
  Integer parent;
  String comment;
  Integer dept;
  Integer seq;
}
