package com.daqsoft.module_statistics.repository.pojo.vo

import com.google.gson.JsonObject
import org.json.JSONObject

/**
 * @package name：com.daqsoft.module_statistics.repository.pojo.vo
 * @date 28/6/2021 下午 5:13
 * @author zp
 * @describe
 */
data class VehicleSource(
    // 来源地名称
    val addr: String,
    // 车辆数
    val count: Int,
    // 环比趋势: 0 上升,1 下降,-1 持平
    val cs: Int,
    // 环比车辆
    val cycle: String,
    // 环比数
    val cycleCount: Float,
    // 同比车辆
    val line: String,
    // 同比数
    val lineCount: Float,
    // 同比比趋势: 0 上升,1 下降,-1 持平
    val ls: Int,
    // 时间
    val time: String
)