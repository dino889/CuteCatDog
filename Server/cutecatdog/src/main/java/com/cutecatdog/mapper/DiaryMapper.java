package com.cutecatdog.mapper;

import java.sql.SQLException;
import java.util.List;

import com.cutecatdog.model.DiaryDto;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DiaryMapper {

    public boolean insertDiary(DiaryDto diaryDto) throws SQLException;

    public boolean updateDiary(DiaryDto diaryDto) throws SQLException;

    public boolean deleteDiary(int id) throws SQLException;

    public List<DiaryDto> selectDiaryAsc(int user_id) throws SQLException;

    public List<DiaryDto> selectDiaryDesc(int user_id) throws SQLException;

    public List<DiaryDto> selectDiaryByDate(int user_id, String date) throws SQLException;

    public List<DiaryDto> selectDiaryByPeriod(int user_id, String start_date, String end_date) throws SQLException;

}
