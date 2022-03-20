package com.ssafy.ccd.src.main.diary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.ccd.R
import com.ssafy.ccd.databinding.ItemDiaryPhotoListBinding
import com.ssafy.ccd.src.dto.Photo

class DiaryPhotoRvAdapter : RecyclerView.Adapter<DiaryPhotoRvAdapter.PhotoViewHolder>(){
    var list = mutableListOf<Photo>()
    inner class PhotoViewHolder(private val binding:ItemDiaryPhotoListBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(photo:Photo){
            binding.photo = photo
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_diary_photo_list,parent,false))
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.apply {
            bind(list[position])
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}