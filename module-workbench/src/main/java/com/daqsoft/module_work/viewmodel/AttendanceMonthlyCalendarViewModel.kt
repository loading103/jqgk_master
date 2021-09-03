package com.daqsoft.module_work.viewmodel

import android.app.Application
import android.graphics.Color
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.wrapper.loadsircallback.ErrorCallback
import com.daqsoft.module_work.repository.WorkRepository
import com.daqsoft.module_work.repository.pojo.vo.AttendanceDetail
import com.daqsoft.module_work.repository.pojo.vo.ClockInfo
import com.daqsoft.module_work.repository.pojo.vo.MonthlyCalendar
import com.daqsoft.module_work.warrper.NotJoinedCallback
import com.daqsoft.module_work.warrper.RosterEmptyCallback
import com.daqsoft.mvvmfoundation.toolbar.ToolbarViewModel
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.daqsoft.mvvmfoundation.utils.SingleLiveEvent
import com.kingja.loadsir.callback.SuccessCallback

/**
 * @package name：com.daqsoft.module_work.viewmodel
 * @date 5/7/2021 上午 9:50
 * @author zp
 * @describe
 */
class AttendanceMonthlyCalendarViewModel  : ToolbarViewModel<WorkRepository> {

    @ViewModelInject
    constructor(application: Application, workRepository: WorkRepository):super(application, workRepository)

    override fun onCreate() {
        super.onCreate()
        initToolbar()
    }

    private fun initToolbar() {
        setBackground(Color.WHITE)
        setTitleText("打卡月历")
    }



    val calendarEvent = SingleLiveEvent<List<MonthlyCalendar>>()
    fun getAttendanceMonthlyCalendar(month: String){
        addSubscribe(
            model
                .getAttendanceMonthlyCalendar(month)
                .compose (RxUtils.schedulersTransformer())
                .compose (RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<MonthlyCalendar>>() {
                    override fun onSuccess(t: AppResponse<MonthlyCalendar>) {
                        t.datas?.let {
                            calendarEvent.value = it
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)

                    }
                })
        )
    }


    val detail = SingleLiveEvent<AttendanceDetail>()
    fun getAttendanceDetail(workDate: String){
        addSubscribe(
            model
                .getAttendanceDetail(workDate)
                .compose (RxUtils.schedulersTransformer())
                .compose (RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<AttendanceDetail>>() {
                    override fun onSuccess(t: AppResponse<AttendanceDetail>) {
                        t.data?.let {
                            detail.value = it
                        }
                    }
                })
        )
    }



}