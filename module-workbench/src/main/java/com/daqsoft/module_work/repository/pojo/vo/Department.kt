package com.daqsoft.module_work.repository.pojo.vo

import android.os.Parcelable
import com.daqsoft.library_common.bean.Employee
import kotlinx.android.parcel.Parcelize

/**
 * @package name：com.daqsoft.module_work.repository.pojo.vo
 * @date 18/5/2021 下午 4:04
 * @author zp
 * @describe
 */

@Parcelize
data class Department(
    // 下级部门
    val children: List<Department>,
    // 部门名称
    val depName: String,
    // 部门人员
    val employee: List<Employee>,
    val id: Int,
    // 父节点Id
    val parentId: Int,
    // 顺序
    val sort: Int
) : Parcelable
