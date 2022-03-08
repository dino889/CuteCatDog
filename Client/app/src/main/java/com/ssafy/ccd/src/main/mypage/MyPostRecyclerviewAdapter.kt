package com.ssafy.ccd.src.main.mypage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.ccd.R
import com.ssafy.ccd.databinding.RecyclerviewMypostListItemBinding
import com.ssafy.ccd.src.dto.Pet

class MyPostRecyclerviewAdapter(private val postList: List<Pet>): RecyclerView.Adapter<MyPostRecyclerviewAdapter.MyPostViewHolder>() {

    inner class MyPostViewHolder(private val binding: RecyclerviewMypostListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val postLayout = binding.myPostItemLl

        fun bind(post: Pet, position: Int) {
//            binding.post = post
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): MyPostRecyclerviewAdapter.MyPostViewHolder {
        return MyPostViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.recyclerview_mypost_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: MyPostRecyclerviewAdapter.MyPostViewHolder, position: Int) {
        val post = postList[position]
        holder.apply {
            bind(post, position)
            postLayout.setOnClickListener {
                itemClickListener.onClick(it, position, post.id)
            }
        }
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    interface ItemClickListener{
        fun onClick(view: View, position: Int, id: Int)
    }

    private lateinit var itemClickListener : ItemClickListener

    fun setItemClickListener(itemClickListener: ItemClickListener){
        this.itemClickListener = itemClickListener
    }
}