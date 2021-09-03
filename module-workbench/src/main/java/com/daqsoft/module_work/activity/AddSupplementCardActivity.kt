package com.daqsoft.module_work.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.GlideEngine
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.library_common.adapter.GridImageAdapter
import com.daqsoft.library_common.warrper.FullyGridLayoutManager
import com.daqsoft.module_work.BR
import com.daqsoft.module_work.R
import com.daqsoft.module_work.adapter.SupplementCardApproveAdapter
import com.daqsoft.module_work.adapter.SupplementCardCarbonCopyAdapter
import com.daqsoft.module_work.databinding.ActivityAddSupplementCardBinding
import com.daqsoft.module_work.viewmodel.AddSupplementCardViewModel
import com.daqsoft.module_work.widget.SupplementCardDatePopup
import com.daqsoft.mvvmfoundation.utils.dp
import com.haibin.calendarview.Calendar
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.broadcast.BroadcastAction
import com.luck.picture.lib.broadcast.BroadcastManager
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.decoration.GridSpacingItemDecoration
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.style.PictureParameterStyle
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.interfaces.SimpleCallback
import dagger.hilt.android.AndroidEntryPoint
import me.tatarka.bindingcollectionadapter2.ItemBinding
import timber.log.Timber

/**
 * @package name：com.daqsoft.module_work.activity
 * @date 7/7/2021 上午 10:02
 * @author zp
 * @describe  新增补卡
 */

