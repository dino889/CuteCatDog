package com.cutecatdog.service;

import com.cutecatdog.model.UserDto;

public interface UserService {

    boolean signup(UserDto userDto) throws Exception;
    UserDto userInfo(int userId) throws Exception;
    boolean modifyUser(UserDto userDto) throws Exception;
    boolean withdrawal(int userId) throws Exception;
    boolean checkEmail(String userEmail) throws Exception;
    boolean checkNickname(String nickname) throws Exception;
    UserDto login(UserDto userDto) throws Exception;
    boolean logout(int userId) throws Exception;
    boolean resetPw(UserDto userDto) throws Exception;

}
