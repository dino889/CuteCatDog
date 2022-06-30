package com.cutecatdog.service;

import java.util.List;

import com.cutecatdog.model.Notification.NotificationDto;

public interface NotificationService {

    public boolean addNotification(NotificationDto notificationDto) throws Exception;

    public List<NotificationDto> findNotificationList(int userId) throws Exception;

    public List<NotificationDto> findNotificationN(int userId) throws Exception;

    public List<NotificationDto> findNotificationE(int userId) throws Exception;

    public List<NotificationDto> findNotificationP(int userId) throws Exception;

    public boolean removeNotification(int id) throws Exception;

}
