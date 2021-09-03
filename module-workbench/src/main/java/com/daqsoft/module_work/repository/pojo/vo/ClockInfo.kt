package com.daqsoft.module_work.repository.pojo.vo

/**
 * @package name：com.daqsoft.module_work.repository.pojo.vo
 * @date 27/7/2021 上午 10:19
 * @author zp
 * @describe
 */
data class ClockInfo(
    // 匹配到的打卡点
    val address: Address,
    // 已经打卡的记录
    val clockedIns: MutableList<ClockedIn>,
    // 最大的班次，表示需要上几次班
    val maxSegment : Int,
    // 应该打卡的信息
    val shouldClockIn: ClockedIn?,
    // 打卡点校验结果： RANGE_IN-在范围内，可以打开，OUT_RANGE-超出范围，NOT_JOINED_RANGE-未加入考勤范围内,可用值:RANGE_IN,OUT_RANGE,NOT_JOINED_RANGE,REFRESH_PAGE,CLOCK_IN_SUCCESS
    val state: String,
    // 系统日期-打开日期使用服务端的时间
    val sysDate: String,
    // 系统时间
    val sysTime:String
)

data class Address(
    // 考勤点地址
    val address: String,
    // 考勤有效范围-单位米
    val distanceRange: Int,
    // 考勤组主键
    val groupId: Int,
    // id主键
    val id: Int,
    // 考勤点纬度
    val latitude: String,
    // 考勤点经度
    val longitude: String,
    // 考勤点名称
    val name: String,
    val vcode: String
)

data class ClockedIn(
    // 迟到或者早退原因
    val cause: String,
    // 打卡点
    val clockInPoint: String,
    // 打卡时间
    val clockInTime: String,
    val createTime: String,
    // 员工id
    val employeeId: Int,
    val id: Long,
    val modifyTime: String,
    // 是否为次日下班
    val nextDayOffWork: Boolean,
    // 迟到或者早退分钟数
    val notOnTimeMinute: Int,
    // 代表工作时间段中的哪一段
    val segmentNum: Int,
    // 打卡状态：10-正常，20-迟到，30-早退， 40-缺卡
    val state: Int,
    // 工作类型：上班-1, 下班-2
    val type: Int,
    val vcode: String,
    // 工作日期
    val workDate: String,
    // 工作时间
    val workTime: String
)
