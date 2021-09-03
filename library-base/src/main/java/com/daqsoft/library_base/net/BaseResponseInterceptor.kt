package com.daqsoft.library_base.net

import okhttp3.Interceptor
import okhttp3.Response


class BaseResponseInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        return response
    }
}