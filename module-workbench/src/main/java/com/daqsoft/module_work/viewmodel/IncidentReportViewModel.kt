package com.daqsoft.module_work.viewmodel

import android.app.Application
import android.media.MediaMetadataRetriever
import android.view.View
import androidx.hilt.lifecycle.ViewModelInject
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.UploadUtils
import com.daqsoft.module_work.R
import com.daqsoft.module_work.repository.WorkRepository
import com.daqsoft.module_work.repository.pojo.dto.AlarmHandleRequest
import com.daqsoft.module_work.repository.pojo.dto.Annex
import com.daqsoft.module_work.repository.pojo.dto.IncidentReportRequest
import com.daqsoft.module_work.repository.pojo.vo.AlarmCount
import com.daqsoft.module_work.repository.pojo.vo.File
import com.daqsoft.module_work.repository.pojo.vo.IncidentType
import com.daqsoft.module_work.repository.pojo.vo.Viewpoint
import com.daqsoft.mvvmfoundation.toolbar.ToolbarViewModel
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.daqsoft.mvvmfoundation.utils.SingleLiveEvent
import com.jeremyliao.liveeventbus.LiveEventBus
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import retrofit2.http.Body
import timber.log.Timber

/**
 * @package name：com.daqsoft.module_work.viewmodel
 * @date 22/7/2021 上午 9:58
 * @author zp
 * @describe
 */
class IncidentReportViewModel: ToolbarViewModel<WorkRepository> {

    @ViewModelInject
    constructor(application: Application, workRepository: WorkRepository) : super(
        application,
        workRepository
    )

    override fun onCreate() {
        initToolbar()
    }

    private fun initToolbar() {
        setTitleText("事件上报")
        setRightTextVisible(View.VISIBLE)
        setRightTextColor(R.color.color_59abff)
        setRightTextTxt("列表")
        setRightTextDrawableRight(R.mipmap.sjsb_icon_liebiao)
    }

    override fun rightTextOnClick() {
        Timber.e("rightTextOnClick")
        ARouter
            .getInstance()
            .build(ARouterPath.Workbench.PAGER_INCIDENT_LIST)
            .navigation()
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


    val viewpointEvent = SingleLiveEvent<List<Viewpoint>>()
    fun getAllViewpoint(){
        addSubscribe(
            model
                .getAllViewpoint()
                .compose (RxUtils.schedulersTransformer())
                .compose (RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<Viewpoint>>() {
                    override fun onSuccess(t: AppResponse<Viewpoint>) {
                        t.datas?.let {
                            viewpointEvent.value = it
                        }
                    }
                })
        )
    }


    val clearEvent = SingleLiveEvent<Unit?>()
    fun postIncidentReport(
        body: IncidentReportRequest,
        annex:List<LocalMedia>,
        finish : Boolean){
        showLoadingDialog()
        UploadUtils.upload(annex.map { if(it.realPath.isNullOrBlank()) it.path else it.realPath }){
            val imageList = arrayListOf<Annex>()
            val videoList = arrayListOf<Annex>()
            val audioList = arrayListOf<Annex>()
            it.forEachIndexed { index, ossUploadResult ->
                val localMedia = annex[index]
                val mimeType = localMedia.mimeType ?:"ANNEX"
                when (mimeType) {
                    PictureMimeType.MIME_TYPE_IMAGE -> {
                        val annex = Annex(((localMedia.duration?:0L) / 1000).toString(),1,ossUploadResult.fileUrl?:"")
                        imageList.add(annex)
                    }
                    PictureMimeType.MIME_TYPE_VIDEO -> {

                        val annex = Annex(((localMedia.duration?:0L) / 1000).toString(),2,ossUploadResult.fileUrl?:"")
                        videoList.add(annex)
                    }
                    PictureMimeType.MIME_TYPE_AUDIO -> {
                        val annex = Annex(((localMedia.duration?:0L) / 1000).toString(),4,ossUploadResult.fileUrl?:"")
                        audioList.add(annex)
                    }
                }
            }
            body.imgs = imageList
            body.videos = videoList
            body.audios = audioList
            report(body,finish)
        }
    }

    fun report(body: IncidentReportRequest,finish: Boolean){
        addSubscribe(
            model
                .postIncidentReport(body)
                .compose (RxUtils.schedulersTransformer())
                .compose (RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<Any>>() {
                    override fun onSuccess(t: AppResponse<Any>) {
                        if (finish){
                            ARouter
                                .getInstance()
                                .build(ARouterPath.Workbench.PAGER_INCIDENT_LIST)
                                .navigation()
                            this@IncidentReportViewModel.finish()
                        }else{
                            clearEvent.call()
                        }
                    }

                    override fun onComplete() {
                        super.onComplete()
                        dismissLoadingDialog()
                    }
                })
        )
    }
}