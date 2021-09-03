package com.daqsoft.module_task.adapter

import androidx.databinding.ViewDataBinding
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.TimeUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.daqsoft.library_base.global.MMKVGlobal
import com.daqsoft.library_base.pojo.LoginInfo
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.MMKVUtils
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.module_task.R
import com.daqsoft.module_task.databinding.RecycleviewTaskListItemDepictItemBinding
import com.daqsoft.module_task.databinding.RecycleviewWeeklySummaryListItemBinding
import com.daqsoft.module_task.repository.pojo.vo.WeeklySummary
import kotlinx.android.synthetic.main.recycleview_weekly_summary_list_item.view.*
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter

/**
 * @package name：com.daqsoft.module_task.adapter
 * @date 3/6/2021 上午 10:22
 * @author zp
 * @describe
 */
class WeeklySummaryAdapter  : BindingRecyclerViewAdapter<WeeklySummary>() {

    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: WeeklySummary
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)
        val itemBinding = binding as RecycleviewWeeklySummaryListItemBinding

        itemBinding.root.setOnClickListenerThrottleFirst{
            ARouter
                .getInstance()
                .build(ARouterPath.Task.PAGER_WEEKLY_SUMMARY)
                .withString("id", item.id.toString())
                .navigation()
        }
        val start = TimeUtils.date2String(TimeUtils.string2Date(item.startTime,"yyyy-MM-dd HH:mm:ss"),"MM.dd")
        val end = TimeUtils.date2String(TimeUtils.string2Date(item.endTime,"yyyy-MM-dd HH:mm:ss"),"MM.dd")
        itemBinding.week.text = "一周小结${start}~${end}"
        val total = item.haveReadQuantity+item.processedQuantity
        itemBinding.total.text = "本周处理事项${total}件"
        val ratio = try {
            total / item.receiveQuantity
        }catch (e:Exception){
            0
        }
        itemBinding.proportion.text = "任务完成率${ratio}%"
        itemBinding.progressBar.progress = ratio

        val loginInfoJson = MMKVUtils.decodeString(MMKVGlobal.LOGIN_INFO)
        if (!loginInfoJson.isNullOrBlank()){
            val loginInfo = GsonUtils.fromJson<LoginInfo>(loginInfoJson, LoginInfo::class.java)
            Glide
                .with(binding.avatar.context)
                .load(loginInfo.profile.employee.img)
                .placeholder(R.mipmap.tongxunlv_touxiang)
                .centerInside()
                .into(binding.avatar)
        }

    }
}