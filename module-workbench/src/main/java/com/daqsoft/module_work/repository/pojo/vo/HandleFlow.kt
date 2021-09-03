package com.daqsoft.module_work.repository.pojo.vo

import com.daqsoft.module_work.R
import kotlinx.android.synthetic.main.activity_alarm_details.view.*

/**
 * @package name：com.daqsoft.module_work.repository.pojo.vo
 * @date 28/5/2021 下午 2:05
 * @author zp
 * @describe
 */
data class HandleFlow(
    // 创建时间
    val createTime: String,
    // 处理细节-描述
    val detail: String,
    // 事件ID
    val eventId: Int,
    // 手动上传的附件
    val files : List<File>,
    // 办理时间
    val handleTime: String,
    // 处理者标识:当前处理人
    val handler: Int,
    // 主键ID
    val id: Int,
    // 手动输入的办理人
    val inputName: String,
    // 是否最后一步
    val last: Boolean,
    // 当前层级,从1开始
    val level: Int,
    // 处理方式 1:任务指派,2:直接处理,3:无效报警,4:确认办结,5:重新处理,6:办理,7:办结,8:退回
    val method: Int,
    // 指向上一步的ID
    val prev: Int,
    // 指派的接收者标识,如果有
    val `receiver`: Int,
    // 指派的接收者名称,如果有
    val receiverName : String,
    val vcode: String,



    //  整体展开状态 true：展开，false：收起
    var expandState : Boolean = false,
    //  文字展开状态 true：展开，false：收起
    var txtExpandState : Boolean = false
)