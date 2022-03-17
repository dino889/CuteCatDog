package com.ssafy.ccd.src.login

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
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


private const val TAG = "LoginFragment_ccd"
class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::bind, R.layout.fragment_login) {
    private lateinit var loginActivity: LoginActivity

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
    }

    private fun loginBtnClickEvent() {
        binding.fragmentLoginBtnLogin.setOnClickListener {
            val loginRes = login(binding.fragmentLoginEmail.text.toString(), binding.fragmentLoginPw.text.toString())

            if(loginRes.data.get("user") != null && loginRes.message == "로그인 성공") {
                val loginUser = loginRes.data["user"]

                val type: Type = object : TypeToken<User>() {}.type
                val user = CommonUtils.parseDto<User>(loginUser!!, type)

                ApplicationClass.sharedPreferencesUtil.addUser(User(user.id, user.deviceToken))
                showCustomToast("로그인 되었습니다.")
                loginActivity.openFragment(1)

            } else if(loginRes.message == "로그인 실패") {
                showCustomToast("ID와 PW를 확인해 주세요.")

            } else if(loginRes.success == false) {
                showCustomToast("서버 통신에 실패했습니다.")
                Log.d(TAG, "loginBtnClickEvent: ${loginRes.message}")

            }
        }
    }

    private fun login(email: String, password: String) : Message{
        var result = Message()
        val encPw = loginActivity.sha256(password)

        runBlocking {
            result = mainViewModel.login(User(email, encPw))
        }
        return result
    }

}