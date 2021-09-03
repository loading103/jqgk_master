package com.daqsoft.module_work.repository.pojo.dto

/**
 * @package name：com.daqsoft.module_work.repository.pojo.dto
 * @date 22/7/2021 下午 5:47
 * @author zp
 * @describe
 */
data class IncidentReportRequest(
    // 事件地址
    var addr: String?=null,
    // 事件类型ID
    var type: Int?=null,
    // 事件类型名称
    var typeName: String?=null,
    // 关联的景点ID
    var spotId: Int?=null,
    // 景点名称
    var spotName: String?=null,
    // 事件内容
    var content: String?=null,
    // 上传的图片地址
    var imgs: List<Annex>?=null,
    // 上传的视频地址
    var videos: List<Annex>?=null,
    // 上传的音频地址
    var audios: List<Annex>?=null,
    // 事件源：纬度
    var lat : String,
    // 事件源：经度
    var lng : String,
)

data class Annex(
    // 用途:比如音频的时间长度
    val time: String,
    // 1:图片 2:视频 3:附件 4:音频
    val type: Int,
    // 文件地址
    val url: String
)
