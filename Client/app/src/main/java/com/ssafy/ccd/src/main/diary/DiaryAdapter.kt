package com.ssafy.ccd.src.main.diary

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.ccd.R
import com.ssafy.ccd.databinding.ItemDiaryListBinding
import com.ssafy.ccd.src.dto.Diary
import com.ssafy.ccd.src.dto.Photo
import com.ssafy.ccd.src.network.viewmodel.MainViewModels

class DiaryAdapter(val context: Context,val viewModel:MainViewModels, val owner:LifecycleOwner) : RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder>() {
    var list = mutableListOf<Diary>()
    inner class DiaryViewHolder(private var binding:ItemDiaryListBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(diary: Diary){
            binding.diary = diary
//            binding.viewModel = viewModel
//            binding.executePendingBindings()
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
            Log.d("DiaryAdapter", "onBindViewHolder: ${list[position].photo}")
            itemView.findViewById<RecyclerView>(R.id.fragment_diary_ImgRv).apply{
                layoutManager = photoListManager
                adapter = photoListAdapter
//                viewModel.diaryPhotoList.observe(owner, {
//                    Log.d("DiaryAdapter", "onBindViewHolder: ${it}")
//                    photoListAdapter.list = it
//                })
                photoListAdapter.list = list[position].photo as MutableList<Photo>
            }

            this.itemView.setOnClickListener {
                itemClickListener.onClick(it,position,list[position].id)
            }

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    interface ItemClickListener{
        fun onClick(view: View, position: Int, id: Int)
    }

    private lateinit var itemClickListener : ItemClickListener

    fun setItemClickListener(itemClickListener: ItemClickListener){
        this.itemClickListener = itemClickListener
    }
}