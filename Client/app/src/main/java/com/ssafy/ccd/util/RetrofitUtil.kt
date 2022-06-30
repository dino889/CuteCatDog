package com.ssafy.ccd.util

import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.src.network.api.*

class RetrofitUtil {
    companion object{
        val userService = ApplicationClass.retrofit.create(UserApi::class.java)
        val petService = ApplicationClass.retrofit.create(PetApi::class.java)
        val diaryService = ApplicationClass.retrofit.create(DiaryApi::class.java)
        val boardService = ApplicationClass.retrofit.create(BoardApi::class.java)
        val calendarService = ApplicationClass.retrofit.create(CalendarApi::class.java)
        val notificationService = ApplicationClass.retrofit.create(NotificationApi::class.java)
        val historyService = ApplicationClass.retrofit.create(HistoryApi::class.java)
    }
}