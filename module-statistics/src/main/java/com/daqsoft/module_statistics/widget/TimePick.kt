package com.daqsoft.module_statistics.widget

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.databinding.DataBindingUtil
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.blankj.utilcode.util.ScreenUtils
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.library_common.bean.Options
import com.daqsoft.library_common.widget.BoldPagerTitleView
import com.daqsoft.library_common.widget.OptionsPopup
import com.daqsoft.library_common.widget.RangePopup
import com.daqsoft.module_statistics.R
import com.daqsoft.module_statistics.databinding.LayoutTimePickBinding
import com.daqsoft.mvvmfoundation.utils.dp
import com.haibin.calendarview.Calendar
import com.lxj.xpopup.XPopup
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.WrapPagerIndicator

/**
 * @package name：com.daqsoft.module_statistics.widget
 * @date 10/6/2021 上午 10:50
 * @author zp
 * @describe
 */
class TimePick : FrameLayout {

    var activity: Activity? = null

    enum class Type(val label: String, val value: String) {
        DAY("日", "DAY"),
        MONTH("月", "MONTH"),
        QUARTERLY("季度", "QUARTER"),
        YEAR("年", "YEAR")
    }

    private var pagerTitles = mutableListOf<Type>()

    // 当前类型
    private var currentType: Type = Type.DAY
    private var currentIndex = 0

    // 屏幕的宽
    private var screenWidth = ScreenUtils.getScreenWidth()

    lateinit var binding: LayoutTimePickBinding

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    private fun initView() {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.layout_time_pick,
            this,
            false
        )
        addView(binding.root)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthModel = MeasureSpec.getMode(widthMeasureSpec)
        val heightModel = MeasureSpec.getMode(heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val width = if (widthModel == MeasureSpec.EXACTLY) {
            widthSize
        } else {
            screenWidth
        }
        val height = if (heightModel == MeasureSpec.EXACTLY) {
            heightSize
        } else {
            44.dp
        }
        setMeasuredDimension(width, height)
    }


    /**
     * 设置日期
     * @param day Boolean
     * @param month Boolean
     * @param quarterly Boolean
     * @param year Boolean
     */
    fun setType(
        activity: Activity,
        day: Boolean,
        month: Boolean,
        quarterly: Boolean,
        year: Boolean,
        listener: OnClickListener?=null
    ) {
        this.activity = activity
        this.onClickListener = listener
        if (day) {
            pagerTitles.add(Type.DAY)
        }
        if (month) {
            pagerTitles.add(Type.MONTH)
        }
        if (quarterly) {
            pagerTitles.add(Type.QUARTERLY)
        }
        if (year) {
            pagerTitles.add(Type.YEAR)
        }

        initIndicator()

        initPick()
    }

    private fun initIndicator() {
        val commonNavigator = CommonNavigator(activity)
        commonNavigator.isAdjustMode = true
        if (pagerTitles.size == 1){
            commonNavigator.isAdjustMode = false
            commonNavigator.leftPadding = 20.dp
            commonNavigator.rightPadding = 20.dp
        }

        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return pagerTitles.size
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val simplePagerTitleView = BoldPagerTitleView(context)
                simplePagerTitleView.text = pagerTitles[index].label
                simplePagerTitleView.textSize = 14f
                simplePagerTitleView.normalColor = resources.getColor(R.color.color_666666)
                simplePagerTitleView.selectedColor = resources.getColor(R.color.color_59abff)
                simplePagerTitleView.setOnClickListenerThrottleFirst {
                    if(index != currentIndex){
                        currentIndex = index
                        switch()
                    }
                }
                return simplePagerTitleView
            }

