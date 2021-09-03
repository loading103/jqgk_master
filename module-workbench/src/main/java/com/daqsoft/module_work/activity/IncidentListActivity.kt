package com.daqsoft.module_work.activity

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.LoadSirUtil
import com.daqsoft.library_base.utils.executePaging
import com.daqsoft.library_common.widget.AlarmFilterPopup
import com.daqsoft.module_work.BR
import com.daqsoft.module_work.R
import com.daqsoft.module_work.adapter.IncidentListAdapter
import com.daqsoft.module_work.databinding.ActivityIncidentListBinding
import com.daqsoft.module_work.repository.pojo.dto.IncidentListRequest
import com.daqsoft.module_work.repository.pojo.vo.Incident
import com.daqsoft.module_work.viewmodel.IncidentListViewModel
import com.daqsoft.mvvmfoundation.utils.dp
import com.haibin.calendarview.Calendar
import com.kingja.loadsir.callback.Callback
import com.kingja.loadsir.core.LoadSir
import com.lxj.xpopup.XPopup
import dagger.hilt.android.AndroidEntryPoint

/**
 * @package name：com.daqsoft.module_work.activity
 * @date 23/7/2021 下午 4:58
 * @author zp
 * @describe 事件列表
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_INCIDENT_LIST)
class IncidentListActivity  : AppBaseActivity<ActivityIncidentListBinding, IncidentListViewModel>(){

    val incidentListAdapter : IncidentListAdapter by lazy {
        IncidentListAdapter().apply{
            setOnClickListener(object : IncidentListAdapter.OnClickListener{
                override fun itemOnClick(position: Int, item: Incident) {
                    ARouter
                        .getInstance()
                        .build(ARouterPath.Workbench.PAGER_INCIDENT_Detail)
                        .withString("id", item.id.toString())
                        .navigation()
                }
            })
        }
    }

    val alarmFilterPopup : AlarmFilterPopup by lazy {
        AlarmFilterPopup(this).apply {
            setTimeData("时间筛选",arrayListOf("今日","本周","本月"))
            setTypeData("告警类型", arrayListOf())
            setStatusData("事件状态", arrayListOf())
        }
    }


    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_incident_list
    }

    override fun initViewModel(): IncidentListViewModel? {
        return viewModels<IncidentListViewModel>().value
    }

    override fun initView() {
        super.initView()
        initLoadService()
        initRefresh()
        initRecyclerView()
    }

    override fun initData() {
        super.initData()
        viewModel.getIncidentType()
        viewModel.getIncidentStatus()
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

        viewModel.typeEvent.observe(this, Observer {
            alarmFilterPopup.apply {
                setTypeData("事件类型",viewModel.typeEvent.value!!.map { it.type }.toMutableList())
            }
        })

        viewModel.statusEvent.observe(this, Observer {
            alarmFilterPopup.apply {
                setStatusData("事件状态",viewModel.statusEvent.value!!.map { it.value }.toMutableList())
            }
        })

        viewModel.filterEvent.observe(this, Observer {
            filterPopup()
        })

    }

    private fun initLoadService() {
        loadService = LoadSir.getDefault().register(binding.refresh, Callback.OnReloadListener {
            LoadSirUtil.postLoading(loadService!!)
            initData()
            viewModel.body.currPage = ConstantGlobal.INITIAL_PAGE
            viewModel.dataSource?.invalidate()
        })
        LoadSirUtil.postLoading(loadService!!)
    }

    private fun initRefresh() {
        binding.refresh.setOnRefreshListener {
            initData()
            viewModel.body.currPage = ConstantGlobal.INITIAL_PAGE
            viewModel.dataSource?.invalidate()
        }
    }

    private fun initRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@IncidentListActivity)
            if (itemDecorationCount == 0) {
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        val position = parent.getChildAdapterPosition(view)
                        if (position == 0){
                            outRect.top = 12.dp
                        }
                        outRect.bottom = 12.dp
                    }
                })
            }
            adapter = incidentListAdapter
        }
    }


    /**
     * 筛选 popup
     */
    fun filterPopup(){
        XPopup
            .Builder(this)
            .isDestroyOnDismiss(false)
            .asCustom(alarmFilterPopup.apply {
                setOnClickListener(object : AlarmFilterPopup.OnClickListener {
                    override fun resetOnClick() {
                        LoadSirUtil.postLoading(loadService!!)
                        viewModel.body =  IncidentListRequest()
                        viewModel.dataSource?.invalidate()
                    }

                    override fun determineOnClick(
                        periodList: List<Calendar>?,
                        typeText: String?,
                        statusText: String?
                    ) {
                        LoadSirUtil.postLoading(loadService!!)
                        viewModel.body =  IncidentListRequest()
                        if (!periodList.isNullOrEmpty()){
                            viewModel.body.dateType = 4
                            viewModel.body.startDate = "${periodList.first().year}-${periodList.first().month}-${periodList.first().day}"
                            viewModel.body.endDate = "${periodList.last().year}-${periodList.last().month}-${periodList.last().day}"
                        }
                        viewModel.body.type = viewModel.typeEvent.value?.find { it.type == typeText  }?.id
                        viewModel.body.status = viewModel.statusEvent.value?.find { it.value == statusText  }?.key?.toInt()
                        viewModel.dataSource?.invalidate()
                    }


                })
            })
            .show()
    }

}