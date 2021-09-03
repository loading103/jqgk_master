package com.daqsoft.module_statistics.repository.pojo.vo

/**
 * @package name：com.daqsoft.module_statistics.repository.pojo.vo
 * @date 25/6/2021 下午 5:32
 * @author zp
 * @describe
 */
data class ScenicSpotComfort(
    // 景区舒适度
    val comfort: List<Comfort>,
    // 瞬时承载量
    val instLoad: InstLoad,
    // 停车位
    val parking: Parking
)

data class Comfort(
    // 此区域的色值
    val colorValue: String,
    // 是否是当前的舒适度
    val curr: Boolean,
    // 舒适度标签
    val label: String,
    // 区间最大值
    val max: Int,
    // 区间最小值
    val min: Int
)

data class InstLoad(
    // 当前瞬时承载
    val currLoad: Int,
    // 最大瞬时承载
    val maxLoad: Int,
    // 百分比数值(不含百分号)
    val percent: Float
)

data class Parking(
    // 停车位使用数
    val currNum: Int,
    // 百分比数值(不含百分号)
    val percent: Float,
    // 停车位总数
    val total: Int
)