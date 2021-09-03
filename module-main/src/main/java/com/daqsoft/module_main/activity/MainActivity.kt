package com.daqsoft.module_main.activity

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import cn.jpush.android.api.JPushInterface
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.GsonUtils
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.global.HttpGlobal
import com.daqsoft.library_base.pojo.UpdateInfo
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.LoadSirUtil
import com.daqsoft.library_common.bean.AppMenu
import com.daqsoft.library_common.widget.SpecialTab
import com.daqsoft.library_common.widget.SpecialTabRound
import com.daqsoft.module_main.BR
import com.daqsoft.module_main.R
import com.daqsoft.module_main.databinding.ActivityMainBinding
import com.daqsoft.module_main.repository.pojo.vo.MyNotificationExtra
import com.daqsoft.module_main.service.UpdateService
import com.daqsoft.module_main.two_progress_alive.KeepAliveJobService
import com.daqsoft.module_main.two_progress_alive.LocalForegroundService
import com.daqsoft.module_main.two_progress_alive.RemoteForegroundService
import com.daqsoft.module_main.uitls.NotifyMessageOpenHelper
import com.daqsoft.module_main.viewmodel.MainViewModel
import com.kingja.loadsir.callback.Callback
import com.kingja.loadsir.core.LoadSir
import com.youth.banner.util.LogUtils
import dagger.hilt.android.AndroidEntryPoint
import me.majiajie.pagerbottomtabstrip.NavigationController
import me.majiajie.pagerbottomtabstrip.PageNavigationView
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectedListener


@AndroidEntryPoint
@Route(path = ARouterPath.Main.PAGER_MAIN)
class MainActivity  : AppBaseActivity<ActivityMainBinding, MainViewModel>(){


    @JvmField
    @Autowired
    var notifyBundle : Bundle ? = null

    lateinit var navigationController : NavigationController

    private var mFragments: ArrayList<Fragment> = arrayListOf()
    private var selectedByDefault : Int = 0

    override fun onResume() {
        super.onResume()
        JPushInterface.setBadgeNumber(this,0)
    }

    override fun initParam() {
        super.initParam()
        notifyBundle?.let {
            val notifyExtra = it.getParcelable<MyNotificationExtra>("notifyExtra")
            if (notifyExtra != null){
                NotifyMessageOpenHelper.pageJump(notifyExtra)
            }
        }
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_main
    }

    override fun initViewModel(): MainViewModel {
        return viewModels<MainViewModel>().value
    }

    override fun initView() {
        super.initView()
        initLoadService()
    }

    private fun initLoadService() {
        loadService = LoadSir.getDefault().register(this, Callback.OnReloadListener {
            LoadSirUtil.postLoading(loadService!!)
            initData()
        })
        LoadSirUtil.postLoading(loadService!!)
    }

    override fun initData() {
        super.initData()
        viewModel.getMenus()
        viewModel.getUserInfo()
        viewModel.getPushAudio()
        viewModel.checkUpdate()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initViewObservable() {
        super.initViewObservable()
        viewModel.appMenu.observe(this, Observer {
            initPage(it)
        })

        viewModel.userInfoEvent.observe(this, Observer {
            if (!it.post.isNullOrEmpty() && it.post.find { it.gps == 1 } != null){
                requestPermission(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
                    Manifest.permission.READ_PHONE_STATE,
                    callback = {
                        startService(Intent(this, LocalForegroundService::class.java))
                        startService(Intent(this, RemoteForegroundService::class.java))
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            KeepAliveJobService.startJob(this)
                        }
                    } )
            }
        })

        viewModel.updateLiveData.observe(this, Observer {
          alertUserDown(it)
        })
    }

