package com.daqsoft.module_work.repository.pojo.vo

/**
 * @package name：com.daqsoft.module_work.repository.pojo.vo
 * @date 3/8/2021 下午 6:03
 * @author zp
 * @describe
 */
data class AttendanceDetail(
    // 当日打卡明细
    val clockInList: List<ClockedIn>,
    // 打卡次数
    val clockInNum: Int,
    // 工作日期
    val workDate: String,
    // 共计工时(单位小时)
    val workHours: Float
)