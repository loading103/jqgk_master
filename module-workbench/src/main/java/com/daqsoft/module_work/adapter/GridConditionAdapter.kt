package com.daqsoft.module_work.adapter

import android.graphics.Rect
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.module_work.R
import com.daqsoft.module_work.databinding.RecycleviewGridConditionItemBinding
import com.daqsoft.module_work.databinding.RecycleviewGridConditionItemItemBinding
import com.daqsoft.mvvmfoundation.utils.dp
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.module_work.adapter
 * @date 6/7/2021 下午 5:10
 * @author zp
 * @describe
 */
class GridConditionAdapter : BindingRecyclerViewAdapter<Pair<String,List<String>>>() {


    val itemAdapterList = arrayListOf<GridConditionItemAdapter>()

    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: Pair<String, List<String>>
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)

        val itemBinding = binding as RecycleviewGridConditionItemBinding

        itemBinding.title.text = item.first

        itemBinding.recyclerView.apply {
            val spanCount = 3
            layoutManager = GridLayoutManager(context!!, 3, GridLayoutManager.VERTICAL, false)
            if (itemDecorationCount == 0) {
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        val position = parent.getChildAdapterPosition(view)
                        val spacing = 16.dp
                        outRect.bottom = spacing
                        val column = position % spanCount
                        outRect.left = column * spacing / spanCount
                        outRect.right = spacing - (column + 1) * spacing / spanCount
                    }
                })
            }
            val gridConditionItemAdapter = GridConditionItemAdapter(position)
            gridConditionItemAdapter.itemBinding = ItemBinding.of(ItemBinding.VAR_NONE, R.layout.recycleview_grid_condition_item_item)
            gridConditionItemAdapter.setItems(item.second)
            itemAdapterList.add(gridConditionItemAdapter)
            adapter = gridConditionItemAdapter
        }

    }

    private var onClickListener: OnClickListener? = null

    fun setOnClickListener(listener: OnClickListener) {
        this.onClickListener = listener
    }


    fun reset(){
        itemAdapterList.forEach {
            it.setSelectedPosition(-1)
        }
    }

    interface OnClickListener {
        fun onClick(listPosition : Int,gridPosition:Int)
    }

    inner class GridConditionItemAdapter(val listPosition: Int) : BindingRecyclerViewAdapter<String>() {

        private var selectedPosition= -1

        override fun onBindBinding(
            binding: ViewDataBinding,
            variableId: Int,
            layoutRes: Int,
            position: Int,
            item: String
        ) {
            super.onBindBinding(binding, variableId, layoutRes, position, item)

            val itemBinding = binding as RecycleviewGridConditionItemItemBinding

            itemBinding.root.setOnClickListenerThrottleFirst {
                onClickListener?.onClick(listPosition,position)
                selectedPosition = position
                notifyDataSetChanged()
            }
            itemBinding.content.isSelected = selectedPosition == position
            binding.content.text = item
        }



        fun setSelectedPosition(position : Int){
            selectedPosition = position
            notifyDataSetChanged()
        }
    }
}
