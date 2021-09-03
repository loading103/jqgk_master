package com.daqsoft.module_main.viewmodel

import android.app.Application
import androidx.databinding.ObservableArrayList
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.daqsoft.library_base.global.MMKVGlobal
import com.daqsoft.library_base.utils.MMKVUtils
import com.daqsoft.module_main.repository.MainRepository
import com.daqsoft.mvvmfoundation.base.BaseViewModel

/**
 * @package name：com.daqsoft.module_main.viewmodel
 * @date 21/12/2020 上午 11:02
 * @author zp
 * @describe
 */
class WelcomeViewModel  : BaseViewModel<MainRepository> {

    @ViewModelInject
    constructor(application: Application,mainRepository: MainRepository):super(application, mainRepository)


    override fun onCreate() {
    }

}