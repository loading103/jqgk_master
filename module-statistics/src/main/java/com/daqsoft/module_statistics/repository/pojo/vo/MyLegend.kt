package com.daqsoft.module_statistics.repository.pojo.vo

import android.graphics.Color

/**
 * @package name：com.daqsoft.module_statistics.repository.pojo.vo
 * @date 16/6/2021 上午 9:47
 * @author zp
 * @describe
 */
data class MyLegend(
    val label : String = "",
    val value : String = "",
    val color : Int = Color.BLACK,
    val unit : String = ""
)