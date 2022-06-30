package com.cutecatdog.service;

import java.util.List;

import com.cutecatdog.model.diary.PhotoDto;
import com.cutecatdog.model.diary.PhotoParamDto;

public interface PhotoService {
    
    public boolean addPhoto(PhotoParamDto paramDto) throws Exception;

    public boolean removePhoto(int id) throws Exception;

    public List<PhotoDto> findPhoto(int diary_id) throws Exception;
    
}
