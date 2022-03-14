package com.cutecatdog.service;

import com.cutecatdog.model.UserDto;
import com.cutecatdog.model.mail.SendCodeByMailResultDto;
import com.cutecatdog.model.user.AccountDto;

public interface UserService {

    boolean addUser(UserDto userDto) throws Exception;

    UserDto findUser(int userId) throws Exception;

    boolean modifyUser(UserDto userDto) throws Exception;

    boolean removeUser(int userId) throws Exception;

    boolean checkEmail(String userEmail) throws Exception;

    // boolean checkNickname(String nickname) throws Exception;

    UserDto loginUser(String email, String password) throws Exception;

    // boolean logoutUser(int userId) throws Exception;

    SendCodeByMailResultDto sendCodeByMail(String email) throws Exception;

    boolean resetPassword(AccountDto account) throws Exception;

    String veryfyEmail(String email) throws Exception;

}
