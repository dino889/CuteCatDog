package com.ssafy.ccd.src.main.mypage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.ccd.R
import com.ssafy.ccd.databinding.ItemHistoryListBinding
import com.ssafy.ccd.src.dto.Board
import com.ssafy.ccd.src.dto.History
import com.ssafy.ccd.src.dto.Pet

class MyHistoryRecyclerviewAdapter(): RecyclerView.Adapter<MyHistoryRecyclerviewAdapter.MyHistoryViewHolder>() {

    var historyList = mutableListOf<History>()
    inner class MyHistoryViewHolder(private val binding: ItemHistoryListBinding) : RecyclerView.ViewHolder(binding.root) {
        val historyDelete = binding.myHistoryItemIvDelete

        fun bind(history: History, position: Int) {
            binding.history = history
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): MyHistoryRecyclerviewAdapter.MyHistoryViewHolder {
        return MyHistoryViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_history_list, parent, false))
    }

    override fun onBindViewHolder(holder: MyHistoryRecyclerviewAdapter.MyHistoryViewHolder, position: Int) {
        val history = historyList[position]
        holder.apply {
            bind(history, position)
            historyDelete.setOnClickListener {
                itemClickListener.onClick(it, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    interface ItemClickListener{
        fun onClick(view: View, position: Int)
    }

    private lateinit var itemClickListener : ItemClickListener

    fun setItemClickListener(itemClickListener: ItemClickListener){
        this.itemClickListener = itemClickListener
    }
}