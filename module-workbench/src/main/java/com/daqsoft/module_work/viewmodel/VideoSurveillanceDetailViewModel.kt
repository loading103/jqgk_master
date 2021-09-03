package com.daqsoft.module_work.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.module_work.repository.WorkRepository
import com.daqsoft.mvvmfoundation.toolbar.ToolbarViewModel

/**
 * @package name：com.daqsoft.module_work.viewmodel
 * @date 14/7/2021 下午 3:33
 * @author zp
 * @describe
 */
class VideoSurveillanceDetailViewModel : ToolbarViewModel<WorkRepository>{

    @ViewModelInject
    constructor(application: Application, workRepository: WorkRepository) : super(
        application,
        workRepository
    )
}