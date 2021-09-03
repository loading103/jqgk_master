package com.daqsoft.module_work.repository.pojo.vo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @package name：com.daqsoft.module_work.repository.pojo.vo
 * @date 21/7/2021 下午 5:10
 * @author zp
 * @describe
 */
@Parcelize
data class VideoSurveillanceList(
    // 1-主码流，0-次码流
    val bitstream: Int,
    // 设备品牌
    val brand: String,
    // 设备编码
    val deviceCode: String,
    // 设备ip
    val deviceIp: String,
    // 设备型号
    val deviceModel: String,
    // 设备类型
    val deviceType: Int,
    // 设备用途(多选)
    val deviceUse: List<String>,
    // 外网ip
    val extranetIp: String,
    // 外网端口
    val extranetPort: Int,
    // 设备分组（多选）
    val groupId: List<String>,
    // 设备分组名
    val groupName: String,
    // 海拔高度
    val height: Int,
    val id: Int,
    // 设备图片地址
    val imgUrl: String,
    // 内网ip
    val intranetIp: String,
    // 内网端口
    val intranetPort: Int,
    // 纬度
    val latitude: String,
    // 经度
    val longitude: String,
    // 流媒体通道号
    val mediaChannel: String,
    // 流媒体端口
    val mediaPort: Int,
    // 流媒体地址
    val mediaUrl: String,
    // 设备名称
    val name: String,
    // 1-在线，0-不在线
    val online: Int,
    // 软件平台Id
    val platformId: Int,
    // 备注信息
    val remark: String,
    // 关联景点id
    val spotId: Int,
    // 1-显示，0-不显示
    val terminalShow: Int,
    // 景区编码
    val vcode: String
): Parcelable