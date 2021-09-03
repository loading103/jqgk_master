package com.daqsoft.module_statistics.repository.pojo.vo

/**
 * @package name：com.daqsoft.module_statistics.repository.pojo.vo
 * @date 23/6/2021 下午 3:08
 * @author zp
 * @describe
 */
data class TicketHolidaySingle(
    val holiday: List<TicketHolidaySingleX>,
    val sale: TicketSaleInfo
)

data class TicketHolidaySingleX(
    val annulus: Int,
    val current: Int,
    val time: String,
    val y2y: Int
)
