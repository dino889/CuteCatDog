package com.cutecatdog.model.comment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequestDto {
  Integer boardId;
  String comment;
  Integer userId;
}
