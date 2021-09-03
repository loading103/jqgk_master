package com.daqsoft.module_work.activity

import android.os.Bundle
import android.text.TextUtils
import android.widget.LinearLayout
import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_work.BR
import com.daqsoft.module_work.R
import com.daqsoft.module_work.databinding.ActivityWebviewBinding
import com.daqsoft.module_work.viewmodel.WebviewViewModel
import com.just.agentweb.AgentWeb
import dagger.hilt.android.AndroidEntryPoint

/**
 * webview界面
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_WEB)
class WebViewActivity : AppBaseActivity<ActivityWebviewBinding, WebviewViewModel>() {
    @JvmField
    @Autowired
    var url : String? = ""

    @JvmField
    @Autowired
    var title : String?=  ""

    lateinit var  mAgentWeb : AgentWeb

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_webview
    }

    override fun initViewModel(): WebviewViewModel? {
        return viewModels<WebviewViewModel>().value
    }

    override fun initView() {
        super.initView()
    }

    override fun initData() {
        super.initData()
        if(TextUtils.isEmpty(url)){
            return
        }
        if(!TextUtils.isEmpty(title)){
          viewModel.setTitle(title!!)
        }
        mAgentWeb = AgentWeb.with(this)
            .setAgentWebParent( binding.llRoot, LinearLayout.LayoutParams(-1, -1))
            .useDefaultIndicator()
            .createAgentWeb()
            .ready()
            .go(url)
    }

    override fun onPause() {
        mAgentWeb.getWebLifeCycle().onPause()
        super.onPause()
    }

    override fun onResume() {
        mAgentWeb.getWebLifeCycle().onResume()
        super.onResume()
    }

    override fun onDestroy() {
        mAgentWeb.getWebLifeCycle().onDestroy()
        super.onDestroy()
    }
    override fun initViewObservable() {
        super.initViewObservable()
    }
}