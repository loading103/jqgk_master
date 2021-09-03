package com.daqsoft.module_work.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.module_work.repository.WorkRepository
import com.daqsoft.mvvmfoundation.toolbar.ToolbarViewModel

/**
 * @package name：com.daqsoft.module_work.viewmodel
 * @date 25/5/2021 上午 9:12
 * @author zp
 * @describe
 */
class MonitorForecastViewModel  : ToolbarViewModel<WorkRepository> {

    @ViewModelInject
    constructor(application: Application, workRepository: WorkRepository) : super(
        application,
        workRepository
    )

    override fun onCreate() {
        initToolbar()
    }

    private fun initToolbar() {
        setTitleText("监测预警")
    }
}