package com.cutecatdog.model.diary;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "Diary Parameters", description = "일기 조회 조건")
@Data
public class DiaryParamDto {
    
    @ApiModelProperty(value = "사용자 ID")
    private int userId;
    @ApiModelProperty(value = "시작일")
    private String start_date;
    @ApiModelProperty(value = "종료일")
    private String end_date;

}
