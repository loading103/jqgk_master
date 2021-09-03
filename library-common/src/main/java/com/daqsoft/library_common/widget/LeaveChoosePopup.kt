package com.daqsoft.library_common.widget

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.library_common.R
import com.daqsoft.library_common.BR
import com.daqsoft.library_common.adapter.LeaveChoosePopupAdapter
import com.daqsoft.library_common.bean.LeaveType
import com.daqsoft.mvvmfoundation.utils.dp
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.util.XPopupUtils
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @describe 请假申请(筛选弹窗)（排序弹窗）
 */
class LeaveChoosePopup  : BottomPopupView {


    constructor(context: Context) : super(context)

    private var topdatas = mutableListOf<LeaveType>()

    private var bottomdatas = mutableListOf<LeaveType>()

    private val listTopAdapter : LeaveChoosePopupAdapter by lazy { LeaveChoosePopupAdapter() }

    private val listBottomAdapter : LeaveChoosePopupAdapter by lazy { LeaveChoosePopupAdapter() }

    private var topBean: LeaveType?=null

    private var buttopmBean: LeaveType?=null

    private var title:String?=null

    private var topTitle:String?=null

    private var  bottomTitle:String?=null

    fun setData(
        topdatas: MutableList<LeaveType>,
        bottomdatas: MutableList<LeaveType>,
        leaveState: LeaveType?,
        leaveType: LeaveType?, title:String?=null, topTitle:String?=null, bottomTitle:String?=null ){
        this.title=title
        this.topTitle=topTitle
        this.bottomTitle=bottomTitle
        this.topdatas = topdatas
        this.bottomdatas = bottomdatas
        if(leaveState==null){
            setSelectedPositionTop(topdatas[0])
        }else{
            setSelectedPositionTop(leaveState)
        }
        if(leaveType==null){
            setSelectedPosition(bottomdatas[0])
        }else{
            setSelectedPosition(leaveType)
        }

    }

    override fun getImplLayoutId(): Int {
        return R.layout.layout_popup_leave_choose
    }

    override fun initPopupContent() {
        super.initPopupContent()

        val close = findViewById<ImageView>(R.id.close)

        val tv_cancel = findViewById<TextView>(R.id.tv_cancel)

        val tv_ensure = findViewById<TextView>(R.id.tv_ensure)


        if(!title.isNullOrBlank()){
            findViewById<TextView>(R.id.tv_title).text=title
        }
        if(!topTitle.isNullOrBlank()){
            findViewById<TextView>(R.id.tv_title1).text=topTitle
        }
        if(!bottomTitle.isNullOrBlank()){
            findViewById<TextView>(R.id.tv_title2).text=bottomTitle
        }

        close.setOnClickListenerThrottleFirst {
            dismiss()
        }

        tv_cancel.setOnClickListenerThrottleFirst {
            dismiss()
            itemOnClickListener?.onClick(null,null)
        }

        tv_ensure.setOnClickListenerThrottleFirst {
            dismiss()
            itemOnClickListener?.onClick(topBean,buttopmBean)
        }

        val topRecycleView = findViewById<RecyclerView>(R.id.recycler_view1)
        topRecycleView.apply {
            layoutManager = GridLayoutManager(context!!, 3, GridLayoutManager.VERTICAL, false)
            adapter = listTopAdapter.apply {
                itemBinding = ItemBinding.of(BR.content, R.layout.item_popup_leave_chooses)
                addItemDecoration(object : RecyclerView.ItemDecoration(){
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        outRect.bottom = 12.dp
                    }
                })
                setItems(topdatas)
                setItemOnClickListener(object : LeaveChoosePopupAdapter.ItemOnClickListener{
                    override fun onClick(position: Int, bean: LeaveType) {
                        topBean=bean
                    }
                })
            }
        }
        val buttomRecycleView = findViewById<RecyclerView>(R.id.recycler_view2)
        buttomRecycleView.apply {
            layoutManager = GridLayoutManager(context!!, 3, GridLayoutManager.VERTICAL, false)
            adapter = listBottomAdapter.apply {
                itemBinding = ItemBinding.of(BR.content, R.layout.item_popup_leave_chooses)
                addItemDecoration(object : RecyclerView.ItemDecoration(){
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        outRect.bottom = 12.dp
                    }
                })
                setItems(bottomdatas)
                setItemOnClickListener(object : LeaveChoosePopupAdapter.ItemOnClickListener{
                    override fun onClick(position: Int, bean: LeaveType) {
                        buttopmBean=bean
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
    fun setSelectedPositionTop(bean: LeaveType){
        if(bean==null && topdatas.size>0){
            listBottomAdapter?.setSelectedPosition(bottomdatas[0])
        }
        topdatas?.forEachIndexed { index, s ->
            if(bean?.id == s.id){
                listTopAdapter.setSelectedPosition(bean)
            }
        }
    }
    fun setSelectedPosition(bean: LeaveType?){
        if(bean==null && bottomdatas.size>0){
            listBottomAdapter?.setSelectedPosition(bottomdatas[0])
        }
        bottomdatas?.forEachIndexed { index, s ->
            if(bean?.id == s.id){
                listBottomAdapter.setSelectedPosition(bean)
            }
        }
    }

    private var itemOnClickListener : ItemOnClickListener? = null

    fun setItemOnClickListener(listener: ItemOnClickListener){
        this.itemOnClickListener = listener
    }

    interface ItemOnClickListener{
        fun onClick(topBean : LeaveType?, bean2: LeaveType?)
    }



}