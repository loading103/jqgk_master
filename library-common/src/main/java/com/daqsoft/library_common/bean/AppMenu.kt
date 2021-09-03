package com.daqsoft.library_common.bean

/**
 * @package name：com.daqsoft.module_main.repository.pojo.vo
 * @date 17/5/2021 下午 5:49
 * @author zp
 * @describe
 */

data class AppMenu(
    // 下级菜单
    val children: List<AppMenu>?,
    // 自定义菜单id
    val cmId: Int?,
    // 图标
    val icon: String?,
    // 菜单名称
    val label: String?,
    // 菜单id
    val mId: Int?,
    // 菜单编码
    val number: String?,
    // 上级菜单
    val parentId: Int?,
    // 路由
    val route: String?,
    // 是否为选中状态: 主要针对APP菜单
    val selected: Boolean?,
    // 选中后的图标
    val selectedIcon: String?,
    // 排序
    val sort: Int?,
    // 菜单类型：1-目录，2-菜单，3-按钮
    val type: Int?
)
