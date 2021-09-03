package com.daqsoft.module_work.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.module_work.R
import com.daqsoft.module_work.databinding.RecycleviewAddressBookTitleBinding
import com.daqsoft.module_work.databinding.RecycleviewSupplementCardListItemBinding
import com.daqsoft.module_work.databinding.RecycleviewVideoSurveillanceListGridItemBinding
import com.daqsoft.module_work.databinding.RecycleviewVideoSurveillanceListLineItemBinding
import com.daqsoft.module_work.viewmodel.itemviewmodel.VideoSurveillanceListGridItemViewModel
import com.daqsoft.module_work.viewmodel.itemviewmodel.VideoSurveillanceListLineItemViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter

/**
 * @package name：com.daqsoft.module_work.adapter
 * @date 13/7/2021 下午 4:13
 * @author zp
 * @describe
 */
class VideoSurveillanceListAdapter : BindingRecyclerViewAdapter<MultiItemViewModel<*>>() {

    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: MultiItemViewModel<*>
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)
        when(item.itemType){
            ConstantGlobal.GRID -> {
                val itemBinding = binding as RecycleviewVideoSurveillanceListGridItemBinding
                val itemViewModel = item as VideoSurveillanceListGridItemViewModel

            }
            ConstantGlobal.LINE -> {
                val itemBinding = binding as RecycleviewVideoSurveillanceListLineItemBinding
                val itemViewModel = item as VideoSurveillanceListLineItemViewModel

            }
        }


    }
}