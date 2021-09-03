package com.daqsoft.module_main.alone

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.daqsoft.library_base.config.ModuleLifecycleConfig
import com.daqsoft.mvvmfoundation.base.BaseApplication
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : BaseApplication() {

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate() {
        super.onCreate()
        //初始化组件(靠后)
        ModuleLifecycleConfig.initModuleLow(this)
    }


    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        //初始化组件(靠前)
        ModuleLifecycleConfig.initModuleAhead(this)
    }
}