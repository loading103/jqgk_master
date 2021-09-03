package com.daqsoft.module_work.repository.pojo.vo

import com.daqsoft.module_work.R
import kotlinx.android.synthetic.main.activity_alarm_details.view.*

/**
 * @package name：com.daqsoft.module_work.repository.pojo.vo
 * @date 28/5/2021 下午 2:05
 * @author zp
 * @describe
 */
data class AlarmDetail(
    // 告警地址
    val addr: String,
    // 告警时间
    val createTimeStr: String,
    // 办理结果,必填
    val detail: String,
    // eventId,必填
    val eventId: Int,
    // 手动上传的附件
    val files: List<File>,
    // 办理时间
    val handleTimeStr: String,
    // 办理人名称:手动输入
    val inputName: String,
    // 处理方式 1:任务指派,2:直接处理,3:无效报警,4:确认办结,5:重新处理,6:办理,7:办结,8:退回
    val method: Int,
    // 告警电话
    val phone: String,
    // 指派人员Id,没有就不填
    val `receiver`: Int,
    val sfiles: List<File>,
    // 来源标识
    val sourceCode: String,
    // 来源描述
    val sourceInfo: String,
    // 事件状态:未处理(1),处理中(2),待确认(3),已办结(4),重新办理(5),无效报警(6),系统解除(7),8:退回,上报领导(9)
    val status: Int,
    val statusText: String,
    // 告警类型,枚举:一键报警(1),巡更上报(2),越界告警(3),客流监测(4)
    val type: Int,
    val typeText: String,
    val content: String,
    // 是否有操作权限
    val operable: Boolean
){
    
    fun coverType():String{
        return when(type){
            1 -> "一键报警"
            2 -> "巡更上报"
            3 -> "越界告警"
            4 -> "客流监测"
            else -> "未知"
        }
    }
    
    fun coverStatus():String{
        return when(status){
            1 -> "未处理"
            2 -> "处理中"
            3 -> "待确认"
            4 -> "已办结"
            5 -> "重新办理"
            6 -> "无效报警"
            7 -> "系统解除"
            8 -> "退回"
            9 -> "待处理"
            else -> "未知"
        }
    }

    fun coverStatusColor():Int{
        return when(status){
            1 -> R.color.color_ff9d46
            2 -> R.color.color_ff5757
            3 -> R.color.color_1ccb85
            4 -> R.color.color_dad7d4
            5 -> R.color.color_59abff
            6 -> R.color.color_ef9dee
            7 -> R.color.color_32d6d4
            8 -> R.color.color_ef9dee
            9 -> R.color.color_59abff
            else -> R.color.color_ff9d46
        }
    }
    
}
//
data class File(
    // 1:图片 2:视频 3:附件 4:音频
    val type: Int,
    // 文件地址
    val url: String,
    // 时间
    var time:String?=null
)
