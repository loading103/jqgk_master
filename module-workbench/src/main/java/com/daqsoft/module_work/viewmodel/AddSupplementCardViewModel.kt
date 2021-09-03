package com.daqsoft.module_work.viewmodel

import android.app.Application
import android.graphics.Color
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.module_work.repository.WorkRepository
import com.daqsoft.mvvmfoundation.toolbar.ToolbarViewModel

/**
 * @package name：com.daqsoft.module_work.viewmodel
 * @date 7/7/2021 上午 10:04
 * @author zp
 * @describe
 */
class AddSupplementCardViewModel : ToolbarViewModel<WorkRepository>{

    @ViewModelInject
    constructor(application: Application, workRepository: WorkRepository) : super(
        application,
        workRepository
    )

    override fun onCreate() {
        super.onCreate()
        initToolbar()
    }


    private fun initToolbar() {
        setBackground(Color.WHITE)
        setTitleText("发起补卡申请")
    }
}