package com.daqsoft.module_statistics.adapter

import android.view.View
import androidx.databinding.ViewDataBinding
import com.daqsoft.library_base.utils.formatGroupingUsed
import com.daqsoft.module_statistics.databinding.RecycleviewLegendItemBinding
import com.daqsoft.module_statistics.databinding.RecycleviewLegendItemWarpBinding
import com.daqsoft.module_statistics.repository.pojo.vo.MyLegend
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter

/**
 * @package name：com.daqsoft.module_statistics.adapter
 * @date 11/6/2021 下午 4:50
 * @author zp
 * @describe
 */
class MyLegendAdapter : BindingRecyclerViewAdapter<MyLegend>() {

    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: MyLegend
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)

        when(binding){
            is RecycleviewLegendItemWarpBinding -> {
                val itemBinding = binding as RecycleviewLegendItemWarpBinding
                itemBinding.color.helper.setBackgroundColorNormal(item.color)
                itemBinding.title.text = item.label
                itemBinding.content.visibility = View.GONE
            }

            is RecycleviewLegendItemBinding ->{
                val itemBinding =  binding as RecycleviewLegendItemBinding
                itemBinding.color.helper.setBackgroundColorNormal(item.color)
                itemBinding.title.text = item.label
                itemBinding.content.text = "${item.value} ${item.unit}"
            }

        }

    }
}