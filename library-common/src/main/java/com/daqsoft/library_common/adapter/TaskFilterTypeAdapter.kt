package com.daqsoft.library_common.adapter

import androidx.databinding.ViewDataBinding
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.library_common.databinding.RecycleviewTaskFilterTypeItemBinding
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter

/**
 * @package name：com.daqsoft.module_task.adapter
 * @date 13/5/2021 下午 2:34
 * @author zp
 * @describe  任务筛选时间段 adapter
 */
class TaskFilterTypeAdapter : BindingRecyclerViewAdapter<String>() {


    private var selectedPosition= -1


    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: String
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)

        val itemBinding = binding as RecycleviewTaskFilterTypeItemBinding
        itemBinding.root.setOnClickListenerThrottleFirst {
            itemOnClickListener?.onClick(position,item)
            selectedPosition = position
            notifyDataSetChanged()
        }
        itemBinding.content.isSelected = selectedPosition == position
        binding.content.text = item
    }

    /**
     * 设置选中位置
     */
    fun setSelectedPosition(position : Int){
        selectedPosition = position
        notifyDataSetChanged()
    }

    private var itemOnClickListener : ItemOnClickListener? = null

    fun setItemOnClickListener(listener: ItemOnClickListener){
        this.itemOnClickListener = listener
    }

    interface ItemOnClickListener{
        fun onClick(position: Int,content : String)
    }
}