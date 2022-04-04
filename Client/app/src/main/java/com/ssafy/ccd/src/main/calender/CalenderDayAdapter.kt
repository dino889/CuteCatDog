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
import androidx.core.os.bundleOf
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.ccd.R
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.src.main.MainActivity
import com.ssafy.ccd.src.network.service.CalendarService
import com.ssafy.ccd.src.network.viewmodel.MainViewModels
import com.ssafy.ccd.util.CommonUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.collections.ArrayList

class CalenderDayAdapter(val tmpMonth:Int, val dayList:MutableList<Date>,val date:ArrayList<String>,var viewModel:MainViewModels, var owner:LifecycleOwner,var context:Context,var fragment:CalenderFragment, var mainActivity: MainActivity):RecyclerView.Adapter<CalenderDayAdapter.DayViewHolder>() {
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
            var strDay = day.text.toString()

            if(dayList[position].month.toString().length == 1){
                strMonth = "0${strMonth}"
            }
            if(day.text.toString().length == 1){
                strDay = "0${strDay}"
            }
            var strDate = "${strMonth}월 ${strDay}일"
            var comDate = "${month}월 ${monthOfday}일"
            var week = CommonUtils.convertWeek(position%7)
            var checkDay = day.text.toString()
            if(checkDay.length == 1){
                checkDay = "0${checkDay}"
            }
            if(checkDay.equals(strDay)){
                if(strDate.equals(comDate)){
                    holder.itemView.findViewById<ImageView>(R.id.fragment_calendar_point).visibility = View.VISIBLE
                    holder.layout.setOnClickListener {
                        runBlocking {
                            viewModel.getCalendarListbyDate(ApplicationClass.sharedPreferencesUtil.getUser().id,CommonUtils.makeBirthMilliSecond(date[i]))
                        }
                        showDetailDialog(strDate,week,CommonUtils.makeBirthMilliSecond(date[i]))
                    }
                }
            }

        }


    }
    @SuppressLint("ClickableViewAccessibility")
    fun showDetailDialog(day:String, week:String, date:String){
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
            setClamp(context.resources.displayMetrics.widthPixels.toFloat() / 4)
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

            detailAdapter.setRemoveListener(object: CalendarDetailAdapter.RemoveListener {
                override fun onRemove(calendarId: Int) {
                    GlobalScope.launch {
                        var response = CalendarService().deleteCalendar(calendarId)
                        var res = response.body()
                        if(response.code() == 200){
                            if(res!=null){
                                if(res.success){
                                    runBlocking {
                                        viewModel.getCalendarListbyDate(ApplicationClass.sharedPreferencesUtil.getUser().id,date)
                                        viewModel.getCalendarListbyUser(ApplicationClass.sharedPreferencesUtil.getUser().id)
                                    }
                                }
                            }
                        }
                    }
                }
            })

            detailAdapter.setClickListener(object: CalendarDetailAdapter.ItemClickListner {
                override fun onClick(view: View, position: Int, calendarId: Int) {
                    mainActivity.runOnUiThread {
                        var calendarId = bundleOf("calendarId" to calendarId)
                        fragment.findNavController().navigate(R.id.calendarDetailFragment,calendarId)
                    }
                    dialog.dismiss()
                }
            })

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