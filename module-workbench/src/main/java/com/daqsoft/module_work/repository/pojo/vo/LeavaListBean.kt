package com.daqsoft.module_work.repository.pojo.vo



data class LeavaListBean(
    val url: String,
    val name: String,
    val content: String,
    val state: String,
    val id: String,
    val longtime: String?,
    val time: String?
){
    fun getLongTime():String{
        if(longtime.isNullOrBlank()){
            return " "
        }
        return "请假时长: ${longtime}天"
    }
    fun getCreatTime():String{
        if(time.isNullOrBlank()){
            return " "
        }
        return "提交时间: ${time}"
    }

}
