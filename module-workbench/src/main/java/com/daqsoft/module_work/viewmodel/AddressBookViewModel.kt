package com.daqsoft.module_work.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.wrapper.loadsircallback.EmptyCallback
import com.daqsoft.module_work.repository.WorkRepository
import com.daqsoft.library_common.bean.AddressBook
import com.daqsoft.module_work.viewmodel.itemviewmodel.AddressBookItemViewModel
import com.daqsoft.module_work.viewmodel.itemviewmodel.AddressBookTitleViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.binding.command.BindingConsumer
import com.daqsoft.mvvmfoundation.toolbar.ToolbarViewModel
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.daqsoft.mvvmfoundation.utils.SingleLiveEvent
import com.kingja.loadsir.callback.SuccessCallback

/**
 * @package name：com.daqsoft.module_work.viewmodel
 * @date 8/5/2021 上午 11:20
 * @author zp
 * @describe
 */
class AddressBookViewModel : ToolbarViewModel<WorkRepository> {

    @ViewModelInject
    constructor(application: Application,workRepository: WorkRepository):super(application, workRepository)

    override fun onCreate() {
        initToolbar()
    }

    private fun initToolbar() {
        setTitleText("通讯录")
    }


    val total = ObservableField<String>("共0位联系人")
    val original = arrayListOf<MultiItemViewModel<*>>()
    val contactList = SingleLiveEvent<List<MultiItemViewModel<*>>>()

    fun getEmployee(){
        addSubscribe(
            model
                .getEmployee()
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<AddressBook>>() {
                    override fun onSuccess(t: AppResponse<AddressBook>) {
                        t.datas?.let {
                            if (it.isNullOrEmpty()){
                                return
                            }


                            var count = 0



                                it.forEach {
                                    if (it.employee.isNullOrEmpty()) {
                                        return@forEach
                                    }

                                    val title = AddressBookTitleViewModel(
                                        this@AddressBookViewModel,
                                        it.firstWord
                                    )
                                    title.multiItemType(ConstantGlobal.TITLE)
                                    original.add(title)

                                    it.employee.forEach {
                                        val item =
                                            AddressBookItemViewModel(this@AddressBookViewModel, it)
                                        item.multiItemType(ConstantGlobal.ITEM)
                                        original.add(item)
                                        count++
                                    }
                                }


                            total.set("共${count}位联系人")
                            contactList.value = original

                            if (original.isEmpty()){
                                loadSirLiveEvent.value = EmptyCallback::class.java
                            }else{
                                loadSirLiveEvent.value = SuccessCallback::class.java
                            }
                        }
                    }
                })
        )
    }


    val searchLiveEvent = SingleLiveEvent<Boolean>()
    val searchTextChangedListener = BindingCommand<String>(BindingConsumer { search ->
        if(original.isEmpty()){
            return@BindingConsumer
        }

        if (search.isNullOrEmpty()){
            searchLiveEvent.value = false
            contactList.value = original
        }else{
            searchLiveEvent.value = true
            val find = original.filter {
                if (it is AddressBookItemViewModel){
                    return@filter it.employee.name.contains(search)
                }
                return@filter false
            }
            contactList.value = find
        }
    })
}