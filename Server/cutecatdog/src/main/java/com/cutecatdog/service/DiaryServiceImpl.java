package com.cutecatdog.service;

import java.util.List;

import com.cutecatdog.mapper.DiaryMapper;
import com.cutecatdog.model.diary.DiaryDto;
import com.cutecatdog.model.diary.DiaryParamDto;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiaryServiceImpl implements DiaryService {

    @Autowired
    private SqlSession sqlSession;

    @Override
    public boolean addDiary(DiaryDto diaryDto) throws Exception {
        return sqlSession.getMapper(DiaryMapper.class).insertDiary(diaryDto);
    }

    @Override
    public boolean modifyDiary(DiaryDto diaryDto) throws Exception {
        return sqlSession.getMapper(DiaryMapper.class).updateDiary(diaryDto);
    }

    @Override
    public List<DiaryDto> findDiaryAsc(int user_id) throws Exception {
        return sqlSession.getMapper(DiaryMapper.class).selectDiaryAsc(user_id);
    }

    @Override
    public List<DiaryDto> findDiaryDesc(int user_id) throws Exception {
        return sqlSession.getMapper(DiaryMapper.class).selectDiaryDesc(user_id);
    }

    @Override
    public List<DiaryDto> findDiaryByDate(DiaryParamDto diaryParamDto) throws Exception {
        return sqlSession.getMapper(DiaryMapper.class).selectDiaryByDate(diaryParamDto);
    }

    @Override
    public boolean removeDiary(int id) throws Exception {
        sqlSession.getMapper(DiaryMapper.class).deletePhoto(id);
        sqlSession.getMapper(DiaryMapper.class).deleteHashtag(id);
        return sqlSession.getMapper(DiaryMapper.class).deleteDiary(id);
    }

    @Override
    public DiaryDto findDiaryDetail(int id) throws Exception {
        return sqlSession.getMapper(DiaryMapper.class).selectDiaryDetali(id);
    }

}
