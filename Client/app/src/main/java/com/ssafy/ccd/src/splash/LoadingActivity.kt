package com.ssafy.ccd.src.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ssafy.ccd.config.BaseActivity
import com.ssafy.ccd.databinding.ActivityLoadingBinding
import android.os.Handler
import com.ssafy.ccd.src.login.LoginActivity

class LoadingActivity : BaseActivity<ActivityLoadingBinding>(ActivityLoadingBinding::inflate){
    private val SPLASH_TIME:Long = 6000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Handler().postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        },SPLASH_TIME)
    }
}