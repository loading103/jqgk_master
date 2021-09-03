package com.daqsoft.module_work.viewmodel

import android.app.Application
import android.graphics.Color
import android.view.View
import androidx.hilt.lifecycle.ViewModelInject
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.paging.Paging2Utils
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.wrapper.loadsircallback.ErrorCallback
import com.daqsoft.module_work.R
import com.daqsoft.module_work.repository.WorkRepository
import com.daqsoft.mvvmfoundation.toolbar.ToolbarViewModel
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.kingja.loadsir.callback.SuccessCallback
import me.tatarka.bindingcollectionadapter2.ItemBinding

class LeaveListViewModel : ToolbarViewModel<WorkRepository>, Paging2Utils<Any> {

    @ViewModelInject
    constructor(application: Application, workBenchRepository: WorkRepository) : super(application, workBenchRepository)

    var approve : Boolean = false

    override fun onCreate() {
        super.onCreate()
        initToolbar()
    }


    private fun initToolbar() {
        setBackground(Color.WHITE)
        setTitleText("请假申请")
        setRightIconVisible(if (approve) View.GONE else View.VISIBLE)
        setRightIconSrc(R.mipmap.qingjia_xinzeng)
    }

    override fun rightIconOnClick() {
        ARouter.getInstance().build(ARouterPath.Workbench.ADD_LEAVE_APPLY).navigation()
    }


    /**
     * 给RecyclerView添加ItemBinding
     */
    var itemBinding: ItemBinding<Any> = ItemBinding.of(ItemBinding.VAR_NONE, R.layout.recycleview_supplement_card_list_item)

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
    var dataSource: DataSource<Int, Any>? = null
    override fun createDataSource(): DataSource<Int, Any> {
        dataSource = object : PageKeyedDataSource<Int, Any>() {
            override fun loadInitial(
                params: LoadInitialParams<Int>,
                callback: LoadInitialCallback<Int, Any>
            ) {
                addSubscribe(
                    model
                        .getSupplementCardList()
                        .compose(RxUtils.schedulersTransformer())
                        .compose(RxUtils.exceptionTransformer())
                        .subscribeWith(object : AppDisposableObserver<AppResponse<Any>>() {
                            override fun onSuccess(t: AppResponse<Any>) {
//                                t.datas?.let {
//                                    if (it.isEmpty()){
//                                        loadSirLiveEvent.value = EmptyCallback::class.java
//                                        return
//                                    }
//                                    callback.onResult(it, ConstantGlobal.INITIAL_PAGE, ConstantGlobal.INITIAL_PAGE+1)
//                                    loadSirLiveEvent.value = SuccessCallback::class.java
//                                }

                                val data = arrayListOf<Any>()
                                for (i in 0..9) {
                                    data.add(i)
                                }
                                callback.onResult(data, ConstantGlobal.INITIAL_PAGE, ConstantGlobal.INITIAL_PAGE + 1)
                                loadSirLiveEvent.value = SuccessCallback::class.java
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
                callback: LoadCallback<Int, Any>
            ) {
                addSubscribe(
                    model
                        .getSupplementCardList(page = params.key)
                        .compose(RxUtils.schedulersTransformer())
                        .compose(RxUtils.exceptionTransformer())
                        .subscribeWith(object : AppDisposableObserver<AppResponse<Any>>() {
                            override fun onSuccess(t: AppResponse<Any>) {
//                                t.datas?.let {
//                                    if (it.isEmpty()){
//                                        return
//                                    }
//                                    callback.onResult(it,params.key+1)
//                                }

                                val data = arrayListOf<Any>()
                                for (i in 0..9) {
                                    data.add(i)
                                }
                                callback.onResult(data, params.key + 1)
                            }
                        })
                )
            }

            override fun loadBefore(
                params: LoadParams<Int>,
                callback: LoadCallback<Int, Any>
            ) {
            }
        }
        return dataSource!!
    }

}