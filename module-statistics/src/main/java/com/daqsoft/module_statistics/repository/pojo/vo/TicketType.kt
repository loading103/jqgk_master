package com.daqsoft.module_statistics.repository.pojo.vo

/**
 * @package name：com.daqsoft.module_statistics.repository.pojo.vo
 * @date 23/6/2021 下午 3:08
 * @author zp
 * @describe
 */
data class TicketType(
    val sale: TicketSaleInfo,
    val ticketNameData : List<TicketTypeX>,
    val ticketName : List<String>
)

data class TicketTypeX(
    // 环比总数量
    val annulusTotalQuantity: Int,
    // 票种名称
    val ticketType: String,
    // 总销售额
    val totalAmount: Int,
    // 总销售量
    val totalQuantity: Int,
    // 同比总数量
    val y2yTotalQuantity: Int
)