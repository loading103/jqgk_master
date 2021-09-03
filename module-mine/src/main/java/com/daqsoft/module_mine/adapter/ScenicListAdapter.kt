package com.daqsoft.module_mine.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import androidx.databinding.ViewDataBinding
import com.daqsoft.module_mine.R
import com.daqsoft.module_mine.databinding.RecycleviewScenicListItemBinding
import com.daqsoft.module_mine.repository.pojo.vo.CompanyInfo
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter

/**
 * @package name：com.daqsoft.module_mine.adapter
 * @date 18/5/2021 上午 10:43
 * @author zp
 * @describe 景区列表 adapter
 */
class ScenicListAdapter : BindingRecyclerViewAdapter<CompanyInfo>() {

    private var selectedPosition= -1

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: CompanyInfo
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)
        val itemBinding = binding as RecycleviewScenicListItemBinding
        itemBinding.root.setOnClickListener {
            itemOnClickListener?.onClick(position,item)
            selectedPosition = position
            notifyDataSetChanged()
        }

        itemBinding.name.isSelected = selectedPosition == position

        if (selectedPosition != -1 && selectedPosition == position){
            val drawable = itemBinding.name.context.resources.getDrawable(R.mipmap.selected)
            drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
            itemBinding.name.setCompoundDrawables(null, null, drawable, null)
        }else{
            itemBinding.name.setCompoundDrawables(null, null, null, null)
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
        fun onClick(position: Int,item : CompanyInfo)
    }
}