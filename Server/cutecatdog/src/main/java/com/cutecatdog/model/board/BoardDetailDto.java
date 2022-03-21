package com.cutecatdog.model.board;

import java.util.List;

import com.cutecatdog.model.comment.commentDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardDetailDto {
  String title;
  String content;
  String author;
  List<commentDto> commentDto;
}
