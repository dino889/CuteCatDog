package com.cutecatdog.service;

import java.util.List;

import com.cutecatdog.model.diary.HashtagDto;

public interface HashtagService {
    
    public boolean addHashtag(String hashtag) throws Exception;
    
    public boolean addHashtagtoDiary(int diary_id, String hashtag) throws Exception;

    public boolean removeHashtag(int id) throws Exception;

    public List<HashtagDto> findHashtag(int diary_id) throws Exception;

    public List<HashtagDto> findHashtagList() throws Exception;

}
