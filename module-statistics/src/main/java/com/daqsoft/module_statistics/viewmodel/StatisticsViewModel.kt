package com.daqsoft.module_statistics.viewmodel

import android.app.Application
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.wrapper.loadsircallback.ErrorCallback
import com.daqsoft.module_statistics.R
import com.daqsoft.module_statistics.repository.StatisticsRepository
import com.daqsoft.module_statistics.repository.pojo.vo.*
import com.daqsoft.mvvmfoundation.toolbar.ToolbarViewModel
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.daqsoft.mvvmfoundation.utils.SingleLiveEvent
import com.kingja.loadsir.callback.SuccessCallback
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.Function
import io.reactivex.rxjava3.schedulers.Schedulers
import me.tatarka.bindingcollectionadapter2.ItemBinding

class StatisticsViewModel : ToolbarViewModel<StatisticsRepository> {

    @ViewModelInject
    constructor(application: Application, statisticsRepository : StatisticsRepository):super(application,statisticsRepository)

    override fun onCreate() {
        super.onCreate()
        initToolbar()
    }

    private fun initToolbar() {
        setBackground(Color.TRANSPARENT)
        setBackIconVisible(View.GONE)
        setTitleText("数据总览")
        setTitleTextColor(Color.WHITE)
    }

    /**
     * 给RecyclerView添加ObservableList
     */
    var observableList: ObservableList<Statistics> = ObservableArrayList()
    /**
     * 给RecyclerView添加ItemBinding
     */
    var itemBinding: ItemBinding<Statistics> = ItemBinding.of(ItemBinding.VAR_NONE, R.layout.recycleview_statistics_item)



    val refreshEvent = SingleLiveEvent<Unit>()
    val comfortEvent = SingleLiveEvent<ScenicSpotComfort>()
    fun getData(){

        val tempList = arrayListOf<Statistics>()
        addSubscribe(
            model
                .getScenicSpotComfort()
                .subscribeOn(Schedulers.io())
                .concatMap(Function<AppResponse<ScenicSpotComfort>, Observable<AppResponse<TodayTourist>>>{
                    it.data?.let {
                        comfortEvent.postValue(it)
                    }
                    return@Function model.getTodayTouristCount()
                })
                .concatMap(Function<AppResponse<TodayTourist>, Observable<AppResponse<TodayTicket>>>{
                    it.data?.let {
                        tempList.add(it)
                    }
                    return@Function model.getTodayTicketCount()
                })
                .concatMap(Function<AppResponse<TodayTicket>, Observable<AppResponse<TodayVehicle>>>{
                    it.data?.let {
                        tempList.add(it)
                    }
                    return@Function model.getTodayVehicleCount()
                })
                .compose (RxUtils.exceptionTransformer())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally{
                    refreshEvent.call()
                }
                .subscribeWith(object : AppDisposableObserver<AppResponse<TodayVehicle>>() {
                    override fun onSuccess(t: AppResponse<TodayVehicle>) {
                        t.data?.let {
                            tempList.add(it)
                        }
                        observableList.clear()
                        observableList.addAll(tempList)
                        loadSirLiveEvent.value = SuccessCallback::class.java
                    }

                    override fun onFail(e: Throwable) {
                        super.onFail(e)
                        loadSirLiveEvent.value = ErrorCallback::class.java
                    }
                })
        )
    }





}