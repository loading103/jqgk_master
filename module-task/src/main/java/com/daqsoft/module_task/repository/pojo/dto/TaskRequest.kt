package com.daqsoft.module_task.repository.pojo.dto

import com.daqsoft.library_base.global.ConstantGlobal

/**
 * @package name：com.daqsoft.module_task.repository.pojo.dto
 * @date 1/6/2021 上午 10:32
 * @author zp
 * @describe
 */
data class TaskRequest(
    // 页码
    var currPage: Int = ConstantGlobal.INITIAL_PAGE,
    // 每页显示数
    var pageSize: Int = ConstantGlobal.INITIAL_PAGE_SIZE,
    // 业务类型=>级联第一级
    var businessType: String? = null,
    // 结束时间
    var endDate: String? = null,
    // 快捷日期：1=>今日,2=>本周,3=>本月
    var fastDate: String? = null,
    // 开始时间
    var startDate: String? = null,
    // 状态=>级联第三级
    var status: String? = null,
    // 任务状态：10=>待办,20=>已办
    var taskStatus: Int? = null,
    // 任务类型=>级联第二级
    var taskType: String? = null
)