package com.cutecatdog.model.diary;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "Diary", description = "일기 정보")
@Data
public class DiaryDto {

    @ApiModelProperty(value = "ID")
    private int id;
    @ApiModelProperty(value = "작성자 ID")
    private int userId;
    @ApiModelProperty(value = "작성 시간")
    private String datetime;
    @ApiModelProperty(value = "제목")
    private String title;
    @ApiModelProperty(value = "내용")
    private String content;
    @ApiModelProperty(value = "해시태그")
    private List<HashtagDto> hashtag;
    @ApiModelProperty(value = "사진")
    private List<PhotoDto> photo;
    
}
