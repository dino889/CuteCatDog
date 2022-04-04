package com.cutecatdog.model.Calendar;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "Schedule", description = "캘린더 일정")
public class ScheduleDto {
    
    @ApiModelProperty(value = "ID")
    private int id;
    @ApiModelProperty(value = "사용자 id", required = true)
    private int userId;
    @ApiModelProperty(value = "반려동물 id", required = true)
    private int petId;
    @ApiModelProperty(value = "일정 종류(1: 접종 2: 산책)", required = true)
    private int type;
    @ApiModelProperty(value = "일정 제목", required = true)
    private String title;
    @ApiModelProperty(value = "메모", required = false)
    private String memo;
    @ApiModelProperty(value = "날짜", required = true)
    private String datetime;
    @ApiModelProperty(value = "장소")
    private String place;
    @ApiModelProperty(value = "알림 전송 여부")
    private int isNoti;
    
}
