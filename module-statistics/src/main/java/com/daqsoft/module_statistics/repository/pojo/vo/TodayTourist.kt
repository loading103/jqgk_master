package com.daqsoft.module_statistics.repository.pojo.vo

/**
 * @package name：com.daqsoft.module_statistics.repository.pojo.vo
 * @date 25/6/2021 下午 5:24
 * @author zp
 * @describe
 */
data class TodayTourist(
    // 环比变化率
    val cycleRate: Float,
    // 环比趋势 0:上升 1:下降 -1:持平
    val cycleTrend: Int,
    // 今日游客数 （入园总人数）
    val totalInNumThisDate: Int,
    // 今日游客出园人数
    val totalOutNumThisDate: Int,
    // 较昨日变化率
    val yesterdayRate: Float,
    // 较昨日趋势 0:上升 1:下降 -1:持平
    val yesterdayTrend: Int
): Statistics()