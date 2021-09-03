package com.daqsoft.module_work.viewmodel

import android.app.Application
import android.graphics.Color
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.module_work.repository.WorkRepository
import com.daqsoft.mvvmfoundation.base.BaseViewModel
import com.daqsoft.mvvmfoundation.toolbar.ToolbarViewModel

/**
 * @package name：com.daqsoft.module_work.viewmodel
 * @date 10/5/2021 下午 4:02
 * @author zp
 * @describe 考勤 viewModel
 */
class AttendanceViewModel : ToolbarViewModel<WorkRepository>{


    @ViewModelInject
    constructor(application: Application,workRepository: WorkRepository):super(application, workRepository)

    override fun onCreate() {
        super.onCreate()
        initToolbar()
    }

    private fun initToolbar() {
        setBackground(Color.WHITE)
    }
}