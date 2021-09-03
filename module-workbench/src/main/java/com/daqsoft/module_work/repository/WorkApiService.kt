package com.daqsoft.module_work.repository

import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.global.HttpGlobal
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_common.bean.AddressBook
import com.daqsoft.library_common.bean.Alarm
import com.daqsoft.module_work.repository.pojo.dto.*
import com.daqsoft.module_work.repository.pojo.vo.*
import io.reactivex.rxjava3.core.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*


/**
 * @package name：com.daqsoft.module_mine.repository
 * @date 6/11/2020 下午 2:10
 * @author zp
 * @describe
 */
interface WorkApiService {


    /**
     * 获取员工
     */
    @GET("v5/business/address/book/employee")
    fun getEmployee (): Observable<AppResponse<AddressBook>>

    /**
     * 获取部门
     */
    @GET("v5/business/address/book/department")
    fun getOrganization (): Observable<AppResponse<Department>>


    /**
     * 获取告警
     */
    @POST("v5/business/alarm/event/page")
    fun getAlarmList(@Body body: AlarmRequest): Observable<AppResponse<Alarm>>

    /**
     * 获取告警类型 告警状态
     */
    @GET("v5/business/alarm/event/eus")
    fun getAlarmTypeAlarmStatus(): Observable<AppResponse<AlarmTypeAlarmStatus>>


    /**
     * 获取告警详情
     */
    @GET("v5/business/alarm/event/detail")
    fun getAlarmDetails (
        @Query("eid") eid : String,
        @Query("rid") rid : String? = null,
    ): Observable<AppResponse<AlarmDetail>>


    /**
     * 获取告警详情
     */
    @GET("v5/business/alarm/event/operable")
    fun getOperatePurview (
        @Query("evtId") evtId : String
    ): Observable<AppResponse<Any>>

    /**
     * 获取告警详情
     */
    @GET("v5/business/alarm/event/process")
    fun getHandleFlow (
        @Query("evtId") evtId : String
    ): Observable<AppResponse<HandleFlow>>


    /**
     * 告警处理
     */
    @POST("v5/business/alarm/event/handle")
    fun alarmHandle(
        @Body body: AlarmHandleRequest
    ):Observable<AppResponse<Any>>


    /**
     * 获取告警统计
     */
    @GET("v5/business/alarm/event/count")
    fun getAlarmCount(): Observable<AppResponse<AlarmCount>>


    /**
     * 获取补卡列表
     */
    @GET("v5/business/task/summary/page")
    fun getSupplementCardList(
        @Query("currPage") page : Int = ConstantGlobal.INITIAL_PAGE,
        @Query("pageSize") size : Int = ConstantGlobal.INITIAL_PAGE_SIZE,
    ): Observable<AppResponse<Any>>



    /**
     * 获取 视频监控分组
     */
    @GET("v5/business/monitor/mobileMonitor/group")
    fun getVideoSurveillanceGroup(): Observable<AppResponse<VideoSurveillanceGroup>>


    /**
     * 获取 视频监控数量
     */
    @GET("v5/business/monitor/mobileMonitor/status/count")
    fun getVideoSurveillanceCount(): Observable<AppResponse<VideoSurveillanceCount>>



    /**
     * 获取 视频监控列表
     */
    @POST("v5/business/monitor/mobileMonitor/page")
    fun getVideoSurveillanceList(
        @Body body: VideoSurveillanceRequest
    ): Observable<AppResponse<VideoSurveillanceList>>

    /**
     * 获取 事件类型
     */
    @GET("v5/business/alrm/report/typeOptions")
    fun getIncidentType(): Observable<AppResponse<IncidentType>>


    /**
     * 获取 所有景点
     */
    @GET("v5/business/public/spotList")
    fun getAllViewpoint(): Observable<AppResponse<Viewpoint>>


    /**
     *  上报事件
     */
    @POST("v5/business/alrm/report/save")
    fun postIncidentReport(
        @Body body: IncidentReportRequest
    ): Observable<AppResponse<Any>>

    /**
     *  获取 事件列表
     */
    @POST("v5/business/alrm/report/page")
    fun getIncidentList(
        @Body body: IncidentListRequest
    ): Observable<AppResponse<Incident>>


    /**
     * 获取 事件状态
     */
    @GET("v5/business/alrm/report/statusOptions")
    fun getIncidentStatus(): Observable<AppResponse<IncidentStatus>>


    /**
     * 获取 事件详情
     */
    @GET("v5/business/alrm/report/dtl")
    fun getIncidentDetail(
        @Query("id") id : String
    ): Observable<AppResponse<Incident>>


    /**
     * 获取 打卡准备
     */
    @GET("v5/clock/in/clock/in/ready")
    fun getClockInfo(
        @Query("location") location : String
    ): Observable<AppResponse<ClockInfo>>


    /**
     * 获取 打卡记录
     */
    @GET("v5/clock/in/clocked/in/record")
    fun getClockRecord(
        @Query("date") date : String
    ): Observable<AppResponse<ClockInfo>>


    /**
     *  打卡
     */
    @POST("v5/clock/in/clock/in")
    fun postClockIn(
        @Body body: ClockInfoRequest
    ): Observable<AppResponse<Map<String,String>>>


    /**
     *  更新打卡
     */
    @GET("v5/clock/in/clocked/in/record/upd")
    fun renewClockIn(
        @Query("clockedInId") clockedInId : String
    ): Observable<AppResponse<Any>>


    /**
     *  获取当前考情分组
     */
    @GET("v5/business/working/user-schedule/current-group")
    fun getRosterGroup(): Observable<AppResponse<RosterGroup>>

    /**
     *  获取个人排班日历
     */
    @GET("v5/business/working/user-schedule/person")
    fun getPersonalRoster(
        @Query("month") month : String
    ): Observable<AppResponse<PersonalRoster>>

    /**
     *  获取 月度考勤汇总
     */
    @GET("v5/business/statistics/working/month")
    fun getAttendanceMonthlyStatistics(
        @Query("month") month : String
    ): Observable<AppResponse<MonthlyStatistics>>

    /**
     *  获取 打卡月历
     */
    @GET("v5/business/statistics/working/clock-in/month/date-list")
    fun getAttendanceMonthlyCalendar(
        @Query("month") month : String
    ): Observable<AppResponse<MonthlyCalendar>>


    /**
     *  获取 打卡月历 天详情
     */
    @GET("v5/business/statistics/working/clock-in/date-detail")
    fun getAttendanceDetail(
        @Query("workDate") workDate : String
    ): Observable<AppResponse<AttendanceDetail>>
}