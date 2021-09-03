package com.daqsoft.module_statistics.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.wrapper.loadsircallback.ErrorCallback
import com.daqsoft.module_statistics.repository.StatisticsRepository
import com.daqsoft.module_statistics.repository.pojo.vo.Attraction
import com.daqsoft.module_statistics.repository.pojo.vo.PassengerFlowAttractionPreference
import com.daqsoft.mvvmfoundation.toolbar.ToolbarViewModel
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.daqsoft.mvvmfoundation.utils.SingleLiveEvent
import com.kingja.loadsir.callback.SuccessCallback

/**
 * @package name：com.daqsoft.module_statistics.viewmodel
 * @date 21/6/2021 上午 10:09
 * @author zp
 * @describe
 */
class PassengerFlowAttractionPreferenceViewModel  : ToolbarViewModel<StatisticsRepository> {

    @ViewModelInject
    constructor(application: Application, statisticsRepository : StatisticsRepository):super(application,statisticsRepository)


    val refreshEvent = SingleLiveEvent<Unit>()
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


    val attraction = SingleLiveEvent<List<Attraction>>()
    fun getAttraction() {
        addSubscribe(
            model
                .getAttraction()
                .compose (RxUtils.schedulersTransformer())
                .compose (RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<Attraction>>() {
                    override fun onSuccess(t: AppResponse<Attraction>) {
                        t.datas?.let {
                            attraction.value = it
                        }
                    }
                })
        )
    }
}