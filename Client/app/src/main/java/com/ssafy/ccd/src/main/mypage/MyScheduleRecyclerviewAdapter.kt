package com.ssafy.ccd.src.main.mypage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.ccd.R
import com.ssafy.ccd.databinding.ItemMyscheduleListBinding
import com.ssafy.ccd.src.dto.Pet

class MyScheduleRecyclerviewAdapter(private val scheduleList: MutableList<Pet>) : RecyclerView.Adapter<MyScheduleRecyclerviewAdapter.MyScheduleViewHolder>() {

    inner class MyScheduleViewHolder(private val binding: ItemMyscheduleListBinding) : RecyclerView.ViewHolder(binding.root) {
        val scheduleLayout = binding.myScheduleItemCvSchedule

        fun bind(schedule: Pet, position: Int) {
//            binding.schedule = schedule
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyScheduleViewHolder {
        return MyScheduleViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_myschedule_list, parent, false))
    }

    override fun onBindViewHolder(holder: MyScheduleViewHolder, position: Int) {
        val schedule = scheduleList[position]
        holder.apply {
            bind(schedule, position)
            scheduleLayout.setOnClickListener {
                itemClickListener.onClick(it, position, schedule.id)
            }
        }
    }

    override fun getItemCount(): Int {
        return scheduleList.size
    }

    interface ItemClickListener{
        fun onClick(view: View, position: Int, id: Int)
    }

    private lateinit var itemClickListener : ItemClickListener

    fun setItemClickListener(itemClickListener: ItemClickListener){
        this.itemClickListener = itemClickListener
    }
}