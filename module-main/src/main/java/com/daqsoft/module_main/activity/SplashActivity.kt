package com.daqsoft.module_main.activity

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.GsonUtils
import com.daqsoft.library_base.global.MMKVGlobal
import com.daqsoft.library_base.pojo.LoginInfo
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.MMKVUtils
import com.daqsoft.module_main.R
import timber.log.Timber


/**
 * @package name：com.daqsoft.module_main.activity
 * @date 3/6/2021 下午 5:48
 * @author zp
 * @describe
 */
class SplashActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({startActicity()},1000)

    }

    private fun startActicity() {
        val isFirst = MMKVUtils.decodeBoolean(MMKVGlobal.ISFIRST)
        val loginInfoJson = MMKVUtils.decodeString(MMKVGlobal.LOGIN_INFO)
        if(!isFirst!!){
            ARouter
                .getInstance()
                .build(ARouterPath.Main.PAGER_WELCOME)
                .navigation()
            finish()
            return
        }

        if (loginInfoJson.isNullOrBlank()){
            ARouter
                .getInstance()
                .build(ARouterPath.Mine.PAGER_LOGIN)
                .navigation()
            finish()
            return
        }

        val loginInfo = GsonUtils.fromJson<LoginInfo>(loginInfoJson, LoginInfo::class.java)
        if (loginInfo == null){
            ARouter
                .getInstance()
                .build(ARouterPath.Mine.PAGER_LOGIN)
                .navigation()
            finish()
            return
        }

        ARouter
            .getInstance()
            .build(ARouterPath.Main.PAGER_MAIN)
            .navigation()
        finish()
    }
}