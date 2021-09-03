package com.daqsoft.module_work.repository.pojo.vo

/**
 * @package name：com.daqsoft.module_work.repository.pojo.vo
 * @date 22/7/2021 下午 4:15
 * @author zp
 * @describe
 */
data class IncidentType(
    // 主键ID
    val id: Int,
    // 更新者
    val `operator`: String,
    // 是否启用 0:禁用 1:启用
    val state: Int,
    // 事件上报类型
    val type: String,
    // 更新时间
    val updateTime: String,
    val vcode: String
)