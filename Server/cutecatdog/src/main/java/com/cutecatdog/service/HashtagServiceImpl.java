package com.cutecatdog.service;

import java.util.List;

import com.cutecatdog.mapper.HashtagMapper;
import com.cutecatdog.model.diary.HashtagDto;
import com.cutecatdog.model.diary.HashtagParamDto;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HashtagServiceImpl implements HashtagService {
    
    @Autowired
    private SqlSession sqlSession;

    @Override
    public boolean addHashtag(String hashtag) throws Exception {
        return sqlSession.getMapper(HashtagMapper.class).insertHashtag(hashtag);
    }

    @Override
    public boolean removeHashtag(int id) throws Exception {
        return sqlSession.getMapper(HashtagMapper.class).deleteHashtag(id);
    }

    @Override
    public List<HashtagDto> findHashtag(int diary_id) throws Exception {
        return sqlSession.getMapper(HashtagMapper.class).selectHashtag(diary_id);
    }

    @Override
    public List<String> findHashtagName(int diary_id) throws Exception {
        return sqlSession.getMapper(HashtagMapper.class).selectHashtagName(diary_id);
    }

    @Override
    public boolean addHashtagtoDiary(HashtagParamDto hashtagParamDto) throws Exception {
        return sqlSession.getMapper(HashtagMapper.class).insertHashtagDiary(hashtagParamDto);
    }

    @Override
    public List<HashtagDto> findHashtagList() throws Exception {
        return sqlSession.getMapper(HashtagMapper.class).selectHashtagList();
    }

    @Override
    public Integer findHashtagId(String hashtag) throws Exception {
        return sqlSession.getMapper(HashtagMapper.class).selectHashtagId(hashtag);
    }

    @Override
    public boolean removeHashtagDiary(HashtagParamDto hashtagParamDto) throws Exception {
        return sqlSession.getMapper(HashtagMapper.class).deleteHashtagDiary(hashtagParamDto);
    }
    
}
