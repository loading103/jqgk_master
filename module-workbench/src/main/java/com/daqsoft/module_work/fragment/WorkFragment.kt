package com.daqsoft.module_work.fragment

import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.GsonUtils
import com.daqsoft.library_base.base.AppBaseFragment
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.LoadSirUtil
import com.daqsoft.library_base.wrapper.AppBarStateChangeListener
import com.daqsoft.library_common.bean.AppMenu
import com.daqsoft.module_work.BR
import com.daqsoft.module_work.R
import com.daqsoft.module_work.adapter.WorkBenchAdapter
import com.daqsoft.module_work.databinding.FragmentWorkBinding
import com.daqsoft.module_work.viewmodel.WorkViewModel
import com.daqsoft.mvvmfoundation.utils.ToolbarUtils
import com.daqsoft.mvvmfoundation.utils.dp
import com.google.android.material.appbar.AppBarLayout
import com.google.gson.reflect.TypeToken
import com.jeremyliao.liveeventbus.utils.AppUtils
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

/**
 * @describe 工作台页面
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_WORKBENCH)
class WorkFragment : AppBaseFragment<FragmentWorkBinding, WorkViewModel>() {


    @JvmField
    @Autowired
    var menuJson : String ? = ""
    var menu : List<AppMenu> ? = null

    @Inject
    lateinit var workBenchAdapter: WorkBenchAdapter

    override fun initParam() {
        super.initParam()
        if (!menuJson.isNullOrBlank()){
            val type = object : TypeToken<List<AppMenu>>() {}.type
            menu = GsonUtils.fromJson(menuJson,type)
        }
    }

    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return  R.layout.fragment_work
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initViewModel(): WorkViewModel? {
        return requireActivity().viewModels<WorkViewModel>().value
    }

    override fun initView() {
        super.initView()
        initAppBar()
        initRecycleView()

    }

    override fun initData() {
        super.initData()

        menu?.let {
            viewModel.createItem(it)
        }

    }

    /**
     * 初始化 recycleView
     */
    private fun initRecycleView() {
        binding.recyclerView.apply {
            adapter = workBenchAdapter
            if (itemDecorationCount == 0) {
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        val position = parent.getChildAdapterPosition(view)
                        if (position == 0) {
                            outRect.top = 12.dp
                        }
                        outRect.left = 12.dp
                        outRect.right = 12.dp
                        outRect.bottom = 12.dp
                    }
                })
            }

        }
    }

    /**
     * 初始化 AppBar
     */
    private fun initAppBar() {
        binding.appbar.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout, state: State) {
                when (state) {
                    //展开状态
                    State.EXPANDED -> {
                        Timber.e("展开")
                    }
                    //折叠状态
                    State.COLLAPSED -> {
                        Timber.e("折叠")
                    }
                    //中间状态
                    else -> {
                        Timber.e("中间")
                    }
                }
            }

            override fun onOffsetChanged(appBarLayout: AppBarLayout, i: Int) {
                if(i == 0){
                    return
                }
                val cardViewTop = binding.recyclerView.top
                val appbarTop = binding.appbar.top
                val minDistance = ToolbarUtils.getStatusBarHeight() + ToolbarUtils.getToolbarHeight()
                if ((cardViewTop + appbarTop) < minDistance) {
                    viewModel.setBackground(Color.WHITE)
                    viewModel.setTitleTextColor(R.color.color_333333)
                } else {
                    viewModel.setBackground(Color.TRANSPARENT)
                    viewModel.setTitleTextColor(R.color.white)
                }
            }
        })
    }

}
