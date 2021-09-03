package com.daqsoft.module_work.activity

import android.graphics.Rect
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.iwgang.simplifyspan.SimplifySpanBuild
import cn.iwgang.simplifyspan.unit.SpecialTextUnit
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.LoadSirUtil
import com.daqsoft.library_base.utils.executePaging
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.module_work.BR
import com.daqsoft.module_work.R
import com.daqsoft.module_work.adapter.VideoSurveillanceListAdapter
import com.daqsoft.module_work.databinding.ActivityVideoSurveillanceListBinding
import com.daqsoft.module_work.repository.pojo.dto.VideoSurveillanceRequest
import com.daqsoft.module_work.repository.pojo.vo.VideoSurveillanceCount
import com.daqsoft.module_work.repository.pojo.vo.VideoSurveillanceGroup
import com.daqsoft.module_work.viewmodel.VideoSurveillanceListViewModel
import com.daqsoft.module_work.widget.VideoSurveillanceFilterPopup
import com.daqsoft.module_work.widget.VideoSurveillanceGroupLeftDrawerPopup
import com.daqsoft.module_work.widget.VideoSurveillanceStatusLeftDrawerPopup
import com.daqsoft.mvvmfoundation.utils.dp
import com.kingja.loadsir.callback.Callback
import com.kingja.loadsir.core.LoadSir
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.interfaces.SimpleCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_clock.view.*

/**
 * @package name：com.daqsoft.module_work.activity
 * @date 13/7/2021 下午 1:35
 * @author zp
 * @describe  视屏监控 列表
 */


@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_VIDEO_SURVEILLANCE_LIST)
class VideoSurveillanceListActivity  : AppBaseActivity<ActivityVideoSurveillanceListBinding, VideoSurveillanceListViewModel>(){

    private val videoSurveillanceListAdapter : VideoSurveillanceListAdapter by lazy { VideoSurveillanceListAdapter() }


    private var statusSelectedPosition : Int? = null
    private val statusPopup : VideoSurveillanceStatusLeftDrawerPopup by lazy {
        VideoSurveillanceStatusLeftDrawerPopup(this,"选择状态").apply{
            setItemOnClickListener(object : VideoSurveillanceStatusLeftDrawerPopup.ItemOnClickListener{
                override fun onClick(position: Int?, content: String?) {
                    statusSelectedPosition = position
                    if (content == "全部"){
                        binding.status.text = "状态"
                    }else{
                        binding.status.text = content
                    }

                    when(content){
                        "全部" -> viewModel.body.online = null
                        "在线" -> viewModel.body.online = 1
                        "离线" -> viewModel.body.online = 0
                    }
                    viewModel.dataSource?.invalidate()
                }
            })
            setData(arrayListOf("全部","在线","离线"))
        }
    }

    private var groupSelectedPosition : Int? = null
    private val groupPopup : VideoSurveillanceGroupLeftDrawerPopup by lazy {
        VideoSurveillanceGroupLeftDrawerPopup(this,"选择监控组").apply{
            setItemOnClickListener(object : VideoSurveillanceGroupLeftDrawerPopup.ItemOnClickListener{
                override fun onClick(position: Int?, content: VideoSurveillanceGroup?) {
                    groupSelectedPosition = position

                    if (content?.name == "全部"){
                        binding.group.text = "监控分组"
                        viewModel.body.groupId = null
                    }else{
                        binding.group.text = content?.name
                        viewModel.body.groupId = content?.id
                    }
                    viewModel.dataSource?.invalidate()
                }
            })


        }
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_video_surveillance_list
    }

    override fun initViewModel(): VideoSurveillanceListViewModel? {
        return viewModels<VideoSurveillanceListViewModel>().value
    }

    override fun initView() {
        super.initView()
        initOnClick()
        initLoadService()
        initRefresh()
    }

    private fun initCount(count: VideoSurveillanceCount) {
        binding.total.text = SimplifySpanBuild()
            .append(SpecialTextUnit("监控数量：").setTextColor(resources.getColor(R.color.color_666666)).setTextSize(12f))
            .append(SpecialTextUnit(count.total.toString()).setTextStyle(Typeface.BOLD).setTextColor(resources.getColor(R.color.color_59abff)).setTextSize(14f))
            .append(SpecialTextUnit("个").setTextStyle(Typeface.BOLD).setTextColor(resources.getColor(R.color.color_59abff)).setTextSize(12f))
            .build()

        binding.unusual.text = SimplifySpanBuild()
            .append(SpecialTextUnit("异常监控：").setTextColor(resources.getColor(R.color.color_666666)).setTextSize(12f))
            .append(SpecialTextUnit(count.exceptionNum.toString()).setTextStyle(Typeface.BOLD).setTextColor(resources.getColor(R.color.color_ff5757)).setTextSize(14f))
            .append(SpecialTextUnit("个").setTextStyle(Typeface.BOLD).setTextColor(resources.getColor(R.color.color_ff5757)).setTextSize(12f))
            .build()

        binding.normal.text = SimplifySpanBuild()
            .append(SpecialTextUnit("正常：").setTextColor(resources.getColor(R.color.color_666666)).setTextSize(12f))
            .append(SpecialTextUnit(count.normalNum.toString()).setTextStyle(Typeface.BOLD).setTextColor(resources.getColor(R.color.color_27cb8c)).setTextSize(14f))
            .append(SpecialTextUnit("个").setTextStyle(Typeface.BOLD).setTextColor(resources.getColor(R.color.color_27cb8c)).setTextSize(12f))
            .build()
    }


