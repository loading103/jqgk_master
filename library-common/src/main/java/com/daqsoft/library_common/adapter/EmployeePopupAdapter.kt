package com.daqsoft.library_common.adapter

import android.annotation.SuppressLint
import android.net.Uri
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.daqsoft.library_common.R
import com.daqsoft.library_common.bean.Employee
import com.daqsoft.library_common.databinding.RecyclerviewEmployeePopupItemBinding
import com.luck.picture.lib.config.PictureMimeType
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import kotlin.coroutines.coroutineContext

/**
 * @package name：com.daqsoft.module_workbench.adapter
 * @date 19/2/2021 下午 4:26
 * @author zp
 * @describe
 */
class EmployeePopupAdapter : BindingRecyclerViewAdapter<Employee>() {

    private var selectedPosition= -1

    private var icons : List<Int> ? = null

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: Employee
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)
        val itemBinding = binding as RecyclerviewEmployeePopupItemBinding
        itemBinding.root.setOnClickListener {
            itemOnClickListener?.onClick(position,item)
            selectedPosition = position
            notifyDataSetChanged()
        }

        Glide.with(itemBinding.root.context)
            .load(item.img)
            .centerCrop()
            .placeholder(R.mipmap.tongxunlv_touxiang)
            .into(itemBinding.avatar)

        itemBinding.content.text = "${item.name}  ${item.depName}"

        itemBinding.content.isSelected = selectedPosition == position

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
        fun onClick(position: Int,employee : Employee)
    }
}