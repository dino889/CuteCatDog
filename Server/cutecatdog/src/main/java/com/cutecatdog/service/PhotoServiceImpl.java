package com.cutecatdog.service;

import java.util.List;

import com.cutecatdog.mapper.PhotoMapper;
import com.cutecatdog.model.PhotoDto;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PhotoServiceImpl implements PhotoService {

    @Autowired
    private SqlSession sqlSession;

    @Override
    public boolean addPhoto(String photo) throws Exception {
        return sqlSession.getMapper(PhotoMapper.class).insertPhoto(photo);
    }

    @Override
    public boolean removePhoto(int id) throws Exception {
        return sqlSession.getMapper(PhotoMapper.class).deletePhoto(id);
    }

    @Override
    public List<PhotoDto> findPhoto(int diary_id) throws Exception {
        return sqlSession.getMapper(PhotoMapper.class).selectPhoto(diary_id);
    }
    
}
