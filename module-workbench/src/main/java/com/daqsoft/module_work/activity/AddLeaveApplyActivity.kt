package com.daqsoft.module_work.activity

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.bigkoo.pickerview.view.TimePickerView
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.GlideEngine
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.library_common.adapter.GridImageAdapter
import com.daqsoft.library_common.adapter.TaskFilterPeriodAdapter
import com.daqsoft.library_common.bean.LeaveType
import com.daqsoft.library_common.warrper.FullyGridLayoutManager
import com.daqsoft.library_common.widget.LeaveTypePopup
import com.daqsoft.module_work.BR
import com.daqsoft.module_work.R
import com.daqsoft.module_work.adapter.CcStaffAdapter
import com.daqsoft.module_work.adapter.SupplementCardApproveAdapter
import com.daqsoft.module_work.adapter.SupplementCardCarbonCopyAdapter
import com.daqsoft.module_work.databinding.ActivityAddLeaveApplyBinding
import com.daqsoft.module_work.viewmodel.AddLeaveApplyViewModel
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
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

/**
 * @ClassName    AskLeaveActivity
 * @Description
 * @Author       yuxc
 * @CreateDate   2021/5/7
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.ADD_LEAVE_APPLY)
class AddLeaveApplyActivity : AppBaseActivity<ActivityAddLeaveApplyBinding, AddLeaveApplyViewModel>(),
    GridImageAdapter.onAddPicClickListener {

    companion object {
        /**
         * 图片个数
         */
        const val MAX_NUMBER: Int = 1
    }

    lateinit var timePicker: TimePickerView

    var mPicAdapter: GridImageAdapter? = null


    val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            val extras: Bundle?
            when (action) {
                BroadcastAction.ACTION_DELETE_PREVIEW_POSITION -> {
                    // 外部预览删除按钮回调
                    extras = intent.extras
                    val position = extras!!.getInt(PictureConfig.EXTRA_PREVIEW_DELETE_POSITION)
                    if (position < mPicAdapter!!.itemCount) {
                        mPicAdapter?.delete(position)
                    }
                }
            }
        }
    }

    val approveAdapter: SupplementCardApproveAdapter by lazy {
        SupplementCardApproveAdapter().apply {
            itemBinding = ItemBinding.of(ItemBinding.VAR_NONE, R.layout.recycleview_supplement_card_approve_item)
            setOnClickListener(object : SupplementCardApproveAdapter.OnClickListener {
                override fun onAddClick(listPosition: Int) {

                }
            })
            val data = mutableListOf<Pair<String, List<String>>>()
            val approve1 = listOf<String>()
            data.add(Pair("部门负责人", approve1))

            val approve2 = listOf<String>("赵小刚", "张曦月", "张玉霞", "范诗雨")
            data.add(Pair("行政", approve2))

            val approve3 = listOf<String>("令狐冲")
            data.add(Pair("财务", approve3))

            setItems(data)
        }
    }

    val carbonCopyAdapter: SupplementCardCarbonCopyAdapter by lazy {
        SupplementCardCarbonCopyAdapter(this).apply {
            setData(arrayListOf("赵小刚", "张曦月", "张玉霞", "范诗雨"))
            setOnClickListener(object : SupplementCardCarbonCopyAdapter.OnClickListener {
                override fun onAddClick() {

                }
            })
        }
    }

    val leaveTypePopup: LeaveTypePopup by lazy {
        LeaveTypePopup(this)
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_add_leave_apply
    }

    override fun initViewModel(): AddLeaveApplyViewModel? {
        return viewModels<AddLeaveApplyViewModel>().value
    }

    override fun initView() {
        super.initView()

        initImageRecyclerView()
        initApproveRecyclerView()
        initCarbonCopyRecyclerView()
        initOnClick()
    }

    private fun initImageRecyclerView() {
        mPicAdapter = GridImageAdapter(this, R.mipmap.fqqjsq_xztp, this)
        mPicAdapter!!.setOnItemClickListener { v, position ->
            val selectList: List<LocalMedia> = mPicAdapter!!.data
            if (selectList.isNotEmpty()) {
                val media = selectList[position]
                val mimeType = media.mimeType
                val mediaType = PictureMimeType.getMimeType(mimeType)
                when (mediaType) {
                    PictureConfig.TYPE_VIDEO -> {
                        // 预览视频
                        PictureSelector
                            .create(this)
                            .themeStyle(R.style.picture_default_style)
                            .setPictureStyle(PictureParameterStyle.ofDefaultStyle())
                            .externalPictureVideo(if (TextUtils.isEmpty(media.androidQToPath)) media.path else media.androidQToPath)
                    }
                    PictureConfig.TYPE_AUDIO -> {
                        // 预览音频
                        PictureSelector
                            .create(this)
                            .externalPictureAudio(if (PictureMimeType.isContent(media.path)) media.androidQToPath else media.path)
                    }
                    PictureConfig.TYPE_IMAGE -> {
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
        mPicAdapter?.setSelectMax(MAX_NUMBER)
        binding.picRecycleView.apply {
            val spanCount = 4
            layoutManager =
                FullyGridLayoutManager(
                    this@AddLeaveApplyActivity,
                    spanCount,
                    GridLayoutManager.VERTICAL,
                    false
                )
            if (itemDecorationCount == 0) {
                val spacing = 0.dp
                val itemDecoration = GridSpacingItemDecoration(spanCount, spacing, true)
                addItemDecoration(itemDecoration)
            }
            adapter = mPicAdapter
        }

        BroadcastManager.getInstance(this).registerReceiver(
            broadcastReceiver,
            BroadcastAction.ACTION_DELETE_PREVIEW_POSITION
        )
    }

    private fun initApproveRecyclerView() {
        binding.approve.apply {
            layoutManager = LinearLayoutManager(this@AddLeaveApplyActivity)
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

    private fun initOnClick() {
        binding.submit.setOnClickListenerThrottleFirst {
            Timber.e("Approve ${approveAdapter.getData()}")
        }
    }

    override fun initViewObservable() {
        super.initViewObservable()

        viewModel.LeaveTypeLiveData.observe(this, androidx.lifecycle.Observer {
            showChooseTypePopup()
        })

        viewModel.StartTimeLiveData.observe(this, androidx.lifecycle.Observer {
            showDatePicker(true)
        })

        viewModel.EndTimeLiveData.observe(this, androidx.lifecycle.Observer {
            showDatePicker(false)
        })


        // 头像
        viewModel.avatarLiveData.observe(this, androidx.lifecycle.Observer {
            requestPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                callback = { jumpAlbum() })
        })

    }


    /**
     * 请假类型
     */
    var selectBean: LeaveType? = null
    fun showChooseTypePopup() {
        XPopup
            .Builder(this)
            .moveUpToKeyboard(false)
            .asCustom(leaveTypePopup.apply {
                setItemOnClickListener(object : LeaveTypePopup.ItemOnClickListener {
                    override fun onClick(bean: LeaveType?) {
                        selectBean = bean
                        binding?.tvChooseType.text = bean?.name
                    }
                })
                setData(viewModel.LeaveTypeChoose, selectBean)
            })
            .show()
    }

    /**
     * 时间选择器
     * isStartTime是不是开始时间
     * isDay =true 年月日 =false 年月日时分
     */
    fun showDatePicker(isStartTime: Boolean) {
        val startDate = Calendar.getInstance()
        val endDate = Calendar.getInstance()
        val selectedDate = Calendar.getInstance() //系统当前时间
        // 正确设置方式 原因：注意事项有说明
        startDate[1970, 1] = 1
        endDate[2030, 12] = 31
        timePicker = TimePickerBuilder(this,
            OnTimeSelectListener { date, v ->
                val stampToTime = stampToTime(date.time.toString())
                if (isStartTime) {
                    binding.tvStartTime.text = stampToTime
                } else {
                    binding.tvEndTime.text = stampToTime
                }
            }).setTimeSelectChangeListener { }
            .addOnCancelClickListener { }
            .setType(booleanArrayOf(true, true, true, viewModel.haveHour?.value ?: false, viewModel.haveHour?.value ?: false, false))
            .isDialog(true)
            // 默认设置false ，内部实现将DecorView 作为它的父控件。
            .setItemVisibleCount(5)
            // 若设置偶数，实际值会加1（比如设置6，则最大可见条目为7）
            .setLineSpacingMultiplier(2.0f)
            .isAlphaGradient(true)
            .setCancelText("取消")
            // 取消按钮文字
            .setSubmitText("确认")
            .setDate(selectedDate)
            // 确认按钮文字
            .setTitleSize(20)
            // 标题文字大小
            .setOutSideCancelable(true)
            // 点击屏幕，点在控件外部范围时，是否取消显示
            .isCyclic(true)
            // 是否循环滚动
            .setTitleBgColor(-0xa0a0b)
            // 标题背景颜色 Night mode
            .setSubmitColor(
                resources.getColor(R.color.color_59abff)
            ) //确定按钮文字颜色
            .setCancelColor(
                resources.getColor(R.color.color_333333)
            )
            // 取消按钮文字颜色
            .setRangDate(startDate, endDate)
            // 起始终止年月日设定
            // 是否只显示中间选中项的label文字，false则每项item全部都带有label。
            .isCenterLabel(false)
            .build()
        val mDialog = timePicker.dialog
        if (mDialog != null) {
            val params = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM
            )
            params.leftMargin = 0
            params.rightMargin = 0
            timePicker.dialogContainerLayout.layoutParams = params
            val dialogWindow = mDialog.window
            if (dialogWindow != null) {
                dialogWindow.setWindowAnimations(R.style.picker_view_slide_anim)
                dialogWindow.setGravity(Gravity.BOTTOM)
                dialogWindow.setDimAmount(0.3f)
            }
        }
        timePicker?.show()
    }


    override fun onDestroy() {
        super.onDestroy()

        BroadcastManager.getInstance(this).unregisterReceiver(
            broadcastReceiver,
            BroadcastAction.ACTION_DELETE_PREVIEW_POSITION
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST -> {
                    // 图片选择结果回调
                    val selectList = PictureSelector.obtainMultipleResult(data)
                    if (!selectList.isNullOrEmpty()) {
                        mPicAdapter?.setList(selectList)
                        mPicAdapter?.notifyDataSetChanged()
                    }
                }
                else -> {
                }
            }
        }
    }


    fun stampToTime(stamp: String): String {
        var type = ""
        if (viewModel.haveHour?.value!!) {
            type = "yyyy-MM-dd HH-mm"
        } else {
            type = "yyyy-MM-dd"
        }
        val simpleDateFormat = SimpleDateFormat(type)
        val lt: Long = stamp.toLong()
        val date = Date(lt)
        return simpleDateFormat.format(date)
    }

    /**
     * 点击添加图片
     */
    override fun onAddPicClick() {
        jumpAlbum()
    }

    /**
     * 跳转相册
     */
    private fun jumpAlbum() {
        PictureSelector
            .create(this)
            // 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
            .openGallery(PictureMimeType.ofAll())
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
            .selectionData(mPicAdapter!!.data)
            //结果回调onActivityResult code
            .forResult(PictureConfig.CHOOSE_REQUEST)
    }
}