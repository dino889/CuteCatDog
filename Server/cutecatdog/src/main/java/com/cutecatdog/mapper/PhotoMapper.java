package com.cutecatdog.mapper;

import java.sql.SQLException;
import java.util.List;

import com.cutecatdog.model.PhotoDto;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PhotoMapper {
    
    public boolean insertPhoto(String photo) throws SQLException;

    public boolean deletePhoto(int id) throws SQLException;

    public List<PhotoDto> selectHashtag(int diary_id) throws SQLException;

}
