package com.daqsoft.module_work.viewmodel

import android.app.Application
import android.content.Intent
import androidx.hilt.lifecycle.ViewModelInject
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.GsonUtils
import com.daqsoft.library_base.global.MMKVGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.pojo.LoginInfo
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.MMKVUtils
import com.daqsoft.module_work.repository.WorkRepository
import com.daqsoft.module_work.repository.pojo.vo.Department
import com.daqsoft.mvvmfoundation.base.BaseViewModel
import com.daqsoft.mvvmfoundation.toolbar.ToolbarViewModel
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.daqsoft.mvvmfoundation.utils.SingleLiveEvent

/**
 * @package name：com.daqsoft.module_work.viewmodel
 * @date 18/5/2021 下午 3:25
 * @author zp
 * @describe
 */
class OrganizationContainerViewModel : BaseViewModel<WorkRepository> {

    @ViewModelInject
    constructor(application: Application, workBenchRepository: WorkRepository):super(application, workBenchRepository)

}