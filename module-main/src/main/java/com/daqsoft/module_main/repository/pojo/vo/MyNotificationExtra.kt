package com.daqsoft.module_main.repository.pojo.vo

import android.os.Parcelable
import androidx.versionedparcelable.ParcelField
import kotlinx.android.parcel.Parcelize

/**
 * @package name：com.daqsoft.module_main.repository.pojo.vo
 * @date 7/1/2021 下午 3:35
 * @author zp
 * @describe
 */
@Parcelize
data class MyNotificationExtra(
    // 业务类型 1：告警任务
    val businessType: String,
    // 数据id
    val dataId: String
) : Parcelable {
}
