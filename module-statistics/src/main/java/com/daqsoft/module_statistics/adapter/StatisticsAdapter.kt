package com.daqsoft.module_statistics.adapter

import android.animation.ValueAnimator
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.view.animation.AccelerateInterpolator
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import cn.iwgang.simplifyspan.SimplifySpanBuild
import cn.iwgang.simplifyspan.unit.SpecialImageUnit
import cn.iwgang.simplifyspan.unit.SpecialTextUnit
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.formatGroupingUsed
import com.daqsoft.library_base.utils.scrollingNumbers
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.module_statistics.R
import com.daqsoft.module_statistics.databinding.RecycleviewStatisticsItemBinding
import com.daqsoft.module_statistics.repository.pojo.vo.Statistics
import com.daqsoft.module_statistics.repository.pojo.vo.TodayTicket
import com.daqsoft.module_statistics.repository.pojo.vo.TodayTourist
import com.daqsoft.module_statistics.repository.pojo.vo.TodayVehicle
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter

/**
 * @package name：com.daqsoft.module_statistics.adapter
 * @date 9/6/2021 上午 10:01
 * @author zp
 * @describe 统计适配器
 */
class StatisticsAdapter  : BindingRecyclerViewAdapter<Statistics>() {

    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: Statistics
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)
        val itemBinding = binding as RecycleviewStatisticsItemBinding

        val rise = itemBinding.root.context.resources.getDrawable(R.mipmap.sj_icon_shangsheng)
        rise.setBounds(0, 0, rise.minimumWidth, rise.minimumHeight)

        val drop =  itemBinding.root.context.resources.getDrawable(R.mipmap.sj_icon_xiajiang)
        drop.setBounds(0, 0, drop.minimumWidth, drop.minimumHeight)


        when(item){
            is TodayTourist ->{
                itemBinding.container.setBackgroundResource(R.mipmap.sj_sskl_bg)
                itemBinding.icon.setImageResource(R.mipmap.sj_icon_keliu)
                itemBinding.title.text = "实时客流"
                itemBinding.countTxt.text = "今日游客数"
                itemBinding.count.scrollingNumbers(item.totalInNumThisDate)
                itemBinding.comparedToYesterday.text = SimplifySpanBuild()
                    .append(SpecialTextUnit("较昨日").setTextColor(itemBinding.comparedToYesterday.context.resources.getColor(R.color.color_d3e6ff)))
                    .append(SpecialTextUnit(" ${
                        when(item.yesterdayTrend){
                            0 -> "+"
                            1 -> "-"
                            else ->""
                        }
                    }${item.yesterdayRate}% ").setTextStyle(Typeface.BOLD).setTextColor(itemBinding.comparedToYesterday.context.resources.getColor(R.color.color_d3e6ff)))
                    .build()
                itemBinding.comparedToYesterday.setCompoundDrawables(null,null,rise,null)
                itemBinding.dayRingRatio.text = SimplifySpanBuild()
                    .append(SpecialTextUnit("日环比").setTextColor(itemBinding.comparedToYesterday.context.resources.getColor(R.color.color_d3e6ff)))
                    .append(SpecialTextUnit(" ${
                        when(item.cycleTrend){
                            0 -> "+"
                            1 -> "-"
                            else ->""
                        }
                    }${item.cycleRate}% ").setTextStyle(Typeface.BOLD).setTextColor(itemBinding.comparedToYesterday.context.resources.getColor(R.color.color_d3e6ff)))
                    .build()
                itemBinding.dayRingRatio.setCompoundDrawables(null,null,drop,null)
                itemBinding.statistics.text = "在园人数：${(item.totalInNumThisDate-item.totalOutNumThisDate).formatGroupingUsed()}"
                itemBinding.root.setOnClickListenerThrottleFirst{
                    ARouter.getInstance().build(ARouterPath.Statistics.PAGER_PASSENGER_FLOW_STATISTICS).navigation()
                }
            }
            is TodayTicket ->{
                itemBinding.container.setBackgroundResource(R.mipmap.sj_sspw_bg)
                itemBinding.icon.setImageResource(R.mipmap.sj_icon_piaowu)
                itemBinding.title.text = "实时票务"
                itemBinding.countTxt.text = "今日销售额"
                itemBinding.count.scrollingNumbers(item.totalAmount)
                itemBinding.comparedToYesterday.text = SimplifySpanBuild()
                    .append(SpecialTextUnit("较昨日").setTextColor(itemBinding.comparedToYesterday.context.resources.getColor(R.color.color_d3e6ff)))
                    .append(SpecialTextUnit(" ${
                        when(item.yesterdayTrend){
                            0 -> "+"
                            1 -> "-"
                            else ->""
                        }
                    }${item.yesterdayRate}% ").setTextStyle(Typeface.BOLD).setTextColor(itemBinding.comparedToYesterday.context.resources.getColor(R.color.color_d3e6ff)))
                    .build()
                itemBinding.comparedToYesterday.setCompoundDrawables(null,null,rise,null)
                itemBinding.dayRingRatio.text = SimplifySpanBuild()
                    .append(SpecialTextUnit("日环比 ").setTextColor(itemBinding.comparedToYesterday.context.resources.getColor(R.color.color_d3e6ff)))
                    .append(SpecialTextUnit(" ${
                        when(item.cycleTrend){
                            0 -> "+"
                            1 -> "-"
                            else ->""
                        }
                    }${item.cycleRate}% ").setTextStyle(Typeface.BOLD).setTextColor(itemBinding.comparedToYesterday.context.resources.getColor(R.color.color_d3e6ff)))
                    .build()
                itemBinding.dayRingRatio.setCompoundDrawables(null,null,drop,null)
                itemBinding.statistics.text = "待核销数：${item.totalToBeWriteOffQuantity.formatGroupingUsed()}"
                itemBinding.root.setOnClickListenerThrottleFirst{
                    ARouter.getInstance().build(ARouterPath.Statistics.PAGER_TICKET_STATISTICS).navigation()
                }
            }
            is TodayVehicle ->{
                itemBinding.container.setBackgroundResource(R.mipmap.sj_sscl_bg)
                itemBinding.icon.setImageResource(R.mipmap.sj_icon_cheliang)
                itemBinding.title.text = "实时车辆"
                itemBinding.countTxt.text = "今日车辆数"
                itemBinding.count.scrollingNumbers(item.totalInNumThisDate)
                itemBinding.comparedToYesterday.text = SimplifySpanBuild()
                    .append(SpecialTextUnit("较昨日 ").setTextColor(itemBinding.comparedToYesterday.context.resources.getColor(R.color.color_d3e6ff)))
                    .append(SpecialTextUnit(" ${
                        when(item.yesterdayTrend){
                            0 -> "+"
                            1 -> "-"
                            else ->""
                        }
                    }${item.yesterdayRate}% ").setTextStyle(Typeface.BOLD).setTextColor(itemBinding.comparedToYesterday.context.resources.getColor(R.color.color_d3e6ff)))
                    .build()
                itemBinding.comparedToYesterday.setCompoundDrawables(null,null,rise,null)
                itemBinding.dayRingRatio.text = SimplifySpanBuild()
                    .append(SpecialTextUnit("日环比 ").setTextColor(itemBinding.comparedToYesterday.context.resources.getColor(R.color.color_d3e6ff)))
                    .append(SpecialTextUnit(" ${
                        when(item.cycleTrend){
                            0 -> "+"
                            1 -> "-"
                            else ->""
                        }
                    }${item.cycleRate}% ").setTextStyle(Typeface.BOLD).setTextColor(itemBinding.comparedToYesterday.context.resources.getColor(R.color.color_d3e6ff)))
                    .build()
                itemBinding.dayRingRatio.setCompoundDrawables(null,null,drop,null)
                itemBinding.statistics.text = "空余车位数：${item.totalNonUseNumThisDate.formatGroupingUsed()}"
                itemBinding.root.setOnClickListenerThrottleFirst{
                    ARouter.getInstance().build(ARouterPath.Statistics.PAGER_VEHICLE_STATISTICS).navigation()
                }
            }
        }
    }
}
