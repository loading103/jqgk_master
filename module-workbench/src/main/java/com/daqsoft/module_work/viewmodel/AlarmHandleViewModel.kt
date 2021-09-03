package com.daqsoft.module_work.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.utils.UploadUtils
import com.daqsoft.module_work.repository.WorkRepository
import com.daqsoft.module_work.repository.pojo.dto.AlarmHandleRequest
import com.daqsoft.module_work.repository.pojo.vo.File
import com.daqsoft.mvvmfoundation.toolbar.ToolbarViewModel
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.jeremyliao.liveeventbus.LiveEventBus
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType

/**
 * @package name：com.daqsoft.module_task.viewmodel
 * @date 20/5/2021 上午 11:06
 * @author zp
 * @describe
 */
class AlarmHandleViewModel : ToolbarViewModel<WorkRepository> {

    @ViewModelInject
    constructor(application: Application, workRepository: WorkRepository) : super(
        application,
        workRepository
    )

    override fun onCreate() {
        super.onCreate()
        initToolbar()
    }

    private fun initToolbar() {
        setTitleText("事件处理")
    }

    fun alarmHandle(body: AlarmHandleRequest){
        showLoadingDialog()
        UploadUtils.upload(body.localFiles?.map { if(it.realPath.isNullOrBlank()) it.path else it.realPath }?: arrayListOf()){
            body.files = it.mapIndexed { index, ossUploadResult ->
                val mimeType = body.localFiles?.get(index)?.mimeType?:"ANNEX"
                val type = when (mimeType) {
                    PictureMimeType.MIME_TYPE_VIDEO -> 2
                    PictureMimeType.MIME_TYPE_AUDIO -> 4
                    PictureMimeType.MIME_TYPE_IMAGE -> 1
                    else -> 3
                }
                return@mapIndexed File(type,ossUploadResult.fileUrl?:"", ((body.localFiles?.get(index)?.duration?:0L) / 1000).toString())
            }
            body.localFiles = null
            handle(body)
        }
    }


    fun handle(body: AlarmHandleRequest){

        addSubscribe(
            model
                .alarmHandle(body)
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<Any>>() {
                    override fun onSuccess(t: AppResponse<Any>) {
                        dismissLoadingDialog()
//                        LiveEventBus.get(LEBKeyGlobal.ALARM_HANDLE).post(true)
                        LiveEventBus.get(LEBKeyGlobal.ALARM_HANDLE_FINISH).post(true)
                        finish()
                    }

                    override fun onFail(e: Throwable) {
                        super.onFail(e)
                        dismissLoadingDialog()
                    }
                })
        )
    }
}