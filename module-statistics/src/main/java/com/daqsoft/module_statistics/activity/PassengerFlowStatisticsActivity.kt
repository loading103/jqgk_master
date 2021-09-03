package com.daqsoft.module_statistics.activity

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.base.BaseFragmentPagerAdapter
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.LoadSirUtil
import com.daqsoft.library_base.utils.ViewPager2BindIndicatorHelper
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.library_common.widget.BoldPagerTitleView
import com.daqsoft.module_statistics.BR
import com.daqsoft.module_statistics.R
import com.daqsoft.module_statistics.databinding.ActivityPassengerFlowStatisticBinding
import com.daqsoft.module_statistics.viewmodel.PassengerFlowStatisticsViewModel
import com.daqsoft.mvvmfoundation.utils.dp
import com.kingja.loadsir.callback.Callback
import com.kingja.loadsir.core.LoadSir
import dagger.hilt.android.AndroidEntryPoint
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.WrapPagerIndicator

/**
 * @package name：com.daqsoft.module_statistics.activity
 * @date 18/6/2021 上午 9:17
 * @author zp
 * @describe 客流统计
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Statistics.PAGER_PASSENGER_FLOW_STATISTICS)
class PassengerFlowStatisticsActivity  : AppBaseActivity<ActivityPassengerFlowStatisticBinding, PassengerFlowStatisticsViewModel>() {

    companion object{
        val  NORMAL_TITLES  = arrayListOf<Pair<String,Int>>(Pair("时段分析",1), Pair("游客画像",2), Pair("景点偏好度",3), Pair("节假日分析",4),)
    }

    private val fragmentList = mutableListOf<Fragment>()
    private var pagerTitles = mutableListOf<Pair<String,Int>>()


    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_passenger_flow_statistic
    }

    override fun initViewModel(): PassengerFlowStatisticsViewModel? {
        return viewModels<PassengerFlowStatisticsViewModel>().value
    }

    override fun initView() {
        super.initView()
        initLoadService()
        initIndicator()
        initViewPager2()
        LoadSirUtil.postSuccess(loadService!!)
    }

    private fun initLoadService() {
        loadService = LoadSir.getDefault().register(binding.container, Callback.OnReloadListener {
            LoadSirUtil.postLoading(loadService!!)
            initData()
        })
        LoadSirUtil.postLoading(loadService!!)
    }

    private fun initViewPager2() {

        val passengerFlowTimePeriod = ARouter.getInstance().build(ARouterPath.Statistics.PAGER_PASSENGER_FLOW_TIME_PERIOD).navigation() as Fragment
        fragmentList.add(passengerFlowTimePeriod)

        val passengerFlowPortrait = ARouter.getInstance().build(ARouterPath.Statistics.PAGER_PASSENGER_FLOW_PORTRAIT).navigation() as Fragment
        fragmentList.add(passengerFlowPortrait)

        val passengerFlowAttractionPreference = ARouter.getInstance().build(ARouterPath.Statistics.PAGER_PASSENGER_FLOW_ATTRACTION_PREFERENCE).navigation() as Fragment
        fragmentList.add(passengerFlowAttractionPreference)

        val passengerFlowHoliday = ARouter.getInstance().build(ARouterPath.Statistics.PAGER_PASSENGER_FLOW_HOLIDAY).navigation() as Fragment
        fragmentList.add(passengerFlowHoliday)


        binding.viewPager2.offscreenPageLimit = 1
        binding.viewPager2.adapter = BaseFragmentPagerAdapter(supportFragmentManager, this.lifecycle,fragmentList)
        binding.viewPager2.currentItem = 0
    }

    private fun initIndicator() {

        pagerTitles = NORMAL_TITLES
        val commonNavigator = CommonNavigator(this)
        commonNavigator.isAdjustMode = false
        commonNavigator.leftPadding = 20.dp
        commonNavigator.rightPadding = 20.dp
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
                simplePagerTitleView.selectedColor = resources.getColor(R.color.color_59abff)
                simplePagerTitleView.setOnClickListenerThrottleFirst {
                    binding.viewPager2.currentItem = index
                }
                return simplePagerTitleView
            }

            override fun getIndicator(context: Context): IPagerIndicator {
                val indicator = WrapPagerIndicator(context)
                indicator.horizontalPadding = 12.dp
                indicator.verticalPadding = 6.dp

                indicator.fillColor = Color.WHITE
                return indicator
            }
        }
        binding.indicator.navigator = commonNavigator
        ViewPager2BindIndicatorHelper.bind(binding.indicator, binding.viewPager2)
        binding.indicator.onPageSelected(0)

    }

    override fun initData() {
        super.initData()
    }


    override fun initViewObservable() {
        super.initViewObservable()
    }
}