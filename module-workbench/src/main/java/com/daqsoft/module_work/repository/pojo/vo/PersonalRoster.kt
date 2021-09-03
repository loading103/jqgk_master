package com.daqsoft.module_work.repository.pojo.vo

/**
 * @package name：com.daqsoft.module_work.repository.pojo.vo
 * @date 29/7/2021 上午 11:42
 * @author zp
 * @describe
 */
data class PersonalRoster(
    // 工作日期
    val calendarDay: String,
    // 上下班打卡标识(true-每个时间段都要打卡,false-上下班只需各打一次卡)
    val clockInFlag: Boolean,
    // 员工主键
    val employeeId: Int,
    // 员工值班日历单元id主键
    val id: Int,
    // 休息时间段
    val restSegment: Segment,
    // 轮班规则id主键
    val ruleId: Int,
    // 轮班规则名称
    val ruleName: String,
    // 班次id主键
    val scheduleId: Int,
    // 班次名称
    val scheduleName: String,
    // 班次时间段
    val segments: List<Segment>,
    // 标识是否上班(true-上班,false-休息)
    val workStatus: Boolean
)

data class Segment(
    // 下班时间是今日/明日(true-今日,false-明日)
    val endDateFlag: Boolean,
    // 结束时间(HH:mm)
    val endTime: String,
    // 开始时间(HH:mm)
    val startTime: String
)