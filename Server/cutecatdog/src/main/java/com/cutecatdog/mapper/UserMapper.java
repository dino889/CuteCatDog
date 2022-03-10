package com.cutecatdog.mapper;

import java.sql.SQLException;

import com.cutecatdog.model.UserDto;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    int signup(UserDto userDto) throws SQLException;

    UserDto userInfo(int userId) throws SQLException;

    int modifyUser(UserDto userDto) throws SQLException;

    int withdrawal(int userId) throws SQLException;

    UserDto checkEmail(String email) throws SQLException;

    UserDto checkNickname(String nickname) throws SQLException;

    UserDto login(UserDto userDto) throws SQLException;

    int resetPw(UserDto userDto) throws SQLException;

}
