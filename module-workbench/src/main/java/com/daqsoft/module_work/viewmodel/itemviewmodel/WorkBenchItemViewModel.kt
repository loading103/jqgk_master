package com.daqsoft.module_work.viewmodel.itemviewmodel

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import com.daqsoft.module_work.viewmodel.WorkViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import me.tatarka.bindingcollectionadapter2.ItemBinding
import com.daqsoft.module_work.R
import com.daqsoft.module_work.BR
/**
 * @package name：com.daqsoft.module_workbench.viewmodel
 * @date 25/11/2020 下午 2:08
 * @author zp
 * @describe 工作台 itemViewModel
 */
class WorkBenchItemViewModel (
    private val workBenchViewModel : WorkViewModel,
    val title : String,
    val data : List<ItemViewModel<*>>
) : MultiItemViewModel<WorkViewModel>(workBenchViewModel){

    /**
     * 标题
     */
    val titleObservable = ObservableField<String>()

    /**
     * 给RecyclerView添加ObservableList
     */
    var observableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()
    /**
     * 给RecyclerView添加ItemBinding
     */
    var itemBinding: ItemBinding<ItemViewModel<*>> = ItemBinding.of(BR.viewModel, R.layout.recycleview_workbench_item_label)

    init {

        titleObservable.set(title)

        observableList.addAll(data)

    }
}
