package com.daqsoft.module_statistics.fragment

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
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
import com.daqsoft.library_common.bean.Options
import com.daqsoft.module_statistics.BR
import com.daqsoft.module_statistics.R
import com.daqsoft.module_statistics.adapter.MyLegendAdapter
import com.daqsoft.module_statistics.databinding.FragmentVehicleTimePeriodBinding
import com.daqsoft.module_statistics.repository.pojo.vo.MyLegend
import com.daqsoft.module_statistics.repository.pojo.vo.VehicleTimePeriod
import com.daqsoft.module_statistics.viewmodel.VehicleTimePeriodViewModel
import com.daqsoft.module_statistics.widget.TimePick
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.haibin.calendarview.Calendar
import com.kingja.loadsir.callback.Callback
import com.kingja.loadsir.core.LoadSir
import dagger.hilt.android.AndroidEntryPoint
import me.tatarka.bindingcollectionadapter2.ItemBinding
import timber.log.Timber

/**
 * @package name：com.daqsoft.module_statistics.fragment
 * @date 21/6/2021 下午 3:20
 * @author zp
 * @describe 车辆 时段分析
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Statistics.PAGER_VEHICLE_TIME_PERIOD)
class VehicleTimePeriodFragment : AppBaseFragment<FragmentVehicleTimePeriodBinding, VehicleTimePeriodViewModel>() {

    val lineChartLegendAdapter : MyLegendAdapter by lazy {
        MyLegendAdapter().apply {
            itemBinding = ItemBinding.of(ItemBinding.VAR_NONE, R.layout.recycleview_legend_item_warp)
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
        return loadService!!.loadLayout
    }

    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return R.layout.fragment_vehicle_time_period
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initViewModel(): VehicleTimePeriodViewModel? {
        return requireActivity().viewModels<VehicleTimePeriodViewModel>().value
    }

    override fun initView() {
        super.initView()
        initRefresh()
        initTimePick()
        initLineChart()
        initLineChartLegend()
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

        viewModel.timePeriodEvent.observe(this, Observer {
            initDepict(it)
            setLineChartData(it)
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
                viewModel.getVehicleTimePeriod(timeType, statisticsTime, endTime, quarter,null)
            }

        })
    }

    private fun initDepict(vehicleTimePeriod : VehicleTimePeriod){
        binding.totalCount.text = SimplifySpanBuild()
            .append(SpecialTextUnit("总车辆数\n").setTextColor(resources.getColor(R.color.color_666666)).setTextSize(14f))
            .append(
                SpecialTextUnit(vehicleTimePeriod.totalNum.formatGroupingUsed()).setTextStyle(
                    Typeface.BOLD).setTextColor(resources.getColor(R.color.color_333333)).setTextSize(20f))
            .append(SpecialTextUnit(" 辆").setTextColor(resources.getColor(R.color.color_333333)).setTextSize(12f))
            .build()
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
            yl.setTextSize(12f)
            yl.setTextColor(resources.getColor(R.color.color_999999))
            yl.granularity = 1f

            val yr: YAxis = getAxisRight()
            yr.setDrawAxisLine(false)
            yr.setDrawGridLines(false)
            yr.setDrawLabels(false)


            setNoDataText("暂无数据")
        }
    }


    private fun setLineChartData(vehicleTimePeriod : VehicleTimePeriod) {

        if (vehicleTimePeriod.items.isNullOrEmpty()){
            binding.lineChart.setData(null)
            binding.lineChart.invalidate()
            lineChartLegendAdapter.setItems(arrayListOf())
            return
        }

        val current: MutableList<Entry> = ArrayList()
        val y2y	: MutableList<Entry> = ArrayList()
        val annulus: MutableList<Entry> = ArrayList()
        vehicleTimePeriod.items.forEachIndexed { index, timeInterval ->
            current.add(Entry(index.toFloat(), timeInterval.currentNum.toFloat(),timeInterval.time))
            y2y.add(Entry(index.toFloat(), timeInterval.yearOnYearChangeNum.toFloat(),timeInterval.time))
            annulus.add(Entry(index.toFloat(), timeInterval.monthOnMonthChangeNum.toFloat(),timeInterval.time))
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
        binding.legend.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL,false)
            adapter = lineChartLegendAdapter
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        binding.timePick.onDestroy()
    }
}

