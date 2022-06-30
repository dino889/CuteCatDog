package com.ssafy.ccd.src.network.api

import com.ssafy.ccd.src.dto.Calendar
import com.ssafy.ccd.src.dto.Message
import retrofit2.Response
import retrofit2.http.*

interface CalendarApi {

    @POST("/calendar")
    suspend fun insertCalendar(@Body scheduleDto:Calendar): Response<Message>

    @PUT("/calendar")
    suspend fun updateCalendar(@Body scheduleDto: Calendar) : Response<Message>

    @DELETE("/calendar/{id}")
    suspend fun deleteCalendar(@Path("id") id:Int):Response<Message>

    @GET("/calendar/pet/{pet_id}")
    suspend fun calendarListbyPet(@Path("pet_id")petId:Int):Response<Message>

    @GET("/calendar/user/{user_id}")
    suspend fun calendarListbyUser(@Path("user_id")userId:Int):Response<Message>

    @GET("/calendar/{user_id}/{datetime}")
    suspend fun calendarListbyDate(@Path("user_id")user_id:Int,@Path("datetime")datetime:String) : Response<Message>

    @GET("/calendar/{user_id}")
    suspend fun calendarListbyWeek(@Path("user_id")user_id:Int):Response<Message>

    @GET("/walk")
    suspend fun recommandWorkSpace(@Query("lat")lat:Double,@Query("lng")lng:Double,@Query("range")range:Double) : Response<Message>

    @GET("/calendar/detail/{id}")
    suspend fun getCalendarDetail(@Path("id")id:Int):Response<Message>
}