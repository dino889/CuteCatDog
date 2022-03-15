package com.cutecatdog.model.diary;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "Diary Photo", description = "일기 사진 정보")
@Data
public class PhotoDto {
    
    @ApiModelProperty(value = "ID")
    private int id;
    
    @ApiModelProperty(value = "사진 경로")
    private String photo;

}
