package com.daqsoft.module_home.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.module_home.repository.HomeRepository
import com.daqsoft.mvvmfoundation.toolbar.ToolbarViewModel

class HomeViewModel : ToolbarViewModel<HomeRepository> {

    @ViewModelInject
    constructor(application: Application,homeRepository : HomeRepository):super(application,homeRepository)
}