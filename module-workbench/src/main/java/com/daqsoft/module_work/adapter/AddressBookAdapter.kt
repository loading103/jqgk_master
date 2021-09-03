package com.daqsoft.module_work.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.module_work.R
import com.daqsoft.module_work.databinding.RecycleviewAddressBookItemBinding
import com.daqsoft.module_work.databinding.RecycleviewAddressBookTitleBinding
import com.daqsoft.module_work.viewmodel.itemviewmodel.AddressBookItemViewModel
import com.daqsoft.module_work.viewmodel.itemviewmodel.AddressBookTitleViewModel
import com.daqsoft.module_work.warrper.StickinessDecoration
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel

/**
 * @package name：com.daqsoft.module_workbench.adapter
 * @date 3/12/2020 下午 1:38
 * @author zp
 * @describe 通讯录 adapter
 */
class AddressBookAdapter (val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList : MutableList<MultiItemViewModel<*>> = mutableListOf()

    inner class TitleViewHolder(val bing:ViewDataBinding):RecyclerView.ViewHolder(bing.root){

        init {
            bing.root.tag = true
        }

        fun bindData(position: Int,item:MultiItemViewModel<*>){
            bing as RecycleviewAddressBookTitleBinding
            item as AddressBookTitleViewModel
            bing.title.text = item.content.toUpperCase()
        }
    }

    inner class ItemViewHolder(val bing:ViewDataBinding):RecyclerView.ViewHolder(bing.root){

        init {
            bing.root.tag = false
        }

        fun bindData(position: Int,item:MultiItemViewModel<*>){
            bing as RecycleviewAddressBookItemBinding
            item as AddressBookItemViewModel
            bing.viewModel = item

            bing.root.setOnClickListenerThrottleFirst {
                clickListener?.itemOnClick(position, item)
            }

            bing.call.setOnClickListenerThrottleFirst {
                clickListener?.callOnClick(position, item)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            StickinessDecoration.ExampleStickyView.STICKINESS->{
                val binding = DataBindingUtil.inflate<RecycleviewAddressBookTitleBinding>(
                    LayoutInflater.from(context),
                    R.layout.recycleview_address_book_title,
                    parent,
                    false
                    )
                TitleViewHolder(binding)
            }
            StickinessDecoration.ExampleStickyView.NONE->{
                val binding = DataBindingUtil.inflate<RecycleviewAddressBookItemBinding>(
                    LayoutInflater.from(context),
                    R.layout.recycleview_address_book_item,
                    parent,
                    false
                )
                ItemViewHolder(binding)
            }
            else ->{
                val binding = DataBindingUtil.inflate<RecycleviewAddressBookItemBinding>(
                    LayoutInflater.from(context),
                    R.layout.recycleview_address_book_title,
                    parent,
                    false
                )
                ItemViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = dataList[position]

        when(holder){
            is ItemViewHolder ->{
                holder.bindData(position,item)

            }
            is TitleViewHolder ->{
                holder.bindData(position,item)
            }
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when(dataList[position].itemType as String){
            ConstantGlobal.TITLE -> StickinessDecoration.ExampleStickyView.STICKINESS
            ConstantGlobal.ITEM -> StickinessDecoration.ExampleStickyView.NONE
            else -> super.getItemViewType(position)
        }

    }

    fun setData(data : List<MultiItemViewModel<*>>){
        dataList.clear()
        dataList.addAll(data)
        notifyDataSetChanged()
    }

    fun getData() = dataList



    private var clickListener : OnClickListener ? = null

    fun setOnClickListener(listener : OnClickListener){
        clickListener = listener
    }

    interface OnClickListener{

        fun itemOnClick(position: Int,item : AddressBookItemViewModel)

        fun callOnClick(position: Int,item : AddressBookItemViewModel)
    }

}