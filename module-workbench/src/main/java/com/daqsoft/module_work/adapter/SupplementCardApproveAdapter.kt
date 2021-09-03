package com.daqsoft.module_work.adapter

import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.library_common.warrper.FullyGridLayoutManager
import com.daqsoft.module_work.R
import com.daqsoft.module_work.databinding.RecycleviewSupplementCardApproveItemBinding
import com.daqsoft.module_work.databinding.RecycleviewSupplementCardApproveItemItemBinding
import com.daqsoft.mvvmfoundation.utils.dp
import com.luck.picture.lib.decoration.GridSpacingItemDecoration
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter

/**
 * @package name：com.daqsoft.module_work.adapter
 * @date 7/7/2021 下午 2:18
 * @author zp
 * @describe
 */
class SupplementCardApproveAdapter  : BindingRecyclerViewAdapter<Pair<String, List<String>>>() {

    private val itemAdapterList = arrayListOf<SupplementCardApproveItemAdapter>()

    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: Pair<String, List<String>>
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)

        val itemBinding = binding as RecycleviewSupplementCardApproveItemBinding

        itemBinding.topLine.isVisible = position != 0
        itemBinding.bottomLine.isVisible = position != itemCount-1

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
                        var spacing = 8.dp
                        outRect.bottom = spacing
                        spacing = 4.dp
                        val column = position % spanCount
                        outRect.left = column * spacing / spanCount
                        outRect.right = spacing - (column + 1) * spacing / spanCount
                    }
                })
            }

            val supplementCardApproveItemAdapter = SupplementCardApproveItemAdapter(context,position)
            supplementCardApproveItemAdapter.setData(item.second.toMutableList())
            itemAdapterList.add(supplementCardApproveItemAdapter)
            adapter = supplementCardApproveItemAdapter
        }

    }



    fun getData():HashMap<Int,List<String>>{
        val map = HashMap<Int,List<String>>()
        itemAdapterList.forEachIndexed { index, itemAdapter ->
            map[index] = itemAdapter.getData()
        }
        return map
    }

    fun updateData(listPosition: Int,list:MutableList<String>){
        itemAdapterList[listPosition].apply {
            setData(list)
            notifyDataSetChanged()
        }
    }

    private var onClickListener: OnClickListener? = null

    fun setOnClickListener(listener: OnClickListener) {
        this.onClickListener = listener
    }

    interface OnClickListener {

        fun onAddClick(listPosition : Int)

    }

    inner class SupplementCardApproveItemAdapter(val context: Context,val listPosition: Int) : RecyclerView.Adapter<SupplementCardApproveItemAdapter.ViewHolder>() {

        inner class ViewHolder(val binding : RecycleviewSupplementCardApproveItemItemBinding) : RecyclerView.ViewHolder(binding.root)

         val NORMAL = 1
         val ADD = 2

        private var data: MutableList<String> = arrayListOf()

        fun setData(list:MutableList<String>){
            this.data = list
        }

        fun getData() = data

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

            val binding = DataBindingUtil.inflate<RecycleviewSupplementCardApproveItemItemBinding>(
                LayoutInflater.from(context),
                R.layout.recycleview_supplement_card_approve_item_item,
                parent,
                false
            )
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (getItemViewType(position) == ADD) {
                holder.binding.ivDel.isVisible = false
                holder.binding.name.setTextColor(context.resources.getColor(R.color.color_59abff))
                holder.binding.name.text = "添加"
                Glide
                    .with(context)
                    .load(R.mipmap.add)
                    .centerCrop()
                    .into(holder.binding.avatar)
                holder.binding.root.setOnClickListenerThrottleFirst{
                    onClickListener?.onAddClick(listPosition)
                }
            } else {
                holder.binding.ivDel.isVisible = true
                holder.binding.name.setTextColor(context.resources.getColor(R.color.color_666666))
                holder.binding.name.text = data[position]
                Glide
                    .with(context)
                    .load("")
                    .placeholder(R.mipmap.tongxunlv_touxiang)
                    .centerCrop()
                    .into(holder.binding.avatar)
            }

            holder.binding.ivDel.setOnClickListenerThrottleFirst {
                try {
                    if (position != RecyclerView.NO_POSITION && data.size > position) {
                        data.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, data.size)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        override fun getItemCount(): Int {
            return data.size + 1
        }

        override fun getItemViewType(position: Int): Int {
            return if (isShowAddItem(position)) {
                ADD
            } else {
                NORMAL
            }
        }

        private fun isShowAddItem(position: Int): Boolean {
            val size: Int = data.size
            return position == size
        }

    }
}