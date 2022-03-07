package com.ssafy.ccd.util

import java.text.DecimalFormat

object CommonUtils {

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

}
