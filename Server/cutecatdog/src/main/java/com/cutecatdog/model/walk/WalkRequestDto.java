package com.cutecatdog.model.walk;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WalkRequestDto {
  Double lat;
  Double lng;
  Double range;
}
