package com.ssafy.ccd.src.main.home

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.ssafy.ccd.R
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.databinding.ItemHomeCommuLocalBinding
import com.ssafy.ccd.databinding.ItemHomeCommuQnaBinding
import com.ssafy.ccd.databinding.ItemHomeCommuShareBinding
import com.ssafy.ccd.src.dto.Board
import com.ssafy.ccd.src.dto.Message
import com.ssafy.ccd.src.dto.User
import com.ssafy.ccd.src.network.service.BoardService
import com.ssafy.ccd.src.network.service.UserService
import com.ssafy.ccd.util.CommonUtils
import kotlinx.coroutines.runBlocking
import retrofit2.Response
import java.lang.reflect.Type

class BoardAdapter(var postList : MutableList<Board>, val userList: MutableList<User>, val userLikePost: MutableList<Int>, val context: Context) : RecyclerView.Adapter<BoardAdapter.BoardBaseHolder>(){
    private val TAG = "BoardAdapter_ccd"
    // 현재 로그인한 유저의 아이디
    val userId = ApplicationClass.sharedPreferencesUtil.getUser().id
    private val BOARD_TYPE_TOWN = 1
    private val BOARD_TYPE_QNA = 2
    private val BOARD_TYPE_SHARE = 3

    open inner class BoardBaseHolder(itemView:View) : RecyclerView.ViewHolder(itemView)

    inner class LocalBoardHolder(private val binding: ItemHomeCommuLocalBinding) : BoardBaseHolder(binding.root) {
        fun bindInfo(post: Board) {
            binding.post = post
            binding.executePendingBindings()
        }
    }

    inner class QnABoardHolder(private val binding: ItemHomeCommuQnaBinding) : BoardBaseHolder(binding.root) {
        val heart = binding.lottieAnimationView
        fun bindInfo(post: Board) {
            // 전체 유저 리스트 받아서
            // post.userId == userList.id -> 작성자
            // 로 코드 수정하기
            for (user in userList) {
                if(post.userId == user.id) {
                    binding.writer = user
                }
            }

            for (i in userLikePost) {   // 로그인 유저가 좋아요 누른 게시글 표시
                if(post.id == i) {
                    binding.lottieAnimationView.progress = 0.5F
                    break
                }
                binding.lottieAnimationView.progress = 0.0F
            }

            val responseLike : Response<Message>
            runBlocking {
                responseLike = BoardService().selectPostIsLike(post.id, userId)
            }
            val res = responseLike.body()
            if(responseLike.code() == 200 || responseLike.code() == 500) {
                if(res != null) {
                    if(res.data["isSuccess"] == true && res.message == "좋아요 가능") {  // 사용자가 좋아요 안누른 상환
                        heart.setColorFilter(context.resources.getColor(R.color.black))
                    } else if(res.data["isSuccess"] == false) { // 이미 like 한 게시물 이거나 게시물이 존재하지 않습니다.
                        heart.setColorFilter(context.resources.getColor(R.color.red))
                    }
                }
            }


            binding.post = post
            binding.executePendingBindings()
        }
    }

    inner class ShareBoardHolder(private val binding: ItemHomeCommuShareBinding) : BoardBaseHolder(binding.root) {
        fun bindInfo(post: Board) {
            // 전체 유저 리스트 받아서
            // post.userId == userList.id -> 작성자
            // 로 코드 수정하기
            for (user in userList) {
                if(post.userId == user.id) {
                    binding.writer = user
                }
            }

            for (i in userLikePost) {   // 로그인 유저가 좋아요 누른 게시글 표시
                if(post.id == i) {
                    binding.lottieAnimationView.progress = 0.5F
                    break
                }
                binding.lottieAnimationView.progress = 0.0F
            }

            binding.post = post
            binding.executePendingBindings()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return postList[position].typeId
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardAdapter.BoardBaseHolder {
        return when(viewType) {
            BOARD_TYPE_TOWN -> {
                LocalBoardHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_home_commu_local, parent, false))
            }
            BOARD_TYPE_QNA -> {
                QnABoardHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_home_commu_qna,
                        parent,
                        false
                    )
                )

            }
            else -> {
                ShareBoardHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_home_commu_share,
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: BoardAdapter.BoardBaseHolder, position: Int) {
        when (postList[position].typeId) {
            BOARD_TYPE_TOWN -> {
                (holder as LocalBoardHolder).apply {
                    bindInfo(postList[position])
                    holder.setIsRecyclable(false)
                }
            }
            BOARD_TYPE_QNA -> {
                (holder as QnABoardHolder).apply {
                    bindInfo(postList[position])
                    holder.setIsRecyclable(false)

                }
            }
            else -> {
                (holder as ShareBoardHolder).bindInfo(postList[position])
                holder.setIsRecyclable(false)
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return postList[position].id.toLong()
    }

    override fun getItemCount(): Int {
        return postList.size
    }

//    interface ItemClickListener {
//        fun onClick(view: View, position: Int, id:Int)
//    }
//
//    private lateinit var itemClickListener : ItemClickListener
//
//    fun setItemClickListener(itemClickListener: ItemClickListener) {
//        this.itemClickListener = itemClickListener
//    }

//    private fun isLike(postId: Int, userId: Int) { // 로그인한 유저가 좋아요 누른 게시글 리스트 불러와서 post.id랑 게시글 리스트.id 랑 같으면 내가 좋아요 누른 게시글 표시
//        val response : Response<Message>
//        runBlocking {
//            response = BoardService().selectPostIsLike(postId, userId)
//        }
//        val res = response.body()
//        if(response.code() == 200 || response.code() == 500) {
//            if(res != null) {
//                if(res.data["isSuccess"] == true && res.message == "좋아요 가능") {  // 사용자가 좋아요 안누른 상환
//
//                } else if(res.data["isSuccess"] == false) { // 이미 like 한 게시물 이거나 게시물이 존재하지 않습니다.
//
//                }
//            }
//        }
//
//    }
}