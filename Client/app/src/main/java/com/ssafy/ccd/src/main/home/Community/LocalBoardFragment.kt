package com.ssafy.ccd.src.main.home.Community

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.ccd.R
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentLocalBoardBinding
import com.ssafy.ccd.src.dto.Message
import com.ssafy.ccd.src.main.home.BoardAdapter
import com.ssafy.ccd.src.network.service.BoardService
import kotlinx.coroutines.runBlocking
import retrofit2.Response

/**
 * @author Jiwoo
 * @since 03/23/22
 * '울동네' 게시판
 */
class LocalBoardFragment : BaseFragment<FragmentLocalBoardBinding>(FragmentLocalBoardBinding::bind,R.layout.fragment_local_board) {
    private lateinit var localBoardAdapter: LocalBoardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        backBtnClickEvent()
        writeBtnClickEvent()
    }

    /**
     * 뒤로가기 버튼 클릭 이벤트
     */
    private fun backBtnClickEvent() {
        binding.fragmentLocalBack.setOnClickListener {
            this@LocalBoardFragment.findNavController().popBackStack()
        }
    }

    /**
     * 글작성 완료 버튼 클릭 이벤트
     */
    private fun writeBtnClickEvent() {
        binding.fragmentLocalWrite.setOnClickListener {
            this@LocalBoardFragment.findNavController().navigate(R.id.action_localBoardFragment_to_writeLocalBoardFragment)
        }
    }

    /**
     * 게시글 recyclerView 초기화
     */
    private fun initRecyclerView() {
        runBlocking {
            mainViewModel.getPostListByType(1)
        }
        mainViewModel.locPostList.observe(viewLifecycleOwner, {
            binding.localBoardFragmentRvPostList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            localBoardAdapter = LocalBoardAdapter(it, mainViewModel.allUserList.value!!, mainViewModel.likePostsByUserId.value!!, requireContext())
            binding.localBoardFragmentRvPostList.adapter = localBoardAdapter

            localBoardAdapter.setHeartItemClickListener(object : LocalBoardAdapter.ItemClickListener {
                override fun onClick(view: View, position: Int) {
                    // boardlike 호출 -> 색 변경
                }
            })

            localBoardAdapter.setCommentItemClickListener(object : LocalBoardAdapter.ItemClickListener {
                override fun onClick(view: View, position: Int) {
                    // postId 포함해서 commentList 페이지로 이동
                }
            })


            localBoardAdapter.setModifyItemClickListener(object : LocalBoardAdapter.MenuClickListener {
                override fun onClick(postId: Int) {
                    this@LocalBoardFragment.findNavController().navigate(R.id.action_localBoardFragment_to_writeLocalBoardFragment,
                        bundleOf("postId" to postId)
                    )
                }
            })

            localBoardAdapter.setDeleteItemClickListener(object : LocalBoardAdapter.MenuClickListener {
                override fun onClick(postId: Int) {
                    deletePost(postId)
                }
            })

        })
    }

    private fun deletePost(postId: Int) {
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
                        mainViewModel.getPostListByType(1)
                    }
                } else {
                    showCustomToast("게시글 삭제 실패")
                }
            }
        }
    }



}