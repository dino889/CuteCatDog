package com.ssafy.ccd.src.network.service

import com.ssafy.ccd.src.dto.Calendar
import com.ssafy.ccd.src.dto.Message
import com.ssafy.ccd.util.RetrofitUtil
import retrofit2.Response
import retrofit2.Retrofit

class CalendarService {

    suspend fun insertCalendar(calendar:Calendar) : Response<Message> {
        return RetrofitUtil.calendarService.insertCalendar(calendar)
    }

    suspend fun updateCalendar(calendar: Calendar) : Response<Message> {
        return RetrofitUtil.calendarService.updateCalendar(calendar)
    }

    suspend fun deleteCalendar(id:Int) : Response<Message> {
        return RetrofitUtil.calendarService.deleteCalendar(id)
    }

    suspend fun getCalendarListByPet(petId:Int) : Response<Message> {
        return RetrofitUtil.calendarService.calendarListbyPet(petId)
    }

    suspend fun getCalendarListByUser(userId:Int) : Response<Message> {
        return RetrofitUtil.calendarService.calendarListbyUser(userId)
    }

    suspend fun getCalendarListByDate(userId:Int, datetime:String) : Response<Message> {
        return RetrofitUtil.calendarService.calendarListbyDate(userId,datetime)
    }

    suspend fun getCalendarListByWeek(userId:Int):Response<Message>{
        return RetrofitUtil.calendarService.calendarListbyWeek(userId)
    }
    suspend fun recommandWalkSpacce(lat:Double, lng:Double, range:Double) : Response<Message>{
        return RetrofitUtil.calendarService.recommandWorkSpace(lat,lng,range)
    }

    suspend fun getCalendarDetail(id:Int):Response<Message>{
        return RetrofitUtil.calendarService.getCalendarDetail(id)
    }
}