package com.daqsoft.library_common.bean

import com.daqsoft.library_common.R

/**
 * @package name：com.daqsoft.module_work.repository.pojo.vo
 * @date 27/5/2021 下午 2:41
 * @author zp
 * @describe
 */
data class Alarm(
    // 告警地址
    val addr: String,
    // 告警时间
    val createTime: String,
    val id: Int,
    // 来源标识
    val sourceCode: String,
    // 来源描述
    val sourceInfo: String,
    // 事件状态:未处理(1),处理中(2),待确认(3),已办结(4),重新办理(5),无效报警(6),系统解除(7)
    val status: Int,
    val statusText: String,
    // 告警类型,枚举:一键报警(1),巡更上报(2),越界告警(3),客流监测(4)
    val type: Int,
    val typeText: String,
    val vcode: String
){

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