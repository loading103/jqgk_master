package com.daqsoft.module_work.adapter

import android.graphics.Color
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.module_work.R
import com.daqsoft.module_work.databinding.RecycleviewCollectDetailItemBinding
import com.daqsoft.module_work.databinding.RecycleviewCollectDetailWorkingHoursBinding
import com.daqsoft.module_work.databinding.RecycleviewCollectItemBinding
import com.daqsoft.module_work.repository.pojo.dto.AttendanceStatisticCollect
import com.daqsoft.module_work.repository.pojo.vo.ClockedIn
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.module_work.adapter
 * @date 2/7/2021 下午 2:24
 * @author zp
 * @describe
 */
class CollectAdapter : BindingRecyclerViewAdapter<AttendanceStatisticCollect>() {

    private var onClickListener : OnClickListener? = null

    fun setOnClickListener(listener: OnClickListener){
        this.onClickListener = listener
    }

    interface OnClickListener{
        fun detailItemOnClick(content : String)
    }


    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: AttendanceStatisticCollect
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)
        val itemBinding = binding as RecycleviewCollectItemBinding

        itemBinding.title.text = item.title
        itemBinding.content.helper.setTextColorNormal(item.contentColor)
        itemBinding.content.text = item.content

        var detailAdapter : BindingRecyclerViewAdapter<String>
        if ("平均工时" == item.title){
            detailAdapter = CollectDetailWorkingHoursAdapter().apply {
                this.itemBinding = ItemBinding.of(ItemBinding.VAR_NONE, R.layout.recycleview_collect_detail_working_hours)
                this.setItems(item.detail)
            }
        }else{
            detailAdapter = CollectDetailAdapter().apply {
                this.setColor(item.dotColor)
                this.setExpand(item.expand?: listOf())
                this.itemBinding = ItemBinding.of(ItemBinding.VAR_NONE, R.layout.recycleview_collect_detail_item)
                this.setItems(item.detail)
            }
        }

        itemBinding.detail.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = detailAdapter
        }

        itemBinding.content.setOnClickListenerThrottleFirst{
            if (item.detail.isNullOrEmpty()){
                return@setOnClickListenerThrottleFirst
            }
            itemBinding.content.isSelected = !itemBinding.content.isSelected
            itemBinding.detail.isVisible = itemBinding.content.isSelected
            itemBinding.arrow.rotation = if (itemBinding.content.isSelected) 90f else 0F
        }

    }



    inner class CollectDetailAdapter : BindingRecyclerViewAdapter<String>() {

        private var c = Color.parseColor("#59abff")
        private var e = listOf<String>()

        override fun onBindBinding(
            binding: ViewDataBinding,
            variableId: Int,
            layoutRes: Int,
            position: Int,
            item: String
        ) {
            super.onBindBinding(binding, variableId, layoutRes, position, item)
            val itemBinding = binding as RecycleviewCollectDetailItemBinding

            itemBinding.topLine.isVisible = position != 0
            itemBinding.bottomLine.isVisible = position != itemCount-1
            itemBinding.content.text = item

            if (!e.isNullOrEmpty()){
                try {
                    itemBinding.expand.text = e[position]
                }catch (e:Exception){
                    itemBinding.expand.text = ""
                }
            }else{
                itemBinding.expand.text = ""
            }

            itemBinding.point.helper.setBackgroundColorNormal(c)

            itemBinding.root.setOnClickListenerThrottleFirst{
                onClickListener?.detailItemOnClick(item)
            }
        }

        fun  setColor(color:Int){
            c = color
        }

        fun  setExpand(expand:List<String>){
            e = expand
        }
    }


    inner class CollectDetailWorkingHoursAdapter : BindingRecyclerViewAdapter<String>() {

        override fun onBindBinding(
            binding: ViewDataBinding,
            variableId: Int,
            layoutRes: Int,
            position: Int,
            item: String
        ) {
            super.onBindBinding(binding, variableId, layoutRes, position, item)
            val itemBinding = binding as RecycleviewCollectDetailWorkingHoursBinding
            itemBinding.title.text = "总工时"
            itemBinding.content.text = item


            itemBinding.root.setOnClickListenerThrottleFirst{
                onClickListener?.detailItemOnClick(item)
            }

        }
    }
}