package com.cutecatdog.mapper;

import java.sql.SQLException;
import java.util.List;

import com.cutecatdog.model.Notification.NotificationDto;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NotificationMapper {
    
    public boolean insertNotification(NotificationDto notificationDto) throws SQLException;

    // public boolean updateNotification(NotificationDto notificationDto) throws SQLException;

    public boolean deleteNotification(int id) throws SQLException;
    
    List<NotificationDto> selectNotificationList(int userId) throws SQLException;

    List<NotificationDto> selectNotificationN(int userId) throws SQLException;

    List<NotificationDto> selectNotificationE(int userId) throws SQLException;

    List<NotificationDto> selectNotificationP(int userId) throws SQLException;
}
