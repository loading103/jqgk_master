package com.daqsoft.module_work.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_res.BR
import com.daqsoft.module_work.R
import com.daqsoft.module_work.databinding.ActivityAttendanceBinding
import com.daqsoft.library_common.widget.BottomNavigationItem
import com.daqsoft.module_work.viewmodel.AttendanceViewModel
import dagger.hilt.android.AndroidEntryPoint
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectedListener

/**
 * @author zp
 * @package name：com.daqsoft.module_work.activity
 * @date 10/5/2021 下午 4:02
 * @describe 考勤
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_ATTENDANCE)
class AttendanceActivity :  AppBaseActivity<ActivityAttendanceBinding, AttendanceViewModel>() {

    private var bottomNavigation  = arrayListOf<BaseTabItem>()
    private var mFragments: ArrayList<Fragment> = arrayListOf()

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_attendance
    }

    override fun initViewModel(): AttendanceViewModel? {
        return viewModels<AttendanceViewModel>().value
    }

    override fun initView() {
        super.initView()

        initBottomNavigation()
        initFragment()

    }

    /**
     * 初始化页面
     */
    private fun initFragment() {
        if (mFragments.isNotEmpty()){
            return
        }

        val clock = ARouter.getInstance().build(ARouterPath.Workbench.PAGER_CLOCK).navigation() as Fragment
        mFragments.add(clock)

        val scheduling = ARouter.getInstance().build(ARouterPath.Workbench.PAGER_SCHEDULING).navigation() as Fragment
        mFragments.add(scheduling)

        val statistics = ARouter.getInstance().build(ARouterPath.Workbench.PAGER_STATISTICS).navigation() as Fragment
        mFragments.add(statistics)

        // 默认选中第一个
        supportFragmentManager.beginTransaction().apply {
            add(R.id.frame_layout, mFragments[0])
            commit()
        }
        viewModel.setTitleText(bottomNavigation[0].title)
    }

    private fun initBottomNavigation() {

        val clock  = createBottomNavigationItem(
            R.mipmap.kaoqing_icon_daka,
            R.mipmap.kaoqing_icon_daka_hover,
           "打卡"
        )
        bottomNavigation.add(clock)

        val scheduling  = createBottomNavigationItem(
            R.mipmap.kaoqing_icon_paiban,
            R.mipmap.kaoqing_icon_paiban_hover,
            "排班日历"
        )
        bottomNavigation.add(scheduling)

        val statistics  = createBottomNavigationItem(
            R.mipmap.kaoqing_icon_tongji,
            R.mipmap.kaoqing_icon_tongji_hover,
            "统计"
        )
        bottomNavigation.add(statistics)


        binding.pageNavigation
            .custom()
            .addItem(clock)
            .addItem(scheduling)
            .addItem(statistics)
            .build()
            .apply {
                addTabItemSelectedListener(object : OnTabItemSelectedListener {
                    override fun onSelected(index: Int, old: Int) {
                        try {
                            val previousFragment = mFragments[old]
                            val currentFragment = mFragments[index]
                            val transaction = supportFragmentManager.beginTransaction()
                            transaction.hide(previousFragment)
                            if (!currentFragment.isAdded) {
                                transaction
                                    .add(R.id.frame_layout, currentFragment)
                                    .commit()
                            } else {
                                transaction
                                    .show(currentFragment)
                                    .setMaxLifecycle(currentFragment, Lifecycle.State.RESUMED)
                                    .commit()
                            }

                            viewModel.setTitleText(bottomNavigation[index].title)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onRepeat(index: Int) {}
                })
            }
    }

    private fun createBottomNavigationItem(defaultDrawable: Int, checkedDrawable: Int, text: String): BaseTabItem {
        val normalItemView = BottomNavigationItem(this)
        normalItemView.initialize(defaultDrawable, checkedDrawable)
        normalItemView.title = text
        return normalItemView
    }

}