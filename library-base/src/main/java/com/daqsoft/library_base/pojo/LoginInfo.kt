package com.daqsoft.library_base.pojo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @package name：com.daqsoft.module_mine.repository.pojo.vo
 * @date 17/5/2021 下午 5:06
 * @author zp
 * @describe
 */

data class LoginInfo(
    val bearer: String,
    val header: String,
    var profile: Profile,
    val token: Token,
    val gpsTimeCycle : Int,
    val gpsSwitch : Boolean
)


data class Profile(
    // 登录账号
    val account: Account,
    // 公司配置
    val config: Config,
    // 员工信息
    val employee: EmployeeSimple,
    // 登录公司
    val umsCompany: UmsCompany,
    // 岗位信息
    val post : List<Post>,

    val department : List<Dept>

)


data class Token(
    val exp: String,
    val iat: String,
    val iss: String,
    val jti: String,
    val nbf: String,
    val token: String
)


data class Account(
    // 超管登陆帐号-大旗云帐号
    val adminAccount: String,
    // 联系人
    val concatName: String,
    // 邮箱
    val email: String,
    val id: Int,
    // 手机号
    val mobile: String,
    // 用户来源 1:大旗云 2:云平台 3:集团用户添加
    val source: Int,
    // 岗位
    val station: String,
    // 用户编码,来源于大旗云
    val userSn: String
)


data class Config(
    // OSS上传key
    val ossKey: String
)


data class EmployeeSimple(
    // 人员id
    val id: Int,
    // 人员姓名
    val name: String,
    // 员工图片（头像）
    val img: String,
)


data class UmsCompany(
    val companyAddress: String,
    val companyName: String,
    val groupSetName: String,
    val id: Int,
    val logo: String,
    val region: String,
    val scenicName: String,
    val scenicSetName: String,
    val status: Int,
    val vcode: String,
    val versionType: Int
)

data class Post(
    // 所属公司/景区
    val companyName: String,
    // 创建时间
    val createTime: String,
    // 岗位描述
    val description: String,
    // 是否开启APP上传GPS定位数据功能 1:开启 0:关闭
    val gps: Int,
    // 岗位id
    val id: Int,
    // 成员数量
    val members: Int,
    // 更新时间
    val modifyTime: String,
    // 岗位名称
    val name: String,
    // 是否推送到指挥调度大屏 0:否 1:是
    val pushDispatch: Int
)


data class Dept(
    val chargePerson: List<Any>,
    val companyId: Int,
    val createTime: String,
    val depName: String,
    val description: String,
    val id: Int,
    val level: Int,
    val parentId: Int,
    val personIds: List<Any>,
    val sort: Int,
    val state: Int
)

@Parcelize
data class UpdateInfo(
    /**
     * 更新内容
     */
    val AppUpdateInfo: String,
    /**
     * 下载地址
     */
    val DownPath: String,
    val IsUpdate: Int,
    val UpdateTime: String,
    val VersionCode: String
) : Parcelable {
}
