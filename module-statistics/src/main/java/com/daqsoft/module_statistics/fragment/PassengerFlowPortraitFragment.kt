package com.daqsoft.module_statistics.fragment

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import cn.iwgang.simplifyspan.SimplifySpanBuild
import cn.iwgang.simplifyspan.unit.SpecialTextUnit
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseFragment
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.LoadSirUtil
import com.daqsoft.library_base.utils.formatGroupingUsed
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.library_common.bean.Options
import com.daqsoft.library_common.widget.BoldPagerTitleView
import com.daqsoft.module_statistics.BR
import com.daqsoft.module_statistics.R
import com.daqsoft.module_statistics.adapter.MyLegendAdapter
import com.daqsoft.module_statistics.databinding.FragmentPassengerFlowPortraitBinding
import com.daqsoft.module_statistics.repository.pojo.vo.MyLegend
import com.daqsoft.module_statistics.repository.pojo.vo.PassengerFlowPortrait
import com.daqsoft.module_statistics.viewmodel.PassengerFlowPortraitViewModel
import com.daqsoft.module_statistics.warrper.MyColorTemplate
import com.daqsoft.module_statistics.widget.TimePick
import com.daqsoft.mvvmfoundation.utils.dp
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.haibin.calendarview.Calendar
import com.kingja.loadsir.callback.Callback
import com.kingja.loadsir.core.LoadSir
import dagger.hilt.android.AndroidEntryPoint
import me.tatarka.bindingcollectionadapter2.ItemBinding
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.WrapPagerIndicator
import timber.log.Timber
import java.text.DecimalFormat


