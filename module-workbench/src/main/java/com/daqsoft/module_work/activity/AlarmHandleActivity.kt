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
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.GlideEngine
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.library_common.adapter.GridImageAdapter
import com.daqsoft.library_common.warrper.FullyGridLayoutManager
import com.daqsoft.module_work.BR
import com.daqsoft.module_work.R
import com.daqsoft.module_work.adapter.AlarmHandleAudioAdapter
import com.daqsoft.module_work.databinding.ActivityAlarmHandleBinding
import com.daqsoft.module_work.repository.pojo.dto.AlarmHandleRequest
import com.daqsoft.module_work.utils.RecordUtils
import com.daqsoft.module_work.viewmodel.AlarmHandleViewModel
import com.daqsoft.module_work.widget.RecordPopup
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import com.daqsoft.mvvmfoundation.utils.dp
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
import me.tatarka.bindingcollectionadapter2.ItemBinding


/**
 * @package name：com.daqsoft.module_task.activity
 * @date 20/5/2021 上午 11:03
 * @author zp
 * @describe 任务处理
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_ALARM_HANDLE)
class AlarmHandleActivity : AppBaseActivity<ActivityAlarmHandleBinding, AlarmHandleViewModel>() ,
    GridImageAdapter.onAddPicClickListener,
    RecordUtils.PlayListener
{


    @JvmField
    @Autowired
    var id: String = ""

    @JvmField
    @Autowired
    // 0:所有告警 1:我指派的 2：我接收的
    var state : Int = 2

    val recordUtils : RecordUtils by lazy(LazyThreadSafetyMode.SYNCHRONIZED ) { RecordUtils() }

    companion object{
        const val MAX_NUMBER : Int = 5
    }

    lateinit var mPicAdapter : GridImageAdapter

    var audioObservableList: ObservableList<LocalMedia> = ObservableArrayList()


    val taskProcessAudioAdapter : AlarmHandleAudioAdapter by lazy {
        AlarmHandleAudioAdapter().apply {
            itemBinding = ItemBinding.of(ItemBinding.VAR_NONE, R.layout.recycleview_alarm_handle_audio_item)
            setItems(audioObservableList)

            setItemOnClickListener(object : AlarmHandleAudioAdapter.ItemOnClickListener{
                override fun delete(position: Int, item: LocalMedia) {

                    recordUtils.playOrPause()
                    taskProcessAudioAdapter.stopAnimation()
                    showConfirmPopup(item)
                }

                override fun play(position: Int, item: LocalMedia) {
                    recordUtils.initPlayer(if (PictureMimeType.isContent(item.path)) item.androidQToPath else item.path)
                }

            })
        }
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
                    taskProcessAudioAdapter.startAnimation()
                } , false
            )
            .show()
    }

    val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            val extras: Bundle?
            when (action) {
                BroadcastAction.ACTION_DELETE_PREVIEW_POSITION -> {
                    // 外部预览删除按钮回调
                    extras = intent.extras
                    val position = extras!!.getInt(PictureConfig.EXTRA_PREVIEW_DELETE_POSITION)
                    if (position < mPicAdapter.itemCount) {
                        mPicAdapter.delete(position)
                    }
                }
            }
        }
    }


    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_alarm_handle
    }

    override fun initViewModel(): AlarmHandleViewModel? {
        return viewModels<AlarmHandleViewModel>().value
    }

    override fun initView() {
        super.initView()
        initOnClick()
        initPictureVideoRecyclerView()
        initAudioRecyclerView()
        recordUtils.setPlayListener(this)
    }


    override fun initViewObservable() {
        super.initViewObservable()
    }

    private fun initPictureVideoRecyclerView() {
        mPicAdapter = GridImageAdapter(this, this)
        mPicAdapter.setOnItemClickListener { v, position ->
            val selectList: List<LocalMedia> = mPicAdapter.data
            if (selectList.isNotEmpty()) {
                val media = selectList[position]
                val mimeType = media.mimeType
                val mediaType = PictureMimeType.getMimeType(mimeType)
                when (mediaType) {
                    PictureConfig.TYPE_VIDEO ->{
                        // 预览视频
                        PictureSelector
                            .create(this)
                            .themeStyle(R.style.picture_default_style)
                            .setPictureStyle(PictureParameterStyle.ofDefaultStyle())
                            .externalPictureVideo(if (TextUtils.isEmpty(media.androidQToPath)) media.path else media.androidQToPath)
                    }
                    PictureConfig.TYPE_AUDIO ->{
                        // 预览音频
                        PictureSelector
                            .create(this)
                            .externalPictureAudio(if (PictureMimeType.isContent(media.path)) media.androidQToPath else media.path)
                    }
                    PictureConfig.TYPE_IMAGE ->{
                        // 预览图片 可自定长按保存路径
                        PictureSelector
                            .create(this)
                            .themeStyle(R.style.picture_default_style)
                            .isNotPreviewDownload(true)
                            .imageEngine(GlideEngine.createGlideEngine())
                            .setPictureStyle(PictureParameterStyle.ofDefaultStyle())
                            .openExternalPreview(position, selectList)
                    }
                }
            }
        }
        mPicAdapter.setSelectMax(MAX_NUMBER)
        binding.pictureVideo.apply {
            val spanCount = 4
            layoutManager =
                FullyGridLayoutManager(
                    this@AlarmHandleActivity,
                    spanCount,
                    GridLayoutManager.VERTICAL,
                    false
                )
            if (itemDecorationCount == 0){
                val spacing = 0.dp
                val itemDecoration =  GridSpacingItemDecoration(spanCount,spacing, true)
                addItemDecoration(itemDecoration)
            }
            adapter=mPicAdapter
        }

        BroadcastManager.getInstance(this).registerReceiver(
            broadcastReceiver,
            BroadcastAction.ACTION_DELETE_PREVIEW_POSITION
        )

    }


    private fun initAudioRecyclerView() {
        binding.audio.apply {
            layoutManager = FullyGridLayoutManager(context,5, GridLayoutManager.VERTICAL,false)
            adapter = taskProcessAudioAdapter
        }
    }

    private fun initOnClick() {
        binding.process.setOnClickListenerThrottleFirst {
            // 办理
            alarmHandle(if (state == 0) 2 else 6)
        }

        binding.finish.setOnClickListenerThrottleFirst {
            // 办结
            alarmHandle(if (state == 0) 10 else 7)
        }

        binding.audioUpload.setOnClickListener {
            requestPermission(
                Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.RECORD_AUDIO,
                callback = { choose(PictureMimeType.ofAudio()) } )
        }

        binding.audioRecord.setOnClickListener {

            recordUtils.stopPlay()

            requestPermission(
                Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.RECORD_AUDIO,
                callback = { showRecordPopup() } )

        }

        binding.content.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                binding.count.text = "${s.toString().length}/200"
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

    }



    fun alarmHandle(method:Int){
        // 办理
        if (binding.content.text.toString().isNullOrBlank()){
            ToastUtils.showShortSafe("请输入办理结果")
            return
        }
        val localFiles = arrayListOf<LocalMedia>()
        localFiles.addAll(mPicAdapter.data)
        localFiles.addAll(audioObservableList)
        val body = AlarmHandleRequest(id,binding.content.text.toString(),method,localFiles = localFiles)
        viewModel.alarmHandle(body)
    }

    override fun onAddPicClick() {
        choose(PictureMimeType.ofAll())
    }


    fun choose(chooseMode:Int){
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
            .maxSelectNum(MAX_NUMBER)
            // 最小选择数量
            .minSelectNum(0)
            // 视频最大选择数量
            .maxVideoSelectNum(MAX_NUMBER)
            // 视频最小选择数量
            .minVideoSelectNum(0)
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
            .selectionData(if (chooseMode == PictureMimeType.ofAudio()) audioObservableList else mPicAdapter.data)
            //结果回调onActivityResult code
            .forResult(chooseMode)
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
                PictureConfig.CHOOSE_REQUEST,PictureConfig.TYPE_ALL ->{
                    // 图片选择结果回调
                    val selectList = PictureSelector.obtainMultipleResult(data)
                    if (!selectList.isNullOrEmpty()){
                        mPicAdapter.setList(selectList)
                        mPicAdapter.notifyDataSetChanged()
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


    /**
     * 录音 popup
     */
    fun showRecordPopup(){

        if (audioObservableList.size >= 5){
            ToastUtils.showShort("音频文件最多上传5个")
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

    override fun playStart() {

    }

    override fun playing() {

    }

    override fun playPause() {

    }

    override fun playStop() {
        taskProcessAudioAdapter.releaseAnimation()
    }


}