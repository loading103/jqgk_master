package com.daqsoft.module_work.adapter

import android.widget.TextView
import androidx.databinding.ViewDataBinding
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.module_work.databinding.RecycleviewGridConditionPeriodBinding
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter

/**
 * @package name：com.daqsoft.module_work.adapter
 * @date 6/7/2021 下午 5:10
 * @author zp
 * @describe
 */
class GridConditionPeriodAdapter : BindingRecyclerViewAdapter<String>() {

    val periodList = hashMapOf<Int,String>()

    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: String
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)

        val itemBinding = binding as RecycleviewGridConditionPeriodBinding
        itemBinding.period.setOnClickListenerThrottleFirst{
            onClickListener?.onClick(position)
        }

        itemBinding.title.text = item


        itemBinding.period.text = periodList[position]?:"请选择时间范围"
    }

    fun setSelectRange(position: Int,period : String){
        periodList[position] = period
        notifyItemChanged(position)
    }

    fun reset(){
        periodList.clear()
        notifyDataSetChanged()
    }

    private var onClickListener: OnClickListener? = null

    fun setOnClickListener(listener: OnClickListener) {
        this.onClickListener = listener
    }

    interface OnClickListener {
        fun onClick(position: Int)
    }

}
