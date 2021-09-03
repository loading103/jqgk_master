package com.daqsoft.module_work.viewmodel

import android.app.Application
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_common.bean.AddressBook
import com.daqsoft.library_common.bean.Employee
import com.daqsoft.module_work.BR
import com.daqsoft.module_work.R
import com.daqsoft.module_work.repository.WorkRepository
import com.daqsoft.module_work.repository.pojo.dto.AlarmHandleRequest
import com.daqsoft.module_work.repository.pojo.vo.AlarmDetail
import com.daqsoft.module_work.repository.pojo.vo.HandleFlow
import com.daqsoft.module_work.viewmodel.itemviewmodel.AlarmDetailsFlowItemViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.toolbar.ToolbarViewModel
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.daqsoft.mvvmfoundation.utils.SingleLiveEvent
import com.jeremyliao.liveeventbus.LiveEventBus
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.Function
import io.reactivex.rxjava3.schedulers.Schedulers
import me.tatarka.bindingcollectionadapter2.ItemBinding
import retrofit2.http.Body

/**
 * @package name：com.daqsoft.module_task.viewmodel
 * @date 14/5/2021 下午 2:34
 * @author zp
 * @describe 任务详情 viewModel
 */
class AlarmDetailsViewModel : ToolbarViewModel<WorkRepository> {

    @ViewModelInject
    constructor(application: Application, workRepository : WorkRepository):super(application,workRepository)


    /**
     * 给RecyclerView添加ObservableList
     */
    var flowObservableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()

    /**
     * 给RecyclerView添加ItemBinding
     */
    var flowItemBinding: ItemBinding<ItemViewModel<*>> = ItemBinding.of(BR.viewModel, R.layout.recycleview_alarm_details_flow_item)



    override fun onCreate() {
        super.onCreate()
        initToolbar()
    }

    private fun initToolbar() {
        setBackground(Color.WHITE)
        setTitleText("任务详情")
    }


    val employeeLiveEvent = SingleLiveEvent<List<Employee>>()
    fun getEmployee(){
        addSubscribe(
            model
                .getEmployee()
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<AddressBook>>() {
                    override fun onSuccess(t: AppResponse<AddressBook>) {
                        t.datas?.let {
                            if (it.isNullOrEmpty()){
                                return
                            }
                            val employeeList = it.flatMap{
                                it.employee
                            }
                            employeeLiveEvent.value = employeeList
                        }
                    }
                })
        )
    }


    val detailField =  ObservableField<AlarmDetail>()
    val annexEvent = SingleLiveEvent<List<LocalMedia>>()
    val purviewEvent = SingleLiveEvent<Boolean>()
    fun getAlarmDetails(id:String){
        addSubscribe(
            model
                .getAlarmDetails(id)
                .subscribeOn(Schedulers.io())
                .concatMap(Function<AppResponse<AlarmDetail>, Observable<AppResponse<HandleFlow>>>{
                    it.data?.let {
                        detailField.set(it)
                        if (!it.sfiles.isNullOrEmpty()) {
                            val annex = it.sfiles.map {
                                val localMedia = LocalMedia()
                                localMedia.path = it.url
                                localMedia.duration = if (it.time.isNullOrBlank()) 0L else it.time!!.toLong()*1000
                                localMedia.mimeType = when (it.type) {
                                    1 -> PictureMimeType.MIME_TYPE_IMAGE
                                    2 -> PictureMimeType.MIME_TYPE_VIDEO
                                    4 -> PictureMimeType.MIME_TYPE_AUDIO
                                    else -> "ANNEX"
                                }
                                return@map localMedia
                            }
                            annexEvent.postValue(annex)
                        } else {
                            annexEvent.postValue(arrayListOf())
                        }
                    }
                    purviewEvent.postValue(true)
                    return@Function model.getHandleFlow(id)
                })
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .doOnComplete{
                    Handler(Looper.getMainLooper()).post {
                        dismissLoadingDialog()
                    }
                }
                .subscribeWith(object : AppDisposableObserver<AppResponse<HandleFlow>>() {
                    override fun onSuccess(t: AppResponse<HandleFlow>) {
                        t.datas?.let {
                            if(it.isNullOrEmpty()){
                                return
                            }
                            flowObservableList.clear()
                            it.forEachIndexed { index, handleFlow ->
                                val item = AlarmDetailsFlowItemViewModel(this@AlarmDetailsViewModel,handleFlow,first = index==0,last = index==it.size-1)
                                flowObservableList.add(item)
                            }
                        }
                    }
                })
        )
    }



    fun alarmHandle(body: AlarmHandleRequest){
        addSubscribe(
            model
                .alarmHandle(body)
                .doOnSubscribe{
                    showLoadingDialog()
                }
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<Any>>() {
                    override fun onSuccess(t: AppResponse<Any>) {
                        finish()
                        LiveEventBus.get(LEBKeyGlobal.ALARM_HANDLE).post(true)
                    }
                })
        )
    }

}
