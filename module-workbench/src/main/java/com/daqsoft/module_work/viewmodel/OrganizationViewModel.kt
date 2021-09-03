package com.daqsoft.module_work.viewmodel

import android.app.Application
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.wrapper.loadsircallback.EmptyCallback
import com.daqsoft.library_base.wrapper.loadsircallback.LoadingCallback
import com.daqsoft.module_work.BR
import com.daqsoft.module_work.R
import com.daqsoft.module_work.repository.WorkRepository
import com.daqsoft.module_work.repository.pojo.vo.Department
import com.daqsoft.module_work.viewmodel.itemviewmodel.AddressBookItemViewModel
import com.daqsoft.module_work.viewmodel.itemviewmodel.DepartmentItemViewModel
import com.daqsoft.mvvmfoundation.base.BaseViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.binding.command.BindingConsumer
import com.daqsoft.mvvmfoundation.toolbar.ToolbarViewModel
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.daqsoft.mvvmfoundation.utils.SingleLiveEvent
import com.kingja.loadsir.callback.Callback
import com.kingja.loadsir.callback.SuccessCallback
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.module_work.viewmodel
 * @date 18/5/2021 下午 3:25
 * @author zp
 * @describe
 */
class OrganizationViewModel : ToolbarViewModel<WorkRepository> {

    @ViewModelInject
    constructor(application: Application, workBenchRepository: WorkRepository):super(application, workBenchRepository)

    /**
     * 给RecyclerView添加ObservableList
     */
    var observableList: ObservableList<MultiItemViewModel<*>> = ObservableArrayList()
    /**
     * 给RecyclerView添加ItemBinding
     */
    var itemBinding: ItemBinding<MultiItemViewModel<*>> = ItemBinding.of{ itemBinding, position, item ->
        when (item.itemType as String) {
            ConstantGlobal.DEPT -> itemBinding.set(BR.viewModel, R.layout.recycleview_department_item)
            ConstantGlobal.EMPLOYEE -> itemBinding.set(BR.viewModel, R.layout.recycleview_address_book_item)
            else -> itemBinding.set(BR.viewModel, R.layout.recycleview_department_item)
        }
    }


    val back = SingleLiveEvent<Unit>()
    override fun backOnClick() {
        super.backOnClick()
        back.call()
    }

    fun initData(dept : Department){
        setTitleText(dept.depName)
        if(!dept.children.isNullOrEmpty()){
            dept.children.forEach {
                val item =  DepartmentItemViewModel(this,it)
                item.multiItemType(ConstantGlobal.DEPT)
                observableList.add(item)
            }
        }
        if (!dept.employee.isNullOrEmpty()){
            dept.employee.forEach {
                val item =  AddressBookItemViewModel(this,it)
                item.multiItemType(ConstantGlobal.EMPLOYEE)
                observableList.add(item)
            }
        }

        if (observableList.isEmpty()){
            loadSirLiveEvent.value = EmptyCallback::class.java
        }else{

            original.addAll(observableList)

            loadSirLiveEvent.value = SuccessCallback::class.java
        }
    }


    fun getOrganization(){
        setTitleText("公司部门")
        addSubscribe(
            model
                .getOrganization()
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<Department>>() {
                    override fun onSuccess(t: AppResponse<Department>) {
                        t.datas?.let {
                            if (it.isNullOrEmpty()){
                                loadSirLiveEvent.value = EmptyCallback::class.java
                                return
                            }
                            it.forEach {
                                val item =  DepartmentItemViewModel(this@OrganizationViewModel,it)
                                item.multiItemType(ConstantGlobal.DEPT)
                                observableList.add(item)
                            }

                            original.addAll(observableList)

                            loadSirLiveEvent.value = SuccessCallback::class.java
                        }
                    }
                })
        )
    }


    val original = arrayListOf<MultiItemViewModel<*>>()

    val searchTextChangedListener = BindingCommand<String>(BindingConsumer { search ->
        if(original.isEmpty()){
            return@BindingConsumer
        }

        if (search.isNullOrEmpty()){

            observableList.clear()
            observableList.addAll(original)

        }else{

            val find = original.filter {
                if (it is AddressBookItemViewModel){
                    return@filter it.employee.name.contains(search)
                }

                if (it is DepartmentItemViewModel){
                    return@filter it.department.depName.contains(search)
                }

                return@filter false
            }

            observableList.clear()
            observableList.addAll(find)
        }
    })
}