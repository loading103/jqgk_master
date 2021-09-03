package com.daqsoft.module_work.viewmodel

import android.app.Application
import android.graphics.Color
import android.view.View
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.library_common.bean.AppMenu
import com.daqsoft.module_work.BR
import com.daqsoft.module_work.R
import com.daqsoft.module_work.repository.WorkRepository
import com.daqsoft.module_work.viewmodel.itemviewmodel.WorkBenchItemLabelViewModel
import com.daqsoft.module_work.viewmodel.itemviewmodel.WorkBenchItemViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.toolbar.ToolbarViewModel
import me.tatarka.bindingcollectionadapter2.ItemBinding

class WorkViewModel : ToolbarViewModel<WorkRepository> {

    @ViewModelInject
    constructor(application: Application,workRepository : WorkRepository):super(application,workRepository)

    override fun onCreate() {
        super.onCreate()
        initToolbar()
    }

    private fun initToolbar() {
        setBackground(Color.TRANSPARENT)
        setBackIconVisible(View.GONE)
        setTitleTextColor(Color.WHITE)
        setTitleText("工作台")
    }


    /**
     * 给RecyclerView添加ObservableList
     */
    var observableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()
    /**
     * 给RecyclerView添加ItemBinding
     */
    var itemBinding: ItemBinding<ItemViewModel<*>> = ItemBinding.of(BR.viewModel, R.layout.recycleview_workbench_item)



    fun createItem(list : List<AppMenu>){
        observableList.clear()
        list.forEach {
            val menu = arrayListOf<ItemViewModel<*>>()
            if (!it.children.isNullOrEmpty()){
                it.children!!.forEach {
                    val item = WorkBenchItemLabelViewModel(this, it)
                    menu.add(item)
                }
            }
            observableList.add(WorkBenchItemViewModel(this,it.label?:"",menu))
        }
    }

}