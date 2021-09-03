package com.daqsoft.module_work.adapter

import androidx.databinding.ViewDataBinding
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.module_work.databinding.RecycleviewAddressBookItemBinding
import com.daqsoft.module_work.databinding.RecycleviewDepartmentItemBinding
import com.daqsoft.module_work.viewmodel.itemviewmodel.AddressBookItemViewModel
import com.daqsoft.module_work.viewmodel.itemviewmodel.DepartmentItemViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter

/**
 * @package name：com.daqsoft.module_work.adapter
 * @date 18/5/2021 下午 5:40
 * @author zp
 * @describe 公司组织架构 adapter
 */
class OrganizationAdapter : BindingRecyclerViewAdapter<MultiItemViewModel<*>>() {

    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: MultiItemViewModel<*>
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)
        when(item.itemType){
            ConstantGlobal.DEPT -> {
                val itemBinding = binding as RecycleviewDepartmentItemBinding
                val itemViewModel = item as DepartmentItemViewModel
                binding.root.setOnClickListenerThrottleFirst {
                    itemOnClickListener?.deptOnClick(position, itemBinding, itemViewModel)
                }

            }
            ConstantGlobal.EMPLOYEE -> {
                val itemBinding = binding as RecycleviewAddressBookItemBinding
                val itemViewModel = item as AddressBookItemViewModel
                binding.root.setOnClickListenerThrottleFirst {
                    itemOnClickListener?.employeeOnClick(position, itemBinding, itemViewModel)
                }

                binding.call.setOnClickListenerThrottleFirst {
                    itemOnClickListener?.employeeCallOnClick(position, itemBinding, itemViewModel)

                }

            }
        }

    }


    private var itemOnClickListener : ItemOnClickListener? = null

    fun setItemOnClickListener(listener: ItemOnClickListener){
        this.itemOnClickListener = listener
    }

    interface ItemOnClickListener{


        fun deptOnClick(position: Int, itemBinding : RecycleviewDepartmentItemBinding, itemViewModel: DepartmentItemViewModel)

        fun employeeOnClick(position: Int, itemBinding : RecycleviewAddressBookItemBinding, itemViewModel: AddressBookItemViewModel)

        fun employeeCallOnClick(position: Int, itemBinding : RecycleviewAddressBookItemBinding, itemViewModel: AddressBookItemViewModel)
    }
}