package com.ssafy.ccd.src.main.calender

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.ccd.R
import java.util.*

class CalenderDayAdapter(val tmpMonth:Int, val dayList:MutableList<Date>):RecyclerView.Adapter<CalenderDayAdapter.DayViewHolder>() {
    val ROW = 6

    inner class DayViewHolder(val layout: View):RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_calender_day,parent,false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        var day = holder.layout.findViewById<TextView>(R.id.fragment_calender_dayTv)
//        holder.layout.findViewById<TextView>(R.id.fragment_calender_dayTv).setOnClickListener {
//
//        }
        var clicked = false

        holder.layout.findViewById<ConstraintLayout>(R.id.fragment_calender_day_item).setOnClickListener {
//            if(!clicked){
//                day.setBackgroundResource(R.drawable.calender_day_pressed)
//                day.setTextColor(Color.WHITE)
//                clicked = true
//            }else{
//                day.background = ColorDrawable(Color.TRANSPARENT)
//                day.setTextColor(when(position%7){
//                    0 -> Color.RED
//                    6-> Color.BLUE
//                    else -> Color.BLACK
//                })
//                clicked = false
//            }
            itemClickListener.onClick(it,position,day.text.toString().toInt())
        }
        day.text = dayList[position].date.toString()
        day.setTextColor(when(position%7){
            0 -> Color.RED
            6-> Color.BLUE
            else -> Color.BLACK
        })

        if(tmpMonth != dayList[position].month){
            day.alpha = 0.4f
        }
    }

    override fun getItemCount(): Int {
        return ROW*7
    }
    interface ItemClickListener{
        fun onClick(view: View, position: Int, day: Int)
    }

    private lateinit var itemClickListener : ItemClickListener

    fun setItemClickListener(itemClickListener: ItemClickListener){
        this.itemClickListener = itemClickListener
    }

}