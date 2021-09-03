package com.daqsoft.module_mine.viewmodel

import android.app.Application
import android.content.Intent
import android.view.View
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import androidx.hilt.lifecycle.ViewModelInject
import cn.jpush.android.api.JPushInterface
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.PhoneUtils
import com.blankj.utilcode.util.RegexUtils
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.global.MMKVGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.pojo.LoginInfo
import com.daqsoft.library_base.pojo.Profile
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.MMKVUtils
import com.daqsoft.library_common.bean.AppMenu
import com.daqsoft.module_mine.R
import com.daqsoft.module_mine.BR
import com.daqsoft.module_mine.repository.MineRepository
import com.daqsoft.module_mine.viewmodel.itemviewmodel.MineItemViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.toolbar.ToolbarViewModel
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.daqsoft.mvvmfoundation.utils.SingleLiveEvent
import com.jeremyliao.liveeventbus.LiveEventBus
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @ClassName    MineViewModel
 * @Description
 * @Author       yuxc
 * @CreateDate   2021/5/7
 */
class MineViewModel : ToolbarViewModel<MineRepository> {

    @ViewModelInject
    constructor(application: Application, mineRepository:MineRepository):super(application,mineRepository)


    val profile = ObservableField<Profile>()
    val mobile = ObservableField<String>()
    val personalCenter = ObservableField<Int>(View.GONE)

    override fun onCreate() {
        initToolbar()
    }

    private fun initToolbar() {
        setBackground(R.color.transparent)
        setBackIconVisible(View.GONE)
        setTitleTextColor(R.color.white)
        setTitleText(getApplication<Application>().resources.getString(R.string.module_mine_homepage))

    }

    val logoutLiveData = SingleLiveEvent<Unit>()


    val postObservable = ObservableField<String>()

    /**
     * 给RecyclerView添加ObservableList
     */
    var observableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()
    /**
     * 给RecyclerView添加ItemBinding
     */
    var itemBinding: ItemBinding<ItemViewModel<*>> = ItemBinding.of(BR.viewModel, R.layout.recycleview_mine_item)


    /**
     * 设置点击事件
     */
    var personalInfoOnClick = BindingCommand<Unit>(BindingAction {
        ARouter
            .getInstance()
            .build(ARouterPath.Mine.PAGER_PERSONAL_INFO)
            .withString("id",profile.get()?.employee?.id?.toString())
            .navigation()
    })


    fun createItem(list : List<AppMenu>){
        observableList.clear()
        list.forEach {
            if (it.number == "PERSONAL_CENTER"){
                personalCenter.set(View.VISIBLE)
            }else{
                observableList.add(MineItemViewModel(this,it))
            }
        }
        observableList.add(MineItemViewModel(this,AppMenu(
            null,
            null,
            null,
            "退出登录",
            null,
            "LOGOUT",
            null,
            null,
            null,
            null,
            null,
            null,
            )))
    }

    /**
     * 获取用户信息
     */
    fun getUserInfo(){
        addSubscribe(
            model
                .getUserInfo()
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<Profile>>() {
                    override fun onSuccess(t: AppResponse<Profile>) {
                        t.data?.let {
                            profile.set(it)
                            mobile.set(it.account.mobile)
                            val sb = StringBuilder()
                            if (!it.department.isNullOrEmpty()){
                                sb.append(it.department[0].depName)
                            }
                            if (!it.post.isNullOrEmpty()){
                                if (sb.isNotBlank()){
                                    sb.append(" ")
                                }
                                sb.append(it.post[0].name)
                            }
                            postObservable.set(sb.toString())
                            if (RegexUtils.isMobileSimple(it.account.mobile)){
                                val chars = it.account.mobile.toCharArray()

                                sb.clear()
                                chars.forEachIndexed { index, c ->
                                    if (index == 3 || index == 7){
                                        sb.append(' ')
                                    }
                                    sb.append(c)
                                }
                                mobile.set(sb.toString())
                            }
                        }
                    }
                })
        )
    }

    /**
     * 退出登录
     */
    fun logout(){
        addSubscribe(
            model
                .logout()
                .doOnSubscribe{
                    showLoadingDialog()
                }
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<Any>>() {
                    override fun onSuccess(t: AppResponse<Any>) {

                        dismissLoadingDialog()

                        val loginInfoJson = MMKVUtils.decodeString(MMKVGlobal.LOGIN_INFO)
                        if (!loginInfoJson.isNullOrBlank()){
                            val loginInfo = GsonUtils.fromJson<LoginInfo>(loginInfoJson,LoginInfo::class.java)
                            JPushInterface.deleteAlias(getApplication(),loginInfo.profile.employee.id)
                        }

                        MMKVUtils.clearAll()

                        ARouter
                            .getInstance()
                            .build(ARouterPath.Mine.PAGER_LOGIN)
                            .withFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK )
                            .navigation()
                    }

                    override fun onFail(e: Throwable) {
                        super.onFail(e)

                        dismissLoadingDialog()

                        val loginInfoJson = MMKVUtils.decodeString(MMKVGlobal.LOGIN_INFO)
                        if (!loginInfoJson.isNullOrBlank()){
                            val loginInfo = GsonUtils.fromJson<LoginInfo>(loginInfoJson,LoginInfo::class.java)
                            JPushInterface.deleteAlias(getApplication(),loginInfo.profile.employee.id)
                        }

                        MMKVUtils.clearAll()

                        ARouter
                            .getInstance()
                            .build(ARouterPath.Mine.PAGER_LOGIN)
                            .withFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK )
                            .navigation()
                    }
                })
        )
    }
}