            override fun getIndicator(context: Context): IPagerIndicator {
                val indicator = WrapPagerIndicator(context)
                indicator.horizontalPadding = 10.dp
                indicator.verticalPadding = 6.dp
                indicator.fillColor = resources.getColor(R.color.color_1959abff)
                return indicator
            }
        }
        binding.indicator.navigator = commonNavigator
        switch()
    }

    fun switch(){
        currentType = pagerTitles[currentIndex]
        binding.indicator.onPageSelected(currentIndex)

        val calendar = java.util.Calendar.getInstance()
        val year = calendar.get(java.util.Calendar.YEAR)
        val month = calendar.get(java.util.Calendar.MONTH)+1
        val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)
        when(currentType){
            Type.DAY -> {
                val current = "${year}-${month}-${day}"
                binding.pick.text = "$current"

                val today = Calendar()
                today.year = year
                today.month = month
                today.day = day

                selectRange = mutableListOf()
                selectRange?.add(today)
                selectRange?.add(today)

                optionOne = null
                optionTwo = null

            }
            Type.MONTH -> {
                binding.pick.text = "${year}-${month}"

                selectRange = null
                optionOne = Options(year.toString(),year.toString(), arrayListOf())
                optionTwo = Options(month.toString(),month.toString(), arrayListOf())
            }
            Type.QUARTERLY -> {
                val quarterly = month / 3
                binding.pick.text = "${year}-Q${quarterly}"

                selectRange = null
                optionOne = Options(year.toString(),year.toString(), arrayListOf())
                optionTwo = Options(quarterly.toString(),quarterly.toString(), arrayListOf())
            }
            Type.YEAR -> {
                binding.pick.text = "$year"

                selectRange = null
                optionOne = Options(year.toString(),year.toString(), arrayListOf())
                optionTwo = null
            }
        }
        onClickListener?.determine(currentType,selectRange,optionOne,optionTwo)
    }

    private fun initPick() {
        binding.pick.setOnClickListenerThrottleFirst {
            when (currentType) {
                Type.DAY -> {
                    calendarPopup()
                }
                Type.MONTH -> {
                    optionsPopup(Type.MONTH)
                }
                Type.QUARTERLY -> {
                    optionsPopup(Type.QUARTERLY)
                }
                Type.YEAR -> {
                    optionsPopup(Type.YEAR)
                }
            }
        }

    }

    // 日历 popup
    var selectRange: MutableList<Calendar>? = null
    private var rangePopup: RangePopup  ? = null
    private fun calendarPopup() {
        rangePopup = XPopup
            .Builder(activity)
            .isDestroyOnDismiss(true)
            .asCustom(RangePopup(activity!!).apply {
                setOnClickListener(object : RangePopup.OnClickListener {
                    override fun determineOnClick(calendars: List<Calendar>?) {
                        this@TimePick.selectRange = calendars?.toMutableList()
                        if (!calendars.isNullOrEmpty()) {
                            val start = "${calendars.first().year}.${calendars.first().month}.${calendars.first().day}"
                            val end = "${calendars.last().year}.${calendars.last().month}.${calendars.last().day}"
                            binding.pick.text = "$start~$end"
                            onClickListener?.determine(currentType,this@TimePick.selectRange,optionOne,optionTwo)
                        }
                    }
                })
            })
            .show() as RangePopup
        rangePopup?.setSelectCalendarRange(selectRange)
    }



    var option1List: MutableList<Options> = ArrayList()
    var option2List: MutableList<MutableList<Options>> = ArrayList()
    private fun createData(type: Type) {
        option1List.clear()
        option2List.clear()
        val calendar = java.util.Calendar.getInstance()
        val year = calendar.get(java.util.Calendar.YEAR)
        val month = calendar.get(java.util.Calendar.MONTH)+1
        for (i in 1970..year) {
            val value = "${i}"
            val label = i.toString()
            val children = mutableListOf<Options>()

            when (type) {
                Type.MONTH -> {
                    for (i in 1..if(i == year) month else 12) {
                        val value = "${i}"
                        val label = i.toString()
                        val options = Options(label, value, arrayListOf<Options>())
                        children.add(options)
                    }
                }
                Type.QUARTERLY -> {
                    for (i in 1..4) {
                        val value = "Q${i}"
                        val label = i.toString()
                        val options = Options(label, value, arrayListOf<Options>())
                        children.add(options)
                    }
                }
                Type.YEAR -> {
                }
            }

            val options = Options(label, value, children)

            option1List.add(options)
            option2List.add(children)
        }
    }

    private fun format(text: String): String {
        var text = text
        for (i in 0..9) {
            text = text.replace(
                ('0'.toInt() + i).toChar(),
                "零一二三四五六七八九"[i]
            )
        }
        return text
    }


    // 选项
    var option1V: Int = 0
    var option2V: Int = 0
    var optionOne : Options? = null
    var optionTwo: Options? = null
    private var optionsPopup : OptionsPopup<Options> ? = null
    private fun optionsPopup(type: Type) {
        createData(type)
        optionsPopup = XPopup
            .Builder(activity)
            .isDestroyOnDismiss(true)
            .asCustom(OptionsPopup<Options>(activity!!).apply {
                setOnOptionsSelectListener(object : OnOptionsSelectListener {
                    override fun onOptionsSelect(
                        options1: Int,
                        options2: Int,
                        options3: Int,
                        v: View?
                    ) {

                        option1V = options1
                        option2V = options2

                        optionOne = option1List[options1]
                        val optionOneChildren  = optionOne?.children
                        if (!optionOneChildren.isNullOrEmpty()) {
                            optionTwo = optionOneChildren[options2]
                        }

                        val sb = StringBuilder()
                        if (optionOne != null){
                            sb.append(optionOne!!.label)
                        }
                        if (optionTwo != null){
                            if (sb.isNotBlank()){
                                sb.append("-")
                            }
                            sb.append(optionTwo!!.label)
                        }
                        binding.pick.text = sb.toString()
                        onClickListener?.determine(currentType,selectRange,optionOne,optionTwo)
                    }

                })
            })
            .show() as OptionsPopup<Options>

        when (type) {
            Type.MONTH -> {
                optionsPopup?.setPicker(
                    option1List,
                    option2List,
                    null
                )
            }
            Type.QUARTERLY -> {
                optionsPopup?.setPicker(
                    option1List,
                    option2List,
                    null
                )
            }
            Type.YEAR -> {
                optionsPopup?.setPicker(
                    option1List,
                    null,
                    null
                )
            }
        }

        optionOne?.let { one ->
            option1List.find { it.value == one.value }?.let {
                option1V = option1List.indexOf(it)

                optionTwo?.let { two ->
                    option1List[option1V].children?.find { it.value == two.value }?.let {
                        option2V = option1List[option1V].children?.indexOf(it)?: 0
                    }
                }
            }
        }

        optionsPopup?.setSelectOptions(option1V, option2V)
    }


    fun getCurrentType() = currentType

    fun onDestroy() {
        activity = null
    }


    private var onClickListener : OnClickListener? = null

    fun setOnClickListener(listener: OnClickListener){
        this.onClickListener = listener
    }

    interface OnClickListener{

        fun determine(type:Type,selectRange: List<Calendar>?,option1 : Options?,option2 : Options?)

    }
}