package com.ssafy.ccd.src.api

import com.ssafy.ccd.src.dto.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface UserApi {

    // 사용자 로그인
    @GET("/user/login")
    fun login(@Query("id") id: String, @Query("password") password: String): Call<User>

    @GET("planShareUser/list/{planId}")
    suspend fun getShareUserList(@Path("planId")planId:Int) : Response<MutableList<User>>
}