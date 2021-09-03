package com.daqsoft.module_work.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.module_work.R
import com.daqsoft.module_work.databinding.RecycleviewSupplementCardCarbonCopyItemBinding

/**
 * @package name：com.daqsoft.module_work.adapter
 * @date 7/7/2021 下午 5:10
 * @author zp
 * @describe
 */
class SupplementCardCarbonCopyAdapter(val context: Context) : RecyclerView.Adapter<SupplementCardCarbonCopyAdapter.ViewHolder>() {

    inner class ViewHolder(val binding : RecycleviewSupplementCardCarbonCopyItemBinding) : RecyclerView.ViewHolder(binding.root)

    val NORMAL = 1
    val ADD = 2

    private var data: MutableList<String> = arrayListOf()

    fun setData(list:MutableList<String>){
        this.data = list
    }

    fun getData() = data

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = DataBindingUtil.inflate<RecycleviewSupplementCardCarbonCopyItemBinding>(
            LayoutInflater.from(context),
            R.layout.recycleview_supplement_card_carbon_copy_item,
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
                onClickListener?.onAddClick()
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

    private var onClickListener: OnClickListener? = null

    fun setOnClickListener(listener: OnClickListener) {
        this.onClickListener = listener
    }

    interface OnClickListener {
        fun onAddClick()
    }
}