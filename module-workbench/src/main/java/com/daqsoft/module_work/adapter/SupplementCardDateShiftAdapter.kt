package com.daqsoft.module_work.adapter

import android.annotation.SuppressLint
import androidx.databinding.ViewDataBinding
import com.daqsoft.library_common.R
import com.daqsoft.module_work.databinding.RecycleviewSupplementCardShiftItemBinding
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter

/**
 * @package name：com.daqsoft.module_work.adapter
 * @date 8/7/2021 下午 4:08
 * @author zp
 * @describe
 */
class SupplementCardDateShiftAdapter: BindingRecyclerViewAdapter<String>() {

    private var selectedPosition = -1

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: String
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)

        val itemBinding = binding as RecycleviewSupplementCardShiftItemBinding

        itemBinding.root.isEnabled = position%2==0
        itemBinding.root.setOnClickListener {
            itemOnClickListener?.onClick(position,item)
            selectedPosition = position
            notifyDataSetChanged()
        }

        itemBinding.content.isEnabled = position%2==0
        itemBinding.content.isSelected = selectedPosition == position
        itemBinding.content.text = item
        if (selectedPosition != -1 && selectedPosition == position){
            val drawable = itemBinding.content.context.resources.getDrawable(R.mipmap.selected)
            drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
            itemBinding.content.setCompoundDrawables(null, null, drawable, null)
        }else{
            itemBinding.content.setCompoundDrawables(null, null, null, null)
        }
    }



    /**
     * 设置选中位置
     */
    fun setSelectedPosition(position : Int){
        selectedPosition = position
    }

    private var itemOnClickListener : ItemOnClickListener? = null

    fun setItemOnClickListener(listener: ItemOnClickListener){
        this.itemOnClickListener = listener
    }

    interface ItemOnClickListener{
        fun onClick(position: Int,item : String)
    }
}