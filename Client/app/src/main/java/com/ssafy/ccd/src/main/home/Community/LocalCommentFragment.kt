package com.ssafy.ccd.src.main.home.Community

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.renderscript.ScriptGroup
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.ccd.R
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentLocalCommentBinding
import com.ssafy.ccd.src.dto.Board
import com.ssafy.ccd.src.dto.Comment
import com.ssafy.ccd.src.dto.Message
import com.ssafy.ccd.src.main.MainActivity
import com.ssafy.ccd.src.network.service.BoardService
import kotlinx.coroutines.runBlocking
import retrofit2.Response
import kotlin.properties.Delegates
import android.widget.EditText

import android.view.MotionEvent
import android.view.View.OnTouchListener
import androidx.core.content.ContextCompat.getSystemService
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.LiveData
import com.google.android.youtube.player.internal.r


/**
 * @author Jiwoo
 * @since 03/24/22
 * '울동네' 게시글 댓글 화면
 */
class LocalCommentFragment : BaseFragment<FragmentLocalCommentBinding>(FragmentLocalCommentBinding::bind, R.layout.fragment_local_comment) {
    private val TAG = "LocalCommentFragment_ccd"
    private lateinit var mainActivity : MainActivity

    private var postId by Delegates.notNull<Int>()

    private lateinit var localCommentAdapter: LocalCommentAdapter
    private lateinit var mInputMethodManager : InputMethodManager

    // 대댓글 작성 시 필요한 parentId == commentId
    private var parentId = -1
    val userId = ApplicationClass.sharedPreferencesUtil.getUser().id

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            postId = getInt("postId")
        }
        mainActivity.hideBottomNavi(true)
        mInputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        mOnGlobalLayoutListener.onGlobalLayout()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED)

        runBlocking {
            mainViewModel.getCommentList(postId)
        }
        initDataBinding()
        backBtnClickEvent()
        initCommentRv()
