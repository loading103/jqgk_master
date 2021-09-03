package com.daqsoft.module_work.viewmodel

import android.app.Application
import android.graphics.Color
import android.view.View
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.GsonUtils
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.paging.Paging2Utils
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.wrapper.loadsircallback.EmptyCallback
import com.daqsoft.library_base.wrapper.loadsircallback.ErrorCallback
import com.daqsoft.module_work.BR
import com.daqsoft.module_work.R
import com.daqsoft.module_work.repository.WorkRepository
import com.daqsoft.module_work.repository.pojo.dto.VideoSurveillanceRequest
import com.daqsoft.module_work.repository.pojo.vo.VideoSurveillanceCount
import com.daqsoft.module_work.repository.pojo.vo.VideoSurveillanceGroup
import com.daqsoft.module_work.repository.pojo.vo.VideoSurveillanceList
import com.daqsoft.module_work.viewmodel.itemviewmodel.VideoSurveillanceListGridItemViewModel
import com.daqsoft.module_work.viewmodel.itemviewmodel.VideoSurveillanceListLineItemViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.toolbar.ToolbarViewModel
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.daqsoft.mvvmfoundation.utils.SingleLiveEvent
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import com.kingja.loadsir.callback.SuccessCallback
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.module_work.viewmodel
 * @date 13/7/2021 下午 1:35
 * @author zp
 * @describe
 */
class VideoSurveillanceListViewModel : ToolbarViewModel<WorkRepository>, Paging2Utils<MultiItemViewModel<*>> {

    @ViewModelInject
    constructor(application: Application, workRepository: WorkRepository) : super(
        application,
        workRepository
    )

    override fun onCreate() {
        super.onCreate()
        initToolbar()

        body = VideoSurveillanceRequest ()
    }

    private fun initToolbar() {
        setBackground(Color.WHITE)
        setTitleText("景区视频监控")
        setRightIconVisible(View.VISIBLE)
        setRightIconSrc(R.mipmap.video_icon_qiehuan1)
    }


    val layoutManagerEvent = MutableLiveData<String>(ConstantGlobal.GRID)

    override fun rightIconOnClick() {
        when (layoutManagerEvent.value) {
            ConstantGlobal.GRID -> {
                layoutManagerEvent.value = ConstantGlobal.LINE
                setRightIconSrc(R.mipmap.video_icon_qiehuan1)
            }
            ConstantGlobal.LINE -> {
                layoutManagerEvent.value = ConstantGlobal.GRID
                setRightIconSrc(R.mipmap.video_icon_qiehuan2)
            }
            else -> {
                layoutManagerEvent.value = ConstantGlobal.GRID
                setRightIconSrc(R.mipmap.video_icon_qiehuan1)
            }
        }
    }

    /**
     * 给RecyclerView添加ItemBinding
     */
    var itemBinding: ItemBinding<MultiItemViewModel<*>> = ItemBinding.of{ itemBinding, position, item ->
        when (item.itemType as String) {
            ConstantGlobal.GRID -> itemBinding.set(BR.viewModel, R.layout.recycleview_video_surveillance_list_grid_item)
            ConstantGlobal.LINE -> itemBinding.set(BR.viewModel, R.layout.recycleview_video_surveillance_list_line_item)
            else -> itemBinding.set(BR.viewModel, R.layout.recycleview_video_surveillance_list_grid_item)
        }
    }


