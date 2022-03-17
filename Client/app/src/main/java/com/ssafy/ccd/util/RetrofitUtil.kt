package com.ssafy.ccd.util

import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.src.network.api.DiaryApi
import com.ssafy.ccd.src.network.api.PetApi
import com.ssafy.ccd.src.network.api.UserApi

class RetrofitUtil {
    companion object{
        val userService = ApplicationClass.retrofit.create(UserApi::class.java)
        val petService = ApplicationClass.retrofit.create(PetApi::class.java)
        val diaryService = ApplicationClass.retrofit.create(DiaryApi::class.java)
    }
}