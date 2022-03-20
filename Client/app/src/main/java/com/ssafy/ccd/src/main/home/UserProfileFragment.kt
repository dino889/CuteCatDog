package com.ssafy.ccd.src.main.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.rx
import com.ssafy.ccd.R
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentUserProfileBinding
import com.ssafy.ccd.src.login.LoginActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers


private const val TAG = "UserProfileFragment_ccd"
class UserProfileFragment : BaseFragment<FragmentUserProfileBinding>(FragmentUserProfileBinding::bind, R.layout.fragment_user_profile) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.loginUserInfo.observe(viewLifecycleOwner, {
            binding.user = it
        })

        // back btn click event
        binding.userProfileFragmentIvBack.setOnClickListener {
            this@UserProfileFragment.findNavController().popBackStack()
        }

        // logout btn click event
        binding.userProfileFragmentBtnLogout.setOnClickListener {
            logout()
        }
    }


    private fun logout(){

        mainViewModel.loginUserInfo.observe(this, {
            val type = it.socialType
            Log.d(TAG, "logout: type : $type")
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
    
}