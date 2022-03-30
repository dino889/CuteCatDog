package com.ssafy.ccd.src.login

import android.app.Application
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.security.keystore.KeyProperties
import com.ssafy.ccd.R
import com.ssafy.ccd.config.BaseActivity
import com.ssafy.ccd.databinding.ActivityLoginBinding
import com.ssafy.ccd.src.main.MainActivity
import android.security.keystore.KeyGenParameterSpec
import android.util.Base64
import android.util.Log
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.facebook.CallbackManager
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.google.android.gms.common.util.Base64Utils
import com.kakao.sdk.common.util.Utility
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.config.ApplicationClass.Companion.sharedPreferencesUtil
import com.ssafy.ccd.src.network.service.UserService
import com.ssafy.ccd.src.network.viewmodel.MainViewModels
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import java.security.*
import java.util.*
import kotlin.experimental.and


private const val TAG = "LoginActivity_ccd"
class LoginActivity : BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate){
    val mainViewModels: MainViewModels by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        var userId = -1
        if(sharedPreferencesUtil.getAutoLogin() != null) {
            userId = sharedPreferencesUtil.getAutoLogin()!!
        }
        //로그인 상태 확인. id가 있다면 로그인 된 상태 -> 가장 첫 화면은 홈 화면의 Fragment로 지정
        if (userId != null || userId != -1){
            var isPossible = 0
            runBlocking {
                isPossible = mainViewModels.getUserInfo(userId, true)
            }
            if(isPossible == 1) {
                openFragment(1)
            } else {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frame_login_layout, LoginFragment())
                    .commit()
            }

        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_login_layout, LoginFragment())
                .commit()
        }

        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)

//        logger.logPurchase(BigDecimal.valueOf(4.32), Currency.getInstance("USD"));

//         kakao 플랫폼 키 해시 등록
        val keyHash = Utility.getKeyHash(this)
        Log.d("kakaoKeyHash", "onCreate: $keyHash")
    }



    fun openFragment(int:Int){
        val transaction = supportFragmentManager.beginTransaction()
        when(int){
            1->{
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            2->{
                transaction.replace(R.id.frame_login_layout, LoginFragment())
                    .addToBackStack(null)
            }
            3->{
                transaction.replace(R.id.frame_login_layout, JoinFragment())
                    .addToBackStack(null)
            }
            4-> {
                transaction.replace(R.id.frame_login_layout, ResetPasswordFragment())
                    .addToBackStack(null)
            }
        }
        transaction.commit()
    }

    fun sha256(pw: String) : String {
        val hash: ByteArray
        try {
            val md = MessageDigest.getInstance("SHA-256")
            md.update(pw.toByteArray())
            hash = md.digest()
        } catch (e: CloneNotSupportedException) {
            throw DigestException("couldn't make digest of partial content");
        }

        return Base64.encodeToString(hash, Base64.NO_WRAP)
    }

}