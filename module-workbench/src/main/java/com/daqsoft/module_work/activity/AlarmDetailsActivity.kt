package com.daqsoft.module_work.activity

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import androidx.databinding.Observable
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.GlideEngine
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.library_common.adapter.GridImageAdapter
import com.daqsoft.library_common.bean.Employee
import com.daqsoft.library_common.warrper.FullyGridLayoutManager
import com.daqsoft.library_common.widget.RemarkPopup
import com.daqsoft.module_work.BR
import com.daqsoft.module_work.R
import com.daqsoft.module_work.adapter.AlarmFlowAdapter
import com.daqsoft.module_work.adapter.AlarmHandleAudioAdapter
import com.daqsoft.module_work.databinding.ActivityAlarmDetailsBinding
import com.daqsoft.module_work.repository.pojo.dto.AlarmHandleRequest
import com.daqsoft.module_work.repository.pojo.dto.Annex
import com.daqsoft.module_work.utils.RecordUtils
import com.daqsoft.module_work.viewmodel.AlarmDetailsViewModel
import com.daqsoft.module_work.widget.AssignPopup
import com.daqsoft.module_work.widget.HandlePopup
import com.daqsoft.module_work.widget.RemindPopup
import com.daqsoft.mvvmfoundation.utils.dp
import com.jeremyliao.liveeventbus.LiveEventBus
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.decoration.GridSpacingItemDecoration
import com.luck.picture.lib.entity.LocalMedia
import com.lxj.xpopup.XPopup
import dagger.hilt.android.AndroidEntryPoint
import me.tatarka.bindingcollectionadapter2.ItemBinding


