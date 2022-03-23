package com.ssafy.ccd.src.main.calender

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.ccd.R
import com.ssafy.ccd.util.CommonUtils
import java.util.*

class CalenderDayAdapter(val tmpMonth:Int, val dayList:MutableList<Date>,val date:String):RecyclerView.Adapter<CalenderDayAdapter.DayViewHolder>() {
    val ROW = 6

    inner class DayViewHolder(val layout: View):RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_calender_day,parent,false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        var day = holder.layout.findViewById<TextView>(R.id.fragment_calender_dayTv)
        var clicked = false


        day.text = dayList[position].date.toString()
        Log.d("adapterCalendar", "onBindViewHolder: ${day.text}?? ${dayList[position].month}")
        day.setTextColor(when(position%7){
            0 -> Color.RED
            6-> Color.BLUE
            else -> Color.BLACK
        })

        if(tmpMonth != dayList[position].month){
            day.alpha = 0.4f
        }

        var month = date.substring(5,8).trim()
        var monthOfday = date.substring(9,date.length-1).trim()
        var strMonth = (dayList[position].month+1).toString()
        var strDay = dayList[position].day.toString()

        if(dayList[position].month.toString().length == 1){
            strMonth = "0${strMonth}"
        }
        if(day.text.toString().length == 1){
            strDay = "0${strDay}"
        }
        var strDate = "${strMonth}월 ${day.text.toString()}일"
        var comDate = "${month}월 ${monthOfday}일"
        Log.d("Adapter", "onBindViewHolder: ${strDate} || ${comDate}")
        if(strDate.equals(comDate)){
            holder.itemView.findViewById<ImageView>(R.id.fragment_calendar_point).visibility = View.VISIBLE
        }
        var week = CommonUtils.convertWeek(position%7)
        holder.layout.findViewById<ConstraintLayout>(R.id.fragment_calender_day_item).setOnClickListener {
            itemClickListener.onClick(it,position,strDate, week)
        }
    }

    override fun getItemCount(): Int {
        return ROW*7
    }
    interface ItemClickListener{
        fun onClick(view: View, position: Int, day: String, week:String)
    }

    private lateinit var itemClickListener : ItemClickListener

    fun setItemClickListener(itemClickListener: ItemClickListener){
        this.itemClickListener = itemClickListener
    }

}