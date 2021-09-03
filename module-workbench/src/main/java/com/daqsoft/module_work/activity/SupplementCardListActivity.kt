package com.daqsoft.module_work.activity

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.LoadSirUtil
import com.daqsoft.library_base.utils.executePaging
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.module_work.BR
import com.daqsoft.module_work.R
import com.daqsoft.module_work.adapter.SupplementCardListAdapter
import com.daqsoft.module_work.databinding.ActivitySupplementCardListBinding
import com.daqsoft.module_work.viewmodel.SupplementCardListViewModel
import com.daqsoft.module_work.widget.GridConditionPopup
import com.daqsoft.mvvmfoundation.utils.dp
import com.haibin.calendarview.Calendar
import com.kingja.loadsir.callback.Callback
import com.kingja.loadsir.core.LoadSir
import com.lxj.xpopup.XPopup
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * @package name：com.daqsoft.module_work.activity
 * @date 6/7/2021 下午 2:06
 * @author zp
 * @describe 补卡列表
 */

@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_SUPPLEMENT_CARD_LIST)
class SupplementCardListActivity  : AppBaseActivity<ActivitySupplementCardListBinding, SupplementCardListViewModel>() {


    @JvmField
    @Autowired
    var approve : Boolean = false

    val filterPopup : GridConditionPopup by lazy {
        GridConditionPopup(this,"筛选").apply {
            setOnClickListener(object : GridConditionPopup.OnClickListener{
                override fun resetOnClick() {

                }

                override fun determineOnClick(
                    clickMap: HashMap<Int, Int>,
                    selectRange: HashMap<Int, List<Calendar>>
                ) {
                    Timber.e(" clickMap : ${clickMap}     selectRange : ${selectRange}")
                }


            })

            val data  = mutableListOf<Pair<String,List<String>>>()
            val condition = listOf<String>("全部","已通过","待审核","被驳回","已撤销")
            data.add(Pair("审批状态",condition))
            setData(data)
        }
    }

    val orderPopup : GridConditionPopup by lazy {
        GridConditionPopup(this,"排序").apply {
            setOnClickListener(object : GridConditionPopup.OnClickListener{
                override fun resetOnClick() {

                }

                override fun determineOnClick(
                    clickMap: HashMap<Int, Int>,
                    selectRange: HashMap<Int, List<Calendar>>
                ) {

                    Timber.e(" clickMap : ${clickMap}     selectRange : ${selectRange}")
                }

            })

            val data  = mutableListOf<Pair<String,List<String>>>()
            val condition = listOf<String>("正序排序","倒序排序")
            data.add(Pair("排序方式",condition))

            val condition1 = listOf<String>("开始时间","结束时间","提交时间","审核时间")
            data.add(Pair("排序属性",condition1))
            setData(data)
        }
    }

    val supplementCardListAdapter : SupplementCardListAdapter by lazy {
        SupplementCardListAdapter().apply{
            setOnClickListener(object : SupplementCardListAdapter.OnClickListener{
                override fun itemOnClick(position: Int, item: Any) {
                    ARouter
                        .getInstance()
                        .build(ARouterPath.Workbench.SUPPLEMENT_CARD_APPLY_INFO)
                        .withBoolean("approve",approve)
                        .navigation()
                }
            })
        }
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_supplement_card_list
    }

    override fun initViewModel(): SupplementCardListViewModel? {
        return viewModels<SupplementCardListViewModel>().value
    }

    override fun initView() {
        super.initView()

        initOnClick()
        initLoadService()
        initRefresh()
        initRecyclerView()
    }

    override fun initData() {
        super.initData()

        viewModel.approve = approve
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
    }

    private fun initOnClick() {
        binding.tvPx.setOnClickListenerThrottleFirst{
            showOrderPopup()
        }

        binding.tvChoose.setOnClickListenerThrottleFirst{
            showFilterPopup()
        }
    }

    /**
     * 筛选
     */
    private fun showFilterPopup(){

        if (approve){
            filterPopup.setPeriodData(arrayListOf("提交时间","审批时间"))
        }

        XPopup
            .Builder(this)
            .isDestroyOnDismiss(false)
            .asCustom(filterPopup)
            .show()
    }

    /**
     * 排序
     */
    private fun showOrderPopup(){
        XPopup
            .Builder(this)
            .isDestroyOnDismiss(false)
            .asCustom(orderPopup)
            .show()
    }


    private fun initLoadService() {
        loadService = LoadSir.getDefault().register(binding.container, Callback.OnReloadListener {
            LoadSirUtil.postLoading(loadService!!)
            initData()
            viewModel.dataSource?.invalidate()
        })
        LoadSirUtil.postLoading(loadService!!)
    }

    private fun initRefresh() {
        binding.refresh.setOnRefreshListener {
            initData()
            viewModel.dataSource?.invalidate()
        }
    }

    private fun initRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@SupplementCardListActivity)
            if (itemDecorationCount == 0) {
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        val point = parent.getChildAdapterPosition(view)
                        val count = state.itemCount - 1

                        outRect.left = 12.dp
                        outRect.top = 12.dp
                        outRect.right = 12.dp
                        outRect.bottom = if (point == count) 12.dp else 0.dp

                    }
                })
            }
            adapter = supplementCardListAdapter
        }
    }
}