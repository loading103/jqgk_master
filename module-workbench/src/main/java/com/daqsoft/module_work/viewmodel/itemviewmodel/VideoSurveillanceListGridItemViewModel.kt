package com.daqsoft.module_work.viewmodel.itemviewmodel

import androidx.databinding.ObservableField
import com.daqsoft.library_common.bean.AppMenu
import com.daqsoft.module_work.repository.pojo.vo.VideoSurveillanceList
import com.daqsoft.module_work.viewmodel.VideoSurveillanceListViewModel
import com.daqsoft.module_work.viewmodel.WorkViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand

/**
 * @package name：com.daqsoft.module_work.viewmodel.itemviewmodel
 * @date 14/7/2021 上午 9:36
 * @author zp
 * @describe
 */
class VideoSurveillanceListGridItemViewModel (
    private val videoSurveillanceListViewModel: VideoSurveillanceListViewModel,
    val item : VideoSurveillanceList
) : MultiItemViewModel<VideoSurveillanceListViewModel>(videoSurveillanceListViewModel){


    val itemObservable = ObservableField<VideoSurveillanceList>()

    init {
        itemObservable.set(item)
    }

    val itemOnClick = BindingCommand<Unit>(BindingAction {
        videoSurveillanceListViewModel.itemOnClick(item)
    })

}