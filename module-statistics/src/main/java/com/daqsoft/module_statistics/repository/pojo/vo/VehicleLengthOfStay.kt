package com.daqsoft.module_statistics.repository.pojo.vo

/**
 * @package name：com.daqsoft.module_statistics.repository.pojo.vo
 * @date 29/6/2021 上午 9:51
 * @author zp
 * @describe
 */
data class VehicleLengthOfStay(
    // 环比
    val circle: List<Float>,
    // 当前
    val cur: List<Float>,
    // 扩展属性
    val ext: VehicleLengthOfStayExt,
    // 同比
    val line: List<Float>,
    // 横轴显示
    val x: List<String>
)

class VehicleLengthOfStayExt(
)