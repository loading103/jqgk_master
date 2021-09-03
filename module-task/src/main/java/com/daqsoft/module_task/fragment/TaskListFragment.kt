package com.daqsoft.module_task.fragment

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.base.AppBaseFragment
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.LoadSirUtil
import com.daqsoft.library_base.utils.executePaging
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.library_common.bean.Options
import com.daqsoft.library_common.widget.AlarmFilterPopup
import com.daqsoft.library_common.widget.TaskFilterPopup
import com.daqsoft.module_task.BR
import com.daqsoft.module_task.R
import com.daqsoft.module_task.viewmodel.TaskListViewModel
import com.daqsoft.module_task.adapter.TaskListAdapter
import com.daqsoft.module_task.databinding.FragmentTaskListBinding
import com.daqsoft.module_task.repository.pojo.dto.TaskRequest
import com.daqsoft.mvvmfoundation.utils.dp
import com.haibin.calendarview.Calendar
import com.jeremyliao.liveeventbus.LiveEventBus
import com.kingja.loadsir.callback.Callback
import com.kingja.loadsir.core.LoadSir
import com.lxj.xpopup.XPopup
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

/**
 * @package name：com.daqsoft.module_task.fragment
 * @date 13/5/2021 上午 10:55
 * @author zp
 * @describe 任务状态列表
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Task.PAGER_TASK_LIST)
class TaskListFragment : AppBaseFragment<FragmentTaskListBinding, TaskListViewModel>() {

    private var state : Int? = null

    val taskListAdapter : TaskListAdapter by lazy { TaskListAdapter() }


    companion object{

        private const val STATE = "state"

        fun newInstance(state : Int):TaskListFragment {
            val args = Bundle()
            args.putInt(STATE, state)
            val fragment = TaskListFragment()
            fragment.arguments = args
            return fragment
        }
    }



    val taskFilterPopup : TaskFilterPopup by lazy {
        TaskFilterPopup(requireActivity()).apply {
            setTimeData("时间筛选",arrayListOf("今日","本周","本月"))
            setOptionsData("业务类型",arrayListOf())
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
        return R.layout.fragment_task_list
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initViewModel(): TaskListViewModel? {
        return ViewModelProvider(this).get(TaskListViewModel::class.java)
    }

    override fun initView() {
        super.initView()
        initOnClick()
        initRecycleView()
        initLoadService()
        initRefresh()
    }

    private fun initLoadService() {
        loadService = LoadSir.getDefault().register(binding.refresh, Callback.OnReloadListener {
            LoadSirUtil.postLoading(loadService!!)
            initData()
            viewModel.dataSource?.invalidate()
        })
        LoadSirUtil.postLoading(loadService!!)
    }

    private fun initRefresh() {
        binding.refresh.setOnRefreshListener{
            initData()
            LiveEventBus.get(LEBKeyGlobal.ALARM_HANDLE_TOP,Boolean::class.java).post(true)
            viewModel.dataSource?.invalidate()
        }
    }

    override fun initData() {
        super.initData()
        viewModel.taskRequest = TaskRequest()
        viewModel.taskRequest.taskStatus = state

    }

    private fun initOnClick() {
        binding.filter.setOnClickListenerThrottleFirst {
            filterPopup()
        }

        binding.summary.setOnClickListenerThrottleFirst {
            ARouter.getInstance().build(ARouterPath.Task.PAGER_WEEKLY_SUMMARY_LIST).navigation()
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


        viewModel.optionsEvent.observe(this, Observer {
            taskFilterPopup.setOptionsData("业务类型",viewModel.optionsEvent.value!!)
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
            adapter = taskListAdapter
        }
    }


    /**
     * 筛选 popup
     */
    fun filterPopup(){
        XPopup
            .Builder(requireActivity())
            .isDestroyOnDismiss(false)
            .asCustom(taskFilterPopup.apply {
                setOnClickListener(object : TaskFilterPopup.OnClickListener {
                    override fun resetOnClick() {
                        LoadSirUtil.postLoading(loadService!!)
                        initData()
                        viewModel.dataSource?.invalidate()
                    }

                    override fun determineOnClick(
                        periodList: List<Calendar>?,
                        option1: Options?,
                        option2: Options?,
                        option3: Options?
                    ) {
                        LoadSirUtil.postLoading(loadService!!)
                        initData()
                        if (!periodList.isNullOrEmpty()){
                            viewModel.taskRequest.startDate = "${periodList.first().year}-${periodList.first().month}-${periodList.first().day}"
                            viewModel.taskRequest.endDate = "${periodList.last().year}-${periodList.last().month}-${periodList.last().day}"
                        }
                        viewModel.taskRequest.businessType	 = option1?.value
                        viewModel.taskRequest.taskType = option2?.value
                        viewModel.taskRequest.status = option3?.value
                        viewModel.dataSource?.invalidate()
                    }

                })
            })
            .show()


        if (viewModel.optionsEvent.value.isNullOrEmpty()) {
            viewModel.getOptions()
        } else {
            taskFilterPopup.setOptionsData("业务类型",viewModel.optionsEvent.value!!)
        }

    }
}