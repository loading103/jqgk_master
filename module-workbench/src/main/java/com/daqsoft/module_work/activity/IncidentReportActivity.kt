package com.daqsoft.module_work.activity

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.amap.api.location.AMapLocation
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.GlideEngine
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.library_common.adapter.GridImageAdapter
import com.daqsoft.library_common.utils.MyAMapUtils
import com.daqsoft.library_common.warrper.FullyGridLayoutManager
import com.daqsoft.module_work.BR
import com.daqsoft.module_work.R
import com.daqsoft.module_work.adapter.AlarmHandleAudioAdapter
import com.daqsoft.module_work.databinding.ActivityIncidentReportBinding
import com.daqsoft.module_work.repository.pojo.dto.IncidentReportRequest
import com.daqsoft.module_work.utils.RecordUtils
import com.daqsoft.module_work.viewmodel.IncidentReportViewModel
import com.daqsoft.module_work.widget.RecordPopup
import com.daqsoft.module_work.widget.VideoSurveillanceFilterPopup
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import com.daqsoft.mvvmfoundation.utils.dp
import com.google.gson.Gson
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.broadcast.BroadcastAction
import com.luck.picture.lib.broadcast.BroadcastManager
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.decoration.GridSpacingItemDecoration
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.style.PictureParameterStyle
import com.lxj.xpopup.XPopup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_incident_report.*
import me.tatarka.bindingcollectionadapter2.ItemBinding
import timber.log.Timber

