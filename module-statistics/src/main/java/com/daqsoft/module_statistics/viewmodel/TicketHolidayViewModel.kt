package com.daqsoft.module_statistics.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.wrapper.loadsircallback.ErrorCallback
import com.daqsoft.module_statistics.repository.StatisticsRepository
import com.daqsoft.module_statistics.repository.pojo.vo.HolidayOption
import com.daqsoft.module_statistics.repository.pojo.vo.TicketHoliday
import com.daqsoft.module_statistics.repository.pojo.vo.TicketHolidaySingle
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
class TicketHolidayViewModel  : ToolbarViewModel<StatisticsRepository> {

    @ViewModelInject
    constructor(application: Application, statisticsRepository : StatisticsRepository):super(application,statisticsRepository)




    val holidayEvent = SingleLiveEvent<List<HolidayOption>>()
    fun getHoliday(statisticsTime : String?) {
        addSubscribe(
            model
                .getHolidayOptions(statisticsTime)
                .compose (RxUtils.schedulersTransformer())
                .compose (RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<HolidayOption>>() {
                    override fun onSuccess(t: AppResponse<HolidayOption>) {
                        t.datas?.let {
                            holidayEvent.value = it
                        }
                    }
                })
        )
    }


    val refreshEvent = SingleLiveEvent<Unit>()
    val ticketHolidayEvent = SingleLiveEvent<TicketHoliday>()
    fun getTicketHoliday(timeType: String?,
                         statisticsTime: String?,
                         endTime: String?,
                         quarter: String?) {
        addSubscribe(
            model
                .getTicketHoliday(timeType, statisticsTime, endTime, quarter)
                .doOnSubscribe{
                    showLoadingDialog()
                }
                .compose (RxUtils.schedulersTransformer())
                .compose (RxUtils.exceptionTransformer())
                .doFinally{
                    dismissLoadingDialog()
                    refreshEvent.call()
                }
                .subscribeWith(object : AppDisposableObserver<AppResponse<TicketHoliday>>() {
                    override fun onSuccess(t: AppResponse<TicketHoliday>) {
                        t.data?.let {
                            ticketHolidayEvent.value = it
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


    val ticketHolidaySingleEvent = SingleLiveEvent<TicketHolidaySingle>()
    fun getTicketHolidaySingle(timeType: String?,
                               statisticsTime: String?,
                               endTime: String?,
                               quarter: String?,
                               holiday: String?, ) {
        addSubscribe(
            model
                .getTicketHolidaySingle(timeType, statisticsTime, endTime, quarter,holiday)
                .doOnSubscribe{
                    showLoadingDialog()
                }
                .compose (RxUtils.schedulersTransformer())
                .compose (RxUtils.exceptionTransformer())
                .doFinally{
                    dismissLoadingDialog()
                    refreshEvent.call()
                }
                .subscribeWith(object : AppDisposableObserver<AppResponse<TicketHolidaySingle>>() {
                    override fun onSuccess(t: AppResponse<TicketHolidaySingle>) {
                        t.data?.let {
                            ticketHolidaySingleEvent.value = it
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