package com.daqsoft.module_statistics.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.wrapper.loadsircallback.ErrorCallback
import com.daqsoft.module_statistics.repository.StatisticsRepository
import com.daqsoft.module_statistics.repository.pojo.vo.TicketType
import com.daqsoft.module_statistics.repository.pojo.vo.TicketTypeSingle
import com.daqsoft.mvvmfoundation.toolbar.ToolbarViewModel
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.daqsoft.mvvmfoundation.utils.SingleLiveEvent
import com.kingja.loadsir.callback.SuccessCallback

/**
 * @package name：com.daqsoft.module_statistics.viewmodel
 * @date 16/6/2021 上午 11:00
 * @author zp
 * @describe
 */
class TicketTypeViewModel  : ToolbarViewModel<StatisticsRepository> {

    @ViewModelInject
    constructor(application: Application, statisticsRepository : StatisticsRepository):super(application,statisticsRepository)


    val refreshEvent = SingleLiveEvent<Unit>()
    val typeEvent = SingleLiveEvent<TicketType>()
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
                            typeEvent.value = it
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


    val typeSingleEvent = SingleLiveEvent<TicketTypeSingle>()
    fun getTicketTypeSingle(timeType: String?,
                            statisticsTime: String?,
                            endTime: String?,
                            quarter: String?,
                            name: String?, ) {
        addSubscribe(
            model
                .getTicketTypeSingle(timeType, statisticsTime, endTime, quarter,name)
                .doOnSubscribe{
                    showLoadingDialog()
                }
                .compose (RxUtils.schedulersTransformer())
                .compose (RxUtils.exceptionTransformer())
                .doFinally{
                    dismissLoadingDialog()
                    refreshEvent.call()
                }
                .subscribeWith(object : AppDisposableObserver<AppResponse<TicketTypeSingle>>() {
                    override fun onSuccess(t: AppResponse<TicketTypeSingle>) {
                        t.data?.let {
                            typeSingleEvent.value = it
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