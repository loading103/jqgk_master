package com.daqsoft.library_common.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @package name：com.daqsoft.module_work.repository.pojo.vo
 * @date 18/5/2021 下午 4:08
 * @author zp
 * @describe
 */

@Parcelize
data class Employee(
    // 员工生日 eg:10-10
    val birthday: String,
    // 部门名称
    val depName: String,
    // 学历小学 EducationEnums
    val education: Int,
    // 邮箱地址
    val email: String,
    // name首字母
    val firstWord: String,
    // 性别 0:女 1:男 2:保密
    val gender: Int,
    // 毕业时间
    val graduatedDate: String,
    // 毕业院校
    val graduatedSchool: String,
    val id: Int,
    // 员工图片
    val img: String,
    // 工号
    val jobNo: String,
    // 专业
    val major: String,
    // 手机号
    val mobile: String,
    // 员工姓名
    val name: String,
    // 籍贯
    val nativePlace: String,
    // 岗位名称
    val postName: String,
    // 座机-区号
    val telArea: String,
    // 座机-分机
    val telExtension: String,
    // 座机-号码
    val telNumber: String,
    // 微信帐号
    val wechat: String,

    val appSwitch : Int?
): Parcelable{

    fun coverGender():String{
        return when (gender){
            0 ->"女"
            1 -> "男"
            2 -> "保密"
            else -> "其他"
        }
    }



    fun coverEducation():String{
        return when(education){
            1 -> "小学"
            2 -> "初中"
            3 -> "中专"
            4 -> "高中"
            5 -> "大专"
            6 -> "本科"
            7 -> "硕士研究生"
            8 -> "博士研究生"
            else -> ""
        }
    }


    fun coverLandline():String{
        return if (!telArea.isNullOrBlank() && !telNumber.isNullOrBlank() && !telExtension.isNullOrBlank()){
            "$telArea-$telNumber-$telExtension"
        }else{
            ""
        }
    }
}
