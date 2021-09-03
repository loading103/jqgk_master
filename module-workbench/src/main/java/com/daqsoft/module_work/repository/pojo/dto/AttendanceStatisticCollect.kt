package com.daqsoft.module_work.repository.pojo.dto

import android.graphics.Color

/**
 * @package name：com.daqsoft.module_work.repository.pojo.dto
 * @date 2/7/2021 下午 3:29
 * @author zp
 * @describe
 */
class AttendanceStatisticCollect(
    val title:String,
    val content:String,
    val contentColor : Int,
    val dotColor : Int,
    val detail:List<String>,
    val expand:List<String>?=null
)
