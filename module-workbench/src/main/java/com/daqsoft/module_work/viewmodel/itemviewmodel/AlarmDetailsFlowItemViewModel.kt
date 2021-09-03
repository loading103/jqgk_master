package com.daqsoft.module_work.viewmodel.itemviewmodel

import android.view.View
import androidx.databinding.ObservableField
import com.daqsoft.module_work.R
import com.daqsoft.module_work.repository.pojo.vo.HandleFlow
import com.daqsoft.module_work.viewmodel.AlarmDetailsViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel

/**
 * @package name：com.daqsoft.module_task.viewmodel
 * @date 13/5/2021 上午 10:56
 * @author zp
 * @describe 任务详情 流程 itemViewModel
 */
class AlarmDetailsFlowItemViewModel(
    private val alarmDetailsViewModel: AlarmDetailsViewModel,
    val handleFlow : HandleFlow,
    val first : Boolean = false,
    val last : Boolean = false
) : ItemViewModel<AlarmDetailsViewModel>(alarmDetailsViewModel){

    val bgObservable = ObservableField<Int>()
    val iconObservable = ObservableField<Int>()

    val lastObservable = ObservableField<Boolean>(false)

    val handleFlowObservable = ObservableField<HandleFlow>()

    val contentObservable = ObservableField<String>()

    val remarkObservable = ObservableField<String>()

    init {

        handleFlowObservable.set(handleFlow)

        var content = ""
        var remark = ""

        if (first){
            bgObservable.set(R.color.color_59abff)
            iconObservable.set(R.mipmap.renwu_shijian)
            content = when(alarmDetailsViewModel.detailField.get()!!.type){
                1 -> "系统自动告警"
                2 -> "${handleFlow.inputName}  巡更事件上报"
                3 -> "系统自动告警"
                4 -> "系统自动告警"
                else -> "系统自动告警"
            }
            remark = ""
        }else{
            bgObservable.set(R.color.color_14c881)
            iconObservable.set(R.mipmap.renwu_liucheng)

            when(handleFlow.method){
                1 -> {
                    content = "${handleFlow.inputName}  指派给  ${handleFlow.receiverName}"
                    remark = "备注："
                }
                2 -> {
                    content = "${handleFlow.inputName}  办理"
                    remark = "办理结果："
                }
                3 -> {
                    content = "已办结"
                    remark = ""
                }
                4 -> {
                    content = "确认办结"
                    remark = ""
                }
                5 -> {
                    content = "${handleFlow.inputName}  打回给  ${handleFlow.receiverName}"
                    remark = "处理结果："
                }
                6 -> {
                    content = "${handleFlow.inputName}  办理"
                    remark = "办理结果："
                }
                7 -> {
                    content = "${handleFlow.inputName}  办结"
                    remark = "处理结果："
                }
                8 -> {
                    content = "${handleFlow.inputName}  退回"
                    remark = "退回原由："
                }
                9 -> {
                    content = "${handleFlow.inputName}  上报领导"
                    remark = "上报原由："
                }
                10 -> {
                    content = "${handleFlow.inputName}  办结"
                    remark = "备注："
                }
                -1 -> {
                    content = "${handleFlow.inputName}"
                    remark = ""
                }
                else ->{

                }
            }
        }

        lastObservable.set(last)

        contentObservable.set(content)
        if (!handleFlow.detail.isNullOrBlank()){
            remarkObservable.set("${remark}${handleFlow.detail}")
        }
    }

}
