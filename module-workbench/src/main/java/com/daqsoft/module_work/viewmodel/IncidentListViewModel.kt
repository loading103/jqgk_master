package com.daqsoft.module_work.viewmodel

import android.app.Application
import android.view.View
import androidx.hilt.lifecycle.ViewModelInject
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.paging.Paging2Utils
import com.daqsoft.library_base.wrapper.loadsircallback.EmptyCallback
import com.daqsoft.library_base.wrapper.loadsircallback.ErrorCallback
import com.daqsoft.module_work.R
import com.daqsoft.module_work.repository.WorkRepository
import com.daqsoft.module_work.repository.pojo.dto.IncidentListRequest
import com.daqsoft.module_work.repository.pojo.vo.Incident
import com.daqsoft.module_work.repository.pojo.vo.IncidentStatus
import com.daqsoft.module_work.repository.pojo.vo.IncidentType
import com.daqsoft.module_work.repository.pojo.vo.Viewpoint
import com.daqsoft.mvvmfoundation.toolbar.ToolbarViewModel
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.daqsoft.mvvmfoundation.utils.SingleLiveEvent
import com.kingja.loadsir.callback.SuccessCallback
import me.tatarka.bindingcollectionadapter2.ItemBinding
import timber.log.Timber

/**
 * @package name：com.daqsoft.module_work.viewmodel
 * @date 23/7/2021 下午 5:02
 * @author zp
 * @describe
 */
class IncidentListViewModel : ToolbarViewModel<WorkRepository> ,
    Paging2Utils<Incident> {

    @ViewModelInject
    constructor(application: Application, workRepository: WorkRepository) : super(
        application,
        workRepository
    )

    override fun onCreate() {
        super.onCreate()
        initToolbar()

        body = IncidentListRequest()
    }

    private fun initToolbar() {
        setTitleText("事件列表")
        setRightTextVisible(View.VISIBLE)
        setRightTextColor(R.color.color_59abff)
        setRightTextTxt("筛选")
        setRightTextDrawableRight(R.mipmap.sjsb_icon_shaixuan)
    }

    val filterEvent = SingleLiveEvent<Unit>()

    override fun rightTextOnClick() {
        Timber.e("rightTextOnClick")
        filterEvent.call()
    }


    lateinit var body : IncidentListRequest

    /**
     * 给RecyclerView添加ItemBinding
     */
    var itemBinding: ItemBinding<Incident> = ItemBinding.of(ItemBinding.VAR_NONE, R.layout.recycleview_incident_list_item)

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
    var dataSource : DataSource<Int, Incident>?= null
    override fun createDataSource(): DataSource<Int, Incident> {
        dataSource = object : PageKeyedDataSource<Int, Incident>(){
            override fun loadInitial(
                params: LoadInitialParams<Int>,
                callback: LoadInitialCallback<Int, Incident>
            ) {
                addSubscribe(
                    model
                        .getIncidentList(body)
                        .compose (RxUtils.schedulersTransformer())
                        .compose (RxUtils.exceptionTransformer())
                        .subscribeWith(object : AppDisposableObserver<AppResponse<Incident>>() {
                            override fun onSuccess(t: AppResponse<Incident>) {
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
                callback: LoadCallback<Int, Incident>
            ) {

                body.currPage = params.key
                addSubscribe(
                    model
                        .getIncidentList(body)
                        .compose (RxUtils.schedulersTransformer())
                        .compose (RxUtils.exceptionTransformer())
                        .subscribeWith(object : AppDisposableObserver<AppResponse<Incident>>() {
                            override fun onSuccess(t: AppResponse<Incident>) {
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
                callback: LoadCallback<Int, Incident>
            ) {
            }
        }
        return dataSource!!
    }



    val typeEvent = SingleLiveEvent<List<IncidentType>>()
    fun getIncidentType(){
        addSubscribe(
            model
                .getIncidentType()
                .compose (RxUtils.schedulersTransformer())
                .compose (RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<IncidentType>>() {
                    override fun onSuccess(t: AppResponse<IncidentType>) {
                        t.datas?.let {
                            typeEvent.value = it
                        }
                    }
                })
        )
    }


    val statusEvent = SingleLiveEvent<List<IncidentStatus>>()
    fun getIncidentStatus(){
        addSubscribe(
            model
                .getIncidentStatus()
                .compose (RxUtils.schedulersTransformer())
                .compose (RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<IncidentStatus>>() {
                    override fun onSuccess(t: AppResponse<IncidentStatus>) {
                        t.datas?.let {
                            statusEvent.value = it
                        }
                    }
                })
        )
    }

}