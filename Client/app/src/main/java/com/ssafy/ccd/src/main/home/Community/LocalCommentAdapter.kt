package com.ssafy.ccd.src.main.home.Community

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.ccd.R
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.databinding.FragmentLocalCommentBinding
import com.ssafy.ccd.databinding.ItemLocalCommentListBinding
import com.ssafy.ccd.src.dto.Comment
import com.ssafy.ccd.src.dto.Message
import com.ssafy.ccd.src.dto.User
import com.ssafy.ccd.src.network.service.BoardService
import com.ssafy.ccd.src.network.viewmodel.MainViewModels
import kotlinx.coroutines.runBlocking
import retrofit2.Response

class LocalCommentAdapter (val context: Context, val mainViewModel: MainViewModels) : RecyclerView.Adapter<LocalCommentAdapter.LocalCommentViewHolder>(){
    private val TAG = "LocalCommentAdapter_ccd"

    lateinit var commentList: MutableList<Comment>
    lateinit var commentAllList : MutableList<Comment>
    lateinit var userList: MutableList<User>
    lateinit var commentReplyAdapter : LocalReplyAdapter
    lateinit var dialog: Dialog

    // 현재 로그인한 유저의 아이디
    val userId = ApplicationClass.sharedPreferencesUtil.getUser().id

    inner class LocalCommentViewHolder(private val binding: ItemLocalCommentListBinding) : RecyclerView.ViewHolder(binding.root) {

        val moreBtn = binding.localCommentItemIvMoreBtn
        val addReply = binding.localCommentItemTvAddReply

        fun bindInfo(comment: Comment) {

            for (user in userList) {    // 작성자 nickname, profileImg 세팅
                if(comment.userId == user.id) {
                    binding.writer = user
                }
            }

            moreBtn.isVisible = comment.userId == ApplicationClass.sharedPreferencesUtil.getUser().id

            binding.comment = comment
            binding.executePendingBindings()

            // 대댓글 rv adapter 추가하기
            val replyList = mutableListOf<Comment>()
            for (reply in commentAllList) {
                if(reply.parent == comment.id) {
                    replyList.add(reply)
                }
            }
            Log.d(TAG, "bindInfo: $replyList")

            commentReplyAdapter = LocalReplyAdapter(context)
//                commentNestedAdapter.submitList(list)
            commentReplyAdapter.commentList = replyList
            commentReplyAdapter.userList = userList
            binding.localCommentItemRvReply.apply{
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
                adapter = commentReplyAdapter
                adapter!!.stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }

            commentReplyAdapter.setModifyItemClickListener(object : LocalReplyAdapter.MenuClickListener {

                override fun onClick(commentId: Int, postId: Int, position: Int) {
                    // 대댓글 수정
                    val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_update_reply,null)
                    if(dialogView.parent!=null){
                        (dialogView.parent as ViewGroup).removeAllViews()
                    }
                    dialog = Dialog(context)
                    dialog.setContentView(dialogView)
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                    dialogView.findViewById<TextView>(R.id.updateReplyDialog_tvContent).text = replyList[position].comment

                    dialog.show()

                    dialogView.findViewById<Button>(R.id.updateReplyDialog_btnCancel).setOnClickListener {
                        dialog.dismiss()
                    }

                    dialogView.findViewById<AppCompatButton>(R.id.updateReplyDialog_btnOk).setOnClickListener {
                        updateReply(commentId, dialogView.findViewById<TextView>(R.id.updateReplyDialog_tvContent).text.toString(), postId)
                    }

                }
            })

            commentReplyAdapter.setDeleteItemClickListener(object : LocalReplyAdapter.MenuClickListener {

                override fun onClick(commentId: Int, postId: Int, position: Int) {
                    deleteReply(commentId, postId, position)
                }
            })

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalCommentAdapter.LocalCommentViewHolder {
        return LocalCommentViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(
                    parent.context
                ), R.layout.item_local_comment_list, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: LocalCommentViewHolder, position: Int) {
        val comment = commentList[position]

        holder.apply {
            bindInfo(comment)
//            setIsRecyclable(false)

            addReply.setOnClickListener {
                addReplyClickListener.onClick(it as TextView, position, comment.id)
            }

            moreBtn.setOnClickListener {
                val popup = PopupMenu(context, moreBtn)
                MenuInflater(context).inflate(R.menu.popup_menu, popup.menu)

                popup.show()
                popup.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.modify -> {
                            modifyItemClickListener.onClick(comment.id, position)
                            return@setOnMenuItemClickListener true
                        }
                        R.id.delete -> {
                            deleteItemClickListener.onClick(comment.id, position)
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
        fun onClick(view: TextView, position: Int, commentId: Int)
    }

    private lateinit var addReplyClickListener : ItemClickListener

    fun setAddReplyItemClickListener(itemClickListener: ItemClickListener) {
        this.addReplyClickListener = itemClickListener
    }


    interface MenuClickListener {
        fun onClick(commentId: Int, position: Int)
    }

    private lateinit var modifyItemClickListener : MenuClickListener

    fun setModifyItemClickListener(menuClickListener: MenuClickListener) {
        this.modifyItemClickListener = menuClickListener
    }

    private lateinit var deleteItemClickListener : MenuClickListener

    fun setDeleteItemClickListener(menuClickListener: MenuClickListener) {
        this.deleteItemClickListener = menuClickListener
    }


    /**
     * 대댓글 삭제 response
     */
    private fun deleteReply(commentId: Int, postId: Int, position: Int) {
        var response: Response<Message>
        runBlocking {
            response = BoardService().deleteComment(commentId)
        }
        if (response.code() == 200 || response.code() == 500) {
            val res = response.body()
            if (res != null) {
                if (res.success == true && res.data["isSuccess"] == true) {
                    Toast.makeText(context, "대댓글이 삭제되었습니다.", Toast.LENGTH_SHORT).show()

                    runBlocking {
                        mainViewModel.getCommentList(postId)
                    }
//                    localCommentAdapter.notifyItemRemoved(position)
//                    commentReplyAdapter.notifyDataSetChanged()
                    commentReplyAdapter.notifyItemRemoved(position)
                    notifyDataSetChanged()
                } else {
                    Toast.makeText(context, "대댓글이 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "deleteReply: ${res.message}", )
                }
            }
        }
    }

    /**
     * 대댓글 수정 response
     */
    private fun updateReply(commentId: Int, content: String, postId: Int) {

        if(content.isNotEmpty() && commentId > 0) {

            val updateComment = Comment(commentId, content)

            var response: Response<Message>

            runBlocking {
                response = BoardService().updateComment(updateComment)
            }

            if (response.code() == 200 || response.code() == 500) {
                val res = response.body()
                if (res != null) {
                    if (res.success == true && res.data["isSuccess"] == true) {
                        Toast.makeText(context, "대댓글이 수정되었습니다.", Toast.LENGTH_SHORT).show()

                        runBlocking {
                            mainViewModel.getCommentList(postId)
                        }
                        dialog.dismiss()
                        commentReplyAdapter.notifyDataSetChanged()
                        notifyDataSetChanged()
                    } else {
                        Toast.makeText(context, "대댓글이 수정 실패", Toast.LENGTH_SHORT).show()

                        Log.e(TAG, "updateComment: ${res.message}",)
                    }
                }
            }
        }
    }
}