package com.daqsoft.module_work.adapter

import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import cn.iwgang.simplifyspan.SimplifySpanBuild
import cn.iwgang.simplifyspan.unit.SpecialTextUnit
import com.daqsoft.library_base.utils.TimeUtils
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.module_work.R
import com.daqsoft.module_work.databinding.RecycleviewClockRecordItemBinding
import com.daqsoft.module_work.repository.pojo.vo.ClockedIn
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter

/**
 * @package name：com.daqsoft.module_work.adapter
 * @date 28/7/2021 上午 11:35
 * @author zp
 * @describe
 */
class ClockAdapter : BindingRecyclerViewAdapter<ClockedIn>() {

    private var today = false

    fun setToday(today:Boolean){
        this.today = today
    }


    private var maxSegment = 0

    fun setMaxSegment(no : Int){
        this.maxSegment = no
    }

    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: ClockedIn
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)

        val itemBinding = binding as RecycleviewClockRecordItemBinding

        // title 内容
        val titleText  = if (maxSegment > 1){
            "第${toChineseNo(item.segmentNum)}次${if (item.type==1)"上班" else "下班"}打卡  ${item.workTime}"
        }else{
            "${if (item.type==1)"上班" else "下班"}打卡  ${item.workTime}"
        }
        itemBinding.clockTitle.text = titleText

        // 打卡时间
        itemBinding.clockTime.text = "打卡时间：${TimeUtils.coverDateFormat(item.clockInTime,"yyyy-MM-dd HH:mm:ss","HH:mm:ss")}"

        // 打卡状态
        when(item.state){
            10 -> {
                itemBinding.timeDiff.isVisible = false
            }
            20 -> {
                if (item.notOnTimeMinute > 0){
                    itemBinding.timeDiff.isVisible = true
                    itemBinding.timeDiff.text = "迟到${TimeUtils.minuteToHour(item.notOnTimeMinute )}"
                }
            }
            30 -> {
                if (item.notOnTimeMinute > 0){
                    itemBinding.timeDiff.isVisible = true
                    itemBinding.timeDiff.text = "早退${TimeUtils.minuteToHour(item.notOnTimeMinute )}"
                }
            }
            40 -> {
                itemBinding.timeDiff.isVisible = false
            }
        }

        // 地址
        itemBinding.clockAddress.text = item.clockInPoint

        // 更新 or 次日、昨日
        if (today && position == itemCount-1){
            itemBinding.day.isVisible = false
            binding.renew.isVisible = true
        }else{
            binding.renew.isVisible = false
            // 昨日、次日
            val clockInDate = TimeUtils.coverDateFormat(item.clockInTime,"yyyy-MM-dd HH:mm:ss","yyyy-MM-dd")
            val clockInTime = TimeUtils.stringToDate(clockInDate,"yyyy-MM-dd")?.time?:0
            val workDate = item.workDate
            val workTime = TimeUtils.stringToDate(workDate,"yyyy-MM-dd")?.time?:0
            when(clockInTime.compareTo(workTime)){
                -1->{
                    itemBinding.day.isVisible = true
                    itemBinding.day.text = "昨日"
                }
                0->{
                    itemBinding.day.isVisible = false
                }
                1->{
                    itemBinding.day.isVisible = true
                    itemBinding.day.text = "次日"
                }
            }
        }

        binding.renew.setOnClickListenerThrottleFirst{
            onClickListener?.renew(position,item)
        }
    }

    private var onClickListener : OnClickListener? = null

    fun setOnClickListener(listener: OnClickListener){
        this.onClickListener = listener
    }

    interface OnClickListener{
        fun renew(position: Int,content : ClockedIn)
    }


    private fun toChineseNo(i: Int): String {
        val s2 = arrayOf("零", "一", "二", "三", "四", "五", "六", "七", "八", "九")
        return s2[i]
    }


}