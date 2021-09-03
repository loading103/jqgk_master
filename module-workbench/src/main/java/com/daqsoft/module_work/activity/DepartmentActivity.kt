package com.daqsoft.module_work.activity

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_work.BR
import com.daqsoft.module_work.R
import com.daqsoft.module_work.adapter.DepartmentTreeBinder
import com.daqsoft.module_work.adapter.StaffTreeBinder
import com.daqsoft.module_work.databinding.ActivityDepartmentBinding
import com.daqsoft.module_work.repository.pojo.vo.Department
import com.daqsoft.module_work.viewmodel.DepartmentViewModel
import com.daqsoft.module_work.widget.tree.TreeNode
import com.daqsoft.module_work.widget.tree.TreeViewAdapter
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

/**
 * @package name：com.daqsoft.module_work.activity
 * @date 10/5/2021 上午 10:08
 * @author zp
 * @describe 部门
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_DEPARTMENT)
class DepartmentActivity : AppBaseActivity<ActivityDepartmentBinding , DepartmentViewModel>() {

    val treeViewAdapter: TreeViewAdapter by lazy {
        TreeViewAdapter(arrayListOf(), arrayListOf(DepartmentTreeBinder(), StaffTreeBinder())).apply {
            setOnTreeNodeListener(object : TreeViewAdapter.OnTreeNodeListener{
            override fun onClick(node: TreeNode<*>?, holder: RecyclerView.ViewHolder?): Boolean {
                if (!node!!.isLeaf) {
                    onToggle(!node.isExpand, holder)
                }
                return false
            }

            override fun onToggle(isExpand: Boolean, holder: RecyclerView.ViewHolder?) {
                val deptViewHolder: DepartmentTreeBinder.ViewHolder = holder as DepartmentTreeBinder.ViewHolder
                val arrow: ImageView = deptViewHolder.arrow
                val rotateDegree = if (isExpand) 90 else -90
                arrow.animate().rotationBy(rotateDegree.toFloat()).start()
            }
        })
        }
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_department
    }

    override fun initViewModel(): DepartmentViewModel? {
        return viewModels<DepartmentViewModel>().value
    }

    override fun initView() {
        super.initView()

        initRecyclerView()
    }


    override fun initData() {
        super.initData()
    }

    private fun initRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@DepartmentActivity)
            adapter = treeViewAdapter
        }
    }


}