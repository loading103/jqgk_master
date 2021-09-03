package com.daqsoft.module_mine.repository.pojo.vo

/**
 * @package name：com.daqsoft.module_mine.repository.pojo.vo
 * @date 17/5/2021 上午 9:46
 * @author zp
 * @describe
 */

data class Company(
    val tempLicense : String,
    val companyList : List<CompanyInfo>
)

data class CompanyInfo(
    // 开通帐号数
    val accountNum: Int,
    // 是否认证 0未认证 1:已认证
    val certified: Int,
    // 公司地址
    val companyAddress: String,
    // 公司名称
    val companyName: String,
    // 创建时间
    val createTime: String,
    // 是否删除 0:未删除 1:已删除
    val deleted: Int,
    // 注销时间
    val deregisterTime: Any,
    // 停用时间
    val disabledTime: String,
    // 是否永久有效 0:否 1:是
    val foreverValid: Int,
    // 景区用户所属集团的集团ID(可以为null)
    val groupId: Int,
    // 集团版套餐ID
    val groupSetId: Int,
    // 集团版套餐名称
    val groupSetName: String,
    // 公司id
    val id: Int,
    // 默认站点标志 0:非默认 1:默认
    val isDefault: Int,
    // 公司logo
    val logo: String,
    // 支付金额: 元
    val payAmount: Int,
    // 支付方式 1:线下转账 2:线上支付
    val payType: Int,
    // 商品名称
    val productName: String,
    // 公司省市区
    val region: String,
    // 备注
    val remarks: String,
    // 景区名称
    val scenicName: String,
    // 景区版套餐ID
    val scenicSetId: Int,
    // 景区版套餐名称
    val scenicSetName: String,
    // 服务周期,数量
    val serviceCycle: Int,
    // 服务周期单位 1:小时 2:天 3:月 4:年
    val serviceUnit: Int,
    // 来源 1:大旗云 2:云平台 3:集团用户添加
    val source: Int,
    // 状态 0:停用 1:启用 2:注销
    val status: Int,
    val vcode: String,
    // 版本号
    val version: Int,
    // 版本类型 1:集团版 2:景区版
    val versionType: Int
)
