package com.daqsoft.module_task.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import com.blankj.utilcode.util.GsonUtils
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.global.MMKVGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.pojo.LoginInfo
import com.daqsoft.library_base.utils.MMKVUtils
import com.daqsoft.library_base.wrapper.loadsircallback.EmptyCallback
import com.daqsoft.library_base.wrapper.loadsircallback.ErrorCallback
import com.daqsoft.module_task.repository.TaskRepository
import com.daqsoft.module_task.repository.pojo.vo.WeeklySummary
import com.daqsoft.mvvmfoundation.base.BaseViewModel
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.daqsoft.mvvmfoundation.utils.SingleLiveEvent
import com.kingja.loadsir.callback.SuccessCallback

/**
 * @package name：com.daqsoft.module_task.viewmodel
 * @date 13/5/2021 下午 5:39
 * @author zp
 * @describe 每周总结 viewModel
 */
class WeeklySummaryViewModel : BaseViewModel<TaskRepository> {

    @ViewModelInject
    constructor(application: Application,taskRepository: TaskRepository):super(application, taskRepository)

    val avatar = ObservableField<String>()

    override fun onCreate() {
        super.onCreate()

        val loginInfoJson = MMKVUtils.decodeString(MMKVGlobal.LOGIN_INFO)
        if (!loginInfoJson.isNullOrBlank()){
            val loginInfo = GsonUtils.fromJson<LoginInfo>(loginInfoJson, LoginInfo::class.java)
            avatar.set(loginInfo.profile.employee.img)
        }

    }

    val summaryEvent = SingleLiveEvent<WeeklySummary>()
    fun getWeeklySummary(id:String){
        addSubscribe(
            model
                .getWeeklySummary(id)
                .compose (RxUtils.schedulersTransformer())
                .compose (RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<WeeklySummary>>() {
                    override fun onSuccess(t: AppResponse<WeeklySummary>) {
                        t.data?.let {
                            summaryEvent.value = it
                            loadSirLiveEvent.value = SuccessCallback::class.java
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