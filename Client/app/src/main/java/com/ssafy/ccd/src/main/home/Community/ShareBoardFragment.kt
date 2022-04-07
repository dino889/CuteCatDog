package com.ssafy.ccd.src.main.home.Community

import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemAnimator
import com.airbnb.lottie.LottieAnimationView
import com.ssafy.ccd.R
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentLocalBoardBinding
import com.ssafy.ccd.databinding.FragmentShareBoardBinding
import com.ssafy.ccd.src.dto.LikeRequestDto
import com.ssafy.ccd.src.dto.Message
import com.ssafy.ccd.src.main.MainActivity
import com.ssafy.ccd.src.main.home.BoardAdapter
import com.ssafy.ccd.src.network.service.BoardService
import kotlinx.coroutines.runBlocking
import retrofit2.Response
import androidx.recyclerview.widget.SimpleItemAnimator
import com.ssafy.ccd.src.dto.Board
import com.ssafy.ccd.src.dto.User


/**
 * @author Jiwoo
 * @since 03/23/22
 * '공유해' 게시판
 */
class ShareBoardFragment : BaseFragment<FragmentShareBoardBinding>(FragmentShareBoardBinding::bind,R.layout.fragment_share_board) {
    private val TAG = "ShareBoardFragment_ccd"
    private lateinit var shareBoardAdapter: ShareBoardAdapter
    private lateinit var mainActivity : MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onResume() {
        super.onResume()
        mainActivity.hideBottomNavi(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.hideBottomNavi(true)

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
//            if(mainViewModel.boardId > 0){
//                Log.d(TAG, "initRecyclerView: ")
//                mainViewModel.getUserInfo(mainViewModel.userId,false)
//            }
        }

        binding.shareBoardFragmentRvPostList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        shareBoardAdapter = ShareBoardAdapter(requireContext())

        mainViewModel.likePostsByUserId.observe(viewLifecycleOwner, {
            shareBoardAdapter.userLikePost = it
        })

        mainViewModel.sharePostList.observe(viewLifecycleOwner, {
            if(mainViewModel.boardId > 0){
                Log.d(TAG, "initRecyclerView: ${mainViewModel.boardId}")
                var array = mutableListOf<Board>()

                for( i in 0..it.size-1){
                    if(it[i].id == mainViewModel.boardId){
                        Log.d(TAG, "initRecyclerView: ${it[i].id}")
                        array.add(it[i])
                    }
                }
                Log.d(TAG, "initRecyclerView: ${array}")
                shareBoardAdapter.postList = array
//                shareBoardAdapter.userList = users
            }else{
                shareBoardAdapter.postList = it
            }
            shareBoardAdapter.userList = mainViewModel.allUserList.value!!
        })

        val animator = binding.shareBoardFragmentRvPostList.itemAnimator     //리사이클러뷰 애니메이터 get
        if (animator is SimpleItemAnimator){          //아이템 애니메이커 기본 하위클래스
            animator.supportsChangeAnimations = false  //애니메이션 값 false (리사이클러뷰가 화면을 다시 갱신 했을때 뷰들의 깜빡임 방지)
        }
        searchBtnClickEvent()
        binding.shareBoardFragmentRvPostList.adapter = shareBoardAdapter
        shareBoardAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        // item - heart 클릭 이벤트
        shareBoardAdapter.setHeartItemClickListener(object : ShareBoardAdapter.HeartItemClickListener {
            override fun onClick(heart: LottieAnimationView, position: Int, id: Int) {
//            override fun onClick(heartBtn: LottieAnimationView, heartCnt: TextView, id: Int) {
                // boardlike 호출 -> 색 변경
                val likeRequestDto = LikeRequestDto(boardId = id, userId = userId)
                likePost(heart, likeRequestDto, position)
            }
        })

        // comment 클릭 이벤트
        shareBoardAdapter.setCommentItemClickListener(object : ShareBoardAdapter.ItemClickListener {
            override fun onClick(view: View, postId: Int) {
                // postId 포함해서 commentList 페이지로 이동
                this@ShareBoardFragment.findNavController().navigate(R.id.action_shareBoardFragment_to_localCommentFragment,
                    bundleOf("postId" to postId)
                )
            }
        })

        // '더보기' 클릭 이벤트
        shareBoardAdapter.setDetailItemClickListener(object : ShareBoardAdapter.DetailItemClickListener {
            override fun onClick(view: View, all: TextView, split: TextView, postId: Int) {
                val detail = view as TextView
                if(detail.text == "더 보기") {
                    all.visibility = View.VISIBLE
                    split.visibility = View.GONE
                    detail.text = "숨기기"
                } else if(detail.text == "숨기기") {
                    all.visibility = View.GONE
                    split.visibility = View.VISIBLE
                    detail.text = "더 보기"
                }
            }
        })

        // more - 수정 버튼 클릭 이벤트
        shareBoardAdapter.setModifyItemClickListener(object : ShareBoardAdapter.MenuClickListener {
            override fun onClick(postId: Int, position: Int) {
                this@ShareBoardFragment.findNavController().navigate(R.id.action_shareBoardFragment_to_writeShareBoardFragment,
                    bundleOf("postId" to postId)
                )
            }
        })

        // more - 삭제 버튼 클릭 이벤트
        shareBoardAdapter.setDeleteItemClickListener(object : ShareBoardAdapter.MenuClickListener {
            override fun onClick(postId: Int, position: Int) {
                deletePost(postId, position)
            }
        })


    }
    private fun searchBtnClickEvent(){
        binding.shareFragmentIbSearch.setOnClickListener {
            this@ShareBoardFragment.findNavController().navigate(R.id.action_shareBoardFragment_to_searchFragment)
        }
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

    /**
     * 게시글 좋아요 response
     */
    private fun likePost(heart: LottieAnimationView, likeRequestDto: LikeRequestDto, position: Int) {

        var response : Response<Message>
        runBlocking {
            response = BoardService().insertOrDeletePostLike(likeRequestDto)
        }
        if(response.code() == 200 || response.code() == 500) {
            val res = response.body()
            if (res != null) {
                if(res.success) {
                    if(res.data["isSuccess"] == true && res.message == "등록") {
                        val animator = ValueAnimator.ofFloat(0f,0.4f).setDuration(500)
                        animator.addUpdateListener { animation ->
                            heart.progress = animation.animatedValue as Float
                        }
                        animator.start()
                        runBlocking {
                            mainViewModel.getLikePostsByUserId(likeRequestDto.userId)
                            mainViewModel.getPostListByType(3)
                        }
                        shareBoardAdapter.notifyItemChanged(position)
//                        shareBoardAdapter.notifyDataSetChanged()


                    } else if(res.data["isSuccess"] == true && res.message == "삭제") {
                        val animator = ValueAnimator.ofFloat(1f,0f).setDuration(500)
                        animator.addUpdateListener { animation ->
                            heart.progress = animation.animatedValue as Float
                        }
                        animator.start()
                        runBlocking {
                            mainViewModel.getLikePostsByUserId(likeRequestDto.userId)
                            mainViewModel.getPostListByType(3)
                        }
                        shareBoardAdapter.notifyItemChanged(position)
//                        shareBoardAdapter.notifyDataSetChanged()

                    }
                } else {
                    showCustomToast("서버 통신 오류 발생")
                    Log.e(TAG, "likePost: ${res.message}")
                }
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainActivity.hideBottomNavi(false)
    }


}