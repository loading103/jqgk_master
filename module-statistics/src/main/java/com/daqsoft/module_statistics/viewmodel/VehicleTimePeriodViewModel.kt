package com.daqsoft.module_statistics.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.wrapper.loadsircallback.ErrorCallback
import com.daqsoft.module_statistics.repository.StatisticsRepository
import com.daqsoft.module_statistics.repository.pojo.vo.VehicleTimePeriod
import com.daqsoft.mvvmfoundation.toolbar.ToolbarViewModel
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.daqsoft.mvvmfoundation.utils.SingleLiveEvent
import com.kingja.loadsir.callback.SuccessCallback

/**
 * @package name：com.daqsoft.module_statistics.viewmodel
 * @date 10/6/2021 上午 10:01
 * @author zp
 * @describe
 */
class VehicleTimePeriodViewModel  : ToolbarViewModel<StatisticsRepository> {

    @ViewModelInject
    constructor(application: Application, statisticsRepository : StatisticsRepository):super(application,statisticsRepository)


    val refreshEvent = SingleLiveEvent<Unit>()
    val timePeriodEvent = SingleLiveEvent<VehicleTimePeriod>()
    fun getVehicleTimePeriod(
        timeType: String?,
        statisticsTime: String?,
        endTime: String?,
        quarter: String?,
        companyId : String?, )
    {
        addSubscribe(
            model
                .getVehicleTimePeriod(timeType, statisticsTime, endTime, quarter,companyId)
                .doOnSubscribe{
                    showLoadingDialog()
                }
                .compose (RxUtils.schedulersTransformer())
                .compose (RxUtils.exceptionTransformer())
                .doFinally{
                    dismissLoadingDialog()
                    refreshEvent.call()
                }
                .subscribeWith(object : AppDisposableObserver<AppResponse<VehicleTimePeriod>>() {
                    override fun onSuccess(t: AppResponse<VehicleTimePeriod>) {
                        t.data?.let {
                            timePeriodEvent.value = it
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