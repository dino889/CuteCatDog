package com.cutecatdog.service;

import com.cutecatdog.cutecatdog.mapper.UserMapper;
import com.cutecatdog.model.UserDto;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private SqlSession sqlSession;

    @Override
    public boolean signupUser(UserDto userDto) throws Exception {
        if (userDto.getId() == 0 || userDto.getPassword() == null) {
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

    @Override
    public boolean checkNickname(String nickname) throws Exception {
        return sqlSession.getMapper(UserMapper.class).checkNickname(nickname) != null;
    }

    @Override
    public UserDto loginUser(UserDto userDto) throws Exception {
        if (userDto.getId() == 0 || userDto.getPassword() == null) {
            return null;
        }
        return sqlSession.getMapper(UserMapper.class).loginUser(userDto);
    }

    @Override
    public boolean logoutUser(int userId) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean resetPassword(UserDto userDto) throws Exception {
        userDto.setPassword(makePw());
        return sqlSession.getMapper(UserMapper.class).resetPassword(userDto) == 1;
    }

    public String makePw(){
        char[] charSet = new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            int idx = (int)(charSet.length*Math.random());
            sb.append(charSet[idx]);
        }

        return sb.toString();
    }
    
}
