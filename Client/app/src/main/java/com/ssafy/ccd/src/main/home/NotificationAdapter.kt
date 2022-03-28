package com.ssafy.ccd.src.main.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.ccd.R
import com.ssafy.ccd.databinding.ItemNotificationBinding
import com.ssafy.ccd.src.dto.Notification

class NotificationAdapter() : RecyclerView.Adapter<NotificationAdapter.NotiViewHolder>() {
    var list = mutableListOf<Notification>()
    inner class NotiViewHolder(private var binding:ItemNotificationBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(noti:Notification){
            binding.noti = noti
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotiViewHolder {
        return NotiViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_notification,parent,false))
    }

    override fun onBindViewHolder(holder: NotiViewHolder, position: Int) {
        holder.apply {
            bind(list[position])
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}