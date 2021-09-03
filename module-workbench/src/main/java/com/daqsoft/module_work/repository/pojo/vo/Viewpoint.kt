package com.daqsoft.module_work.repository.pojo.vo

/**
 * @package name：com.daqsoft.module_work.repository.pojo.vo
 * @date 22/7/2021 下午 5:13
 * @author zp
 * @describe
 */
data class Viewpoint(
    // 拥堵预警阈值
    val blockThreshold: Int,
    // 景点关闭提示信息
    val closeTips: String,
    // 创建者
    val createId: String,
    // 创建时间
    val createTime: String,
    // 前端显示状态
    val display: Boolean,
    // 电子围栏点位
    val electronicFence: String,
    // ID
    val id: Int,
    // 景点简介
    val introduction: String,
    // 纬度
    val lat: String,
    // 经度
    val lng: String,
    // 负责人电话
    val mainMobile: String,
    // 负责人
    val mainName: String,
    // 最大承载量
    val maxTourist: Int,
    // 开放时段
    val openPeriod: String,
    // 景点类型 1:封闭的 2:开放
    val pointType: Int,
    // 景点名称
    val spotName: String,
    // 景点开启状态
    val state: Boolean,
    // 修改人
    val updateBy: String,
    val vcode: String
)