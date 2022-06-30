package com.cutecatdog.model.mail;

import lombok.Data;

@Data
public class SendCodeByMailResultDto {
  private boolean success;
  private String code;

  public SendCodeByMailResultDto() {
    this.success = false;
    this.code = null;
  }
}
