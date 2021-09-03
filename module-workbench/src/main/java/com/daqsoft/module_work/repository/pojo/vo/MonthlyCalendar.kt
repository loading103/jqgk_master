package com.daqsoft.module_work.repository.pojo.vo

/**
 * @package name：com.daqsoft.module_work.repository.pojo.vo
 * @date 2/8/2021 下午 5:36
 * @author zp
 * @describe
 */
data class MonthlyCalendar(
    // 员工id主键
    val employeeId: Int,
    // 考勤状态(NORMAL-正常,LEAVE-请假,LATE-迟到,LEAVE_EARLY-早退,MISSING-缺卡,ABSENTEEISM-旷工)
    val statusList : List<Status>,
    // 日期(yyyy-MM-dd)
    val workDate: String
)

data class Status(
    val status: String,
    // 考勤状态描述
    val statusDesc: String,
)