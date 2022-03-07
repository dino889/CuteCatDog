package com.ssafy.ccd.config.intercepter

import com.ssafy.ccd.config.ApplicationClass
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class AddCookiesInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response? {
        val builder: Request.Builder = chain.request().newBuilder()

        // cookie 가져오기
        val getCookies = ApplicationClass.sharedPreferencesUtil.getUserCookie()
        for (cookie in getCookies!!) {
            builder.addHeader("Cookie", cookie)
        }
        return chain.proceed(builder.build())
    }
}