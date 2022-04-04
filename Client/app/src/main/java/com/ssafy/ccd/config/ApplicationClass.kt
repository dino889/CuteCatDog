package com.ssafy.ccd.config

import android.app.Application
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.kakao.sdk.common.KakaoSdk
import com.ssafy.ccd.R
import com.ssafy.ccd.config.intercepter.AddCookiesInterceptor
import com.ssafy.ccd.config.intercepter.ReceivedCookiesInterceptor
import com.ssafy.ccd.config.intercepter.XAccessTokenInterceptor
import com.ssafy.ccd.util.SharedPreferencesUtil
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.security.KeyPairGenerator
import java.util.concurrent.TimeUnit

class ApplicationClass : Application() {
    companion object{
//        const val SERVER_URL = "http://61.85.38.39:8889/"
        const val SERVER_URL = "https://j6d103.p.ssafy.io/"
//        const val SERVER_URL = "http://suho.asuscomm.com:3000"
        const val IMGS_URL = "${SERVER_URL}imgs/"

        lateinit var sharedPreferencesUtil: SharedPreferencesUtil
        lateinit var retrofit: Retrofit

        // JWT Token Header 키 값
        const val X_ACCESS_TOKEN = "X-ACCESS-TOKEN"

        // 키 alias
        const val KEY_ALIAS = "cutecatdog"

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate() {
        super.onCreate()
        //shared preference 초기화
        sharedPreferencesUtil = SharedPreferencesUtil(applicationContext)

        val okHttpClient = OkHttpClient.Builder()
//            .addInterceptor(AddCookiesInterceptor())
////            .addInterceptor(ReceivedCookiesInterceptor())
            .addNetworkInterceptor(XAccessTokenInterceptor()) // JWT 자동 헤더 전송
            .connectTimeout(30, TimeUnit.SECONDS).build()

        // Gson 객체 생성 - setLenient 속성 추가
        val gson : Gson = GsonBuilder()
            .setLenient()
            .create()
        
        retrofit = Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addConverterFactory(ScalarsConverterFactory.create())
            .client(okHttpClient)
            .build()

        // 인증키 생성
        val kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore")

        val parameterSpec: KeyGenParameterSpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
        ).run {
            setDigests(KeyProperties.DIGEST_SHA256)
            build()
        }

        kpg.initialize(parameterSpec)
        val kp = kpg.generateKeyPair()

        // Kakao SDK 초기화
        KakaoSdk.init(this,"78b660953c918503e1a723afddb4d6e8")
    }

}