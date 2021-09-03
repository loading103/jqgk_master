package com.daqsoft.module_task.viewmodel

import android.app.Application
import android.graphics.Color
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.hilt.lifecycle.ViewModelInject
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.paging.Paging2Utils
import com.daqsoft.library_base.wrapper.loadsircallback.EmptyCallback
import com.daqsoft.library_base.wrapper.loadsircallback.ErrorCallback
import com.daqsoft.module_task.BR
import com.daqsoft.module_task.R
import com.daqsoft.module_task.repository.TaskRepository
import com.daqsoft.module_task.repository.pojo.vo.Task
import com.daqsoft.module_task.repository.pojo.vo.WeeklySummary
import com.daqsoft.module_task.viewmodel.itemviewmodel.WeeklySummaryListItemViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.toolbar.ToolbarViewModel
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.kingja.loadsir.callback.SuccessCallback
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.module_task.viewmodel
 * @date 13/5/2021 下午 5:39
 * @author zp
 * @describe 每周总结列表 viewModel
 */
class WeeklySummaryListViewModel : ToolbarViewModel<TaskRepository>, Paging2Utils<WeeklySummary> {

    @ViewModelInject
    constructor(application: Application,taskRepository: TaskRepository):super(application, taskRepository)

    override fun onCreate() {
        super.onCreate()

        initToolbar()
    }

    private fun initToolbar() {
        setBackground(Color.WHITE)
        setTitleText("任务")
    }


    /**
     * 给RecyclerView添加ItemBinding
     */
    var itemBinding: ItemBinding<WeeklySummary> = ItemBinding.of(ItemBinding.VAR_NONE, R.layout.recycleview_weekly_summary_list_item)

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
    var dataSource : DataSource<Int, WeeklySummary>?= null
    override fun createDataSource(): DataSource<Int, WeeklySummary> {
        dataSource = object : PageKeyedDataSource<Int, WeeklySummary>(){
            override fun loadInitial(
                params: LoadInitialParams<Int>,
                callback: LoadInitialCallback<Int, WeeklySummary>
            ) {
                addSubscribe(
                    model
                        .getWeeklySummaryList()
                        .compose (RxUtils.schedulersTransformer())
                        .compose (RxUtils.exceptionTransformer())
                        .subscribeWith(object : AppDisposableObserver<AppResponse<WeeklySummary>>() {
                            override fun onSuccess(t: AppResponse<WeeklySummary>) {
                                t.datas?.let {
                                    if (it.isEmpty()){
                                        loadSirLiveEvent.value = EmptyCallback::class.java
                                        return
                                    }
                                    callback.onResult(it, ConstantGlobal.INITIAL_PAGE, ConstantGlobal.INITIAL_PAGE+1)
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
                callback: LoadCallback<Int, WeeklySummary>
            ) {
                addSubscribe(
                    model
                        .getWeeklySummaryList(page = params.key)
                        .compose (RxUtils.schedulersTransformer())
                        .compose (RxUtils.exceptionTransformer())
                        .subscribeWith(object : AppDisposableObserver<AppResponse<WeeklySummary>>() {
                            override fun onSuccess(t: AppResponse<WeeklySummary>) {
                                t.datas?.let {
                                    if (it.isEmpty()){
                                        return
                                    }
                                    callback.onResult(it,params.key+1)
                                }
                            }
                        })
                )
            }

            override fun loadBefore(
                params: LoadParams<Int>,
                callback: LoadCallback<Int, WeeklySummary>
            ) {
            }
        }
        return dataSource!!
    }
}