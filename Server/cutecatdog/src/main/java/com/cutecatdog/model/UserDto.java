package com.cutecatdog.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "User", description = "회원 정보")
@Data
public class UserDto {

    @ApiModelProperty(value = "ID")
    private int id;
    @ApiModelProperty(value = "이메일", required = true)
    private String email;
    @ApiModelProperty(value = "비밀번호", required = true)
    private String password;
    @ApiModelProperty(value = "닉네임", required = true)
    private String nickname;
    @ApiModelProperty(value = "프로필 사진", required = true)
    private String profileImage;
    @ApiModelProperty(value = "SNS 토큰", required = true)
    private String SNSToken;
    @ApiModelProperty(value = "디바이스 토큰", required = true)
    private String deviceToken;
    @ApiModelProperty(value = "소셜 로그인 타입", required = true)
    private String socialType;
}
