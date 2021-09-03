package com.daqsoft.module_statistics.viewmodel

import android.app.Application
import android.graphics.Color
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.module_statistics.R
import com.daqsoft.module_statistics.repository.StatisticsRepository
import com.daqsoft.mvvmfoundation.toolbar.ToolbarViewModel

class TicketStatisticsViewModel : ToolbarViewModel<StatisticsRepository> {

    @ViewModelInject
    constructor(application: Application, statisticsRepository : StatisticsRepository):super(application,statisticsRepository)

    override fun onCreate() {
        super.onCreate()
        initToolbar()
    }

    private fun initToolbar() {
        setBackground(Color.TRANSPARENT)
        setTitleText("景区票务统计")
        setTitleTextColor(Color.WHITE)
        setBackIconSrc(R.mipmap.back_white)
    }

}