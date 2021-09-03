package com.daqsoft.module_statistics.fragment

import android.content.Context
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
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.base.AppBaseFragment
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.LoadSirUtil
import com.daqsoft.library_base.utils.formatGroupingUsed
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.library_common.bean.Options
import com.daqsoft.library_common.widget.BoldPagerTitleView
import com.daqsoft.library_common.widget.BottomMenuPopup
import com.daqsoft.module_statistics.BR
import com.daqsoft.module_statistics.R
import com.daqsoft.module_statistics.adapter.MyLegendAdapter
import com.daqsoft.module_statistics.databinding.FragmentVehicleParkingLotBinding
import com.daqsoft.module_statistics.repository.pojo.vo.MyLegend
import com.daqsoft.module_statistics.repository.pojo.vo.VehicleParkingLot
import com.daqsoft.module_statistics.viewmodel.VehicleParkingLotViewModel
import com.daqsoft.module_statistics.widget.TimePick
import com.daqsoft.mvvmfoundation.utils.dp
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
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.WrapPagerIndicator
import timber.log.Timber
import java.util.ArrayList

/**
 * @package name：com.daqsoft.module_statistics.fragment
 * @date 21/6/2021 下午 3:59
 * @author zp
 * @describe
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Statistics.PAGER_VEHICLE_PARKING_LOT)
class VehicleParkingLotFragment : AppBaseFragment<FragmentVehicleParkingLotBinding, VehicleParkingLotViewModel>() {

    val barChartLegendAdapter : MyLegendAdapter by lazy {
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
                    binding.parkingLot.text = content
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

            setData(arrayListOf("全部","停车场A","停车场B","停车场C","停车场D","停车场E","停车场F","停车场G","停车场H","停车场I","停车场J"))
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
        return R.layout.fragment_vehicle_parking_lot
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initViewModel(): VehicleParkingLotViewModel? {
        return requireActivity().viewModels<VehicleParkingLotViewModel>().value
    }

    override fun initView() {
        super.initView()
        initRefresh()
        initTimePick()
        initBarChart()
        initBarChartLegend()
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

        viewModel.parkingLotEvent.observe(this, Observer {
            bottomMenuPopup.setData(it.map { it.value }.toMutableList().apply { add(0,"全部")})
        })

        viewModel.vehicleParkingLotEvent.observe(this, Observer {
            initDepict(it)
            if (bottomMenuSelectedPosition == 0){
                setBarChartData(it)
            }else{
                setLineChartData(it)
            }
        })

    }

    private fun initOnClick() {
        binding.fullScreen.setOnClickListenerThrottleFirst{
            binding.fullScreen.setOnClickListenerThrottleFirst{
                ARouter
                    .getInstance()
                    .build(ARouterPath.Statistics.PAGER_BAR_CHART_FULL_SCREEN)
                    .withString("from",ARouterPath.Statistics.PAGER_VEHICLE_PARKING_LOT)
                    .navigation()
            }
        }

        binding.parkingLot.setOnClickListenerThrottleFirst{
            showBottomMenuPopup()
        }
    }

    private fun initRefresh() {
        binding.refresh.setOnRefreshListener{
            initData()
        }
    }

    private fun initTimePick() {
        binding.timePick.setType(requireActivity(),day = true, month = true, quarterly = true, year = true)
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

                viewModel.getVehicleParkingLot(timeType, statisticsTime, endTime, quarter,if (bottomMenuSelectedPosition == 0) null else bottomMenuPopup.getData()[bottomMenuSelectedPosition],null)
            }

        })
    }

    private fun initDepict(vehicleParkingLot : VehicleParkingLot){
        binding.totalCount.text = SimplifySpanBuild()
            .append(SpecialTextUnit("总车辆数\n").setTextColor(resources.getColor(R.color.color_666666)).setTextSize(14f))
            .append(
                SpecialTextUnit(((Math.random()*100)*100).toLong().formatGroupingUsed()).setTextStyle(
                    Typeface.BOLD).setTextColor(resources.getColor(R.color.color_333333)).setTextSize(20f))
            .append(SpecialTextUnit(" 辆").setTextColor(resources.getColor(R.color.color_333333)).setTextSize(12f))
            .build()
    }

    private fun initBarChart() {
        binding.barChart.apply {
            // 偏移
            setExtraOffsets(0f, 0f, 0f, 10f)
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

            setFitBars(true)

            setNoDataText("暂无数据")
        }
    }


    private fun setBarChartData(vehicleParkingLot : VehicleParkingLot) {

        if (vehicleParkingLot.items.isNullOrEmpty()){
            binding.barChart.setData(null)
            binding.barChart.invalidate()
            barChartLegendAdapter.setItems(arrayListOf())
            return
        }

        var data = vehicleParkingLot.items
        if (vehicleParkingLot.items.size>4){
            data = vehicleParkingLot.items.subList(0,4)
        }

        val current: MutableList<BarEntry> = ArrayList()
        val y2y	: MutableList<BarEntry> = ArrayList()
        val annulus: MutableList<BarEntry> = ArrayList()
        data.forEachIndexed { index, attraction ->
            current.add(BarEntry(index.toFloat(), attraction.currentNum.toFloat(), attraction.carParkName))
            y2y.add(BarEntry(index.toFloat(), attraction.yearOnYearNum.toFloat(),attraction.carParkName))
            annulus.add(BarEntry(index.toFloat(), attraction.monthOnMonthNum.toFloat(),attraction.carParkName))
        }

        val groupSpace = 0.46f
        val barSpace = 0.08f
        val barWidth = 0.1f
        // (0.08 + 0.1) * 3 + 0.46 = 1.00 -> interval per "group"

        val groupCount : Int = data.size
        val start = 0
        val end = start + groupCount

        val currentBarDataSet = BarDataSet(current, "本${binding.timePick.getCurrentType().label}")
        // 绘制 icon
        currentBarDataSet.setDrawIcons(false)
        // item 颜色
        currentBarDataSet.setColors(resources.getColor(R.color.color_268fff))
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
        y2yBarDataSet.setColors(resources.getColor(R.color.color_ff9d46))
        // 是否在图上显示数值
        y2yBarDataSet.setDrawValues(true)
        // 标签文字的大小
        y2yBarDataSet.setValueTextSize(10f)
        // 标签文字的颜色
        y2yBarDataSet.setValueTextColor(resources.getColor(R.color.color_59abff))

        val annulusBarDataSet = BarDataSet(annulus, "环比")
        // 绘制 icon
        annulusBarDataSet.setDrawIcons(false)
        // item 颜色
        annulusBarDataSet.setColors(resources.getColor(R.color.color_ff5757))
        // 是否在图上显示数值
        annulusBarDataSet.setDrawValues(true)
        // 标签文字的大小
        annulusBarDataSet.setValueTextSize(10f)
        // 标签文字的颜色
        annulusBarDataSet.setValueTextColor(resources.getColor(R.color.color_59abff))

        val barData = BarData(currentBarDataSet,y2yBarDataSet,annulusBarDataSet)
        barData.setValueTextSize(10f)
        barData.setDrawValues(true)
        barData.setBarWidth(barWidth)
        barData.setValueFormatter(object : ValueFormatter(){
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        })

        binding.barChart.setData(barData)
        binding.barChart.highlightValues(null)
        val xl: XAxis =  binding.barChart.getXAxis()
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

        binding.barChart.groupBars(start.toFloat(), groupSpace, barSpace)
        binding.barChart.invalidate()

        // 图例
        val legendList = barData.dataSets.map {
            MyLegend(it.label,"",it.getColor(),"")
        }
        barChartLegendAdapter.setItems(legendList)
    }


    private fun initBarChartLegend() {
        binding.legend.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL,false)
            adapter = barChartLegendAdapter
        }
    }

    private fun initIndicator() {
        val commonNavigator = CommonNavigator(context)
        commonNavigator.isAdjustMode = true
        val pagerTitles = arrayListOf<Pair<String,Int>>(Pair("总览",1), Pair("省内",2), Pair("省外",3))
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return pagerTitles.size
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val simplePagerTitleView = BoldPagerTitleView(context)
                simplePagerTitleView.text = pagerTitles[index].first
                simplePagerTitleView.textSize = 14f
                simplePagerTitleView.normalColor = resources.getColor(R.color.color_666666)
                simplePagerTitleView.selectedColor = resources.getColor(R.color.color_59abff)
                simplePagerTitleView.setOnClickListenerThrottleFirst {
                    binding.indicator.onPageSelected(index)
                }
                return simplePagerTitleView
            }

            override fun getIndicator(context: Context): IPagerIndicator {
                val indicator = WrapPagerIndicator(context)
                indicator.horizontalPadding = 33.dp
                indicator.verticalPadding = 6.dp
                indicator.fillColor = resources.getColor(R.color.color_e5f8ff)
                return indicator
            }
        }
        binding.indicator.navigator = commonNavigator
        binding.indicator.onPageSelected(0)
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


    private fun setLineChartData(vehicleParkingLot : VehicleParkingLot) {

        if (vehicleParkingLot.tableItems.isNullOrEmpty()){
            binding.lineChart.setData(null)
            binding.lineChart.invalidate()
            lineChartLegendAdapter.setItems(arrayListOf())
            return
        }

        val current: MutableList<Entry> = ArrayList()
        val y2y	: MutableList<Entry> = ArrayList()
        val annulus: MutableList<Entry> = ArrayList()
        vehicleParkingLot.tableItems.forEachIndexed { index, timeInterval ->
            current.add(Entry(index.toFloat(), timeInterval.currentNum.toFloat(),timeInterval.time))
            y2y.add(Entry(index.toFloat(), timeInterval.yearOnYearNum.toFloat(),timeInterval.time))
            annulus.add(Entry(index.toFloat(), timeInterval.monthOnMonthNum.toFloat(),timeInterval.time))
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

        val annulusLineDataSet = LineDataSet(annulus, "环比")
        annulusLineDataSet.setColor(resources.getColor(R.color.color_ff5757))
        annulusLineDataSet.setLineWidth(1f)
        annulusLineDataSet.setDrawFilled(false)
        annulusLineDataSet.setDrawCircleHole(false)
        annulusLineDataSet.setDrawCircles(false)
        annulusLineDataSet.setDrawValues(false)
        annulusLineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER)

        val lineData =  LineData(currentLineDataSet, y2yLineDataSet, annulusLineDataSet)
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

