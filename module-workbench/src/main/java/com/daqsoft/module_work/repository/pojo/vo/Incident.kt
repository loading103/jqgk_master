package com.daqsoft.module_work.repository.pojo.vo

import com.daqsoft.module_work.repository.pojo.dto.Annex

/**
 * @package name：com.daqsoft.module_work.repository.pojo.vo
 * @date 23/7/2021 下午 5:13
 * @author zp
 * @describe
 */

data class Incident(
    // 事件地址
    val addr: String,
    // 上传的音频地址
    val audios: List<Annex>,
    // 事件内容
    val content: String,
    // 告警时间
    val createTime: String,
    // 告警类型,巡更上报(2)
    val firstType: Int,
    // 事件ID
    val id: Int,
    // 上传的图片地址
    val imgs: List<Annex>,
    // 上报人员工ID
    val reporterId: Int,
    // 关联的景点ID
    val spotId: Int,
    // 景点名称
    val spotName: String,
    // 事件状态  未处理(1),处理中(2),待确认(3),已办结(4),重新办理(5),无效报警(6),系统解除(7)
    val status: Int,
    // 事件状态翻译
    val statusStr: String,
    // 事件类型ID
    val type: Int,
    // 事件类型名称
    val typeName: String,
    // 上传的视频地址
    val videos: List<Annex>
)
