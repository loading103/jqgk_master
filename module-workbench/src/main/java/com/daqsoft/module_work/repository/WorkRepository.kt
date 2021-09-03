package com.daqsoft.module_work.repository

import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_common.bean.AddressBook
import com.daqsoft.library_common.bean.Alarm
import com.daqsoft.module_work.repository.pojo.dto.*
import com.daqsoft.module_work.repository.pojo.vo.*
import com.daqsoft.mvvmfoundation.base.BaseModel
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

/**
 * @package name：com.daqsoft.module_mine.repository
 * @date 6/11/2020 下午 2:09
 * @author zp
 * @describe
 */
class WorkRepository @Inject constructor(private val mineApiService: WorkApiService) : BaseModel(),WorkApiService {
    override fun getEmployee(): Observable<AppResponse<AddressBook>> {
        return mineApiService.getEmployee()
    }

    override fun getOrganization(): Observable<AppResponse<Department>> {
        return mineApiService.getOrganization()
    }

    override fun getAlarmList(body: AlarmRequest): Observable<AppResponse<Alarm>> {
        return mineApiService.getAlarmList(body)
    }

    override fun getAlarmTypeAlarmStatus(): Observable<AppResponse<AlarmTypeAlarmStatus>> {
        return mineApiService.getAlarmTypeAlarmStatus()
    }

    override fun getAlarmDetails(eid: String, rid: String?): Observable<AppResponse<AlarmDetail>> {
        return mineApiService.getAlarmDetails(eid,rid)
    }

    override fun getOperatePurview(evtId: String): Observable<AppResponse<Any>> {
        return mineApiService.getOperatePurview(evtId)
    }

    override fun getHandleFlow(evtId: String): Observable<AppResponse<HandleFlow>> {
        return mineApiService.getHandleFlow(evtId)
    }

    override fun alarmHandle(body: AlarmHandleRequest): Observable<AppResponse<Any>> {
        return mineApiService.alarmHandle(body)
    }

    override fun getAlarmCount(): Observable<AppResponse<AlarmCount>> {
        return mineApiService.getAlarmCount()
    }

    override fun getSupplementCardList(page: Int, size: Int): Observable<AppResponse<Any>> {
        return mineApiService.getSupplementCardList(page, size)
    }

    override fun getVideoSurveillanceGroup(): Observable<AppResponse<VideoSurveillanceGroup>> {
        return mineApiService.getVideoSurveillanceGroup()
    }

    override fun getVideoSurveillanceCount(): Observable<AppResponse<VideoSurveillanceCount>> {
        return mineApiService.getVideoSurveillanceCount()
    }

    override fun getVideoSurveillanceList(body: VideoSurveillanceRequest): Observable<AppResponse<VideoSurveillanceList>> {
        return mineApiService.getVideoSurveillanceList(body)
    }

    override fun getIncidentType(): Observable<AppResponse<IncidentType>> {
        return mineApiService.getIncidentType()
    }

    override fun getAllViewpoint(): Observable<AppResponse<Viewpoint>> {
        return mineApiService.getAllViewpoint()
    }

    override fun postIncidentReport(body: IncidentReportRequest): Observable<AppResponse<Any>> {
        return mineApiService.postIncidentReport(body)
    }

    override fun getIncidentList(body: IncidentListRequest): Observable<AppResponse<Incident>> {
        return mineApiService.getIncidentList(body)
    }

    override fun getIncidentStatus(): Observable<AppResponse<IncidentStatus>> {
        return mineApiService.getIncidentStatus()
    }

    override fun getIncidentDetail(id : String): Observable<AppResponse<Incident>> {
        return mineApiService.getIncidentDetail(id)
    }

    override fun getClockInfo(location: String): Observable<AppResponse<ClockInfo>> {
        return mineApiService.getClockInfo(location)
    }

    override fun getClockRecord(date: String): Observable<AppResponse<ClockInfo>> {
        return mineApiService.getClockRecord(date)
    }

    override fun postClockIn(body: ClockInfoRequest): Observable<AppResponse<Map<String,String>>> {
        return mineApiService.postClockIn(body)
    }

    override fun renewClockIn(clockedInId: String): Observable<AppResponse<Any>> {
        return mineApiService.renewClockIn(clockedInId)
    }

    override fun getRosterGroup(): Observable<AppResponse<RosterGroup>> {
        return mineApiService.getRosterGroup()
    }

    override fun getPersonalRoster(month : String): Observable<AppResponse<PersonalRoster>> {
        return mineApiService.getPersonalRoster(month)
    }

    override fun getAttendanceMonthlyStatistics(month: String): Observable<AppResponse<MonthlyStatistics>> {
        return mineApiService.getAttendanceMonthlyStatistics(month)
    }

    override fun getAttendanceMonthlyCalendar(month: String): Observable<AppResponse<MonthlyCalendar>> {
        return mineApiService.getAttendanceMonthlyCalendar(month)
    }

    override fun getAttendanceDetail(workDate: String): Observable<AppResponse<AttendanceDetail>> {
        return mineApiService.getAttendanceDetail(workDate)
    }
}