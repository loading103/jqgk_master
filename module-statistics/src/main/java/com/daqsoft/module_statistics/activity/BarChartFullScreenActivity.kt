package com.daqsoft.module_statistics.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.LoadSirUtil
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.library_common.bean.Options
import com.daqsoft.module_statistics.BR
import com.daqsoft.module_statistics.R
import com.daqsoft.module_statistics.adapter.MyLegendAdapter
import com.daqsoft.module_statistics.databinding.ActivityBarChartFullScreenBinding
import com.daqsoft.module_statistics.repository.pojo.vo.*
import com.daqsoft.module_statistics.viewmodel.BarChartFullScreenViewModel
import com.daqsoft.module_statistics.widget.TimePick
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.haibin.calendarview.Calendar
import com.kingja.loadsir.callback.Callback
import com.kingja.loadsir.core.LoadSir
import dagger.hilt.android.AndroidEntryPoint
import me.tatarka.bindingcollectionadapter2.ItemBinding
import timber.log.Timber

/**
 * @package name：com.daqsoft.module_statistics.activity
 * @date 17/6/2021 上午 9:19
 * @author zp
 * @describe  柱状图 全屏模式
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Statistics.PAGER_BAR_CHART_FULL_SCREEN)
class BarChartFullScreenActivity : AppBaseActivity<ActivityBarChartFullScreenBinding, BarChartFullScreenViewModel>() {

    @JvmField
    @Autowired
    var from : String = ""


    val barChartLegendAdapter : MyLegendAdapter by lazy {
        MyLegendAdapter().apply {
            itemBinding = ItemBinding.of(ItemBinding.VAR_NONE, R.layout.recycleview_legend_item_warp)
            setItems(arrayListOf())
        }
    }


    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_bar_chart_full_screen
    }

    override fun initViewModel(): BarChartFullScreenViewModel? {
        return viewModels<BarChartFullScreenViewModel>().value
    }

    override fun initView() {
        super.initView()
        initRefresh()
        initLoadService()
        initTimePick()
        initBarChart()
        initBarChartLegend()
        initOnClick()
        initTitle()


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

        viewModel.ticketTypeEvent.observe(this, Observer {
            setTicketTypeBarChartData(it)
        })

        viewModel.attractionPreferenceEvent.observe(this, Observer {
            setPassengerFlowAttractionPreferenceBarChartData(it)
        })

        viewModel.vehicleSourceEvent.observe(this, Observer {
            setVehicleSourceBarChartData(it)
        })

        viewModel.vehicleParkingLotEvent.observe(this, Observer {
            setVehicleParkingLotBarChartData(it)
        })
    }

    private fun initTitle() {
        when(from){
            ARouterPath.Statistics.PAGER_TICKET_TYPE ->{
                binding.chartTitle.text = "单票种门票销售量TOP10"
            }
            ARouterPath.Statistics.PAGER_PASSENGER_FLOW_ATTRACTION_PREFERENCE ->{
                binding.chartTitle.text = "景区偏好度分析TOP10"
            }
            ARouterPath.Statistics.PAGER_VEHICLE_SOURCE ->{
                binding.chartTitle.text = "景区车辆来源地排名TOP10"
            }
            ARouterPath.Statistics.PAGER_VEHICLE_PARKING_LOT ->{
                binding.chartTitle.text = "景区停车场车辆分析TOP10"
            }
        }

    }

    private fun initRefresh() {
        binding.refresh.setOnRefreshListener{
            initData()
        }
    }


    private fun initOnClick() {
        binding.close.setOnClickListenerThrottleFirst{
            finish()
        }
    }

    private fun initLoadService() {
        loadService = LoadSir.getDefault().register(binding.refresh, Callback.OnReloadListener {
            LoadSirUtil.postLoading(loadService!!)
            initData()
        })
        LoadSirUtil.postLoading(loadService!!)
    }

    private fun initTimePick() {
        binding.timePick.setType(this,day = true, month = true, quarterly = true, year = true)
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


                when(from){
                    ARouterPath.Statistics.PAGER_TICKET_TYPE ->{
                        viewModel.getTicketType(timeType, statisticsTime, endTime, quarter)
                    }
                    ARouterPath.Statistics.PAGER_PASSENGER_FLOW_ATTRACTION_PREFERENCE ->{
                        viewModel.getPassengerFlowAttractionPreference(timeType, statisticsTime, endTime, quarter, null, null)
                    }
                    ARouterPath.Statistics.PAGER_VEHICLE_SOURCE ->{
                        viewModel.getVehicleSource(timeType, statisticsTime, endTime, quarter, 0.toString(), null)
                    }
                    ARouterPath.Statistics.PAGER_VEHICLE_PARKING_LOT ->{
                        viewModel.getVehicleParkingLot(timeType, statisticsTime, endTime, quarter, null, null)
                    }
                }

            }

        })
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

            setScaleMinima(1.15f, 1.0f)

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

            setNoDataText("暂无数据")
        }
    }


    private fun setTicketTypeBarChartData(ticketType: TicketType) {

        if (ticketType.ticketNameData.isNullOrEmpty()){
            binding.barChart.setData(null)
            binding.barChart.invalidate()
            barChartLegendAdapter.setItems(arrayListOf())
            return
        }

        var data = ticketType.ticketNameData
        if (ticketType.ticketNameData.size>10){
            data = ticketType.ticketNameData.subList(0,10)
        }

        val current: MutableList<BarEntry> = ArrayList()
        val y2y	: MutableList<BarEntry> = ArrayList()
        val annulus: MutableList<BarEntry> = ArrayList()
        data.forEachIndexed { index, ticketType ->
            current.add(BarEntry(index.toFloat(), ticketType.totalQuantity.toFloat(), ticketType.ticketType))
            y2y.add(BarEntry(index.toFloat(), ticketType.y2yTotalQuantity.toFloat(),ticketType.ticketType))
            annulus.add(BarEntry(index.toFloat(), ticketType.annulusTotalQuantity.toFloat(),ticketType.ticketType))
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


    private fun setPassengerFlowAttractionPreferenceBarChartData(passengerFlowAttractionPreference: PassengerFlowAttractionPreference) {

        if (passengerFlowAttractionPreference.items.isNullOrEmpty()){
            binding.barChart.setData(null)
            binding.barChart.invalidate()
            barChartLegendAdapter.setItems(arrayListOf())
            return
        }

        var data = passengerFlowAttractionPreference.items
        if (passengerFlowAttractionPreference.items.size>10){
            data = passengerFlowAttractionPreference.items.subList(0,10)
        }

        val current: MutableList<BarEntry> = ArrayList()
        val y2y	: MutableList<BarEntry> = ArrayList()
        val annulus: MutableList<BarEntry> = ArrayList()
        data.forEachIndexed { index, attraction ->
            current.add(BarEntry(index.toFloat(), attraction.currentNum.toFloat(), attraction.spotName))
            y2y.add(BarEntry(index.toFloat(), attraction.yearOnYearNum.toFloat(),attraction.spotName))
            annulus.add(BarEntry(index.toFloat(), attraction.monthOnMonthNum.toFloat(),attraction.spotName))
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

    private fun setVehicleSourceBarChartData(vehicleSource : List<VehicleSource>){
        if (vehicleSource.isNullOrEmpty()){
            binding.barChart.setData(null)
            binding.barChart.invalidate()
            barChartLegendAdapter.setItems(arrayListOf())
            return
        }

        var data = vehicleSource
        if (vehicleSource.size>4){
            data = vehicleSource.subList(0,4)
        }

        val current: MutableList<BarEntry> = java.util.ArrayList()
        val y2y	: MutableList<BarEntry> = java.util.ArrayList()
        val annulus: MutableList<BarEntry> = java.util.ArrayList()

        data.forEachIndexed { index, vehicleSource ->
            current.add(BarEntry(index.toFloat(), vehicleSource.count.toFloat(), vehicleSource.addr))
            y2y.add(BarEntry(index.toFloat(), if (vehicleSource.line.isNullOrBlank()) 0f else vehicleSource.line.toFloat(),vehicleSource.addr))
            annulus.add(BarEntry(index.toFloat(), if (vehicleSource.cycle.isNullOrBlank()) 0f else vehicleSource.cycle.toFloat(),vehicleSource.addr))
        }

        val groupSpace = 0.46f
        val barSpace = 0.08f
        val barWidth = 0.1f
        // (0.08 + 0.1) * 3 + 0.46 = 1.00 -> interval per "group"

        val groupCount : Int = current.size
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

    private fun setVehicleParkingLotBarChartData(vehicleParkingLot : VehicleParkingLot){
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

        val current: MutableList<BarEntry> = java.util.ArrayList()
        val y2y	: MutableList<BarEntry> = java.util.ArrayList()
        val annulus: MutableList<BarEntry> = java.util.ArrayList()
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
            layoutManager = LinearLayoutManager(this@BarChartFullScreenActivity, RecyclerView.HORIZONTAL,false)
            adapter = barChartLegendAdapter
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        binding.timePick.onDestroy()
    }
}