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
    private String profile_image;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String gePprofile_image() {
        return this.profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

}
