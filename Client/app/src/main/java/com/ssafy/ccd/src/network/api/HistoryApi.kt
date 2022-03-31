package com.ssafy.ccd.src.network.api

import com.ssafy.ccd.src.dto.History
import com.ssafy.ccd.src.dto.Message
import retrofit2.Response
import retrofit2.http.*

interface HistoryApi {

    // 사용자 분석 history List
    @GET("/history")
    suspend fun selectAllHistory(@Query("userId") userId: Int) : Response<Message>

    // 분석 결과 저장
    @POST("/history")
    suspend fun insertHistory(@Body historyRequestDto : History) : Response<Message>

    // 분석 결과 삭제
    @DELETE("/history/{history_id}")
    suspend fun deleteHistory(@Path("history_id") history_id: Int) : Response<Message>

    // 분석 결과 1개 조회
    @GET("/history/detail")
    suspend fun selectHistory(@Query("id") id : Int) : Response<Message>

}