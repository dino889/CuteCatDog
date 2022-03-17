package com.ssafy.ccd.src.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.gson.Gson
import com.ssafy.ccd.R
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentLoginBinding
import com.ssafy.ccd.src.dto.Message
import com.ssafy.ccd.src.dto.User
import com.ssafy.ccd.src.network.service.UserService
import com.ssafy.ccd.src.network.viewmodel.MainViewModels
import kotlinx.coroutines.runBlocking
import com.google.gson.reflect.TypeToken
import com.ssafy.ccd.util.CommonUtils
import java.lang.reflect.Type
import kotlin.math.log


private const val TAG = "LoginFragment_ccd"
class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::bind, R.layout.fragment_login) {
    private lateinit var loginActivity: LoginActivity

    // google 로그인
    private lateinit var mAuth: FirebaseAuth
    var mGoogleSignInClient: GoogleSignInClient? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loginActivity = context as LoginActivity
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fragmentLoginSignup.setOnClickListener {
            loginActivity.openFragment(3)
        }

        loginBtnClickEvent()
        googleLoginBtnClickEvent()
    }

    private fun loginBtnClickEvent() {
        binding.fragmentLoginBtnLogin.setOnClickListener {
            val encPw = loginActivity.sha256(binding.fragmentLoginPw.text.toString())
            login(binding.fragmentLoginEmail.text.toString(), binding.fragmentLoginPw.text.toString())

//            if(loginRes.data.get("user") != null && loginRes.message == "로그인 성공") {
//                val loginUser = loginRes.data["user"]
//
//                val type: Type = object : TypeToken<User>() {}.type
//                val user = CommonUtils.parseDto<User>(loginUser!!, type)
//
//                ApplicationClass.sharedPreferencesUtil.addUser(User(user.id, user.deviceToken))
//                showCustomToast("로그인 되었습니다.")
//                loginActivity.openFragment(1)
//
//            } else if(loginRes.message == "로그인 실패") {
//                showCustomToast("ID와 PW를 확인해 주세요.")
//
//            } else if(loginRes.success == false) {
//                showCustomToast("서버 통신에 실패했습니다.")
//                Log.d(TAG, "loginBtnClickEvent: ${loginRes.message}")
//
//            }
        }
    }

    private fun login(email: String, password: String){
        var result = Message()

        runBlocking {
            result = mainViewModel.login(User(email, loginActivity.sha256(password)))
        }

        if(result.data.get("user") != null && result.message == "로그인 성공") {
            val loginUser = result.data["user"]

            val type: Type = object : TypeToken<User>() {}.type
            val user = CommonUtils.parseDto<User>(loginUser!!, type)

            ApplicationClass.sharedPreferencesUtil.addUser(User(user.id, user.deviceToken))

            if(binding.loginFragmentChkb.isChecked) {   // 자동 로그인이 체크되어 있으면
                ApplicationClass.sharedPreferencesUtil.setAutoLogin(user.id)
            }

            showCustomToast("로그인 되었습니다.")
            loginActivity.openFragment(1)

        } else if(result.message == "로그인 실패") {
            showCustomToast("ID와 PW를 확인해 주세요.")

        } else if(result.success == false) {
            showCustomToast("서버 통신에 실패했습니다.")
            Log.d(TAG, "loginBtnClickEvent: ${result.message}")
        }
    }


    // ---------------------------------------------------------------------------------------------
    private fun googleLoginBtnClickEvent() {
        binding.loginFragmentBtnGoogle.setOnClickListener {
            initAuth()
        }
    }
    /**
     * sns Login - Google 로그인
     */
    // 인증 초기화
    private fun initAuth() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_login_key))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        mAuth = FirebaseAuth.getInstance()
        signIn()
    }

    // 구글 로그인 창을 띄우는 작업
    private fun signIn() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        requestActivity.launch(signInIntent)
    }

    // 구글 인증 결과 획득 후 동작 처리
    private val requestActivity: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            val data = it.data

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)

            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    // 구글 인증 결과 성공 여부에 따른 처리
    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    if(user != null) {
                        // email nickname pw photo
                        val email = user.email.toString()
                        val nickname = user.displayName.toString()
                        val uid = user.uid
                        val image = user.photoUrl.toString()

                        val newUser = User(email, nickname, uid, image, "google")
                        Log.d(TAG, "firebaseAuthWithGoogle: $newUser")
                        existEmailChk(newUser)
                    }
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }

    /**
     * email 중복 체크
     * @return 중복된 이메일이 없으면 true 반환
     */
    private fun existEmailChk(user: User) : Boolean {
        var existEmailRes = Message()
        runBlocking {
            existEmailRes = mainViewModel.existsChkUserEmail(user.email)
        }

        if(existEmailRes.data["isExisted"] == false && existEmailRes.message == "중복된 이메일 없음") {
            snsLoginJoin(user)
            return true
        } else if(existEmailRes.data["isExisted"] == true && existEmailRes.message == "이미 존재하는 이메일") {
            login(user.email, user.password)
            return false
        } else {
            showCustomToast("서버 통신에 실패했습니다.")
            Log.d(TAG, "existEmailChk: ${existEmailRes.message}")
            return false
        }
    }

    /**
     * 새로운 회원이라면 회원가입 진행
     */
    private fun snsLoginJoin(user: User) {
        var result = Message()
        val realPw = user.password

        val encPw = loginActivity.sha256(user.password)
        user.password = encPw

        runBlocking {
            result = mainViewModel.join(user)
        }

        if(user != null) {

            if(result.data["isSignup"] == true && result.message == "회원가입 성공") {
                login(user.email, realPw)
            } else if(result.data["isSignup"] == false && result.message == "회원가입 실패") {
                showCustomToast("회원가입에 실패했습니다. 다시 시도해 주세요.")
            } else if(result.data["isExist"] == false && result.message == "회원가입 실패") {
                showCustomToast("이미 존재하는 이메일입니다. 다시 인증해 주세요.")
            } else {
                showCustomToast("서버 통신에 실패했습니다.")
                Log.d(TAG, "snsLoginJoin: ${result.message}")
            }
        } else {
            showCustomToast("입력 값을 다시 확인해 주세요.")
        }
    }

}