package com.ssafy.ccd.src.main.home.Community

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.ccd.R
import com.ssafy.ccd.databinding.ItemShareListBinding
import com.ssafy.ccd.src.dto.Board
import com.ssafy.ccd.src.dto.User

class ShareBoardAdapter (var postList : MutableList<Board>, val userList: MutableList<User>, val userLikePost: MutableList<Int>, val context: Context) : RecyclerView.Adapter<ShareBoardAdapter.ShareBoardViewHolder>(){

    inner class ShareBoardViewHolder(private val binding: ItemShareListBinding) : RecyclerView.ViewHolder(binding.root) {
        val postDetailBtn = binding.shareItemTvPostDetail

        fun bindInfo(post: Board) {
            for (user in userList) {
                if(post.userId == user.id) {
                    binding.writer = user
                }
            }

            for (i in userLikePost) {
                if(post.id == i) {
                    binding.shareItemLottieHeart.progress = 0.5F
                } else {
                    binding.shareItemLottieHeart.progress = 0.0F
                }
            }

            binding.post = post
            binding.executePendingBindings()
        }
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShareBoardViewHolder {
        return ShareBoardViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_share_list, parent, false))
    }

    override fun onBindViewHolder(holder: ShareBoardViewHolder, position: Int) {
        holder.apply {
            bindInfo(postList[position])
            setIsRecyclable(false)

             postDetailBtn.setOnClickListener {
                itemClickListener.onClick(it, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    interface ItemClickListener {
        fun onClick(view: View, position: Int)
    }

    private lateinit var itemClickListener : ItemClickListener

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

}