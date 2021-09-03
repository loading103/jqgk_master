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
import com.daqsoft.module_statistics.databinding.ActivityTicketStatisticBinding
import com.daqsoft.module_statistics.databinding.ActivityVehicleStatisticBinding
import com.daqsoft.module_statistics.viewmodel.TicketStatisticsViewModel
import com.daqsoft.module_statistics.viewmodel.VehicleStatisticsViewModel
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
 * @date 21/6/2021 下午 1:53
 * @author zp
 * @describe 车辆统计
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Statistics.PAGER_VEHICLE_STATISTICS)
class VehicleStatisticsActivity : AppBaseActivity<ActivityVehicleStatisticBinding, VehicleStatisticsViewModel>() {

    companion object{
        val  NORMAL_TITLES  = arrayListOf<Pair<String,Int>>(Pair("来源地",1), Pair("时段分析",2), Pair("停留时长",3), Pair("停车场分析",4),Pair("节假日分析",5))
    }

    private val fragmentList = mutableListOf<Fragment>()
    private var pagerTitles = mutableListOf<Pair<String,Int>>()


    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_vehicle_statistic
    }

    override fun initViewModel(): VehicleStatisticsViewModel? {
        return viewModels<VehicleStatisticsViewModel>().value
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

        val vehicleSource = ARouter.getInstance().build(ARouterPath.Statistics.PAGER_VEHICLE_SOURCE).navigation() as Fragment
        fragmentList.add(vehicleSource)

        val vehicleTimePeriod = ARouter.getInstance().build(ARouterPath.Statistics.PAGER_VEHICLE_TIME_PERIOD).navigation() as Fragment
        fragmentList.add(vehicleTimePeriod)

        val vehicleLengthOfStay = ARouter.getInstance().build(ARouterPath.Statistics.PAGER_VEHICLE_LENGTH_OF_STAY).navigation() as Fragment
        fragmentList.add(vehicleLengthOfStay)

        val vehicleParkingLot = ARouter.getInstance().build(ARouterPath.Statistics.PAGER_VEHICLE_PARKING_LOT).navigation() as Fragment
        fragmentList.add(vehicleParkingLot)

        val vehicleHoliday = ARouter.getInstance().build(ARouterPath.Statistics.PAGER_VEHICLE_HOLIDAY).navigation() as Fragment
        fragmentList.add(vehicleHoliday)


        binding.viewPager2.offscreenPageLimit = 1
        binding.viewPager2.adapter = BaseFragmentPagerAdapter(supportFragmentManager, this.lifecycle,fragmentList)
        binding.viewPager2.currentItem = 0
    }

    private fun initIndicator() {

        pagerTitles = NORMAL_TITLES
        val commonNavigator = CommonNavigator(this)
        commonNavigator.isAdjustMode = false
        commonNavigator.leftPadding = 14.dp
        commonNavigator.rightPadding = 14.dp
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