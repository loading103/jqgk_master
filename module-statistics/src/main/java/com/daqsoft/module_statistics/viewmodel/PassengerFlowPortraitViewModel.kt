package com.daqsoft.module_statistics.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.wrapper.loadsircallback.ErrorCallback
import com.daqsoft.module_statistics.repository.StatisticsRepository
import com.daqsoft.module_statistics.repository.pojo.vo.PassengerFlowPortrait
import com.daqsoft.mvvmfoundation.toolbar.ToolbarViewModel
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.daqsoft.mvvmfoundation.utils.SingleLiveEvent
import com.kingja.loadsir.callback.SuccessCallback

/**
 * @package name：com.daqsoft.module_statistics.viewmodel
 * @date 18/6/2021 上午 10:08
 * @author zp
 * @describe
 */
class PassengerFlowPortraitViewModel  : ToolbarViewModel<StatisticsRepository> {

    @ViewModelInject
    constructor(application: Application, statisticsRepository : StatisticsRepository):super(application,statisticsRepository)


    val refreshEvent = SingleLiveEvent<Unit>()
    val portraitEvent = SingleLiveEvent<PassengerFlowPortrait>()
    fun getPassengerFlowPortrait(timeType: String?,
                                   statisticsTime: String?,
                                   endTime: String?,
                                   quarter: String?,
                                   companyId : String?, ) {
        addSubscribe(
            model
                .getPassengerFlowPortrait(timeType, statisticsTime, endTime, quarter,companyId)
                .doOnSubscribe{
                    showLoadingDialog()
                }
                .compose (RxUtils.schedulersTransformer())
                .compose (RxUtils.exceptionTransformer())
                .doFinally{
                    dismissLoadingDialog()
                    refreshEvent.call()
                }
                .subscribeWith(object : AppDisposableObserver<AppResponse<PassengerFlowPortrait>>() {
                    override fun onSuccess(t: AppResponse<PassengerFlowPortrait>) {
                        t.data?.let {
                            portraitEvent.value = it
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