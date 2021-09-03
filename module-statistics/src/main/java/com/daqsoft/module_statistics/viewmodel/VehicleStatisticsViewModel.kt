package com.daqsoft.module_statistics.viewmodel

import android.app.Application
import android.graphics.Color
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.module_statistics.R
import com.daqsoft.module_statistics.repository.StatisticsRepository
import com.daqsoft.mvvmfoundation.toolbar.ToolbarViewModel

/**
 * @package name：com.daqsoft.module_statistics.viewmodel
 * @date 21/6/2021 下午 2:10
 * @author zp
 * @describe
 */
class VehicleStatisticsViewModel : ToolbarViewModel<StatisticsRepository> {

    @ViewModelInject
    constructor(application: Application, statisticsRepository : StatisticsRepository):super(application,statisticsRepository)

    override fun onCreate() {
        super.onCreate()
        initToolbar()
    }

    private fun initToolbar() {
        setBackground(Color.TRANSPARENT)
        setTitleText("景区车辆统计")
        setTitleTextColor(Color.WHITE)
        setBackIconSrc(R.mipmap.back_white)
    }

}