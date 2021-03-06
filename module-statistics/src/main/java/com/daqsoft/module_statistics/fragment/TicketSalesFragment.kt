package com.daqsoft.module_statistics.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import cn.iwgang.simplifyspan.other.SpecialGravity
import cn.iwgang.simplifyspan.unit.SpecialImageUnit
import cn.iwgang.simplifyspan.unit.SpecialTextUnit
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseFragment
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.LoadSirUtil
import com.daqsoft.library_base.utils.formatGroupingUsed
import com.daqsoft.library_base.utils.percentCompareToZero
import com.daqsoft.library_common.bean.Options
import com.daqsoft.module_statistics.BR
import com.daqsoft.module_statistics.R
import com.daqsoft.module_statistics.adapter.MyLegendAdapter
import com.daqsoft.module_statistics.databinding.FragmentTicketSalesBinding
import com.daqsoft.module_statistics.repository.pojo.vo.MyLegend
import com.daqsoft.module_statistics.repository.pojo.vo.TicketSales
import com.daqsoft.module_statistics.viewmodel.TicketSalesViewModel
import com.daqsoft.module_statistics.warrper.MyColorTemplate
import com.daqsoft.module_statistics.widget.TimePick
import com.daqsoft.mvvmfoundation.utils.sp
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.haibin.calendarview.Calendar
import com.kingja.loadsir.callback.Callback
import com.kingja.loadsir.core.LoadSir
import dagger.hilt.android.AndroidEntryPoint
import me.tatarka.bindingcollectionadapter2.ItemBinding
import timber.log.Timber
import java.text.DecimalFormat


