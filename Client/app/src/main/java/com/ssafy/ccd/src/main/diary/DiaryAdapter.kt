package com.ssafy.ccd.src.main.diary

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.ccd.R
import com.ssafy.ccd.databinding.ItemDiaryListBinding
import com.ssafy.ccd.src.dto.Diary
import com.ssafy.ccd.src.dto.Photo

class DiaryAdapter(val context: Context) : RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder>() {
    var list = mutableListOf<Diary>()
    inner class DiaryViewHolder(private var binding:ItemDiaryListBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(diary: Diary){
            binding.diary = diary
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
        return DiaryViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_diary_list,parent,false))
    }

    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        var photoListManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        var photoListAdapter = DiaryPhotoRvAdapter()
        holder.apply {
            bind(list[position])

            itemView.findViewById<RecyclerView>(R.id.fragmentDiary_img_item).apply{
                layoutManager = photoListManager
                adapter = photoListAdapter
                photoListAdapter.list = list[position].photo as MutableList<Photo>
            }

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}