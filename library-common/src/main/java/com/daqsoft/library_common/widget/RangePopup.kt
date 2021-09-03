package com.daqsoft.library_common.widget

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.library_common.R
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarUtil
import com.haibin.calendarview.CalendarView
import com.haibin.calendarview.CalendarView.OnCalendarRangeSelectListener
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.util.XPopupUtils
import timber.log.Timber

/**
 * @package name：com.daqsoft.module_work.widget
 * @date 11/5/2021 下午 4:14
 * @author zp
 * @describe 日历
 */
class RangePopup(context: Context) : BottomPopupView(context),
    CalendarView.OnMonthChangeListener,
    CalendarView.OnCalendarInterceptListener,
    OnCalendarRangeSelectListener
{


    lateinit var determine : TextView
    lateinit var date : TextView
    var calendarView : CalendarView ? = null
    var selectRange: MutableList<Calendar>? = null

    var start : Calendar? = null

    override fun getImplLayoutId(): Int {
        return R.layout.layout_popup_range
    }

    override fun onCreate() {
        super.onCreate()

        val cancel = findViewById<TextView>(R.id.cancel)
        cancel.setOnClickListenerThrottleFirst {
            dismiss()
        }

        determine = findViewById<TextView>(R.id.determine)
        determine.setOnClickListenerThrottleFirst {
            var calendars: MutableList<Calendar>? = calendarView!!.selectCalendarRange
            if(calendars.isNullOrEmpty()){
                calendars = arrayListOf()
                if (start != null){
                    calendars.add(start!!)
                    calendars.add(start!!)
                }
            }


            clickListener?.determineOnClick(calendars)
            dismiss()
        }

        date = findViewById<TextView>(R.id.date)

        val left = findViewById<ImageView>(R.id.left)
        left.setOnClickListenerThrottleFirst {
            calendarView!!.scrollToPre()
        }

        val right = findViewById<ImageView>(R.id.right)
        right.setOnClickListenerThrottleFirst {
            calendarView!!.scrollToNext()
        }

        calendarView = findViewById<CalendarView>(R.id.calendar_view)
        date.text = "${calendarView!!.curYear}年${calendarView!!.curMonth}月"
        calendarView!!.setOnCalendarRangeSelectListener(this)
        calendarView!!.setOnMonthChangeListener(this)
        calendarView!!.setOnCalendarInterceptListener(this)
        calendarView!!.setRange(
            1970, 1, 1,
            calendarView!!.curYear, calendarView!!.curMonth, calendarView!!.curDay
        )
        calendarView!!.post(Runnable { calendarView!!.scrollToCurrent() })

        if(!selectRange.isNullOrEmpty()){
            calendarView?.setSelectCalendarRange(selectRange!!.first(),selectRange!!.last())
        }


    }


    override fun getMaxHeight(): Int {
        return (XPopupUtils.getScreenHeight(context)*0.95f).toInt()
    }

    fun setSelectCalendarRange(selectRange: MutableList<Calendar>?){
        this.selectRange = selectRange

        if(!selectRange.isNullOrEmpty()){
            calendarView?.setSelectCalendarRange(selectRange.first(), selectRange.last())
        }
    }


    private var clickListener : OnClickListener? = null

    fun setOnClickListener(listener: OnClickListener){
        this.clickListener = listener
    }

    interface OnClickListener{

        fun determineOnClick(calendars: List<Calendar>?)

    }


    override fun onMonthChange(year: Int, month: Int) {
        date.text = "${year}年${month}月"
    }

    override fun onCalendarIntercept(calendar: Calendar): Boolean {
        val compare = CalendarUtil.compareTo(calendar.year, calendar.month, calendar.day, calendarView!!.curYear, calendarView!!.curMonth, calendarView!!.curDay)
        return compare > 0
    }

    override fun onCalendarInterceptClick(calendar: Calendar?, isClick: Boolean) {
        ToastUtils.showShortSafe("选择时间大于当前时间")
    }

    override fun onCalendarSelectOutOfRange(calendar: Calendar?) {

    }

    override fun onSelectOutOfRange(calendar: Calendar?, isOutOfMinRange: Boolean) {

    }

    override fun onCalendarRangeSelect(calendar: Calendar?, isEnd: Boolean) {
        Timber.e("calendar ： ${calendar}")
        if (!isEnd) {
            start =  calendar
        }
    }


    fun clearSelectRange(){
        calendarView?.clearSelectRange()
    }
}

