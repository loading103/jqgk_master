package com.daqsoft.module_mine.viewmodel

import android.app.Application
import android.content.Intent
import android.view.View
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.GsonUtils
import com.daqsoft.library_base.global.HttpGlobal
import com.daqsoft.library_base.global.MMKVGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.pojo.LoginInfo
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.MMKVUtils
import com.daqsoft.library_common.utils.MyCountDownTimer
import com.daqsoft.module_mine.R
import com.daqsoft.module_mine.repository.MineRepository
import com.daqsoft.module_mine.repository.pojo.vo.Company
import com.daqsoft.module_mine.repository.pojo.vo.CompanyInfo
import com.daqsoft.mvvmfoundation.base.BaseViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.binding.command.BindingConsumer
import com.daqsoft.mvvmfoundation.http.NetworkUtil
import com.daqsoft.mvvmfoundation.utils.AesCryptUtils
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import timber.log.Timber

/**
 * @package name：com.daqsoft.module_mine.viewmodel
 * @date 6/11/2020 下午 2:02
 * @author zp
 * @describe 登录viewModel
 */
class LoginViewModel : BaseViewModel<MineRepository> {

    @ViewModelInject
    constructor(application: Application,mineRepository: MineRepository):super(application,mineRepository)

    /**
     * 账号
     */
    val accountNumber = ObservableField<String>("")

    /**
     * 密码
     */
    val password = ObservableField<String>("")

    /**
     * 验证码
     */
    val verifyCodeObservable = ObservableField<String>()

    /**
     * 验证码可见性
     */
    val verifyCodeVisible =  ObservableField<Int>(View.VISIBLE)

    /**
     * 密码可见性
     */
    val passVisible =  ObservableField<Int>(View.GONE)

    /**
     * 清除账号可见性
     */
    val accountNumberCleanVisible =  ObservableField<Int>(View.GONE)

    /**
     * 清除密码可见性
     */
    val passwordCleanVisible =  ObservableField<Int>(View.GONE)

    /**
     * 登陆失败提示可见性
     */
    val  errorMessageVisible = ObservableField<Int>(View.INVISIBLE)

    /**
     * 登陆失败提示信息
     */
    val errorMessage = ObservableField<String>()

    /**
     * 清除账号点击事件
     */
    val accountNumberCleanOnClick = BindingCommand<Unit>(BindingAction {
        accountNumber.set("")
    })

    /**
     * 清除密码点击事件
     */
    val passwordCleanOnClick = BindingCommand<Unit>(BindingAction {
        password.set("")
    })

    /**
     * 密码可见性
     */
    val passwordVisible = MutableLiveData<Boolean>(false)

    /**
     * 验证码点击
     */
    val onClickYzm = MutableLiveData<Boolean>()

    /**
     * 密码可见ICON
     */
    val passwordVisibleIcon = ObservableField<Int>(R.mipmap.dl_yj)

    /**
     * 是否输入账号
     */
    val accountIconSelected = ObservableField<Boolean>(false)
    /**
     * 是否输入验证码
     */
    val verifyIconSelected = ObservableField<Boolean>(false)

    /**
     * 密码可见点击事件
     */
    val passwordVisibleOnClick = BindingCommand<Unit>(BindingAction {
        passwordVisible.value = !passwordVisible.value!!
        passwordVisibleIcon.set(if (passwordVisible.value!!) R.mipmap.dl_yjk else R.mipmap.dl_yj)
    })

    /**
     * 账号密码是否都有数据
     */
    val bothHaveData = MutableLiveData<Pair<Boolean,Boolean>>(Pair(false,false))
    
    /**
     * 账号输入监听
     */
    var accountNumberChangedListener = BindingCommand<String>(BindingConsumer {
        if (it.isNullOrEmpty()){
            accountIconSelected.set(false)
            accountNumberCleanVisible.set(View.GONE)
            bothHaveData.value = Pair(false,bothHaveData.value!!.second)
        }else{
            accountNumberCleanVisible.set(View.VISIBLE)
            accountIconSelected.set(true)
            bothHaveData.value = Pair(true,bothHaveData.value!!.second)
        }
    })

