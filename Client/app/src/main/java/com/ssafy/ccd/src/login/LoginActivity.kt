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
import androidx.annotation.RequiresApi
import com.google.android.gms.common.util.Base64Utils
import com.ssafy.ccd.config.ApplicationClass
import java.security.*
import java.util.*
import kotlin.experimental.and


private const val TAG = "LoginActivity_ccd"
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