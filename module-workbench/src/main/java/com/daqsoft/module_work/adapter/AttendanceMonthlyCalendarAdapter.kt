package com.daqsoft.module_work.adapter

import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.module_work.databinding.RecycleviewAttendanceMonthlyCalendarRecordItemBinding
import com.daqsoft.module_work.repository.pojo.vo.ClockedIn
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter

/**
 * @package name：com.daqsoft.module_work.adapter
 * @date 5/7/2021 上午 11:21
 * @author zp
 * @describe
 */
class AttendanceMonthlyCalendarAdapter : BindingRecyclerViewAdapter<ClockedIn>() {

    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: ClockedIn
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)
        val itemBinding = binding as RecycleviewAttendanceMonthlyCalendarRecordItemBinding

        itemBinding.topLine.isVisible = position != 0
        itemBinding.bottomLine.isVisible = position != itemCount-1

        itemBinding.dot.isSelected = item.state == 40
        itemBinding.clockTitle.isSelected = item.state == 40
        itemBinding.supplementCard.isVisible = item.state == 40
        itemBinding.clockDetails.isVisible = item.state != 40

        itemBinding.clockTitle.text = "${if (item.type == 1) "上班" else "下班"}打卡  ${item.workTime}     ${if(item.state == 40) "缺卡" else ""}"
        itemBinding.clockTime.text = "打卡时间：${item.clockInTime}"
        itemBinding.clockAddress.text = item.clockInPoint

        itemBinding.supplementCard.setOnClickListenerThrottleFirst{
            onClickListener?.makeUpCard(position, item)
        }
    }


    private var onClickListener : OnClickListener? = null

    fun setOnClickListener(listener: OnClickListener){
        this.onClickListener = listener
    }

    interface OnClickListener{
        fun makeUpCard(position: Int,item: Any)
    }
}