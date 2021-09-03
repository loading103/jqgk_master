package com.daqsoft.module_work.viewmodel

import android.app.Application
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.wrapper.loadsircallback.EmptyCallback
import com.daqsoft.library_base.wrapper.loadsircallback.ErrorCallback
import com.daqsoft.module_work.R
import com.daqsoft.module_work.BR
import com.daqsoft.module_work.repository.WorkRepository
import com.daqsoft.module_work.repository.pojo.dto.ClockInfoRequest
import com.daqsoft.module_work.repository.pojo.vo.ClockInfo
import com.daqsoft.module_work.repository.pojo.vo.Incident
import com.daqsoft.module_work.viewmodel.itemviewmodel.ClockRecordItemViewMode
import com.daqsoft.module_work.warrper.NotJoinedCallback
import com.daqsoft.module_work.warrper.RosterEmptyCallback
import com.daqsoft.mvvmfoundation.base.BaseViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.daqsoft.mvvmfoundation.utils.SingleLiveEvent
import com.kingja.loadsir.callback.SuccessCallback
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.observers.DisposableObserver
import me.tatarka.bindingcollectionadapter2.ItemBinding
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @package name：com.daqsoft.module_work.viewmodel
 * @date 10/5/2021 下午 4:54
 * @author zp
 * @describe 打卡 viewModel
 */
class ClockViewMode : BaseViewModel<WorkRepository>{

    @ViewModelInject
    constructor(application: Application,workRepository: WorkRepository):super(application, workRepository)


    override fun onCreate() {
        super.onCreate()
    }

    var timing : Disposable? = null
    val timeObservable = ObservableField<String>()
    val refresh = SingleLiveEvent<Unit?>()


    /**
     * 打卡准备
     */
    val clockInfo = SingleLiveEvent<ClockInfo>()
    fun getClockInfo(location: String){
        addSubscribe(
            model
                .getClockInfo(location)
                .compose (RxUtils.schedulersTransformer())
                .compose (RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<ClockInfo>>() {
                    override fun onSuccess(t: AppResponse<ClockInfo>) {
                        t.data?.let {

                            if (it.maxSegment == 0){
                                loadSirLiveEvent.value = RosterEmptyCallback::class.java
                                return
                            }

                            if (it.state == "NOT_JOINED_RANGE"){
                                loadSirLiveEvent.value = NotJoinedCallback::class.java
                                return
                            }

                            if (timing == null){
                                timing()
                            }

                            clockInfo.value = it

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


    /**
     * 打卡记录
     */
    val record = SingleLiveEvent<ClockInfo>()
    fun getClockRecord(date: String){
        addSubscribe(
            model
                .getClockRecord(date)
                .compose (RxUtils.schedulersTransformer())
                .compose (RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<ClockInfo>>() {
                    override fun onSuccess(t: AppResponse<ClockInfo>) {
                        t.data?.let {

                            if (it.maxSegment == 0){
                                loadSirLiveEvent.value = RosterEmptyCallback::class.java
                                return
                            }

                            if(it.maxSegment > 0 && (it.clockedIns.isNullOrEmpty() || it.shouldClockIn == null)){
                                loadSirLiveEvent.value = ErrorCallback::class.java
                                return
                            }


                            if (it.state == "NOT_JOINED_RANGE"){
                                loadSirLiveEvent.value = NotJoinedCallback::class.java
                                return
                            }

                            if (it.state == "NOT_JOINED_RANGE"){
                                loadSirLiveEvent.value = NotJoinedCallback::class.java
                                return
                            }

                            record.value = it
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


    /**
     * 打卡
     */
    fun postClockIn(body: ClockInfoRequest){
        addSubscribe(
            model
                .postClockIn(body)
                .doOnSubscribe{
                    showLoadingDialog()
                }
                .compose (RxUtils.schedulersTransformer())
                .compose (RxUtils.exceptionTransformer())
                .doFinally{
                    dismissLoadingDialog()
                }
                .subscribeWith(object : AppDisposableObserver<AppResponse<Map<String,String>>>() {
                    override fun onSuccess(t: AppResponse<Map<String,String>>) {
                        refresh.call()
                    }
                })
        )
    }


    /**
     * 更新打卡
     */
    fun renewClockIn(clockedInId : String){
        addSubscribe(
            model
                .renewClockIn(clockedInId)
                .doOnSubscribe{
                    showLoadingDialog()
                }
                .compose (RxUtils.schedulersTransformer())
                .compose (RxUtils.exceptionTransformer())
                .doFinally{
                    dismissLoadingDialog()
                }
                .subscribeWith(object : AppDisposableObserver<AppResponse<Any>>() {
                    override fun onSuccess(t: AppResponse<Any>) {
                        refresh.call()
                    }
                })
        )
    }


    /**
     * 时间
     */
    fun timing(){
        timing = Observable
            .interval(0,1, TimeUnit.SECONDS)
            .compose(RxUtils.schedulersTransformer())
            .subscribeWith(object : DisposableObserver<Long>() {
                override fun onNext(t: Long) {
                    val calendar = Calendar.getInstance()
                    val hour = calendar.get(Calendar.HOUR_OF_DAY)
                    val minute = calendar.get(Calendar.MINUTE)
                    val second = calendar.get(Calendar.SECOND)
                    timeObservable.set(String.format(Locale.getDefault(), "%02d:%02d:%02d", hour, minute,second))
                    if (t%60 == 0L){
                        refresh.call()
                    }
                }

                override fun onError(e: Throwable?) {
                }

                override fun onComplete() {
                }
            })
        addSubscribe(
            timing!!
        )
    }

}