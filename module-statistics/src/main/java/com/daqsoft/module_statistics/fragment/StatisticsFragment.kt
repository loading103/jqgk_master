package com.daqsoft.module_statistics.fragment

import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseFragment
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.LoadSirUtil
import com.daqsoft.library_base.wrapper.AppBarStateChangeListener
import com.daqsoft.module_statistics.BR
import com.daqsoft.module_statistics.R
import com.daqsoft.module_statistics.adapter.StatisticsAdapter
import com.daqsoft.module_statistics.databinding.FragmentStatisticBinding
import com.daqsoft.module_statistics.viewmodel.StatisticsViewModel
import com.daqsoft.mvvmfoundation.utils.ToolbarUtils
import com.daqsoft.mvvmfoundation.utils.dp
import com.google.android.material.appbar.AppBarLayout
import com.kingja.loadsir.callback.Callback
import com.kingja.loadsir.core.LoadSir
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * @describe 我的页面
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Statistics.PAGER_STATISTICS)
class StatisticsFragment : AppBaseFragment<FragmentStatisticBinding, StatisticsViewModel>() {

    val statisticsAdapter : StatisticsAdapter by lazy { StatisticsAdapter() }

    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return  R.layout.fragment_statistic
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initViewModel(): StatisticsViewModel? {
        return requireActivity().viewModels<StatisticsViewModel>().value
    }

    override fun initView() {
        super.initView()
        initAppBar()
        initRefresh()
        initLoadService()
        initRecycleView()
    }

    private fun initRefresh() {
        binding.refresh.setOnRefreshListener{
            initData()
        }
    }

    private fun initLoadService() {
        loadService = LoadSir.getDefault().register(binding.container, Callback.OnReloadListener {
            LoadSirUtil.postLoading(loadService!!)
            initData()
        })
        LoadSirUtil.postLoading(loadService!!)
    }

    private fun initRecycleView() {
        binding.recyclerView.apply {
            if (itemDecorationCount == 0){
                addItemDecoration(object : RecyclerView.ItemDecoration(){
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        val position = parent.getChildAdapterPosition(view)
                        if (position == 0 ){
                            outRect.top = 6.dp
                        }else{
                            outRect.top = (-10).dp
                        }
                    }
                })
            }
            layoutManager = LinearLayoutManager(requireContext())
            adapter = statisticsAdapter
        }
    }

    override fun initData() {
        super.initData()
        viewModel.getData()

    }

    override fun initViewObservable() {
        super.initViewObservable()

        viewModel.refreshEvent.observe(this, Observer {
            binding.refresh.finishRefresh()
        })

        viewModel.comfortEvent.observe(this, Observer {
            binding.calibration.setProgress(it.instLoad.currLoad.toFloat(),it.instLoad.maxLoad.toFloat())
            val current = it.instLoad.currLoad
            var comfort = "未知"
            var backgroundColor = "#0cb88d"
            it.comfort.forEach {
                if(current > it.min && current < it.max){
                    comfort = it.label
                    backgroundColor = it.colorValue
                }
            }
            if (backgroundColor == "#0cb88d"){
                backgroundColor = if (it.comfort.isNullOrEmpty()) "#0cb88d" else it.comfort[0].colorValue
            }
            if (backgroundColor.isNullOrEmpty()){
                backgroundColor = "#0cb88d"
            }
            binding.comfort.text = "景区舒适度：${comfort}"
            binding.bg.helper.setBackgroundColorNormalArray(intArrayOf(Color.parseColor(StringBuilder(backgroundColor).insert(1,"CC").toString()),Color.parseColor(backgroundColor),))
        })

    }


    /**
     * 初始化 AppBar
     */
    private fun initAppBar() {
        binding.appbar.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout, state: State) {
                when (state) {
                    //展开状态
                    State.EXPANDED -> {
                        Timber.e("展开")
                    }
                    //折叠状态
                    State.COLLAPSED -> {
                        Timber.e("折叠")
                    }
                    //中间状态
                    else -> {
                        Timber.e("中间")
                    }
                }
            }

            override fun onOffsetChanged(appBarLayout: AppBarLayout, i: Int) {
                if(i == 0){
                    return
                }
                val cardViewTop = binding.recyclerView.top
                val appbarTop = binding.appbar.top
                val minDistance = ToolbarUtils.getStatusBarHeight() + ToolbarUtils.getToolbarHeight()
                if ((cardViewTop + appbarTop) < minDistance) {
                    viewModel.setBackground(Color.WHITE)
                    viewModel.setTitleTextColor(R.color.color_333333)
                } else {
                    viewModel.setBackground(Color.TRANSPARENT)
                    viewModel.setTitleTextColor(R.color.white)
                }
            }
        })
    }
}

