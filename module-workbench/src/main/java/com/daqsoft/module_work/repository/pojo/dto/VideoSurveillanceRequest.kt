package com.daqsoft.module_work.repository.pojo.dto

import com.daqsoft.library_base.global.ConstantGlobal

/**
 * @package name：com.daqsoft.module_work.repository.pojo.dto
 * @date 21/7/2021 下午 5:13
 * @author zp
 * @describe
 */
data class VideoSurveillanceRequest(
    // 设备类型
    var deviceType : Int ? = null,
    // 分组Id
    var groupId : String ? = null,
    // 设备名称/IP
    var name : String ? = null,
    // 状态：1-在线，0-离线
    var online : Int ? = null,
    // 页码
    var currPage: Int = ConstantGlobal.INITIAL_PAGE,
    // 每页显示数
    var pageSize: Int = ConstantGlobal.INITIAL_PAGE_SIZE,
)
