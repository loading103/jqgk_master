package com.daqsoft.module_task.activity

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.LoadSirUtil
import com.daqsoft.library_base.utils.executePaging
import com.daqsoft.module_task.BR
import com.daqsoft.module_task.R
import com.daqsoft.module_task.adapter.WeeklySummaryAdapter
import com.daqsoft.module_task.databinding.ActivityWeeklySummaryListBinding
import com.daqsoft.module_task.viewmodel.WeeklySummaryListViewModel
import com.daqsoft.mvvmfoundation.utils.dp
import com.kingja.loadsir.callback.Callback
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import dagger.hilt.android.AndroidEntryPoint

/**
 * @package name：com.daqsoft.module_task.activity
 * @date 14/5/2021 上午 9:36
 * @author zp
 * @describe 周总结列表
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Task.PAGER_WEEKLY_SUMMARY_LIST)
class WeeklySummaryListActivity : AppBaseActivity<ActivityWeeklySummaryListBinding, WeeklySummaryListViewModel>() {


    val weeklySummaryAdapter : WeeklySummaryAdapter by lazy { WeeklySummaryAdapter() }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_weekly_summary_list
    }

    override fun initViewModel(): WeeklySummaryListViewModel? {
        return viewModels<WeeklySummaryListViewModel>().value
    }

    override fun initView() {
        super.initView()
        initLoadService()
        initRecycleView()
    }

    private fun initLoadService(){
        loadService =  LoadSir.getDefault().register(binding.recyclerView, object : Callback.OnReloadListener {
            override fun onReload(v: View?) {
                LoadSirUtil.postLoading(loadService!!)
                viewModel.dataSource?.invalidate()
            }
        })
        LoadSirUtil.postLoading(loadService!!)
    }


    override fun initViewObservable() {
        super.initViewObservable()
        viewModel.pageList.observe(this, Observer {
            if (it.isEmpty()){
                return@Observer
            }
            binding.recyclerView.executePaging(it, viewModel.diff)
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
                        outRect.top = 12.dp
                        outRect.left = 12.dp
                        outRect.right = 12.dp
                        if (position == count){
                            outRect.bottom = 12.dp
                        }
                    }
                })
            }

            adapter = weeklySummaryAdapter
        }
    }


}