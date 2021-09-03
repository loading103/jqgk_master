package com.daqsoft.module_mine.viewmodel

import android.app.Application
import android.graphics.Color
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_common.bean.Employee
import com.daqsoft.module_mine.R
import com.daqsoft.module_mine.repository.MineRepository
import com.daqsoft.mvvmfoundation.toolbar.ToolbarViewModel
import com.daqsoft.mvvmfoundation.utils.RxUtils

/**
 * @package name：com.daqsoft.module_mine.viewmodel
 * @date 17/11/2020 下午 2:29
 * @author zp
 * @describe
 */
class PersonalInfoViewModel : ToolbarViewModel<MineRepository> {

    @ViewModelInject constructor(application: Application,mineRepository: MineRepository):super(application, mineRepository)


    override fun onCreate() {
        initToolbar()
    }


    private fun initToolbar(){
        setBackground(Color.TRANSPARENT)
        setBackIconSrc(R.mipmap.back_white)
        setTitleTextColor(Color.WHITE)
        setTitleText(getApplication<Application>().resources.getString(com.daqsoft.library_base.R.string.module_mine_homepage))
    }

    val employee = ObservableField<Employee>()
    val postObservable = ObservableField<String>()
    fun getEmployeeDetail(id:String){
        addSubscribe(
            model
                .getEmployeeDetail(id)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<Employee>>() {
                    override fun onSuccess(t: AppResponse<Employee>) {
                        t.data?.let {
                            employee.set(it)

                            val sb = StringBuilder()
                            if (!it.depName.isNullOrBlank()){
                                sb.append(it.depName)
                            }
                            if (!it.postName.isNullOrBlank()){
                                if (sb.isNotBlank()){
                                    sb.append(" ")
                                }
                                sb.append(it.postName)
                            }
                            postObservable.set(sb.toString())
                        }
                    }
                })
        )
    }
}