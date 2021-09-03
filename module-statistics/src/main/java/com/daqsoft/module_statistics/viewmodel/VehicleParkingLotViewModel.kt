package com.daqsoft.module_statistics.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.wrapper.loadsircallback.ErrorCallback
import com.daqsoft.module_statistics.repository.StatisticsRepository
import com.daqsoft.module_statistics.repository.pojo.vo.ParkingLot
import com.daqsoft.module_statistics.repository.pojo.vo.VehicleParkingLot
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
class VehicleParkingLotViewModel  : ToolbarViewModel<StatisticsRepository> {

    @ViewModelInject
    constructor(application: Application, statisticsRepository : StatisticsRepository):super(application,statisticsRepository)


    val parkingLotEvent = SingleLiveEvent<List<ParkingLot>>()
    fun getParkingLot() {
        addSubscribe(
            model
                .getParkingLot()
                .compose (RxUtils.schedulersTransformer())
                .compose (RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<ParkingLot>>() {
                    override fun onSuccess(t: AppResponse<ParkingLot>) {
                        t.datas?.let {
                            parkingLotEvent.value = it
                        }
                    }
                })
        )
    }


    val refreshEvent = SingleLiveEvent<Unit>()
    val vehicleParkingLotEvent = SingleLiveEvent<VehicleParkingLot>()
    fun getVehicleParkingLot(
        timeType: String?,
        statisticsTime: String?,
        endTime: String?,
        quarter: String?,
        carPark: String?,
        companyId: String?,
        ) {
        addSubscribe(
            model
                .getVehicleParkingLot(timeType, statisticsTime, endTime, quarter, carPark, companyId)
                .doOnSubscribe{
                    showLoadingDialog()
                }
                .compose (RxUtils.schedulersTransformer())
                .compose (RxUtils.exceptionTransformer())
                .doFinally{
                    dismissLoadingDialog()
                    refreshEvent.call()
                }
                .subscribeWith(object : AppDisposableObserver<AppResponse<VehicleParkingLot>>() {
                    override fun onSuccess(t: AppResponse<VehicleParkingLot>) {
                        t.data?.let {
                            vehicleParkingLotEvent.value = it
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