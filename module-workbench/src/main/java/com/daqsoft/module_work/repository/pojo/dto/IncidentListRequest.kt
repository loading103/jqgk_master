package com.daqsoft.module_work.repository.pojo.dto

import com.daqsoft.library_base.global.ConstantGlobal

/**
 * @package name：com.daqsoft.module_work.repository.pojo.dto
 * @date 23/7/2021 下午 5:07
 * @author zp
 * @describe
 */
data class IncidentListRequest(
    // 1-今日 2-本周 3-本月 4-自定义时间范围 null-全部
    var dateType: Int?=null,
    // 起始日期 yyyy-MM-dd
    var startDate: String?=null,
    // 结束日期 yyyy-MM-dd
    var endDate: String?=null,
    // 处理状态
    var status: Int?=null,
    // 事件类型
    var type: Int?=null,
    // 页码
    var currPage: Int = ConstantGlobal.INITIAL_PAGE,
    // 每页显示数
    var pageSize: Int = ConstantGlobal.INITIAL_PAGE_SIZE,
)