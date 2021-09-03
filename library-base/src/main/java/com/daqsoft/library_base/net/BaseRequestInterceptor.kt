package com.daqsoft.library_base.net

import android.os.Parcelable
import android.webkit.WebSettings
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.GsonUtils
import com.daqsoft.library_base.global.MMKVGlobal
import com.daqsoft.library_base.pojo.LoginInfo
import com.daqsoft.library_base.utils.MMKVUtils
import com.daqsoft.mvvmfoundation.utils.ContextUtils
import okhttp3.Interceptor
import okhttp3.Response

class BaseRequestInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()

        var authorization = "Authorization"
        var token = "Bearer "

        val loginInfoJson = MMKVUtils.decodeString(MMKVGlobal.LOGIN_INFO)
        if (!loginInfoJson.isNullOrBlank()){
            val loginInfo = GsonUtils.fromJson<LoginInfo>(loginInfoJson,LoginInfo::class.java)
            authorization = if (loginInfo.header.isBlank()) "Authorization" else  loginInfo.header
            val header = if (loginInfo.bearer.isBlank()) "Bearer " else  loginInfo.bearer
            val body = if (loginInfo.token.token.isBlank()) "" else  loginInfo.token.token
            token = "${header}${body}"
        }

        builder
            .addHeader("User-Agent",getUserAgent())
            .addHeader("cli",getDeviceInfo())
            .addHeader("v",AppUtils.getAppVersionName())
            .addHeader(authorization,token)
        return chain.proceed(builder.build())
    }

    private fun getDeviceInfo():String{
        return "${DeviceUtils.getManufacturer()}-${DeviceUtils.getModel()}-Android-${DeviceUtils.getSDKVersionName()}"
    }


    private fun getUserAgent():String{
        var userAgent = ""
        userAgent = try {
            WebSettings.getDefaultUserAgent(ContextUtils.getContext())
        } catch (e: Exception) {
            System.getProperty("http.agent").toString()
        }

        val sb = StringBuffer()
        var i = 0
        val length = userAgent.length
        while (i < length) {
            val c = userAgent[i]
            if (c <= '\u001f' || c >= '\u007f') {
                sb.append(String.format("\\u%04x", c.toInt()))
            } else {
                sb.append(c)
            }
            i++
        }
        return sb.toString()
    }
}