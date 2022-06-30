package com.ssafy.ccd.src.network.api

import com.ssafy.ccd.src.dto.Message
import com.ssafy.ccd.src.dto.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface UserApi {

    // 회원 정보 조회 by userId
    @GET("/users")
    suspend fun readUserInfo(@Query("id") id: Int) : Response<Message>

    // 회원가입(이메일, 비밀번호, 닉네임, 프로필 사진)
    @POST("/users")
    suspend fun createUser(@Body userDto: User) : Response<Message>

    // 회원 정보 수정
    @PUT("/users")
    suspend fun updateUser(@Body userDto: User) : Response<Message>

    // 회원 탈퇴
    @DELETE("/users/{id}")
    suspend fun deleteUser(@Path("id") id: Int) : Response<Message>

    // 이메일 중복 검사
    @GET("/users/exists")
    suspend fun existsUserEmail(@Query("email") email: String) : Response<Message>

    // 모든 회원 id, profileImg, nickname 조회
    @GET("/users/id")
    suspend fun selectAllUserList() : Response<Message>

    // 로그인
    @POST("/users/login")
    suspend fun loginUser(@Body account: User) : Response<Message>

    // 비밀번호 초기화
    @PUT("/users/reset-password")
    suspend fun resetUserPw(@Body account: User, @Query("email") email: String) : Response<Message>

    // 이메일 인증
    @GET("/users/send-code")
    suspend fun verifyUserEmail(@Query("email") email: String) : Response<Message>

}