    override fun initData() {
        super.initData()
        viewModel.getVideoSurveillanceGroup()
        viewModel.getVideoSurveillanceCount()
    }

    override fun initViewObservable() {
        super.initViewObservable()
        viewModel.pageList.observe(this, Observer {
            binding.refresh.finishRefresh()
            if (it.isEmpty()){
                return@Observer
            }
            when (viewModel.layoutManagerEvent.value!!) {
                ConstantGlobal.GRID -> {
                    initGridRecyclerView()
                }
                ConstantGlobal.LINE -> {
                    initLineRecyclerView()
                }
                else -> {
                    initGridRecyclerView()
                }
            }
            binding.recyclerView.executePaging(it, viewModel.diff)
        })

        viewModel.layoutManagerEvent.observe(this, Observer {
            viewModel.dataSource?.invalidate()
        })


        viewModel.groupEvent.observe(this, Observer {
            groupPopup.setData(it)
        })

        viewModel.countEvent.observe(this, Observer {
            initCount(it)
        })
    }

    private fun initOnClick() {
        binding.status.setOnClickListenerThrottleFirst{
            showStatusPopUp()
        }

        binding.group.setOnClickListenerThrottleFirst{
            showGroupPopUp()
        }
    }

    /**
     * 展示状态popup
     */
    fun showStatusPopUp() {
        XPopup
            .Builder(this)
            .asCustom(statusPopup)
            .show()
        statusPopup.setSelectedPosition(statusSelectedPosition?:-1)
    }

    /**
     * 展示状态popup
     */
    fun showGroupPopUp() {
        XPopup
            .Builder(this)
            .asCustom(groupPopup)
            .show()
        groupPopup.setSelectedPosition(groupSelectedPosition?:-1)
    }


    private fun initLoadService() {
        loadService = LoadSir.getDefault().register(binding.refresh, Callback.OnReloadListener {
            LoadSirUtil.postLoading(loadService!!)
            initData()
            viewModel.dataSource?.invalidate()
        })
        LoadSirUtil.postLoading(loadService!!)
    }

    private fun initRefresh() {
        binding.refresh.setOnRefreshListener {
            initData()
            viewModel.dataSource?.invalidate()
        }
    }

    private fun initLineRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@VideoSurveillanceListActivity)
            if (itemDecorationCount == 0) {
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        val point = parent.getChildAdapterPosition(view)
                        val count = state.itemCount - 1

                        outRect.left = 20.dp
                        outRect.top = if (point == 0) 20.dp else 12.dp
                        outRect.right = 20.dp
                        outRect.bottom = if (point == count) 20.dp else 0.dp

                    }
                })
            }
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val top = recyclerView.canScrollVertically(-1)
                    if (!top){
                        viewModel.setRightIconVisible(View.VISIBLE)
                    }else{
                        viewModel.setRightIconVisible(View.GONE)
                    }
                }

            })
        }
    }


    private fun initGridRecyclerView() {
        binding.recyclerView.apply {
            val spanCount = 2
            layoutManager = GridLayoutManager(context!!, spanCount, GridLayoutManager.VERTICAL, false)
            if (itemDecorationCount == 0) {
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        val position = parent.getChildAdapterPosition(view)
                        var spacing = 20.dp
                        if (position < spanCount) {
                            outRect.top = spacing
                        }else{
                            outRect.top = 12.dp
                        }
                        spacing = 12.dp
                        val column = position % spanCount
                        outRect.left = column * spacing / spanCount
                        outRect.right = spacing - (column + 1) * spacing / spanCount
                    }
                })
            }
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val top = recyclerView.canScrollVertically(-1)
                    if (!top){
                        viewModel.setRightIconVisible(View.VISIBLE)
                    }else{
                        viewModel.setRightIconVisible(View.GONE)
                    }
                }

            })
        }
    }

}