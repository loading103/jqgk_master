package com.daqsoft.module_statistics.repository.pojo.vo

/**
 * @package name：com.daqsoft.module_statistics.repository.pojo.vo
 * @date 23/6/2021 下午 3:08
 * @author zp
 * @describe
 */
data class TicketHoliday(
    val holiday: List<TicketHolidayX>,
    val sale: TicketSaleInfo
)

data class TicketHolidayX(
    val holiday: String,
    val totalQuantity: Int,
    val y2y: Int
)
