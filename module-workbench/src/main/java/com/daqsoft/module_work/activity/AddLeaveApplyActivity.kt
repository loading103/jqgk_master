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
         * ????????????
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
                    // ??????????????????????????????
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
            data.add(Pair("???????????????", approve1))

            val approve2 = listOf<String>("?????????", "?????????", "?????????", "?????????")
            data.add(Pair("??????", approve2))

            val approve3 = listOf<String>("?????????")
            data.add(Pair("??????", approve3))

            setItems(data)
        }
    }

    val carbonCopyAdapter: SupplementCardCarbonCopyAdapter by lazy {
        SupplementCardCarbonCopyAdapter(this).apply {
            setData(arrayListOf("?????????", "?????????", "?????????", "?????????"))
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
                        // ????????????
                        PictureSelector
                            .create(this)
                            .themeStyle(R.style.picture_default_style)
                            .setPictureStyle(PictureParameterStyle.ofDefaultStyle())
                            .externalPictureVideo(if (TextUtils.isEmpty(media.androidQToPath)) media.path else media.androidQToPath)
                    }
                    PictureConfig.TYPE_AUDIO -> {
                        // ????????????
                        PictureSelector
                            .create(this)
                            .externalPictureAudio(if (PictureMimeType.isContent(media.path)) media.androidQToPath else media.path)
                    }
                    PictureConfig.TYPE_IMAGE -> {
                        // ???????????? ???????????????????????????
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


        // ??????
        viewModel.avatarLiveData.observe(this, androidx.lifecycle.Observer {
            requestPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                callback = { jumpAlbum() })
        })

    }


    /**
     * ????????????
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
     * ???????????????
     * isStartTime?????????????????????
     * isDay =true ????????? =false ???????????????
     */
    fun showDatePicker(isStartTime: Boolean) {
        val startDate = Calendar.getInstance()
        val endDate = Calendar.getInstance()
        val selectedDate = Calendar.getInstance() //??????????????????
        // ?????????????????? ??????????????????????????????
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
            // ????????????false ??????????????????DecorView ????????????????????????
            .setItemVisibleCount(5)
            // ?????????????????????????????????1???????????????6???????????????????????????7???
            .setLineSpacingMultiplier(2.0f)
            .isAlphaGradient(true)
            .setCancelText("??????")
            // ??????????????????
            .setSubmitText("??????")
            .setDate(selectedDate)
            // ??????????????????
            .setTitleSize(20)
            // ??????????????????
            .setOutSideCancelable(true)
            // ???????????????????????????????????????????????????????????????
            .isCyclic(true)
            // ??????????????????
            .setTitleBgColor(-0xa0a0b)
            // ?????????????????? Night mode
            .setSubmitColor(
                resources.getColor(R.color.color_59abff)
            ) //????????????????????????
            .setCancelColor(
                resources.getColor(R.color.color_333333)
            )
            // ????????????????????????
            .setRangDate(startDate, endDate)
            // ???????????????????????????
            // ?????????????????????????????????label?????????false?????????item???????????????label???
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
                    // ????????????????????????
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
     * ??????????????????
     */
    override fun onAddPicClick() {
        jumpAlbum()
    }

    /**
     * ????????????
     */
    private fun jumpAlbum() {
        PictureSelector
            .create(this)
            // ??????.PictureMimeType.ofAll()?????????.ofImage()?????????.ofVideo()?????????.ofAudio()
            .openGallery(PictureMimeType.ofAll())
            // ??????????????????????????????????????????
            .imageEngine(GlideEngine.createGlideEngine())
            // ????????????????????????
            .maxSelectNum(MAX_NUMBER)
            // ??????????????????
            .minSelectNum(0)
            // ????????????????????????
            .maxVideoSelectNum(MAX_NUMBER)
            // ????????????????????????
            .minVideoSelectNum(0)
            // ??????????????????
            .imageSpanCount(4)
            // ????????????Activity????????????????????????????????????
            //            .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            // ????????????????????????????????????????????????true?????????????????????????????????????????????????????????????????????????????????
            .isOriginalImageControl(true)
            // ?????? or ??????
            .selectionMode(PictureConfig.MULTIPLE)
            // ????????????????????????????????????PictureConfig.SINGLE???????????????
            .isSingleDirectReturn(false)
            // ?????????????????????
            .isPreviewImage(true)
            // ?????????????????????
            .isPreviewVideo(true)
            // ?????????????????????
            .enablePreviewAudio(true)
            // ????????????????????????
            .isCamera(true)
            // ?????????????????? ???????????? ??????true
            .isZoomAnim(true)
            // ??????????????????????????? 0~ 100
            .compressQuality(80)
            //??????false?????????true ?????? ????????????
            .synOrAsy(true)
            // ????????????gif??????
            .isGif(false)
            // ?????????????????? ??????100
            .cutOutQuality(90)
            // ????????????
            //            .isEnableCrop(true)
            // ??????100kb??????????????????
            .minimumCompressSize(100)
            // ?????????????????????????????????,??????ofAll???????????????
            .isWithVideoImage(true)
            // ????????????????????????
            .selectionData(mPicAdapter!!.data)
            //????????????onActivityResult code
            .forResult(PictureConfig.CHOOSE_REQUEST)
    }
}