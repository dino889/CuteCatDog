package com.ssafy.ccd.util

import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.src.api.UserApi

class RetrofitUtil {
    companion object{
        val userService = ApplicationClass.retrofit.create(UserApi::class.java)
    }
}