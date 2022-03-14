package com.cutecatdog.service;

import com.cutecatdog.common.Random.RandomCode;
import com.cutecatdog.common.mail.SendMailHelper;
import com.cutecatdog.mapper.UserMapper;
import com.cutecatdog.model.UserDto;
import com.cutecatdog.model.mail.SendCodeByMailResultDto;
import com.cutecatdog.model.user.AccountDto;

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
        return sqlSession.getMapper(UserMapper.class).modifyUser(userDto) == 1;
    }

    @Override
    public boolean removeUser(int userId) throws Exception {
        return sqlSession.getMapper(UserMapper.class).deleteUser(userId) == 1;
    }

    @Override
    public boolean checkEmail(String email) throws Exception {
        return sqlSession.getMapper(UserMapper.class).checkEmail(email) != null;
    }

    // @Override
    // public boolean checkNickname(String nickname) throws Exception {
    // return sqlSession.getMapper(UserMapper.class).checkNickname(nickname) !=
    // null;
    // }

    @Override
    public UserDto loginUser(String email, String password) throws Exception {
        if (email == null || password == null) {
            return null;
        }
        return sqlSession.getMapper(UserMapper.class).loginUser(email, password);
    }

    // @Override
    // public boolean logoutUser(int userId) throws Exception {
    // // TODO Auto-generated method stub
    // return false;
    // }

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
    public String veryfyEmail(String email) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean resetPassword(AccountDto account) throws Exception {
        System.out.println(account.getEmail() + ", " + account.getPassword());
        return sqlSession.getMapper(UserMapper.class).resetPassword(account) == 1;
    }
}
