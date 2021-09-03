package com.daqsoft.module_work.repository.pojo.vo

/**
 * @package name：com.daqsoft.module_work.repository.pojo.vo
 * @date 2/8/2021 下午 2:49
 * @author zp
 * @describe
 */
data class MonthlyStatistics(
    // 旷工日期集合
    val absenteeismDateList: List<AttendanceDate>,
    // 旷工天数
    val absenteeismDateNum: Int,
    // 出勤日期集合
    val attendanceDateList: List<AttendanceDate>,
    // 出勤天数
    val attendanceDateNum: Int,
    // 平均工时(小时)
    val averageWorkHours: Double,
    // 迟到日期集合
    val lateDateList: List<AttendanceDate>,
    // 迟到次数
    val lateNum: Int,
    // 请假日期集合
    val leaveDateList: List<AttendanceDate>,
    // 请假天数
    val leaveDateNum: Int,
    // 早退日期集合
    val leaveEarlyDateList: List<AttendanceDate>,
    // 早退次数
    val leaveEarlyNum: Int,
    // 请假小时数
    val leaveHourNum: Int,
    // 缺卡日期集合
    val missDateList: List<AttendanceDate>,
    // 缺卡次数
    val missNum: Int,
    // 正常日期集合(排班出勤正常)
    val normalDateList: List<AttendanceDate>,
    // 正常天数(排班出勤正常)
    val normalDateNum: Int,
    // 总工时(小时)
    val realWorkHours: Double,
    // 休息日期集合
    val restDateList: List<AttendanceDate>,
    // 休息天数
    val restDateNum: Int
)

data class AttendanceDate(
    // 天数(当日累计)
    val dateNum: Int,
    // 小时数(当日累计)
    val hourNum: Int,
    // 分钟数(当日累计)
    val minutes: Int,
    // 次数(当日累计)
    val num: Int,
    // 星期几
    val week: Int,
    // 星期几描述
    val weekDesc: String,
    // 日期
    val workDate: String
)