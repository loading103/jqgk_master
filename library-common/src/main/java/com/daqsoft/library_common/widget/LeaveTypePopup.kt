package com.daqsoft.library_common.widget

import android.content.Context
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.library_common.R
import com.daqsoft.library_common.BR
import com.daqsoft.library_common.adapter.LeaveTypePopupAdapter
import com.daqsoft.library_common.bean.LeaveType
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.util.XPopupUtils
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @describe 请假类型选择弹窗
 */
class LeaveTypePopup  : BottomPopupView {

    constructor(context: Context) : super(context)

    private var datas = mutableListOf<LeaveType>()

    private val mAdapter : LeaveTypePopupAdapter by lazy { LeaveTypePopupAdapter() }

    private var selectBean: LeaveType?=null

    fun setData(
        datas: MutableList<LeaveType>, leaveType: LeaveType? ){
        this.datas = datas
        if(leaveType==null){
            setSelectedPosition(datas[0])
        }else{
            setSelectedPosition(leaveType)
        }

    }

    override fun getImplLayoutId(): Int {
        return R.layout.layout_popup_leave_type
    }

    override fun initPopupContent() {
        super.initPopupContent()

        val tv_cancel = findViewById<TextView>(R.id.tv_cancel)

        val tv_ensure = findViewById<TextView>(R.id.tv_ensure)


        tv_cancel.setOnClickListener {
            dismiss()
        }

        tv_ensure.setOnClickListener {
            dismiss()
            itemOnClickListener?.onClick(selectBean)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view1)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = mAdapter.apply {
                itemBinding = ItemBinding.of(BR.content, R.layout.item_popup_leave_type)
//                addItemDecoration(object : RecyclerView.ItemDecoration(){
//                    override fun getItemOffsets(
//                        outRect: Rect,
//                        view: View,
//                        parent: RecyclerView,
//                        state: RecyclerView.State
//                    ) {
//                        outRect.bottom = 12.dp
//                    }
//                })
                setItems(datas)
                setItemOnClickListener(object : LeaveTypePopupAdapter.ItemOnClickListener{
                    override fun onClick(position: Int, bean: LeaveType) {
                        selectBean=bean
                    }
                })
            }
        }
    }

    override fun getMaxHeight(): Int {
        return (XPopupUtils.getScreenHeight(context)*.65f).toInt()
    }

    /**
     * 设置选中位置
     */
    fun setSelectedPosition(bean: LeaveType?){
        if(bean==null && datas.size>0){
            mAdapter?.setSelectedPosition(datas[0])
        }
        datas?.forEachIndexed { index, s ->
            if(bean?.id == s.id){
                mAdapter.setSelectedPosition(bean)
            }
        }
    }

    private var itemOnClickListener : ItemOnClickListener? = null

    fun setItemOnClickListener(listener: ItemOnClickListener){
        this.itemOnClickListener = listener
    }

    interface ItemOnClickListener{
        fun onClick(selectBean:LeaveType?)
    }



}