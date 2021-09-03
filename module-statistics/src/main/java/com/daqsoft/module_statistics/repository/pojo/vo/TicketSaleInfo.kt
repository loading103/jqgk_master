package com.daqsoft.module_statistics.repository.pojo.vo

/**
 * @package name：com.daqsoft.module_statistics.repository.pojo.vo
 * @date 23/6/2021 上午 10:48
 * @author zp
 * @describe
 */
data class TicketSaleInfo(
    // 销售总金额
    val totalAmount: Int,
    // 销售总金额环比
    val totalAmountAnnulus: String,
    // 销售总金额环比的值
    val totalAmountAnnulusVal: Float,
    // 销售总金额同比，百分比
    val totalAmountY2y: String,
    // 销售总金额同比的值
    val totalAmountY2yVal: Float,
    // 销售总数量
    val totalQuantity: Int,
    // 销售总数量环比百分比
    val totalQuantityAnnulus: String,
    // 销售总数量环比的值
    val totalQuantityAnnulusVal: Float,
    // 销售总数量同比，百分比
    val totalQuantityY2y: String,
    // 销售总数量同比的值
    val totalQuantityY2yVal: Float,
    // 待核销总数
    val totalToBeWriteOffQuantity: Int
)