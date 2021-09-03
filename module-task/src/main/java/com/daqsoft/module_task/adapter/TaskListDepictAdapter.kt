package com.daqsoft.module_task.adapter

import androidx.databinding.ViewDataBinding
import com.daqsoft.module_task.databinding.RecycleviewTaskListItemBinding
import com.daqsoft.module_task.databinding.RecycleviewTaskListItemDepictItemBinding
import com.daqsoft.module_task.repository.pojo.vo.Task
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter

/**
 * @package name：com.daqsoft.module_task.adapter
 * @date 2/6/2021 上午 10:07
 * @author zp
 * @describe
 */
class TaskListDepictAdapter : BindingRecyclerViewAdapter<String>() {

    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: String
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)
        val itemBinding = binding as RecycleviewTaskListItemDepictItemBinding

        itemBinding.content.text = item

    }
}