package com.daqsoft.module_work.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.daqsoft.library_base.base.AppBaseFragment
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.LoadSirUtil
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.library_base.wrapper.loadsircallback.ErrorCallback
import com.daqsoft.library_base.wrapper.loadsircallback.LoadingCallback
import com.daqsoft.library_common.bean.Options
import com.daqsoft.library_common.widget.OptionsPopup
import com.daqsoft.module_work.BR
import com.daqsoft.module_work.R
import com.daqsoft.module_work.adapter.CollectAdapter
import com.daqsoft.module_work.databinding.FragmentStatisticsBinding
import com.daqsoft.module_work.repository.pojo.dto.AttendanceStatisticCollect
import com.daqsoft.module_work.viewmodel.StatisticsViewModel
import com.daqsoft.module_work.warrper.NotJoinedCallback
import com.daqsoft.module_work.warrper.RosterEmptyCallback
import com.kingja.loadsir.callback.Callback
import com.kingja.loadsir.callback.SuccessCallback
import com.kingja.loadsir.core.LoadSir
import com.lxj.xpopup.XPopup
import dagger.hilt.android.AndroidEntryPoint
import me.tatarka.bindingcollectionadapter2.ItemBinding
import java.util.*

/**
 * @package name：com.daqsoft.module_work.fragment
 * @date 10/5/2021 下午 4:45
 * @author zp
 * @describe 统计
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_STATISTICS)
class StatisticsFragment  : AppBaseFragment<FragmentStatisticsBinding, StatisticsViewModel>() {

    lateinit var selectCalendar : com.haibin.calendarview.Calendar


    val collectAdapter by lazy {
        CollectAdapter().apply {
            itemBinding = ItemBinding.of(ItemBinding.VAR_NONE,R.layout.recycleview_collect_item)
            setOnClickListener(object : CollectAdapter.OnClickListener{
                override fun detailItemOnClick(content: String) {
                    var year : Int
                    var month : Int
                    var day  : Int
                    try {
                        val date = content.split("  ")[0].split("-")
                        year = date[0].toInt()
                        month = date[1].toInt()
                        day = date[2].toInt()
                    }catch (e:Exception){
                        val calendar = Calendar.getInstance()
                        year = calendar.get(Calendar.YEAR)
                        month = calendar.get(Calendar.MONTH)+1
                        day = calendar.get(Calendar.DAY_OF_MONTH)
                        binding.date.text = "${year}年${month}月${day}日"
                    }

                    ARouter
                        .getInstance()
                        .build(ARouterPath.Workbench.PAGER_ATTENDANCE_MONTHLY_CALENDAR)
                        .withInt("year",year)
                        .withInt("month",month)
                        .withInt("day",day)
                        .navigation()
                }
            })
        }
    }

    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return R.layout.fragment_statistics
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initViewModel(): StatisticsViewModel? {
        return requireActivity().viewModels<StatisticsViewModel>().value
    }

    override fun initView() {
        super.initView()

        initLoadService()
        initOnClick()
        initRecyclerView()

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)+1
        binding.date.text = "${year}年${month}月"

        selectCalendar = com.haibin.calendarview.Calendar()
        selectCalendar.year = year
        selectCalendar.month = month


    }


    private fun initLoadService() {
        loadService = LoadSir.getDefault().register(binding.container, Callback.OnReloadListener {
            LoadSirUtil.postLoading(loadService!!)
            initData()
        })
        LoadSirUtil.postLoading(loadService!!)
    }

    override fun initData() {
        super.initData()

        createOptionData()

        viewModel.getMonthlyStatistics("${selectCalendar.year}-${String.format("%02d", selectCalendar.month)}-${selectCalendar.day}")

    }

    override fun initViewObservable() {
        super.initViewObservable()


        viewModel.statistics.observe(this, androidx.lifecycle.Observer {
            collectAdapter.setItems(it)
        })
    }

    private fun initRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = collectAdapter
        }
    }

    private fun initOnClick() {
        binding.date.setOnClickListenerThrottleFirst{
            optionsPopup()
        }

        binding.calendar.setOnClickListenerThrottleFirst{
            ARouter
                .getInstance()
                .build(ARouterPath.Workbench.PAGER_ATTENDANCE_MONTHLY_CALENDAR)
                .navigation()
        }
    }


    private var option1List: MutableList<Options> = ArrayList()
    private var option2List: MutableList<MutableList<Options>> = ArrayList()
    private fun createOptionData() {
        option1List.clear()
        option2List.clear()
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)+1
        for (i in 1970..year) {
            val value = "${i}"
            val label = i.toString()
            val children = mutableListOf<Options>()
            for (i in 1..if(i == year) month else 12) {
                val value = "${i}"
                val label = i.toString()
                val options = Options(label, value, arrayListOf<Options>())
                children.add(options)
            }
            val options = Options(label, value, children)
            option1List.add(options)
            option2List.add(children)
        }

        optionOne = Options(year.toString(),year.toString(), arrayListOf())
        optionTwo = Options(month.toString(),month.toString(), arrayListOf())
    }


    var option1V: Int = 0
    var option2V: Int = 0
    var optionOne : Options? = null
    var optionTwo: Options? = null
    private var optionsPopup : OptionsPopup<Options> ? = null
    private fun optionsPopup() {
        optionsPopup = XPopup
            .Builder(requireActivity())
            .isDestroyOnDismiss(true)
            .asCustom(OptionsPopup<Options>(requireActivity()).apply {
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
                            sb.append("${optionOne!!.label}年")
                            selectCalendar.year = optionOne!!.label.toInt()

                        }
                        if (optionTwo != null){
                            sb.append("${optionTwo!!.label}月")
                            selectCalendar.month = optionTwo!!.label.toInt()
                        }
                        binding.date.text = sb.toString()

                        viewModel.getMonthlyStatistics("${selectCalendar.year}-${String.format("%02d", selectCalendar.month)}-${selectCalendar.day}")

                    }

                })
            })
            .show() as OptionsPopup<Options>

        optionsPopup?.setPicker(
            option1List,
            option2List,
            null
        )
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

}