/**
 * @package name：com.daqsoft.module_work.activity
 * @date 22/7/2021 上午 9:54
 * @author zp
 * @describe 事件上报
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_INCIDENT_REPORT)
class IncidentReportActivity  : AppBaseActivity<ActivityIncidentReportBinding, IncidentReportViewModel>(),
    RecordUtils.PlayListener

{

    companion object{
        const val IMAGE_MAX_NUMBER : Int = 5
        const val VIDEO_MAX_NUMBER : Int = 3
        const val AUDIO_MAX_NUMBER : Int = 3

    }

    val imageAdapter: GridImageAdapter by lazy {
        GridImageAdapter(this,R.mipmap.pictureselector_only_img) {
            requestPermission(
                Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.RECORD_AUDIO,
                callback = { choose(PictureMimeType.ofImage()) } )
        }.apply {
            setOnItemClickListener { v, position ->
                val selectList: List<LocalMedia> = data
                if (selectList.isNotEmpty()) {
                    val media = selectList[position]
                    val mimeType = media.mimeType
                    val mediaType = PictureMimeType.getMimeType(mimeType)
                    when (mediaType) {
                        PictureConfig.TYPE_IMAGE -> {
                            previewMediaType = PictureConfig.TYPE_IMAGE
                            // 预览图片 可自定长按保存路径
                            PictureSelector
                                .create(this@IncidentReportActivity)
                                .themeStyle(R.style.picture_default_style)
                                .isNotPreviewDownload(true)
                                .imageEngine(GlideEngine.createGlideEngine())
                                .setPictureStyle(PictureParameterStyle.ofDefaultStyle())
                                .openExternalPreview(position, selectList)
                        }
                    }
                }
            }
            setSelectMax(IMAGE_MAX_NUMBER)
        }
    }

    val videoAdapter : GridImageAdapter by lazy {
        GridImageAdapter(this,R.mipmap.pictureselector_only_video) {
            requestPermission(
                Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.RECORD_AUDIO,
                callback = { choose(PictureMimeType.ofVideo()) } )
        }.apply {
            setOnItemClickListener { v, position ->
                val selectList: List<LocalMedia> = data
                if (selectList.isNotEmpty()) {
                    val media = selectList[position]
                    val mimeType = media.mimeType
                    val mediaType = PictureMimeType.getMimeType(mimeType)
                    when (mediaType) {
                        PictureConfig.TYPE_VIDEO -> {
                            previewMediaType = PictureConfig.TYPE_VIDEO
                            // 预览视频
                            PictureSelector
                                .create(this@IncidentReportActivity)
                                .themeStyle(R.style.picture_default_style)
                                .setPictureStyle(PictureParameterStyle.ofDefaultStyle())
                                .externalPictureVideo(if (TextUtils.isEmpty(media.androidQToPath)) media.path else media.androidQToPath)
                        }
                    }
                }
            }
            setSelectMax(VIDEO_MAX_NUMBER)
        }
    }


    var previewMediaType : Int? = null
    val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            val extras: Bundle?
            when (action) {
                BroadcastAction.ACTION_DELETE_PREVIEW_POSITION -> {
                    // 外部预览删除按钮回调
                    extras = intent.extras
                    val position = extras!!.getInt(PictureConfig.EXTRA_PREVIEW_DELETE_POSITION)

                    when(previewMediaType){
                        PictureConfig.TYPE_IMAGE ->{
                            if (position < imageAdapter.itemCount) {
                                imageAdapter.delete(position)
                            }
                        }
                        PictureConfig.TYPE_VIDEO ->{
                            if (position < videoAdapter.itemCount) {
                                videoAdapter.delete(position)
                            }
                        }
                    }
                }
            }
        }
    }

    val recordUtils : RecordUtils by lazy(LazyThreadSafetyMode.SYNCHRONIZED ) { RecordUtils() }

    var audioObservableList: ObservableList<LocalMedia> = ObservableArrayList()

    val audioAdapter : AlarmHandleAudioAdapter by lazy {
        AlarmHandleAudioAdapter().apply {
            itemBinding = ItemBinding.of(ItemBinding.VAR_NONE, R.layout.recycleview_alarm_handle_audio_item)
            setItems(audioObservableList)
            setItemOnClickListener(object : AlarmHandleAudioAdapter.ItemOnClickListener{
                override fun delete(position: Int, item: LocalMedia) {
                    recordUtils.playOrPause()
                    audioAdapter.stopAnimation()
                    showConfirmPopup(item)
                }

                override fun play(position: Int, item: LocalMedia) {
                    recordUtils.initPlayer(if (PictureMimeType.isContent(item.path)) item.androidQToPath else item.path)
                }
            })
        }
    }


    val typePopup : VideoSurveillanceFilterPopup by lazy {
        VideoSurveillanceFilterPopup(this,"事件类型").apply {
            setItemOnClickListener(object : VideoSurveillanceFilterPopup.ItemOnClickListener{
                override fun onClick(position: Int?, content: String?) {
                    binding.type.text = content
                    if (binding.typeTips.isVisible){
                        binding.typeTips.isVisible = false
                    }
                }
            })
        }
    }

    val viewpointPopup : VideoSurveillanceFilterPopup by lazy {
        VideoSurveillanceFilterPopup(this,"关联景点").apply {
            setItemOnClickListener(object : VideoSurveillanceFilterPopup.ItemOnClickListener{
                override fun onClick(position: Int?, content: String?) {
                    binding.viewpoint.text = content
                }
            })
        }

    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_incident_report
    }

    override fun initViewModel(): IncidentReportViewModel? {
        return viewModels<IncidentReportViewModel>().value
    }


    override fun initView() {
        super.initView()

        initOnClick()

        initImageRecyclerView()
        initVideoRecyclerView()
        initAudioRecyclerView()

        BroadcastManager.getInstance(this).registerReceiver(
            broadcastReceiver,
            BroadcastAction.ACTION_DELETE_PREVIEW_POSITION
        )

        recordUtils.setPlayListener(this)


        requestPermission(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            callback = {
                startLocation()
            })
    }


    override fun initData() {
        super.initData()

        viewModel.getIncidentType()
        viewModel.getAllViewpoint()
    }

    override fun initViewObservable() {
        super.initViewObservable()

        viewModel.typeEvent.observe(this, Observer {
            typePopup.setData(it.map { it.type })
        })

        viewModel.viewpointEvent.observe(this, Observer {
            viewpointPopup.setData(it.map { it.spotName })
        })

        viewModel.clearEvent.observe(this, Observer {
            clear()
        })
    }

    private fun clear(){
        binding.address.setText("")
        binding.type.text = ""
        typePopup.setSelectedPosition(-1)
        binding.viewpoint.text = ""
        viewpointPopup.setSelectedPosition(-1)
        binding.reasonContent.setText("")
        imageAdapter.setList(arrayListOf())
        imageAdapter.notifyDataSetChanged()
        videoAdapter.setList(arrayListOf())
        videoAdapter.notifyDataSetChanged()
        audioObservableList.clear()
    }

    private fun initOnClick() {

        binding.type.setOnClickListenerThrottleFirst {
            showTypePopup()
        }

        binding.viewpoint.setOnClickListenerThrottleFirst {
            showViewpointPopup()
        }

        binding.submit.setOnClickListenerThrottleFirst {
            checkParameters(true)
        }

        binding.submitContinue.setOnClickListenerThrottleFirst {
            checkParameters(false)
        }

        binding.cancel.setOnClickListener {
            showCancelPopup()
        }

        binding.audioRecord.setOnClickListener {

            recordUtils.stopPlay()

            requestPermission(
                Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.RECORD_AUDIO,
                callback = { showRecordPopup() } )

        }

        binding.reasonContent.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                binding.reasonCount.text = "${s.toString().length}/200"
                if (binding.reasonTips.isVisible){
                    binding.reasonTips.isVisible = false
                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        binding.address.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (binding.addressTips.isVisible){
                    binding.addressTips.isVisible = false
                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

    }

    private fun checkParameters(finish : Boolean){
        var unfilled = 0
        if (binding.address.text.toString().trim().isBlank()){
            binding.addressTips.isVisible = true
            unfilled++
        }
        if (binding.type.text.toString().isBlank()){
            binding.typeTips.isVisible = true
            unfilled++
        }
        if (binding.reasonContent.text.toString().trim().isBlank()){
            binding.reasonTips.isVisible = true
            unfilled++
        }

        if (unfilled > 0){
            ToastUtils.showShortSafe("请完善信息")
            return
        }

        val body = IncidentReportRequest(
            addr = if (binding.address.text.isNullOrBlank()) null else binding.address.text.toString(),
            type = viewModel.typeEvent.value?.find { it.type == binding.type.text.toString() }?.id,
            typeName = if (binding.type.text.isNullOrBlank()) null else binding.type.text.toString(),
            spotId = viewModel.viewpointEvent.value?.find { it.spotName == binding.viewpoint.text.toString() }?.id,
            spotName = if (binding.viewpoint.text.isNullOrBlank()) null else binding.viewpoint.text.toString(),
            content = if (binding.reasonContent.text.isNullOrBlank()) null else binding.reasonContent.text.toString(),
            imgs = null,
            videos = null,
            audios = null,
            lat = currentLocation?.latitude.toString(),
            lng = currentLocation?.longitude.toString()
        )

        val annex = arrayListOf<LocalMedia>()
        annex.addAll(imageAdapter.data)
        annex.addAll(videoAdapter.data)
        annex.addAll(audioObservableList)

        viewModel.postIncidentReport(body,annex,finish)
    }


    private fun showCancelPopup() {
        XPopup
            .Builder(this)
            .isDestroyOnDismiss(true)
            .asConfirm(
                "提示",
                "确定要取消事件上报？",
                "取消",
                "确定",
                {
                    finish()
                },
                null,
                false,
                R.layout.layout_popup_confirm
            )
            .show()
    }

    private fun showViewpointPopup() {
        XPopup
            .Builder(this)
            .asCustom(viewpointPopup)
            .show()
    }

    private fun showTypePopup() {
        XPopup
            .Builder(this)
            .asCustom(typePopup)
            .show()
    }


    private fun initImageRecyclerView() {
        binding.image.apply {
            val spanCount = 4
            layoutManager = FullyGridLayoutManager(
                this@IncidentReportActivity,
                spanCount,
                GridLayoutManager.VERTICAL,
                false
            )
            if (itemDecorationCount == 0) {
                val spacing = 0.dp
                val itemDecoration = GridSpacingItemDecoration(spanCount, spacing, true)
                addItemDecoration(itemDecoration)
            }
            adapter = imageAdapter
        }
    }

    private fun initVideoRecyclerView() {
        binding.video.apply {
            val spanCount = 4
            layoutManager = FullyGridLayoutManager(
                this@IncidentReportActivity,
                spanCount,
                GridLayoutManager.VERTICAL,
                false
            )
            if (itemDecorationCount == 0) {
                val spacing = 0.dp
                val itemDecoration = GridSpacingItemDecoration(spanCount, spacing, true)
                addItemDecoration(itemDecoration)
            }
            adapter = videoAdapter
        }
    }

    private fun initAudioRecyclerView() {
        binding.audio.apply {
            layoutManager = FullyGridLayoutManager(context,5, GridLayoutManager.VERTICAL,false)
            adapter = audioAdapter
        }

    }


    private fun choose(chooseMode:Int){
        PictureSelector
            .create(this)
            // 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
            .openGallery(chooseMode)
            .filterMaxFileSize(
                when(chooseMode){
                    PictureMimeType.ofImage() -> 10 * PictureConfig.MB
                    PictureMimeType.ofVideo() -> 100 * PictureConfig.MB
                    PictureMimeType.ofAudio() -> 10 * PictureConfig.MB
                    else -> 10 * PictureConfig.MB
                }
            )
            // 外部传入图片加载引擎，必传项
            .imageEngine(GlideEngine.createGlideEngine())
            // 最大图片选择数量
            .maxSelectNum( when(chooseMode){
                PictureMimeType.ofImage() -> IMAGE_MAX_NUMBER
                PictureMimeType.ofVideo() -> VIDEO_MAX_NUMBER
                PictureMimeType.ofAudio() -> AUDIO_MAX_NUMBER
                else -> VIDEO_MAX_NUMBER
            })
            // 最小选择数量
            .minSelectNum(0)
            .maxVideoSelectNum(VIDEO_MAX_NUMBER)
            // 每行显示个数
            .imageSpanCount(4)
            // 设置相册Activity方向，不设置默认使用系统
            //            .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            // 是否显示原图控制按钮，如果设置为true则用户可以自由选择是否使用原图，压缩、裁剪功能将会失效
            .isOriginalImageControl(true)
            // 多选 or 单选
            .selectionMode(PictureConfig.MULTIPLE)
            // 单选模式下是否直接返回，PictureConfig.SINGLE模式下有效
            .isSingleDirectReturn(false)
            // 是否可预览图片
            .isPreviewImage(true)
            // 是否可预览视频
            .isPreviewVideo(true)
            // 是否可播放音频
            .enablePreviewAudio(true)
            // 是否显示拍照按钮
            .isCamera(true)
            // 图片列表点击 缩放效果 默认true
            .isZoomAnim(true)
            // 图片压缩后输出质量 0~ 100
            .compressQuality(80)
            //同步false或异步true 压缩 默认同步
            .synOrAsy(true)
            // 是否显示gif图片
            .isGif(false)
            // 裁剪输出质量 默认100
            .cutOutQuality(90)
            // 是否裁剪
            //            .isEnableCrop(true)
            // 小于100kb的图片不压缩
            .minimumCompressSize(100)
            // 图片和视频是否可以同选,只在ofAll模式下有效
            .isWithVideoImage(true)
            // 是否传入已选图片
            .selectionData(
                when(chooseMode){
                    PictureMimeType.ofImage() -> imageAdapter.data
                    PictureMimeType.ofVideo() -> videoAdapter.data
                    PictureMimeType.ofAudio() -> audioObservableList
                    else -> arrayListOf()
                }
            )
            //结果回调onActivityResult code
            .forResult(chooseMode)
    }

    /**
     * 录音 popup
     */
    fun showRecordPopup(){

        if (audioObservableList.size >= AUDIO_MAX_NUMBER){
            ToastUtils.showShort("最多可上传${AUDIO_MAX_NUMBER}个音频")
            return
        }

        XPopup
            .Builder(this)
            .enableDrag(false)
            .dismissOnTouchOutside(false)
            .isDestroyOnDismiss(true)
            .asCustom(RecordPopup(this).apply {
                setOnClickListener(object : RecordPopup.OnClickListener{
                    override fun determine(localMedia: LocalMedia) {
                        audioObservableList.add(localMedia)
                    }

                })
            })
            .show()
    }

    private fun showConfirmPopup(item:LocalMedia) {
        XPopup
            .Builder(this)
            .isDestroyOnDismiss(true)
            .asConfirm(
                "提示",
                "确定要移除当前录音吗？",
                "取消",
                "确定",
                {
                    audioObservableList.remove(item)
                }, {
                    recordUtils.playOrPause()
                    audioAdapter.startAnimation()
                } , false
            )
            .show()
    }

    override fun playStart() {

    }

    override fun playing() {

    }

    override fun playPause() {

    }

    override fun playStop() {
        audioAdapter.releaseAnimation()
    }

    override fun onDestroy() {
        super.onDestroy()

        BroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver,
            BroadcastAction.ACTION_DELETE_PREVIEW_POSITION
        )

        recordUtils.stopPlay()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                PictureConfig.TYPE_IMAGE ->{
                    // 图片选择结果回调
                    val selectList = PictureSelector.obtainMultipleResult(data)
                    if (!selectList.isNullOrEmpty()){
                        imageAdapter.setList(selectList)
                        imageAdapter.notifyDataSetChanged()
                    }
                }
                PictureConfig.TYPE_VIDEO ->{
                    // 图片选择结果回调
                    val selectList = PictureSelector.obtainMultipleResult(data)
                    if (!selectList.isNullOrEmpty()){
                        videoAdapter.setList(selectList)
                        videoAdapter.notifyDataSetChanged()
                    }
                }
                PictureConfig.TYPE_AUDIO ->{
                    val selectList = PictureSelector.obtainMultipleResult(data)
                    if (!selectList.isNullOrEmpty()){
                        audioObservableList.addAll(selectList)
                    }
                }
                else -> {
                }
            }
        }
    }



    // 当前位置
    private var currentLocation : AMapLocation? = null

    /**
     * 开始定位
     */
    private fun startLocation() {
        MyAMapUtils.getLocation(this,object : MyAMapUtils.MyLocationListener {
            override fun onNext(aMapLocation: AMapLocation) {
                Timber.e("aMapLocation ${Gson().toJson(aMapLocation)}")
                currentLocation = aMapLocation
                MyAMapUtils.destroy()
            }

            override fun onError(errorMessage: String) {
                Timber.e("errorMessage ${errorMessage}")
                ToastUtils.showShortSafe("定位失败，请重试")
                MyAMapUtils.destroy()
            }

        })
    }

}
