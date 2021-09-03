package com.daqsoft.module_work.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.wrapper.loadsircallback.ErrorCallback
import com.daqsoft.module_work.repository.WorkRepository
import com.daqsoft.module_work.repository.pojo.vo.Incident
import com.daqsoft.mvvmfoundation.toolbar.ToolbarViewModel
import com.daqsoft.mvvmfoundation.utils.RxUtils

/**
 * @package name：com.daqsoft.module_work.viewmodel
 * @date 26/7/2021 上午 11:17
 * @author zp
 * @describe
 */
class IncidentDetailViewModel: ToolbarViewModel<WorkRepository> {

    @ViewModelInject
    constructor(application: Application, workRepository: WorkRepository) : super(
        application,
        workRepository
    )

    override fun onCreate() {
        super.onCreate()
        initToolbar()
    }

    private fun initToolbar() {
        setTitleText("事件上报")
    }


    var detailObservable = ObservableField<Incident>()
    fun getIncidentDetail(id:String){
        addSubscribe(
            model
                .getIncidentDetail(id)
                .compose (RxUtils.schedulersTransformer())
                .compose (RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<Incident>>() {
                    override fun onSuccess(t: AppResponse<Incident>) {
                        t.data?.let {
                            detailObservable.set(it)
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        loadSirLiveEvent.value = ErrorCallback::class.java
                    }
                })
        )
    }
}