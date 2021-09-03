package com.daqsoft.module_work.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_common.bean.Alarm
import com.daqsoft.module_work.repository.WorkRepository
import com.daqsoft.module_work.repository.pojo.vo.AlarmCount
import com.daqsoft.mvvmfoundation.toolbar.ToolbarViewModel
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.daqsoft.mvvmfoundation.utils.SingleLiveEvent

/**
 * @package name：com.daqsoft.module_work.viewmodel
 * @date 25/5/2021 上午 10:01
 * @author zp
 * @describe
 */
class AlarmViewModel : ToolbarViewModel<WorkRepository> {

    @ViewModelInject
    constructor(application: Application, workRepository: WorkRepository):super(application, workRepository)



    val refreshEvent : SingleLiveEvent<List<AlarmCount>> by lazy { SingleLiveEvent<List<AlarmCount>>() }
    val countEvent = SingleLiveEvent<List<AlarmCount>>()
    fun getAlarmCount(refresh : Boolean = false){
        addSubscribe(
            model
                .getAlarmCount()
                .compose (RxUtils.schedulersTransformer())
                .compose (RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<AlarmCount>>() {
                    override fun onSuccess(t: AppResponse<AlarmCount>) {
                        t.datas?.let {
                            if(refresh){
                                refreshEvent.value = it
                            }else{
                                countEvent.value = it
                            }
                        }
                    }

                    override fun onFail(e: Throwable) {
                        super.onFail(e)
                        if(refresh){
                            refreshEvent.value = arrayListOf()
                        }else{
                            countEvent.value = arrayListOf()
                        }
                    }

                })
        )
    }
}