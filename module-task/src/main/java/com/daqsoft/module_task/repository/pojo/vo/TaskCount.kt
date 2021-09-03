package com.daqsoft.module_task.repository.pojo.vo

/**
 * @package name：com.daqsoft.module_task.repository.pojo.vo
 * @date 2/6/2021 上午 11:07
 * @author zp
 * @describe
 */
data class TaskCount(
    // 数量
    val quantity: Int,
    // 状态：10-待办，20-已办，30-待阅，40-已阅
    val status: Int
)