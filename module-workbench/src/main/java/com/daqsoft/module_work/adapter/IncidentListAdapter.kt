package com.daqsoft.module_work.adapter

import androidx.databinding.ViewDataBinding
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.module_work.R
import com.daqsoft.module_work.databinding.RecycleviewIncidentListItemBinding
import com.daqsoft.module_work.repository.pojo.vo.Incident
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter

/**
 * @package name：com.daqsoft.module_work.adapter
 * @date 23/7/2021 下午 5:49
 * @author zp
 * @describe
 */
class IncidentListAdapter  : BindingRecyclerViewAdapter<Incident>() {


    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: Incident
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)
        val itemBinding = binding as RecycleviewIncidentListItemBinding

        itemBinding.title.text = item.typeName
        itemBinding.content.text = "上报内容：${item.content}"
        itemBinding.time.text = "告警时间：${item.createTime}"

        var tc = itemBinding.status.context.resources.getColor(R.color.color_888888)
        var bg = itemBinding.status.context.resources.getColor(R.color.color_f0f0f0)
        var txt = "未处理"
        when(item.status){
            1 ->{
                tc = itemBinding.status.context.resources.getColor(R.color.color_888888)
                bg = itemBinding.status.context.resources.getColor(R.color.color_f0f0f0)
                txt = "未处理"
            }
            2 ->{
                tc = itemBinding.status.context.resources.getColor(R.color.color_f0ad26)
                bg = itemBinding.status.context.resources.getColor(R.color.color_fff6e0)
                txt = "处理中"
            }
            3 ->{
                tc = itemBinding.status.context.resources.getColor(R.color.color_59abff)
                bg = itemBinding.status.context.resources.getColor(R.color.color_eff7ff)
                txt = "待确认"
            }
            4 ->{
                tc = itemBinding.status.context.resources.getColor(R.color.color_21b87c)
                bg = itemBinding.status.context.resources.getColor(R.color.color_edfff8)
                txt = "已办结"
            }
            5 ->{
                tc = itemBinding.status.context.resources.getColor(R.color.color_ff5757)
                bg = itemBinding.status.context.resources.getColor(R.color.color_fff2f2)
                txt = "重新办理"
            }
            6 ->{
                tc = itemBinding.status.context.resources.getColor(R.color.color_888888)
                bg = itemBinding.status.context.resources.getColor(R.color.color_f0f0f0)
                txt = "无效报警"
            }
            7 ->{
                tc = itemBinding.status.context.resources.getColor(R.color.color_59abff)
                bg = itemBinding.status.context.resources.getColor(R.color.color_eff7ff)
                txt = "系统解除"
            }
        }

        itemBinding.status.helper.setBackgroundColorNormal(bg)
        itemBinding.status.helper.setTextColorNormal(tc)
        itemBinding.status.setText(txt)

        itemBinding.root.setOnClickListenerThrottleFirst {
            onClickListener?.itemOnClick(position, item)
        }

    }


    private var onClickListener : OnClickListener? = null

    fun setOnClickListener(listener: OnClickListener){
        this.onClickListener = listener
    }

    interface OnClickListener{

        fun itemOnClick(position: Int,item: Incident)
    }
}