    /**
     * 密码输入监听（现在是监听验证码）
     */
    var passwordChangedListener = BindingCommand<String>(BindingConsumer {
        if (it.isNullOrEmpty()){
            passwordCleanVisible.set(View.GONE)
            verifyIconSelected.set(false)
            bothHaveData.value = Pair(bothHaveData.value!!.first,false)
        }else{
            passwordCleanVisible.set(View.VISIBLE)
            verifyIconSelected.set(true)
            bothHaveData.value = Pair(bothHaveData.value!!.first,true)
        }
    })

    /**
     * 验证码点击事件
     */
    val verifyCodeOnClick = BindingCommand<Unit>(BindingAction {
        getVerifyCode()
    })

    /**
     * 登录点击事件
     */
    val logInOnClick = BindingCommand<Unit>(BindingAction {
        if(!NetworkUtil.isNetworkAvailable(getApplication())){
            errorMessageVisible.set(View.VISIBLE)
            errorMessage.set(getApplication<Application>().resources.getString(R.string.network_connection_failed))
            return@BindingAction
        }
        if(!checkedFild.get()!!){
            ToastUtils.showLong("请阅读并同意隐私政策")
            return@BindingAction
        }
        MMKVUtils.encode(MMKVGlobal.ISFIRSTAGREE,true)
        logIn()
    })

    /**
     * 隐私政策点击事件
     */
    val checkedFild = ObservableField<Boolean>(false)
    val privateOnClick = BindingCommand<Unit>(BindingAction {
        ARouter
            .getInstance()
            .build(ARouterPath.Workbench.PAGER_WEB)
            .withString("url", HttpGlobal.PRIVATE_XIEYI)
            .withString("title", "隐私政策")
            .navigation()
    })

    /**
     * 获取验证码
     */
    fun getVerifyCode(){
        if(accountNumber.get().isNullOrBlank()){
            errorMessageVisible.set(View.VISIBLE)
            errorMessage.set("请输入手机号或账号")
            return
        }
        if( accountNumber.get()?.length!=11){ //如果不是11位全数字，必须是字母开头
            errorMessageVisible.set(View.VISIBLE)
            errorMessage.set("请输入正确的手机号")
            return;
        }
        errorMessageVisible.set(View.INVISIBLE)
        onClickYzm.value=true
        addSubscribe(
            model
                .getVerifyCode(accountNumber.get()!!)
                .doOnSubscribe{
                    showLoadingDialog()
                }
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<Any>>() {
                    override fun onSuccess(t: AppResponse<Any>) {
                        dismissLoadingDialog()
//                        verifyCodeObservable.set(t.data.toString())
                    }

                    override fun onFail(e: Throwable) {
                        super.onFail(e)
                        dismissLoadingDialog()
                        errorMessageVisible.set(View.VISIBLE)
                        errorMessage.set(e.message)
                    }

                })

        )
    }

    /**
     * 登录
     */
    private fun logIn(){
        if(verifyCodeObservable.get().isNullOrBlank()){
            errorMessageVisible.set(View.VISIBLE)
            errorMessage.set("请输入验证码")
            return
        }

        val param = mapOf(
            "account" to accountNumber.get()!!,
            "msgCode" to verifyCodeObservable.get()!!
        )
        addSubscribe(
            model
                .login(param)
                .doOnSubscribe{
                    showLoadingDialog()
                }
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<Company>>() {
                    override fun onSuccess(t: AppResponse<Company>) {
                        dismissLoadingDialog()

                        MMKVUtils.encode(MMKVGlobal.ACCOUNT,accountNumber.get()!!)

                        t.data?.let {
                            if(it.companyList.isNullOrEmpty()){
                                errorMessageVisible.set(View.VISIBLE)
                                errorMessage.set("当前没有可登录的景区")
                                return
                            }

                            if (it.companyList.size == 1 && it.companyList[0].status == 1){
                                chooseCompany(it)
                                return
                            }

                            ARouter
                                .getInstance()
                                .build(ARouterPath.Mine.PAGER_SCENIC_LIST)
                                .withString("companyJson", GsonUtils.toJson(it))
                                .navigation()

                        }


                    }

                    override fun onFail(e: Throwable) {
                        super.onFail(e)
                        dismissLoadingDialog()
                        errorMessageVisible.set(View.VISIBLE)
                        errorMessage.set(e.message)
                    }

                })

        )
    }


    fun chooseCompany(company : Company){
        val companyInfo = company.companyList[0]
        val account = accountNumber.get()!!
        val companyId = companyInfo.id.toString()
        val tempLicense = company.tempLicense
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