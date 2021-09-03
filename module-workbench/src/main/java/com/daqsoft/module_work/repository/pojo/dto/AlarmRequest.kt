package com.daqsoft.module_work.repository.pojo.dto

import com.daqsoft.library_base.global.ConstantGlobal

/**
 * @package name：com.daqsoft.module_work.repository.pojo.dto
 * @date 27/5/2021 下午 2:46
 * @author zp
 * @describe
 */
data class AlarmRequest(
    // 页码
    var currPage: Int = ConstantGlobal.INITIAL_PAGE,
    // 告警时间:结束
    var endTime: String? = null,
    // 指定项导出用
    var ids: List<Int>? = null,
    // 关键字
    var keyword: String? = null,
    // 每页显示数
    var pageSize: Int = ConstantGlobal.INITIAL_PAGE_SIZE,
    // 告警时间:开始
    var startTime: String? = null,
    // 事件状态:未处理(1),处理中(2),待确认(3),已办结(4),重新办理(5),无效报警(6),系统解除(7)
    var status: Int? = null,
    // 0:所有告警 1:我指派的 2：我接收的
    var t: Int? = 0,
    // 告警类型,枚举:一键报警(1),巡更上报(2),越界告警(3),客流监测(4)
    var type: Int? = null
){



}