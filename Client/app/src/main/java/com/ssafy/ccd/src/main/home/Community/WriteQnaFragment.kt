package com.ssafy.ccd.src.main.home.Community

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.jakewharton.rxbinding3.widget.textChanges
import com.ssafy.ccd.R
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentWriteQnaBinding
import com.ssafy.ccd.src.dto.Board
import com.ssafy.ccd.src.dto.Comment
import com.ssafy.ccd.src.dto.Message
import com.ssafy.ccd.src.main.MainActivity
import com.ssafy.ccd.src.network.service.BoardService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.runBlocking
import retrofit2.Response
import java.util.concurrent.TimeUnit

/**
 * @author Jueun
 * @since 03/25/22
 * 궁금해 질문/답변 게시글 작성 화면
 */

const val QUESTION_TYPE = 0
const val ANSWER_TYPE = 1
const val POST_REWRITE_TYPE = 2
const val ANSWER_REWRITE_TYPE = 3

class WriteQnaFragment : BaseFragment<FragmentWriteQnaBinding>(FragmentWriteQnaBinding::bind, R.layout.fragment_write_qna) {
    private val TAG = "WriteQuestion_ccd"
    private lateinit var mainActivity : MainActivity

    // Dispose
    private lateinit var editTextSubscription: Disposable

