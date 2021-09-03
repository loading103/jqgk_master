package com.daqsoft.library_common.adapter

import android.annotation.SuppressLint
import androidx.databinding.ViewDataBinding
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.library_common.bean.LeaveType
import com.daqsoft.library_common.databinding.ItemPopupLeaveChoosesBinding
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter

class LeaveChoosePopupAdapter : BindingRecyclerViewAdapter<LeaveType>() {

    private var bean : LeaveType?=null

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindBinding(binding: ViewDataBinding, variableId: Int, layoutRes: Int, position: Int, item: LeaveType) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)
        val itemBinding = binding as ItemPopupLeaveChoosesBinding

        itemBinding.tvTag.text=item.name

        itemBinding.tvTag.isSelected = !bean?.id.isNullOrBlank() && bean?.id == item.id

        itemBinding.root.setOnClickListenerThrottleFirst {
            itemOnClickListener?.onClick(position, item)
            bean = item
            notifyDataSetChanged()
        }
    }

    /**
     * 设置选中位置
     */
    fun setSelectedPosition(bean: LeaveType?){
        this.bean = bean
        notifyDataSetChanged()
    }

    private var itemOnClickListener : ItemOnClickListener? = null

    fun setItemOnClickListener(listener: ItemOnClickListener){
        this.itemOnClickListener = listener
    }

    interface ItemOnClickListener{
        fun onClick(position: Int,bean : LeaveType)
    }
}