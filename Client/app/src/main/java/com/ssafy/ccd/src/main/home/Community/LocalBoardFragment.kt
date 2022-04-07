package com.ssafy.ccd.src.main.home.Community

    import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.ssafy.ccd.R
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentLocalBoardBinding
    import com.ssafy.ccd.src.dto.Board
    import com.ssafy.ccd.src.dto.LikeRequestDto
import com.ssafy.ccd.src.dto.Message
import com.ssafy.ccd.src.main.MainActivity
import com.ssafy.ccd.src.network.service.BoardService
import kotlinx.coroutines.runBlocking
import retrofit2.Response
import kotlin.properties.Delegates

/**
 * @author Jiwoo
 * @since 03/23/22
 * '울동네' 게시판
 */
class LocalBoardFragment : BaseFragment<FragmentLocalBoardBinding>(FragmentLocalBoardBinding::bind,R.layout.fragment_local_board) {
    private val TAG = "LocalBoardFragment_ccd"
    private lateinit var localBoardAdapter: LocalBoardAdapter
    private lateinit var mainActivity : MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        mainActivity.hideBottomNavi(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.hideBottomNavi(true)

        runBlocking {
//            mainViewModel.getPostListByType(1)

            mainViewModel.getLocPostListByUserLoc(mainViewModel.userLoc!!)
            mainViewModel.getLikePostsByUserId(ApplicationClass.sharedPreferencesUtil.getUser().id)
        }

        binding.mainViewModel = mainViewModel

        setUserAddr()

        initRecyclerView()
        backBtnClickEvent()
        writeBtnClickEvent()
        searchBtnClickEvent()
    }

    /**
     * 사용자 위치 정보 세팅
     */
    private fun setUserAddr() {
        val addrAll = mainActivity.getAddress(mainViewModel.userLoc!!)
        val addr = addrAll.split(" ")

        binding.localBoardFragmentTvUserLoc.text = addr[3]
    }

    /**
     * 검색버튼 클릭이벤트 ( 제목으로 찾기 )
     */
    private fun searchBtnClickEvent(){
        binding.fragmentLocalSearch.setOnClickListener {
            this@LocalBoardFragment.findNavController().navigate(R.id.action_localBoardFragment_to_searchFragment)
        }
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
     * 글작성 버튼 클릭 이벤트
     */
    private fun writeBtnClickEvent() {
        binding.fragmentLocalWrite.setOnClickListener {
            this@LocalBoardFragment.findNavController().navigate(R.id.action_localBoardFragment_to_writeLocalBoardFragment,
                    bundleOf("postId" to -1))
        }
    }

    /**
     * 게시글 recyclerView 초기화 + rv 아이템 클릭 이벤트
     */
    private fun initRecyclerView() {
        val userId = ApplicationClass.sharedPreferencesUtil.getUser().id

        binding.localBoardFragmentRvPostList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        localBoardAdapter = LocalBoardAdapter(requireContext())

        mainViewModel.likePostsByUserId.observe(viewLifecycleOwner) {
            localBoardAdapter.userLikePost = it
//            localBoardAdapter.notifyDataSetChanged()
        }

//        localBoardAdapter.submitList(mainViewModel.locPostList.value)
//        localBoardAdapter.userList = mainViewModel.allUserList.value!!

        mainViewModel.locPostList.observe(viewLifecycleOwner) {
            if(mainViewModel.boardId > 0){
                Log.d(TAG, "initRecyclerView: ${mainViewModel.boardId}")
                var array = mutableListOf<Board>()

                for( i in 0..it.size-1){
                    if(it[i].id == mainViewModel.boardId){
                        array.add(it[i])
                    }
                }
                localBoardAdapter.postList = array
            }else{
                localBoardAdapter.postList = it
            }

            localBoardAdapter.userList = mainViewModel.allUserList.value!!
        }

        binding.localBoardFragmentRvPostList.adapter = localBoardAdapter
        localBoardAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY


        // item
        localBoardAdapter.setHeartItemClickListener(object : LocalBoardAdapter.HeartItemClickListener {
            override fun onClick(heart: LottieAnimationView, position: Int, id: Int) {
//            override fun onClick(heartBtn: LottieAnimationView, heartCnt: TextView, id: Int) {
                // boardlike 호출 -> 색 변경
                val likeRequestDto = LikeRequestDto(boardId = id, userId = userId)
                likePost(heart, likeRequestDto, position)
            }
        })

        localBoardAdapter.setCommentItemClickListener(object : LocalBoardAdapter.ItemClickListener {
            override fun onClick(view: View, postId: Int) {
                // postId 포함해서 commentList 페이지로 이동
                this@LocalBoardFragment.findNavController().navigate(R.id.action_localBoardFragment_to_localCommentFragment,
                    bundleOf("postId" to postId)
                )
            }
        })


        localBoardAdapter.setModifyItemClickListener(object : LocalBoardAdapter.MenuClickListener {
            override fun onClick(postId: Int, position: Int) {
                this@LocalBoardFragment.findNavController().navigate(R.id.action_localBoardFragment_to_writeLocalBoardFragment,
                    bundleOf("postId" to postId)
                )
            }
        })

        localBoardAdapter.setDeleteItemClickListener(object : LocalBoardAdapter.MenuClickListener {
            override fun onClick(postId: Int, position: Int) {
                deletePost(postId, position)
            }
        })
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
                if(res.success && res.data["isSuccess"] == true) {
                    showCustomToast("게시글이 삭제되었습니다.")
                    runBlocking {
                        mainViewModel.getLocPostListByUserLoc(mainViewModel.userLoc!!)
                    }
//                    localBoardAdapter.notifyItemRemoved(position)
                    localBoardAdapter.notifyDataSetChanged()
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
                            mainViewModel.getLocPostListByUserLoc(mainViewModel.userLoc!!)
                        }
                        localBoardAdapter.notifyItemChanged(position)


                    } else if(res.data["isSuccess"] == true && res.message == "삭제") {
                        val animator = ValueAnimator.ofFloat(1f,0f).setDuration(500)
                        animator.addUpdateListener { animation ->
                            heart.progress = animation.animatedValue as Float
                        }
                        animator.start()
                        runBlocking {
                            mainViewModel.getLikePostsByUserId(likeRequestDto.userId)
                            mainViewModel.getLocPostListByUserLoc(mainViewModel.userLoc!!)
                        }
                        localBoardAdapter.notifyItemChanged(position)
                    }
                } else {
                    showCustomToast("서버 통신 오류 발생")
                    Log.e(TAG, "likePost: ${res.message}", )
                }
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainActivity.hideBottomNavi(false)
    }

}