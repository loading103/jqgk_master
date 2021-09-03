package com.daqsoft.module_main.repository

import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.pojo.Profile
import com.daqsoft.library_common.bean.AppMenu
import com.daqsoft.mvvmfoundation.base.BaseModel
import io.reactivex.rxjava3.core.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject


/**
 * @package name：com.daqsoft.module_main.repository
 * @date 5/11/2020 下午 5:25
 * @author zp
 * @describe
 */
class MainRepository @Inject constructor(private val mainApiService: MainApiService) : BaseModel(),MainApiService {
    override fun getMenus(): Observable<AppResponse<AppMenu>> {
        return mainApiService.getMenus()
    }

    override fun getUserInfo(): Observable<AppResponse<Profile>> {
        return mainApiService.getUserInfo()
    }

    override fun getPushAudio(): Observable<AppResponse<Any>> {
        return mainApiService.getPushAudio()
    }

    override fun uploadLocation(body: Map<String, String>): Observable<AppResponse<Any>> {
        return mainApiService.uploadLocation(body)
    }
    override fun checkUpdate(
        url: String,
        appId: String,
        method: String,
        token: String,
        appType: String,
        version: String
    ): Observable<Response<ResponseBody>> {
        return mainApiService.checkUpdate(url, appId, method, token, appType, version)
    }
}