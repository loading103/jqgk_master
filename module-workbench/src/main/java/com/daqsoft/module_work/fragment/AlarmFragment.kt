package com.daqsoft.module_work.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.GsonUtils
import com.daqsoft.library_base.base.AppBaseFragment
import com.daqsoft.library_base.base.BaseFragmentPagerAdapter
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.LoadSirUtil
import com.daqsoft.library_base.utils.ViewPager2BindIndicatorHelper
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.library_common.bean.AppMenu
import com.daqsoft.library_common.widget.BoldPagerTitleView
import com.daqsoft.module_work.BR
import com.daqsoft.module_work.R
import com.daqsoft.module_work.databinding.FragmentAlarmBinding
import com.daqsoft.module_work.repository.pojo.vo.AlarmCount
import com.daqsoft.module_work.viewmodel.AlarmViewModel
import com.daqsoft.mvvmfoundation.utils.dp
import com.google.gson.reflect.TypeToken
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
 * @package name：com.daqsoft.module_work.fragment
 * @date 25/5/2021 上午 10:00
 * @author zp
 * @describe  告警处理
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_ALARM)
class AlarmFragment : AppBaseFragment<FragmentAlarmBinding, AlarmViewModel>() {

    var appMenus: List<AppMenu> ? = null

    companion object{

        const val MENU = "menu"

        private var NORMAL_TITLES = mutableListOf<Pair<String,Int>>(Pair("我接收的/0",2), Pair("我指派的/0",1),Pair("全部告警/0",0))

        fun newInstance(menu : String ?):AlarmFragment {
            val args = Bundle()
            args.putString(MENU, menu)
            val fragment = AlarmFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private val fragmentList = mutableListOf<Fragment>()
    private var pagerTitles = mutableListOf<Pair<String,Int>>()

    override fun initParam() {
        super.initParam()
        val menu : String ? = arguments?.getString(MENU, "")
        if (!menu.isNullOrBlank()) {
            val type = object : TypeToken<List<AppMenu>>() {}.type
            appMenus = GsonUtils.fromJson<List<AppMenu>>(menu, type)
        }
    }

    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return R.layout.fragment_alarm
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initViewModel(): AlarmViewModel? {
        return requireActivity().viewModels<AlarmViewModel>().value
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

    override fun initData() {
        super.initData()
        viewModel.getAlarmCount()
    }

    override fun initViewObservable() {
        super.initViewObservable()
        viewModel.countEvent.observe(this, Observer { count ->
            LoadSirUtil.postSuccess(loadService!!)
            if (!appMenus.isNullOrEmpty() && !count.isNullOrEmpty()){
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

        LiveEventBus.get(LEBKeyGlobal.ALARM_HANDLE,Boolean::class.java).observe(this, Observer {
            viewModel.getAlarmCount(true)
        })
        LiveEventBus.get(LEBKeyGlobal.ALARM_HANDLE_FINISH,Boolean::class.java).observe(this, Observer {
            viewModel.getAlarmCount(true)
        })
    }

    private fun initViewPager2() {
        pagerTitles.forEach{
            fragmentList.add(AlarmListFragment.newInstance(it.second))
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
                simplePagerTitleView.normalColor = requireContext().resources.getColor(R.color.color_666666)
                simplePagerTitleView.selectedColor = requireContext().resources.getColor(R.color.white)
                simplePagerTitleView.setOnClickListenerThrottleFirst {
                    binding.viewPager2.currentItem = index
                }
                return simplePagerTitleView
            }

            override fun getIndicator(context: Context): IPagerIndicator {
                val indicator = WrapPagerIndicator(context)
                indicator.horizontalPadding = 10.dp
                indicator.verticalPadding = 6.dp

                indicator.fillColor = requireContext().resources.getColor(R.color.color_20a0ff)
                return indicator
            }
        }
        binding.indicator.navigator = commonNavigator
        ViewPager2BindIndicatorHelper.bind(binding.indicator, binding.viewPager2)
        binding.indicator.onPageSelected(binding.viewPager2.currentItem)
    }


    private fun handleTitle(count : List<AlarmCount>){
        pagerTitles.clear()
        appMenus!!.forEach { menu ->
            count.find { it.num == menu.number }?.let {
                pagerTitles.add(Pair("${menu.label}/${it.count}",it.type))
            }
        }
    }
}