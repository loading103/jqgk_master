package com.daqsoft.module_work.adapter

import android.graphics.Rect
import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.daqsoft.module_work.R
import com.daqsoft.module_work.databinding.RecycleviewSupplementCardApproveDetailItemBinding
import com.daqsoft.module_work.databinding.RecycleviewSupplementCardApproveDetailItemItemBinding
import com.daqsoft.module_work.databinding.RecycleviewSupplementCardApproveItemBinding
import com.daqsoft.module_work.repository.pojo.vo.ApproveDetail
import com.daqsoft.mvvmfoundation.utils.dp
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.module_work.adapter
 * @date 12/7/2021 上午 10:55
 * @author zp
 * @describe
 */
class SupplementCardApproveDetailAdapter : BindingRecyclerViewAdapter<ApproveDetail>() {

    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: ApproveDetail
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)

        val itemBinding = binding as RecycleviewSupplementCardApproveDetailItemBinding

        itemBinding.topLine.isVisible = position != 0
        itemBinding.bottomLine.isVisible = position != itemCount - 1

        itemBinding.title.text = item.title

        itemBinding.recyclerView.apply {
            val spanCount = 4
            layoutManager = GridLayoutManager(context!!, spanCount, GridLayoutManager.VERTICAL, false)
            if (itemDecorationCount == 0) {
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        val position = parent.getChildAdapterPosition(view)
                        var spacing = 12.dp
                        if (position > spanCount){
                            outRect.top = 12.dp
                        }
                        spacing = 20.dp
                        val column = position % spanCount
                        outRect.left = column * spacing / spanCount
                        outRect.right = spacing - (column + 1) * spacing / spanCount
                    }
                })
            }

            val itemAdapter = SupplementCardApproveDetailItemAdapter().apply {
                this@apply.itemBinding = ItemBinding.of(ItemBinding.VAR_NONE,R.layout.recycleview_supplement_card_approve_detail_item_item)
                this@apply.setItems(item.staff)
            }
            adapter = itemAdapter
        }

        if (position == 0 || position == itemCount - 1){
            binding.remark.isVisible = false
        }else{
            binding.remark.isVisible = true
            binding.remark.text = item.remark
        }



    }

    internal class SupplementCardApproveDetailItemAdapter : BindingRecyclerViewAdapter<Pair<String,String>>() {

        override fun onBindBinding(
            binding: ViewDataBinding,
            variableId: Int,
            layoutRes: Int,
            position: Int,
            item: Pair<String, String>
        ) {
            super.onBindBinding(binding, variableId, layoutRes, position, item)

            val itemBinding = binding as RecycleviewSupplementCardApproveDetailItemItemBinding

            itemBinding.name.text = item.first

            Glide
                .with(itemBinding.avatar.context)
                .load(item.first)
                .placeholder(R.mipmap.tongxunlv_touxiang)
                .centerCrop()
                .into(itemBinding.avatar)
        }
    }
}
