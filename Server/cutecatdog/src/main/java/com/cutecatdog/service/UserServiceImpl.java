package com.cutecatdog.service;

import java.util.List;

import com.cutecatdog.common.Random.RandomCode;
import com.cutecatdog.common.mail.SendMailHelper;
import com.cutecatdog.mapper.UserMapper;
import com.cutecatdog.model.UserDto;
import com.cutecatdog.model.mail.SendCodeByMailResultDto;
import com.cutecatdog.model.user.AccountDto;
import com.cutecatdog.model.user.UserResponseDto;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private SqlSession sqlSession;

    @Autowired
    private SendMailHelper mailHelper;

    @Override
    public boolean addUser(UserDto userDto) throws Exception {
        if (userDto.getEmail() == null || userDto.getPassword() == null) {
            throw new Exception();
        }
        return sqlSession.getMapper(UserMapper.class).insertUser(userDto) == 1;
    }

    @Override
    public UserDto findUser(int userId) throws Exception {
        return sqlSession.getMapper(UserMapper.class).selectUser(userId);
    }

    @Override
    public boolean modifyUser(UserDto userDto) throws Exception {
        return sqlSession.getMapper(UserMapper.class).updateUser(userDto) == 1;
    }

    @Override
    public boolean removeUser(int userId) throws Exception {
        return sqlSession.getMapper(UserMapper.class).deleteUser(userId) == 1;
    }

    @Override
    public UserDto checkEmail(String email) throws Exception {
        return sqlSession.getMapper(UserMapper.class).checkEmail(email);
    }

    @Override
    public UserDto loginUser(AccountDto account) throws Exception {
        if (account.getEmail() == null || account.getPassword() == null) {
            return null;
        }
        return sqlSession.getMapper(UserMapper.class).loginUser(account);
    }

    @Override
    public SendCodeByMailResultDto sendCodeByMail(String email) throws Exception {
        RandomCode rp = new RandomCode();
        String code = rp.getRandomCode(5);

        SendCodeByMailResultDto result = new SendCodeByMailResultDto();
        boolean isSuccess = mailHelper.SendMail(email, code);

        if (isSuccess) {
            result.setSuccess(true);
            result.setCode(code);
        }

        return result;
    }

    @Override
    public boolean resetPassword(AccountDto account) throws Exception {
        return sqlSession.getMapper(UserMapper.class).resetPassword(account) == 1;
    }

    @Override
    public UserDto findUserByToken(String targetToken) throws Exception {
        return sqlSession.getMapper(UserMapper.class).selectUserbyToken(targetToken);
    }

    @Override
    public boolean modifyTokenByUserId(UserDto user) throws Exception {
        return sqlSession.getMapper(UserMapper.class).updateTokenByUserId(user) == 1;
    }

    @Override
    public List<UserDto> findAllUser() throws Exception {
        return sqlSession.getMapper(UserMapper.class).selectAllUser();
    }
    public List<UserResponseDto> findUserId() throws Exception {
        return sqlSession.getMapper(UserMapper.class).selectUserAll();
    }

    
}
