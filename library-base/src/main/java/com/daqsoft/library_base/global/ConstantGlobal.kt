package com.daqsoft.library_base.global

/**
 * @package name：com.daqsoft.library_base.global
 * @date 30/11/2020 下午 4:13
 * @author zp
 * @describe
 */

object ConstantGlobal {
    const val INITIAL_PAGE = 1
    const val INITIAL_PAGE_SIZE = 10
    const val PREFETCH_DISTANCE = 3
    const val FILE_PROVIDER = ".fileProvider"


    const val TITLE = "title"
    const val ITEM = "item"

    const val DEPT = "dept"
    const val EMPLOYEE = "employee"

    const val GRID = "grid"
    const val LINE = "line"



    // 位置不可更改
    val ALARM_TYPE = arrayListOf("一键报警","巡更上报","越界告警","客流监测")
    val EVENT_STATUS = arrayListOf("未处理","处理中","待确认","已办结","重新办理","无效报警","系统解除")
}

