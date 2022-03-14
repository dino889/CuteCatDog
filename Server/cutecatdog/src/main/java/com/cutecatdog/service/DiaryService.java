package com.cutecatdog.service;

import java.util.List;

import com.cutecatdog.model.DiaryDto;

public interface DiaryService {

    public boolean addDiary(DiaryDto diaryDto) throws Exception;

    public boolean modifyDiary(DiaryDto diaryDto) throws Exception;

    public List<DiaryDto> findDiaryAsc(int user_id) throws Exception;

    public List<DiaryDto> findDiaryDesc(int user_id) throws Exception;

    public List<DiaryDto> findDiaryByDate(int user_id, String date) throws Exception;

    public List<DiaryDto> findDiaryByPeriod(int user_id, String start_date, String end_date) throws Exception;

    public boolean removeDiary(int id) throws Exception;
    
}
