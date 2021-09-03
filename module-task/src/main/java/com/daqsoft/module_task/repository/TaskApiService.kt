package com.daqsoft.module_task.repository

import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_common.bean.AddressBook
import com.daqsoft.library_common.bean.Options
import com.daqsoft.module_task.repository.pojo.dto.TaskRequest
import com.daqsoft.module_task.repository.pojo.vo.Task
import com.daqsoft.module_task.repository.pojo.vo.TaskCount
import com.daqsoft.module_task.repository.pojo.vo.WeeklySummary
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


/**
 * @package name：com.daqsoft.module_mine.repository
 * @date 6/11/2020 下午 2:10
 * @author zp
 * @describe
 */
interface TaskApiService {

    /**
     * 获取员工
     */
    @GET("v5/business/address/book/employee")
    fun getEmployee (): Observable<AppResponse<AddressBook>>


    /**
     * 获取任务列表
     */
    @POST("v5/business/task/mobileTask/page")
    fun getTaskList(
        @Body body: TaskRequest
    ): Observable<AppResponse<Task>>


    /**
     * 获取任务总数
     */
    @GET("v5/business/task/mobileTask/status/count")
    fun getTaskCount (): Observable<AppResponse<TaskCount>>



    /**
     * 获取周总结列表
     */
    @GET("v5/business/task/summary/page")
    fun getWeeklySummaryList(
        @Query("currPage") page : Int = ConstantGlobal.INITIAL_PAGE,
        @Query("pageSize") size : Int = ConstantGlobal.INITIAL_PAGE_SIZE,
    ): Observable<AppResponse<WeeklySummary>>


    /**
     * 获取周总结列表
     */
    @GET("v5/business/task/summary/detail")
    fun getWeeklySummary(
        @Query("id") id : String
    ): Observable<AppResponse<WeeklySummary>>

    /**
     * 获取任务三级联动选项
     */
    @GET("v5/business/task/mobileTask/filter/optional")
    fun getOptions(): Observable<AppResponse<Options>>
}