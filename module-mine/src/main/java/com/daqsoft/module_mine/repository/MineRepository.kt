package com.daqsoft.module_mine.repository

import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.module_mine.repository.pojo.vo.Company
import com.daqsoft.library_base.pojo.LoginInfo
import com.daqsoft.library_base.pojo.Profile
import com.daqsoft.library_common.bean.Employee
import com.daqsoft.mvvmfoundation.base.BaseModel
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

/**
 * @ClassName    MineRepository
 * @Description
 * @Author       yuxc
 * @CreateDate   2021/5/7
 */
class MineRepository  @Inject constructor(private val mineApiService: MineApiService) : BaseModel(),MineApiService {
    override fun chooseCompany(body:  Map<String,String>): Observable<AppResponse<LoginInfo>> {
        return mineApiService.chooseCompany(body)
    }

    override fun getVerifyCode(mobile: String): Observable<AppResponse<Any>> {
        return mineApiService.getVerifyCode(mobile)
    }

    override fun login(body:  Map<String,String>): Observable<AppResponse<Company>> {
        return mineApiService.login(body)
    }

    override fun logout(): Observable<AppResponse<Any>> {
        return mineApiService.logout()
    }

    override fun getUserInfo(): Observable<AppResponse<Profile>> {
        return mineApiService.getUserInfo()
    }

    override fun getEmployeeDetail(id: String): Observable<AppResponse<Employee>> {
        return mineApiService.getEmployeeDetail(id)
    }
}