package com.daqsoft.library_common.widget

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.library_common.R
import com.daqsoft.library_common.adapter.TaskFilterPeriodAdapter
import com.daqsoft.library_common.bean.Options
import com.daqsoft.mvvmfoundation.utils.dp
import com.google.android.flexbox.*
import com.haibin.calendarview.Calendar
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.interfaces.SimpleCallback
import com.lxj.xpopup.util.XPopupUtils
import me.tatarka.bindingcollectionadapter2.ItemBinding
import java.util.*
import kotlin.collections.ArrayList


/**
 * @describe 任务筛选
 */
class TaskFilterPopup(context: Context) : BottomPopupView(context) {

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
                    this@TaskFilterPopup.selectRange = calendars
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


    // 状态相关
    private var title3: String = ""
    private var status: TextView ? = null
    private var statusArrow: ImageView ? = null

    var optionOne : Options? = null
    var optionTwo: Options? = null
    var optionThree : Options? = null

    var option1V : Int = 0
    var option2V : Int = 0
    var option3V : Int = 0


    private val optionsPopup: OptionsPopup<Options> by lazy {
        OptionsPopup<Options>(context).apply {
            setOnOptionsSelectListener(object : OnOptionsSelectListener{
                override fun onOptionsSelect(
                    options1: Int,
                    options2: Int,
                    options3: Int,
                    v: View?
                ) {

                    option1V = options1
                    option2V = options2
                    option3V = options3

                    // 返回的分别是三个级别的选中位置

                    optionOne = allOptions[options1]
                    val optionOneChildren  = optionOne?.children
                    if (!optionOneChildren.isNullOrEmpty()) {
                        optionTwo = optionOneChildren[option2]
                        val optionTwoChildren= optionTwo?.children
                        if (!optionTwoChildren.isNullOrEmpty()) {
                            optionThree = optionTwoChildren[options3]
                        }
                    }
                    status?.text = "${optionOne?.label?:""} - ${optionTwo?.label ?: ""} - ${optionThree?.label ?: ""}"
                }

            })
        }
    }

    override fun getImplLayoutId(): Int {
        return R.layout.layout_popup_task_filter
    }

    override fun getMaxHeight(): Int {
        return (XPopupUtils.getScreenHeight(context)*0.8f).toInt()
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

            onClickListener?.determineOnClick(periodList,optionOne,optionTwo,optionThree)
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



        val title3 = findViewById<TextView>(R.id.tv_title3)
        title3.text = this.title3
        status = findViewById<TextView>(R.id.status)
        status?.text = this.title3
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

        taskFilterPeriodAdapter.setSelectedPosition(periodSelectedPosition)

        rangePopup.clearSelectRange()


        option1V  = 0
        option2V  = 0
        option3V = 0
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
     * 设置时间选中位置
     */
    fun setTimeSelectedPosition(position: Int) {
        taskFilterPeriodAdapter.setSelectedPosition(position)
    }





    var allOptions :List<Options> = ArrayList()
    var option1List:MutableList<Options> = ArrayList()
    var option2List:MutableList<MutableList<Options>> = ArrayList()
    var option3List :MutableList<MutableList<MutableList<Options>>>  = ArrayList()

    fun setOptionsData(title: String,options:List<Options>){
        this.title3 = title
        if (options.isNullOrEmpty()){
            return
        }

        this.allOptions = options
        options.forEach {
            option1List.add(it)
            // 当前第二级
            val currentSecondList: MutableList<Options> = ArrayList()
            // 当前第三级
            val currentThirdList: MutableList<MutableList<Options>> = ArrayList()

            if(it.children.isNullOrEmpty()){
                currentSecondList.add(Options(value = "暂无数据","",null))
                currentThirdList.add(currentSecondList)
            }else{
                it.children?.forEach {
                    currentSecondList.add(it)
                    // 当前第二级
                    val currentAllList: MutableList<Options> = ArrayList()
                    it.children?.forEach {
                        currentAllList.add(it)
                    }
                    currentThirdList.add(currentAllList)
                }
            }
            option2List.add(currentSecondList)
            option3List.add(currentThirdList)
        }

        optionsPopup.setPicker(
            option1List.toList(),
            option2List.toList(),
            option3List.toList()
        )
    }

    /**
     * 展示状态popup
     */
    fun showStatusPopUp() {
        XPopup
            .Builder(context)
            .atView(status)
            .setPopupCallback(object : SimpleCallback() {
                override fun onShow(popupView: BasePopupView?) {
                    statusArrow?.rotation = 270.toFloat()
                }

                override fun onDismiss(popupView: BasePopupView?) {
                    statusArrow?.rotation = 90.toFloat()
                }
            })
            .asCustom(optionsPopup)
            .show()

        optionsPopup.setSelectOptions(option1V,option2V,option3V)
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

        fun determineOnClick(periodList : List<Calendar>?, option1 : Options?,option2 : Options?,option3 : Options?)

    }
}
