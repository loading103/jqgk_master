package com.daqsoft.module_statistics.repository.pojo.vo

/**
 * @package name：com.daqsoft.module_statistics.repository.pojo.vo
 * @date 29/6/2021 上午 10:30
 * @author zp
 * @describe
 */
data class VehicleHoliday(
    // 结果项集合
    val items: List<VehicleHolidayX>,
    // 总车俩数
    val totalNum: Int
)

data class VehicleHolidayX(
    // 节假日
    val holiday: String,
    // 景区名称
    val scenicName: String,
    // 时间
    val time: String,
    // 总车辆数(辆)
    val totalNum: Int,
    // 同比变化数量(上升/下降数量)
    val yearOnYearChangeNum: Float,
    // 同比车辆(辆)
    val yearOnYearNum: Int,
    // 同比比率(%)
    val yearOnYearRate: Float,
    // 同比趋势（0-上升,1-下降,-1持平）
    val yearOnYearTrend: Int
)