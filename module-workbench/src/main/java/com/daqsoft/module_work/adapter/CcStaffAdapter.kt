package com.daqsoft.module_work.adapter

import android.graphics.Typeface
import androidx.databinding.ViewDataBinding
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.library_common.databinding.RecycleviewTaskFilterPeriodItemBinding
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter

class CcStaffAdapter: BindingRecyclerViewAdapter<String>() {


    private var selectedPosition= -1


    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: String
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)

        val itemBinding = binding as RecycleviewTaskFilterPeriodItemBinding
        itemBinding.root.setOnClickListenerThrottleFirst {
            itemOnClickListener?.onClick(position,item)
            selectedPosition = position
            notifyDataSetChanged()
        }
        itemBinding.content.isSelected = selectedPosition == position

        if (itemBinding.content.isSelected){
            binding.content.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        }else{
            binding.content.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
        }
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