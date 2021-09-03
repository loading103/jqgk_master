package com.daqsoft.module_main.repository

import com.daqsoft.library_base.global.HttpGlobal
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.pojo.Profile
import com.daqsoft.library_base.utils.AppUtils
import com.daqsoft.library_common.bean.AppMenu
import io.reactivex.rxjava3.core.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*


/**
 * @package name：com.daqsoft.module_main.repository
 * @date 5/11/2020 下午 5:27
 * @author zp
 * @describe
 */
interface MainApiService {

    /**
     * 获取菜单
     */
    @GET("v5/business/ums/menus")
    fun getMenus(): Observable<AppResponse<AppMenu>>


    /**
     * 获取用户信息
     */
    @GET("v5/business/ums/profile")
    fun getUserInfo(): Observable<AppResponse<Profile>>




    /**
     * 获取用户信息
     */
    @GET("v5/business/ums/PushAudio")
    fun getPushAudio(): Observable<AppResponse<Any>>


    /**
     * 上传地理位置
     */
    @POST("v5/business/gps")
    fun uploadLocation(@Body body: Map<String,String>): Observable<AppResponse<Any>>


    /**
     * 检查更新
     */
    @GET
    fun checkUpdate(
        @Url url: String = HttpGlobal.Update.URL,
        @Query("AppId") appId: String = HttpGlobal.Update.APP_ID,
        @Query("Method") method: String = HttpGlobal.Update.METHOD,
        @Query("token") token: String = HttpGlobal.Update.TOKEN,
        @Query("AppType") appType: String = HttpGlobal.Update.APP_TYPE,
        @Query("VersionCode") version: String = AppUtils.getVersionName()
    ) : Observable<Response<ResponseBody>>
}