//        binding.test.setOnTouchListener { v, event ->
//            binding.localCmtFragmentTvWriterNick.visibility = View.GONE
//            binding.localCmtFragmentTvWriterNick.text = ""
//            hideKeyboard()
//            true
//        }
        binding.test.viewTreeObserver.addOnGlobalLayoutListener(mOnGlobalLayoutListener)


    }

    private fun initDataBinding() {
        binding.mainViewModel = mainViewModel
        binding.loginUser = mainViewModel.loginUserInfo.value
    }

    /**
     * 뒤로가기 버튼 클릭 이벤트
     */
    private fun backBtnClickEvent() {
        binding.localCmtFragmentIbBack.setOnClickListener {
            this@LocalCommentFragment.findNavController().popBackStack()
        }
    }

    /**
     * 댓글 recyclerView 초기화
     */
    private fun initCommentRv() {

        binding.localCmtFragmentRvComment.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        localCommentAdapter = LocalCommentAdapter(requireContext())

        mainViewModel.commentListWoParents.observe(viewLifecycleOwner, {

            localCommentAdapter.commentList = it
            localCommentAdapter.commentAllList = mainViewModel.commentAllList.value!!
            localCommentAdapter.userList = mainViewModel.allUserList.value!!
            localCommentAdapter.setAddReplyItemClickListener(object : LocalCommentAdapter.ItemClickListener {
                override fun onClick(view: TextView, position: Int, commentId: Int) {
                    // 대댓글 작성
                    // 클릭한 댓글 뷰 색상 바꿔주고
                    // 하단 댓글 작성 부분 focus
                    // 게시 버튼 눌렀을 때 글자가 하나라도 쓰여져있으면 insert

//                    val mInputMethodManager = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                    mInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
//                    binding.localCmtFragmentEtComment.requestFocus()

                    showKeyboard(binding.localCmtFragmentEtComment)

                    binding.localCmtFragmentTvWriterNick.visibility = View.VISIBLE
                    binding.localCmtFragmentTvWriterNick.text = "@${it[position].nickname}"

                    parentId = it[position].id

                    insertCommentAndReply(true)

                }
            })
        })

        localCommentAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.localCmtFragmentRvComment.adapter = localCommentAdapter



        localCommentAdapter.setModifyItemClickListener(object : LocalCommentAdapter.MenuClickListener {
            override fun onClick(commentId: Int, position: Int) {
                // 댓글 수정
                // 수정 클릭한 댓글 뷰 색상 변경하고,는 안되겠넹
                // 기존 댓글 내용 editText에 세팅

            }
        })

        localCommentAdapter.setDeleteItemClickListener(object : LocalCommentAdapter.MenuClickListener {
            override fun onClick(commentId: Int, position: Int) {
                deleteComment(commentId, position)
            }
        })
    }

    /**
     * 댓글 삭제 response
     */
    private fun deleteComment(commentId: Int, position: Int) {
        Log.d(TAG, "deleteComment: $commentId")
        var response : Response<Message>
        runBlocking {
            response = BoardService().deleteComment(commentId)
        }
        if(response.code() == 200 || response.code() == 500) {
            val res = response.body()
            if(res != null) {
                if(res.success == true && res.data["isSuccess"] == true) {
                    showCustomToast("댓글이 삭제되었습니다.")
                    runBlocking {
                        mainViewModel.getCommentList(postId)
                    }
//                    localCommentAdapter.notifyItemRemoved(position)
                    localCommentAdapter.notifyDataSetChanged()
                } else {
                    showCustomToast("댓글 삭제 실패")
                }
            }
        }
    }

    /**
     * 댓글 및 대댓글 등록
     * parentId가 -1 -> 댓글 등록
     * parentId > 0 -> 대댓글 등록
     */
    private fun insertCommentAndReply(chk : Boolean) {

        binding.localCmtFragmentTvConfirm.setOnClickListener {
            val commentContent = binding.localCmtFragmentEtComment.text.toString()
            if(chk == true && parentId != -1 && commentContent.isNotEmpty()) {   // 대댓글 작성
                val reply = Comment(boardId = postId, comment = commentContent, parent = parentId, userId = userId)

                var response : Response<Message>

                runBlocking {
                    response = BoardService().insertReply(reply)
                }

                if(response.code() == 200 || response.code() == 500) {
                    val res = response.body()
                    if(res != null) {
                        if(res.success == true && res.data["isSuccess"] == true) {
                            showCustomToast("대댓글이 등록되었습니다.")
                            runBlocking {
                                mainViewModel.getCommentList(postId)
                            }
                            localCommentAdapter.notifyDataSetChanged()
                            binding.localCmtFragmentEtComment.setText("")
                        } else {
                            showCustomToast("대댓글 등록 실패")
                        }
                    }
                }
            }
//            else if(parentId == -1 && commentContent.isNotEmpty()) {  // 댓글 등록
//
//            }


        }
    }


    /**
     * 키보드 UP/DOWN 감지 리스너
     */
    var lastHeightDiff = 0
    var isOpenKeyboard = false
    private var mOnGlobalLayoutListener = OnGlobalLayoutListener {
        val activityRootView: View =
            mainActivity.window.decorView.findViewById(android.R.id.content)
        val heightDiff = activityRootView.rootView.height - activityRootView.height
        if (lastHeightDiff == 0) {
            lastHeightDiff = heightDiff
        }
        if (heightDiff > lastHeightDiff) { //keyboard show
            Log.d(TAG, ": kkkkkk")
            isOpenKeyboard = true
        } else { //keyboard hide
            if (isOpenKeyboard) {
                Log.d(TAG, ": ddddd")
                clearFocus(requireActivity())
                binding.localCmtFragmentTvWriterNick.visibility = View.GONE
                binding.localCmtFragmentTvWriterNick.text = ""
                parentId = -1
                isOpenKeyboard = false
            }
        }
    }

    private fun clearFocus(activity: Activity) {
        val v: View = activity.getCurrentFocus() ?: return
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
        v.clearFocus()
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    private fun showKeyboard(editText: EditText?) {
        if (editText == null) {
            return
        }
        editText.requestFocus()
        mInputMethodManager.showSoftInput(
            editText,
            InputMethodManager.SHOW_FORCED or InputMethodManager.HIDE_IMPLICIT_ONLY
        )
    }


    override fun onDestroy() {
        super.onDestroy()
        mainActivity.hideBottomNavi(false)
        mOnGlobalLayoutListener.onGlobalLayout()
    }

}