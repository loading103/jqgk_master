package com.daqsoft.module_statistics.fragment

import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.iwgang.simplifyspan.SimplifySpanBuild
import cn.iwgang.simplifyspan.unit.SpecialTextUnit
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseFragment
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.LoadSirUtil
import com.daqsoft.library_base.utils.formatGroupingUsed
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.library_common.bean.Options
import com.daqsoft.library_common.widget.BottomMenuPopup
import com.daqsoft.module_statistics.BR
import com.daqsoft.module_statistics.R
import com.daqsoft.module_statistics.adapter.MyLegendAdapter
import com.daqsoft.module_statistics.databinding.FragmentVehicleHolidayBinding
import com.daqsoft.module_statistics.repository.pojo.vo.MyLegend
import com.daqsoft.module_statistics.repository.pojo.vo.VehicleHoliday
import com.daqsoft.module_statistics.viewmodel.VehicleHolidayViewModel
import com.daqsoft.module_statistics.widget.TimePick
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.haibin.calendarview.Calendar
import com.kingja.loadsir.callback.Callback
import com.kingja.loadsir.core.LoadSir
import com.lxj.xpopup.XPopup
import dagger.hilt.android.AndroidEntryPoint
import me.tatarka.bindingcollectionadapter2.ItemBinding
import timber.log.Timber
import java.util.ArrayList

