package com.cutecatdog.service;

import java.util.List;

import com.cutecatdog.model.diary.DiaryDto;
import com.cutecatdog.model.diary.DiaryParamDto;

public interface DiaryService {

    public boolean addDiary(DiaryDto diaryDto) throws Exception;

    public boolean modifyDiary(DiaryDto diaryDto) throws Exception;

    public List<DiaryDto> findDiaryAsc(int user_id) throws Exception;

    public List<DiaryDto> findDiaryDesc(int user_id) throws Exception;

    public List<DiaryDto> findDiaryByDate(DiaryParamDto diaryParamDto) throws Exception;

    public DiaryDto findDiaryDetail(int id) throws Exception;

    public boolean removeDiary(int id) throws Exception;
    
}
