package com.daqsoft.module_task.viewmodel.itemviewmodel

import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_task.repository.pojo.vo.Task
import com.daqsoft.module_task.viewmodel.TaskListViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand

/**
 * @package name：com.daqsoft.module_task.viewmodel
 * @date 13/5/2021 上午 10:56
 * @author zp
 * @describe
 */
class TaskListItemViewModel(
    private val taskListViewModel: TaskListViewModel,
    val task : Task
) : ItemViewModel<TaskListViewModel>(taskListViewModel){

    init {

    }

    val itemOnClick = BindingCommand<Unit>(BindingAction {
        ARouter
            .getInstance()
            .build(ARouterPath.Workbench.PAGER_ALARM_DETAILS)
            .withString("from",ARouterPath.Task.PAGER_TASK_LIST)
            .withString("id","")
            .withInt("state",2)
            .navigation()
    })
}