/**
 * @package name???com.daqsoft.module_statistics.fragment
 * @date 15/6/2021 ?????? 3:25
 * @author zp
 * @describe  ??????  ????????????
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Statistics.PAGER_PASSENGER_FLOW_PORTRAIT)
class PassengerFlowPortraitFragment : AppBaseFragment<FragmentPassengerFlowPortraitBinding, PassengerFlowPortraitViewModel>() {

    val pieCharLegendAdapter : MyLegendAdapter by lazy {
        MyLegendAdapter().apply {
            itemBinding = ItemBinding.of(ItemBinding.VAR_NONE, R.layout.recycleview_legend_item)
            setItems(arrayListOf())
        }
    }


    val pieChar2LegendAdapter : MyLegendAdapter by lazy {
        MyLegendAdapter().apply {
            itemBinding = ItemBinding.of(ItemBinding.VAR_NONE, R.layout.recycleview_legend_item)
            setItems(arrayListOf())
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
        return loadService!!.getLoadLayout()
    }

    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return R.layout.fragment_passenger_flow_portrait
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initViewModel(): PassengerFlowPortraitViewModel? {
        return requireActivity().viewModels<PassengerFlowPortraitViewModel>().value
    }

    override fun initView() {
        super.initView()
        initRefresh()
        initTimePick()
        initPieChart()
        initPieChartLegend()
        initPieChart2()
        initPieChart2Legend()
        initIndicator()
        initHorizontalBarChart()
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

        viewModel.portraitEvent.observe(this, Observer {
            initDepict(it)
            initGender(it)
            setPieChartData(it)
            setPieChart2Data(it)
            setHorizontalBarChartData(it)
        })
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
                viewModel.getPassengerFlowPortrait(timeType, statisticsTime, endTime, quarter,null)
            }

        })
    }

    private fun initDepict(passengerFlowPortrait : PassengerFlowPortrait){
        binding.totalPeople.text = SimplifySpanBuild()
            .append(SpecialTextUnit("???????????????\n").setTextColor(resources.getColor(R.color.color_666666)).setTextSize(14f))
            .append(SpecialTextUnit(passengerFlowPortrait.totalNum.formatGroupingUsed()).setTextStyle(Typeface.BOLD).setTextColor(resources.getColor(R.color.color_333333)).setTextSize(20f))
            .append(SpecialTextUnit(" ???").setTextColor(resources.getColor(R.color.color_333333)).setTextSize(12f))
            .build()
    }

    private fun initGender(passengerFlowPortrait: PassengerFlowPortrait) {
        binding.maleQuantity.text = SimplifySpanBuild()
            .append(SpecialTextUnit(passengerFlowPortrait.maleNum.formatGroupingUsed()).setTextSize(20f))
            .append(SpecialTextUnit(" ???").setTextSize(12f))
            .build()
        binding.manProgressBar.setProgress(passengerFlowPortrait.maleNum.toFloat(), passengerFlowPortrait.totalNum.toFloat())

        binding.femaleQuantity.text = SimplifySpanBuild()
            .append(SpecialTextUnit(passengerFlowPortrait.femaleNum.formatGroupingUsed()).setTextSize(20f))
            .append(SpecialTextUnit(" ???").setTextSize(12f))
            .build()
        binding.femaleProgressBar.setProgress(passengerFlowPortrait.femaleNum.toFloat(), passengerFlowPortrait.totalNum.toFloat())

    }

    private fun initPieChart() {
        binding.pieChart.apply {
            // ?????????
            setUsePercentValues(true)
            // ???????????????
            getDescription().setEnabled(false)
            // ??????
            setExtraOffsets(20f, 10f, 20f, 10f)

            // ?????????
            setDrawHoleEnabled(true)
            setHoleRadius(50f)
            setHoleColor(resources.getColor(R.color.color_1959abff))
            // ????????????
            setTransparentCircleColor(Color.WHITE)
            setTransparentCircleAlpha(255)
            setTransparentCircleRadius(60f)
            // ??????
            val l: Legend = getLegend()
            l.isEnabled = false

            setNoDataText("????????????")
        }
    }



    private fun setPieChartData(passengerFlowPortrait: PassengerFlowPortrait) {

        if (passengerFlowPortrait.typeRates.isNullOrEmpty()){
            binding.pieChart.setData(null)
            binding.pieChart.invalidate()
            pieCharLegendAdapter.setItems(arrayListOf())
            return
        }

        val entries: MutableList<PieEntry> = passengerFlowPortrait.typeRates.map {
            PieEntry(it.num.toFloat(), it.name)
        }.toMutableList()

        // ?????????
        val pieDataSet = PieDataSet(entries, "")
        // item ?????????
        pieDataSet.setAutomaticallyDisableSliceSpacing(true)
        // item ??????
        pieDataSet.setSliceSpace(0f)
        // item????????????
        pieDataSet.setSelectionShift(5f)
        // ???????????????item?????????
        pieDataSet.setUsingSliceColorAsValueLineColor(true)
        // ????????????
        pieDataSet.setValueLineWidth(2f)
        // ???????????????????????????
        val colors: MutableList<Int> = ArrayList()

        for (c in MyColorTemplate.MATERIAL_COLORS) colors.add(c)

        for (c in MyColorTemplate.VORDIPLOM_COLORS) colors.add(c)

        for (c in MyColorTemplate.JOYFUL_COLORS) colors.add(c)

        for (c in MyColorTemplate.COLORFUL_COLORS) colors.add(c)

        for (c in MyColorTemplate.LIBERTY_COLORS) colors.add(c)

        for (c in MyColorTemplate.PASTEL_COLORS) colors.add(c)

        pieDataSet.colors = colors
        // Y????????????????????????????????????
        pieDataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        // X????????????????????????????????????
        pieDataSet.xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        // ????????????????????????????????????????????????????????????
        pieDataSet.valueLinePart1Length = 0.5f
        // ?????????????????????????????????????????????????????????
        pieDataSet.valueLinePart2Length = 0.6f
        // ???????????????????????????
        pieDataSet.setDrawValues(true)
        // ?????????????????????
        pieDataSet.setValueTextSize(12f)
        // ?????????????????????
        pieDataSet.setValueTextColors(colors)

        val pieData = PieData(pieDataSet)
        pieData.setValueFormatter(object : ValueFormatter(){
            var mFormat: DecimalFormat = DecimalFormat("###,###,##0.0")
            override fun getPieLabel(value: Float, pieEntry: PieEntry?): String {
                return if (binding.pieChart.isUsePercentValuesEnabled()) {
                    "${pieEntry?.label}(${getFormattedValue(value)})"
                } else {
                    "${pieEntry?.label}(${mFormat.format(value.toDouble())})"
                }
            }

            override fun getFormattedValue(value: Float): String {
                return mFormat.format(value.toDouble()) + " %"
            }

        })

        binding.pieChart.setData(pieData)

        // ????????????
        binding.pieChart.highlightValues(null)
        binding.pieChart.invalidate()


        // ??????
        val legendList = entries.mapIndexed { index, pieEntry ->
            MyLegend(pieEntry.label,pieEntry.value.toInt().formatGroupingUsed(),colors[index],"???")
        }
        pieCharLegendAdapter.setItems(legendList)

    }

//    private fun initArcChart(){
//
//        val entries: MutableList<MyLegend> = ArrayList()
//
//        entries.add(MyLegend("??????20???",(Math.random()*10*100).toInt().toString(),requireContext().resources.getColor(R.color.color_21b87c),"???"))
//        entries.add(MyLegend("20-30???",(Math.random()*10*100).toInt().toString(),requireContext().resources.getColor(R.color.color_ffbf40),"???"))
//        entries.add(MyLegend("30-40???",(Math.random()*10*100).toInt().toString(),requireContext().resources.getColor(R.color.color_4cd8ff),"???"))
//        entries.add(MyLegend("40-60???",(Math.random()*10*100).toInt().toString(),requireContext().resources.getColor(R.color.color_8189ff),"???"))
//        entries.add(MyLegend("60?????????",(Math.random()*10*100).toInt().toString(),requireContext().resources.getColor(R.color.color_2fa7ff),"???"))
//
//        binding.arcChart.setArcChartData(entries)
//
//        // ??????
//        myLegendAdapter1.setItems(entries)
//
//    }


    private fun initPieChart2() {
        binding.pieChart2.apply {
            // ?????????
            setUsePercentValues(true)
            // ???????????????
            getDescription().setEnabled(false)
            // ??????
            setExtraOffsets(20f, 10f, 20f, 10f)

            // ?????????
            setDrawHoleEnabled(false)
//            setHoleRadius(50f)
//            setHoleColor(resources.getColor(R.color.color_1959abff))
//            // ????????????
//            setTransparentCircleColor(Color.WHITE)
//            setTransparentCircleAlpha(255)
//            setTransparentCircleRadius(60f)
            // ??????
            val l: Legend = getLegend()
            l.isEnabled = false

            setNoDataText("????????????")
        }
    }



    private fun setPieChart2Data(passengerFlowPortrait: PassengerFlowPortrait) {

        if (passengerFlowPortrait.ageRates.isNullOrEmpty()){
            binding.pieChart.setData(null)
            binding.pieChart.invalidate()
            pieCharLegendAdapter.setItems(arrayListOf())
            return
        }

        val entries: MutableList<PieEntry> = passengerFlowPortrait.ageRates.map {
            PieEntry(it.num.toFloat(), it.name)
        }.toMutableList()

        // ?????????
        val pieDataSet = PieDataSet(entries, "")
        // item ?????????
        pieDataSet.setAutomaticallyDisableSliceSpacing(true)
        // item ??????
        pieDataSet.setSliceSpace(0f)
        // item????????????
        pieDataSet.setSelectionShift(5f)
        // ???????????????item?????????
        pieDataSet.setUsingSliceColorAsValueLineColor(true)
        // ????????????
        pieDataSet.setValueLineWidth(2f)
        // ???????????????????????????
        val colors: MutableList<Int> = ArrayList()

        for (c in MyColorTemplate.MATERIAL_COLORS) colors.add(c)

        for (c in MyColorTemplate.VORDIPLOM_COLORS) colors.add(c)

        for (c in MyColorTemplate.JOYFUL_COLORS) colors.add(c)

        for (c in MyColorTemplate.COLORFUL_COLORS) colors.add(c)

        for (c in MyColorTemplate.LIBERTY_COLORS) colors.add(c)

        for (c in MyColorTemplate.PASTEL_COLORS) colors.add(c)

        pieDataSet.colors = colors
        // Y????????????????????????????????????
        pieDataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        // X????????????????????????????????????
        pieDataSet.xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        // ????????????????????????????????????????????????????????????
        pieDataSet.valueLinePart1Length = 0.5f
        // ?????????????????????????????????????????????????????????
        pieDataSet.valueLinePart2Length = 0.6f
        // ???????????????????????????
        pieDataSet.setDrawValues(true)
        // ?????????????????????
        pieDataSet.setValueTextSize(12f)
        // ?????????????????????
        pieDataSet.setValueTextColors(colors)

        val pieData = PieData(pieDataSet)
        pieData.setValueFormatter(object : ValueFormatter(){
            var mFormat: DecimalFormat = DecimalFormat("###,###,##0.0")
            override fun getPieLabel(value: Float, pieEntry: PieEntry?): String {
                return if (binding.pieChart.isUsePercentValuesEnabled()) {
                    "${pieEntry?.label}(${getFormattedValue(value)})"
                } else {
                    "${pieEntry?.label}(${mFormat.format(value.toDouble())})"
                }
            }

            override fun getFormattedValue(value: Float): String {
                return mFormat.format(value.toDouble()) + " %"
            }

        })

        binding.pieChart2.setData(pieData)

        // ????????????
        binding.pieChart2.highlightValues(null)
        binding.pieChart2.invalidate()


        // ??????
        val legendList = entries.mapIndexed { index, pieEntry ->
            MyLegend(pieEntry.label,pieEntry.value.toInt().formatGroupingUsed(),colors[index],"???")
        }
        pieChar2LegendAdapter.setItems(legendList)

    }


    private fun initPieChartLegend() {
        binding.legend.apply {
            layoutManager = GridLayoutManager(requireContext(),2)
            adapter = pieCharLegendAdapter
        }
    }


    private fun initPieChart2Legend() {
        binding.ageLegend.apply {
            layoutManager = GridLayoutManager(requireContext(),2)
            adapter = pieChar2LegendAdapter
        }
    }

    var indicatorCurrentIndex = 0
    private fun initIndicator() {
        val commonNavigator = CommonNavigator(context)
        commonNavigator.isAdjustMode = true
        val pagerTitles = arrayListOf<Pair<String,Int>>(Pair("??????",1), Pair("??????",2), Pair("??????",3))
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
                    indicatorCurrentIndex = index
                    setHorizontalBarChartData(viewModel.portraitEvent.value!!)
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

    private fun initHorizontalBarChart() {
        binding.horizontalBarChart.apply {
            // ??????
//            setExtraOffsets(0f, 0f, 0f, 0f)
            // ???????????????
            getDescription().setEnabled(false)
            // ??????
            val l: Legend = getLegend()
            l.isEnabled = false
            // ????????????
            setDrawBarShadow(false)
            // ?????????????????????
            setDrawValueAboveBar(true)
            // ??????????????????
            setMaxVisibleValueCount(60)
            // ????????????
            setPinchZoom(true)
            // ????????????
            setDrawGridBackground(false)

//            setScaleMinima(1f, 1.15f)

            val xl: XAxis = getXAxis()
            xl.position = XAxis.XAxisPosition.BOTTOM
            xl.setDrawAxisLine(true)
            xl.setAxisLineColor(resources.getColor(R.color.color_dedede))
            xl.setDrawGridLines(false)
            xl.setAvoidFirstLastClipping(true)
            xl.granularity = 1f
            xl.setTextColor(resources.getColor(R.color.color_999999))
            xl.setTextSize(12f)

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
            yr.setTextColor(resources.getColor(R.color.color_999999))
            yr.setTextSize(12f)
            yr.setAxisMinimum(0f)

            setFitBars(true)

            setNoDataText("????????????")

        }
    }


    private fun setHorizontalBarChartData(passengerFlowPortrait: PassengerFlowPortrait) {
        var entries: List<BarEntry> = ArrayList()
        when(indicatorCurrentIndex){
            0 ->{
                // ??????
                if (passengerFlowPortrait.allSources.isNullOrEmpty()){
                    binding.horizontalBarChart.setData(null)
                    binding.horizontalBarChart.invalidate()
                    pieCharLegendAdapter.setItems(arrayListOf())
                    return
                }
                entries = passengerFlowPortrait.allSources.mapIndexed { index, allSource ->
                    BarEntry(index.toFloat(), allSource.num.toFloat(),allSource.sourceName)
                }
            }
            1 ->{
                // ??????
                if (passengerFlowPortrait.inProvinceSources.isNullOrEmpty()){
                    binding.horizontalBarChart.setData(null)
                    binding.horizontalBarChart.invalidate()
                    pieCharLegendAdapter.setItems(arrayListOf())
                    return
                }
                entries = passengerFlowPortrait.inProvinceSources.mapIndexed { index, allSource ->
                    BarEntry(index.toFloat(), allSource.num.toFloat(),allSource.sourceName)
                }
            }
            2 ->{
                // ??????
                if (passengerFlowPortrait.outProvinceSources.isNullOrEmpty()){
                    binding.horizontalBarChart.setData(null)
                    binding.horizontalBarChart.invalidate()
                    pieCharLegendAdapter.setItems(arrayListOf())
                    return
                }
                entries = passengerFlowPortrait.outProvinceSources.mapIndexed { index, allSource ->
                    BarEntry(index.toFloat(), allSource.num.toFloat(),allSource.sourceName)
                }
            }
        }

        // ?????????
        val barDataSet = BarDataSet(entries, "")
        // ?????? icon
        barDataSet.setDrawIcons(false)
        // item ??????
        barDataSet.setColors(resources.getColor(R.color.color_21b87c))
        // ???????????????????????????
        barDataSet.setDrawValues(true)
        // ?????????????????????
        barDataSet.setValueTextSize(10f)
        // ?????????????????????
        barDataSet.setValueTextColor(resources.getColor(R.color.color_59abff))

        val barData = BarData(barDataSet)
        barData.setValueTextSize(10f)
        barData.setDrawValues(true)
        barData.setBarWidth(0.4f)
        barData.setValueFormatter(object : ValueFormatter(){
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        })

        binding.horizontalBarChart.setData(barData)
        binding.horizontalBarChart.highlightValues(null)
        val xl: XAxis =  binding.horizontalBarChart.getXAxis()
        xl.setLabelCount(entries.size)
        xl.valueFormatter = object : ValueFormatter(){
            override fun getFormattedValue(value: Float): String {
                return try {
                    entries[value.toInt()].getData().toString()
                }catch (e:Exception){
                    ""
                }
            }
        }
        binding.horizontalBarChart.invalidate()
        binding.horizontalBarChart.notifyDataSetChanged()
    }



    override fun onDestroy() {
        super.onDestroy()
        binding.timePick.onDestroy()
    }
}

