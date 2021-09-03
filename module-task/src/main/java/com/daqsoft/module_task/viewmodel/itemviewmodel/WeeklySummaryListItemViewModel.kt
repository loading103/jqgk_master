package com.daqsoft.module_task.viewmodel.itemviewmodel

import androidx.databinding.ObservableField
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_task.repository.pojo.vo.WeeklySummary
import com.daqsoft.module_task.viewmodel.WeeklySummaryListViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand

/**
 * @package name：com.daqsoft.module_task.viewmodel
 * @date 13/5/2021 上午 10:56
 * @author zp
 * @describe
 */
class WeeklySummaryListItemViewModel(
    private val weeklySummaryListViewModel: WeeklySummaryListViewModel,
    val weeklySummary : WeeklySummary
) : ItemViewModel<WeeklySummaryListViewModel>(weeklySummaryListViewModel){


    val  summary = ObservableField<WeeklySummary>()

    init {
        summary.set(weeklySummary)



    }

    val itemOnClick = BindingCommand<Unit>(BindingAction {
        ARouter.getInstance().build(ARouterPath.Task.PAGER_WEEKLY_SUMMARY).navigation()
    })
}
