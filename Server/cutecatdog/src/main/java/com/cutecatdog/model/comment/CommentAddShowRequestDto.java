package com.cutecatdog.model.comment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentAddShowRequestDto {
  Integer boardId;
  String comment;
  Integer parent;
  Integer userId;
}
