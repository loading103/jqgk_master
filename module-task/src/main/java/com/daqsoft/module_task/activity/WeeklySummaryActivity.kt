package com.daqsoft.module_task.activity

import android.app.Activity
import android.app.Application
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import cn.iwgang.simplifyspan.SimplifySpanBuild
import cn.iwgang.simplifyspan.unit.SpecialTextUnit
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.LoadSirUtil
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.module_task.R
import com.daqsoft.module_task.BR
import com.daqsoft.module_task.databinding.ActivityWeeklySummaryBinding
import com.daqsoft.module_task.viewmodel.WeeklySummaryViewModel
import com.kingja.loadsir.callback.Callback
import com.kingja.loadsir.core.LoadSir
import dagger.hilt.android.AndroidEntryPoint

/**
 * @package name：com.daqsoft.module_task.activity
 * @date 13/5/2021 下午 5:35
 * @author zp
 * @describe 一周小结
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Task.PAGER_WEEKLY_SUMMARY)
class WeeklySummaryActivity : AppBaseActivity<ActivityWeeklySummaryBinding, WeeklySummaryViewModel>() {


    @JvmField
    @Autowired
    var id : String  = ""

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_weekly_summary
    }

    override fun initViewModel(): WeeklySummaryViewModel? {
        return viewModels<WeeklySummaryViewModel>().value
    }

    override fun initView() {
        super.initView()
        initLoadService()
        initOnClick()

    }

    private fun initLoadService(){
        loadService =  LoadSir.getDefault().register(this, object : Callback.OnReloadListener {
            override fun onReload(v: View?) {
                LoadSirUtil.postLoading(loadService!!)
                initData()
            }
        })
        LoadSirUtil.postLoading(loadService!!)
    }

    private fun initOnClick() {
        binding.arrow.setOnClickListenerThrottleFirst {
            finish()
        }
    }


    override fun initData() {
        super.initData()
        viewModel.getWeeklySummary(id)
    }

    override fun initViewObservable() {
        super.initViewObservable()
        viewModel.summaryEvent.observe(this, Observer {

            binding.tips.text = it.chickenSoup

            createSpannableString(binding.receive,"本周接收任务",it.receiveQuantity)
            createSpannableString(binding.complete,"本周已办任务",it.processedQuantity)
            createSpannableString(binding.undone,"本周待办任务",it.untreatedQuantity)
            createSpannableString(binding.read,"本周已阅事项",it.haveReadQuantity)
        })
    }

    private fun createSpannableString(textView: TextView,title:String,count:Int){
        val ssb = SimplifySpanBuild()
            .append(SpecialTextUnit(title).setTextSize(12f).setTextColor(resources.getColor(R.color.color_999999)))
            .append(SpecialTextUnit("\n"))
            .append(SpecialTextUnit(count.toString()).setTextSize(20f).setTextStyle(Typeface.BOLD).setTextColor(resources.getColor(R.color.color_666666)))
            .append(SpecialTextUnit("件").setTextSize(13f).setTextColor(resources.getColor(R.color.color_666666)))
            .build()
        textView.text = ssb
    }

}