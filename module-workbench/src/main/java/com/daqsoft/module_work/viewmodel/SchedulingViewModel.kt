package com.daqsoft.module_work.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.module_work.repository.WorkRepository
import com.daqsoft.module_work.repository.pojo.vo.PersonalRoster
import com.daqsoft.module_work.repository.pojo.vo.RosterGroup
import com.daqsoft.mvvmfoundation.base.BaseViewModel
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.daqsoft.mvvmfoundation.utils.SingleLiveEvent

/**
 * @package name：com.daqsoft.module_work.viewmodel
 * @date 11/5/2021 上午 11:12
 * @author zp
 * @describe 排班 viewModel
 */
class SchedulingViewModel : BaseViewModel<WorkRepository> {

    @ViewModelInject
    constructor(application: Application, workRepository: WorkRepository) : super(
        application,
        workRepository
    )


    val personalRosterEvent = SingleLiveEvent<List<PersonalRoster>>()
    fun getPersonalRoster(month : String){
        addSubscribe(
            model
                .getPersonalRoster(month)
                .compose (RxUtils.schedulersTransformer())
                .compose (RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<PersonalRoster>>() {
                    override fun onSuccess(t: AppResponse<PersonalRoster>) {
                        t.datas?.let {
                            personalRosterEvent.value = it
                        }
                    }
                })
        )
    }



    val rosterGroupObservable = ObservableField<RosterGroup>()
    fun getRosterGroup(){
        addSubscribe(
            model
                .getRosterGroup()
                .compose (RxUtils.schedulersTransformer())
                .compose (RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<RosterGroup>>() {
                    override fun onSuccess(t: AppResponse<RosterGroup>) {
                        t.data?.let {
                            rosterGroupObservable.set(it)
                        }
                    }
                })
        )
    }

}