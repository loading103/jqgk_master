package com.daqsoft.module_work.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.PhoneUtils
import com.daqsoft.library_base.base.AppBaseFragment
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.LoadSirUtil
import com.daqsoft.module_work.BR
import com.daqsoft.module_work.R
import com.daqsoft.module_work.adapter.OrganizationAdapter
import com.daqsoft.module_work.databinding.FragmentOrganizationBinding
import com.daqsoft.module_work.databinding.RecycleviewAddressBookItemBinding
import com.daqsoft.module_work.databinding.RecycleviewDepartmentItemBinding
import com.daqsoft.module_work.repository.pojo.vo.Department
import com.daqsoft.library_common.bean.Employee
import com.daqsoft.module_work.viewmodel.OrganizationContainerViewModel
import com.daqsoft.module_work.viewmodel.OrganizationViewModel
import com.daqsoft.module_work.viewmodel.itemviewmodel.AddressBookItemViewModel
import com.daqsoft.module_work.viewmodel.itemviewmodel.DepartmentItemViewModel
import com.daqsoft.library_common.widget.CallPopup
import com.kingja.loadsir.callback.Callback.OnReloadListener
import com.kingja.loadsir.core.LoadSir
import com.lxj.xpopup.XPopup
import dagger.hilt.android.AndroidEntryPoint

/**
 * @package name：com.daqsoft.module_work.fragment
 * @date 18/5/2021 下午 3:38
 * @author zp
 * @describe 公司组织结构
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_ORGANIZATION)
class OrganizationFragment: AppBaseFragment<FragmentOrganizationBinding, OrganizationViewModel>() {

    lateinit var navController: NavController

    val containerViewModel : OrganizationContainerViewModel by activityViewModels()

    var dept : Department? = null

    val organizationAdapter : OrganizationAdapter by lazy { OrganizationAdapter().apply {
        setItemOnClickListener(object : OrganizationAdapter.ItemOnClickListener{
            override fun deptOnClick(
                position: Int,
                itemBinding: RecycleviewDepartmentItemBinding,
                itemViewModel: DepartmentItemViewModel
            ) {
                val bundle = Bundle()
                bundle.putString("dept",GsonUtils.toJson(itemViewModel.department))
                navController.navigate(R.id.module_workbench_organizationfragment,bundle)
            }

            override fun employeeOnClick(
                position: Int,
                itemBinding: RecycleviewAddressBookItemBinding,
                itemViewModel: AddressBookItemViewModel
            ) {
                ARouter
                    .getInstance()
                    .build(ARouterPath.Mine.PAGER_PERSONAL_INFO)
                    .withString("id", itemViewModel.employee.id.toString())
                    .navigation()
            }

            override fun employeeCallOnClick(
                position: Int,
                itemBinding: RecycleviewAddressBookItemBinding,
                itemViewModel: AddressBookItemViewModel
            ) {
                showCallPopUp(itemViewModel.employee)
            }
        })
    } }

    override fun initParam() {
        super.initParam()

        arguments?.let {
            val deptJson = it.getString("dept")
            if (!deptJson.isNullOrBlank()){
                dept = GsonUtils.fromJson(deptJson,Department::class.java)
            }
        }
    }

    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return R.layout.fragment_organization
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initViewModel(): OrganizationViewModel? {
        return ViewModelProvider(this).get(OrganizationViewModel::class.java)
    }


    override fun initView() {
        super.initView()
        initNavController()
        initRecyclerView()
        initLoadService()
    }

    private fun initLoadService() {
        loadService = LoadSir.getDefault().register(binding.recyclerView, OnReloadListener {
            initData()
        })
    }

    private fun initNavController() {
        navController = Navigation.findNavController(requireView())
    }

    override fun initData() {
        super.initData()
        LoadSirUtil.postLoading(loadService!!)

        if (dept == null){
            viewModel.getOrganization()
            return
        }

        viewModel.initData(dept!!)
    }

    private fun initRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = organizationAdapter
        }
    }


    override fun initViewObservable() {
        super.initViewObservable()

        viewModel.back.observe(this, Observer {
            containerViewModel.finish()
        })
    }


    fun showCallPopUp(employee: Employee){
        XPopup
            .Builder(requireActivity())
            .enableDrag(false)
            .dismissOnTouchOutside(false)
            .isDestroyOnDismiss(true)
            .asCustom(CallPopup(requireActivity(),employee.mobile).apply {
                setOnClickListener(object : CallPopup.OnClickListener{
                    override fun onClick(mobile:String) {
                        PhoneUtils.dial(mobile)
                        this@apply.dismiss()
                    }
                })
            })
            .show()

    }
}