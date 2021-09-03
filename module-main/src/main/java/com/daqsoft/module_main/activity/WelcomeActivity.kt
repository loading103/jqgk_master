package com.daqsoft.module_main.activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.global.MMKVGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.MMKVUtils
import com.daqsoft.module_main.BR
import com.daqsoft.module_main.R
import com.daqsoft.module_main.adapter.WelcomeBannerAdapter
import com.daqsoft.module_main.databinding.ActivityWelcomeBinding
import com.daqsoft.module_main.uitls.MyRoundLinesIndicator
import com.daqsoft.module_main.viewmodel.WelcomeViewModel
import com.daqsoft.mvvmfoundation.utils.dp
import com.youth.banner.config.IndicatorConfig
import com.youth.banner.listener.OnPageChangeListener
import dagger.hilt.android.AndroidEntryPoint

/**
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Main.PAGER_WELCOME)
class WelcomeActivity : AppBaseActivity<ActivityWelcomeBinding, WelcomeViewModel>() {

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_welcome
    }

    override fun initViewModel(): WelcomeViewModel? {
        return viewModels<WelcomeViewModel>().value
    }

    override fun initViewObservable() {
        super.initViewObservable()
        initBanner()
    }

    /**
     * 初始化  banner
     */
    private fun initBanner() {
        MMKVUtils.encode(MMKVGlobal.ISFIRST,true)
        val data = arrayListOf<Int>(
            R.mipmap.user_guide1,
            R.mipmap.user_guide2,
            R.mipmap.user_guide3,
        )

        binding.banner.apply {
            setAdapter(WelcomeBannerAdapter(data).apply {
                setJoinOnClickListener(object : WelcomeBannerAdapter.JoinOnClickListener{
                    override fun joinOnClick() {
                        jumpPage()
                    }
                })
            },false)
            isAutoLoop(false)
            indicator = MyRoundLinesIndicator(this@WelcomeActivity)
            setIndicatorRadius(4.dp)
            setIndicatorNormalColor(resources.getColor(R.color.color_803392ff))
            setIndicatorSelectedColor(resources.getColor(R.color.color_3392ff))
            setIndicatorNormalWidth(8.dp)
            setIndicatorSelectedWidth(8.dp)
            setIndicatorHeight(8.dp)
            setIndicatorSpace(6.dp)
            setIndicatorMargins(IndicatorConfig.Margins(0,0,0,80.dp))

            addOnPageChangeListener(object : OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }
                override fun onPageSelected(position: Int) {
                    if (position == data.size -1){
                        (indicator as MyRoundLinesIndicator).visibility = View.GONE
                    }else{
                        (indicator as MyRoundLinesIndicator).visibility = View.VISIBLE
                    }
                }

                override fun onPageScrollStateChanged(state: Int) {
                }

            })
        }
    }

    /**
     * 页面跳转
     */
    private fun jumpPage(){
        ARouter
            .getInstance()
            .build(ARouterPath.Mine.PAGER_LOGIN)
            .navigation()
    }
}