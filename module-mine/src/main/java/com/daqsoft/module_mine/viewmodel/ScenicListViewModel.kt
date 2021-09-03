package com.daqsoft.module_mine.viewmodel

import android.app.Application
import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.hilt.lifecycle.ViewModelInject
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.GsonUtils
import com.daqsoft.library_base.global.MMKVGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.MMKVUtils
import com.daqsoft.module_mine.R
import com.daqsoft.module_mine.BR
import com.daqsoft.module_mine.repository.MineRepository
import com.daqsoft.module_mine.repository.pojo.vo.Company
import com.daqsoft.module_mine.repository.pojo.vo.CompanyInfo
import com.daqsoft.library_base.pojo.LoginInfo
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.toolbar.ToolbarViewModel
import com.daqsoft.mvvmfoundation.utils.RxUtils
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.module_mine.viewmodel
 * @date 6/11/2020 下午 2:02
 * @author zp
 * @describe 景区列表  viewModel
 */
class ScenicListViewModel : ToolbarViewModel<MineRepository> {

    @ViewModelInject
    constructor(application: Application,mineRepository: MineRepository):super(application,mineRepository)

    var company : Company? = null


    override fun onCreate() {
        super.onCreate()
        initToolbar()
    }

    private fun initToolbar() {
        setBackIconVisible(View.GONE)
        setBackground(Color.TRANSPARENT)
        setTitleText("选择景区")
        setTitleTextColor(Color.WHITE)
    }


    fun chooseCompany(companyInfo : CompanyInfo){
        val account = MMKVUtils.decodeString(MMKVGlobal.ACCOUNT)?:""
        val companyId = companyInfo.id.toString()
        val tempLicense = company?.tempLicense?:""
        val param = mapOf(
            "account" to account,
            "companyId" to companyId,
            "tempLicense" to tempLicense
        )
        addSubscribe(
            model
                .chooseCompany(param)
                .doOnSubscribe{
                    showLoadingDialog()
                }
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<LoginInfo>>() {
                    override fun onSuccess(t: AppResponse<LoginInfo>) {
                        dismissLoadingDialog()

                        t.data?.let {
                            MMKVUtils.encode(MMKVGlobal.LOGIN_INFO,GsonUtils.toJson(it))
                        }
                        ARouter
                            .getInstance()
                            .build(ARouterPath.Main.PAGER_MAIN)
                            .withFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK )
                            .navigation()
                    }

                    override fun onFail(e: Throwable) {
                        super.onFail(e)
                        dismissLoadingDialog()
                    }
                })
        )
    }
}