/**
 * @package name???com.daqsoft.module_statistics.fragment
 * @date 10/6/2021 ?????? 9:58
 * @author zp
 * @describe ?????? ????????????
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Statistics.PAGER_TICKET_SALES)
class TicketSalesFragment : AppBaseFragment<FragmentTicketSalesBinding, TicketSalesViewModel>() {


    val pieChartLegendAdapter : MyLegendAdapter by lazy {
        MyLegendAdapter().apply {
            itemBinding = ItemBinding.of(ItemBinding.VAR_NONE,R.layout.recycleview_legend_item)
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
        return R.layout.fragment_ticket_sales
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initViewModel(): TicketSalesViewModel? {
        return requireActivity().viewModels<TicketSalesViewModel>().value
    }

    override fun initView() {
        super.initView()
        initRefresh()
        initTimePick()
        initPieChart()
        initPieChartLegend()
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

        viewModel.salesEvent.observe(this, Observer {
            initDepict(it)
            setPieChartData(it)
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
                viewModel.getTicketSales(timeType, statisticsTime, endTime, quarter)
            }

        })
    }

    private fun initDepict(ticketSales : TicketSales){
        val rise = BitmapFactory.decodeResource(resources, R.mipmap.pwtj_icon_shangsheng)
        val drop = BitmapFactory.decodeResource(resources, R.mipmap.pwtj_icon_xiajiang)

        val salesY2YCompare = ticketSales.salesY2Y.percentCompareToZero()
        val salesAnnulusCompare = ticketSales.salesAnnulus.percentCompareToZero()
        binding.totalAmount.text = SimplifySpanBuild()
            .append(SpecialTextUnit("??????????????????\n").setTextColor(resources.getColor(R.color.color_666666)).setTextSize(14f))
            .append(SpecialTextUnit(ticketSales.totalAmount.formatGroupingUsed()).setTextStyle(Typeface.BOLD).setTextColor(resources.getColor(R.color.color_333333)).setTextSize(20f))
            .append(SpecialTextUnit(" ???\n").setTextColor(resources.getColor(R.color.color_333333)).setTextSize(12f))
            .append(SpecialTextUnit("??????").setTextColor(resources.getColor(R.color.color_666666)).setTextSize(12f))
            .append(SpecialTextUnit(" ${ticketSales.salesY2Y} ").setTextColor(
                when(salesY2YCompare){
                    -1 -> resources.getColor(R.color.color_ff5757)
                    1 -> resources.getColor(R.color.color_27cb8c)
                    else -> resources.getColor(R.color.color_27cb8c)
                }
            ).setTextSize(12f))
            .apply {
                val bitmap : Bitmap? = when(salesY2YCompare){
                    -1 -> drop
                    1 -> rise
                    else -> null
                }
                if (bitmap != null){
                    this.append(SpecialImageUnit(requireContext(), bitmap, bitmap.width, bitmap.height).setGravity(SpecialGravity.CENTER))
                }
            }
            .append(SpecialTextUnit("               "))
            .append(SpecialTextUnit("??????").setTextColor(resources.getColor(R.color.color_666666)).setTextSize(12f))
            .append(SpecialTextUnit(" ${ticketSales.salesAnnulus} ").setTextColor(
                when(salesAnnulusCompare){
                    -1 -> resources.getColor(R.color.color_ff5757)
                    1 -> resources.getColor(R.color.color_27cb8c)
                    else -> resources.getColor(R.color.color_27cb8c)
                }
            ).setTextSize(12f))
            .apply {
                val bitmap : Bitmap? = when(salesAnnulusCompare){
                    -1 -> drop
                    1 -> rise
                    else -> null
                }
                if (bitmap != null){
                    this.append(SpecialImageUnit(requireContext(), bitmap, bitmap.width, bitmap.height).setGravity(SpecialGravity.CENTER))
                }
            }
            .build()

        val quantityY2YCompare = ticketSales.quantityY2Y.percentCompareToZero()
        val quantityAnnulusCompare = ticketSales.quantityAnnulus.percentCompareToZero()
        binding.totalCount.text = SimplifySpanBuild()
            .append(SpecialTextUnit("??????????????????\n").setTextColor(resources.getColor(R.color.color_666666)).setTextSize(14f))
            .append(SpecialTextUnit(ticketSales.totalQuantity.formatGroupingUsed()).setTextStyle(Typeface.BOLD).setTextColor(resources.getColor(R.color.color_333333)).setTextSize(20f))
            .append(SpecialTextUnit(" ???\n").setTextColor(resources.getColor(R.color.color_333333)).setTextSize(12f))
            .append(SpecialTextUnit("??????").setTextColor(resources.getColor(R.color.color_666666)).setTextSize(12f))
            .append(SpecialTextUnit(" ${ticketSales.quantityY2Y} ").setTextColor(
                when(quantityY2YCompare){
                    -1 -> resources.getColor(R.color.color_ff5757)
                    1 -> resources.getColor(R.color.color_27cb8c)
                    else -> resources.getColor(R.color.color_27cb8c)
                }
            ).setTextSize(12f))
            .apply {
                val bitmap : Bitmap? = when(quantityY2YCompare){
                    -1 -> drop
                    1 -> rise
                    else -> null
                }
                if (bitmap != null){
                    this.append(SpecialImageUnit(requireContext(), bitmap, bitmap.width, bitmap.height).setGravity(SpecialGravity.CENTER))
                }
            }
            .append(SpecialTextUnit("               "))
            .append(SpecialTextUnit("??????").setTextColor(resources.getColor(R.color.color_666666)).setTextSize(12f))
            .append(SpecialTextUnit(" ${ticketSales.quantityAnnulus} ").setTextColor(
                when(quantityAnnulusCompare){
                    -1 -> resources.getColor(R.color.color_ff5757)
                    1 -> resources.getColor(R.color.color_27cb8c)
                    else -> resources.getColor(R.color.color_27cb8c)
                }
            ).setTextSize(12f))
            .apply {
                val bitmap : Bitmap? = when(quantityAnnulusCompare){
                    -1 -> drop
                    1 -> rise
                    else -> null
                }
                if (bitmap != null){
                    this.append(SpecialImageUnit(requireContext(), bitmap, bitmap.width, bitmap.height).setGravity(SpecialGravity.CENTER))
                }
            }
            .build()

        binding.pendingCount.text = SimplifySpanBuild()
            .append(SpecialTextUnit("???????????????\n").setTextColor(resources.getColor(R.color.color_666666)).setTextSize(14f))
            .append(SpecialTextUnit(ticketSales.totalToBeWriteOffQuantity.formatGroupingUsed()).setTextStyle(Typeface.BOLD).setTextColor(resources.getColor(R.color.color_333333)).setTextSize(20f))
            .append(SpecialTextUnit(" ???").setTextColor(resources.getColor(R.color.color_333333)).setTextSize(12f))
            .build()
    }

    private fun initPieChart() {
        binding.pieChart.apply {
            // ?????????
            setUsePercentValues(true)
            // ???????????????
            getDescription().setEnabled(false)
            // ??????
            setExtraOffsets(20f, 10f, 20f, 10f)
            // ????????????
            setDrawCenterText(true)
            val icon = BitmapFactory.decodeResource(resources, R.mipmap.pwtj_icon_piaowu)
            setCenterText( SimplifySpanBuild()
                .append(SpecialImageUnit(requireContext(), icon, icon.width, icon.height).setGravity(SpecialGravity.CENTER))
                .append(SpecialTextUnit("\n????????????").setTextColor(resources.getColor(R.color.color_59abff)).setTextSize(12.sp.toFloat()))
                .build()
            )
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



    private fun setPieChartData(ticketSales : TicketSales) {

        if (ticketSales.ticketType.isNullOrEmpty()){
            binding.pieChart.setData(null)
            binding.pieChart.invalidate()
            pieChartLegendAdapter.setItems(arrayListOf())
            return
        }

        val entries = ticketSales.ticketType.map {
            PieEntry(it.totalAmount.toFloat(), it.ticketType)
        }

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
            MyLegend(pieEntry.label,pieEntry.value.formatGroupingUsed(),colors[index],"???")
        }
        pieChartLegendAdapter.setItems(legendList)


    }


    private fun initPieChartLegend() {
        binding.legend.apply {
            layoutManager = GridLayoutManager(requireContext(),2)
            adapter = pieChartLegendAdapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.timePick.onDestroy()
    }
}