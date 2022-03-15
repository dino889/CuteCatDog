package com.cutecatdog.model.history;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HistoryDto {
  Integer id;
  Integer petId;
  String emotion;
  String datetime;
}
