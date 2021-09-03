package com.daqsoft.module_statistics.repository.pojo.vo

/**
 * @package name：com.daqsoft.module_statistics.repository.pojo.vo
 * @date 24/6/2021 下午 4:29
 * @author zp
 * @describe
 */
data class PassengerFlowPortrait(
    // 年龄占比
    val ageRates: List<AgeRate>,
    // 总览-所有游客来源集合
    val allSources: List<AllSource>,
    // 女游客数
    val femaleNum: Int,
    // 女性占比
    val femaleRate: Float,
    // 省内游客来源集合
    val inProvinceSources: List<InProvinceSource>,
    // 男游客数
    val maleNum: Int,
    // 男性占比
    val maleRate: Float,
    // 省外游客来源集合
    val outProvinceSources: List<OutProvinceSource>,
    // 总游客数
    val totalNum: Int,
    // 类型占比
    val typeRates: List<TypeRate>
)


data class AgeRate(
    // 年龄描述
    val name: String,
    // 年龄数量
    val num: Int,
    // 年龄占比
    val rate: Float
)

data class AllSource(
    // 游客数量
    val num: Int,
    // 游客占比
    val rate: Float,
    // 来源地名称
    val sourceName: String
)

data class InProvinceSource(
    // 游客数量
    val num: Int,
    // 游客占比
    val rate: Float,
    // 来源地名称
    val sourceName: String
)

data class OutProvinceSource(
    // 游客数量
    val num: Int,
    // 游客占比
    val rate: Float,
    // 来源地名称
    val sourceName: String
)

data class TypeRate(
    // 游客类型
    val name: String,
    // 游客数量
    val num: Int,
    // 游客占比
    val rate: Float
)