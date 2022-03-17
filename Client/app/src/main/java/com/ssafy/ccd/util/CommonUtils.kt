package com.ssafy.ccd.util

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
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
    //천단위 콤마
    fun makeComma(num: Int): String {
        var comma = DecimalFormat("#,###")
        return "${comma.format(num)} 원"
    }

    fun getFormattedTitle(title:String) : String{
        val a = title.split("[")
//        var tmp = ""
//        for(i in 0..title.length){
//            if(title.get(i) == '['){
//                tmp = title.substring(0,i-1)
//                break
//            }
//        }
        return a[0]
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

}
