package com.cutecatdog.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@ApiModel(value="응답 형식", description="호출 결과를 반환")
@Data
@Getter
@Setter
public class ApiResponse {

    @ApiModelProperty(value="성공 여부")
    private boolean isSuccess;

    @ApiModelProperty(value = "메세지")
    private String message;

    @ApiModelProperty(value = "데이터")
    private Object data;

}
