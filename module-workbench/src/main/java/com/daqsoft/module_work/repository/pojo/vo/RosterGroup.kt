package com.daqsoft.module_work.repository.pojo.vo

/**
 * @package name：com.daqsoft.module_work.repository.pojo.vo
 * @date 29/7/2021 上午 11:27
 * @author zp
 * @describe
 */
data class RosterGroup(
    // 员工id主键
    val employeeId: Int,
    // 员工头像
    val employeeImg: String,
    // 员工名称
    val employeeName: String,
    // 考勤组id
    val groupId: Int,
    // 考勤组名称
    val groupName: String
)