    /**
     * 弹出确认框
     *
     * @param context
     * @param text
     * @param url
     */
    private var dialog: AlertDialog? = null
    @RequiresApi(Build.VERSION_CODES.O)
    fun alertUserDown(info: UpdateInfo) {
        dialog = AlertDialog.Builder(this, R.style.LightDialog).create()
        dialog!!.show()
        val window = dialog!!.window
        window!!.setContentView(R.layout.include_version_window)
        val tvContent = window.findViewById(R.id.version_content) as TextView
        tvContent.text = "发现新版本v${info.VersionCode}，建议更新"
        val tvVerionLogo = window.findViewById<TextView>(R.id.version_content_logo)
        if (!info.AppUpdateInfo.isNullOrBlank()) {
            tvVerionLogo.text = info.AppUpdateInfo
        } else {
            tvVerionLogo.visibility = View.GONE
        }
        val btnCancel = window.findViewById(R.id.version_cancel) as TextView
        val btnSure = window.findViewById(R.id.version_sure) as TextView
        var vLine: View = window.findViewById(R.id.v_version_line)
        if (HttpGlobal.Update.isMustUpdate) {
            btnCancel.visibility = View.GONE
            vLine.visibility = View.GONE
            dialog!!.setCanceledOnTouchOutside(false)
            dialog!!.setCancelable(false)
        } else {
            btnCancel.visibility = View.VISIBLE
            vLine.visibility = View.VISIBLE
        }
        btnCancel.setOnClickListener { dialog?.dismiss() }
        btnSure.setOnClickListener {
            var isGranted = packageManager.canRequestPackageInstalls();
            if (isGranted) {
                val updateIntent = Intent(this, UpdateService::class.java)
                updateIntent.putExtra("app_name", resources.getString(R.string.app_name))
                updateIntent.putExtra("updatepath", info.DownPath)
                startService(updateIntent)
                LogUtils.e("下载中...")
                dialog?.dismiss()
            }else{
                checkUpdatePermission()
            }
        }
    }

