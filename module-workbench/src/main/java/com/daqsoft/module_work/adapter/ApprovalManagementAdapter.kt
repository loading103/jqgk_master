package com.daqsoft.module_work.adapter

import android.graphics.Typeface
import androidx.databinding.ViewDataBinding
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.library_common.databinding.RecycleviewTaskFilterPeriodItemBinding
import com.daqsoft.module_work.databinding.RecycleviewApprovalManagementItemBinding
import com.daqsoft.module_work.repository.pojo.vo.ApprovalManagement
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter

/**
 * @package name：com.daqsoft.module_work.adapter
 * @date 9/7/2021 下午 1:46
 * @author zp
 * @describe
 */
class ApprovalManagementAdapter: BindingRecyclerViewAdapter<ApprovalManagement>() {


    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: ApprovalManagement
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)

        val itemBinding = binding as RecycleviewApprovalManagementItemBinding
        itemBinding.root.setOnClickListenerThrottleFirst {
            itemOnClickListener?.onClick(position,item)
        }

        itemBinding.icon.setBackgroundResource(item.icon)
        itemBinding.title.text = item.title
        itemBinding.remark.text = item.remark
    }


    private var itemOnClickListener: ItemOnClickListener? = null

    fun setItemOnClickListener(listener: ItemOnClickListener) {
        this.itemOnClickListener = listener
    }

    interface ItemOnClickListener {
        fun onClick(position: Int, content: ApprovalManagement)
    }

}
