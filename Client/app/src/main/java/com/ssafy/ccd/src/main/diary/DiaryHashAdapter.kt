package com.ssafy.ccd.src.main.diary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.ccd.R
import com.ssafy.ccd.databinding.ItemDiaryWriteHashtagBinding
import com.ssafy.ccd.src.dto.Hashtag

class DiaryHashAdapter : RecyclerView.Adapter<DiaryHashAdapter.HashViewHolder>(){
    var list = mutableListOf<Hashtag>()
    inner class HashViewHolder(private val binding:ItemDiaryWriteHashtagBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(hash:Hashtag){
            binding.hash = hash
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HashViewHolder {
        return HashViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_diary_write_hashtag,parent,false))
    }

    override fun onBindViewHolder(holder: HashViewHolder, position: Int) {
        holder.apply {
            bind(list[position])
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}