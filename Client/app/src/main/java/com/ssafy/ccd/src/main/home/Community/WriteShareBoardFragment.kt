package com.ssafy.ccd.src.main.home.Community

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.youtube.player.internal.t
import com.google.firebase.storage.FirebaseStorage
import com.jakewharton.rxbinding3.widget.textChanges
import com.ssafy.ccd.R
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentWriteLocalBoardBinding
import com.ssafy.ccd.databinding.FragmentWriteShareBoardBinding
import com.ssafy.ccd.src.dto.Board
import com.ssafy.ccd.src.dto.Message
import com.ssafy.ccd.src.dto.User
import com.ssafy.ccd.src.main.MainActivity
import com.ssafy.ccd.src.network.service.BoardService
import com.ssafy.ccd.util.CommonUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.runBlocking
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

/**
 * @author Jiwoo
 * @since 03/23/22
 * 울동네 게시글 작성 화면
 */
class WriteShareBoardFragment : BaseFragment<FragmentWriteShareBoardBinding>(FragmentWriteShareBoardBinding::bind, R.layout.fragment_write_share_board) {
    private val TAG = "WriteShareBoardF_ccd"
    private lateinit var mainActivity : MainActivity
    private lateinit var editTextSubscription: Disposable

    private val STORAGE_CODE = 99
    private val STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    // 사진 선택
    private lateinit var imgUri: Uri    // 파일 uri
    private var fileExtension : String? = ""    // 파일 확장자
    private var imgSelectedChk = false

    private var timeName = "" // firebase storage upload file name

