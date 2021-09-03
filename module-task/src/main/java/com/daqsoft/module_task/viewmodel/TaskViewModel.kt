package com.daqsoft.module_task.viewmodel

import android.app.Application
import android.graphics.Color
import android.view.View
import androidx.hilt.lifecycle.ViewModelInject
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_task.R
import com.daqsoft.module_task.repository.TaskRepository
import com.daqsoft.module_task.repository.pojo.vo.TaskCount
import com.daqsoft.mvvmfoundation.toolbar.ToolbarViewModel
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.daqsoft.mvvmfoundation.utils.SingleLiveEvent

class TaskViewModel : ToolbarViewModel<TaskRepository> {

    @ViewModelInject
    constructor(application: Application,taskRepository : TaskRepository):super(application,taskRepository)


    override fun onCreate() {
        super.onCreate()

        initToolbar()
    }

    private fun initToolbar() {
        setBackground(Color.TRANSPARENT)
        setBackIconVisible(View.GONE)
        setTitleText("任务")
        setTitleTextColor(Color.WHITE)

        setRightIconVisible(View.VISIBLE)
        setRightIconSrc(R.mipmap.rw_yzxj)

    }

    override fun rightIconOnClick() {
        super.rightIconOnClick()
        ARouter.getInstance().build(ARouterPath.Task.PAGER_WEEKLY_SUMMARY_LIST).navigation()
    }



    val refreshEvent : SingleLiveEvent<List<TaskCount>> by lazy { SingleLiveEvent<List<TaskCount>>() }
    val countEvent = SingleLiveEvent<List<TaskCount>>()
    fun getTaskCount(refresh : Boolean = false){
        addSubscribe(
            model
                .getTaskCount()
                .compose (RxUtils.schedulersTransformer())
                .compose (RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<TaskCount>>() {
                    override fun onSuccess(t: AppResponse<TaskCount>) {
                        t.datas?.let {
                            if (refresh){
                                refreshEvent.value = it
                            }else{
                                countEvent.value = it
                            }
                        }
                    }

                    override fun onFail(e: Throwable) {
                        super.onFail(e)
                        if (refresh){
                            refreshEvent.value = arrayListOf()
                        }else{
                            countEvent.value = arrayListOf()
                        }
                    }

                })
        )
    }
}