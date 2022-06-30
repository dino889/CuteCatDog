package com.ssafy.ccd.src.main.home.Community

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.ssafy.ccd.R
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.databinding.ItemQnaListBinding
import com.ssafy.ccd.src.dto.Board
import com.ssafy.ccd.src.dto.LikeRequestDto
import com.ssafy.ccd.src.dto.Message
import com.ssafy.ccd.src.dto.User
import com.ssafy.ccd.src.main.MainActivity
import com.ssafy.ccd.src.network.service.BoardService
import com.ssafy.ccd.util.CommonUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Response

/**
 * @author Jueun
 * QnABoard RecyclerView를 위해서 만든 것
 */
class QnABoardAdapter (var postList : MutableList<Board>, val userList: MutableList<User>, val userLikePost: MutableList<Int>, val context: Context, val fragment: QnABoardFragment) : RecyclerView.Adapter<QnABoardAdapter.QnABoardViewHolder>(){

    val TAG = "QnABoardAdapter_ccd"

    inner class QnABoardViewHolder(private val binding: ItemQnaListBinding) : RecyclerView.ViewHolder(binding.root) {

        val mainActivity : MainActivity = context as MainActivity

        fun bindInfo(post: Board) {
            for (user in userList) {
                if(post.userId == user.id) {
                    binding.writer = user
                }
            }

            /**
             * 답글 달기
             */
            binding.fragmentQnaBoardAnswerBtn.setOnClickListener {
                (context as MainActivity).mainViewModels.writeType = 1
                context.mainViewModels.boardQna = post
                fragment.findNavController().navigate(R.id.writeQnaFragment)
            }

            /**
             * 공감버튼 누르기
             */

            binding.fragmentQnaBoardLikeBtn.setOnClickListener {
                insertLike(post.id)
            }

            binding.post = post
        }

        /**
         * 공감해요(좋아요)를 누르는 것
         */
        private fun insertLike(boardId:Int) {
            var response : Response<Message>

            runBlocking {
                response = BoardService().insertOrDeletePostLike(LikeRequestDto(boardId , ApplicationClass.sharedPreferencesUtil.getUser().id))
            }

            if(response.code() == 200 || response.code() == 500) {
                val res = response.body()
                if(res != null) {
                    if(res.success && res.data["isSuccess"] == true) {
                        reLoadData(boardId)
                    } else if(res.data["isSuccess"] == false) {
                        Log.d(TAG, "deleteQuestion: ${res.message}")
                    }
                }
            }
        }

        /**
         * 해당 아이디에 맞는 게시판 데이터를 다시 로드한다
         */
        private fun reLoadData(boardId: Int) {
            var response : Response<Message>

            runBlocking {
                response = BoardService().selectPostDetail(boardId)
            }

            if(response.code() == 200 || response.code() == 500) {
                val res = response.body()
                if(res != null) {
                    if(res.success) {
                        val type = object : TypeToken<Board>() {}.type
                        val post: Board = CommonUtils.parseDto(res.data["board"]!!, type)
                        binding.fragmentQnaBoardLikeCnt.text = post.count.toString()
                    } else if(res.data["isSuccess"] == false) {
                        Log.d(TAG, "deleteQuestion: ${res.message}")
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QnABoardViewHolder {
        return QnABoardViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_qna_list, parent, false))
    }

    override fun onBindViewHolder(holder: QnABoardViewHolder, position: Int) {
        holder.apply {
            bindInfo(postList[position])
            setIsRecyclable(false)

            holder.itemView.setOnClickListener {
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

    fun updateData(data : MutableList<Board>){
        this.postList = data
        notifyDataSetChanged()
    }

}