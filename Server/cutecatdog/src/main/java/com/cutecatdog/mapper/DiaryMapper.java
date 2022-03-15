package com.cutecatdog.mapper;

import java.sql.SQLException;
import java.util.List;

import com.cutecatdog.model.diary.DiaryDto;
import com.cutecatdog.model.diary.DiaryParamDto;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DiaryMapper {

    public boolean insertDiary(DiaryDto diaryDto) throws SQLException;

    public boolean updateDiary(DiaryDto diaryDto) throws SQLException;

    public boolean deleteDiary(int id) throws SQLException;
    
    public boolean deletePhoto(int id) throws SQLException;
    
    public boolean deleteHashtag(int id) throws SQLException;

    public List<DiaryDto> selectDiaryAsc(int user_id) throws SQLException;

    public List<DiaryDto> selectDiaryDesc(int user_id) throws SQLException;

    public List<DiaryDto> selectDiaryByDate(DiaryParamDto diaryParamDto) throws SQLException;

    public DiaryDto selectDiaryDetali(int user_id) throws SQLException;

}
