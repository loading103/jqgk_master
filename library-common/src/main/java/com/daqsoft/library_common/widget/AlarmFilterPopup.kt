package com.daqsoft.library_common.widget

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.library_common.R
import com.daqsoft.library_common.adapter.TaskFilterPeriodAdapter
import com.daqsoft.library_common.adapter.TaskFilterTypeAdapter
import com.daqsoft.library_common.bean.Dict
import com.daqsoft.mvvmfoundation.utils.dp
import com.google.android.flexbox.*
import com.haibin.calendarview.Calendar
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.enums.PopupPosition
import com.lxj.xpopup.interfaces.SimpleCallback
import com.lxj.xpopup.util.XPopupUtils
import me.tatarka.bindingcollectionadapter2.ItemBinding
import java.util.*


/**
 * @describe 任务筛选
 */
class AlarmFilterPopup(context: Context) : BottomPopupView(context) {

    // 时间筛选相关
    private var title1: String = ""
    private var timeData = mutableListOf<String>()
    private var periodSelectedPosition = -1
    private val taskFilterPeriodAdapter: TaskFilterPeriodAdapter by lazy {
        TaskFilterPeriodAdapter().apply {
            itemBinding = ItemBinding.of(
                ItemBinding.VAR_NONE,
                R.layout.recycleview_task_filter_period_item
            )
            setItems(timeData)
            setItemOnClickListener(object : TaskFilterPeriodAdapter.ItemOnClickListener {
                override fun onClick(position: Int, content: String) {
                    periodSelectedPosition = position
                    if(selectRange != null){
                        selectRange = null
                        period?.text = "请选择时间范围"
                        rangePopup.clearSelectRange()
                    }
                }
            })
        }
    }
    // 时间范围
    private var period : TextView ? = null
    var selectRange : List<Calendar> ? = null
    val rangePopup : RangePopup by lazy {
        RangePopup(context).apply {
            setOnClickListener(object : RangePopup.OnClickListener {
                override fun determineOnClick(calendars: List<Calendar>?) {
                    this@AlarmFilterPopup.selectRange = calendars
                    if (!calendars.isNullOrEmpty()){
                        val start = "${calendars.first().year}.${calendars.first().month}.${calendars.first().day}"
                        val end = "${calendars.last().year}.${calendars.last().month}.${calendars.last().day}"
                        period?.text = "$start ~ $end"
                        periodSelectedPosition = -1
                        taskFilterPeriodAdapter.setSelectedPosition(periodSelectedPosition)
                    }
                }
            })
        }
    }

    // 类型相关
    private var title2: String = ""
    private var typeData = mutableListOf<String>()
    private var typeSelectedPosition = -1
    private var typeText : String ? = null
    private val taskFilterTypeAdapter: TaskFilterTypeAdapter by lazy {
        TaskFilterTypeAdapter().apply {
            itemBinding = ItemBinding.of(
                ItemBinding.VAR_NONE,
                R.layout.recycleview_task_filter_type_item
            )
            setItems(typeData)
            setItemOnClickListener(object : TaskFilterTypeAdapter.ItemOnClickListener {
                override fun onClick(position: Int, content: String) {
                    typeSelectedPosition = position
                    typeText = content
                }
            })
        }
    }

    // 状态相关
    private var title3: String = ""
    private var status: TextView ? = null
    private var statusArrow: ImageView ? = null
    private var statusData = mutableListOf<String>()
    private var statusSelectedPosition = -1
    private var statusText : String ? = null
    private val statusPopup: TopMenuPopup by lazy {
        TopMenuPopup(context).apply {
            setItemOnClickListener(object : TopMenuPopup.ItemOnClickListener {
                override fun onClick(position: Int, content: String) {
                    status?.text = content
                    statusSelectedPosition = position
                    statusText = content
                }
            })
        }
    }

    override fun getImplLayoutId(): Int {
        return R.layout.layout_popup_alarm_filter
    }

    override fun getMaxHeight(): Int {
        return (XPopupUtils.getScreenHeight(context)*0.9f).toInt()
    }

    override fun onCreate() {
        super.onCreate()

        val close = findViewById<ImageView>(R.id.close)
        close.setOnClickListenerThrottleFirst {
            dismiss()
        }
        val tv_cancel = findViewById<TextView>(R.id.tv_cancel)
        tv_cancel.setOnClickListenerThrottleFirst {
            reset()
            onClickListener?.resetOnClick()
            dismiss()
        }
        val tv_ensure = findViewById<TextView>(R.id.tv_ensure)
        tv_ensure.setOnClickListenerThrottleFirst {
            var periodList : MutableList<Calendar>? =  null
            if (selectRange == null){
                val calendar = java.util.Calendar.getInstance()
                val today = Calendar()
                today.year = calendar.get(java.util.Calendar.YEAR)
                today.month = calendar.get(java.util.Calendar.MONTH)+1
                today.day = calendar.get(java.util.Calendar.DAY_OF_MONTH)

                when(periodSelectedPosition){
                    -1 -> periodList = null
                    0 -> {
                        if (periodList == null){
                            periodList = mutableListOf()
                        }
                        periodList.add(today)
                        periodList.add(today)
                    }
                    1 ->{
                        if (periodList == null){
                            periodList = mutableListOf()
                        }
                        calendar.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.MONDAY)
                        val week = Calendar()
                        week.year = calendar.get(java.util.Calendar.YEAR)
                        week.month = calendar.get(java.util.Calendar.MONTH)+1
                        week.day = calendar.get(java.util.Calendar.DAY_OF_MONTH)

                        periodList.add(week)
                        periodList.add(today)
                    }
                    2->{
                        if (periodList == null){
                            periodList = mutableListOf()
                        }
                        calendar.set(java.util.Calendar.DAY_OF_MONTH, calendar.getActualMinimum(java.util.Calendar.DAY_OF_MONTH))

                        val month = Calendar()
                        month.year = calendar.get(java.util.Calendar.YEAR)
                        month.month = calendar.get(java.util.Calendar.MONTH)+1
                        month.day = calendar.get(java.util.Calendar.DAY_OF_MONTH)

                        periodList.add(month)
                        periodList.add(today)
                    }
                    else -> periodList = null
                }
            }else{
                periodList = selectRange!!.toMutableList()
            }

            onClickListener?.determineOnClick(periodList,typeText,statusText)
            dismiss()
        }

