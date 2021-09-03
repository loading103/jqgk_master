package com.daqsoft.module_work.repository.pojo.vo

import com.daqsoft.module_work.R
import com.daqsoft.module_work.widget.tree.LayoutItemType

/**
 * @package name：com.daqsoft.module_work.repository.pojo.vo
 * @date 21/7/2021 上午 11:52
 * @author zp
 * @describe
 */
data class VideoSurveillanceCount(
    // 异常数
    val exceptionNum: Int,
    // 正常数
    val normalNum: Int,
    // 总数
    val total: Int
)

