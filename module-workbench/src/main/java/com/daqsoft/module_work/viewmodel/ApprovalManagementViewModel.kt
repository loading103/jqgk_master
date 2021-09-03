package com.daqsoft.module_work.viewmodel

import android.app.Application
import android.graphics.Color
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.module_work.R
import com.daqsoft.module_work.repository.WorkRepository
import com.daqsoft.mvvmfoundation.toolbar.ToolbarViewModel

/**
 * @package name：com.daqsoft.module_work.viewmodel
 * @date 9/7/2021 上午 11:50
 * @author zp
 * @describe
 */
class ApprovalManagementViewModel : ToolbarViewModel<WorkRepository> {

    @ViewModelInject
    constructor(application: Application, workRepository: WorkRepository):super(application, workRepository)


    override fun onCreate() {
        super.onCreate()
        initToolbar()
    }

    private fun initToolbar(){
        setBackground(Color.WHITE)
        setTitleText("审批管理")
    }

}