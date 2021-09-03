package com.daqsoft.module_task.repository.pojo.vo

/**
 * @author zp
 * @package name：com.daqsoft.module_task.repository.pojo.vo
 * @date 2/6/2021 下午 1:37
 * @describe
 */
data class WeeklySummary(
    // 鸡汤
    val chickenSoup: String,
    // 创建时间
    val createTime: String,
    // 员工id
    val empId: Int,
    // 统计截止时间
    val endTime: String,
    // 统计项-已阅数量
    val haveReadQuantity: Int,
    val id: Int,
    // 统计项-已办数量
    val processedQuantity: Int,
    // 统计项-接收数量
    val receiveQuantity: Int,
    // 统计开始时间
    val startTime: String,
    // 统计项-待办数量
    val untreatedQuantity: Int,
    val vcode: String
)