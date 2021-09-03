package com.daqsoft.library_common.bean

import android.os.Parcelable
import com.daqsoft.library_common.bean.Employee
import kotlinx.android.parcel.Parcelize

/**
 * @package name：com.daqsoft.module_work.repository.pojo.vo
 * @date 18/5/2021 下午 4:07
 * @author zp
 * @describe
 */
@Parcelize
data class AddressBook(
    val employee: List<Employee>,
    // 姓名首字母
    val firstWord: String
) : Parcelable