    //binding
    private lateinit var btnSend : Button
    private lateinit var ibBack : ImageButton
    private lateinit var etTitle : EditText
    private lateinit var tieContent : TextInputEditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setInstance()
        inputObservable()
        setListener()
        setData()
    }

    private fun setData() {
        if(mainViewModel.writeType == POST_REWRITE_TYPE) {
            etTitle.setText(mainViewModel.boardQna.title)
            tieContent.setText(mainViewModel.boardQna.content)
        }else if(mainViewModel.writeType == ANSWER_REWRITE_TYPE){
            tieContent.setText(mainViewModel.commentQna.comment)
        }
    }

    /**
     * 리스너 설정
     */
    private fun setListener() {
        // 뒤로가기
        ibBack.setOnClickListener {
            this@WriteQnaFragment.findNavController().popBackStack()
        }

        // 완료 버튼 누르기
        btnSend.setOnClickListener {
            confirmBtnClickEvent()
        }
    }

    /**
     * 화면 종료 시 dispose
     */
    override fun onDestroy() {
        super.onDestroy()
        mainActivity.hideBottomNavi(false)
        if (!editTextSubscription.isDisposed) {
            editTextSubscription.dispose()
        }
    }

    /**
     * 완료 버튼 클릭 이벤트
     */
    private fun confirmBtnClickEvent() {
        // title (1 ~ 50), content(30 ~ 500 체크), 사진 선택
        val title = etTitle.text.toString()
        val content = tieContent.text.toString()
        val userId = ApplicationClass.sharedPreferencesUtil.getUser().id
        val nickName = ApplicationClass.sharedPreferencesUtil.getUser().nickname

        if(mainViewModel.writeType == QUESTION_TYPE){
            if(titleLenChk(title) && contentLenChk(content)) {
                val post = Board(
                    userId = userId,
                    typeId = 2,
                    author = mainViewModel.loginUserInfo.value!!.nickname,
                    title = title,
                    content = content,
                    time = System.currentTimeMillis().toString(),
                    photoPath = "")
                insertPost(post)
            }
        }else if(mainViewModel.writeType == ANSWER_TYPE){
            if(contentLenChk(content)) {
                val comment = Comment(
                    mainViewModel.boardQna.id,
                    content,
                    userId,
                    nickName
                )
                insertComment(comment)
            }
        }else if(mainViewModel.writeType == POST_REWRITE_TYPE){
            if(titleLenChk(title) && contentLenChk(content)) {
                val board = mainViewModel.boardQna
                board.content = content
                board.title = title
                updatePost(board)
            }
        }else if(mainViewModel.writeType == ANSWER_REWRITE_TYPE){
            if(contentLenChk(content)) {
                val comment = mainViewModel.commentQna
                comment.comment = content
                updateComment(comment)
            }
        }
    }

    /**
     * 게시글 insert
     */
    private fun insertPost(post: Board) {
        var response : Response<Message>

        runBlocking {
            response = BoardService().insertPost(post)
        }

        if(response.code() == 200 || response.code() == 500) {
            val res = response.body()
            if(res != null) {
                if(res.success && res.data["isSuccess"] == true) {
                    showCustomToast("게시글 등록이 완료되었습니다")
                    this@WriteQnaFragment.findNavController().popBackStack()
                } else if(res.data["isSuccess"] == false) {
                    showCustomToast("게시글 등록 실패")
                    Log.e(TAG, "insertPost: ${res.message}", )
                }
            }
        }
    }

    /**
     * 게시글 update
     */
    private fun updatePost(post: Board) {
        var response : Response<Message>

        runBlocking {
            response = BoardService().updatePost(post)
        }

        if(response.code() == 200 || response.code() == 500) {
            val res = response.body()
            if(res != null) {
                if(res.success && res.data["isSuccess"] == true) {
                    showCustomToast("게시글 수정이 완료되었습니다")
                    this@WriteQnaFragment.findNavController().popBackStack()
                } else if(res.data["isSuccess"] == false) {
                    showCustomToast("게시글 수정 실패")
                    Log.e(TAG, "insertPost: ${res.message}", )
                }
            }
        }
    }

    /**
     * 답변 insert
     */
    private fun insertComment(comment: Comment) {
        var response : Response<Message>

        runBlocking {
            response = BoardService().insertComment(comment)
        }

        if(response.code() == 200 || response.code() == 500) {
            val res = response.body()
            if(res != null) {
                if(res.success && res.data["isSuccess"] == true) {
                    showCustomToast("답변 등록이 완료되었습니다")
                    this@WriteQnaFragment.findNavController().popBackStack()
                } else if(res.data["isSuccess"] == false) {
                    showCustomToast("답변 등록 실패")
                    Log.e(TAG, "insertPost: ${res.message}", )
                }
            }
        }
    }

    /**
     * 답변 update
     */
    private fun updateComment(comment: Comment) {
        var response : Response<Message>

        runBlocking {
            response = BoardService().updateComment(comment)
        }

        if(response.code() == 200 || response.code() == 500) {
            val res = response.body()
            if(res != null) {
                if(res.success && res.data["isSuccess"] == true) {
                    showCustomToast("답변 수정이 완료되었습니다")
                    this@WriteQnaFragment.findNavController().popBackStack()
                } else if(res.data["isSuccess"] == false) {
                    showCustomToast("답변 수정 실패")
                    Log.e(TAG, "insertPost: ${res.message}", )
                }
            }
        }
    }

    /**
     * 각 EditText 쿼리 디바운스 적용
     */
    private fun inputObservable() {
        mainActivity.runOnUiThread(kotlinx.coroutines.Runnable {
            tieContent.setQueryDebounce {
                contentLenChk(it)
            }
        })
    }


    /**
     * EditText 쿼리 디바운싱 함수 적용
     */
    private fun EditText.setQueryDebounce(queryFunction: (String) -> Unit): Disposable {
        val editTextChangeObservable = this.textChanges()
        editTextSubscription = editTextChangeObservable
            // 마지막 글자 입력 0.5초 후에 onNext 이벤트로 데이터 발행
            .debounce(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            // 구독을 통해 이벤트 응답 처리
            .subscribeBy(
                onNext = {
                    Log.d(TAG, "onNext : $it")
                    queryFunction(it.toString())
                },
                onComplete = {
                    Log.d(TAG, "onComplete")
                },
                onError = {
                    Log.i(TAG, "onError : $it")
                }
            )
        return editTextSubscription  // Disposable 반환
    }

    private fun setInstance() {
        mainActivity = requireActivity() as MainActivity
        mainActivity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        mainActivity.hideBottomNavi(true)

        // binding
        btnSend = binding.fragmentWriteQuestionBtnSend
        ibBack = binding.fragmentWriteQuestionIbBack
        etTitle = binding.fragmentWriteQuestionEtTitle
        tieContent = binding.fragmentWriteQuestionTieContent

        if(mainViewModel.writeType == ANSWER_TYPE) etTitle.visibility = View.GONE
        else if(mainViewModel.writeType == QUESTION_TYPE) etTitle.visibility = View.VISIBLE
        else if(mainViewModel.writeType == ANSWER_REWRITE_TYPE) etTitle.visibility = View.GONE
        else if(mainViewModel.writeType == POST_REWRITE_TYPE) etTitle.visibility = View.VISIBLE
    }

    /**
     * title 길이 체크
     */
    private fun titleLenChk(input: String) : Boolean {
        return !(input.trim().isEmpty() || input.length > 50)
    }

    /**
     * content 길이 체크
     */
    private fun contentLenChk(input: String) : Boolean {
        return if(input.trim().isEmpty()){
            tieContent.error = "Required Field"
            tieContent.requestFocus()
            false
        } else if(input.length < 30 || input.length > 500) {
            tieContent.error = "작성된 내용의 길이를 확인해 주세요."
            tieContent.requestFocus()
            false
        } else {
            tieContent.error = null
            true
        }
    }
}