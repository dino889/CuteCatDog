package com.ssafy.ccd.config.intercepter

import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.config.ApplicationClass.Companion.X_ACCESS_TOKEN
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class XAccessTokenInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder: Request.Builder = chain.request().newBuilder()
        val jwtToken: String? = ApplicationClass.sharedPreferencesUtil.getString(X_ACCESS_TOKEN)
        if (jwtToken != null) {
            builder.addHeader("X-ACCESS-TOKEN", jwtToken)
        }
        return chain.proceed(builder.build())
    }
}