    /**
     * 展示未知来源安装
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun checkUpdatePermission() {
        AlertDialog.Builder(this).setCancelable(false).setTitle(
            "安装应用需要打开未知来源权限，请去设置中开启权限"
        ).setPositiveButton(
            "确定"
        ) { d, w ->
            toInstallPermissionSettingIntent()
        }.show()
    }

    /**
     * 开启安装未知来源权限
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    fun toInstallPermissionSettingIntent() {
        val packageURI = Uri.parse("package:$packageName")
        val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI)
        startActivityForResult(intent, 1000)
    }



    override fun onBackPressed() {
        ActivityUtils.startHomeActivity()
    }

    fun simulationData(){
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
        //这里需要通过ARouter获取，不能直接new,因为在组件独立运行时，宿主app是没有依赖其他组件，所以new不到其他组件的Fragment)
        val task = ARouter.getInstance().build(ARouterPath.Task.PAGER_TASK).navigation() as Fragment
        mFragments.add(task)
        val work = ARouter.getInstance().build(ARouterPath.Workbench.PAGER_WORKBENCH).navigation() as Fragment
        mFragments.add(work)
        val home = ARouter.getInstance().build(ARouterPath.Home.PAGER_HOME).navigation() as Fragment
        mFragments.add(home)
        val data = ARouter.getInstance().build(ARouterPath.Statistics.PAGER_STATISTICS).navigation() as Fragment
        mFragments.add(data)
        val mine = ARouter.getInstance().build(ARouterPath.Mine.PAGER_MINE).navigation() as Fragment
        mFragments.add(mine)
        // 默认选中第一个
        supportFragmentManager.beginTransaction().apply {
            add(R.id.frame_layout, mFragments[0])
            commit()
        }
    }

    private fun initFragment(mFragments : List<Fragment>) {
        // 默认选中第一个
        supportFragmentManager.beginTransaction().apply {
            add(R.id.frame_layout, mFragments[selectedByDefault])
            commit()
        }

    }

    private fun initBottomNavigation(pageList:List<BaseTabItem>){
        val customBuilder : PageNavigationView.CustomBuilder = binding.pageNavigation.custom()
        pageList.forEach {
            customBuilder.addItem(it)
        }
        val build = customBuilder.build()
        build.setSelect(selectedByDefault)
        build.addTabItemSelectedListener(object : OnTabItemSelectedListener {
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
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onRepeat(index: Int) {}
            })
    }

    private fun initBottomNavigation() {

        val task  = createSpecialTab(
            R.mipmap.home_menu_renwu,
            R.mipmap.home_menu_renwu_hover,
            "任务"
        )

        val workbench  = createSpecialTab(
            R.mipmap.home_menu_gongzt,
            R.mipmap.home_menu_gongzt_hover,
            "工作台"
        )

        val group  = createSpecialTabRound(
            R.mipmap.home_menu_juheye,
            R.mipmap.home_menu_juheye_hover
        )

        val data  = createSpecialTab(
            R.mipmap.home_menu_shuju,
            R.mipmap.home_menu_shuju_hover,
            "数据"
        )

        val mine  = createSpecialTab(
            R.mipmap.home_menu_wodi,
            R.mipmap.home_menu_wodi_hover,
            "我的"
        )


        binding
            .pageNavigation
            .custom()
            .addItem(task)
            .addItem(workbench)
            .addItem(group)
            .addItem(data)
            .addItem(mine)
            .build()
            .addTabItemSelectedListener(object : OnTabItemSelectedListener {
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
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onRepeat(index: Int) {}
            })
    }

    private fun createSpecialTab(defaultDrawable: Int, checkedDrawable: Int, text: String): BaseTabItem {
        val normalItemView = SpecialTab(this)
        normalItemView.initDrawable(defaultDrawable, checkedDrawable)
        normalItemView.title = text
        return normalItemView
    }

    private fun createSpecialTabRound(defaultDrawable: Int, checkedDrawable: Int): BaseTabItem {
        val normalItemView = SpecialTabRound(this)
        normalItemView.initDrawable(defaultDrawable, checkedDrawable)
        return normalItemView
    }


    private fun createSpecialTabUrl(defaultUrl: String, checkedUrl: String, text: String): BaseTabItem {
        val normalItemView = SpecialTab(this)
        normalItemView.initUrl(defaultUrl, checkedUrl)
        normalItemView.title = text
        return normalItemView
    }

    private fun createSpecialTabRoundUrl(defaultUrl: String, checkedUrl: String): BaseTabItem {
        val normalItemView = SpecialTabRound(this)
        normalItemView.initUrl(defaultUrl, checkedUrl)
        return normalItemView
    }

    private fun initPage(list : List<AppMenu>) {
        val pageList = arrayListOf<BaseTabItem>()
        list.forEachIndexed { index, it ->
            if(it.selected == true){
                selectedByDefault = index
            }
            val item = if (it.number == "GROUP_PAGE"){
                createSpecialTabRoundUrl(it.icon?:"",it.selectedIcon?:"")
            }else{
                createSpecialTabUrl(it.icon?:"",it.selectedIcon?:"",it.label?:"")
            }
            pageList.add(item)
            when(it.number){
                "DATA_CENTER" -> {
                    val data = ARouter
                        .getInstance()
                        .build(ARouterPath.Statistics.PAGER_STATISTICS)
                        .withString("menuJson",GsonUtils.toJson(it.children?:""))
                        .navigation() as Fragment
                    mFragments.add(data)
                }
                "TASK" -> {
                    val task = ARouter
                        .getInstance()
                        .build(ARouterPath.Task.PAGER_TASK)
                        .withString("menuJson",GsonUtils.toJson(it.children?:""))
                        .navigation() as Fragment
                    mFragments.add(task)
                }
                "WORK_BENCH" ->{
                    val work = ARouter
                        .getInstance()
                        .build(ARouterPath.Workbench.PAGER_WORKBENCH)
                        .withString("menuJson",GsonUtils.toJson(it.children?:""))
                        .navigation() as Fragment
                    mFragments.add(work)
                }
                "GROUP_PAGE" ->{
                    val home = ARouter
                        .getInstance()
                        .build(ARouterPath.Home.PAGER_HOME)
                        .withString("menuJson",GsonUtils.toJson(it.children?:""))
                        .navigation() as Fragment
                    mFragments.add(home)
                }
                "MY" ->{
                    val mine = ARouter
                        .getInstance()
                        .build(ARouterPath.Mine.PAGER_MINE)
                        .withString("menuJson",GsonUtils.toJson(it.children?:""))
                        .navigation() as Fragment
                    mFragments.add(mine)
                }
            }
        }
        initBottomNavigation(pageList)
        initFragment(mFragments)
    }
}