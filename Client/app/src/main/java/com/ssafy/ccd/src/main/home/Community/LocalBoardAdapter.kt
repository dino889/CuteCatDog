package com.ssafy.ccd.src.main.home.Community

import android.content.Context
import android.location.Geocoder
import android.util.Log
import android.view.*
import android.widget.Filter
import android.widget.Filterable
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.ssafy.ccd.R
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.databinding.ItemLocalListBinding
import com.ssafy.ccd.src.dto.Board
import com.ssafy.ccd.src.dto.User
import java.util.*

class LocalBoardAdapter (val context: Context) : RecyclerView.Adapter<LocalBoardAdapter.LocalBoardViewHolder>(){
//class LocalBoardAdapter(val context: Context) : ListAdapter<Board, LocalBoardAdapter.LocalBoardViewHolder>(DiffCallback) {
    lateinit var postList : MutableList<Board>
    lateinit var userList: MutableList<User>
    lateinit var userLikePost: MutableList<Int>

    inner class LocalBoardViewHolder(private val binding: ItemLocalListBinding) : RecyclerView.ViewHolder(binding.root) {
        val heartBtn = binding.fragmentLocalboardHeart
        val commentBtn = binding.localItemClComment
        val moreBtn = binding.localItemBtnMore

        fun bindInfo(post: Board) {
            binding.post = post

            for (user in userList) {    // 작성자 nickname, profileImg 세팅
                if(post.userId == user.id) {
                    binding.writer = user
                }
            }

            for (i in userLikePost) {   // 로그인 유저가 좋아요 누른 게시글 표시
                if(post.id == i) {
                    heartBtn.progress = 0.5F
                    break
                }
                heartBtn.progress = 0.0F
            }

            moreBtn.isVisible = post.userId == ApplicationClass.sharedPreferencesUtil.getUser().id

            if(post.lat.toString() == "null" || post.lat.toString() == "") {
                binding.fragmentLocalboardLoc.text = ""
            } else {
                val geoCoder = Geocoder(context, Locale.getDefault())
                val address = geoCoder.getFromLocation(post.lat, post.lng, 1).first().getAddressLine(0)
                val tmp = address.split(" ")
                binding.fragmentLocalboardLoc.text = tmp[3]
            }

            binding.executePendingBindings()
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalBoardViewHolder {
        return LocalBoardViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_local_list, parent, false))
    }

    override fun onBindViewHolder(holder: LocalBoardViewHolder, position: Int) {
//        val post = getItem(position)
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

    object DiffCallback : DiffUtil.ItemCallback<Board>() {
        override fun areItemsTheSame(oldItem: Board, newItem: Board): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Board, newItem: Board): Boolean {
            return oldItem.id == newItem.id
        }
    }



}