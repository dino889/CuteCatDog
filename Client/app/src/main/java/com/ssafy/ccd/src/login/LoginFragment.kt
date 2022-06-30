package com.ssafy.ccd.src.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.ssafy.ccd.R
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentLoginBinding
import com.ssafy.ccd.src.dto.Message
import com.ssafy.ccd.src.dto.User
import kotlinx.coroutines.runBlocking
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.rx
import com.ssafy.ccd.util.CommonUtils
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import java.lang.reflect.Type


private const val TAG = "LoginFragment_ccd"
class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::bind, R.layout.fragment_login) {
    private lateinit var loginActivity: LoginActivity

    // google 로그인
    private lateinit var mAuth: FirebaseAuth
    var mGoogleSignInClient: GoogleSignInClient? = null


    // facebook 로그인
    private var callbackManager: CallbackManager? = null

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

        binding.fragmentLoginForgot.setOnClickListener {
            loginActivity.openFragment(4)
        }

        callbackManager = CallbackManager.Factory.create()

        loginBtnClickEvent()
        googleLoginBtnClickEvent()
        kakaoLoginBtnClickEvent()
        facebookLoginBtnClickEvent()
    }


    private fun loginBtnClickEvent() {
        binding.fragmentLoginBtnLogin.setOnClickListener {
            login(binding.fragmentLoginEmail.text.toString(), binding.fragmentLoginPw.text.toString())
        }
    }

    private fun login(email: String, password: String){
        var result = Message()

        runBlocking {
            result = mainViewModel.login(User(email, loginActivity.sha256(password)))
            Log.d(TAG, "login: ${loginActivity.sha256(password)}")
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

    /**
     * email 중복 체크
     * @return 중복된 이메일이 없으면 true 반환
     */
    private fun existEmailChk(user: User) : Boolean {
        var existEmailRes = Message()
        runBlocking {
            existEmailRes = mainViewModel.existsChkUserEmail(user.email)
        }

        if(existEmailRes.data["type"] == "false" && existEmailRes.message == "중복된 이메일 없음") {
            snsLoginJoin(user)
            return true
        } else if(existEmailRes.message == "이미 존재하는 이메일") {

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

    // firebase auth 인증 초기화
    private fun initAuth() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_login_key))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        mAuth = FirebaseAuth.getInstance()

    }

    // ---------------------------------------------------------------------------------------------
    /**
     * sns Login - Google 로그인
     */
    private fun googleLoginBtnClickEvent() {
        binding.loginFragmentBtnGoogle.setOnClickListener {
            initAuth()
            signIn()
        }
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

    // ---------------------------------------------------------------------------------------------
    /**
     * sns Login - Kakao
     */
    private fun kakaoLoginBtnClickEvent() {
        val disposables = CompositeDisposable()
        binding.loginFragmentBtnKakao.setOnClickListener {
            // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(requireContext())) {
                UserApiClient.rx.loginWithKakaoTalk(requireContext())
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorResumeNext { error ->
                        // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                        // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            Single.error(error)
                        } else {
                            // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                            UserApiClient.rx.loginWithKakaoAccount(requireContext())
                        }
                    }.observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ token ->
                        Log.i(TAG, "로그인 성공 ${token.accessToken}")
                        kakaoLogin()
                    }, { error ->
                        Log.e(TAG, "로그인 실패", error)
                    }).addTo(disposables)
            } else {
                UserApiClient.rx.loginWithKakaoAccount(requireContext())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ token ->
                        Log.i(TAG, "로그인 성공 ${token.accessToken}")
                        kakaoLogin()
                    }, { error ->
                        Log.e(TAG, "로그인 실패", error)
                    }).addTo(disposables)
            }
        }
    }

    private fun kakaoLogin() {
        val disposables = CompositeDisposable()
        // 사용자 정보 요청 (기본)
        UserApiClient.rx.me()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ user ->
//                Log.i(TAG, "사용자 정보 요청 성공" +
//                        "\n회원번호: ${user.id}" +  // pw
//                        "\n이메일: ${user.kakaoAccount?.email}" +  // id, email
//                        "\n닉네임: ${user.kakaoAccount?.profile?.nickname}" +  // nickname
//                        "\n프로필사진: ${user.kakaoAccount?.profile?.thumbnailImageUrl}")   // image

                val email = user.kakaoAccount?.email.toString()
                val uid = user.id.toString()
                val nickname = user.kakaoAccount?.profile?.nickname.toString()
                val image = user.kakaoAccount?.profile?.thumbnailImageUrl.toString()

                val newUser = User(email, nickname, uid, image, "kakao")
                existEmailChk(newUser)

            }, { error ->
                Log.e(TAG, "사용자 정보 요청 실패", error)
            })
            .addTo(disposables)
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * sns Login - Facebook
     */
    private fun facebookLoginBtnClickEvent() {
        binding.loginFragmentBtnFacebook.setOnClickListener {
            initAuth()
            facebookLogin()
        }
    }

    private fun facebookLogin() {
        LoginManager.getInstance()
            .logInWithReadPermissions(this, listOf("email", "public_profile"))

        LoginManager.getInstance()
            .registerCallback(callbackManager, object: FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {
                    if (result?.accessToken != null) {
                        // facebook 계정 정보를 firebase 서버에게 전달(로그인)
                        val accessToken = result.accessToken
                        firebaseAuthWithFacebook(accessToken)
                    } else {
                        Log.d("Facebook", "Fail Facebook Login")
                    }
                }

                override fun onCancel() {
                    //취소가 된 경우 할일
                }

                override fun onError(error: FacebookException?) {
                    //에러가 난 경우 할일
                }
            })
    }

    private fun firebaseAuthWithFacebook(accessToken: AccessToken?) {
        // AccessToken 으로 Facebook 인증
        val credential = FacebookAuthProvider.getCredential(accessToken!!.token)

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

                        val newUser = User(email = email, nickname = nickname, password = uid, profileImage = image, "facebook")
                        existEmailChk(newUser)
                        Log.d(TAG, "signInWithCredential:success $newUser $uid")
                    }

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    showCustomToast("Authentication failed.")
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

}