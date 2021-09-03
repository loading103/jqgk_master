package com.daqsoft.module_work.repository.pojo.vo

/**
 * @package name：com.daqsoft.module_work.repository.pojo.vo
 * @date 12/7/2021 上午 10:59
 * @author zp
 * @describe
 */
data class ApproveDetail(
    val title:String,
    val staff:List<Pair<String,String>>,
    val remark:String

) {
}