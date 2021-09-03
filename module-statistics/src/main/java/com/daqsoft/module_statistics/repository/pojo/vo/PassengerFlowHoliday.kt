package com.daqsoft.module_statistics.repository.pojo.vo

/**
 * @package name：com.daqsoft.module_statistics.repository.pojo.vo
 * @date 25/6/2021 下午 2:18
 * @author zp
 * @describe
 */
data class PassengerFlowHoliday(
    // 结果项集合
    val items: List<PassengerFlowHolidayX>,
    // 总游客数
    val totalNum: Int,
    // 总销售额
    val totalPrice: Float,
    // 销售额同比，百分比
    val salesAmountY2y: String,
    // salesAmountY2yVal
    val salesAmountY2yVal: Int,
    // 游客数同比，百分比
    val totalNumY2y: String,
    // 游客数同比的值
    val totalNumY2yVal: Int,
)

data class PassengerFlowHolidayX(
    // 节假日
    val holiday: String,
    // 时间
    val time: String,
    // 总游客数(人)
    val totalNum: Int,
    // 销售额(元)
    val totalPrice: Float,
    // 同比变化数量(上升/下降数量)
    val yearOnYearChangeNum: Float,
    // 同比游客(人)
    val yearOnYearNum: Int,
    // 同比比率(%)
    val yearOnYearRate: Float,
    // 同比趋势（0-上升,1-下降,-1持平）
    val yearOnYearTrend: Int
)