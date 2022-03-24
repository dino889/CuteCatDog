package com.ssafy.ccd.src.main.calender

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.view.menu.MenuView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.ccd.R
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.src.network.viewmodel.MainViewModels
import com.ssafy.ccd.util.CommonUtils
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.collections.ArrayList

class CalenderDayAdapter(val tmpMonth:Int, val dayList:MutableList<Date>,val date:ArrayList<String>,var viewModel:MainViewModels, var owner:LifecycleOwner,var context:Context):RecyclerView.Adapter<CalenderDayAdapter.DayViewHolder>() {
    val ROW = 6

    inner class DayViewHolder(val layout: View):RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_calender_day,parent,false)
        return DayViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        var day = holder.layout.findViewById<TextView>(R.id.fragment_calender_dayTv)
        var clicked = false
        day.text = dayList[position].date.toString()
        day.setTextColor(when(position%7){
            0 -> Color.RED
            6-> Color.BLUE
            else -> Color.BLACK
        })

        if(tmpMonth != dayList[position].month){
            day.alpha = 0.4f
        }

        for(i in 0..date.size-1){
            var month = date[i].substring(5,8).trim()
            var monthOfday = date[i].substring(9,date[i].length-1).trim()

            var strMonth = (dayList[position].month+1).toString()
            var strDay = dayList[position].day.toString()

            if(dayList[position].month.toString().length == 1){
                strMonth = "0${strMonth}"
            }
            if(day.text.toString().length == 1){
                strDay = "0${strDay}"
            }
            var strDate = "${strMonth}월 ${day.text}일"
            var comDate = "${month}월 ${monthOfday}일"
            var week = CommonUtils.convertWeek(position%7)
            if(strDate.equals(comDate)){
                holder.layout.findViewById<ImageView>(R.id.fragment_calendar_point).visibility = View.VISIBLE
                holder.layout.setOnClickListener {
                    runBlocking {
                        viewModel.getCalendarListbyDate(ApplicationClass.sharedPreferencesUtil.getUser().id,CommonUtils.makeBirthMilliSecond(date[i]))
                    }
                    showDetailDialog(comDate,week)
                }
            }
        }


    }
    @SuppressLint("ClickableViewAccessibility")
    fun showDetailDialog(day:String, week:String){
        val dialogView = LayoutInflater.from(context).inflate(R.layout.fragment_calender_day_dialog,null)
        val dialog = Dialog(context)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(dialogView)
        val param = WindowManager.LayoutParams()
        param.width = WindowManager.LayoutParams.MATCH_PARENT
        param.height = WindowManager.LayoutParams.MATCH_PARENT
        val window = dialog.window
        window?.attributes = param
        dialogView.findViewById<ImageButton>(R.id.fragment_calender_dialog_cancle).setOnClickListener {
            dialog.dismiss()
        }
        dialogView.findViewById<TextView>(R.id.fragment_calender_dialog_week).setText(week)
        dialogView.findViewById<TextView>(R.id.fragment_calender_dialog_date).setText(day)


        var detailAdapter = CalendarDetailAdapter()
        val swipeHelper = CalendarHelperCallback(detailAdapter).apply {
            setClamp(200f)
//            setClamp(resources.displayMetrics.widthPixels.toFloat() / 4)
        }

        val itemTouchHelper = ItemTouchHelper(swipeHelper)
        var dialogRecyclerView = dialogView.findViewById<RecyclerView>(R.id.fragment_calender_dialog_rv)
        itemTouchHelper.attachToRecyclerView(dialogRecyclerView)
        dialogRecyclerView.addItemDecoration(DividerItemDecoration(context,DividerItemDecoration.VERTICAL))
        viewModel.schedule.observe(owner, {
            detailAdapter.list = it
            dialogRecyclerView.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
                adapter = detailAdapter
                setOnTouchListener{_, _ ->
                    swipeHelper.removePreviousClamp(this)
                    false
                }
            }
        })


        dialog.show()
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