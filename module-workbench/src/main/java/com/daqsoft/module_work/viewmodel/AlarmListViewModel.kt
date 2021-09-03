package com.daqsoft.module_work.viewmodel

import android.app.Application
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.hilt.lifecycle.ViewModelInject
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.paging.Paging2Utils
import com.daqsoft.library_base.wrapper.loadsircallback.EmptyCallback
import com.daqsoft.library_base.wrapper.loadsircallback.ErrorCallback
import com.daqsoft.library_common.bean.Alarm
import com.daqsoft.module_work.BR
import com.daqsoft.module_work.R
import com.daqsoft.module_work.repository.WorkRepository
import com.daqsoft.module_work.repository.pojo.dto.AlarmRequest
import com.daqsoft.module_work.repository.pojo.vo.AlarmTypeAlarmStatus
import com.daqsoft.module_work.viewmodel.itemviewmodel.AlarmListItemViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.toolbar.ToolbarViewModel
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.daqsoft.mvvmfoundation.utils.SingleLiveEvent
import com.kingja.loadsir.callback.SuccessCallback
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.module_work.viewmodel
 * @date 25/5/2021 上午 10:01
 * @author zp
 * @describe
 */
class AlarmListViewModel : ToolbarViewModel<WorkRepository> , Paging2Utils<ItemViewModel<*>> {

    @ViewModelInject
    constructor(application: Application, workRepository: WorkRepository):super(application, workRepository)

    // 请求参数
    lateinit var alarmRequest : AlarmRequest


    /**
     * 给RecyclerView添加ItemBinding
     */
    var itemBinding: ItemBinding<ItemViewModel<*>> = ItemBinding.of(BR.viewModel, R.layout.recycleview_alarm_list_item)

    /**
     * 分页 差分器
     */
    var diff = createDiff()

    /**
     * 分页 数据监听
     */
    var pageList = createPagedList()

    /**
     * 分页 数据源
     */
    var dataSource : DataSource<Int, ItemViewModel<*>>?= null
    override fun createDataSource(): DataSource<Int, ItemViewModel<*>> {
        dataSource = object : PageKeyedDataSource<Int, ItemViewModel<*>>(){
            override fun loadInitial(
                params: LoadInitialParams<Int>,
                callback: LoadInitialCallback<Int, ItemViewModel<*>>
            ) {
                addSubscribe(
                    model
                        .getAlarmList(alarmRequest)
                        .compose (RxUtils.schedulersTransformer())
                        .compose (RxUtils.exceptionTransformer())
                        .subscribeWith(object : AppDisposableObserver<AppResponse<Alarm>>() {
                            override fun onSuccess(t: AppResponse<Alarm>) {
                                t.datas?.let {
                                    if (it.isEmpty()){
                                        loadSirLiveEvent.value = EmptyCallback::class.java
                                        return
                                    }
                                    val observableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()
                                    it.forEach {
                                        observableList.add(
                                            AlarmListItemViewModel(
                                                this@AlarmListViewModel,
                                                it,
                                                alarmRequest.t
                                            )
                                        )
                                    }
                                    callback.onResult(observableList, alarmRequest.currPage, alarmRequest.currPage + 1)
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

            override fun loadAfter(
                params: LoadParams<Int>,
                callback: LoadCallback<Int, ItemViewModel<*>>
            ) {
                alarmRequest.currPage = params.key
                addSubscribe(
                    model
                        .getAlarmList(alarmRequest)
                        .compose (RxUtils.schedulersTransformer())
                        .compose (RxUtils.exceptionTransformer())
                        .subscribeWith(object : AppDisposableObserver<AppResponse<Alarm>>() {
                            override fun onSuccess(t: AppResponse<Alarm>) {
                                t.datas?.let {
                                    if (it.isEmpty()){
                                        return
                                    }
                                    val observableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()
                                    it.forEach {
                                        observableList.add(
                                            AlarmListItemViewModel(
                                                this@AlarmListViewModel,
                                                it,
                                                alarmRequest.t
                                            )
                                        )
                                    }
                                    callback.onResult(observableList,alarmRequest.currPage + 1)
                                }
                            }
                        })
                )
            }

            override fun loadBefore(
                params: LoadParams<Int>,
                callback: LoadCallback<Int, ItemViewModel<*>>
            ) {
            }
        }
        return dataSource!!
    }



    val typeStatusEvent = SingleLiveEvent<AlarmTypeAlarmStatus>()
    fun getAlarmTypeAndStatus(){
        model
            .getAlarmTypeAlarmStatus()
            .compose (RxUtils.schedulersTransformer())
            .compose (RxUtils.exceptionTransformer())
            .subscribeWith(object : AppDisposableObserver<AppResponse<AlarmTypeAlarmStatus>>() {
                override fun onSuccess(t: AppResponse<AlarmTypeAlarmStatus>) {
                    t.data?.let {
                        typeStatusEvent.value = it
                    }
                }
            })
    }
}