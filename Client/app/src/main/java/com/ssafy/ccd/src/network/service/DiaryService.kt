package com.ssafy.ccd.src.network.service

import com.ssafy.ccd.src.dto.Diary
import com.ssafy.ccd.src.dto.Hashtag
import com.ssafy.ccd.src.dto.Message
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

    suspend fun diaryDetailService(id: Int): Response<Message> {
        return RetrofitUtil.diaryService.diaryListDetail(id)
    }

    suspend fun insertDiaryService(diary: Diary): Response<Message> {
        return RetrofitUtil.diaryService.diaryInsert(diary)
    }

    suspend fun updateDiaryService(diary:Diary) : Response<Message> {
        return RetrofitUtil.diaryService.diaryUpdate(diary)
    }

    suspend fun deleteDiaryService(id:Int):Response<Message> {
        return RetrofitUtil.diaryService.diaryDelete(id)
    }

    suspend fun getHashTagService():Response<Message>{
        return RetrofitUtil.diaryService.hashTagList()
    }
}
