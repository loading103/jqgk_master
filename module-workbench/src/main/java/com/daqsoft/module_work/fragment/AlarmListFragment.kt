package com.daqsoft.module_work.fragment

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseFragment
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.LoadSirUtil
import com.daqsoft.library_base.utils.executePaging
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.library_common.widget.AlarmFilterPopup
import com.daqsoft.module_work.BR
import com.daqsoft.module_work.R
import com.daqsoft.module_work.databinding.FragmentAlarmListBinding
import com.daqsoft.module_work.repository.pojo.dto.AlarmRequest
import com.daqsoft.module_work.viewmodel.AlarmListViewModel
import com.daqsoft.mvvmfoundation.utils.dp
import com.haibin.calendarview.Calendar
import com.jeremyliao.liveeventbus.LiveEventBus
import com.kingja.loadsir.callback.Callback
import com.kingja.loadsir.core.LoadSir
import com.lxj.xpopup.XPopup
import dagger.hilt.android.AndroidEntryPoint

/**
 * @package name：com.daqsoft.module_work.fragment
 * @date 25/5/2021 上午 11:36
 * @author zp
 * @describe
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_ALARM_LIST)
class AlarmListFragment : AppBaseFragment<FragmentAlarmListBinding, AlarmListViewModel>() {

    private var state : Int? = null

    companion object{

        private const val STATE = "state"

        fun newInstance(state : Int):AlarmListFragment {
            val args = Bundle()
            args.putInt(STATE, state)
            val fragment = AlarmListFragment()
            fragment.arguments = args
            return fragment
        }
    }

    val alarmFilterPopup : AlarmFilterPopup by lazy {
        AlarmFilterPopup(requireActivity()).apply {
            ConstantGlobal
            setTimeData("时间筛选",arrayListOf("今日","本周","本月"))
            setTypeData("告警类型", arrayListOf())
            setStatusData("事件状态", arrayListOf())
        }
    }

    override fun initParam() {
        super.initParam()
        state = arguments?.getInt(STATE)
    }

    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return R.layout.fragment_alarm_list
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initViewModel(): AlarmListViewModel? {
        return ViewModelProvider(this).get(AlarmListViewModel::class.java)

    }

    override fun initView() {
        super.initView()
        initOnClick()
        initRecycleView()
        initLoadService()
        initRefresh()
    }

    private fun initRefresh() {
        binding.refresh.setOnRefreshListener{
            initData()
            viewModel.dataSource?.invalidate()
        }
    }

    override fun initData() {
        super.initData()
        viewModel.alarmRequest = AlarmRequest()
        viewModel.alarmRequest.t = state
    }

    private fun initLoadService() {
        loadService = LoadSir.getDefault().register(binding.refresh, Callback.OnReloadListener {
            LoadSirUtil.postLoading(loadService!!)
            initData()
            viewModel.dataSource?.invalidate()
        })
        LoadSirUtil.postLoading(loadService!!)
    }

    private fun initOnClick() {
        binding.filter.setOnClickListenerThrottleFirst() {
            filterPopup()
        }
    }


    override fun initViewObservable() {
        super.initViewObservable()
        viewModel.pageList.observe(this, Observer {
            binding.refresh.finishRefresh()
            if (it.isEmpty()){
                return@Observer
            }
            binding.recyclerView.executePaging(it, viewModel.diff)
        })

        viewModel.typeStatusEvent.observe(this, Observer {
            alarmFilterPopup.apply {
                setTypeData("告警类型",viewModel.typeStatusEvent.value!!.type.map { it.label }.toMutableList())
                setStatusData("事件状态",viewModel.typeStatusEvent.value!!.status.map { it.label }.toMutableList())
            }
        })

        LiveEventBus.get(LEBKeyGlobal.ALARM_HANDLE,Boolean::class.java).observe(this, Observer {
            initData()
            viewModel.dataSource?.invalidate()
        })
        LiveEventBus.get(LEBKeyGlobal.ALARM_HANDLE_FINISH,Boolean::class.java).observe(this, Observer {
            initData()
            viewModel.dataSource?.invalidate()
        })
    }

    private fun initRecycleView(){
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            if (itemDecorationCount == 0){
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        val position = parent.getChildAdapterPosition(view)
                        val count = state.itemCount - 1
                        outRect.top = 8.dp
                        outRect.left = 8.dp
                        outRect.right = 8.dp
                        if (position == count){
                            outRect.bottom = 8.dp
                        }
                    }
                })
            }
        }
    }


    /**
     * 筛选 popup
     */
    fun filterPopup(){
        XPopup
            .Builder(requireActivity())
            .isDestroyOnDismiss(false)
            .asCustom(alarmFilterPopup.apply {
                setOnClickListener(object : AlarmFilterPopup.OnClickListener {
                    override fun resetOnClick() {
                        LoadSirUtil.postLoading(loadService!!)
                        initData()
                        viewModel.dataSource?.invalidate()
                    }

                    override fun determineOnClick(
                        periodList: List<Calendar>?,
                        typeText: String?,
                        statusText: String?
                    ) {
                        LoadSirUtil.postLoading(loadService!!)
                        initData()
                        if (!periodList.isNullOrEmpty()){
                            viewModel.alarmRequest.startTime = "${periodList.first().year}-${periodList.first().month}-${periodList.first().day}"
                            viewModel.alarmRequest.endTime = "${periodList.last().year}-${periodList.last().month}-${periodList.last().day}"
                        }
                        viewModel.alarmRequest.type = viewModel.typeStatusEvent.value?.type?.find { it.label == typeText  }?.value
                        viewModel.alarmRequest.status = viewModel.typeStatusEvent.value?.status?.find { it.label == statusText  }?.value
                        viewModel.dataSource?.invalidate()
                    }


                })
            })
            .show()

        if (viewModel.typeStatusEvent.value == null){
            viewModel.getAlarmTypeAndStatus()
        }else{
            alarmFilterPopup.apply {
                setTypeData("告警类型",viewModel.typeStatusEvent.value!!.type.map { it.label }.toMutableList())
                setStatusData("事件状态",viewModel.typeStatusEvent.value!!.status.map { it.label }.toMutableList())
            }
        }

    }

}