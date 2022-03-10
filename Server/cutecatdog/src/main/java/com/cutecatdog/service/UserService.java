package com.cutecatdog.service;

import com.cutecatdog.model.UserDto;

public interface UserService {

    boolean signupUser(UserDto userDto) throws Exception;
    UserDto findUser(int userId) throws Exception;
    boolean modifyUser(UserDto userDto) throws Exception;
    boolean removeUser(int userId) throws Exception;
    boolean checkEmail(String userEmail) throws Exception;
    boolean checkNickname(String nickname) throws Exception;
    UserDto loginUser(UserDto userDto) throws Exception;
    boolean logoutUser(int userId) throws Exception;
    boolean resetPassword(UserDto userDto) throws Exception;

}
