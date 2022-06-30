package com.cutecatdog.mapper;

import java.sql.SQLException;
import java.util.List;

import com.cutecatdog.model.diary.PhotoDto;
import com.cutecatdog.model.diary.PhotoParamDto;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PhotoMapper {
    
    public boolean insertPhoto(PhotoParamDto paramDto) throws SQLException;

    public boolean deletePhoto(int id) throws SQLException;

    public List<PhotoDto> selectPhoto(int diary_id) throws SQLException;

}
