package com.daqsoft.module_statistics.repository.pojo.vo

/**
 * @package name：com.daqsoft.module_statistics.repository.pojo.vo
 * @date 23/6/2021 上午 10:46
 * @author zp
 * @describe
 */
data class TicketTimePeriod(
    val sale : TicketSaleInfo,
    val timeInterval: List<TicketTimePeriodX>
)

data class TicketTimePeriodX(
    val annulus: Int,
    val current: Int,
    val time: String,
    val y2y: Int
)