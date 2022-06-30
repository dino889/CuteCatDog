package com.cutecatdog.mapper;

import java.sql.SQLException;
import java.util.List;

import com.cutecatdog.model.UserDto;
import com.cutecatdog.model.user.AccountDto;
import com.cutecatdog.model.user.UserResponseDto;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    int insertUser(UserDto userDto) throws SQLException;

    UserDto selectUser(int userId) throws SQLException;

    int updateUser(UserDto userDto) throws SQLException;

    int deleteUser(int userId) throws SQLException;

    UserDto checkEmail(String email) throws SQLException;

    UserDto checkNickname(String nickname) throws SQLException;

    UserDto loginUser(AccountDto account) throws SQLException;

    int resetPassword(AccountDto account) throws SQLException;

    UserDto selectUserbyToken(String token) throws SQLException;

    int updateTokenByUserId(UserDto userDto) throws SQLException;

    List<UserDto> selectAllUser() throws SQLException;

    
    List<UserResponseDto> selectUserAll()throws SQLException;
}
