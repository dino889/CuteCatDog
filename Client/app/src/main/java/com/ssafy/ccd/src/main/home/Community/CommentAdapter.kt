package com.ssafy.ccd.src.main.home.Community

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.ccd.R
import com.ssafy.ccd.databinding.ItemAnswerBinding
import com.ssafy.ccd.src.dto.Comment
import com.ssafy.ccd.src.dto.Message
import com.ssafy.ccd.src.main.MainActivity
import com.ssafy.ccd.src.network.service.BoardService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Response

class CommentAdapter (var commentList : MutableList<Comment>, val context: Context, val fragment : QnABoardDetailFragment, val loginUserId : Int) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    inner class CommentViewHolder(private val binding: ItemAnswerBinding) : RecyclerView.ViewHolder(binding.root) {
        var willDeleteComment:Comment? = null

        val dialogBuilder = AlertDialog.Builder(context)
            .setTitle("답변 삭제")
            .setMessage("정말로 답변을 삭제하시겠습니까?")
            .setPositiveButton("네") { _, _ -> CoroutineScope(Dispatchers.Default).launch { deleteItem(willDeleteComment!!.boardId, willDeleteComment!!.id) } }
            .setNegativeButton("아니요") { _, _ -> dlg.hide() }

        val dlg : AlertDialog = dialogBuilder.create()

        fun bindInfo(comment: Comment) {
            binding.comment = comment
            binding.loginUserId = loginUserId

            binding.itemAnswerTvDelete.setOnClickListener {
                willDeleteComment = comment
                dlg.show()
            }

            binding.itemAnswerTvRewrite.setOnClickListener {
                (context as MainActivity).mainViewModels.commentQna = comment
                (context as MainActivity).mainViewModels.writeType = 3
                fragment.findNavController().navigate(R.id.writeQnaFragment)
            }
        }

        private suspend fun deleteItem(postId : Int, id:Int){
            var response : Response<Message>

            runBlocking {
                response = BoardService().deleteComment(id)
            }

            if(response.code() == 200 || response.code() == 500) {
                val res = response.body()
                if(res != null) {
                    if(res.success && res.data["isSuccess"] == true) {
                        (context as MainActivity).mainViewModels.getCommentsByPostId(postId)
                    } else if(res.data["isSuccess"] == false) {
                        Log.e("CommentAdapter_ccd", "insertPost: ${res.message}", )
                    }
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_answer, parent, false))
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.apply {
            bindInfo(commentList[position])
        }
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    interface ItemClickListener {
        fun onClick(view: View, position: Int)
    }

    private lateinit var itemClickListener: ItemClickListener

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    fun updateData(data: MutableList<Comment>) {
        this.commentList = data
        notifyDataSetChanged()
    }
}