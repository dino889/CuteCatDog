package com.cutecatdog.model.like;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LikeRequestDto {
  Integer likeId;
  Integer boardId;
  Integer userId;
}
