package com.ssafy.ccd.src.main.calender

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
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.ssafy.ccd.R
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.src.main.MainActivity
import com.ssafy.ccd.src.network.viewmodel.MainViewModels
import com.ssafy.ccd.util.CommonUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.collections.ArrayList

class CalenderMonthAdapter(val context: Context, val date:ArrayList<String>, val viewModel: MainViewModels, val owner: LifecycleOwner,var fragment:CalenderFragment,var mainActivity: MainActivity):RecyclerView.Adapter<CalenderMonthAdapter.MonthViewHolder>() {
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
        val dayListAdapter = CalenderDayAdapter(tmpMonth,dayList,date,viewModel,owner,context,fragment,mainActivity)

        holder.layout.findViewById<RecyclerView>(R.id.fragment_calender_dayRv).apply {
            layoutManager = dayListManager
            adapter = dayListAdapter
        }

    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }

}