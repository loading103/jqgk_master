package com.daqsoft.module_work.activity

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_work.BR
import com.daqsoft.module_work.R
import com.daqsoft.module_work.adapter.ApprovalManagementAdapter
import com.daqsoft.module_work.databinding.ActivityAddSupplementCardBinding
import com.daqsoft.module_work.databinding.ActivityApprovalManagementBinding
import com.daqsoft.module_work.repository.pojo.vo.ApprovalManagement
import com.daqsoft.module_work.viewmodel.AddSupplementCardViewModel
import com.daqsoft.module_work.viewmodel.ApprovalManagementViewModel
import com.daqsoft.mvvmfoundation.utils.dp
import dagger.hilt.android.AndroidEntryPoint
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.module_work.activity
 * @date 9/7/2021 上午 11:47
 * @author zp
 * @describe  审批管理
 */

@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_APPROVAL_MANAGEMENT)
class ApprovalManagementActivity : AppBaseActivity<ActivityApprovalManagementBinding, ApprovalManagementViewModel>(){

    val approvalManagementAdapter : ApprovalManagementAdapter by lazy {
        ApprovalManagementAdapter().apply {
            itemBinding = ItemBinding.of(ItemBinding.VAR_NONE,R.layout.recycleview_approval_management_item)
            setItemOnClickListener(object : ApprovalManagementAdapter.ItemOnClickListener{
                override fun onClick(position: Int, content: ApprovalManagement) {
                    when(content.title){
                        "请假审批"->{
                            ARouter
                                .getInstance()
                                .build(ARouterPath.Workbench.PAGER_LEAVE_LIST)
                                .withBoolean("approve",true)
                                .navigation()
                        }
                        "补卡审批"->{
                            ARouter
                                .getInstance()
                                .build(ARouterPath.Workbench.PAGER_SUPPLEMENT_CARD_LIST)
                                .withBoolean("approve",true)
                                .navigation()
                        }
                    }
                }
            })
        }
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_approval_management
    }

    override fun initViewModel(): ApprovalManagementViewModel? {
        return viewModels<ApprovalManagementViewModel>().value
    }


    override fun initView() {
        super.initView()

        initRecyclerView()

    }

    override fun initData() {
        super.initData()

        approvalManagementAdapter.setItems(arrayListOf<ApprovalManagement>(
            ApprovalManagement(R.mipmap.shengpiguanli_qingjiashengping,"请假审批","调休假以外的请假"),
            ApprovalManagement(R.mipmap.shengpiguanli_bukasp,"补卡审批","忘记打卡补卡申请"),
        ))
    }

    private fun initRecyclerView() {
        binding.recyclerView.apply {
            val spanCount = 2
            layoutManager = GridLayoutManager(context!!, spanCount, GridLayoutManager.VERTICAL, false)
            if (itemDecorationCount == 0) {
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        val position = parent.getChildAdapterPosition(view)
                        var spacing = 12.dp
                        val column = position % spanCount
                        outRect.left = column * spacing / spanCount
                        outRect.right = spacing - (column + 1) * spacing / spanCount
                    }
                })
            }
            adapter = approvalManagementAdapter
        }
    }
}

