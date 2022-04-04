package com.ssafy.ccd.src.network.api

import com.ssafy.ccd.src.dto.Message
import com.ssafy.ccd.src.dto.Pet
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface PetApi {
    //반려동물 등록
    @POST("/pets")
    suspend fun petsCreate(@Body petDto: Pet): Response<Message>

    //반려동물 전체목록
    @GET("/pets")
    suspend fun petsAllList() : Response<Message>

    //반려동물 상세정보
    @GET("/pets/{id}")
    suspend fun petsDetailList(@Path("id") id:Int) : Response<Message>

    //내 반려동물 전체조회
    @GET("/pets/my/{userId}")
    suspend fun myPetsAllList(@Path("userId")userId:Int) : Response<Message>

    //반려동물 정보수정
    @PUT("/pets")
    suspend fun petsUpdate(@Body pet:Pet) : Response<Message>

    //반려동물 삭제
    @DELETE("/pets/{id}")
    suspend fun petsDelete(@Path("id")id:Int):Response<Message>

    @GET("/kinds")
    suspend fun kindsAllList() : Response<Message>

    @GET("/kinds/{kind_id}")
    suspend fun kindsbyId(@Path("kind_id")kindId:Int) : Response<Message>

    @Multipart
    @POST("/ai")
    suspend fun getAipetType(@Part file: MultipartBody.Part):Response<Message>
}