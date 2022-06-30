package com.cutecatdog.model.walk;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WalkInsertRequestDto {
  Double lat;
  Double lng;
  String place;
}
