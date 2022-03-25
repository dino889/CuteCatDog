package com.ssafy.ccd.src.main.home.Community

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.ccd.R
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentQnABoardDetailBinding
import com.ssafy.ccd.src.dto.Message
import com.ssafy.ccd.src.main.MainActivity
import com.ssafy.ccd.src.network.service.BoardService
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

    private lateinit var commentAdapter: CommentAdapter

    private lateinit var dialogBuilder : AlertDialog.Builder
    private lateinit var dlg : AlertDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
            // TODO 공감해요 갯수 변경해서 update
        }

        binding.fragmentQnaBoardDetailTvDelete.setOnClickListener {
            dlg.show()
        }

        binding.fragmentQnaBoardDetailTvRewrite.setOnClickListener {
            mainViewModel.writeType = 2
            this@QnABoardDetailFragment.findNavController().navigate(R.id.writeQnaFragment)
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
}