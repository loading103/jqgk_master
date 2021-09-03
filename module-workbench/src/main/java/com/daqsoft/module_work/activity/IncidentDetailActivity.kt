package com.daqsoft.module_work.activity

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.databinding.Observable
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.GlideEngine
import com.daqsoft.library_common.adapter.GridImageAdapter
import com.daqsoft.library_common.warrper.FullyGridLayoutManager
import com.daqsoft.module_work.BR
import com.daqsoft.module_work.R
import com.daqsoft.module_work.adapter.AlarmHandleAudioAdapter
import com.daqsoft.module_work.databinding.ActivityIncidentDetailBinding
import com.daqsoft.module_work.databinding.ActivityIncidentListBinding
import com.daqsoft.module_work.repository.pojo.dto.Annex
import com.daqsoft.module_work.utils.RecordUtils
import com.daqsoft.module_work.viewmodel.AlarmDetailsViewModel
import com.daqsoft.module_work.viewmodel.IncidentDetailViewModel
import com.daqsoft.module_work.viewmodel.IncidentListViewModel
import com.daqsoft.mvvmfoundation.utils.dp
import com.jeremyliao.liveeventbus.LiveEventBus
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.broadcast.BroadcastAction
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.decoration.GridSpacingItemDecoration
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.style.PictureParameterStyle
import dagger.hilt.android.AndroidEntryPoint
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.module_work.activity
 * @date 26/7/2021 上午 11:14
 * @author zp
 * @describe
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_INCIDENT_Detail)
class IncidentDetailActivity : AppBaseActivity<ActivityIncidentDetailBinding, IncidentDetailViewModel>()
,    RecordUtils.PlayListener
{


    @Autowired
    @JvmField
    var id:String=""


    val imageAdapter: GridImageAdapter by lazy {
        GridImageAdapter(this,null) .apply {
            setOnItemClickListener { v, position ->
                val selectList: List<LocalMedia> = data
                if (selectList.isNotEmpty()) {
                    val media = selectList[position]
                    val mimeType = media.mimeType
                    val mediaType = PictureMimeType.getMimeType(mimeType)
                    when (mediaType) {
                        PictureConfig.TYPE_IMAGE -> {
                            // 预览图片 可自定长按保存路径
                            PictureSelector
                                .create(this@IncidentDetailActivity)
                                .themeStyle(R.style.picture_default_style)
                                .isNotPreviewDownload(true)
                                .imageEngine(GlideEngine.createGlideEngine())
                                .openExternalPreview(position, selectList)
                        }
                    }
                }
            }
            setSelectMax(0)
            setPureDisplay(true)
        }
    }

    val videoAdapter : GridImageAdapter by lazy {
        GridImageAdapter(this,null).apply {
            setOnItemClickListener { v, position ->
                val selectList: List<LocalMedia> = data
                if (selectList.isNotEmpty()) {
                    val media = selectList[position]
                    val mimeType = media.mimeType
                    val mediaType = PictureMimeType.getMimeType(mimeType)
                    when (mediaType) {
                        PictureConfig.TYPE_VIDEO -> {
                            // 预览视频
                            PictureSelector
                                .create(this@IncidentDetailActivity)
                                .themeStyle(R.style.picture_default_style)
                                .externalPictureVideo(if (TextUtils.isEmpty(media.androidQToPath)) media.path else media.androidQToPath)
                        }
                    }
                }
            }
            setSelectMax(0)
            setPureDisplay(true)
        }
    }

    val recordUtils : RecordUtils by lazy(LazyThreadSafetyMode.SYNCHRONIZED ) { RecordUtils()}
    val audioAdapter : AlarmHandleAudioAdapter by lazy {
        AlarmHandleAudioAdapter().apply {
            itemBinding = ItemBinding.of(ItemBinding.VAR_NONE, R.layout.recycleview_alarm_handle_audio_item)
            setItemOnClickListener(object : AlarmHandleAudioAdapter.ItemOnClickListener{
                override fun delete(position: Int, item: LocalMedia) {
                }

                override fun play(position: Int, item: LocalMedia) {
                    recordUtils.initPlayer(if (PictureMimeType.isContent(item.path)) item.androidQToPath else item.path)
                }
            })
            setPureDisplay(true)
        }
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_incident_detail
    }

    override fun initViewModel(): IncidentDetailViewModel? {
        return viewModels<IncidentDetailViewModel>().value
    }

    override fun initView() {
        super.initView()

        recordUtils.setPlayListener(this)
    }

    override fun initData() {
        super.initData()

        viewModel.getIncidentDetail(id)
    }

    override fun initViewObservable() {
        super.initViewObservable()

        viewModel.detailObservable.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                viewModel.detailObservable.get()?.takeIf { true }?.apply {
                    initImageRecyclerView(imgs)
                    initVideoRecyclerView(videos)
                    initAudioRecyclerView(audios)
                }
            }
        })

    }



    private fun initImageRecyclerView(data: List<Annex>) {

        if (data.isNullOrEmpty()){
            binding.imageGroup.isVisible = false
            return
        }

        binding.imageGroup.isVisible = true
        imageAdapter.setList(data.map {
            val localMedia = LocalMedia()
            localMedia.path = it.url
            localMedia.duration = if (it.time.isNullOrBlank()) 0L else it.time.toLong()*1000
            localMedia.mimeType = when (it.type) {
                1 -> PictureMimeType.MIME_TYPE_IMAGE
                2 -> PictureMimeType.MIME_TYPE_VIDEO
                4 -> PictureMimeType.MIME_TYPE_AUDIO
                else -> "ANNEX"
            }
            return@map localMedia
        })

        binding.image.apply {
            val spanCount = 4
            layoutManager =
                FullyGridLayoutManager(
                    this@IncidentDetailActivity,
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

    private fun initVideoRecyclerView(data: List<Annex>) {

        if (data.isNullOrEmpty()){
            binding.videoGroup.isVisible = false
            return
        }

        binding.videoGroup.isVisible = true
        videoAdapter.setList(data.map {
            val localMedia = LocalMedia()
            localMedia.path = it.url
            localMedia.duration = if (it.time.isNullOrBlank()) 0L else it.time.toLong()*1000
            localMedia.mimeType = when (it.type) {
                1 -> PictureMimeType.MIME_TYPE_IMAGE
                2 -> PictureMimeType.MIME_TYPE_VIDEO
                4 -> PictureMimeType.MIME_TYPE_AUDIO
                else -> "ANNEX"
            }
            return@map localMedia
        })

        binding.video.apply {
            val spanCount = 4
            layoutManager =
                FullyGridLayoutManager(
                    this@IncidentDetailActivity,
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

    private fun initAudioRecyclerView(data: List<Annex>) {

        if (data.isNullOrEmpty()){
            binding.audioGroup.isVisible = false
            return
        }

        binding.audioGroup.isVisible = true
        audioAdapter.setItems(data.map {
            val localMedia = LocalMedia()
            localMedia.path = it.url
            localMedia.duration = if (it.time.isNullOrBlank()) 0L else it.time.toLong()*1000
            localMedia.mimeType = when (it.type) {
                1 -> PictureMimeType.MIME_TYPE_IMAGE
                2 -> PictureMimeType.MIME_TYPE_VIDEO
                4 -> PictureMimeType.MIME_TYPE_AUDIO
                else -> "ANNEX"
            }
            return@map localMedia
        })

        binding.audio.apply {
            val spanCount = 4
            layoutManager =
                FullyGridLayoutManager(
                    this@IncidentDetailActivity,
                    spanCount,
                    GridLayoutManager.VERTICAL,
                    false
                )
            if (itemDecorationCount == 0) {
                val spacing = 0.dp
                val itemDecoration = GridSpacingItemDecoration(spanCount, spacing, true)
                addItemDecoration(itemDecoration)
            }
            adapter = audioAdapter
        }

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
        recordUtils.stopPlay()
    }
}