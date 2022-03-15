package com.cutecatdog.model.diary;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "Diary Hashtag", description = "일기 해시태그 정보")
@Data
public class HashtagDto {

    @ApiModelProperty(value = "ID")
    private int id;
    
    @ApiModelProperty(value = "해시태그")
    private String hashtag;

}
