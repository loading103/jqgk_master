package com.daqsoft.module_task.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseFragment
import com.daqsoft.library_base.base.BaseFragmentPagerAdapter
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.LoadSirUtil
import com.daqsoft.library_base.utils.ViewPager2BindIndicatorHelper
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.module_task.BR
import com.daqsoft.module_task.R
import com.daqsoft.module_task.databinding.FragmentTaskBinding
import com.daqsoft.module_task.viewmodel.TaskViewModel
import com.daqsoft.library_common.widget.BoldPagerTitleView
import com.daqsoft.module_task.repository.pojo.vo.TaskCount
import com.daqsoft.mvvmfoundation.utils.dp
import com.jeremyliao.liveeventbus.LiveEventBus
import com.kingja.loadsir.callback.Callback
import com.kingja.loadsir.core.LoadSir
import dagger.hilt.android.AndroidEntryPoint
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.WrapPagerIndicator

/**
 * @describe 任务页面
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Task.PAGER_TASK)
class TaskFragment : AppBaseFragment<FragmentTaskBinding, TaskViewModel>() {


    companion object{
        val  NORMAL_TITLES  = arrayListOf<Pair<String,Int>>(Pair("待办/0",10), Pair("待阅/0",30), Pair("已办/0",20), Pair("已阅/0",40))
    }

    private val fragmentList = mutableListOf<Fragment>()
    private var pagerTitles = mutableListOf<Pair<String,Int>>()

    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return  R.layout.fragment_task
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initViewModel(): TaskViewModel? {
        return requireActivity().viewModels<TaskViewModel>().value
    }

    override fun initView() {
        super.initView()
        initLoadService()
    }

    private fun initLoadService() {
        loadService = LoadSir.getDefault().register(binding.outermost, Callback.OnReloadListener {
            LoadSirUtil.postLoading(loadService!!)
            initData()
        })
        LoadSirUtil.postLoading(loadService!!)
    }

    private fun initViewPager2() {
        pagerTitles.forEach {
            fragmentList.add(TaskListFragment.newInstance(it.second))
        }
        binding.viewPager2.offscreenPageLimit = 1
        binding.viewPager2.adapter = BaseFragmentPagerAdapter(childFragmentManager, this.lifecycle,fragmentList)
        binding.viewPager2.currentItem = 0
    }

    private fun initIndicator() {
        val commonNavigator = CommonNavigator(context)
        commonNavigator.isAdjustMode = true
        commonNavigator.isSkimOver = true
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return pagerTitles.size
            }
            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val simplePagerTitleView = BoldPagerTitleView(context)
                simplePagerTitleView.text = pagerTitles[index].first
                simplePagerTitleView.textSize = 15f
                simplePagerTitleView.normalColor = Color.WHITE
                simplePagerTitleView.selectedColor = requireContext().resources.getColor(R.color.color_59abff)
                simplePagerTitleView.setOnClickListenerThrottleFirst {
                    binding.viewPager2.currentItem = index
                }
                return simplePagerTitleView
            }

            override fun getIndicator(context: Context): IPagerIndicator {
                val indicator = WrapPagerIndicator(context)
                indicator.horizontalPadding = 12.dp
                indicator.verticalPadding = 8.dp

                indicator.fillColor = Color.WHITE
                return indicator
            }
        }
        binding.indicator.navigator = commonNavigator
        ViewPager2BindIndicatorHelper.bind(binding.indicator, binding.viewPager2)
        binding.indicator.onPageSelected(binding.viewPager2.currentItem)

    }

    override fun initData() {
        super.initData()
        viewModel.getTaskCount()
    }


    override fun initViewObservable() {
        super.initViewObservable()

        viewModel.countEvent.observe(this, Observer {count ->
            LoadSirUtil.postSuccess(loadService!!)
            if (!count.isNullOrEmpty()){
                handleTitle(count)
            }
            if (pagerTitles.isEmpty()){
                pagerTitles =  NORMAL_TITLES
            }
            initIndicator()
            initViewPager2()
        })

        viewModel.refreshEvent.observe(this, Observer {count ->
            if (!count.isNullOrEmpty()){
                handleTitle(count)
            }
            if (pagerTitles.isEmpty()){
                pagerTitles =  NORMAL_TITLES
            }
            initIndicator()
        })

        // 子类下来刷新这里顶部的数据
        LiveEventBus.get(LEBKeyGlobal.ALARM_HANDLE_TOP,Boolean::class.java).observe(this, Observer {
            viewModel.getTaskCount(true)
        })
        LiveEventBus.get(LEBKeyGlobal.ALARM_HANDLE,Boolean::class.java).observe(this, Observer {
            viewModel.getTaskCount(true)
        })
        LiveEventBus.get(LEBKeyGlobal.ALARM_HANDLE_FINISH,Boolean::class.java).observe(this, Observer {
            viewModel.getTaskCount(true)
        })
    }


    private fun handleTitle(count : List<TaskCount>){
        pagerTitles.clear()
        count.forEach {
            when(it.status){
                10 ->{
                    pagerTitles.add(Pair("待办/${it.quantity}",it.status))
                }
                20 ->{
                    pagerTitles.add(Pair("已办/${it.quantity}",it.status))
                }
                30 ->{
                    pagerTitles.add(Pair("待阅/${it.quantity}",it.status))
                }
                40 ->{
                    pagerTitles.add(Pair("已阅/${it.quantity}",it.status))
                }
            }
        }
    }
}
