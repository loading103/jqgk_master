package com.daqsoft.module_mine.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.KeyEvent
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.DeviceUtils
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.global.MMKVGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.MMKVUtils
import com.daqsoft.library_common.utils.MyCountDownTimer
import com.daqsoft.module_mine.BR
import com.daqsoft.module_mine.R
import com.daqsoft.module_mine.databinding.ActivityLoginBinding
import com.daqsoft.module_mine.viewmodel.LoginViewModel
import com.daqsoft.mvvmfoundation.utils.AesCryptUtils
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * @describe 登录界面
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Mine.PAGER_LOGIN)
class LoginActivity : AppBaseActivity<ActivityLoginBinding, LoginViewModel>() {

    private var mTimer: MyCountDownTimer? = null

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_login
    }

    override fun initViewModel(): LoginViewModel? {
        return viewModels<LoginViewModel>().value
    }

    override fun initData() {
//        viewModel.getCompanyInfo()

        val isFirst = MMKVUtils.decodeBoolean(MMKVGlobal.ISFIRSTAGREE)
        viewModel.checkedFild.set(isFirst)
    }

    @SuppressLint("ResourceType")
    override fun initViewObservable() {
        super.initViewObservable()
        viewModel.passwordVisible.observe(this, Observer {
            if (it) {
                binding.password.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else {
                binding.password.transformationMethod = PasswordTransformationMethod.getInstance()
            }
            binding.password.setSelection(binding.password.text.length)
        })


        //  验证码点击
        viewModel.onClickYzm.observe(this, Observer {
            countDownTime()
        })

        viewModel.bothHaveData.observe(this, Observer {
            if (it.first && it.second) {
                binding.logIn.isEnabled = true
                binding.logIn.alpha=1f
            } else {
                binding.logIn.isEnabled = false
                binding.logIn.alpha=0.5f
            }
        })
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed()
            true
        } else {// 如果不是back键正常响应
            super.onKeyDown(keyCode, event)
        }
    }

    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        if(binding.accountNumber.text.isNotBlank() && binding.verifyCode.text.isNotBlank()){
            binding.logIn.isEnabled = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mTimer != null) {
            mTimer?.cancel()
            mTimer = null
        }
    }
    /**
     * 倒计时
     */
    private fun countDownTime() {
        mTimer = object : MyCountDownTimer(60000, 1000) {
            override  fun onTick(millisUntilFinished: Long) {
                binding.verifyCodeImage?.isEnabled = false
                binding.verifyCodeImage?.text= (millisUntilFinished / 1000).toString() + "s"
                binding.verifyCodeImage?.setTextColor( resources.getColor(R.color.color_59abff))
                binding.verifyCodeImage.helper.borderColorNormal = resources.getColor(R.color.color_59abff)
            }
            override  fun onFinish() {
                binding.verifyCodeImage?.isEnabled = true
                binding.verifyCodeImage?.text="获取验证码"
                binding.verifyCodeImage?.setTextColor( resources.getColor(R.color.color_999999))
                binding.verifyCodeImage.helper.borderColorNormal = resources.getColor(R.color.color_e2e2e2)
            }
        }.start()
    }

}