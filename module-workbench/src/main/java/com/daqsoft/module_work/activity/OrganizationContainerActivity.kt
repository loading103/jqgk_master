package com.daqsoft.module_work.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_work.R
import com.daqsoft.module_work.BR
import com.daqsoft.module_work.databinding.ActivityOrganizationContainerBinding
import com.daqsoft.module_work.viewmodel.OrganizationContainerViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * @package name：com.daqsoft.module_work.activity
 * @date 18/5/2021 下午 3:06
 * @author zp
 * @describe 公司组织结构
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_ORGANIZATION_CONTAINER)
class OrganizationContainerActivity : AppBaseActivity<ActivityOrganizationContainerBinding, OrganizationContainerViewModel>() {

    lateinit var navController: NavController

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_organization_container
    }

    override fun initViewModel(): OrganizationContainerViewModel? {
        return viewModels<OrganizationContainerViewModel>().value
    }

    override fun initView() {
        super.initView()
        initNavController()
    }


    override fun initData() {
        super.initData()
    }

    private fun initNavController() {
        navController = Navigation.findNavController(this, R.id.container_fragment)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }
}