package com.daqsoft.module_work.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.module_work.repository.WorkRepository
import com.daqsoft.mvvmfoundation.base.BaseViewModel

/**
 * @package name：com.daqsoft.module_work.viewmodel
 * @date 8/7/2021 下午 2:27
 * @author zp
 * @describe
 */
class SupplementCardDateCalendarViewModel : BaseViewModel<WorkRepository>{

    @ViewModelInject
    constructor(application: Application, workRepository: WorkRepository) : super(
        application,
        workRepository
    )
}