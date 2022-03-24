package com.ssafy.ccd.src.main.home.Community

import android.content.Context
import android.util.Log
import android.view.*
import android.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.ccd.R
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.databinding.ItemLocalListBinding
import com.ssafy.ccd.src.dto.Board
import com.ssafy.ccd.src.dto.User
import com.ssafy.ccd.src.network.service.BoardService

class LocalBoardAdapter (var postList : MutableList<Board>, val userList: MutableList<User>, val userLikePost: MutableList<Int>, val context: Context) : RecyclerView.Adapter<LocalBoardAdapter.LocalBoardViewHolder>(){

    inner class LocalBoardViewHolder(private val binding: ItemLocalListBinding) : RecyclerView.ViewHolder(binding.root) {
        val heartBtn = binding.fragmentLocalboardHeart
        val commentBtn = binding.fragmentLocalboardChat
        val moreBtn = binding.localItemBtnMore

        fun bindInfo(post: Board) {
            for (user in userList) {    // 작성자 nickname, profileImg 세팅
                if(post.userId == user.id) {
                    binding.writer = user
                }
            }

            for (i in userLikePost) {   // 로그인 유저가 좋아요 누른 게시글 표시
                if(post.id == i) {
                    binding.fragmentLocalboardHeart.progress = 0.5F
                } else {
                    binding.fragmentLocalboardHeart.progress = 0.0F
                }
            }

            moreBtn.isVisible = post.userId == ApplicationClass.sharedPreferencesUtil.getUser().id

            binding.post = post
            binding.executePendingBindings()
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalBoardViewHolder {
        return LocalBoardViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_local_list, parent, false))
    }

    override fun onBindViewHolder(holder: LocalBoardViewHolder, position: Int) {
        holder.apply {
            bindInfo(postList[position])
            setIsRecyclable(false)

            heartBtn.setOnClickListener {
                heartItemClickListener.onClick(it, position)
            }

            commentBtn.setOnClickListener {
                commentItemClickListener.onClick(it, position)
            }

            moreBtn.setOnClickListener {
                val popup = PopupMenu(context, moreBtn)
                MenuInflater(context).inflate(R.menu.popup_menu, popup.menu)

                popup.show()
                popup.setOnMenuItemClickListener {
                    when(it.itemId) {
                        R.id.modify -> {
                            modifyItemClickListener.onClick(postList[position].id)
                            return@setOnMenuItemClickListener true
                        }
                        R.id.delete -> {
                            deleteItemClickListener.onClick(postList[position].id)
                            return@setOnMenuItemClickListener true
                        } else -> {
                            return@setOnMenuItemClickListener false
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    interface ItemClickListener {
        fun onClick(view: View, position: Int)
    }

    private lateinit var heartItemClickListener : ItemClickListener

    fun setHeartItemClickListener(itemClickListener: ItemClickListener) {
        this.heartItemClickListener = itemClickListener
    }

    private lateinit var commentItemClickListener : ItemClickListener

    fun setCommentItemClickListener(itemClickListener: ItemClickListener) {
        this.commentItemClickListener = itemClickListener
    }

    interface MenuClickListener {
        fun onClick(postId: Int)
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