package com.ssafy.ccd.src.main.mypage

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.storage.FirebaseStorage
import com.ssafy.ccd.R
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentUserProfileBinding
import com.ssafy.ccd.src.dto.Message
import com.ssafy.ccd.src.dto.User
import com.ssafy.ccd.src.login.LoginActivity
import com.ssafy.ccd.src.main.MainActivity
import com.ssafy.ccd.src.network.service.UserService
import kotlinx.coroutines.runBlocking
import retrofit2.Response
import kotlin.math.log

/**
 * @since 03.21.22.
 * @author Jiwoo
 * 사용자 정보 수정 페이지
 */
private const val TAG = "UserProfileFragment_ccd"
class UserProfileFragment : BaseFragment<FragmentUserProfileBinding>(FragmentUserProfileBinding::bind, R.layout.fragment_user_profile) {
    private lateinit var mainActivity : MainActivity
    private val STORAGE_CODE = 99
    private val STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    // 사진 선택
    private lateinit var imgUri: Uri    // 파일 uri
    private var fileExtension : String? = ""    // 파일 확장자

    private var timeName = "" // firebase storage upload file name

    private lateinit var beforeUser: User
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity.hideBottomNavi(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loginUser = mainViewModel.loginUserInfo.value
        binding.user = loginUser
        beforeUser = loginUser!!
        val arr = loginUser.email.split("@")
        binding.userProfileFragmentEtEmail.setText(arr[0])
        binding.userProfileFragmentEtDomain.setText(arr[1])

        initUserImage(loginUser.profileImage)


        // back btn click event
        binding.userProfileFragmentIvBack.setOnClickListener {
            this@UserProfileFragment.findNavController().popBackStack()
        }

        selectImgBtnEvent()
        updateConfirmBtnClickEvent()
    }

    /**
     * 사용자 프로필 사진 초기화
     */
    private fun initUserImage(imgUrl: String?) {
        if(imgUrl == null || imgUrl == ""){
            Glide.with(this)
                .load(R.drawable.defaultimg)
                .into(binding.userProfileFragmentIvUserImage)

        } else if(imgUrl.contains("https://")) {
            Glide.with(this)
                .load(imgUrl)
                .into(binding.userProfileFragmentIvUserImage)

        } else {
            val storage = FirebaseStorage.getInstance("gs://cutecatdog-32527.appspot.com/")
            val storageRef = storage.reference
            storageRef.child(imgUrl).downloadUrl.addOnSuccessListener { p0 ->
                Glide.with(this)
                    .load(p0)
                    .into(binding.userProfileFragmentIvUserImage)
            }.addOnFailureListener {
                Log.d(TAG, "initUserImage: $it")
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

        binding.userProfileFragmentIbSelectImg.setOnClickListener {
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
                val currentImageUri = it.data?.data

                try {
                    currentImageUri?.let {
                        imgUri = currentImageUri
                        fileExtension = requireActivity().contentResolver.getType(currentImageUri)
                        // 사진 set
                        Glide.with(this)
                            .load(currentImageUri)
                            .into(binding.userProfileFragmentIvUserImage)

                        fileExtension = fileExtension!!.substring(fileExtension!!.lastIndexOf("/") + 1, fileExtension!!.length)
                    }
                } catch(e:Exception) {
                    e.printStackTrace()
                }
            } else if(it.resultCode == AppCompatActivity.RESULT_CANCELED){
                showCustomToast("사진 선택 취소")
//                imgUri = Uri.EMPTY
            } else{
                Log.d(TAG,"filterActivityLauncher 실패")
            }
        }

    /**
     * 회원정보 수정 완료 버튼 클릭 이벤트
     */
    private fun updateConfirmBtnClickEvent() {
        binding.userProfileFragmentBtnConfirm.setOnClickListener {
            // 이미지는 firebase에 업로드
            // 닉네임, 사진 경로는 db에 업로드
            val userId = ApplicationClass.sharedPreferencesUtil.getUser().id
            var fileName = if(imgUri == null || imgUri.toString() == "" || imgUri == Uri.EMPTY) {
                ""
            } else{
                timeName = System.currentTimeMillis().toString()
                "${userId}/${timeName}."+ fileExtension
            }

            val beforeFileName = beforeUser.profileImage.substring(beforeUser.profileImage.lastIndexOf("/") + 1, beforeUser.profileImage.length)
            if(fileName == "" || fileName == beforeFileName) {
                fileName = beforeUser.profileImage
            }

//            val updateUser = User(id = userId, nickname = binding.userProfileFragmentEtNick.text.toString(), profileImage = fileName, email = "dmg05152@naver.com", password = "K4gUQxGDLVnvE4YAyQvhKoIcfPAancVqkliTMlwK+Z8=", socialType = "none")
            val updateUser = User(id = userId, nickname = binding.userProfileFragmentEtNick.text.toString(), profileImage = fileName)

            updateUser(updateUser)
        }
    }

    /**
     * 사용자 정보 업데이트 서버 통신
     */
    private fun updateUser(user: User) {
        var pageBack = false
        var response : Response<Message>
        runBlocking {
            response = UserService().updateUser(user)
            if(user.profileImage != beforeUser.profileImage) {
                uploadUserImgToFirebase(user)
                pageBack = false
            } else {
                pageBack = true
            }
        }

        if(response.code() == 200 || response.code() == 500) {
            val body = response.body()
            if(body != null) {
                if(body.data["isModify"] == true && body.message == "회원 정보 수정 성공") {
                    showCustomToast("회원 정보가 정상적으로 변경되었습니다.")
                    runBlocking {
                        mainViewModel.getUserInfo(user.id, true)
                    }

                    if(pageBack) {
                        (requireActivity() as MainActivity).onBackPressed()
                    }
                } else if(body.data["isModify"] == false) {
                    showCustomToast("회원 정보 수정 실패")
                }
                if(body.success == false) {
                    showCustomToast("서버 통신 실패")
                    Log.d(TAG, "updateUser: ${body.message}")
                }
            }
        }

    }

    /**
     * 이미지 firebase storage 업로드
     */
    private fun uploadUserImgToFirebase(user: User) {
        if(imgUri == null || imgUri == Uri.EMPTY) {
            Log.e("ERROR", "이미지 Uri에서 문제가 발생하였습니다.")
            showCustomToast("이미지 Uri에서 문제가 발생하였습니다.")
            childFragmentManager.popBackStack()
        }

        val storageReferenceChild = FirebaseStorage.getInstance().getReference("${user.id}").child("$timeName.$fileExtension")

        storageReferenceChild.putFile(imgUri)
            .addOnSuccessListener{
                storageReferenceChild.downloadUrl
                    .addOnSuccessListener {
                        Log.d(TAG, "uploadUserImgToFirebase: $it")
                        (requireActivity() as MainActivity).onBackPressed()
                    }
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        mainActivity.hideBottomNavi(false)
    }
    
}