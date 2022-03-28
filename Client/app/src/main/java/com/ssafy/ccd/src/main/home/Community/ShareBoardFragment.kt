package com.ssafy.ccd.src.main.home.Community

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.ssafy.ccd.R
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentLocalBoardBinding
import com.ssafy.ccd.databinding.FragmentShareBoardBinding
import com.ssafy.ccd.src.dto.LikeRequestDto
import com.ssafy.ccd.src.dto.Message
import com.ssafy.ccd.src.main.home.BoardAdapter
import com.ssafy.ccd.src.network.service.BoardService
import kotlinx.coroutines.runBlocking
import retrofit2.Response

/**
 * @author Jiwoo
 * @since 03/23/22
 * '공유해' 게시판
 */
class ShareBoardFragment : BaseFragment<FragmentShareBoardBinding>(FragmentShareBoardBinding::bind,R.layout.fragment_share_board) {
    private lateinit var shareBoardAdapter: ShareBoardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mainViewModel = mainViewModel

        initRecyclerView()
        backBtnClickEvent()
        writeBtnClickEvent()
    }

    private fun initRecyclerView() {
        val userId = ApplicationClass.sharedPreferencesUtil.getUser().id

        runBlocking {
            mainViewModel.getPostListByType(3)
            mainViewModel.getLikePostsByUserId(userId)
        }

        binding.shareBoardFragmentRvPostList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        shareBoardAdapter = ShareBoardAdapter(requireContext())

        mainViewModel.likePostsByUserId.observe(viewLifecycleOwner, {
            shareBoardAdapter.userLikePost = it
        })

        mainViewModel.sharePostList.observe(viewLifecycleOwner, {

            shareBoardAdapter.postList = it
            shareBoardAdapter.userList = mainViewModel.allUserList.value!!
        })

        binding.shareBoardFragmentRvPostList.adapter = shareBoardAdapter
        shareBoardAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        // item
        shareBoardAdapter.setHeartItemClickListener(object : ShareBoardAdapter.HeartItemClickListener {
            override fun onClick(heart: LottieAnimationView, position: Int, id: Int) {
//            override fun onClick(heartBtn: LottieAnimationView, heartCnt: TextView, id: Int) {
                // boardlike 호출 -> 색 변경
                val likeRequestDto = LikeRequestDto(boardId = id, userId = userId)
//                likePost(heart, likeRequestDto, position)
            }
        })

        shareBoardAdapter.setCommentItemClickListener(object : ShareBoardAdapter.ItemClickListener {
            override fun onClick(view: View, postId: Int) {
                // postId 포함해서 commentList 페이지로 이동
                this@ShareBoardFragment.findNavController().navigate(R.id.action_localBoardFragment_to_localCommentFragment,
                    bundleOf("postId" to postId)
                )
            }
        })


        shareBoardAdapter.setModifyItemClickListener(object : ShareBoardAdapter.MenuClickListener {
            override fun onClick(postId: Int, position: Int) {
                this@ShareBoardFragment.findNavController().navigate(R.id.action_shareBoardFragment_to_writeShareBoardFragment,
                    bundleOf("postId" to postId)
                )
            }
        })

        shareBoardAdapter.setDeleteItemClickListener(object : ShareBoardAdapter.MenuClickListener {
            override fun onClick(postId: Int, position: Int) {
                deletePost(postId, position)
            }
        })
    }

    /**
     * 뒤로가기 버튼 클릭 이벤트
     */
    private fun backBtnClickEvent() {
        binding.shareFragmentIbBack.setOnClickListener {
            this@ShareBoardFragment.findNavController().popBackStack()
        }
    }

    /**
     * 게시글 작성 클릭 이벤트
     */
    private fun writeBtnClickEvent() {
        binding.shareFragmentIbWrite.setOnClickListener {
            this@ShareBoardFragment.findNavController().navigate(R.id.action_shareBoardFragment_to_writeShareBoardFragment,
                bundleOf("postId" to -1))
        }
    }

    /**
     * 게시글 삭제 response
     */
    private fun deletePost(postId: Int, position: Int) {
        var response : Response<Message>
        runBlocking {
            response = BoardService().deletePost(postId)
        }
        if(response.code() == 200 || response.code() == 500) {
            val res = response.body()
            if(res != null) {
                if(res.success == true && res.data["isSuccess"] == true) {
                    showCustomToast("게시글이 삭제되었습니다.")
                    runBlocking {
                        mainViewModel.getPostListByType(3)
                    }
                    shareBoardAdapter.notifyDataSetChanged()
                } else {
                    showCustomToast("게시글 삭제 실패")
                }
            }
        }
    }


}