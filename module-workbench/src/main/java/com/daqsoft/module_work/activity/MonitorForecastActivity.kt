package com.daqsoft.module_work.activity

import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.GsonUtils
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.base.BaseFragmentPagerAdapter
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.ViewPager2BindIndicatorHelper
import com.daqsoft.library_common.bean.AppMenu
import com.daqsoft.library_common.widget.BoldPagerTitleView
import com.daqsoft.module_work.BR
import com.daqsoft.module_work.R
import com.daqsoft.module_work.databinding.ActivityMonitorForecastBinding
import com.daqsoft.module_work.fragment.AlarmFragment
import com.daqsoft.module_work.viewmodel.MonitorForecastViewModel
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator

/**
 * @package name：com.daqsoft.module_work.activity
 * @date 25/5/2021 上午 9:10
 * @author zp
 * @describe  监测预警
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_MONITOR_FORECAST)
class MonitorForecastActivity : AppBaseActivity<ActivityMonitorForecastBinding, MonitorForecastViewModel>() {

    @JvmField
    @Autowired
    var menu : String? = null

    private val fragmentList = mutableListOf<Fragment>()
    private val pagerTitles = arrayListOf<String>("统计分析", "告警处理")

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_monitor_forecast
    }

    override fun initViewModel(): MonitorForecastViewModel? {
        return viewModels<MonitorForecastViewModel>().value
    }

    override fun initView() {
        super.initView()
//        initIndicator()
        initViewPager2()
    }

    private fun initViewPager2() {

//        pagerTitles.forEach {
//            fragmentList.add(AlarmHandleFragment.newInstance())
//        }

        fragmentList.add(AlarmFragment.newInstance(menu))

        binding.viewPager2.offscreenPageLimit = 1
        binding.viewPager2.adapter = BaseFragmentPagerAdapter(supportFragmentManager , this.lifecycle,fragmentList)
        binding.viewPager2.currentItem = 0
    }

    private fun initIndicator() {
        val commonNavigator = CommonNavigator(this)
        commonNavigator.isAdjustMode = true
        commonNavigator.isSkimOver = true
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return pagerTitles.size
            }
            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val simplePagerTitleView = BoldPagerTitleView(context)
                simplePagerTitleView.text = pagerTitles[index]
                simplePagerTitleView.normalColor = resources.getColor(R.color.color_666666)
                simplePagerTitleView.selectedColor = resources.getColor(R.color.color_20a0ff)
                simplePagerTitleView.textSize = 16f
                simplePagerTitleView.setOnClickListener {
                    binding.viewPager2.currentItem = index
                }
                return simplePagerTitleView
            }

            override fun getIndicator(context: Context): IPagerIndicator {
                val indicator = LinePagerIndicator(context)
                indicator.mode = LinePagerIndicator.MODE_EXACTLY

                indicator.setColors(resources.getColor(R.color.color_20a0ff))
                return indicator
            }
        }
        binding.indicator.navigator = commonNavigator
        ViewPager2BindIndicatorHelper.bind(binding.indicator, binding.viewPager2)
        binding.indicator.onPageSelected(0)

    }


}

