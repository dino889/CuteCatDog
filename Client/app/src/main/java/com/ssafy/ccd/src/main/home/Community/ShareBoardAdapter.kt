package com.ssafy.ccd.src.main.home.Community

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.ssafy.ccd.R
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.databinding.ItemShareListBinding
import com.ssafy.ccd.src.dto.Board
import com.ssafy.ccd.src.dto.User

class ShareBoardAdapter (val context: Context) : RecyclerView.Adapter<ShareBoardAdapter.ShareBoardViewHolder>(){
    lateinit var postList : MutableList<Board>
    lateinit var userList: MutableList<User>
    lateinit var userLikePost: MutableList<Int>

    inner class ShareBoardViewHolder(private val binding: ItemShareListBinding) : RecyclerView.ViewHolder(binding.root) {
        val heartBtn = binding.shareItemLottieHeart
        val commentBtn = binding.shareItemClComment
        val moreBtn = binding.shareItemBtnMore
        val contentDetail = binding.shareItemTvPostDetail

        val allContent = binding.shareItemTvContentAll
        val splitContent = binding.shareItemTvContent

        fun bindInfo(post: Board) {
            for (user in userList) {
                if (post.userId == user.id) {
                    binding.writer = user
                }
            }

            for (i in userLikePost) {   // 로그인 유저가 좋아요 누른 게시글 표시
                if (post.id == i) {
                    heartBtn.progress = 0.5F
                    break
                }
                heartBtn.progress = 0.0F
            }

            moreBtn.isVisible = post.userId == ApplicationClass.sharedPreferencesUtil.getUser().id

            contentDetail.isVisible = post.content.length > 30

            binding.post = post
            binding.executePendingBindings()

        }
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShareBoardAdapter.ShareBoardViewHolder {
        return ShareBoardViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(
                    parent.context
                ), R.layout.item_share_list, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ShareBoardAdapter.ShareBoardViewHolder, position: Int) {
        val post = postList[position]
        holder.apply {
            bindInfo(post)
            setIsRecyclable(false)


            heartBtn.setOnClickListener {
                heartItemClickListener.onClick(it as LottieAnimationView, position, post.id)
            }

            commentBtn.setOnClickListener {
                commentItemClickListener.onClick(it, post.id)
            }

            contentDetail.setOnClickListener {
                detailItemClickListener.onClick(it, allContent, splitContent, post.id)
            }

            moreBtn.setOnClickListener {
                val popup = PopupMenu(context, moreBtn)
                MenuInflater(context).inflate(R.menu.popup_menu, popup.menu)

                popup.show()
                popup.setOnMenuItemClickListener {
                    when(it.itemId) {
                        R.id.modify -> {
                            modifyItemClickListener.onClick(post.id, position)
                            return@setOnMenuItemClickListener true
                        }
                        R.id.delete -> {
                            deleteItemClickListener.onClick(post.id, position)
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

    interface HeartItemClickListener {
        fun onClick(heart: LottieAnimationView, position: Int, id: Int)
    }


    private lateinit var heartItemClickListener : HeartItemClickListener

    fun setHeartItemClickListener(itemClickListener: HeartItemClickListener) {
        this.heartItemClickListener = itemClickListener
    }

    interface ItemClickListener {
        fun onClick(view: View, postId: Int)
    }

    private lateinit var commentItemClickListener : ItemClickListener

    fun setCommentItemClickListener(itemClickListener: ItemClickListener) {
        this.commentItemClickListener = itemClickListener
    }

    interface DetailItemClickListener {
        fun onClick(view: View, all: TextView, split: TextView, postId: Int)
    }

    private lateinit var detailItemClickListener : DetailItemClickListener

    fun setDetailItemClickListener(itemClickListener: DetailItemClickListener ) {
        this.detailItemClickListener = itemClickListener
    }

    interface MenuClickListener {
        fun onClick(postId: Int, position: Int)
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