/**
 * @package name：com.daqsoft.module_statistics.fragment
 * @date 21/6/2021 下午 4:12
 * @author zp
 * @describe 车辆  节日日分析
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Statistics.PAGER_VEHICLE_HOLIDAY)
class VehicleHolidayFragment : AppBaseFragment<FragmentVehicleHolidayBinding, VehicleHolidayViewModel>() {

    val horizontalBarChartLegendAdapter : MyLegendAdapter by lazy {
        MyLegendAdapter().apply {
            itemBinding = ItemBinding.of(ItemBinding.VAR_NONE, R.layout.recycleview_legend_item_warp)
            setItems(arrayListOf())
        }
    }

    val lineChartLegendAdapter : MyLegendAdapter by lazy {
        MyLegendAdapter().apply {
            itemBinding = ItemBinding.of(ItemBinding.VAR_NONE, R.layout.recycleview_legend_item_warp)
            setItems(arrayListOf())
        }
    }

    private var bottomMenuSelectedPosition = 0
    private val bottomMenuPopup: BottomMenuPopup by lazy {
        BottomMenuPopup(requireActivity()).apply {
            setItemOnClickListener(object : BottomMenuPopup.ItemOnClickListener {
                override fun onClick(position: Int, content: String) {
                    bottomMenuSelectedPosition = position
                    binding.vehicleHoliday.text = content
                    if (position == 0){
                        binding.cardChart2.isVisible = false
                        binding.cardChart.isVisible = true
                    }else{
                        binding.cardChart2.isVisible = true
                        binding.cardChart.isVisible = false
                    }
                    binding.timePick.switch()
                }
            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = super.onCreateView(inflater, container, savedInstanceState)
        loadService = LoadSir.getDefault().register(rootView, Callback.OnReloadListener {
            LoadSirUtil.postLoading(loadService!!)
            initData()
        })
        LoadSirUtil.postLoading(loadService!!)
        return loadService!!.loadLayout
    }

    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return R.layout.fragment_vehicle_holiday
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initViewModel(): VehicleHolidayViewModel? {
        return requireActivity().viewModels<VehicleHolidayViewModel>().value
    }

    override fun initView() {
        super.initView()
        initRefresh()
        initTimePick()
        initHorizontalBarChart()
        initHorizontalBarChartLegend()
        initLineChart()
        initLineChartLegend()
        initOnClick()
    }

    override fun initData() {
        super.initData()
        binding.timePick.switch()
    }

    override fun initViewObservable() {
        super.initViewObservable()

        viewModel.refreshEvent.observe(this, Observer {
            binding.refresh.finishRefresh()
        })

        viewModel.holidayEvent.observe(this, Observer {
            bottomMenuPopup.setData(it.map { it.label }.toMutableList().apply { add(0,"全部")})
        })

        viewModel.vehicleHolidayEvent.observe(this, Observer {
            initDepict(it)
            if (bottomMenuSelectedPosition == 0){
                setHorizontalBarChartData(it)
            }else{
                setLineChartData(it)
            }

        })
    }


    private fun initOnClick() {
        binding.vehicleHoliday.setOnClickListenerThrottleFirst{
            showBottomMenuPopup()
        }
    }

    private fun initRefresh() {
        binding.refresh.setOnRefreshListener{
            initData()
        }
    }


    private fun initTimePick() {
        binding.timePick.setType(requireActivity(),day = false, month = false, quarterly = false, year = true)
        binding.timePick.setOnClickListener(object : TimePick.OnClickListener{
            override fun determine(
                type: TimePick.Type,
                selectRange: List<Calendar>?,
                option1: Options?,
                option2: Options?
            ) {
                Timber.e("timePick determine === type:${type},selectRange:${selectRange},option1:${option1},option2${option2}")
                var timeType: String? = type.value
                var statisticsTime: String? = null
                var endTime: String? = null
                var quarter: String? = null

                when (type) {
                    TimePick.Type.DAY -> {
                        if (!selectRange.isNullOrEmpty()){
                            val fitst = selectRange.first()
                            val last = selectRange.last()
                            statisticsTime = "${fitst.year}-${String.format("%02d", fitst.month)}-${String.format("%02d", fitst.day)}"
                            endTime = "${last.year}-${String.format("%02d", last.month)}-${String.format("%02d", last.day)}"
                        }
                    }
                    TimePick.Type.MONTH -> {
                        statisticsTime = option1?.value + "-" + String.format("%02d", option2?.value?.toInt())
                    }
                    TimePick.Type.QUARTERLY -> {
                        statisticsTime = option1?.value
                        quarter = String.format("%02d", option2?.value?.toInt())
                    }
                    TimePick.Type.YEAR -> {
                        statisticsTime = option1?.value
                    }
                }

                viewModel.getVehicleHoliday(timeType, statisticsTime, endTime, quarter,if (bottomMenuSelectedPosition == 0) null else viewModel.holidayEvent.value!![bottomMenuSelectedPosition-1].value)
                viewModel.getHoliday(timeType,statisticsTime)
            }

        })
    }

    private fun initDepict(vehicleHoliday : VehicleHoliday){
        val rise = BitmapFactory.decodeResource(resources, R.mipmap.pwtj_icon_shangsheng)
        val drop = BitmapFactory.decodeResource(resources, R.mipmap.pwtj_icon_xiajiang)

        binding.totalCount.text = SimplifySpanBuild()
            .append(SpecialTextUnit("总车辆数\n").setTextColor(resources.getColor(R.color.color_666666)).setTextSize(14f))
            .append(SpecialTextUnit(vehicleHoliday.totalNum.formatGroupingUsed()).setTextStyle(Typeface.BOLD).setTextColor(resources.getColor(R.color.color_333333)).setTextSize(20f))
            .append(SpecialTextUnit(" 辆").setTextColor(resources.getColor(R.color.color_333333)).setTextSize(12f))
            .build()

    }

    private fun initHorizontalBarChart() {
        binding.horizontalBarChart.apply {
            // 偏移
            setExtraOffsets(0f, 0f, 0f, 0f)
            // 右下角描述
            getDescription().setEnabled(false)
            // 图例
            val l: Legend = getLegend()
            l.isEnabled = false
            // 条形阴影
            setDrawBarShadow(false)
            // 条形末端展示值
            setDrawValueAboveBar(true)
            // 最大可见条目
            setMaxVisibleValueCount(60)
            // 双手缩放
            setPinchZoom(true)
            // 网格背景
            setDrawGridBackground(false)

            val xl: XAxis = getXAxis()
            xl.position = XAxis.XAxisPosition.BOTTOM
            xl.setCenterAxisLabels(true)
            xl.setDrawAxisLine(true)
            xl.setAxisLineColor(resources.getColor(R.color.color_dedede))
            xl.setDrawGridLines(false)
            xl.setAvoidFirstLastClipping(true)
            xl.granularity = 1f
            xl.setTextSize(12f)
            xl.setTextColor(resources.getColor(R.color.color_999999))

            val yl: YAxis = getAxisLeft()
            yl.setDrawAxisLine(false)
            yl.setDrawGridLines(false)
            yl.setDrawLabels(false)
            yl.setAxisMinimum(0f)


            val yr: YAxis = getAxisRight()
            yr.setDrawAxisLine(true)
            yr.setAxisLineColor(resources.getColor(R.color.color_dedede))
            yr.setDrawGridLines(true)
            yr.setGridColor(resources.getColor(R.color.color_dedede))
            yr.setAxisMinimum(0f)
            yr.setTextSize(12f)
            yr.setTextColor(resources.getColor(R.color.color_999999))

            setFitBars(true)

            setNoDataText("暂无数据")
        }

    }


    private fun setHorizontalBarChartData(vehicleHoliday : VehicleHoliday) {

        if (vehicleHoliday.items.isNullOrEmpty()){
            binding.horizontalBarChart.setData(null)
            binding.horizontalBarChart.invalidate()
            horizontalBarChartLegendAdapter.setItems(arrayListOf())
            return
        }

        val current: MutableList<BarEntry> = ArrayList()
        val y2y	: MutableList<BarEntry> = ArrayList()

        vehicleHoliday.items.asReversed().forEachIndexed { index, holiday ->
            current.add(BarEntry(index.toFloat(), holiday.totalNum.toFloat(), holiday.holiday))
            y2y.add(BarEntry(index.toFloat(), holiday.yearOnYearNum.toFloat(),holiday.holiday))
        }


        val groupSpace = 0.28f
        val barSpace = 0.08f
        val barWidth = 0.28f
        // (0.28f + 0.08f) * 2 + 0.28 = 1.00 -> interval per "group"

        val groupCount : Int = vehicleHoliday.items.size
        val start = 0
        val end = start + groupCount


        val currentBarDataSet = BarDataSet(current, "本${binding.timePick.getCurrentType().label}")
        // 绘制 icon
        currentBarDataSet.setDrawIcons(false)
        // item 颜色
        currentBarDataSet.setColors(resources.getColor(R.color.color_ff9d46))
        // 是否在图上显示数值
        currentBarDataSet.setDrawValues(true)
        // 标签文字的大小
        currentBarDataSet.setValueTextSize(10f)
        // 标签文字的颜色
        currentBarDataSet.setValueTextColor(resources.getColor(R.color.color_59abff))

        val y2yBarDataSet = BarDataSet(y2y, "同比")
        // 绘制 icon
        y2yBarDataSet.setDrawIcons(false)
        // item 颜色
        y2yBarDataSet.setColors(resources.getColor(R.color.color_27cb8c))
        // 是否在图上显示数值
        y2yBarDataSet.setDrawValues(true)
        // 标签文字的大小
        y2yBarDataSet.setValueTextSize(10f)
        // 标签文字的颜色
        y2yBarDataSet.setValueTextColor(resources.getColor(R.color.color_59abff))


        val barData = BarData(currentBarDataSet,y2yBarDataSet)
        barData.setValueTextSize(10f)
        barData.setDrawValues(true)
        barData.setBarWidth(barWidth)
        barData.setValueFormatter( object : ValueFormatter(){
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        })

        binding.horizontalBarChart.setData(barData)
        binding.horizontalBarChart.highlightValues(null)
        val xl: XAxis =  binding.horizontalBarChart.getXAxis()
        xl.setAxisMinimum(start.toFloat())
        xl.setAxisMaximum(end.toFloat())
        xl.valueFormatter = object : ValueFormatter(){
            override fun getFormattedValue(value: Float): String {
                return  try {
                    current[value.toInt()].data.toString()
                }catch (e : Exception){
                    ""
                }
            }
        }

        binding.horizontalBarChart.groupBars(start.toFloat(), groupSpace, barSpace)
        binding.horizontalBarChart.invalidate()

        // 图例
        val legendList = barData.dataSets.map {
            MyLegend(it.label,"",it.getColor(),"")
        }
        horizontalBarChartLegendAdapter.setItems(legendList)

    }


    private fun initHorizontalBarChartLegend() {
        binding.legend.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL,false)
            adapter = horizontalBarChartLegendAdapter
        }
    }


    private fun initLineChart() {
        binding.lineChart.apply {
            // 偏移
            setExtraOffsets(0f, 0f, 0f, 10f)
            // 右下角描述
            getDescription().setEnabled(false)
            // 图例
            val l: Legend = getLegend()
            l.isEnabled = false
            // 双手缩放
            setPinchZoom(true)
            // 网格背景
            setDrawGridBackground(false)
            // 最大可见条目
            setMaxVisibleValueCount(60)

            val xl: XAxis = getXAxis()
            xl.position = XAxis.XAxisPosition.BOTTOM
            xl.setAxisMinimum(0f)
            xl.setDrawAxisLine(true)
            xl.setAxisLineColor(resources.getColor(R.color.color_dedede))
            xl.setDrawGridLines(false)
            xl.setAvoidFirstLastClipping(true)
            xl.granularity = 1f
            xl.setTextSize(12f)
            xl.setTextColor(resources.getColor(R.color.color_999999))

            val yl: YAxis = getAxisLeft()
            yl.setDrawAxisLine(true)
            yl.setAxisLineColor(resources.getColor(R.color.color_dedede))
            yl.setDrawGridLines(true)
            yl.setGridColor(resources.getColor(R.color.color_dedede))
            yl.setAxisMinimum(0f)
            yl.setTextSize(12f)
            yl.setTextColor(resources.getColor(R.color.color_999999))

            val yr: YAxis = getAxisRight()
            yr.setDrawAxisLine(false)
            yr.setDrawGridLines(false)
            yr.setDrawLabels(false)
            yr.setAxisMinimum(0f)

            setNoDataText("暂无数据")

        }

    }


    private fun setLineChartData(vehicleHoliday : VehicleHoliday) {

        if (vehicleHoliday.items.isNullOrEmpty()){
            binding.lineChart.setData(null)
            binding.lineChart.invalidate()
            lineChartLegendAdapter.setItems(arrayListOf())
            return
        }

        val current: MutableList<Entry> = ArrayList()
        val y2y	: MutableList<Entry> = ArrayList()
        vehicleHoliday.items.forEachIndexed { index, holiday ->
            current.add(Entry(index.toFloat(), holiday.totalNum.toFloat(),holiday.time))
            y2y.add(Entry(index.toFloat(), holiday.yearOnYearNum.toFloat(),holiday.time))
        }

        val currentLineDataSet = LineDataSet(current, "本${binding.timePick.getCurrentType().label}")
        currentLineDataSet.setColor(resources.getColor(R.color.color_268fff))
        currentLineDataSet.setLineWidth(1f)
        currentLineDataSet.setDrawFilled(false)
        currentLineDataSet.setDrawCircleHole(false)
        currentLineDataSet.setDrawValues(false)
        currentLineDataSet.setDrawCircles(false)
        currentLineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER)

        val y2yLineDataSet = LineDataSet(y2y, "同比")
        y2yLineDataSet.setColor(resources.getColor(R.color.color_ff9d46))
        y2yLineDataSet.setLineWidth(1f)
        y2yLineDataSet.setDrawFilled(false)
        y2yLineDataSet.setDrawCircleHole(false)
        y2yLineDataSet.setDrawCircles(false)
        y2yLineDataSet.setDrawValues(false)
        y2yLineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER)


        val lineData =  LineData(currentLineDataSet, y2yLineDataSet)
        binding.lineChart.setData(lineData)
        binding.lineChart.highlightValues(null)
        val xl: XAxis =  binding.lineChart.getXAxis()
        xl.valueFormatter = object : ValueFormatter(){
            override fun getFormattedValue(value: Float): String {
                return try {
                    current[value.toInt()].getData().toString()
                }catch (e : Exception){
                    ""
                }
            }
        }
        binding.lineChart.invalidate()


        // 图例
        val legendList = lineData.dataSets.map {
            MyLegend(it.label,"",it.getColor(),"")
        }
        lineChartLegendAdapter.setItems(legendList)

    }

    private fun initLineChartLegend() {
        binding.legend2.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL,false)
            adapter = lineChartLegendAdapter
        }
    }


    /**
     * 展示底部菜单popup
     */
    fun showBottomMenuPopup() {
        XPopup
            .Builder(requireActivity())
            .asCustom(bottomMenuPopup)
            .show()
        bottomMenuPopup.setSelectedPosition(bottomMenuSelectedPosition)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.timePick.onDestroy()
    }
}


