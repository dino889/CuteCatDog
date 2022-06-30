package com.ssafy.ccd.src.network.api

import com.ssafy.ccd.src.dto.Diary
import com.ssafy.ccd.src.dto.Message
import retrofit2.Response
import retrofit2.http.*

interface DiaryApi {

    @GET("/diary")
    suspend fun diaryListbyDesc(@Query("user_id")userId:Int): Response<Message>

    @GET("/diary/asc")
    suspend fun diaryListbyAsc(@Query("user_id")userId:Int) : Response<Message>

    @GET("/diary/{user_id}/{start_date}/{end_date}")
    suspend fun diaryListbyDate(@Path("end_date")endDate:String,@Path("start_date")startDate:String,@Path("user_id")userId:Int):Response<Message>

    @GET("/diary/{id}")
    suspend fun diaryListDetail(@Path("id")id:Int):Response<Message>

    @DELETE("/diary/{id}")
    suspend fun diaryDelete(@Path("id")id:Int):Response<Message>

    @POST("/diary")
    suspend fun diaryInsert(@Body diaryDto: Diary):Response<Message>

    @PUT("/diary")
    suspend fun diaryUpdate(@Body diaryDto:Diary):Response<Message>

    @GET("/hashtags")
    suspend fun hashTagList():Response<Message>
}