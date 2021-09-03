package com.daqsoft.module_work.viewmodel.itemviewmodel

import androidx.databinding.ObservableField
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.GsonUtils
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_common.bean.AppMenu
import com.daqsoft.module_work.R
import com.daqsoft.module_work.viewmodel.WorkViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.utils.ToastUtils

/**
 * @package name：com.daqsoft.module_workbench.viewmodel
 * @date 25/11/2020 下午 2:08
 * @author zp
 * @describe 工作台 item label  ViewModel
 */
class WorkBenchItemLabelViewModel (
    private val workBenchViewModel: WorkViewModel,
    val menu: AppMenu
) : MultiItemViewModel<WorkViewModel>(workBenchViewModel){

    val menuInfoObservable = ObservableField<AppMenu>()


    val placeholderRes = ObservableField<Int>()

    init {
        menuInfoObservable.set(menu)

        placeholderRes.set(when(menu.number){
            // 考勤
            "ATTENDANCE" ->{
                R.mipmap.home_icon_kaoqing
            }
            // 今日工作
            "TODAY_WORK"->{
                R.mipmap.home_icon_gongzuo
            }
            // 工作日历
            "WORK_CALENDAR" ->{
                R.mipmap.home_icon_rili
            }
            // 待办事项
            "TO_DO_TASK" ->{
                R.mipmap.home_icon_daiban
            }
            // 会议通知
            "MEETING_NOTIFY" ->{
                R.mipmap.home_icon_tongzhi
            }
            // 通讯录
            "ADDRESS_BOOK" ->{
                R.mipmap.home_icon_tongxunlu
            }
            // 请假
            "LEAVE" ->{
                R.mipmap.home_icon_qingjia
            }
            // 补卡申请
            "CLOCK_APPLY"->{
                R.mipmap.home_icon_buka
            }

            // 电子巡更
            "PATROL" ->{
                R.mipmap.home_icon_xungeng
            }
            // 游客投诉
            "COMPLAINT" ->{
                R.mipmap.home_icon_tousu
            }
            // 网评管理
            "COMMENT" ->{
                R.mipmap.home_icon_wangping
            }
            // 环保监测
            "ENVIRONMENTAL_PROTECTION" ->{
                R.mipmap.home_icon_huanbaojiance
            }
            // 审批管理
            "AUDIT" ->{
                R.mipmap.home_icon_shengpi
            }
            // 上报事件
            "EVENT_PUBLISH" ->{
                R.mipmap.home_icon_shangbao
            }
            // 信息发布
            "MSG_PUBLISH" ->{
                R.mipmap.home_icon_fabu
            }

            // 监测预警
            "MONITOR_WARNING" ->{
                R.mipmap.home_icon_yujing
            }

            // 越界预警
            "OUT_OF_BOUNDS" ->{
                R.mipmap.home_icon_empty
            }
            // 手环报警
            "BRACELET_ALARM" ->{
                R.mipmap.home_icon_empty
            }
            // 火灾报警
            "FIRE_ALARM" ->{
                R.mipmap.home_icon_empty
            }
            // 环境报警
            "ENVIRONMENTAL_ALARM" ->{
                R.mipmap.home_icon_empty
            }
            // 景区监控
            "SCENIC_MONITOR" ->{
                R.mipmap.home_icon_jiankong
            }
            else ->{
                R.mipmap.home_icon_empty
            }
        })
    }

    /**
     * item 点击事件
     */
    val itemOnClick = BindingCommand<Unit>(BindingAction {

        when(menu.number){
            // 考勤
            "ATTENDANCE" ->{
                ARouter.getInstance().build(ARouterPath.Workbench.PAGER_ATTENDANCE).navigation()
            }
            // 今日工作
            "TODAY_WORK"->{
                ToastUtils.showShortSafe("功能开发中，敬请期待！")
            }
            // 工作日历
            "WORK_CALENDAR" ->{
                ToastUtils.showShortSafe("功能开发中，敬请期待！")
            }
            // 待办事项
            "TO_DO_TASK" ->{
                ToastUtils.showShortSafe("功能开发中，敬请期待！")
            }
            // 会议通知
            "MEETING_NOTIFY" ->{
                ToastUtils.showShortSafe("功能开发中，敬请期待！")
            }
            // 通讯录
            "ADDRESS_BOOK" ->{
                ARouter.getInstance().build(ARouterPath.Workbench.PAGER_ADDRESS_BOOK).navigation()
            }
            // 请假
            "LEAVE" ->{
                ARouter.getInstance().build(ARouterPath.Workbench.PAGER_LEAVE_LIST).navigation()
            }
            // 补卡申请
            "CLOCK_APPLY"->{
                ARouter.getInstance().build(ARouterPath.Workbench.PAGER_SUPPLEMENT_CARD_LIST).navigation()
            }

            // 电子巡更
            "PATROL" ->{
                ToastUtils.showShortSafe("功能开发中，敬请期待！")
            }
            // 游客投诉
            "COMPLAINT" ->{
                ToastUtils.showShortSafe("功能开发中，敬请期待！")
            }
            // 网评管理
            "COMMENT" ->{
                ToastUtils.showShortSafe("功能开发中，敬请期待！")
            }
            // 环保监测
            "ENVIRONMENTAL_PROTECTION" ->{
                ToastUtils.showShortSafe("功能开发中，敬请期待！")
            }
            // 审批管理
            "AUDIT" ->{
                ARouter.getInstance().build(ARouterPath.Workbench.PAGER_APPROVAL_MANAGEMENT).navigation()
            }
            // 上报事件
            "EVENT_PUBLISH" ->{
                ARouter.getInstance().build(ARouterPath.Workbench.PAGER_INCIDENT_REPORT).navigation()
            }
            // 信息发布
            "MSG_PUBLISH" ->{
                ToastUtils.showShortSafe("功能开发中，敬请期待！")
            }

            // 监测预警
            "MONITOR_WARNING" ->{
                ARouter
                    .getInstance()
                    .build(ARouterPath.Workbench.PAGER_MONITOR_FORECAST)
                    .withString("menu",GsonUtils.toJson(menu.children?: arrayListOf<AppMenu>()))
                    .navigation()
            }

            // 越界预警
            "OUT_OF_BOUNDS" ->{
                ToastUtils.showShortSafe("功能开发中，敬请期待！")
            }
            // 手环报警
            "BRACELET_ALARM" ->{
                ToastUtils.showShortSafe("功能开发中，敬请期待！")
            }
            // 火灾报警
            "FIRE_ALARM" ->{
                ToastUtils.showShortSafe("功能开发中，敬请期待！")
            }
            // 环境报警
            "ENVIRONMENTAL_ALARM" ->{
                ToastUtils.showShortSafe("功能开发中，敬请期待！")
            }
            // 景区监控
            "SCENIC_MONITOR" ->{
                ARouter.getInstance().build(ARouterPath.Workbench.PAGER_VIDEO_SURVEILLANCE_LIST).navigation()
            }
            else ->{
                ToastUtils.showShortSafe("功能开发中，敬请期待！")
            }
        }
    })

}
