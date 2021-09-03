package com.daqsoft.module_statistics.repository.pojo.vo

/**
 * @package name：com.daqsoft.module_statistics.repository.pojo.vo
 * @date 22/6/2021 下午 4:01
 * @author zp
 * @describe
 */
data class TicketChannel(
    // 	渠道统计
    val channel: List<TicketChannelX>,
    // 门票销售数数据
    val sale : TicketSaleInfo
)

data class TicketChannelX(
    // 渠道
    val channel: String,
    // 占比，例如 22%
    val percent: String,
    // 数量
    val totalQuantity: Int
)
