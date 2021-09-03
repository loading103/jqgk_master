package com.daqsoft.module_task.repository.pojo.vo

import android.util.JsonReader
import com.google.gson.JsonObject

/**
 * @package name：com.daqsoft.module_task.repository.pojo.vo
 * @date 1/6/2021 下午 5:17
 * @author zp
 * @describe
 */
data class Task(
    // 业务类型,1-告警任务
    val businessType: Int,
    // 创建时间
    val createTime: String,
    // 对于任务数据的id
    val dataId: Int,
    // 创建时间的日期
    val day: Int,
    // 任务描述，是一个json
    val description: JsonObject,

    val id: Int,
    // 创建时间的月份
    val month: Int,
    // 状态，根据业务类型存储对应的任务的状态
    val status: Int,
    // 状态对应的文本描述
    val statusText: String,
    // 任务类型：为业务类型的子类型，例如告警任务中的事件上报，一键报警，巡更上报等
    val taskType: Int,
    // 任务类型文本描述
    val taskTypeText: String,
    // 任务标题
    val title: String,
    val vcode: String,
    // 创建时间的这一年中的星期数
    val weekOfYear: Int,
    // 创建时间的年份
    val year: Int,
    val businessTypeText : String
)
