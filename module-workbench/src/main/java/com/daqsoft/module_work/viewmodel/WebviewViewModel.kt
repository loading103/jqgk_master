package com.daqsoft.module_work.viewmodel

import android.app.Application
import android.graphics.Color
import android.view.View
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.module_work.repository.WorkRepository
import com.daqsoft.mvvmfoundation.toolbar.ToolbarViewModel

class WebviewViewModel : ToolbarViewModel<WorkRepository> {

    @ViewModelInject
    constructor(application: Application, workBenchRepository: WorkRepository) : super(application, workBenchRepository)

    var approve : Boolean = false

    override fun onCreate() {
        super.onCreate()
        initToolbar()
    }

    private fun initToolbar() {
        setBackground(Color.WHITE)
        setRightIconVisible(View.GONE )
    }

    fun setTitle( title:String) {
        setTitleText(title)
    }

}