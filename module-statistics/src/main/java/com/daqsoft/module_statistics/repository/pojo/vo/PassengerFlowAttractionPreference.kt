package com.daqsoft.module_statistics.repository.pojo.vo

/**
 * @package name：com.daqsoft.module_statistics.repository.pojo.vo
 * @date 25/6/2021 上午 11:18
 * @author zp
 * @describe
 */
data class PassengerFlowAttractionPreference(
    // 结果项集合
    val items: List<PassengerFlowAttractionPreferenceX>,
    // 表的结果项集合(仅单景点且选中季度存在值(其他时间维度的图和表数据相同,取items))
    val tableItems: List<PassengerFlowAttractionPreferenceX>,
    // 总游客数
    val totalNum: Int
)

data class PassengerFlowAttractionPreferenceX(
    // 当前数据
    val currentNum: Int,
    // 环比变化数量(上升/下降数量)
    val monthOnMonthChangeNum: Float,
    // 环比数据
    val monthOnMonthNum: Int,
    // 环比比率(%)
    val monthOnMonthRate: Float,
    // 环比趋势（0-上升,1-下降,-1持平）
    val monthOnMonthTrend: Int,
    // 景点名称
    val spotName: String,
    // 时间
    val time: String,
    // 时间的表格描述
    val timeDesc: String,
    // 同比变化数量(上升/下降数量)
    val yearOnYearChangeNum: Float,
    // 同比数据
    val yearOnYearNum: Int,
    // 同比比率(%)
    val yearOnYearRate: Float,
    // 同比趋势（0-上升,1-下降,-1持平）
    val yearOnYearTrend: Int
)