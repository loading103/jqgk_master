package com.daqsoft.module_work.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import cn.iwgang.simplifyspan.SimplifySpanBuild
import cn.iwgang.simplifyspan.unit.SpecialTextUnit
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseFragment
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.LoadSirUtil
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.module_work.BR
import com.daqsoft.module_work.R
import com.daqsoft.module_work.databinding.FragmentSchedulingBinding
import com.daqsoft.module_work.repository.pojo.vo.PersonalRoster
import com.daqsoft.module_work.viewmodel.SchedulingViewModel
import com.daqsoft.module_work.warrper.RosterNotJoinedCallback
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarUtil
import com.haibin.calendarview.CalendarView
import com.kingja.loadsir.callback.SuccessCallback
import com.kingja.loadsir.core.LoadSir
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


/**
 * @package name：com.daqsoft.module_work.fragment
 * @date 10/5/2021 下午 4:45
 * @author zp
 * @describe 排班
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_SCHEDULING)
class SchedulingFragment  : AppBaseFragment<FragmentSchedulingBinding, SchedulingViewModel>() ,
    CalendarView.OnCalendarSelectListener,
    CalendarView.OnMonthChangeListener
//    ,CalendarView.OnCalendarInterceptListener
{

    lateinit var rosterClLoadSir: LoadSir
    lateinit var selectCalendar : com.haibin.calendarview.Calendar

    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return R.layout.fragment_scheduling
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initViewModel(): SchedulingViewModel? {
        return requireActivity().viewModels<SchedulingViewModel>().value
    }

    override fun initView() {
        super.initView()

        initLoadService()
        initCalendar()
    }

    private fun initLoadService() {
        val loadSir = LoadSir.Builder()
            .addCallback(RosterNotJoinedCallback())
            .setDefaultCallback(SuccessCallback::class.java)
            .build()
        loadService = loadSir.register(binding.rosterCl)
    }

    override fun initData() {
        super.initData()

        val selectCalendar = com.haibin.calendarview.Calendar()
        selectCalendar.year = binding.calendarView.curYear
        selectCalendar.month = binding.calendarView.curMonth
        selectCalendar.day = binding.calendarView.curDay

        viewModel.getRosterGroup()

        viewModel.getPersonalRoster("${selectCalendar.year}-${String.format("%02d", selectCalendar.month)}")
    }

    override fun initViewObservable() {
        super.initViewObservable()

        viewModel.personalRosterEvent.observe(this, Observer {
            calendarUI(it)
            rosterUI()
        })

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
//        binding.calendarView.setOnCalendarInterceptListener(this)
//        binding.calendarView.setRange(
//            1970, 1, 1,
//            binding.calendarView.curYear, binding.calendarView.curMonth, binding.calendarView.curDay
//        )
        binding.calendarView.post(Runnable { binding.calendarView.scrollToCurrent() })
    }





    override fun onCalendarOutOfRange(calendar: Calendar?) {

    }

    override fun onCalendarSelect(calendar: Calendar, isClick: Boolean) {
        selectCalendar = calendar
        binding.selectDate.text = "日期    ${calendar.year}年${calendar.year}年${calendar.year}年    ${getWeekFormCalendar(calendar)}"
        rosterUI()
    }

    override fun onMonthChange(year: Int, month: Int) {
        binding.date.text = "${year}年${month}月"
        viewModel.getPersonalRoster("${year}-${String.format("%02d", month)}")
    }

//    override fun onCalendarIntercept(calendar: Calendar): Boolean {
//        val compare = CalendarUtil.compareTo(calendar.year, calendar.month, calendar.day, binding.calendarView.curYear, binding.calendarView.curMonth, binding.calendarView.curDay)
//        return compare > 0
//    }

//    override fun onCalendarInterceptClick(calendar: Calendar?, isClick: Boolean) {
//        ToastUtils.showShortSafe("选择时间大于当前时间")
//    }



    fun getWeekFormCalendar(calendar: Calendar):String{
        return when(calendar.week){
            0 -> "星期日"
            1 -> "星期一"
            2 -> "星期二"
            3 -> "星期三"
            4 -> "星期四"
            5 -> "星期五"
            6 -> "星期六"
            else -> ""
        }
    }


    private fun calendarUI(list:List<PersonalRoster>){
        val map: MutableMap<String, Calendar> = HashMap()
        list.filter { !it.workStatus }.forEach {
            val date = it.calendarDay.split("-")
            val scheme =  getSchemeCalendar(date[0].toInt(),date[1].toInt(),date[2].toInt(),resources.getColor(R.color.color_59abff),"休")
            map[scheme.toString()] = scheme
        }
        binding.calendarView.setSchemeDate(map)
    }

    private fun rosterUI(){
        val list = viewModel.personalRosterEvent.value
        if (list.isNullOrEmpty()){
            LoadSirUtil.postCallback(loadService!!,RosterNotJoinedCallback::class.java)
            return
        }

        val today = list.find { it.calendarDay == "${selectCalendar.year}-${String.format("%02d", selectCalendar.month)}-${selectCalendar.day}" }
        if(today == null){
            LoadSirUtil.postCallback(loadService!!,RosterNotJoinedCallback::class.java)
            return
        }

        LoadSirUtil.postCallback(loadService!!,SuccessCallback::class.java)

        binding.jobRule.text= "轮班规则：${today.ruleName}    班次：${today.scheduleName}"

        if(today.workStatus){
            // 上班
            if(today.segments.size > 1){
                binding.startWorkTxt.text = SimplifySpanBuild()
                    .append(SpecialTextUnit("第一次").setTextColor(resources.getColor(R.color.color_333333)))
                    .build()
                binding.startWork.text = "上班  ${today.segments[0].startTime}                下班  ${today.segments[0].endTime}"

                binding.offWorkTxt.text = SimplifySpanBuild()
                    .append(SpecialTextUnit("第二次").setTextColor(resources.getColor(R.color.color_333333)))
                    .build()
                binding.offWork.text = "上班  ${today.segments[1].startTime}                下班  ${today.segments[1].endTime}"
            }else{
                binding.startWorkTxt.text = SimplifySpanBuild()
                    .append(SpecialTextUnit("上班").setTextColor(resources.getColor(R.color.color_59abff)))
                    .build()
                binding.startWork.text = today.segments[0].startTime

                binding.offWorkTxt.text = SimplifySpanBuild()
                    .append(SpecialTextUnit("下班").setTextColor(resources.getColor(R.color.color_59abff)))
                    .build()
                binding.offWork.text = today.segments[0].endTime
            }
        }else{
            // 休息
            binding.startWorkTxt.text = SimplifySpanBuild()
                .append(SpecialTextUnit("上班").setTextColor(resources.getColor(R.color.color_59abff)))
                .build()
            binding.startWork.text = "当日无排班"

            binding.offWorkTxt.text = SimplifySpanBuild()
                .append(SpecialTextUnit("下班").setTextColor(resources.getColor(R.color.color_59abff)))
                .build()
            binding.offWork.text = "当日无排班"
        }
    }

        private fun getSchemeCalendar(year:Int,month:Int,day:Int,color:Int,text:String):Calendar {
        val calendar = Calendar()
            calendar.year = year
            calendar.month = month
            calendar.day = day
            calendar.schemeColor = color
            calendar.scheme = text
        return calendar
    }

}