package com.daqsoft.module_work.viewmodel

import android.app.Application
import android.graphics.Color
import android.view.View
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.module_work.R
import com.daqsoft.module_work.repository.WorkRepository
import com.daqsoft.mvvmfoundation.toolbar.ToolbarViewModel

/**
 * @ClassName    LeaveApplyInfoViewModel
 * @Description
 * @Author       yuxc
 * @CreateDate   2021/5/10
 */
class LeaveApplyInfoViewModel : ToolbarViewModel<WorkRepository> {

    @ViewModelInject
    constructor(application: Application, workBenchRepository: WorkRepository):super(application, workBenchRepository)

    val picUrl= ObservableField<String>()

    override fun onCreate() {
        super.onCreate()
        initToolbar()
    }


    private fun initToolbar() {
        setBackground(Color.WHITE)
        setTitleText("请假申请详情")
        setTitleTextColor(R.color.color_333333)
    }
}