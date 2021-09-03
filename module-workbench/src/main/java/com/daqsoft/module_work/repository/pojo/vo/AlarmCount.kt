package com.daqsoft.module_work.repository.pojo.vo

/**
 * @package name：com.daqsoft.module_work.repository.pojo.vo
 * @date 1/6/2021 上午 9:47
 * @author zp
 * @describe
 */
data class AlarmCount(
    // 数量
    val count: Int,
    // 编码：ALL-> 全部，SEND->我指派的，RECEIVE-> 我接收的
    val num: String,
    // 类型：0=>全部 ，1=> 我指派的 ，2 => 我收到的
    val type: Int
)