package com.ssafy.ccd.src.network.service

import com.ssafy.ccd.src.dto.Message
import com.ssafy.ccd.util.RetrofitUtil
import retrofit2.Response
import retrofit2.http.Path

class NotificationService {
    suspend fun getNotificationList(userId:Int) : Response<Message> = RetrofitUtil.notificationService.getNotificationList(userId)

    suspend fun getNotificationEventList(userId:Int) : Response<Message> = RetrofitUtil.notificationService.getNotificationEventList(userId)

    suspend fun getNotificationNoticeList(userId:Int) : Response<Message> = RetrofitUtil.notificationService.getNotificationNoticeList(userId)

    suspend fun getNotificationScheduleList(userId:Int) : Response<Message> = RetrofitUtil.notificationService.getNotificationScheduleList(userId)
}