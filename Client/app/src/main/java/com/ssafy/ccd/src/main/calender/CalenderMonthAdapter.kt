package com.ssafy.ccd.src.main.calender

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.ssafy.ccd.R
import com.ssafy.ccd.util.CommonUtils
import java.util.*

class CalenderMonthAdapter(val context: Context,val date:String):RecyclerView.Adapter<CalenderMonthAdapter.MonthViewHolder>() {
    val center = Int.MAX_VALUE/2
    private var calender = Calendar.getInstance()

    inner class MonthViewHolder(val layout: View):RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_calender_month,parent,false)
        return MonthViewHolder(view)
    }

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        calender.time = Date()
        calender.set(Calendar.DAY_OF_MONTH,1)
        calender.add(Calendar.MONTH,position-center)
        holder.layout.findViewById<TextView>(R.id.fragment_calender_dateMonth).text = "${CommonUtils.convertEnglishMonth(calender.get(Calendar.MONTH)+1)} ${calender.get(Calendar.YEAR)}"
        val tmpMonth = calender.get(Calendar.MONTH)

        var dayList:MutableList<Date> = MutableList(6*7){Date()}
        for(i in 0..5){
            for(j in 0..6){
                calender.add(Calendar.DAY_OF_MONTH,(1-calender.get(Calendar.DAY_OF_WEEK))+j)
                dayList[i*7+j] = calender.time
            }
            calender.add(Calendar.WEEK_OF_MONTH,1)
        }

        val dayListManager = GridLayoutManager(holder.layout.context,7)
        Log.d("Adapter", "onBindViewHolder: ${date}")
        val dayListAdapter = CalenderDayAdapter(tmpMonth,dayList,date)

        holder.layout.findViewById<RecyclerView>(R.id.fragment_calender_dayRv).apply {
            layoutManager = dayListManager
            adapter = dayListAdapter
        }

        dayListAdapter.setItemClickListener(object : CalenderDayAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int, day: String, week: String) {
                showDetailDialog(day,week)
            }


        })
    }
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

        dialog.show()
    }
    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }

}