package com.ssafy.ccd.src.login

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import com.ssafy.ccd.R
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentJoinBinding
import com.ssafy.ccd.src.dto.Message
import com.ssafy.ccd.src.dto.User
import kotlinx.coroutines.runBlocking

private const val TAG = "JoinFragment_ccd"
class JoinFragment : BaseFragment<FragmentJoinBinding>(FragmentJoinBinding::bind, R.layout.fragment_join) {
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

        joinBtnClickEvent()

    }

    private fun joinBtnClickEvent() {
        binding.fragmentJoinBtnJoin.setOnClickListener {
            val nickname = binding.fragmentJoinEtNick.text.toString()
            val password = binding.fragmentJoinEtPw.text.toString()
            val email = "${binding.fragmentJoinEtEmail.text}@${binding.fragmentJoinEtDomain.text}"
            
            val joinRes = join(email, nickname, password)

            if(joinRes.data["isSignup"] == true && joinRes.message == "회원가입 성공") {
                showCustomToast("회원가입이 완료되었습니다. 다시 로그인 해주세요.")
                (requireActivity() as LoginActivity).onBackPressed()
            } else if(joinRes.data["isSignup"] == false && joinRes.message == "회원가입 실패") {
                showCustomToast("회원가입에 실패했습니다. 다시 시도해 주세요.")
            } else if(joinRes.data["isExist"] == false && joinRes.message == "회원가입 실패") {
                showCustomToast("이미 존재하는 이메일입니다. 다시 인증해 주세요.")
            } else {
                showCustomToast("서버 통신에 실패했습니다.")
                Log.d(TAG, "joinBtnClickEvent: ${joinRes.message}")
            }
        }
    }

    private fun join(email: String, nickname: String, password: String) : Message {
        var result = Message()
        runBlocking {
            result = mainViewModel.join(User(email, nickname, password, "default.png"))
        }
        return result
    }



}