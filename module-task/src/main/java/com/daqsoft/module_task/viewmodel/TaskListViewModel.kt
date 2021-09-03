package com.daqsoft.module_task.viewmodel

import android.app.Application
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
import com.daqsoft.library_common.bean.Options
import com.daqsoft.module_task.R
import com.daqsoft.module_task.BR
import com.daqsoft.module_task.repository.TaskRepository
import com.daqsoft.module_task.repository.pojo.dto.TaskRequest
import com.daqsoft.module_task.repository.pojo.vo.Task
import com.daqsoft.module_task.viewmodel.itemviewmodel.TaskListItemViewModel
import com.daqsoft.mvvmfoundation.base.BaseViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.daqsoft.mvvmfoundation.utils.SingleLiveEvent
import com.kingja.loadsir.callback.SuccessCallback
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.module_task.viewmodel
 * @date 13/5/2021 上午 10:56
 * @author zp
 * @describe 任务状态列表 itemViewModel
 */
class TaskListViewModel : BaseViewModel<TaskRepository>, Paging2Utils<Task> {

    @ViewModelInject
    constructor(application: Application, taskRepository: TaskRepository) : super(
        application,
        taskRepository
    )



    // 请求参数
    lateinit var taskRequest : TaskRequest

    /**
     * 给RecyclerView添加ItemBinding
     */
    var itemBinding: ItemBinding<Task> = ItemBinding.of(ItemBinding.VAR_NONE, R.layout.recycleview_task_list_item)

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
    var dataSource : DataSource<Int, Task>?= null
    override fun createDataSource(): DataSource<Int, Task> {
        dataSource = object : PageKeyedDataSource<Int, Task>(){
            override fun loadInitial(
                params: LoadInitialParams<Int>,
                callback: LoadInitialCallback<Int, Task>
            ) {
                addSubscribe(
                    model
                        .getTaskList(taskRequest)
                        .compose (RxUtils.schedulersTransformer())
                        .compose (RxUtils.exceptionTransformer())
                        .subscribeWith(object : AppDisposableObserver<AppResponse<Task>>() {
                            override fun onSuccess(t: AppResponse<Task>) {
                                t.datas?.let {
                                    if (it.isEmpty()){
                                        loadSirLiveEvent.value = EmptyCallback::class.java
                                        return
                                    }
                                    callback.onResult(it, taskRequest.currPage, taskRequest.currPage + 1)
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
                callback: LoadCallback<Int, Task>
            ) {
                taskRequest.currPage = params.key
                addSubscribe(
                    model
                        .getTaskList(taskRequest)
                        .compose (RxUtils.schedulersTransformer())
                        .compose (RxUtils.exceptionTransformer())
                        .subscribeWith(object : AppDisposableObserver<AppResponse<Task>>() {
                            override fun onSuccess(t: AppResponse<Task>) {
                                t.datas?.let {
                                    if (it.isEmpty()){
                                        return
                                    }
                                    callback.onResult(it,taskRequest.currPage + 1)
                                }
                            }
                        })
                )
            }

            override fun loadBefore(
                params: LoadParams<Int>,
                callback: LoadCallback<Int, Task>
            ) {
            }
        }
        return dataSource!!
    }



    val optionsEvent = SingleLiveEvent<List<Options>>()
    fun getOptions(){
        addSubscribe(
            model
                .getOptions()
                .compose (RxUtils.schedulersTransformer())
                .compose (RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<Options>>() {
                    override fun onSuccess(t: AppResponse<Options>) {
                        t.datas?.let {
                            if (it.isEmpty()){
                                return
                            }
                            optionsEvent.value = it
                        }
                    }
                })
        )
    }
}