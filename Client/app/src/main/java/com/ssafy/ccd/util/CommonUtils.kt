package com.ssafy.ccd.util

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ssafy.ccd.src.dto.User
import java.lang.reflect.Type
import java.sql.Timestamp
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.time.Duration.Companion.milliseconds

object CommonUtils {
    //MillitoString Birth
    fun makeBirthString(birth:String):String {
        var result = ""
        var formatter = SimpleDateFormat("yyyy년 MM월 dd일")
        var date = Date(birth.toLong() * 1000)
        result = formatter.format(date)
        return result
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun makeBirthMilliSecond(birth:String):String{
        var dt = SimpleDateFormat("yyyy년 MM월 dd일").parse(birth)
        var result = dt.time / 1000
        return result.toString()
    }

    fun getFormattedDescription(descript : String): String {
        var tmp = ""
        if(descript.length > 38){
            tmp = descript.substring(0,33)
        }
        tmp += "..."
        return tmp
    }

    fun getFormattedDueDate(startDate:String, endDate:String):String{
        var result = "${startDate} ~ ${endDate}"
        return result
    }
    fun convertEnglishMonth(month:Int):String{
        if(month == 1){
            return "January"
        }
        if(month == 2){
            return "February"
        }
        if(month == 3){
            return "March"
        }
        if(month == 4){
            return "April"
        }
        if(month == 5){
            return "May"
        }
        if(month == 6){
            return "June"
        }
        if(month == 7){
            return "July"
        }
        if(month == 8){
            return "August"
        }
        if(month == 9){
            return "September"
        }
        if(month == 10){
            return "October"
        }
        if(month == 11){
            return "November"
        }
        if(month == 12){
            return "December"
        }
        return ""
    }


    inline fun <reified T> parseDto(data: Any, typeToken: Type): T {
        val jsonResult: String = Gson().toJson(data)
        return Gson().fromJson<T>(jsonResult, typeToken)
    }
    
    fun convertWeek(week:Int):String{
        if(week == 0){
            return "일요일"
        }else if(week == 1){
            return "월요일"
        }else if(week == 2){
            return "화요일"
        }else if(week == 3){
            return "수요일"
        }else if(week == 4){
            return "목요일"
        }else if(week == 5){
            return "금요일"
        }else if(week == 6){
            return "토요일"
        }
        return ""
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun unixTimeToDateFormat(unixTime: Long): String {
        val simpleDateFormat = SimpleDateFormat("yyyy년 MM월 dd일") //"YYYY-MM-dd HH:mm:ss.SSS"
        val date = Date()
        date.time = unixTime

        return simpleDateFormat.format(date)
    }

    fun converPhotoSize(size:Int):String{
        return "${size}/10"
    }
}
