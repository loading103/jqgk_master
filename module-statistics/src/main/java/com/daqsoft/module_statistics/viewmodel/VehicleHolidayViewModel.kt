package com.daqsoft.module_statistics.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.wrapper.loadsircallback.ErrorCallback
import com.daqsoft.module_statistics.repository.StatisticsRepository
import com.daqsoft.module_statistics.repository.pojo.vo.HolidayMenu
import com.daqsoft.module_statistics.repository.pojo.vo.VehicleHoliday
import com.daqsoft.mvvmfoundation.toolbar.ToolbarViewModel
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.daqsoft.mvvmfoundation.utils.SingleLiveEvent
import com.kingja.loadsir.callback.SuccessCallback

/**
 * @package name：com.daqsoft.module_statistics.viewmodel
 * @date 21/6/2021 下午 2:20
 * @author zp
 * @describe
 */
class VehicleHolidayViewModel  : ToolbarViewModel<StatisticsRepository> {

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
    val vehicleHolidayEvent = SingleLiveEvent<VehicleHoliday>()
    fun getVehicleHoliday(
        timeType: String?,
        statisticsTime: String?,
        endTime: String?,
        quarter: String?,
        holidayId: String?
    ) {
        addSubscribe(
            model
                .getVehicleHoliday(
                    timeType,
                    statisticsTime,
                    endTime,
                    quarter,
                    holidayId
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
                .subscribeWith(object : AppDisposableObserver<AppResponse<VehicleHoliday>>() {
                    override fun onSuccess(t: AppResponse<VehicleHoliday>) {
                        t.data?.let {
                            vehicleHolidayEvent.value = it
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