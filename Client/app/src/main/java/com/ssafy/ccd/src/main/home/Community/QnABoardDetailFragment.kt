package com.ssafy.ccd.src.main.home.Community

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.ssafy.ccd.R
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentQnABoardDetailBinding
import com.ssafy.ccd.src.dto.Board
import com.ssafy.ccd.src.dto.LikeRequestDto
import com.ssafy.ccd.src.dto.Message
import com.ssafy.ccd.src.main.MainActivity
import com.ssafy.ccd.src.network.service.BoardService
import com.ssafy.ccd.util.CommonUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Response
import java.text.SimpleDateFormat

/**
 * @author Jueun
 * @since 03/24/22
 * '궁금해' 상세페이지
 */
class QnABoardDetailFragment : BaseFragment<FragmentQnABoardDetailBinding>(FragmentQnABoardDetailBinding::bind, R.layout.fragment_qn_a_board_detail) {
    private val TAG = "QnABoardDetailFragment_ccd"

    // binding
    private lateinit var tvTitle : TextView
    private lateinit var tvContent : TextView
    private lateinit var tvUserName : TextView
    private lateinit var tvDate : TextView
    private lateinit var tvAnswerCnt : TextView
    private lateinit var clLikeBtn : ConstraintLayout
    private lateinit var clAnswerBtn : ConstraintLayout
    private lateinit var rvAnswer : RecyclerView
    private lateinit var tvLikeCnt : TextView

    private lateinit var commentAdapter: CommentAdapter

    private lateinit var dialogBuilder : AlertDialog.Builder
    private lateinit var dlg : AlertDialog

    private lateinit var mainActivity : MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.hideBottomNavi(true)

        setInstance()
        setListner()
        setView()
        setData()
    }

    /**
     * 게시글(질문) 삭제
     */
    private suspend fun deleteQuestion(){
        var response : Response<Message>

        runBlocking {
            response = BoardService().deletePost(mainViewModel.boardQna.id)
        }

        if(response.code() == 200 || response.code() == 500) {
            val res = response.body()
            if(res != null) {
                if(res.success && res.data["isSuccess"] == true) {
                    this@QnABoardDetailFragment.findNavController().popBackStack()
                } else if(res.data["isSuccess"] == false) {
                    Log.d(TAG, "deleteQuestion: ${res.message}")
                }
            }
        }
    }

    /**
     * 초기 데이터 설정
     */
    private fun setData() {
        runBlocking {
            mainViewModel.getCommentsByPostId(mainViewModel.boardQna.id)
        }

        mainViewModel.comments.observe(viewLifecycleOwner) {
            if (it != null) {
                CoroutineScope(Dispatchers.Main).launch {
                    tvAnswerCnt.text = it.size.toString()
                }
                Log.d(TAG, "setData: ${it::class}")
                commentAdapter.updateData(it)
            }
        }
    }

    /**
     * 초기 리스너 설정
     */
    private fun setListner() {
        clAnswerBtn.setOnClickListener {
            mainViewModel.writeType = 1
            this@QnABoardDetailFragment.findNavController().navigate(R.id.writeQnaFragment)
        }
        
        clLikeBtn.setOnClickListener {
            insertLike()
        }

        binding.fragmentQnaBoardDetailTvDelete.setOnClickListener {
            dlg.show()
        }

        binding.fragmentQnaBoardDetailTvRewrite.setOnClickListener {
            mainViewModel.writeType = 2
            this@QnABoardDetailFragment.findNavController().navigate(R.id.writeQnaFragment)
        }

        binding.fragmentQnaBoardDetailIbBack.setOnClickListener {
            this@QnABoardDetailFragment.findNavController().popBackStack()
        }
    }

    /**
     * 공감해요(좋아요)를 누르는 것
     */
    private fun insertLike() {
        var response : Response<Message>

        runBlocking {
            response = BoardService().insertOrDeletePostLike(LikeRequestDto(mainViewModel.boardQna.id, ApplicationClass.sharedPreferencesUtil.getUser().id))
        }

        if(response.code() == 200 || response.code() == 500) {
            val res = response.body()
            if(res != null) {
                if(res.success && res.data["isSuccess"] == true) {
                    reLoadData()
                } else if(res.data["isSuccess"] == false) {
                    Log.d(TAG, "deleteQuestion: ${res.message}")
                }
            }
        }
    }

    /**
     * 해당 아이디에 맞는 게시판 데이터를 다시 로드한다
     */
    private fun reLoadData() {
        var response : Response<Message>

        runBlocking {
            response = BoardService().selectPostDetail(mainViewModel.boardQna.id)
        }

        if(response.code() == 200 || response.code() == 500) {
            val res = response.body()
            Log.d(TAG, "reLoadData: $res")
            if(res != null) {
                if(res.success) {
                    val type = object : TypeToken<Board>() {}.type
                    val post: Board = CommonUtils.parseDto(res.data["board"]!!, type)
                    Log.d(TAG, "reLoadData: $post")
                    mainViewModel.boardQna = post
                    tvLikeCnt.text = post.count.toString()
                } else if(res.data["isSuccess"] == false) {
                    Log.d(TAG, "deleteQuestion: ${res.message}")
                }
            }
        }
    }

    /**
     * 초기 뷰 설정
     */
    private fun setView() {
        binding.post = mainViewModel.boardQna
        binding.writer = ApplicationClass.sharedPreferencesUtil.getUser()
        tvDate.text = millToDate(mainViewModel.boardQna.time.toLong())
        rvAnswer.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = commentAdapter
        }
        tvLikeCnt.text = mainViewModel.boardQna.count.toString()
    }

    /**
     * 시간 변환 함수
     */
    private fun millToDate(mills: Long): String {
        val pattern = "yyyy.MM.dd."
        val formatter = SimpleDateFormat(pattern)
        return formatter.format(java.sql.Timestamp(mills))
    }

    /**
     * 초기 객체 생성
     */
    private fun setInstance() {
        // binding
        tvAnswerCnt = binding.fragmentQnaBoardDetailTvAnswerCnt
        tvContent = binding.fragmentQnaBoardDetailTvContent
        tvDate = binding.fragmentQnaBoardDetailTvDate
        tvTitle = binding.fragmentQnaBoardDetailTvTitle
        tvUserName = binding.fragmentQnaBoardDetailTvUserName
        clAnswerBtn = binding.fragmentQnaBoardDetailClAnswerBtn
        clLikeBtn = binding.fragmentQnaBoardDetailClLikeBtn
        rvAnswer = binding.fragmentQnaBoardDetailRvAnswer
        tvLikeCnt = binding.fragmentQnaBoardDetailTvLikeCnt

        val userId = ApplicationClass.sharedPreferencesUtil.getUser().id
        commentAdapter = CommentAdapter(mutableListOf(), requireContext(), this, userId)


        // dialog
        dialogBuilder = AlertDialog.Builder(requireContext())
            .setTitle("질문 삭제")
            .setMessage("정말로 질문을 삭제하시겠습니까?")
            .setPositiveButton("네") { _, _ -> CoroutineScope(Dispatchers.Main).launch { deleteQuestion() } }
            .setNegativeButton("아니요") { _, _ -> dlg.hide() }

        dlg = dialogBuilder.create()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainActivity.hideBottomNavi(false)
    }
}