package com.daqsoft.module_work.widget


import android.content.Context
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.daqsoft.library_base.base.BaseFragmentPagerAdapter
import com.daqsoft.library_base.utils.ViewPager2BindIndicatorHelper
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.library_common.bean.Employee
import com.daqsoft.module_work.R
import com.daqsoft.module_work.fragment.SupplementCardDateCalendarFragment
import com.daqsoft.module_work.fragment.SupplementCardDateShiftFragment
import com.daqsoft.mvvmfoundation.utils.dp
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.util.XPopupUtils
import net.lucode.hackware.magicindicator.MagicIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView
import java.util.*

/**
 * @package name：com.daqsoft.module_work.widget
 * @date 8/7/2021 上午 10:27
 * @author zp
 * @describe
 */
class SupplementCardDatePopup : BottomPopupView ,
    SupplementCardDateCalendarFragment.OnCalendarSelectListener,
    SupplementCardDateShiftFragment.OnShiftSelectListener
{

    constructor(context: Context) : super(context)

    private var determine : TextView ? = null

    private var indicator : MagicIndicator ? = null
    private var navigator : CommonNavigator ? = null

    private var viewPager2 : ViewPager2 ? = null

    private var initSelected = 0

    private var calendar = SupplementCardDateCalendarFragment.newInstance().apply {
        setOnCalendarSelectListener(this@SupplementCardDatePopup)
    }
    private var shift = SupplementCardDateShiftFragment.newInstance().apply {
        setOnCalendarSelectListener(this@SupplementCardDatePopup)
    }
    private val fragmentList = mutableListOf<Fragment>(calendar,shift)

    private val today = Calendar.getInstance()
    private val year = today.get(Calendar.YEAR)
    private val month = today.get(Calendar.MONTH)+1
    private val day = today.get(Calendar.DAY_OF_MONTH)
    private var pagerTitles = arrayListOf<String>("${year}年${month}月${day}日","班次")

    private var selectedCalendar : com.haibin.calendarview.Calendar ? = null
    private var selectedShift : String ? = null

    override fun getImplLayoutId(): Int {
        return R.layout.layout_popup_supplement_card_date
    }

    override fun onCreate() {
        super.onCreate()

        determine = findViewById<TextView>(R.id.determine)
        determine?.setOnClickListenerThrottleFirst {
            clickListener?.determine(selectedCalendar,selectedShift)
            dismiss()
        }

        indicator = findViewById<MagicIndicator>(R.id.indicator)
        viewPager2 = findViewById<ViewPager2>(R.id.view_pager2)
        initIndicator()
        initViewPager2()

    }

    override fun getMaxHeight(): Int {
        return (XPopupUtils.getScreenHeight(context)*0.8f).toInt()
    }

    private fun initIndicator() {

        navigator = CommonNavigator(context)
        navigator?.isAdjustMode = false
        navigator?.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return pagerTitles.size
            }
            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val simplePagerTitleView = SimplePagerTitleView(context)
                simplePagerTitleView.text = pagerTitles[index]
                simplePagerTitleView.textSize = 14f
                simplePagerTitleView.normalColor = resources.getColor(R.color.color_a0a0a3)
                simplePagerTitleView.selectedColor = resources.getColor(R.color.color_69696c)
                simplePagerTitleView.setOnClickListenerThrottleFirst {
                    viewPager2?.currentItem = index
                }
                return simplePagerTitleView
            }

            override fun getIndicator(context: Context): IPagerIndicator {
                val linePagerIndicator = LinePagerIndicator(context)
                linePagerIndicator.mode = LinePagerIndicator.MODE_WRAP_CONTENT
                linePagerIndicator.setColors(resources.getColor(R.color.color_69696c))
                linePagerIndicator.setLineHeight(2.dp.toFloat())
                return linePagerIndicator
            }
        }
        indicator?.navigator = navigator
        ViewPager2BindIndicatorHelper.bind(indicator!!, viewPager2!!)
        indicator?.onPageSelected(0)
    }

    private fun initViewPager2() {

        viewPager2?.setUserInputEnabled(false);
        viewPager2?.offscreenPageLimit = 1
        viewPager2?.adapter = BaseFragmentPagerAdapter((context as AppCompatActivity).supportFragmentManager, (context as AppCompatActivity).lifecycle,fragmentList)
        viewPager2?.currentItem = 0
    }


    private var clickListener : OnClickListener? = null

    fun setOnClickListener(listener: OnClickListener){
        this.clickListener = listener
    }

    interface OnClickListener{

        fun determine(calendar: com.haibin.calendarview.Calendar?,shift:String?)

    }

    override fun onCalendarSelect(calendar: com.haibin.calendarview.Calendar) {
        // 因为设置了时间范围  选中接口会回调2次 开始时间和结束时间
        if (initSelected >= 2){
            selectedCalendar = calendar
            viewPager2?.currentItem = 1
            pagerTitles = arrayListOf<String>("${calendar.year}年${calendar.month}月${calendar.day}日","班次")
            navigator?.adapter?.notifyDataSetChanged()
            shift.getShift(calendar)
        }
        initSelected++
    }

    override fun onShiftSelect(item: String) {
        selectedShift = item
        pagerTitles = arrayListOf<String>("${selectedCalendar?.year?:year}年${selectedCalendar?.month?:month}月${selectedCalendar?.day?:day}日",item)
        navigator?.adapter?.notifyDataSetChanged()
    }

}
