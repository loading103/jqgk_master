package com.daqsoft.module_work.viewmodel.itemviewmodel

import androidx.databinding.ObservableField
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_work.repository.pojo.vo.LeavaListBean
import com.daqsoft.module_work.viewmodel.LeaveListViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand

/**
 * 请假列表item
 */
class LeaveItemViewModel (private val leaveViewModel:
                          LeaveListViewModel, val data: LeavaListBean) : ItemViewModel<LeaveListViewModel>(leaveViewModel){


    val dataObservable = ObservableField<LeavaListBean>()

    val placeholderRes = ObservableField<Int>()

    init {
        dataObservable.set(data)
    }


    /**
     * item点击事件
     */
    val onItemClick = BindingCommand<Unit>(BindingAction {
        ARouter.getInstance().build(ARouterPath.Workbench.ADD_LEAVE_APPLY_INFO).navigation()
    })
}
