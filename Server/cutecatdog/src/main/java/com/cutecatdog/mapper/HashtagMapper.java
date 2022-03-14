package com.cutecatdog.mapper;

import java.sql.SQLException;
import java.util.List;

import com.cutecatdog.model.HashtagDto;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HashtagMapper {

    public boolean insertHashtag(String hashtag) throws SQLException;

    public boolean deleteHashtag(int id) throws SQLException;

    public List<HashtagDto> selectHashtag(int diary_id) throws SQLException;
    
}
