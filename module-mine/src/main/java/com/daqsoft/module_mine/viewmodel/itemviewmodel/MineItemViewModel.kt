package com.daqsoft.module_mine.viewmodel.itemviewmodel

import androidx.databinding.ObservableField
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_common.bean.AppMenu
import com.daqsoft.module_mine.R
import com.daqsoft.module_mine.viewmodel.MineViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.utils.ToastUtils

/**
 * @ClassName    MineViewModel
 * @Description
 * @Author       yuxc
 * @CreateDate   2021/5/7
 */
class MineItemViewModel (
    private val mineViewModel : MineViewModel,
    val appMenu: AppMenu
) : MultiItemViewModel<MineViewModel>(mineViewModel){

    val menu = ObservableField<AppMenu>()

    val placeholderRes = ObservableField<Int>()

    init {
        menu.set(appMenu)


        placeholderRes.set(when(appMenu.number){
            // 个人中心
            "PERSONAL_CENTER" ->{
                R.color.color_ececec
            }
            // 消息
            "MSG" ->{
                R.mipmap.mine_xiaoxi
            }
            // 设置
            "SET" ->{
                R.mipmap.mine_shezhi
            }
            // 退出
            "LOGOUT" ->{
                R.mipmap.mine_tuichudenglv
            }
            else -> {
                R.color.color_ececec
            }
        })


    }

    val itemOnClick = BindingCommand<Unit>(BindingAction {
        when(appMenu.number){
            // 个人中心
            "PERSONAL_CENTER" ->{
                ARouter
                    .getInstance()
                    .build(ARouterPath.Mine.PAGER_PERSONAL_INFO)
                    .withString("id",mineViewModel.profile.get()?.employee?.id?.toString())
                    .navigation()
            }
            // 消息
            "MSG" ->{
                ToastUtils.showShortSafe("功能开发中，敬请期待！")
            }
            // 设置
            "SET" ->{
                ARouter
                    .getInstance()
                    .build(ARouterPath.Mine.PAGER_SET_UP)
                    .withString("id",mineViewModel.profile.get()?.employee?.id?.toString())
                    .navigation()
            }


            // 退出
            "LOGOUT" ->{
                mineViewModel.logoutLiveData.call()
            }
        }

    })

}