package com.daqsoft.module_statistics.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.wrapper.loadsircallback.ErrorCallback
import com.daqsoft.module_statistics.repository.StatisticsRepository
import com.daqsoft.module_statistics.repository.pojo.vo.PassengerFlowAttractionPreference
import com.daqsoft.module_statistics.repository.pojo.vo.TicketType
import com.daqsoft.module_statistics.repository.pojo.vo.VehicleParkingLot
import com.daqsoft.module_statistics.repository.pojo.vo.VehicleSource
import com.daqsoft.mvvmfoundation.toolbar.ToolbarViewModel
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.daqsoft.mvvmfoundation.utils.SingleLiveEvent
import com.kingja.loadsir.callback.SuccessCallback

/**
 * @package name：com.daqsoft.module_statistics.viewmodel
 * @date 17/6/2021 上午 9:23
 * @author zp
 * @describe
 */
class BarChartFullScreenViewModel: ToolbarViewModel<StatisticsRepository> {

    @ViewModelInject
    constructor(application: Application, statisticsRepository: StatisticsRepository) : super(
        application,
        statisticsRepository
    )

    override fun onCreate() {
        super.onCreate()
    }

    val refreshEvent = SingleLiveEvent<Unit>()


    val ticketTypeEvent = SingleLiveEvent<TicketType>()
    fun getTicketType(timeType: String?,
                      statisticsTime: String?,
                      endTime: String?,
                      quarter: String?) {
        addSubscribe(
            model
                .getTicketType(timeType, statisticsTime, endTime, quarter)
                .doOnSubscribe{
                    showLoadingDialog()
                }
                .compose (RxUtils.schedulersTransformer())
                .compose (RxUtils.exceptionTransformer())
                .doFinally{
                    dismissLoadingDialog()
                    refreshEvent.call()
                }
                .subscribeWith(object : AppDisposableObserver<AppResponse<TicketType>>() {
                    override fun onSuccess(t: AppResponse<TicketType>) {
                        t.data?.let {
                            ticketTypeEvent.value = it
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


    val attractionPreferenceEvent = SingleLiveEvent<PassengerFlowAttractionPreference>()
    fun getPassengerFlowAttractionPreference(
        timeType: String?,
        statisticsTime: String?,
        endTime: String?,
        quarter: String?,
        spot: String?,
        companyId : String?)
    {
        addSubscribe(
            model
                .getPassengerFlowAttractionPreference(timeType, statisticsTime, endTime, quarter, spot, companyId)
                .doOnSubscribe{
                    showLoadingDialog()
                }
                .compose (RxUtils.schedulersTransformer())
                .compose (RxUtils.exceptionTransformer())
                .doFinally{
                    dismissLoadingDialog()
                    refreshEvent.call()
                }
                .subscribeWith(object : AppDisposableObserver<AppResponse<PassengerFlowAttractionPreference>>() {
                    override fun onSuccess(t: AppResponse<PassengerFlowAttractionPreference>) {
                        t.data?.let {
                            attractionPreferenceEvent.value = it
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

    val vehicleSourceEvent = SingleLiveEvent<List<VehicleSource>>()
    fun getVehicleSource(
        timeType: String?,
        statisticsTime: String?,
        endTime: String?,
        quarter: String?,
        source : String?,
        city: String?
    ) {
        addSubscribe(
            model
                .getVehicleSource(timeType,statisticsTime,endTime,quarter,source,city)
                .doOnSubscribe{
                    showLoadingDialog()
                }
                .compose (RxUtils.schedulersTransformer())
                .compose (RxUtils.exceptionTransformer())
                .doFinally{
                    dismissLoadingDialog()
                    refreshEvent.call()
                }
                .subscribeWith(object : AppDisposableObserver<AppResponse<VehicleSource>>() {
                    override fun onSuccess(t: AppResponse<VehicleSource>) {
                        t.datas?.let {
                            vehicleSourceEvent.value = it
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