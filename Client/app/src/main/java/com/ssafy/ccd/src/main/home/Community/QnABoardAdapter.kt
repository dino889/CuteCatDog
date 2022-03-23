package com.ssafy.ccd.src.main.home.Community

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.ccd.R
import com.ssafy.ccd.databinding.ItemQnaListBinding
import com.ssafy.ccd.src.dto.Board
import com.ssafy.ccd.src.dto.User

class QnABoardAdapter (var postList : MutableList<Board>, val userList: MutableList<User>, val userLikePost: MutableList<Int>, val context: Context) : RecyclerView.Adapter<QnABoardAdapter.QnABoardViewHolder>(){

    inner class QnABoardViewHolder(private val binding: ItemQnaListBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindInfo(post: Board) {
            for (user in userList) {
                if(post.userId == user.id) {
                    binding.writer = user
                }
            }

            binding.post = post
            binding.executePendingBindings()
        }
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QnABoardViewHolder {
        return QnABoardViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_qna_list, parent, false))
    }

    override fun onBindViewHolder(holder: QnABoardViewHolder, position: Int) {
        holder.apply {
            bindInfo(postList[position])
            setIsRecyclable(false)

//             QnAPostItem.setOnClickListener {
//                itemClickListener.onClick(it, position)
//            }
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