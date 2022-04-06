package com.ssafy.ccd.src.main.mypage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.ccd.R
import com.ssafy.ccd.databinding.ItemMypostListBinding
import com.ssafy.ccd.src.dto.Board
import com.ssafy.ccd.src.dto.Pet

class MyPostRecyclerviewAdapter(): RecyclerView.Adapter<MyPostRecyclerviewAdapter.MyPostViewHolder>() {
    var list = mutableListOf<Board>()
    inner class MyPostViewHolder(private val binding: ItemMypostListBinding) : RecyclerView.ViewHolder(binding.root) {
        val postLayout = binding.myPostItemLl

        fun bind(post: Board, position: Int) {
            binding.board = post
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): MyPostRecyclerviewAdapter.MyPostViewHolder {
        return MyPostViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_mypost_list, parent, false))
    }

    override fun onBindViewHolder(holder: MyPostRecyclerviewAdapter.MyPostViewHolder, position: Int) {
        val post = list[position]
        holder.apply {
            bind(post, position)
//            postLayout.setOnClickListener {
//                itemClickListener.onClick(it, position, post.id)
//            }
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