    // 수정인 경우 넘어오는 postId
    private var postId by Delegates.notNull<Int>()
    private lateinit var beforePost: Board

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
        mainActivity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            postId = getInt("postId")
        }
    }

    override fun onResume() {
        super.onResume()
        mainActivity.hideBottomNavi(true)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        mainActivity.hideBottomNavi(true)

        mainActivity.runOnUiThread(kotlinx.coroutines.Runnable {
            inputObservable()
        })

        if(postId > 0) {
            runBlocking { 
                mainViewModel.getPostDetail(postId)
            }
            beforePost = mainViewModel.postDetail.value!!
            binding.post = mainViewModel.postDetail.value
            binding.fragmentBoardWriteAddCameraBtn.visibility = View.INVISIBLE
            binding.fragmentBoardWriteIvSelectImg.visibility = View.VISIBLE
            modifyBtnClickEvent()

        } else {
            confirmBtnClickEvent()
        }

        backBtnClickEvent()
        selectImgBtnEvent()

    }


    /**
     * 완료 버튼 클릭 이벤트 - insert
     */
    private fun confirmBtnClickEvent() {    // 게시글 insert
        binding.fragmentBoardWriteSuccessBtn.setOnClickListener {

            // content(10 ~ 500 체크), 사진 선택
            val content = binding.fragmentDiaryWriteContent.text.toString()

            val userId = ApplicationClass.sharedPreferencesUtil.getUser().id
            val fileName = if(imgUri == null || imgUri.toString() == "" || imgUri == Uri.EMPTY) {
                ""
            } else{
                timeName = System.currentTimeMillis().toString()
                "${userId}/${timeName}."+ fileExtension
            }
            if(fileName == "") {
                showCustomToast("사진을 선택해 주세요")
            } else {
                if(contentLenChk(content)) {
                    val post = Board(
                        userId = userId,
                        typeId = 3,
                        title = "",
                        author = mainViewModel.loginUserInfo.value!!.nickname,
                        content = content,
                        time = System.currentTimeMillis().toString(),
                        photoPath = fileName)

                    insertPost(post)
                }

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
            uploadUserImgToFirebase(post)
        }

        if(response.code() == 200 || response.code() == 500) {
            val res = response.body()
            if(res != null) {
                if(res.success && res.data["isSuccess"] == true) {
                    showCustomToast("게시글 등록이 완료되었습니다")
                } else if(res.data["isSuccess"] == false) {
                    showCustomToast("게시글 등록 실패")
                    Log.e(TAG, "insertPost: ${res.message}", )
                }
            }
        }
    }

    /**
     * 완료 버튼 클릭 이벤트 - update
     */
    private fun modifyBtnClickEvent() {    // 게시글 insert Or Update
        binding.fragmentBoardWriteSuccessBtn.setOnClickListener {

            // content(10 ~ 500 체크), 사진 선택
            val content = binding.fragmentDiaryWriteContent.text.toString()

            val userId = ApplicationClass.sharedPreferencesUtil.getUser().id
            var fileName = if(imgUri == null || imgUri.toString() == "" || imgUri == Uri.EMPTY) {
                ""
            } else{
                timeName = System.currentTimeMillis().toString()
                "${userId}/${timeName}."+ fileExtension
            }

            if(contentLenChk(content)) {
                val beforeFileName = beforePost.photoPath.substring(beforePost.photoPath.lastIndexOf("/") + 1, beforePost.photoPath.length)
                if(fileName == "" || fileName == beforeFileName) {
                    fileName = beforePost.photoPath
                }

                val post = Board(
                    id = postId,
                    typeId = 3,
                    title = "",
                    content = content,
                    photoPath = fileName)
                updatePost(post)
            }

        }
    }


    /**
     * 게시글 update
     */
    private fun updatePost(post: Board) {
        var pageBack = false
        var response : Response<Message>

        runBlocking {
            response = BoardService().updatePost(post)
            if(post.photoPath != beforePost.photoPath) {
                uploadUserImgToFirebase(post)
                pageBack = false
            } else {
                pageBack = true
            }
        }

        if(response.code() == 200 || response.code() == 500) {
            val res = response.body()
            if(res != null) {
                if(res.success && res.data["isSuccess"] == true) {
                    showCustomToast("게시글 수정이 완료되었습니다")
                    if(pageBack) {
                        (requireActivity() as MainActivity).onBackPressed()
                    }
                } else if(res.data["isSuccess"] == false) {
                    showCustomToast("게시글 수정 실패")
                    Log.e(TAG, "updatePost: ${res.message}", )
                }
            }
        }
    }

    /**
     * 이미지 firebase storage 업로드
     */
    private fun uploadUserImgToFirebase(post: Board) {
        if(imgUri == null || imgUri == Uri.EMPTY) {
            Log.e("ERROR", "이미지 Uri에서 문제가 발생하였습니다.")
            showCustomToast("이미지 Uri에서 문제가 발생하였습니다.")
            childFragmentManager.popBackStack()
        }

        val storageReferenceChild = FirebaseStorage.getInstance().getReference("${post.userId}").child("$timeName.$fileExtension")

        storageReferenceChild.putFile(imgUri)
            .addOnSuccessListener{
                storageReferenceChild.downloadUrl
                    .addOnSuccessListener {
                        Log.d(TAG, "uploadUserImgToFirebase: $it")
                        (requireActivity() as MainActivity).onBackPressed()
                    }
            }
    }

    /**
     * 사진 선택 버튼 클릭 이벤트
     */
    private fun selectImgBtnEvent() {

        if (::imgUri.isInitialized) {
            Log.d(TAG, "selectImgBtnEvent: $imgUri")
        } else {
            imgUri = Uri.EMPTY
            Log.d(TAG, "fileUri 초기화  $imgUri")
        }

        binding.fragmentBoardWriteAddCameraBtn.setOnClickListener {
            if (mainActivity.checkPermission(STORAGE, STORAGE_CODE)) {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = MediaStore.Images.Media.CONTENT_TYPE
                filterActivityLauncher.launch(intent)
            }
        }

        binding.fragmentBoardWriteIvSelectImg.setOnClickListener {
            if (mainActivity.checkPermission(STORAGE, STORAGE_CODE)) {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = MediaStore.Images.Media.CONTENT_TYPE
                filterActivityLauncher.launch(intent)
            }
        }
    }

    /**
     * 갤러리 사진 선택 result
     */
    private val filterActivityLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == AppCompatActivity.RESULT_OK && it.data != null) {
                binding.fragmentBoardWriteAddCameraBtn.visibility = View.INVISIBLE
                binding.fragmentBoardWriteIvSelectImg.visibility = View.VISIBLE
                imgSelectedChk = false
                val currentImageUri = it.data?.data

                try {
                    currentImageUri?.let {
                        imgUri = currentImageUri
                        fileExtension = requireActivity().contentResolver.getType(currentImageUri)
                        // 사진 set
                        Glide.with(this)
                            .load(currentImageUri)
                            .into(binding.fragmentBoardWriteIvSelectImg)

                        fileExtension = fileExtension!!.substring(fileExtension!!.lastIndexOf("/") + 1, fileExtension!!.length)

                        // 파일 이름 set
                        binding.fragmentBoardWriteTvImgName.text = "${currentImageUri.lastPathSegment}.$fileExtension"
                    }
                } catch(e:Exception) {
                    e.printStackTrace()
                }
            } else if(it.resultCode == AppCompatActivity.RESULT_CANCELED){
                showCustomToast("사진 선택 취소")
            } else {
                binding.fragmentBoardWriteIvSelectImg.visibility = View.INVISIBLE
                Log.d(TAG,"filterActivityLauncher 실패")
            }
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
        binding.writeLocalBoardFragmentTvTextLen.text = "(${input.length} / 500)"
        if(input.trim().isEmpty()){
            binding.fragmentDiaryWriteTilContent.error = "Required Field"
            binding.fragmentDiaryWriteContent.requestFocus()
            return false
        } else if(input.length < 10 || input.length > 500) {
            binding.fragmentDiaryWriteTilContent.error = "작성된 내용의 길이를 확인해 주세요."
            binding.fragmentDiaryWriteContent.requestFocus()
            return false
        }
        else {
            binding.fragmentDiaryWriteTilContent.error = null
            return true
        }
    }

    /**
     * 뒤로가기 버튼 클릭 이벤트
     */
    private fun backBtnClickEvent() {
        binding.fragmentBoardWriteBack.setOnClickListener {
            this@WriteShareBoardFragment.findNavController().popBackStack()
        }
    }

    /**
     * 각 EditText 쿼리 디바운스 적용
     */
    private fun inputObservable() {
        binding.fragmentDiaryWriteContent.setQueryDebounce {
            contentLenChk(it)
        }
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

    override fun onDestroy() {
        super.onDestroy()
        mainActivity.hideBottomNavi(false)
        if (!editTextSubscription.isDisposed) {
            editTextSubscription.dispose()
        }
    }

}