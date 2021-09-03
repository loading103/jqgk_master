package com.daqsoft.module_statistics.repository.pojo.vo

/**
 * @package name：com.daqsoft.module_statistics.repository.pojo.vo
 * @date 23/6/2021 下午 4:54
 * @author zp
 * @describe
 */
data class TicketTypeSingle(
    val sale: TicketSaleInfo,
    val ticketType: List<TicketTypeSingleX>
)


data class TicketTypeSingleX(
    // 环比总数量
    val annulusTotalQuantity: Int,
    // 时间
    val time: String,
    // 总销售额
    val totalAmount: Int,
    // 总销售量
    val totalQuantity: Int,
    // 同比总数量
    val y2yTotalQuantity: Int
)