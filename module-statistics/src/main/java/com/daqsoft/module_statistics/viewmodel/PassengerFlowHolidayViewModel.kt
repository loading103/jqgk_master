package com.daqsoft.module_statistics.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.wrapper.loadsircallback.ErrorCallback
import com.daqsoft.module_statistics.repository.StatisticsRepository
import com.daqsoft.module_statistics.repository.pojo.vo.HolidayMenu
import com.daqsoft.module_statistics.repository.pojo.vo.PassengerFlowHoliday
import com.daqsoft.mvvmfoundation.toolbar.ToolbarViewModel
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.daqsoft.mvvmfoundation.utils.SingleLiveEvent
import com.kingja.loadsir.callback.SuccessCallback

/**
 * @package name：com.daqsoft.module_statistics.viewmodel
 * @date 17/6/2021 下午 1:34
 * @author zp
 * @describe
 */
class PassengerFlowHolidayViewModel  : ToolbarViewModel<StatisticsRepository> {

    @ViewModelInject
    constructor(application: Application, statisticsRepository : StatisticsRepository):super(application,statisticsRepository)


    val holidayEvent = SingleLiveEvent<List<HolidayMenu>>()
    fun getHoliday(
        timeType: String?,
        statisticsTime: String?
    ) {
        addSubscribe(
            model
                .getHolidayMenu(timeType, statisticsTime)
                .compose (RxUtils.schedulersTransformer())
                .compose (RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<HolidayMenu>>() {
                    override fun onSuccess(t: AppResponse<HolidayMenu>) {
                        t.datas?.let {
                            holidayEvent.value = it
                        }
                    }
                })
        )
    }

    val refreshEvent = SingleLiveEvent<Unit>()
    val passengerFlowHolidayEvent = SingleLiveEvent<PassengerFlowHoliday>()
    fun getPassengerFlowHoliday(
        timeType: String?,
        statisticsTime: String?,
        endTime: String?,
        quarter: String?,
        holidayId: String?,
        holidayName: String?
    ) {
        addSubscribe(
            model
                .getPassengerFlowHoliday(
                    timeType,
                    statisticsTime,
                    endTime,
                    quarter,
                    holidayId,
                    holidayName
                )
                .doOnSubscribe{
                    showLoadingDialog()
                }
                .compose (RxUtils.schedulersTransformer())
                .compose (RxUtils.exceptionTransformer())
                .doFinally{
                    dismissLoadingDialog()
                    refreshEvent.call()
                }
                .subscribeWith(object : AppDisposableObserver<AppResponse<PassengerFlowHoliday>>() {
                    override fun onSuccess(t: AppResponse<PassengerFlowHoliday>) {
                        t.data?.let {
                            passengerFlowHolidayEvent.value = it
                        }
                        loadSirLiveEvent.value = SuccessCallback::class.java
                    }

                    override fun onFail(e: Throwable) {
                        super.onFail(e)
                        loadSirLiveEvent.value = ErrorCallback::class.java
                    }
                })
        )
    }

}