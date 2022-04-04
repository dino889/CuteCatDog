package com.cutecatdog.model.AI;

import lombok.Data;

@Data
public class AIResponseDto {
  private boolean isSuccess;
  private String kind;

  public AIResponseDto() {
    this.isSuccess = false;
    this.kind = "UNKNOWN";
  }
}
