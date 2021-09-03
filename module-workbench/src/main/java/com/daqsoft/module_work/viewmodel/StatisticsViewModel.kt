package com.daqsoft.module_work.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.wrapper.loadsircallback.ErrorCallback
import com.daqsoft.module_work.R
import com.daqsoft.module_work.repository.WorkRepository
import com.daqsoft.module_work.repository.pojo.dto.AttendanceStatisticCollect
import com.daqsoft.module_work.repository.pojo.vo.MonthlyStatistics
import com.daqsoft.mvvmfoundation.base.BaseViewModel
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.daqsoft.mvvmfoundation.utils.SingleLiveEvent
import com.kingja.loadsir.callback.SuccessCallback

/**
 * @package name：com.daqsoft.module_work.viewmodel
 * @date 11/5/2021 上午 11:13
 * @author zp
 * @describe 统计 viewModel
 */
class StatisticsViewModel : BaseViewModel<WorkRepository> {

    @ViewModelInject
    constructor(application: Application, workRepository: WorkRepository) : super(
        application,
        workRepository
    )



    val statistics = SingleLiveEvent<List<AttendanceStatisticCollect>>()
    fun getMonthlyStatistics(month : String){

        val resources = getApplication<Application>().resources

        addSubscribe(
            model
                .getAttendanceMonthlyStatistics(month)
                .compose (RxUtils.schedulersTransformer())
                .compose (RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<MonthlyStatistics>>() {
                    override fun onSuccess(t: AppResponse<MonthlyStatistics>) {
                        t.data?.let {


                            val attendanceDays = AttendanceStatisticCollect(
                                "出勤天数",
                                "${it.attendanceDateNum}天",
                                resources.getColor(R.color.color_333333),resources.getColor(R.color.color_59abff),
                                it.attendanceDateList.map { "${it.workDate}  ${it.weekDesc}" }
                            )
                            val averageWorkingHours = AttendanceStatisticCollect(
                                "平均工时",
                                "${it.averageWorkHours}小时",
                                resources.getColor(R.color.color_333333),resources.getColor(R.color.color_59abff),
                                arrayListOf("${it.realWorkHours}小时",)
                            )

                            val normalDays = AttendanceStatisticCollect(
                                "正常天数",
                                "${it.normalDateNum}天",
                                resources.getColor(R.color.color_333333),resources.getColor(R.color.color_59abff),
                                it.normalDateList.map { "${it.workDate}  ${it.weekDesc}" }
                            )

                            val daysOff = AttendanceStatisticCollect(
                                "休息天数",
                                "${it.restDateNum}天",
                                resources.getColor(R.color.color_333333),resources.getColor(R.color.color_59abff),
                                it.restDateList.map { "${it.workDate}  ${it.weekDesc}" }
                            )

                            val arriveLate = AttendanceStatisticCollect(
                                "迟到",
                                "${it.lateNum}次",
                                resources.getColor(R.color.color_ff5757),resources.getColor(R.color.color_ff5757),
                                it.lateDateList.map { "${it.workDate}  ${it.weekDesc}" },
                                it.lateDateList.map { "${it.minutes}分钟" },
                            )

                            val leaveEarly = AttendanceStatisticCollect(
                                "早退",
                                "${it.leaveEarlyNum}次",
                                resources.getColor(R.color.color_ff5757),resources.getColor(R.color.color_ff5757),
                                it.leaveEarlyDateList.map { "${it.workDate}  ${it.weekDesc}" },
                                it.leaveEarlyDateList.map { "${it.minutes}分钟" },
                            )

                            val missingCard = AttendanceStatisticCollect(
                                "缺卡",
                                "${it.missNum}次",
                                resources.getColor(R.color.color_ff5757),resources.getColor(R.color.color_ff5757),
                                it.missDateList.map { "${it.workDate}  ${it.weekDesc}" },
                            )

                            val askForLeave = AttendanceStatisticCollect(
                                "请假",
                                "${it.leaveDateNum}次",
                                resources.getColor(R.color.color_fe7800),resources.getColor(R.color.color_fe7800),
                                it.leaveDateList.map { "${it.workDate}  ${it.weekDesc}" },
                            )

                            val absenteeism = AttendanceStatisticCollect(
                                "缺卡",
                                "${it.absenteeismDateNum}次",
                                resources.getColor(R.color.color_ff5757),resources.getColor(R.color.color_ff5757),
                                it.absenteeismDateList.map { "${it.workDate}  ${it.weekDesc}" },
                            )

                            val data = (arrayListOf(attendanceDays,averageWorkingHours,normalDays,daysOff,arriveLate,leaveEarly,missingCard,askForLeave,absenteeism))

                            statistics.value = data
                            loadSirLiveEvent.value = SuccessCallback::class.java
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        loadSirLiveEvent.value = ErrorCallback::class.java
                    }
                })
        )
    }
}