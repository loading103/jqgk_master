package com.daqsoft.module_work.viewmodel.itemviewmodel

import androidx.databinding.ObservableField
import com.daqsoft.module_work.repository.pojo.vo.Department
import com.daqsoft.mvvmfoundation.base.BaseViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel

/**
 * @package name：com.daqsoft.module_work.viewmodel.itemviewmodel
 * @date 8/5/2021 下午 1:38
 * @author zp
 * @describe 部门 item viewModel
 */
class DepartmentItemViewModel (
    private val baseViewModel: BaseViewModel<*>,
    val department: Department
) : MultiItemViewModel<BaseViewModel<*>>(baseViewModel){


    val dept = ObservableField<Department>()

    init {
        dept.set(department)
    }
}