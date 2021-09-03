package com.daqsoft.module_mine.fragment

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.GsonUtils
import com.daqsoft.library_base.base.AppBaseFragment
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_common.bean.AppMenu
import com.daqsoft.module_mine.BR
import com.daqsoft.module_mine.R
import com.daqsoft.module_mine.databinding.FragmentMineBinding
import com.daqsoft.module_mine.viewmodel.MineViewModel
import com.daqsoft.mvvmfoundation.utils.dp
import com.google.gson.reflect.TypeToken
import com.lxj.xpopup.XPopup
import dagger.hilt.android.AndroidEntryPoint

/**
 * @ClassName    MineFragment
 * @Description
 * @Author       yuxc
 * @CreateDate   2021/5/7
 */

@AndroidEntryPoint
@Route(path = ARouterPath.Mine.PAGER_MINE)
class MineFragment : AppBaseFragment<FragmentMineBinding, MineViewModel>() {

    @JvmField
    @Autowired
    var menuJson : String ? = ""
    var menu : List<AppMenu> ? = null

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
        return R.layout.fragment_mine
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initViewModel(): MineViewModel? {
        return requireActivity().viewModels<MineViewModel>().value
    }

    override fun initView() {
        super.initView()
        initRecycleView()
    }

    private fun initRecycleView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            if (itemDecorationCount == 0) {
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        val position = parent.getChildAdapterPosition(view)
                        if (position != 0) {
                            outRect.top = 8.dp
                        }
                    }
                })
            }

        }

    }


    override fun initData() {
        super.initData()

        viewModel.getUserInfo()

        menu?.let {
            viewModel.createItem(it)
        }
    }

    override fun initViewObservable() {
        super.initViewObservable()
        viewModel.logoutLiveData.observe(this, Observer {
            showLogOutPopup()
        })
    }


    /**
     * 退出提示
     */
    private fun showLogOutPopup(){
        XPopup
            .Builder(requireActivity())
            .isDestroyOnDismiss(true)
            .asConfirm(
                "提示",
                "确定要退出系统?",
                "取消",
                "确定",
                {
                    viewModel.logout()
                },
                null,
                false,
                R.layout.layout_popup_confirm
            )
            .show()
    }
}
