package com.daqsoft.module_statistics.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.wrapper.loadsircallback.ErrorCallback
import com.daqsoft.module_statistics.repository.StatisticsRepository
import com.daqsoft.module_statistics.repository.pojo.vo.VehicleLengthOfStay
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
class VehicleLengthOfStayViewModel  : ToolbarViewModel<StatisticsRepository> {

    @ViewModelInject
    constructor(application: Application, statisticsRepository : StatisticsRepository):super(application,statisticsRepository)

    val refreshEvent = SingleLiveEvent<Unit>()
    val vehicleLengthOfStayEvent = SingleLiveEvent<VehicleLengthOfStay>()
    fun getVehicleLengthOfStay(
        timeType: String?,
        statisticsTime: String?,
        endTime: String?,
        quarter: String?,
    ) {
        addSubscribe(
            model
                .getVehicleLengthOfStay(timeType,statisticsTime,endTime,quarter)
                .doOnSubscribe{
                    showLoadingDialog()
                }
                .compose (RxUtils.schedulersTransformer())
                .compose (RxUtils.exceptionTransformer())
                .doFinally{
                    dismissLoadingDialog()
                    refreshEvent.call()
                }
                .subscribeWith(object : AppDisposableObserver<AppResponse<VehicleLengthOfStay>>() {
                    override fun onSuccess(t: AppResponse<VehicleLengthOfStay>) {
                        t.data?.let {
                            vehicleLengthOfStayEvent.value = it
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