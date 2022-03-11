package com.cutecatdog.common.message;

import lombok.Data;
@Data
public class Message {

  private boolean success;
  private String message;
  private Object data;

  public Message() {
    this.success = false;
    this.data = null;
    this.message = null;
  }
}