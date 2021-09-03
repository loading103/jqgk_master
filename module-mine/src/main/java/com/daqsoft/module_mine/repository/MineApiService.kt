package com.daqsoft.module_mine.repository

import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.module_mine.repository.pojo.vo.Company
import com.daqsoft.library_base.pojo.LoginInfo
import com.daqsoft.library_base.pojo.Profile
import com.daqsoft.library_common.bean.Employee
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.*

/**
 * @ClassName    MineApiService
 * @Description
 * @Author       yuxc
 * @CreateDate   2021/5/7
 */
interface MineApiService {

    /**
     * 选择登录公司
     */
    @POST("v5/business/ums/choose/login/company")
    fun chooseCompany(
        @Body body: Map<String,String>
    ):Observable<AppResponse<LoginInfo>>


    /**
     * 获取验证码
     */
    @GET("v5/business/ums/msg/code")
    fun getVerifyCode(
        @Query("mobile") mobile: String
    ):Observable<AppResponse<Any>>

    /**
     * 登录
     */
    @POST("v5/business/ums/login")
    fun login(
        @Body body: Map<String,String>
    ): Observable<AppResponse<Company>>

    /**
     * 退出登录
     */
    @GET("v5/business/ums/logout")
    fun logout(): Observable<AppResponse<Any>>

    /**
     * 获取用户信息
     */
    @GET("v5/business/ums/profile")
    fun getUserInfo(): Observable<AppResponse<Profile>>


    /**
     * 员工详情
     */
    @GET("v5/business/address/book/detail/employee")
    fun getEmployeeDetail(
        @Query("id") id: String
    ): Observable<AppResponse<Employee>>



}