package com.ssafy.ccd.src.main.home.Community

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.ccd.R
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentLocalCommentBinding
import com.ssafy.ccd.src.dto.Board
import com.ssafy.ccd.src.dto.Message
import com.ssafy.ccd.src.main.MainActivity
import com.ssafy.ccd.src.network.service.BoardService
import kotlinx.coroutines.runBlocking
import retrofit2.Response
import kotlin.properties.Delegates

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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        runBlocking {
            mainViewModel.getCommentList(postId)
        }
        initDataBinding()
        initCommentRv()
    }

    private fun initDataBinding() {
        binding.mainViewModel = mainViewModel
        binding.loginUser = mainViewModel.loginUserInfo.value
    }

    /**
     * 댓글 recyclerView 초기화
     */
    private fun initCommentRv() {
        val userId = ApplicationClass.sharedPreferencesUtil.getUser().id
        binding.localCmtFragmentRvComment.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        localCommentAdapter = LocalCommentAdapter(requireContext())

        mainViewModel.commentList.observe(viewLifecycleOwner, {
            localCommentAdapter.commentList = it
            localCommentAdapter.userList = mainViewModel.allUserList.value!!
        })

        localCommentAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.localCmtFragmentRvComment.adapter = localCommentAdapter

        localCommentAdapter.setAddReplyItemClickListener(object : LocalCommentAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int, commentId: Int) {
                // 대댓글 작성
                // 클릭한 댓글 뷰 색상 바꿔주고
                // 하단 댓글 작성 부분 focus
            }
        })


        localCommentAdapter.setModifyItemClickListener(object : LocalCommentAdapter.MenuClickListener {
            override fun onClick(commentId: Int, position: Int) {
                // 댓글 수정
            }
        })

        localCommentAdapter.setDeleteItemClickListener(object : LocalCommentAdapter.MenuClickListener {
            override fun onClick(commentId: Int, position: Int) {
                deleteComment(commentId, position)
            }
        })

    }



    override fun onDestroy() {
        super.onDestroy()
        mainActivity.hideBottomNavi(false)
    }
}