        val title1 = findViewById<TextView>(R.id.tv_title1)
        title1.text = this.title1
        val recyclerView1 = findViewById<RecyclerView>(R.id.recycler_view1)
        recyclerView1.apply {
            val spanCount = 3
            layoutManager = GridLayoutManager(context!!, 3, GridLayoutManager.VERTICAL, false)
            adapter = taskFilterPeriodAdapter
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
                        if (position >= spanCount) {
                            outRect.top = spacing
                        }
                        val column = position % spanCount
                        outRect.left = column * spacing / spanCount
                        outRect.right = spacing - (column + 1) * spacing / spanCount
                    }
                })
            }
        }
        period = findViewById<TextView>(R.id.period)
        period?.setOnClickListenerThrottleFirst {
            calendarPopup()
        }

        val title2 = findViewById<TextView>(R.id.tv_title2)
        title2.text = this.title2
        val recyclerView2 = findViewById<RecyclerView>(R.id.recycler_view2)
        recyclerView2.apply {
            layoutManager = FlexboxLayoutManager(context)
            adapter = taskFilterTypeAdapter
            if (itemDecorationCount == 0) {
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        outRect.set(6.dp, 6.dp, 6.dp, 6.dp)
                    }
                })
            }

        }

        val group3 = findViewById<Group>(R.id.group3)
        if(this.title3.isNotBlank()){
           group3.visibility = View.VISIBLE
        }
        val title3 = findViewById<TextView>(R.id.tv_title3)
        title3.text = this.title3
        status = findViewById<TextView>(R.id.status)
        status?.text = if (statusData.isEmpty()) "未处理" else statusData[0]
        status?.setOnClickListenerThrottleFirst {
            showStatusPopUp()
        }
        statusArrow = findViewById<ImageView>(R.id.status_arrow)
    }

    /**
     * 重置
     */
    private fun reset() {
        period?.text = "请选择时间范围"
        selectRange = null
        periodSelectedPosition = -1
        typeSelectedPosition = -1
        statusSelectedPosition = -1
        taskFilterPeriodAdapter.setSelectedPosition(periodSelectedPosition)
        taskFilterTypeAdapter.setSelectedPosition(typeSelectedPosition)
        statusPopup.setSelectedPosition(statusSelectedPosition)
        rangePopup.clearSelectRange()

        typeText = null
        statusText = null
    }

    /**
     * 设置时间数据源
     */
    fun setTimeData(title: String, data: MutableList<String>) {
        this.title1 = title
        this.timeData = data
        taskFilterPeriodAdapter.setItems(timeData)
        taskFilterPeriodAdapter.notifyDataSetChanged()
    }

    fun getTimeData() = timeData

    /**
     * 设置类型数据源
     */
    fun setTypeData(title: String, data: MutableList<String>) {
        this.title2 = title
        this.typeData = data
        taskFilterTypeAdapter.setItems(typeData)
        taskFilterTypeAdapter.notifyDataSetChanged()
    }

    fun getTypeData() = typeData


    /**
     * 设置状态数据源
     */
    fun setStatusData(title: String, data: MutableList<String>) {
        this.title3 = title
        this.statusData = data
        statusPopup.setData(statusData)
    }

    fun getStatusData() = statusData


    /**
     * 设置时间选中位置
     */
    fun setTimeSelectedPosition(position: Int) {
        taskFilterPeriodAdapter.setSelectedPosition(position)
    }


    /**
     * 设置类型选中位置
     */
    fun setTypeSelectedPosition(position: Int) {
        taskFilterPeriodAdapter.setSelectedPosition(position)
    }

    /**
     * 设置状态选中位置
     */
    fun setStatusSelectedPosition(position: Int) {
        statusPopup.setSelectedPosition(position)
    }

    /**
     * 展示状态popup
     */
    fun showStatusPopUp() {
        XPopup
            .Builder(context)
            .atView(status)
            .popupPosition(PopupPosition.Bottom)
            .setPopupCallback(object : SimpleCallback() {
                override fun onShow(popupView: BasePopupView?) {
                    statusArrow?.rotation = 270.toFloat()
                }

                override fun onDismiss(popupView: BasePopupView?) {
                    statusArrow?.rotation = 90.toFloat()
                }
            })
            .asCustom(statusPopup)
            .show()
        statusPopup.setSelectedPosition(statusSelectedPosition)
    }

    /**
     * 日历 popup
     */
    fun calendarPopup(){
        XPopup
            .Builder(context)
            .asCustom(rangePopup)
            .show()
    }

    private var onClickListener: OnClickListener? = null

    fun setOnClickListener(listener: OnClickListener) {
        this.onClickListener = listener
    }

    interface OnClickListener {

        fun resetOnClick()

        fun determineOnClick(periodList : List<Calendar>?, typeText : String?, statusText:String?)

    }
}
