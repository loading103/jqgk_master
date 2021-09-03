package com.daqsoft.module_work.viewmodel.itemviewmodel

import android.view.View
import androidx.databinding.ObservableField
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_common.bean.Employee
import com.daqsoft.mvvmfoundation.base.BaseViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand

/**
 * @package name：com.daqsoft.module_work.viewmodel.itemviewmodel
 * @date 8/5/2021 下午 1:38
 * @author zp
 * @describe 通讯录 item viewModel
 */
class AddressBookItemViewModel (
    private val baseViewModel : BaseViewModel<*>,
    val employee: Employee
) : MultiItemViewModel<BaseViewModel<*>>(baseViewModel){


    val employeeObservable = ObservableField<Employee>()


    val callObservable  = ObservableField<Boolean>()

    init {

        callObservable.set(employee.appSwitch != 1)

        employeeObservable.set(employee)
    }



    val itemOnClick = BindingCommand<Unit>(BindingAction {
        ARouter
            .getInstance()
            .build(ARouterPath.Mine.PAGER_PERSONAL_INFO)
            .withString("id", employee.id.toString())
            .navigation()
    })
}