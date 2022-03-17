package com.ssafy.ccd.src.network.api

import android.os.Message
import retrofit2.Response
import retrofit2.http.*

interface DiaryApi {

    @GET("/diary")
    suspend fun diaryListbyDesc(@Query("user_id")userId:Int): Response<Message>

    @GET("/diary/asc")
    suspend fun diaryListbyAsc(@Query("user_id")userId:Int) : Response<Message>

    @GET("/diary/date")
    suspend fun diaryListbyDate(@Query("end_date")endDate:String,@Query("start_date")startDate:String,@Query("userId")userId:Int):Response<Message>

    @GET("/diary/{id}")
    suspend fun diaryListDetail(@Path("id")id:Int):Response<Message>

    @DELETE("/diary/{id}")
    suspend fun diaryDelete(@Path("id")id:Int):Response<Message>
}