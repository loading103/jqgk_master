package com.daqsoft.module_work.viewmodel.itemviewmodel

import androidx.databinding.ObservableField
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_common.R
import com.daqsoft.library_common.bean.Alarm
import com.daqsoft.module_work.viewmodel.AlarmListViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand

/**
 * @package name：com.daqsoft.module_task.viewmodel
 * @date 13/5/2021 上午 10:56
 * @author zp
 * @describe
 */
class AlarmListItemViewModel(
    private val alarmListViewModel: AlarmListViewModel,
    val alarm : Alarm,
    val state : Int? = 2
) : ItemViewModel<AlarmListViewModel>(alarmListViewModel){


    val dataSource = ObservableField<Alarm>()


    init {
        dataSource.set(alarm)
    }

    val itemOnClick = BindingCommand<Unit>(BindingAction {
        ARouter
            .getInstance()
            .build(ARouterPath.Workbench.PAGER_ALARM_DETAILS)
            .withString("from",ARouterPath.Workbench.PAGER_ALARM_LIST)
            .withString("id",alarm.id.toString())
            .withInt("state",state?:2)
            .navigation()
    })
}
