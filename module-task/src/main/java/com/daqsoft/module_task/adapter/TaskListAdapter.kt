package com.daqsoft.module_task.adapter

import android.graphics.Rect
import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.GsonUtils
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.library_common.bean.AppMenu
import com.daqsoft.module_task.R
import com.daqsoft.module_task.databinding.RecycleviewTaskListItemBinding
import com.daqsoft.module_task.repository.pojo.vo.Task
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.utils.dp
import com.google.gson.reflect.TypeToken
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.module_task.adapter
 * @date 2/6/2021 上午 9:47
 * @author zp
 * @describe
 */
class TaskListAdapter : BindingRecyclerViewAdapter<Task>() {

    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: Task
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)
        val itemBinding = binding as RecycleviewTaskListItemBinding

        itemBinding.title.text = item.title

        itemBinding.label.text = item.businessTypeText


        if (item.description.isJsonNull()){
            binding.depict.isVisible = false
        }else{
            val data = item.description.asJsonObject.entrySet().map{
                "${it.key}：${it.value.toString().replace("\"","")}"
            }

            binding.depict.isVisible = true
            binding.depict.apply {
                layoutManager = LinearLayoutManager(this.context)
                if (itemDecorationCount == 0){
                    addItemDecoration(object : RecyclerView.ItemDecoration() {
                        override fun getItemOffsets(
                            outRect: Rect,
                            view: View,
                            parent: RecyclerView,
                            state: RecyclerView.State
                        ) {
                            if (parent.getChildAdapterPosition(view) != 0){
                                outRect.top = 10.dp
                            }
                        }
                    })
                }
                adapter = TaskListDepictAdapter().apply {
                    this@apply.itemBinding = ItemBinding.of(ItemBinding.VAR_NONE,R.layout.recycleview_task_list_item_depict_item)
                    this@apply.setItems(data)
                }
                setOnTouchListener { view, motionEvent ->
                    itemBinding.root.onTouchEvent(motionEvent)
                }
            }
        }


        when(item.businessType){
            1 ->{
                alarm(itemBinding,item)
            }
        }

    }


    /**
     * -告警任务
     */
    private fun alarm(binding: RecycleviewTaskListItemBinding,item: Task){
        binding.root.setOnClickListenerThrottleFirst {
            ARouter
                .getInstance()
                .build(ARouterPath.Workbench.PAGER_ALARM_DETAILS)
                .withString("from", ARouterPath.Task.PAGER_TASK_LIST)
                .withString("id", item.dataId.toString())
                .withInt("state",2)
                .navigation()
        }

        var statusText = ""
        var statusColor = R.color.color_ff9d46
        when(item.status){
            1 -> {
                statusText = "未处理"
                statusColor = R.color.color_ff9d46
            }
            2 ->{
                statusText = "处理中"
                statusColor = R.color.color_ff5757
            }
            3 -> {
                statusText = "待确认"
                statusColor = R.color.color_1ccb85
            }
            4 -> {
                statusText = "已办结"
                statusColor = R.color.color_dad7d4
            }
            5 -> {
                statusText = "重新办理"
                statusColor = R.color.color_59abff
            }
            6 -> {
                statusText =  "无效报警"
                statusColor = R.color.color_ef9dee
            }
            7 -> {
                statusText ="系统解除"
                statusColor = R.color.color_32d6d4
            }
            8 -> {
                statusText ="退回"
                statusColor = R.color.color_ef9dee
            }
            9 -> {
                statusText ="待确认"
                statusColor = R.color.color_59abff
            }
        }
        binding.state.text = item.statusText
        binding.state.helper.backgroundColorNormal = binding.state.context.resources.getColor(statusColor)
    }
}