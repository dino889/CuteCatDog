package com.cutecatdog.service;

import java.util.List;

import com.cutecatdog.model.PhotoDto;

public interface PhotoService {
    
    public boolean addPhoto(String photo) throws Exception;

    public boolean removePhoto(int id) throws Exception;

    public List<PhotoDto> findPhoto(int diary_id) throws Exception;
    
}
