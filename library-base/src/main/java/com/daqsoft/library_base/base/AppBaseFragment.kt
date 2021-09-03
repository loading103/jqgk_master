package com.daqsoft.library_base.base

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.utils.LoadSirUtil
import com.daqsoft.mvvmfoundation.base.BaseFragment
import com.daqsoft.mvvmfoundation.base.BaseViewModel
import com.daqsoft.mvvmfoundation.utils.JumpPermissionManagement
import com.google.gson.Gson
import com.kingja.loadsir.core.LoadService
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.impl.LoadingPopupView
import com.permissionx.guolindev.PermissionX
import timber.log.Timber


/**
 * @package name：com.daqsoft.library_base.base
 * @date 2/11/2020 下午 3:19
 * @author zp
 * @describe
 */
abstract class AppBaseFragment<V : ViewDataBinding, VM : BaseViewModel<*>> : BaseFragment<V, VM>() {

    var loadService : LoadService<*>? = null

    override fun initParam() {
        ARouter.getInstance().inject(this)
    }

    override fun initViewObservable() {
        viewModel.showLoadingDialogLiveData.observe(this, Observer {
            showLoading(it)
        })

        viewModel.dismissLoadingDialogLiveData.observe(this, Observer {
            dismissLoading()
        })

        viewModel.loadSirLiveEvent.observe(this, Observer {
            if (loadService == null){
                throw Throwable("loadService is null")
                return@Observer
            }
            LoadSirUtil.postCallback(loadService!!,it)
        })
    }

    var loadingPopup : LoadingPopupView? = null
    fun showLoading(title: String? = null){
        loadingPopup = XPopup.Builder(requireActivity())
            .dismissOnBackPressed(false)
            .dismissOnTouchOutside(false)
            .hasShadowBg(false)
            .asLoading(title)
            .show() as LoadingPopupView
    }

    fun dismissLoading(){
        loadingPopup?.dismiss()
    }


    /**
     * 权限请求
     */
    fun requestPermission(vararg permissions: String, callback: () -> Unit){
        PermissionX
            .init(this)
            .permissions(permissions.toList())
            .onExplainRequestReason { scope, deniedList ->
                // 解释原因 重新申请
                Timber.e("解释原因 重新申请  ${Gson().toJson(deniedList)}")
            }
            .onForwardToSettings { scope, deniedList ->
                Timber.e("跳转设置开启权限 ${Gson().toJson(deniedList)}")
                // 跳转设置开启权限
                goToSetting()
            }
            .request { allGranted, grantedList, deniedList ->
                // 权限通过
                if (allGranted) {
                    callback()
                }
            }
    }


    fun goToSetting(){
        XPopup
            .Builder(requireActivity())
            .isDestroyOnDismiss(true)
            .asConfirm(
                "提示", "请授予相关权限以便更好地为您提供服务",
                "取消", "确定",
                {
                    JumpPermissionManagement.GoToSetting(requireActivity())
                }, null, false
            )
            .show()
    }

}