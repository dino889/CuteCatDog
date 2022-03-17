package com.ssafy.ccd.src.network.service

import android.os.Message
import com.ssafy.ccd.util.RetrofitUtil
import retrofit2.Response

class DiaryService {

    suspend fun diaryListbyDescService(userId:Int) : Response<Message> {
        return RetrofitUtil.diaryService.diaryListbyDesc(userId)
    }

    suspend fun diaryListbyAscService(userId:Int) : Response<Message> {
        return RetrofitUtil.diaryService.diaryListbyAsc(userId)
    }

    suspend fun diaryListbyDateService(endDate:String, startDate:String, userId: Int) : Response<Message> {
        return RetrofitUtil.diaryService.diaryListbyDate(endDate, startDate, userId)
    }
}