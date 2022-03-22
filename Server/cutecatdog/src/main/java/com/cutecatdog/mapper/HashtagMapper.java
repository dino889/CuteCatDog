package com.cutecatdog.mapper;

import java.sql.SQLException;
import java.util.List;

import com.cutecatdog.model.diary.HashtagDto;
import com.cutecatdog.model.diary.HashtagParamDto;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HashtagMapper {

    public boolean insertHashtag(String hashtag) throws SQLException;

    public boolean deleteHashtag(int id) throws SQLException;

    public List<HashtagDto> selectHashtag(int diary_id) throws SQLException;

    public List<String> selectHashtagName(int diary_id) throws SQLException;

    public List<HashtagDto> selectHashtagList() throws SQLException;
    
    public boolean insertHashtagDiary(HashtagParamDto hashtagParamDto) throws SQLException;

    public Integer selectHashtagId(String hashtag) throws SQLException;

    public boolean deleteHashtagDiary(HashtagParamDto hashtagParamDto) throws SQLException;

}
