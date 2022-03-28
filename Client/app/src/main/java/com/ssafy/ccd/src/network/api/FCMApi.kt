package com.ssafy.ccd.src.network.api

import com.ssafy.ccd.src.dto.Fcm
import com.ssafy.ccd.src.dto.Message
import retrofit2.Response
import retrofit2.http.*

interface FCMApi {

    // Token 정보 서버로 전송
    @POST("/fcm/token")
    suspend fun uploadToken(@Query("token") token: String, @Query("userId") userId: Int): Response<Message>

    @POST("fcm/sendMessageTo")
    suspend fun sendMessageTo(@Body fcmParamDto: Fcm): Response<Message>

    @POST("fcm/broadcast")
    suspend fun broadCast(@Body fcmParamDto: Fcm) : Response<Message>
}