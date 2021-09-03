package com.daqsoft.module_task

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import com.daqsoft.library_base.init.IModuleInit
import timber.log.Timber

class TaskModuleInit : IModuleInit{

    @SuppressLint("DefaultLocale")
    override fun onInitAhead(application: Application): Boolean {
        Timber.e("Task初始化 -- onInitAhead")
        return false
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onInitLow(application: Application): Boolean {
        Timber.e("Task初始化 -- onInitLow")
        return false
    }
}


