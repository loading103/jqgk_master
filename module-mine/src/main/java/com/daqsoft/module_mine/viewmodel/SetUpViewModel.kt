package com.daqsoft.module_mine.viewmodel

import android.app.Application
import android.graphics.Color
import android.view.View
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.library_base.pojo.Profile
import com.daqsoft.module_mine.R
import com.daqsoft.module_mine.repository.MineRepository
import com.daqsoft.mvvmfoundation.toolbar.ToolbarViewModel

/**
 * @package name：com.daqsoft.module_mine.viewmodel
 * @date 28/6/2021 下午 1:46
 * @author zp
 * @describe
 */
class SetUpViewModel: ToolbarViewModel<MineRepository> {

    @ViewModelInject
    constructor(application: Application, mineRepository: MineRepository) : super(
        application,
        mineRepository
    )

    override fun onCreate() {
        super.onCreate()
        initToolbar()
    }

    private fun initToolbar() {
        setBackground(Color.WHITE)
        setTitleText("设置")
    }
}
