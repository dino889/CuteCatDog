package com.cutecatdog.model.Notification;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "Notification", description = "알림 정보")
public class NotificationDto {

    @ApiModelProperty(value = "id")
    private int id;

    @ApiModelProperty(value = "사용자 id", required = true)
    private int userId;

    @ApiModelProperty(value = "알림 유형(1: 공지 2: 이벤트 3: 개인 일정)", required = true)
    private int type;

    @ApiModelProperty(value = "시간", required = true)
    private String datetime;

    @ApiModelProperty(value = "알림 제목", required = true)
    private String title;

    @ApiModelProperty(value = "알림 내용", required = true)
    private String content;
    
}
