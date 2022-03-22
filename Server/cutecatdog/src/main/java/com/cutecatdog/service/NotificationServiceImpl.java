package com.cutecatdog.service;

import java.util.List;

import com.cutecatdog.mapper.NotificationMapper;
import com.cutecatdog.model.Notification.NotificationDto;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    SqlSession sqlSession;

    @Override
    public boolean addNotification(NotificationDto notificationDto) throws Exception {
        return sqlSession.getMapper(NotificationMapper.class).insertNotification(notificationDto);
    }

    @Override
    public List<NotificationDto> findNotificationList(int userId) throws Exception {
        return sqlSession.getMapper(NotificationMapper.class).selectNotificationList(userId);
    }

    @Override
    public List<NotificationDto> findNotificationN(int userId) throws Exception {
        return sqlSession.getMapper(NotificationMapper.class).selectNotificationN(userId);
    }

    @Override
    public List<NotificationDto> findNotificationE(int userId) throws Exception {
        return sqlSession.getMapper(NotificationMapper.class).selectNotificationE(userId);
    }

    @Override
    public List<NotificationDto> findNotificationP(int userId) throws Exception {
        return sqlSession.getMapper(NotificationMapper.class).selectNotificationP(userId);
    }

    @Override
    public boolean removeNotification(int id) throws Exception {
        return sqlSession.getMapper(NotificationMapper.class).deleteNotification(id);
    }
    
}
