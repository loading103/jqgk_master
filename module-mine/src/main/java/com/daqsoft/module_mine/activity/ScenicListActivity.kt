package com.daqsoft.module_mine.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.GsonUtils
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_mine.BR
import com.daqsoft.module_mine.R
import com.daqsoft.module_mine.adapter.ScenicListAdapter
import com.daqsoft.module_mine.databinding.ActivityScenicListBinding
import com.daqsoft.module_mine.repository.pojo.vo.Company
import com.daqsoft.module_mine.repository.pojo.vo.CompanyInfo
import com.daqsoft.module_mine.viewmodel.ScenicListViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.module_mine.activity
 * @date 17/5/2021 上午 11:43
 * @author zp
 * @describe 景区列表
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Mine.PAGER_SCENIC_LIST)
class ScenicListActivity : AppBaseActivity<ActivityScenicListBinding, ScenicListViewModel>() {

    @JvmField
    @Autowired
    var companyJson : String? = null

    var company : Company? = null

    private val scenicListAdapter : ScenicListAdapter by lazy { ScenicListAdapter() }

    private var selectedScenic : CompanyInfo ? = null

    override fun initParam() {
        super.initParam()
        if (!companyJson.isNullOrEmpty()){
            val type = object : TypeToken<Company>() {}.type
            company =  GsonUtils.fromJson<Company>(companyJson, type)
        }
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_scenic_list
    }

    override fun initViewModel(): ScenicListViewModel? {
        return viewModels<ScenicListViewModel>().value
    }

    override fun initView() {
        super.initView()

        initOnClick()
        initRecyclerView()
    }

    private fun initOnClick() {
        binding.enter.setOnClickListener {
            if (selectedScenic == null){
                ToastUtils.showShortSafe("请选择您要进入的景区")
                return@setOnClickListener
            }
            viewModel.chooseCompany(selectedScenic!!)
        }
    }

    private fun initRecyclerView() {
        company?.let {
            binding.recyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = scenicListAdapter.apply {
                    itemBinding = ItemBinding.of(BR.info, R.layout.recycleview_scenic_list_item)
                    setItems(it.companyList)
                    setItemOnClickListener(object : ScenicListAdapter.ItemOnClickListener{
                        override fun onClick(position: Int, item: CompanyInfo) {
                            selectedScenic = item
                        }
                    })

                }
            }
        }
    }

    override fun initData() {
        super.initData()
        viewModel.company = company
    }
}