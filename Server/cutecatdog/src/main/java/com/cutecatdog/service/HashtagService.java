package com.cutecatdog.service;

import java.util.List;

import com.cutecatdog.model.diary.HashtagDto;
import com.cutecatdog.model.diary.HashtagParamDto;

public interface HashtagService {
    
    public boolean addHashtag(String hashtag) throws Exception;
    
    public boolean addHashtagtoDiary(HashtagParamDto hashtagParamDto) throws Exception;

    public boolean removeHashtag(int id) throws Exception;

    public List<HashtagDto> findHashtag(int diary_id) throws Exception;

    public List<String> findHashtagName(int diary_id) throws Exception;

    public List<HashtagDto> findHashtagList() throws Exception;

    public Integer findHashtagId(String hashtag) throws Exception;

    public boolean removeHashtagDiary(HashtagParamDto hashtagParamDto) throws Exception;

}
