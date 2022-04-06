package com.ssafy.ccd.src.main.calender

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.ccd.R
import com.ssafy.ccd.databinding.ItemCalendarDetailListBinding
import com.ssafy.ccd.src.dto.Calendar
import com.ssafy.ccd.src.dto.Schedule

private const val TAG = "CalendarDetailAdapter"
class CalendarDetailAdapter : RecyclerView.Adapter<CalendarDetailAdapter.DetailViewHolder>(){
    var list = mutableListOf<Schedule>()
    val vHolder = HashMap<Int, DetailViewHolder>()

    inner class DetailViewHolder(private val binding:ItemCalendarDetailListBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(data : Schedule){
            binding.schedules = data
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        return DetailViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_calendar_detail_list,parent,false))
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        vHolder[position] = holder

        holder.apply {
            bind(list[position])
            holder.itemView.findViewById<TextView>(R.id.item_swipe_delete_tv).setOnClickListener {
                Log.d(TAG, "bind: 삭제")
                removeData(this.layoutPosition)
            }
            itemView.setOnClickListener{
                clickListener.onClick(it,position,list[position].schedule.id)
            }
        }
    }
    fun removeData(position: Int){
        removeListener.onRemove(list[position].schedule.id)
    }
    override fun getItemCount(): Int {
        return list.size
    }
    interface RemoveListener{
        fun onRemove(calendarId:Int)
    }
    private lateinit var removeListener : RemoveListener
    fun setRemoveListener(removeListener: RemoveListener){
        this.removeListener = removeListener
    }

    interface ItemClickListner{
        fun onClick(view: View, position:Int, calendarId: Int)
    }
    private lateinit var clickListener : ItemClickListner
    fun setClickListener(clickListner: ItemClickListner){
        this.clickListener = clickListner
    }
}