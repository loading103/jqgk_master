package com.daqsoft.module_work.widget

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.library_common.widget.RangePopup
import com.daqsoft.module_work.R
import com.daqsoft.module_work.adapter.GridConditionAdapter
import com.daqsoft.module_work.adapter.GridConditionPeriodAdapter
import com.daqsoft.mvvmfoundation.utils.dp
import com.haibin.calendarview.Calendar
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.util.XPopupUtils
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.module_work.widget
 * @date 6/7/2021 下午 4:41
 * @author zp
 * @describe
 */
class GridConditionPopup: BottomPopupView {

    constructor(context: Context):super(context)

    constructor(context: Context,title:String ):super(context){
        this.title = title
    }

    private var title : String=""

    private val clickMap = hashMapOf<Int,Int>()

    private var periodData : List<String> ? = null
    private var recyclerView2 : RecyclerView ? = null
    private var periodPosition : Int = 0
    private val periodAdapter : GridConditionPeriodAdapter by lazy {
        GridConditionPeriodAdapter().apply {
            itemBinding = ItemBinding.of(
                ItemBinding.VAR_NONE,
                R.layout.recycleview_grid_condition_period
            )
            setItems(periodData)
            setOnClickListener(object : GridConditionPeriodAdapter.OnClickListener {
                override fun onClick(position: Int) {
                    periodPosition = position
                    calendarPopup()
                }
            })
        }
    }


    // 时间范围
    private val selectRange : HashMap<Int,List<Calendar>> by lazy { hashMapOf<Int,List<Calendar>>() }


    private var conditionData = mutableListOf<Pair<String,List<String>>>()
    private val gridConditionAdapter : GridConditionAdapter by lazy {
        GridConditionAdapter().apply {
            itemBinding = ItemBinding.of(
                ItemBinding.VAR_NONE,
                R.layout.recycleview_grid_condition_item
            )
            setItems(conditionData)
            setOnClickListener(object : GridConditionAdapter.OnClickListener {
                override fun onClick(listPosition: Int, gridPosition: Int) {
                    clickMap[listPosition] = gridPosition
                }

            })
        }
    }

    fun setData(data:MutableList<Pair<String,List<String>>>) {
        this.conditionData = data
        gridConditionAdapter.setItems(conditionData)
        gridConditionAdapter.notifyDataSetChanged()
    }

    override fun getImplLayoutId(): Int {
        return R.layout.layout_popup_grid_condition
    }

    override fun onCreate() {
        super.onCreate()

        val title = findViewById<TextView>(R.id.tv_title)
        title.text= this.title

        val close = findViewById<ImageView>(com.daqsoft.library_common.R.id.close)
        close.setOnClickListenerThrottleFirst {
            dismiss()
        }

        val tv_cancel = findViewById<TextView>(com.daqsoft.library_common.R.id.tv_cancel)
        tv_cancel.setOnClickListenerThrottleFirst {
            clickMap.clear()
            gridConditionAdapter.reset()

            selectRange.clear()
            periodAdapter.reset()


            onClickListener?.resetOnClick()
            dismiss()
        }

        val tv_ensure = findViewById<TextView>(com.daqsoft.library_common.R.id.tv_ensure)
        tv_ensure.setOnClickListenerThrottleFirst {
            onClickListener?.determineOnClick(clickMap,selectRange)
            dismiss()
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = gridConditionAdapter
        }


        recyclerView2  = findViewById<RecyclerView>(R.id.recycler_view2)
        recyclerView2?.apply {
            isVisible = !periodData.isNullOrEmpty()
            layoutManager = LinearLayoutManager(context)
            if (itemDecorationCount == 0){
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        val position = parent.getChildAdapterPosition(view)
                        if (position > 0){
                            outRect.top = 28.dp
                        }
                    }
                })
            }
            adapter = periodAdapter
        }

    }

    override fun getMaxHeight(): Int {
        return (XPopupUtils.getScreenHeight(context)*0.8f).toInt()
    }

    fun setPeriodData(data: List<String>){
        this.periodData = data
        periodAdapter.setItems(periodData)
        periodAdapter.notifyDataSetChanged()
    }

    /**
     * 日历 popup
     */
    fun calendarPopup(){
        XPopup
            .Builder(context)
            .isDestroyOnDismiss(true)
            .asCustom(RangePopup(context).apply {
                setOnClickListener(object : RangePopup.OnClickListener {
                    override fun determineOnClick(calendars: List<Calendar>?) {
                        this@GridConditionPopup.selectRange[periodPosition] = calendars!!
                        if (!calendars.isNullOrEmpty()){
                            val start = "${calendars.first().year}.${calendars.first().month}.${calendars.first().day}"
                            val end = "${calendars.last().year}.${calendars.last().month}.${calendars.last().day}"
                            periodAdapter.setSelectRange(periodPosition,"${start}~${end}")
                        }
                    }
                })
            })
            .show()
    }


    private var onClickListener : OnClickListener? = null

    fun setOnClickListener(listener: OnClickListener){
        this.onClickListener = listener
    }

    interface OnClickListener{

        fun resetOnClick()

        fun determineOnClick(clickMap : HashMap<Int,Int> ,selectRange :  HashMap<Int,List<Calendar>> )

    }
}