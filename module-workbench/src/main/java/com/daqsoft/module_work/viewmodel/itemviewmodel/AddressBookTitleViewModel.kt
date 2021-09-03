package com.daqsoft.module_work.viewmodel.itemviewmodel

import androidx.databinding.ObservableField
import com.daqsoft.module_work.viewmodel.AddressBookViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel

/**
 * @package name：com.daqsoft.module_work.viewmodel.itemviewmodel
 * @date 8/5/2021 下午 1:38
 * @author zp
 * @describe 通讯录 title viewModel
 */
class AddressBookTitleViewModel (
    private val addressBookViewModel: AddressBookViewModel,
    val content : String
) : MultiItemViewModel<AddressBookViewModel>(addressBookViewModel){

    val contentObservable = ObservableField<String>()

    init {

        contentObservable.set(content.toUpperCase())
    }

}