    lateinit var body : VideoSurveillanceRequest

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
    var dataSource : DataSource<Int, MultiItemViewModel<*>>?= null
    override fun createDataSource(): DataSource<Int, MultiItemViewModel<*>> {
        dataSource = object : PageKeyedDataSource<Int, MultiItemViewModel<*>>(){
            override fun loadInitial(
                params: LoadInitialParams<Int>,
                callback: LoadInitialCallback<Int, MultiItemViewModel<*>>
            ) {
                addSubscribe(
                    model
                        .getVideoSurveillanceList(body)
                        .compose (RxUtils.schedulersTransformer())
                        .compose (RxUtils.exceptionTransformer())
                        .subscribeWith(object : AppDisposableObserver<AppResponse<VideoSurveillanceList>>() {
                            override fun onSuccess(t: AppResponse<VideoSurveillanceList>) {
                                t.datas?.let {
                                    if (it.isEmpty()){
                                        loadSirLiveEvent.value = EmptyCallback::class.java
                                        return
                                    }
                                    val data = arrayListOf<MultiItemViewModel<*>>()
                                    it.forEach {
                                        val itemViewModel = when (layoutManagerEvent.value) {
                                            ConstantGlobal.GRID -> {
                                                VideoSurveillanceListGridItemViewModel(
                                                    this@VideoSurveillanceListViewModel,
                                                    it
                                                ).apply {
                                                    multiItemType(ConstantGlobal.GRID)
                                                }
                                            }
                                            ConstantGlobal.LINE -> {
                                                VideoSurveillanceListLineItemViewModel(
                                                    this@VideoSurveillanceListViewModel,
                                                    it
                                                ).apply {
                                                        multiItemType(ConstantGlobal.LINE)
                                                    }
                                            }
                                            else -> {
                                                VideoSurveillanceListGridItemViewModel(
                                                    this@VideoSurveillanceListViewModel,
                                                    it
                                                ).apply {
                                                        multiItemType(ConstantGlobal.GRID)
                                                    }
                                            }
                                        }
                                        data.add(itemViewModel)
                                    }
                                    callback.onResult(data, ConstantGlobal.INITIAL_PAGE, ConstantGlobal.INITIAL_PAGE+1)
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
                callback: LoadCallback<Int, MultiItemViewModel<*>>
            ) {

                body.currPage = params.key
                addSubscribe(
                    model
                        .getVideoSurveillanceList(body)
                        .compose (RxUtils.schedulersTransformer())
                        .compose (RxUtils.exceptionTransformer())
                        .subscribeWith(object : AppDisposableObserver<AppResponse<VideoSurveillanceList>>() {
                            override fun onSuccess(t: AppResponse<VideoSurveillanceList>) {
                                t.datas?.let {
                                    if (it.isEmpty()){
                                        return
                                    }
                                    val data = arrayListOf<MultiItemViewModel<*>>()
                                    it.forEach {
                                        val itemViewModel = when (layoutManagerEvent.value) {
                                            ConstantGlobal.GRID -> {
                                                VideoSurveillanceListGridItemViewModel(
                                                    this@VideoSurveillanceListViewModel,
                                                    it
                                                ).apply {
                                                    multiItemType(ConstantGlobal.GRID)
                                                }
                                            }
                                            ConstantGlobal.LINE -> {
                                                VideoSurveillanceListLineItemViewModel(
                                                    this@VideoSurveillanceListViewModel,
                                                    it
                                                ).apply {
                                                        multiItemType(ConstantGlobal.LINE)
                                                    }
                                            }
                                            else -> {
                                                VideoSurveillanceListGridItemViewModel(
                                                    this@VideoSurveillanceListViewModel,
                                                    it
                                                ).apply {
                                                        multiItemType(ConstantGlobal.GRID)
                                                    }
                                            }
                                        }
                                        data.add(itemViewModel)
                                    }
                                    callback.onResult(data,params.key+1)
                                }
                            }
                        })
                )
            }

            override fun loadBefore(
                params: LoadParams<Int>,
                callback: LoadCallback<Int, MultiItemViewModel<*>>
            ) {
            }
        }
        return dataSource!!
    }


    fun itemOnClick(item:VideoSurveillanceList){
        if (item.online != 1){
            ToastUtils.showShortSafe("当前设备不在线")
            return
        }

        ARouter
            .getInstance()
            .build(ARouterPath.Workbench.PAGER_VIDEO_SURVEILLANCE_DETAIL)
            .withString("itemJson",GsonUtils.toJson(item))
            .navigation()
    }

    val countEvent = SingleLiveEvent<VideoSurveillanceCount>()

    fun getVideoSurveillanceCount(){
        addSubscribe(
            model
                .getVideoSurveillanceCount()
                .compose (RxUtils.schedulersTransformer())
                .compose (RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<VideoSurveillanceCount>>() {
                    override fun onSuccess(t: AppResponse<VideoSurveillanceCount>) {
                        t.data?.let {
                            countEvent.value = it
                        }
                    }
                })
        )


    }


    val groupEvent = SingleLiveEvent<List<VideoSurveillanceGroup>>()

    fun getVideoSurveillanceGroup(){
        addSubscribe(
            model
                .getVideoSurveillanceGroup()
                .compose (RxUtils.schedulersTransformer())
                .compose (RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<VideoSurveillanceGroup>>() {
                    override fun onSuccess(t: AppResponse<VideoSurveillanceGroup>) {
                        t.datas?.let {
                            groupEvent.value = it
                        }

                    }
                })
        )


    }
}