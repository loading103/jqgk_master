package com.daqsoft.module_main.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import cn.jpush.android.api.JPushInterface
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.StringUtils
import com.daqsoft.library_base.global.MMKVGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.pojo.LoginInfo
import com.daqsoft.library_base.pojo.Profile
import com.daqsoft.library_base.pojo.UpdateInfo
import com.daqsoft.library_base.utils.AppUtils
import com.daqsoft.library_base.utils.DownloadQueueController
import com.daqsoft.library_base.utils.MMKVUtils
import com.daqsoft.library_base.utils.MMKVUtils.decodeString
import com.daqsoft.library_base.utils.MyUtils
import com.daqsoft.library_base.wrapper.loadsircallback.EmptyCallback
import com.daqsoft.library_base.wrapper.loadsircallback.ErrorCallback
import com.daqsoft.library_common.bean.AppMenu
import com.daqsoft.module_main.repository.MainRepository
import com.daqsoft.mvvmfoundation.base.BaseApplication
import com.daqsoft.mvvmfoundation.base.BaseViewModel
import com.daqsoft.mvvmfoundation.utils.ContextUtils
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.daqsoft.mvvmfoundation.utils.SingleLiveEvent
import com.google.gson.Gson
import com.kingja.loadsir.callback.SuccessCallback
import io.reactivex.rxjava3.observers.DisposableObserver
import okhttp3.ResponseBody
import retrofit2.Response


/**
 * @package name：com.daqsoft.module_main.viewmodel
 * @date 28/10/2020 下午 3:24
 * @author zp
 * @describe
 */

class MainViewModel : BaseViewModel<MainRepository> {

    @ViewModelInject
    constructor(application: Application,mainRepository:MainRepository):super(application,mainRepository)


    val appMenu = SingleLiveEvent<List<AppMenu>>()
    fun getMenus(){
        addSubscribe(
            model
                .getMenus()
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<AppMenu>>() {
                    override fun onSuccess(t: AppResponse<AppMenu>) {
                        t.datas?.let {
                            if (it.isNullOrEmpty()){
                                loadSirLiveEvent.value = EmptyCallback::class.java
                                return
                            }
                            appMenu.value = it
                            loadSirLiveEvent.value = SuccessCallback::class.java
                        }
                    }

                    override fun onFail(e: Throwable) {
                        super.onFail(e)
                        loadSirLiveEvent.value = ErrorCallback::class.java
                    }
                })
        )
    }



    val userInfoEvent = SingleLiveEvent<Profile>()
    /**
     * 获取用户信息
     */
    fun getUserInfo(){
        addSubscribe(
            model
                .getUserInfo()
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<Profile>>() {
                    override fun onSuccess(t: AppResponse<Profile>) {
                        t.data?.let {
                            JPushInterface.setAlias(ContextUtils.getContext(),it.employee.id,"${it.umsCompany.vcode}_${it.employee.id}")

                            val loginInfoJson = decodeString(MMKVGlobal.LOGIN_INFO)
                            if (!loginInfoJson.isNullOrBlank()) {
                                val loginInfo = GsonUtils.fromJson(loginInfoJson, LoginInfo::class.java)
                                if (loginInfo != null) {
                                    loginInfo.profile = it
                                    MMKVUtils.encode(MMKVGlobal.LOGIN_INFO,GsonUtils.toJson(loginInfo))
                                }
                            }


                            userInfoEvent.value = it
                        }
                    }
                })
        )
    }


    val downloadQueueController : DownloadQueueController by lazy{ DownloadQueueController () }

    /**
     * 获取推送音频
     */
    fun getPushAudio(){
//        addSubscribe(
//            model
//                .getPushAudio()
//                .compose(RxUtils.schedulersTransformer())
//                .compose(RxUtils.exceptionTransformer())
//                .subscribeWith(object : AppDisposableObserver<AppResponse<Any>>() {
//                    override fun onSuccess(t: AppResponse<Any>) {
//                        t.datas?.let {
//
//                            if (!it.isNullOrEmpty()){
//                                downloadQueueController.apply {
//
//                                    initTasks(it, createDownloadContextListener{
//
//                                    })
//
//                                    start(
//                                        createListener1(
//                                            taskStart = { task, model ->
//                                                Timber.e("start")
//                                            },
//                                            retry = { task, cause ->
//                                                Timber.e("retry")
//                                            },
//                                            connected = { task, blockCount, currentOffset, totalLength ->
//                                                Timber.e("task")
//                                            },
//                                            progress = { task, currentOffset, totalLength ->
//                                                Timber.e("progress :  ${currentOffset}/${totalLength}  ")
//                                            },
//                                            taskEnd = { task, cause, realCause, model ->
//                                                Timber.e("task")
//                                            })
//                                    )
//                                }
//
//                            }
//                        }
//                    }
//                })
//        )
    }


    /**
     * 检查更新
     */
    val updateLiveData = MutableLiveData<UpdateInfo>()
    fun checkUpdate(){
        addSubscribe(
            model
                .checkUpdate()
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(object : DisposableObserver<Response<ResponseBody>>() {
                    override fun onNext(t: Response<ResponseBody>?) {
                        t?.body()?.let {
                            val result = it.string()
                            try {
                                val updateInfo = Gson().fromJson<UpdateInfo>(result,UpdateInfo::class.java)
                                // 高版本才提示更新
                                val update: Boolean = MyUtils.isNeedUpdate(BaseApplication.getInstance()?.applicationContext, AppUtils.getVersionName(),updateInfo.VersionCode )
                                if(update){
                                    updateLiveData.value = updateInfo
                                }
                            }catch (e:Exception){
                                e.printStackTrace()
                            }
                        }
                    }

                    override fun onError(e: Throwable?) {
                        e?.printStackTrace()
                    }

                    override fun onComplete() {
                    }
                })
        )
    }
}