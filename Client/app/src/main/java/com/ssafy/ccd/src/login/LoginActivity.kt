package com.ssafy.ccd.src.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ssafy.ccd.R
import com.ssafy.ccd.config.BaseActivity
import com.ssafy.ccd.databinding.ActivityLoginBinding
import com.ssafy.ccd.src.main.MainActivity

class LoginActivity : BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_login_layout, LoginFragment())
            .commit()
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
        }
        transaction.commit()
    }
}