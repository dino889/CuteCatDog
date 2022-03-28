package com.ssafy.ccd.src.network.api

import com.ssafy.ccd.src.dto.Message
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface NotificationApi {

    @GET("/notification/{user_id}")
    suspend fun getNotificationList(@Path("user_id")userId:Int) : Response<Message>

    @GET("/notification/event/{user_id}")
    suspend fun getNotificationEventList(@Path("user_id")userId:Int) : Response<Message>

    @GET("/notification/notice/{user_id}")
    suspend fun getNotificationNoticeList(@Path("user_id")userId:Int) : Response<Message>

    @GET("/notification/schedule/{user_id}")
    suspend fun getNotificationScheduleList(@Path("user_id")userId:Int) : Response<Message>

}