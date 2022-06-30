package com.ssafy.ccd.src.main.home.Community

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.ccd.R
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.databinding.FragmentLocalCommentBinding
import com.ssafy.ccd.databinding.ItemLocalCommentListBinding
import com.ssafy.ccd.databinding.ItemLocalReplyListBinding
import com.ssafy.ccd.src.dto.Comment
import com.ssafy.ccd.src.dto.User

class LocalReplyAdapter (val context: Context) : RecyclerView.Adapter<LocalReplyAdapter.LocalReplyViewHolder>(){
    private val TAG = "LocalCommentAdapter_ccd"

    lateinit var commentList: MutableList<Comment>
//    lateinit var commentAllList : MutableList<Comment>
    lateinit var userList: MutableList<User>

    // 현재 로그인한 유저의 아이디
    val userId = ApplicationClass.sharedPreferencesUtil.getUser().id

    inner class LocalReplyViewHolder(private val binding: ItemLocalReplyListBinding) : RecyclerView.ViewHolder(binding.root) {

        val moreBtn = binding.localReplyItemIvMore

        fun bindInfo(comment: Comment) {

            for (user in userList) {    // 작성자 nickname, profileImg 세팅
                if(comment.userId == user.id) {
                    binding.writer = user
                }
            }

            moreBtn.isVisible = comment.userId == ApplicationClass.sharedPreferencesUtil.getUser().id

            binding.comment = comment
            binding.executePendingBindings()

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalReplyViewHolder {
        return LocalReplyViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(
                    parent.context
                ), R.layout.item_local_reply_list, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: LocalReplyViewHolder, position: Int) {
        val comment = commentList[position]

        holder.apply {
            bindInfo(comment)
//            setIsRecyclable(false)

            moreBtn.setOnClickListener {
                val popup = PopupMenu(context, moreBtn)
                MenuInflater(context).inflate(R.menu.popup_menu, popup.menu)

                popup.show()
                popup.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.modify -> {
                            modifyItemClickListener.onClick(comment.id, comment.boardId, position)
                            return@setOnMenuItemClickListener true
                        }
                        R.id.delete -> {
                            deleteItemClickListener.onClick(comment.id, comment.boardId, position)
                            return@setOnMenuItemClickListener true
                        }
                        else -> {
                            return@setOnMenuItemClickListener false
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    interface ItemClickListener {
        fun onClick(view: View, position: Int, commentId: Int)
    }

    private lateinit var addReplyClickListener : ItemClickListener

    fun setAddReplyItemClickListener(itemClickListener: ItemClickListener) {
        this.addReplyClickListener = itemClickListener
    }


    interface MenuClickListener {
        fun onClick(commentId: Int, postId: Int, position: Int)
    }

    private lateinit var modifyItemClickListener : MenuClickListener

    fun setModifyItemClickListener(menuClickListener: MenuClickListener) {
        this.modifyItemClickListener = menuClickListener
    }

    private lateinit var deleteItemClickListener : MenuClickListener

    fun setDeleteItemClickListener(menuClickListener: MenuClickListener) {
        this.deleteItemClickListener = menuClickListener
    }


}