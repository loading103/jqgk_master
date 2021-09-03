package com.daqsoft.module_work.repository.pojo.dto

/**
 * @package name：com.daqsoft.module_work.repository.pojo.dto
 * @date 28/7/2021 上午 9:22
 * @author zp
 * @describe
 */
data class ClockInfoRequest(
    var addressId : Int,
    // 迟到或者早退原因
    var cause: String?,
    // 用户实时位置
    var location: String?,
    // 应该打卡的记录id
    var shouldClockInId: Long?
)