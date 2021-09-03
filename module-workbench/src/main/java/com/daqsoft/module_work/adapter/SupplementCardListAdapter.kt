package com.daqsoft.module_work.adapter

import androidx.databinding.ViewDataBinding
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.module_work.R
import com.daqsoft.module_work.databinding.RecycleviewSupplementCardListItemBinding
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter

/**
 * @package name：com.daqsoft.module_work.adapter
 * @date 6/7/2021 下午 2:48
 * @author zp
 * @describe
 */
class SupplementCardListAdapter : BindingRecyclerViewAdapter<Any>() {

    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: Any
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)
        val itemBinding = binding as RecycleviewSupplementCardListItemBinding

        var bcn = itemBinding.tvState.context.resources.getColor(R.color.color_edfff8)
        var tcn = itemBinding.tvState.context.resources.getColor(R.color.color_21b87c)
        var tn = "已通过"
        when(position%4){
            0 ->{
                bcn = itemBinding.tvState.context.resources.getColor(R.color.color_edfff8)
                tcn = itemBinding.tvState.context.resources.getColor(R.color.color_21b87c)
                tn = "已通过"
            }
            1 ->{
                bcn = itemBinding.tvState.context.resources.getColor(R.color.color_eff7ff)
                tcn = itemBinding.tvState.context.resources.getColor(R.color.color_59abff)
                tn = "待审批"
            }
            2 ->{
                bcn = itemBinding.tvState.context.resources.getColor(R.color.color_fff2f2)
                tcn = itemBinding.tvState.context.resources.getColor(R.color.color_ff5757)
                tn = "被驳回"
            }
            3 ->{
                bcn = itemBinding.tvState.context.resources.getColor(R.color.color_fff2f2)
                tcn = itemBinding.tvState.context.resources.getColor(R.color.color_ff5757)
                tn = "已撤销"
            }

        }

        itemBinding.tvState.helper.setBackgroundColorNormal(bcn)
        itemBinding.tvState.helper.setTextColorNormal(tcn)
        itemBinding.tvState.setText(tn)

        itemBinding.root.setOnClickListenerThrottleFirst{
            onClickListener?.itemOnClick(position, item)
        }


    }


    private var onClickListener : OnClickListener? = null

    fun setOnClickListener(listener: OnClickListener){
        this.onClickListener = listener
    }

    interface OnClickListener{

        fun itemOnClick(position: Int,item: Any)
    }
}