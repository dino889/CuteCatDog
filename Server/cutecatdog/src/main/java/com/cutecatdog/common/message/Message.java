package com.cutecatdog.common.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="Response", description="호출 결과를 반환")
public class Message {

  @ApiModelProperty(value="통신 성공 여부")
  private boolean success;

  @ApiModelProperty(value = "메세지")
  private String message;

  @ApiModelProperty(value = "데이터")
  private Object data;

  public Message() {
    this.success = false;
    this.data = null;
    this.message = null;
  }
}