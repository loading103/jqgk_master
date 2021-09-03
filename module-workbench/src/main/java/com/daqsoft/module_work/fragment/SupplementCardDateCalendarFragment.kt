package com.daqsoft.module_work.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import com.daqsoft.library_base.base.AppBaseFragment
import com.daqsoft.module_work.BR
import com.daqsoft.module_work.R
import com.daqsoft.module_work.databinding.FragmentSupplementCardDateCalendarBinding
import com.daqsoft.module_work.viewmodel.SupplementCardDateCalendarViewModel
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarUtil
import com.haibin.calendarview.CalendarView
import dagger.hilt.android.AndroidEntryPoint
import javax.xml.transform.ErrorListener

/**
 * @package name：com.daqsoft.module_work.fragment
 * @date 8/7/2021 下午 2:24
 * @author zp
 * @describe  补卡日期选择 日历
 */
@AndroidEntryPoint
class SupplementCardDateCalendarFragment : AppBaseFragment<FragmentSupplementCardDateCalendarBinding, SupplementCardDateCalendarViewModel>(),
    CalendarView.OnCalendarSelectListener,
    CalendarView.OnCalendarInterceptListener
{

    companion object{

        fun newInstance():SupplementCardDateCalendarFragment {
            val args = Bundle()
            val fragment = SupplementCardDateCalendarFragment()
            fragment.arguments = args
            return fragment
        }
    }


    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return R.layout.fragment_supplement_card_date_calendar
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initViewModel(): SupplementCardDateCalendarViewModel? {
        return requireActivity().viewModels<SupplementCardDateCalendarViewModel>().value
    }

    override fun initView() {
        super.initView()

        initCalendar()

    }

    private fun initCalendar() {

        binding.calendarView.setOnCalendarSelectListener(this)
        binding.calendarView.setOnCalendarInterceptListener(this)
        binding.calendarView.setRange(
            1970, 1, 1,
            binding.calendarView.curYear, binding.calendarView.curMonth, binding.calendarView.curDay
        )
        binding.calendarView.post(Runnable { binding.calendarView.scrollToCurrent() })
    }

    override fun onCalendarOutOfRange(calendar: Calendar?) {

    }

    override fun onCalendarSelect(calendar: Calendar, isClick: Boolean) {
        onCalendarSelectListener?.onCalendarSelect(calendar)
    }

    override fun onCalendarIntercept(calendar: Calendar): Boolean {
        val max = CalendarUtil.compareTo(calendar.year, calendar.month, calendar.day, binding.calendarView.curYear, binding.calendarView.curMonth, binding.calendarView.curDay)
        val before = java.util.Calendar.getInstance()
        before.add(java.util.Calendar.DATE,-5)
        val min = CalendarUtil.compareTo(calendar.year, calendar.month, calendar.day, before.get(java.util.Calendar.YEAR), before.get(java.util.Calendar.MONTH)+1, before.get(java.util.Calendar.DAY_OF_MONTH))
        return max > 0 || min < 0
    }

    override fun onCalendarInterceptClick(calendar: Calendar?, isClick: Boolean) {
        ToastUtils.showShortSafe("超出可选时间范围")
    }


    private var onCalendarSelectListener : OnCalendarSelectListener ? = null

    fun setOnCalendarSelectListener(listener: OnCalendarSelectListener){
        this.onCalendarSelectListener = listener
    }

    interface OnCalendarSelectListener{
        fun onCalendarSelect(calendar: Calendar)
    }

}