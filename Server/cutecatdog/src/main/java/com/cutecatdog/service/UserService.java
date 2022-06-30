package com.cutecatdog.service;

import java.util.List;

import com.cutecatdog.model.UserDto;
import com.cutecatdog.model.mail.SendCodeByMailResultDto;
import com.cutecatdog.model.user.AccountDto;
import com.cutecatdog.model.user.UserResponseDto;

public interface UserService {

    boolean addUser(UserDto userDto) throws Exception;

    UserDto findUser(int userId) throws Exception;

    boolean modifyUser(UserDto userDto) throws Exception;

    boolean removeUser(int userId) throws Exception;

    UserDto checkEmail(String userEmail) throws Exception;

    UserDto loginUser(AccountDto account) throws Exception;

    SendCodeByMailResultDto sendCodeByMail(String email) throws Exception;

    boolean resetPassword(AccountDto account) throws Exception;

    UserDto findUserByToken(String targetToken) throws Exception;

    boolean modifyTokenByUserId(UserDto user) throws Exception;

    List<UserDto> findAllUser() throws Exception;
    List<UserResponseDto> findUserId() throws Exception;

}
