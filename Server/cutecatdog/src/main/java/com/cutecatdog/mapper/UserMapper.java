package com.cutecatdog.mapper;

import java.sql.SQLException;

import com.cutecatdog.model.UserDto;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    int insertUser(UserDto userDto) throws SQLException;

    UserDto selectUser(int userId) throws SQLException;

    int modifyUser(UserDto userDto) throws SQLException;

    int deleteUser(int userId) throws SQLException;

    UserDto checkEmail(String email) throws SQLException;

    UserDto checkNickname(String nickname) throws SQLException;

    UserDto loginUser(String email, String password) throws SQLException;

    int resetPassword(String email, String password) throws SQLException;

}