/**
 * @package name：com.daqsoft.module_task.activity
 * @date 14/5/2021 下午 2:32
 * @author zp
 * @describe 任务详情
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_ALARM_DETAILS)
class AlarmDetailsActivity : AppBaseActivity<ActivityAlarmDetailsBinding, AlarmDetailsViewModel>()
    ,RecordUtils.PlayListener
{


    @JvmField
    @Autowired
    var from: String = ""

    @JvmField
    @Autowired
    var id: String = ""

    @JvmField
    @Autowired
    // 0:所有告警 1:我指派的 2：我接收的
    var state : Int = 2

    var mPicAdapter : GridImageAdapter?=null


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
                                .create(this@AlarmDetailsActivity)
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
                                .create(this@AlarmDetailsActivity)
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

    val alarmFlowAdapter : AlarmFlowAdapter by lazy { AlarmFlowAdapter().apply {
        setOnClickListener(object : AlarmFlowAdapter.OnClickListener{
            override fun preview(position: Int, data: List<LocalMedia>) {
                this@AlarmDetailsActivity.preview(position, data)
            }

            override fun play(position: Int, data: LocalMedia) {
                recordUtils.initPlayer(if (PictureMimeType.isContent(data.path)) data.androidQToPath else data.path)
            }

        })

    } }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_alarm_details
    }

    override fun initViewModel(): AlarmDetailsViewModel? {
        return viewModels<AlarmDetailsViewModel>().value
    }

    override fun initView() {
        super.initView()
//        initAnnexRecyclerView()
        initFlowRecyclerView()
        recordUtils.setPlayListener(this)
    }

    private fun initFlowRecyclerView() {
        binding.flow.apply {
            adapter = alarmFlowAdapter
        }
    }


    override fun initData() {
        super.initData()
        viewModel.getAlarmDetails(id)
    }

    override fun initViewObservable() {
        super.initViewObservable()

        viewModel.detailField.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (viewModel.detailField.get()!!.phone.isNullOrBlank()){
                    runOnUiThread {
                        binding.numberGroup.isVisible = false
                    }
                }
            }

        })

        viewModel.annexEvent.observe(this, Observer {
//            mPicAdapter?.setList(it)
//            mPicAdapter?.setSelectMax(it.size)
//            mPicAdapter?.notifyDataSetChanged()

            if(it.isEmpty()){
                binding.imageGroup.isVisible = false
                binding.videoGroup.isVisible = false
                binding.audioGroup.isVisible = false
                return@Observer
            }

            val imgs = it.filter { it.mimeType == PictureMimeType.MIME_TYPE_IMAGE }
            val videos = it.filter { it.mimeType == PictureMimeType.MIME_TYPE_VIDEO }
            val audios = it.filter { it.mimeType == PictureMimeType.MIME_TYPE_AUDIO }

            initImageRecyclerView(imgs)
            initVideoRecyclerView(videos)
            initAudioRecyclerView(audios)

        })

        viewModel.purviewEvent.observe(this, Observer {

            assignPopup?.dismiss()
            returnPopup?.dismiss()
            invalidPopup?.dismiss()


            if(initProcessCl){
                return@Observer
            }
            if (it){
                initProcessCl()
            }else{
                binding.processCl.visibility = View.GONE
            }
        })


        viewModel.employeeLiveEvent.observe(this, Observer {
            assignPopup?.setEmployeeData(it)
        })

        LiveEventBus.get(LEBKeyGlobal.ALARM_HANDLE,Boolean::class.java).observe(this, Observer {
            initData()
        })
        LiveEventBus.get(LEBKeyGlobal.ALARM_HANDLE_FINISH,Boolean::class.java).observe(this, Observer {
           finish()
        })
    }

//    private fun initAnnexRecyclerView() {
//
//        mPicAdapter = GridImageAdapter(this, null)
//        mPicAdapter?.setPureDisplay(true)
//        mPicAdapter!!.setOnItemClickListener { v, position ->
//            val selectList: List<LocalMedia> = mPicAdapter!!.data
//            this@AlarmDetailsActivity.preview(position,selectList)
//        }
//
//        binding.annex.apply {
//            val spanCount = 4
//            layoutManager =
//                FullyGridLayoutManager(
//                    this@AlarmDetailsActivity,
//                    spanCount,
//                    GridLayoutManager.VERTICAL,
//                    false
//                )
//            if (itemDecorationCount == 0) {
//                val spacing = 0.dp
//                val itemDecoration = GridSpacingItemDecoration(spanCount, spacing, true)
//                addItemDecoration(itemDecoration)
//            }
//            adapter = mPicAdapter
//        }
//
//    }



    private fun preview(position:Int,data:List<LocalMedia>){
        if (data.isNotEmpty()) {
            val media = data[position]
            val mimeType = media.mimeType
            val mediaType = PictureMimeType.getMimeType(mimeType)
            when (mediaType) {
                PictureConfig.TYPE_VIDEO ->{
                    // 预览视频
                    PictureSelector
                        .create(this)
                        .themeStyle(R.style.picture_default_style)

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
                        .openExternalPreview(position, data)
                }
            }
        }
    }


    var initProcessCl  = false
    private fun initProcessCl() {
        var visibility = true
//        binding.processCl.removeAllViews()
        when(viewModel.detailField.get()!!.status){
            1 -> {
                when(state){
                    0 ->{
                        visibility = true
                        val views = arrayListOf<View>()
                        val process = createBottomButton("处理") {
                            ARouter
                                .getInstance()
                                .build(ARouterPath.Workbench.PAGER_ALARM_HANDLE)
                                .withInt("state",state)
                                .withString("id",viewModel.detailField.get()!!.eventId.toString())
                                .navigation()
                        }
                        process.id = R.id.d_process
                        views.add(process)
                        val designate = createBottomButton("指派") {
                            showAssignPopup(){ r,e ->
                                val body = AlarmHandleRequest(this@AlarmDetailsActivity.id,r,1, e.id.toString())
                                viewModel.alarmHandle(body)
                            }
                        }
                        designate.id = R.id.d_designate
                        views.add(designate)
                        if (viewModel.detailField.get()!!.type == 1){
                            val invalid = createBottomButton("无效报警") {
                                showRemindPopup("提醒","请确认是否为无效报警？") {
                                    val body = AlarmHandleRequest(this@AlarmDetailsActivity.id,"",3)
                                    viewModel.alarmHandle(body)
                                }
                            }
                            invalid.id = R.id.d_invalid
                            views.add(invalid)
                        }
                        bottomButtonLayoutParams(views)
                    }
                    1 ->{
                        visibility = true
                        val views = arrayListOf<View>()
                        val process = createBottomButton("处理") {
                            ARouter
                                .getInstance()
                                .build(ARouterPath.Workbench.PAGER_ALARM_HANDLE)
                                .withInt("state",state)
                                .withString("id",viewModel.detailField.get()!!.eventId.toString())
                                .navigation()
                        }
                        process.id = R.id.d_process
                        views.add(process)
                        val designate = createBottomButton("指派") {
                            showAssignPopup(){r,e ->
                                val body = AlarmHandleRequest(this@AlarmDetailsActivity.id,r,1, e.id.toString())
                                viewModel.alarmHandle(body)
                            }
                        }
                        designate.id = R.id.d_designate
                        views.add(designate)
                        // 暂时不要
//                        val report = createBottomButton("上报领导") {
//                            showAssignPopup(){ r,e ->
//                                val body = AlarmHandleRequest(this@AlarmDetailsActivity.id,r,1, e.id.toString())
//                                viewModel.alarmHandle(body)
//                            }
//                        }
//                        report.id = R.id.d_report
//                        views.add(report)
                        bottomButtonLayoutParams(views)
                    }
                    2 ->{
                        visibility = true
                        val views = arrayListOf<View>()
                        val process = createBottomButton("处理") {
                            ARouter
                                .getInstance()
                                .build(ARouterPath.Workbench.PAGER_ALARM_HANDLE)
                                .withInt("state",state)
                                .withString("id",viewModel.detailField.get()!!.eventId.toString())
                                .navigation()
                        }
                        process.id = R.id.d_process
                        views.add(process)
//                        val designate = createBottomButton("任务指派") {
//                            showAssignPopup(){r,e ->
//                                val body = AlarmHandleRequest(this@AlarmDetailsActivity.id,r,1, e.id.toString())
//                                viewModel.alarmHandle(body)
//                            }
//                        }
//                        designate.id = R.id.d_designate
//                        views.add(designate)

                        val withdraw = createBottomButton("退回") {
                            showRemarkPopup("退回原由", "请输入退回原由", "提交"){
                                val body = AlarmHandleRequest(this@AlarmDetailsActivity.id,it,8)
                                viewModel.alarmHandle(body)
                            }
                        }

                        withdraw.id = R.id.d_withdraw
                        views.add(withdraw)
                        // 暂时不要
//                        val report = createBottomButton("上报领导") {
//                            showAssignPopup(){ r,e ->
//                                val body = AlarmHandleRequest(this@AlarmDetailsActivity.id,r,1, e.id.toString())
//                                viewModel.alarmHandle(body)
//                            }
//                        }
//                        report.id = R.id.d_report
//                        views.add(report)
                        if (viewModel.detailField.get()!!.type == 1){
                            val invalid = createBottomButton("无效报警") {
                                showRemindPopup("提醒","请确认是否为无效报警？") {
                                    val body = AlarmHandleRequest(this@AlarmDetailsActivity.id,"",3)
                                    viewModel.alarmHandle(body)
                                }
                            }
                            invalid.id = R.id.d_invalid
                            views.add(invalid)
                        }
                        bottomButtonLayoutParams(views)
                    }
                }
            }
            2 -> {
//                when(state){
//                    0 ->{
//                        visibility = false
//                    }
//                    1 ->{
//                        visibility = false
//                    }
//                    2 ->{
//                        visibility = true
//                        val views = arrayListOf<View>()
//                        val process = createBottomButton("处理") {
//                            ARouter
//                                .getInstance()
//                                .build(ARouterPath.Workbench.PAGER_ALARM_HANDLE)
//                                .withInt("state",state)
//                                .withString("id",viewModel.detailField.get()!!.eventId.toString())
//                                .navigation()
//                        }
//                        process.id = R.id.d_process
//                        views.add(process)
//                        bottomButtonLayoutParams(views)
//                    }
//                }
                if(viewModel.detailField.get()!!.operable){
                    visibility = true
                    val views = arrayListOf<View>()
                    val process = createBottomButton("处理") {
                        ARouter
                            .getInstance()
                            .build(ARouterPath.Workbench.PAGER_ALARM_HANDLE)
                            .withInt("state",state)
                            .withString("id",viewModel.detailField.get()!!.eventId.toString())
                            .navigation()
                    }
                    process.id = R.id.d_process
                    views.add(process)
                    bottomButtonLayoutParams(views)
                }
            }
            3 -> {
//                when(state){
//                    0 ->{
//                        visibility = false
//                    }
//                    1 ->{
//                        visibility = true
//                        val views = arrayListOf<View>()
//                        val finish = createBottomButton("确认办结") {
//                            showRemindPopup("提醒","是否确定该事件办结？") {
//                                val body = AlarmHandleRequest(this@AlarmDetailsActivity.id,"",4)
//                                viewModel.alarmHandle(body)
//                            }
//                        }
//                        finish.id = R.id.d_finish
//                        views.add(finish)
//                        val again = createBottomButton("重新处理") {
//                            showRemarkPopup("重新处理", "请输入办理结果", "提交"){
//                                val body = AlarmHandleRequest(this@AlarmDetailsActivity.id,it,5)
//                                viewModel.alarmHandle(body)
//                            }
//                        }
//                        again.id = R.id.d_again
//                        views.add(again)
//                        bottomButtonLayoutParams(views)
//                    }
//                    2 ->{
//                        visibility = false
//                    }
//                }

                if(viewModel.detailField.get()!!.operable){
                    visibility = true
                    val views = arrayListOf<View>()
                    val finish = createBottomButton("确认办结") {
                        showRemindPopup("提醒","是否确定该事件办结？") {
                            val body = AlarmHandleRequest(this@AlarmDetailsActivity.id,"",4)
                            viewModel.alarmHandle(body)
                        }
                    }
                    finish.id = R.id.d_finish
                    views.add(finish)
                    val again = createBottomButton("重新处理") {
                        showRemarkPopup("重新处理", "请输入办理结果", "提交"){
                            val body = AlarmHandleRequest(this@AlarmDetailsActivity.id,it,5)
                            viewModel.alarmHandle(body)
                        }
                    }
                    again.id = R.id.d_again
                    views.add(again)
                    bottomButtonLayoutParams(views)
                }
            }
            4 -> {
                visibility = false
            }
            5 -> {
//                when(state){
//                    0 ->{
//                        visibility = false
//                    }
//                    1 ->{
//                        visibility = false
//                    }
//                    2 ->{
//                        visibility = true
//                        val views = arrayListOf<View>()
//                        val process = createBottomButton("处理") {
//                            ARouter
//                                .getInstance()
//                                .build(ARouterPath.Workbench.PAGER_ALARM_HANDLE)
//                                .withInt("state",state)
//                                .withString("id",viewModel.detailField.get()!!.eventId.toString())
//                                .navigation()
//                        }
//                        process.id = R.id.d_process
//                        views.add(process)
//                        val withdraw = createBottomButton("退回") {
//                            showRemarkPopup("退回原由", "请输入退回原由", "提交"){
//                                val body = AlarmHandleRequest(this@AlarmDetailsActivity.id,it,8)
//                                viewModel.alarmHandle(body)
//                            }
//                        }
//                        withdraw.id = R.id.d_withdraw
//                        views.add(withdraw)
//                        bottomButtonLayoutParams(views)
//                    }
//                }
                if(viewModel.detailField.get()!!.operable){
                    visibility = true
                    val views = arrayListOf<View>()
                    val process = createBottomButton("处理") {
                        ARouter
                            .getInstance()
                            .build(ARouterPath.Workbench.PAGER_ALARM_HANDLE)
                            .withInt("state",state)
                            .withString("id",viewModel.detailField.get()!!.eventId.toString())
                            .navigation()
                    }
                    process.id = R.id.d_process
                    views.add(process)
                    val withdraw = createBottomButton("退回") {
                        showRemarkPopup("退回原由", "请输入退回原由", "提交"){
                            val body = AlarmHandleRequest(this@AlarmDetailsActivity.id,it,8)
                            viewModel.alarmHandle(body)
                        }
                    }
                    withdraw.id = R.id.d_withdraw
                    views.add(withdraw)
                    bottomButtonLayoutParams(views)
                }
            }
            6 -> {
                visibility = false
            }
            7 -> {
                visibility = false
            }
            8 -> {
//                when(state){
//                    0 ->{
//                        visibility = false
//                    }
//                    1 ->{
//                        visibility = true
//                        val views = arrayListOf<View>()
//                        val process = createBottomButton("处理") {
//                            ARouter
//                                .getInstance()
//                                .build(ARouterPath.Workbench.PAGER_ALARM_HANDLE)
//                                .withInt("state",state)
//                                .withString("id",viewModel.detailField.get()!!.eventId.toString())
//                                .navigation()
//                        }
//                        process.id = R.id.d_process
//                        views.add(process)
//                        val designate = createBottomButton("指派") {
//                            showAssignPopup(){r,e ->
//                                val body = AlarmHandleRequest(this@AlarmDetailsActivity.id,r,1, e.id.toString())
//                                viewModel.alarmHandle(body)
//                            }
//                        }
//                        designate.id = R.id.d_designate
//                        views.add(designate)
//                        bottomButtonLayoutParams(views)
//                    }
//                    2 ->{
//                        visibility = false
//                    }
//                }

                if(viewModel.detailField.get()!!.operable){
                    visibility = true
                    val views = arrayListOf<View>()
                    val process = createBottomButton("处理") {
                        ARouter
                            .getInstance()
                            .build(ARouterPath.Workbench.PAGER_ALARM_HANDLE)
                            .withInt("state",state)
                            .withString("id",viewModel.detailField.get()!!.eventId.toString())
                            .navigation()
                    }
                    process.id = R.id.d_process
                    views.add(process)
                    val designate = createBottomButton("指派") {
                        showAssignPopup(){r,e ->
                            val body = AlarmHandleRequest(this@AlarmDetailsActivity.id,r,1, e.id.toString())
                            viewModel.alarmHandle(body)
                        }
                    }
                    designate.id = R.id.d_designate
                    views.add(designate)
                    bottomButtonLayoutParams(views)
                }
            }
            9 ->{
                when(state){
                    1 ->{
                        if(viewModel.detailField.get()!!.operable){
                            visibility = true
                            val views = arrayListOf<View>()
                            val process = createBottomButton("直接处理") {
                                ARouter
                                    .getInstance()
                                    .build(ARouterPath.Workbench.PAGER_ALARM_HANDLE)
                                    .withInt("state",state)
                                    .withString("id",viewModel.detailField.get()!!.eventId.toString())
                                    .navigation()
                            }
                            process.id = R.id.d_process
                            views.add(process)

                            val designate = createBottomButton("重新指派") {
                                showAssignPopup(){r,e ->
                                    val body = AlarmHandleRequest(this@AlarmDetailsActivity.id,r,1, e.id.toString())
                                    viewModel.alarmHandle(body)
                                }
                            }
                            designate.id = R.id.d_designate
                            views.add(designate)
                            bottomButtonLayoutParams(views)
                        }
                    }
                    else ->{
                        if(viewModel.detailField.get()!!.operable) {
                            visibility = true
                            val views = arrayListOf<View>()
                            val process = createBottomButton("处理") {
                                ARouter
                                    .getInstance()
                                    .build(ARouterPath.Workbench.PAGER_ALARM_HANDLE)
                                    .withInt("state", state)
                                    .withString(
                                        "id",
                                        viewModel.detailField.get()!!.eventId.toString()
                                    )
                                    .navigation()
                            }
                            process.id = R.id.d_process
                            views.add(process)
                            val withdraw = createBottomButton("退回") {
                                showRemarkPopup("退回原由", "请输入退回原由", "提交") {
                                    val body =
                                        AlarmHandleRequest(this@AlarmDetailsActivity.id, it, 8)
                                    viewModel.alarmHandle(body)
                                }
                            }
                            withdraw.id = R.id.d_withdraw
                            views.add(withdraw)
                            bottomButtonLayoutParams(views)
                        }
                    }
                }
            }
        }

        binding.processCl.isVisible = visibility
        binding.processLine.isVisible = visibility
        initProcessCl = true
    }


    private fun bottomButtonLayoutParams(views:List<View>){

        val padding = if (views.size > 3) 12.dp else 20.dp

        views.forEachIndexed { index, view ->
            view.setPadding(padding,10.dp,padding,10.dp)
            val layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
            if (index == 0){
                layoutParams.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
            }else {
                layoutParams.leftToRight = views[index-1].id
            }

            if (index == views.size-1){
                layoutParams.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
            }else{
                layoutParams.rightToLeft = views[index+1].id
            }

            view.layoutParams = layoutParams
            binding.processCl.addView(view)
        }
    }


    var returnPopup : RemarkPopup? = null
    /**
     * 退回 popup
     */
    fun showRemarkPopup(title: String, hint:String, determine:String, callback: (r:String) -> Unit) {
        returnPopup = XPopup
            .Builder(this)
            .isDestroyOnDismiss(true)
            .asCustom(RemarkPopup(this, title, hint, determine).apply {
                setOnClickListener(object : RemarkPopup.OnClickListener {
                    override fun onClick(remark: String) {
                        callback(remark)
                    }
                })
            })
            .show() as RemarkPopup
    }


    var invalidPopup : RemindPopup? = null
    /**
     * 无效报警
     */
    fun showRemindPopup(title:String,content: String,callback: () -> Unit) {
        invalidPopup = XPopup
            .Builder(this)
            .isDestroyOnDismiss(true)
            .asCustom(RemindPopup(
                this,
                title,
                content
            ).apply {
                setOnClickListener(object : RemindPopup.OnClickListener {
                    override fun determine() {
                        callback()
                    }

                })
            })
            .show() as RemindPopup
    }

    /**
     * 处理
     */
    fun showHandlePopup() {
        XPopup
            .Builder(this)
            .isDestroyOnDismiss(true)
            .asCustom(HandlePopup(this).apply {
                setOnClickListener(object : HandlePopup.OnClickListener {
                    override fun handle(remark: String) {

                    }

                    override fun finish(remark: String) {

                    }

                })
            })
            .show()
    }



    var assignPopup : AssignPopup? = null
    /**
     * 指派
     */
    fun showAssignPopup(callback: (r:String,e:Employee) -> Unit) {
        viewModel.getEmployee()
        assignPopup = XPopup
            .Builder(this)
            .isDestroyOnDismiss(true)
            .asCustom(AssignPopup(this).apply {
                setOnClickListener(object : AssignPopup.OnClickListener {
                    override fun submit(remark: String, employee: Employee) {
                        callback(remark,employee)
                    }

                })
            })
            .show() as AssignPopup
    }


    fun createBottomButton(content: String, callback: () -> Unit):TextView {
        val textView = LayoutInflater.from(this)
            .inflate(R.layout.layout_alarm_detail_bottom_text, binding.processCl, false) as TextView
        textView.text = content
        textView.setOnClickListenerThrottleFirst {
            callback()
        }
        return textView
    }

    override fun playStart() {

    }

    override fun playing() {

    }

    override fun playPause() {

    }

    override fun playStop() {
        audioAdapter.releaseAnimation()
        alarmFlowAdapter.releaseAnimation()
    }


    override fun onDestroy() {
        super.onDestroy()
        recordUtils.stopPlay()
    }

    private fun initImageRecyclerView(data: List<LocalMedia>) {

        if (data.isNullOrEmpty()){
            binding.imageGroup.isVisible = false
            return
        }

        binding.imageGroup.isVisible = true
        imageAdapter.setList(data)

        binding.image.apply {
            val spanCount = 4
            layoutManager =
                FullyGridLayoutManager(
                    this@AlarmDetailsActivity,
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

    private fun initVideoRecyclerView(data: List<LocalMedia>) {

        if (data.isNullOrEmpty()){
            binding.videoGroup.isVisible = false
            return
        }

        binding.videoGroup.isVisible = true
        videoAdapter.setList(data)

        binding.video.apply {
            val spanCount = 4
            layoutManager =
                FullyGridLayoutManager(
                    this@AlarmDetailsActivity,
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

    private fun initAudioRecyclerView(data: List<LocalMedia>) {

        if (data.isNullOrEmpty()){
            binding.audioGroup.isVisible = false
            return
        }

        binding.audioGroup.isVisible = true
        audioAdapter.setItems(data)

        binding.audio.apply {
            val spanCount = 4
            layoutManager =
                FullyGridLayoutManager(
                    this@AlarmDetailsActivity,
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
}