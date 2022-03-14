package com.ssafy.ccd.src.network.service

import com.ssafy.ccd.src.dto.Message
import com.ssafy.ccd.src.dto.User
import com.ssafy.ccd.util.RetrofitUtil
import retrofit2.Response

class UserService {

    /**
     * 로그인
     * @param email
     * @param password
     */
    suspend fun loginUser(account : User) : Response<Message> {
        return RetrofitUtil.userService.loginUser(account)
    }
}