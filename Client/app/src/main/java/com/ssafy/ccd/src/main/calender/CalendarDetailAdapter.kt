package com.ssafy.ccd.src.main.calender

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.ccd.R
import com.ssafy.ccd.databinding.ItemCalendarDetailListBinding
import com.ssafy.ccd.src.dto.Calendar

class CalendarDetailAdapter : RecyclerView.Adapter<CalendarDetailAdapter.DetailViewHolder>(){
    var list = mutableListOf<Calendar>()
    inner class DetailViewHolder(private val binding:ItemCalendarDetailListBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(data : Calendar){
            binding.calendar = data
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        return DetailViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_calendar_detail_list,parent,false))
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        holder.apply {
            bind(list[position])
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}