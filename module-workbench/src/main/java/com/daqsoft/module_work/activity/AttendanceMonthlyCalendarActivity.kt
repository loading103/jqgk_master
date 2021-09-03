package com.daqsoft.module_work.activity

import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.LoadSirUtil
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.module_work.BR
import com.daqsoft.module_work.R
import com.daqsoft.module_work.adapter.AttendanceMonthlyCalendarAdapter
import com.daqsoft.module_work.databinding.ActivityAttendanceMonthlyCalendarBinding
import com.daqsoft.module_work.viewmodel.AttendanceMonthlyCalendarViewModel
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarUtil
import com.haibin.calendarview.CalendarView
import com.kingja.loadsir.callback.Callback
import com.kingja.loadsir.core.LoadSir
import dagger.hilt.android.AndroidEntryPoint
import me.tatarka.bindingcollectionadapter2.ItemBinding
import timber.log.Timber
import java.util.*

/**
 * @package name：com.daqsoft.module_work.activity
 * @date 5/7/2021 上午 9:49
 * @author zp
 * @describe 打卡月历
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_ATTENDANCE_MONTHLY_CALENDAR)
class AttendanceMonthlyCalendarActivity :  AppBaseActivity<ActivityAttendanceMonthlyCalendarBinding, AttendanceMonthlyCalendarViewModel>(),
    CalendarView.OnCalendarSelectListener,
    CalendarView.OnMonthChangeListener,
    CalendarView.OnCalendarInterceptListener {


    @Autowired
    @JvmField
    var year : Int = -1

    @Autowired
    @JvmField
    var month : Int = -1

    @Autowired
    @JvmField
    var day : Int = -1

    val attendanceMonthlyCalendarAdapter : AttendanceMonthlyCalendarAdapter by lazy {
        AttendanceMonthlyCalendarAdapter().apply {
            itemBinding = ItemBinding.of(ItemBinding.VAR_NONE,R.layout.recycleview_attendance_monthly_calendar_record_item)
            setOnClickListener(object : AttendanceMonthlyCalendarAdapter.OnClickListener{
                override fun makeUpCard(position: Int, item: Any) {
                    // 补卡  todo
                }
            })
        }
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_attendance_monthly_calendar
    }

    override fun initViewModel(): AttendanceMonthlyCalendarViewModel? {
        return viewModels<AttendanceMonthlyCalendarViewModel>().value
    }

    override fun initView() {
        super.initView()
        initLoadService()
        initCalendar()
        initRecyclerView()
    }


    private fun initLoadService() {
        loadService = LoadSir.getDefault().register(binding.recyclerView, Callback.OnReloadListener {
        })
        LoadSirUtil.postLoading(loadService!!)
    }

    override fun initData() {
        super.initData()

        val date = "${
            if (year > 0 && year <= binding.calendarView.curYear) {
                year
            }  else {
                binding.calendarView.curYear
            }}-${
            String.format(
                "%02d",
                if (month > 0 && month <= binding.calendarView.curMonth) {
                    month
                } else {
                    binding.calendarView.curMonth
                })
        }"
        viewModel.getAttendanceMonthlyCalendar(date)

        viewModel.getAttendanceDetail(
            "${date}-${
                String.format(
                "%02d",
                if (day > 0 && day <= binding.calendarView.curDay) {
                    day
                } else {
                    binding.calendarView.curDay
                })
            }"
        )

        Timber.e("date : " + "${date}-${
            String.format(
                "%02d",
                if (day > 0 && day <= binding.calendarView.curDay) {
                    day
                } else {
                    binding.calendarView.curDay
                })
        }")

    }


    override fun initViewObservable() {
        super.initViewObservable()

        viewModel.calendarEvent.observe(this, androidx.lifecycle.Observer {
            val map: MutableMap<String, Calendar?> = HashMap()

            it.forEach {
                try {
                    val date = it.workDate.split("-")
                    val y = date[0].toInt()
                    val m = date[1].toInt()
                    val d = date[2].toInt()
                    val colors = arrayListOf<Int>()
                    val leave = it.statusList.find { it.status == "LEAVE" }
                    if (leave != null){
                        colors.add(resources.getColor(R.color.color_ffa800))
                    }
                    val other = it.statusList.find { it.status != "LEAVE" }
                    if (other != null){
                        colors.add(resources.getColor(R.color.color_ff5757))
                    }
                    val scheme = getSchemeCalendar(y, m, d, colors)
                    map[scheme.toString()] = scheme
                } catch (e : Exception){
                    return@forEach
                }
            }
            binding.calendarView.setSchemeDate(map)
        })

        viewModel.detail.observe(this, androidx.lifecycle.Observer {
            if (it.clockInList.isNullOrEmpty()){
                LoadSirUtil.postEmpty(loadService!!)
            }else{
                LoadSirUtil.postSuccess(loadService!!)
            }

            binding.checkInDetail.text = "今天打卡${it.clockInNum}次，共计工时${it.workHours}小时"
            attendanceMonthlyCalendarAdapter.setItems(it.clockInList)
        })
        
    }

    private fun initRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@AttendanceMonthlyCalendarActivity)
            adapter = attendanceMonthlyCalendarAdapter

        }
    }

    private fun initCalendar() {
        binding.left.setOnClickListenerThrottleFirst {
            binding.calendarView.scrollToPre()
        }
        binding.right.setOnClickListenerThrottleFirst {
            binding.calendarView.scrollToNext()
        }
        binding.date.text = "${binding.calendarView.curYear}年${binding.calendarView.curMonth}月"
        binding.calendarView.setOnCalendarSelectListener(this)
        binding.calendarView.setOnMonthChangeListener(this)
        binding.calendarView.setOnCalendarInterceptListener(this)
        binding.calendarView.setRange(
            1970,
            1,
            1,
            binding.calendarView.curYear,
            binding.calendarView.curMonth,
            binding.calendarView.curDay
        )
        binding.calendarView.post(Runnable {
            if (year > 0 && month > 0 && day > 0 && year <= binding.calendarView.curYear && month <= binding.calendarView.curMonth && day <= binding.calendarView.curDay){
                binding.calendarView.scrollToCalendar(year, month, day)
            }else{
                binding.calendarView.scrollToCurrent()
            }
        })


    }

    override fun onCalendarOutOfRange(calendar: Calendar?) {

    }


    private var initDaySelected = 0
    override fun onCalendarSelect(calendar: Calendar, isClick: Boolean) {
        // 因为设置了时间范围  选中接口会回调2次 开始时间和结束时间
        if (initDaySelected >= 2){
            LoadSirUtil.postLoading(loadService!!)
            viewModel.getAttendanceDetail("${calendar.year}-${String.format("%02d", calendar.month)}-${String.format("%02d", calendar.day)}")
        }
        initDaySelected++
    }

    private var initMonthSelected = 0
    override fun onMonthChange(year: Int, month: Int) {
        // 因为设置了时间范围  选中接口会回调2次 开始时间和结束时间
        if (initDaySelected >= 2){
            binding.date.text = "${year}年${month}月"
            viewModel.getAttendanceMonthlyCalendar("${year}-${String.format("%02d", month)}")
        }
        initMonthSelected++
    }

    override fun onCalendarIntercept(calendar: Calendar): Boolean {
        val compare = CalendarUtil.compareTo(calendar.year, calendar.month, calendar.day, binding.calendarView.curYear, binding.calendarView.curMonth, binding.calendarView.curDay)
        return compare > 0
    }

    override fun onCalendarInterceptClick(calendar: Calendar?, isClick: Boolean) {
        ToastUtils.showShortSafe("选择时间大于当前时间")
    }

    private fun getSchemeCalendar(
        year: Int,
        month: Int,
        day: Int,
        color : List<Int>,
    ): Calendar? {
        val calendar = Calendar()
        calendar.year = year
        calendar.month = month
        calendar.day = day
        color.forEach {
            val scheme = Calendar.Scheme()
            scheme.shcemeColor = it
            calendar.addScheme(scheme)
        }
        return calendar
    }

}