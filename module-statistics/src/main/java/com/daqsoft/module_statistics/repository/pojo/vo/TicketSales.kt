package com.daqsoft.module_statistics.repository.pojo.vo

/**
 * @package name：com.daqsoft.module_statistics.repository.pojo.vo
 * @date 22/6/2021 上午 9:22
 * @author zp
 * @describe
 */
data class TicketSales(
    // 总销售数环比
    val quantityAnnulus: String,
    // 总销售数同比
    val quantityY2Y: String,
    // 销售额环比
    val salesAnnulus: String,
    // 销售额同比
    val salesY2Y: String,
    // 销售类型统计
    val ticketType : List<TicketSalesX>,
    // 门票销售总额
    val totalAmount: Int,
    // 门票销售总数
    val totalQuantity: Int,
    // 待核销票数
    val totalToBeWriteOffQuantity: Int
)

data class TicketSalesX(
    // 占比，例如 22%
    val ratio: String,
    // 票的类型
    val ticketType: String,
    // 销售额
    val totalAmount: Int
)