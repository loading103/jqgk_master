package com.daqsoft.module_work.widget

import android.content.Context
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.module_work.R
import com.daqsoft.module_work.adapter.VideoSurveillanceFilterAdapter
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.util.XPopupUtils
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.module_work.widget
 * @date 14/7/2021 下午 1:56
 * @author zp
 * @describe
 */
class VideoSurveillanceFilterPopup : BottomPopupView {

    constructor(context: Context,title:String) : super(context){
        this.title = title
    }

    lateinit var title:String

    private var filterData : List<String> ? = null

    private var selectedPosition : Int? = null
    private var selectedContent : String ? = null

    private val videoSurveillanceFilterAdapter : VideoSurveillanceFilterAdapter by lazy {
        VideoSurveillanceFilterAdapter().apply {
            itemBinding = ItemBinding.of(ItemBinding.VAR_NONE, R.layout.recyclerview_video_surveillance_filter_item)
            setItems(filterData)
            setItemOnClickListener(object : VideoSurveillanceFilterAdapter.ItemOnClickListener{
                override fun onClick(position: Int, item: String) {
                    selectedPosition = position
                    selectedContent = item
                }
            })
        }
    }


    fun setData(data : List<String>){
        this.filterData = data
        videoSurveillanceFilterAdapter.setItems(filterData)
    }

    fun getData() = filterData

    override fun getImplLayoutId(): Int {
        return R.layout.layout_popup_video_surveillance_filter
    }

    override fun initPopupContent() {
        super.initPopupContent()

        val tv_cancel = findViewById<TextView>(R.id.tv_cancel)

        val tv_ensure = findViewById<TextView>(R.id.tv_ensure)

        val title = findViewById<TextView>(R.id.tv_title)
        title.text = this.title

        tv_cancel.setOnClickListener {
            dismiss()
        }

        tv_ensure.setOnClickListener {
            dismiss()
            itemOnClickListener?.onClick(selectedPosition,selectedContent)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view1)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = videoSurveillanceFilterAdapter
        }
    }

    override fun getMaxHeight(): Int {
        return (XPopupUtils.getScreenHeight(context)*.85f).toInt()
    }
    /**
     * 设置选中位置
     */
    fun setSelectedPosition(position : Int){
        videoSurveillanceFilterAdapter.setSelectedPosition(position)
    }


    private var itemOnClickListener : ItemOnClickListener? = null

    fun setItemOnClickListener(listener: ItemOnClickListener){
        this.itemOnClickListener = listener
    }

    interface ItemOnClickListener{
        fun onClick(position: Int?,content : String?)
    }


}