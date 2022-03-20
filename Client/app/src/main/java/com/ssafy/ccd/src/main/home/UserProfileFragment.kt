package com.ssafy.ccd.src.main.home

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.rx
import com.ssafy.ccd.R
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentUserProfileBinding
import com.ssafy.ccd.src.dto.Message
import com.ssafy.ccd.src.login.LoginActivity
import com.ssafy.ccd.src.main.MainActivity
import com.ssafy.ccd.src.network.service.UserService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.runBlocking
import retrofit2.Response


private const val TAG = "UserProfileFragment_ccd"
class UserProfileFragment : BaseFragment<FragmentUserProfileBinding>(FragmentUserProfileBinding::bind, R.layout.fragment_user_profile) {
    private lateinit var mainActivity : MainActivity

    // firebase authentication
    var mGoogleSignInClient: GoogleSignInClient? = null


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
        mainViewModel.loginUserInfo.observe(viewLifecycleOwner, {
            binding.user = it
        })

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_login_key))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        // back btn click event
        binding.userProfileFragmentIvBack.setOnClickListener {
            this@UserProfileFragment.findNavController().popBackStack()
        }

        // logout btn click event
        binding.userProfileFragmentBtnLogout.setOnClickListener {
            logout()
        }

        // withdrawal btn click event
        binding.userProfileFragmentBtnWithdrawal.setOnClickListener {
            showDeleteUserDialog()
        }
    }


    private fun logout(){

        mainViewModel.loginUserInfo.observe(viewLifecycleOwner, {
            val type = it.socialType
            if(type == "google" || type == "facebook") {
                // google, facebook Logout
                FirebaseAuth.getInstance().signOut()
            } else if(type == "kakao") {
                // kakao Logout
                val disposables = CompositeDisposable()

                UserApiClient.rx.logout()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        Log.i(TAG, "로그아웃 성공. SDK에서 토큰 삭제 됨")
                    }, { error ->
                        Log.e(TAG, "로그아웃 실패. SDK에서 토큰 삭제 됨", error)
                    }).addTo(disposables)
            }
        })

        ApplicationClass.sharedPreferencesUtil.deleteUser()
        ApplicationClass.sharedPreferencesUtil.deleteUserCookie()
        ApplicationClass.sharedPreferencesUtil.deleteAutoLogin()
        //화면이동
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    // 회원 탈퇴 dialog
    private fun showDeleteUserDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("회원 탈퇴")
            .setMessage("정말로 탈퇴하시겠습니까?")
            .setPositiveButton("YES", DialogInterface.OnClickListener{ dialogInterface, id ->
                // 탈퇴기능구현
                mainViewModel.loginUserInfo.observe(viewLifecycleOwner, {
                    val type = it.socialType
                    if(type == "kakao") {
                        val disposables = CompositeDisposable()
                        // 연결 끊기
                        UserApiClient.rx.unlink()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                Log.i(TAG, "연결 끊기 성공. SDK에서 토큰 삭제 됨")
                            }, { error ->
                                Log.e(TAG, "연결 끊기 실패", error)
                            }).addTo(disposables)
                    } else if(type == "google" || type == "facebook") {
                        FirebaseAuth.getInstance().currentUser?.delete()!!.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                //로그아웃처리
                                FirebaseAuth.getInstance().signOut()
                                mGoogleSignInClient?.signOut()
                                Log.i(TAG, "firebase auth 로그인 연결 끊기 성공")
                            } else {
                                Log.i(TAG, "firebase auth 로그인 user 삭제 실패")
                            }
                        }
                    }

                    var res : Response<Message>
                    runBlocking {
                        res = UserService().deleteUser(ApplicationClass.sharedPreferencesUtil.getUser().id)
                    }
                    if(res.code() == 200 || res.code() == 500) {
                        val rbody = res.body()
                        if(rbody != null) {
                            if(rbody.data["isDelete"] == true && rbody.message == "회원 탈퇴 성공") {
                                showCustomToast("회원 탈퇴가 완료되었습니다.")
                                logout()

                            } else if(rbody.data["isDelete"] == false) {
                                showCustomToast("회원 탈퇴 실패")
                            }
                            if(rbody.success == false) {
                                showCustomToast("서버 통신 실패")
                                Log.d(TAG, "changePwBtnClickEvent: ${rbody.message}")
                            }
                        }
                    }
                    ApplicationClass.sharedPreferencesUtil.deleteUser()
                    ApplicationClass.sharedPreferencesUtil.deleteUserCookie()
                    ApplicationClass.sharedPreferencesUtil.deleteAutoLogin()
                })
            })
            .setNeutralButton("NO", null)
            .create()

        builder.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainActivity.hideBottomNavi(false)
    }
    
}