@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_ADD_SUPPLEMENT_CARD)
class AddSupplementCardActivity  : AppBaseActivity<ActivityAddSupplementCardBinding, AddSupplementCardViewModel>()
    ,GridImageAdapter.onAddPicClickListener
{

    companion object{
        const val MAX_NUMBER : Int = 1

        const val APPROVE : Int = 100
        const val CARBON_COPY : Int = 200
    }

    val imageAdapter : GridImageAdapter by lazy {
        GridImageAdapter(this,R.mipmap.fqqjsq_xztp ,this).apply {
            setOnItemClickListener { v, position ->
                val selectList: List<LocalMedia> = imageAdapter.data
                if (selectList.isNotEmpty()) {
                    val media = selectList[position]
                    val mimeType = media.mimeType
                    val mediaType = PictureMimeType.getMimeType(mimeType)
                    when (mediaType) {
                        PictureConfig.TYPE_VIDEO -> {
                            // 预览视频
                            PictureSelector
                                .create(this@AddSupplementCardActivity)
                                .themeStyle(R.style.picture_default_style)
                                .setPictureStyle(PictureParameterStyle.ofDefaultStyle())
                                .externalPictureVideo(if (TextUtils.isEmpty(media.androidQToPath)) media.path else media.androidQToPath)
                        }
                        PictureConfig.TYPE_AUDIO -> {
                            // 预览音频
                            PictureSelector
                                .create(this@AddSupplementCardActivity)
                                .externalPictureAudio(if (PictureMimeType.isContent(media.path)) media.androidQToPath else media.path)
                        }
                        PictureConfig.TYPE_IMAGE -> {
                            // 预览图片 可自定长按保存路径
                            PictureSelector
                                .create(this@AddSupplementCardActivity)
                                .themeStyle(R.style.picture_default_style)
                                .isNotPreviewDownload(true)
                                .imageEngine(GlideEngine.createGlideEngine())
                                .setPictureStyle(PictureParameterStyle.ofDefaultStyle())
                                .openExternalPreview(position, selectList)
                        }
                    }
                }

            }
            setSelectMax(MAX_NUMBER)
        }
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
                    if (position < imageAdapter.itemCount) {
                        imageAdapter.delete(position)
                    }
                }
            }
        }
    }

    val approveAdapter : SupplementCardApproveAdapter by lazy {
        SupplementCardApproveAdapter().apply {
            itemBinding = ItemBinding.of(ItemBinding.VAR_NONE,R.layout.recycleview_supplement_card_approve_item)
            setOnClickListener(object : SupplementCardApproveAdapter.OnClickListener{
                override fun onAddClick(listPosition: Int) {
                    ARouter
                        .getInstance()
                        .build(ARouterPath.Workbench.PAGER_ORGANIZATION_CONTAINER)
                        .navigation(this@AddSupplementCardActivity,APPROVE)
                }
            })
            val data  = mutableListOf<Pair<String,List<String>>>()
            val approve1 = listOf<String>()
            data.add(Pair("部门负责人",approve1))

            val approve2 = listOf<String>("赵小刚","张曦月","张玉霞","范诗雨")
            data.add(Pair("行政",approve2))

            val approve3 = listOf<String>("令狐冲")
            data.add(Pair("财务",approve3))

            setItems(data)
        }
    }

    val carbonCopyAdapter : SupplementCardCarbonCopyAdapter by lazy{
        SupplementCardCarbonCopyAdapter(this).apply{
            setData(arrayListOf("赵小刚","张曦月","张玉霞","范诗雨"))
            setOnClickListener(object : SupplementCardCarbonCopyAdapter.OnClickListener{
                override fun onAddClick() {
                    ARouter
                        .getInstance()
                        .build(ARouterPath.Workbench.PAGER_ORGANIZATION_CONTAINER)
                        .navigation(this@AddSupplementCardActivity, CARBON_COPY)
                }
            })
        }
    }


    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_add_supplement_card
    }

    override fun initViewModel(): AddSupplementCardViewModel? {
        return viewModels<AddSupplementCardViewModel>().value
    }

    override fun initView() {
        super.initView()
        initImageRecyclerView()
        initApproveRecyclerView()
        initCarbonCopyRecyclerView()
        initOnClick()
    }

    override fun onDestroy() {
        super.onDestroy()

        BroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver,
            BroadcastAction.ACTION_DELETE_PREVIEW_POSITION
        )
    }

    private fun initOnClick() {

        binding.date.setOnClickListenerThrottleFirst{
            showDatePicker()
        }

        binding.shift.setOnClickListenerThrottleFirst{
            showDatePicker()
        }

        binding.submit.setOnClickListenerThrottleFirst{
            Timber.e("Approve ${approveAdapter.getData()}")
        }


    }


    private fun initImageRecyclerView() {
        binding.image.apply {
            val spanCount = 4
            layoutManager = FullyGridLayoutManager(
                    this@AddSupplementCardActivity,
                    spanCount,
                    GridLayoutManager.VERTICAL,
                    false
                )
            if (itemDecorationCount == 0){
                val spacing = 0.dp
                val itemDecoration =  GridSpacingItemDecoration(spanCount,spacing, true)
                addItemDecoration(itemDecoration)
            }
            adapter = imageAdapter
        }

        BroadcastManager.getInstance(this).registerReceiver(
            broadcastReceiver,
            BroadcastAction.ACTION_DELETE_PREVIEW_POSITION
        )

    }

    private fun initApproveRecyclerView() {
        binding.approve.apply {
            layoutManager = LinearLayoutManager(this@AddSupplementCardActivity)
            adapter = approveAdapter
        }

    }

    private fun initCarbonCopyRecyclerView() {
        binding.carbonCopy.apply {
            val spanCount = 3
            layoutManager = GridLayoutManager(context!!, 3, GridLayoutManager.VERTICAL, false)
            if (itemDecorationCount == 0) {
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        val position = parent.getChildAdapterPosition(view)
                        var spacing = 8.dp
                        outRect.bottom = spacing
                        spacing = 4.dp
                        val column = position % spanCount
                        outRect.left = column * spacing / spanCount
                        outRect.right = spacing - (column + 1) * spacing / spanCount
                    }
                })
            }
            adapter = carbonCopyAdapter
        }

    }


    private fun showDatePicker(){
        XPopup
            .Builder(this)
            .isDestroyOnDismiss(true)
            .asCustom(SupplementCardDatePopup(this).apply {
                setOnClickListener(object : SupplementCardDatePopup.OnClickListener{
                    override fun determine(calendar: Calendar?, shift: String?) {
                        calendar?.let {
                            binding.date.text = "${calendar.year}年${calendar.month}月${calendar.day}日"
                        }

                        shift?.let {
                            binding.shift.text = it
                        }
                    }

                })
            })
            .show()
    }

    override fun onAddPicClick() {
        PictureSelector
            .create(this)
            // 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
            .openGallery(PictureMimeType.ofImage())
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
            .selectionMode(PictureConfig.SINGLE)
            // 单选模式下是否直接返回，PictureConfig.SINGLE模式下有效
            .isSingleDirectReturn(true)
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
            .selectionData(imageAdapter.data)
            //结果回调onActivityResult code
            .forResult(PictureConfig.CHOOSE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST ->{
                    // 图片选择结果回调
                    val selectList = PictureSelector.obtainMultipleResult(data)
                    if (!selectList.isNullOrEmpty()){
                        imageAdapter.setList(selectList)
                        imageAdapter.notifyDataSetChanged()
                    }
                }
                APPROVE->{

                }
                CARBON_COPY->{

                }
                else -> {
                }
            }
        }
    }

}