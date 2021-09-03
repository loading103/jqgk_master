package com.daqsoft.module_work.repository.pojo.vo

import com.daqsoft.library_common.bean.Dict

/**
 * @package name：com.daqsoft.module_work.repository.pojo.vo
 * @date 3/6/2021 上午 9:21
 * @author zp
 * @describe
 */
data class AlarmTypeAlarmStatus(
    val status: List<Dict>,
    val type: List<Dict>
)


