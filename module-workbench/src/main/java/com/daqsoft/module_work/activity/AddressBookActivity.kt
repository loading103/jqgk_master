
package com.daqsoft.module_work.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.PhoneUtils
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.LoadSirUtil
import com.daqsoft.module_work.BR
import com.daqsoft.module_work.R
import com.daqsoft.module_work.adapter.AddressBookAdapter
import com.daqsoft.module_work.databinding.ActivityAddressBookBinding
import com.daqsoft.library_common.bean.Employee
import com.daqsoft.library_common.widget.CallPopup
import com.daqsoft.module_work.widget.SideBar
import com.daqsoft.module_work.warrper.StickinessDecoration
import com.daqsoft.module_work.viewmodel.AddressBookViewModel
import com.daqsoft.module_work.viewmodel.itemviewmodel.AddressBookItemViewModel
import com.daqsoft.module_work.viewmodel.itemviewmodel.AddressBookTitleViewModel
import com.kingja.loadsir.callback.Callback
import com.kingja.loadsir.core.LoadSir
import com.lxj.xpopup.XPopup
import dagger.hilt.android.AndroidEntryPoint

/**
 * @package name：com.daqsoft.module_work.activity
 * @date 8/5/2021 上午 11:18
 * @author zp
 * @describe 通讯录
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_ADDRESS_BOOK)
class AddressBookActivity : AppBaseActivity<ActivityAddressBookBinding, AddressBookViewModel>() {

    var position = 0
    var move = false
    val manager : LinearLayoutManager by lazy { LinearLayoutManager(this) }
    val stickinessDecoration : StickinessDecoration by lazy { StickinessDecoration() }

    val addressBookAdapter : AddressBookAdapter by lazy { AddressBookAdapter(this).apply {
        setOnClickListener(object : AddressBookAdapter.OnClickListener{
            override fun itemOnClick(position: Int, item: AddressBookItemViewModel) {

            }

            override fun callOnClick(position: Int, item: AddressBookItemViewModel) {
                showCallPopUp(item.employee)
            }

        })

    } }


    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_address_book
    }

    override fun initViewModel(): AddressBookViewModel? {
        return viewModels<AddressBookViewModel>().value
    }

    override fun initView() {
        super.initView()

        initRecyclerView()
        initSideBar()
        initOnClick()
        initLoadService()
    }

    private fun initLoadService() {
        loadService = LoadSir.getDefault().register(binding.recyclerView, Callback.OnReloadListener {
            initData()
        })
    }

    override fun initData() {
        super.initData()
        LoadSirUtil.postLoading(loadService!!)
        viewModel.getEmployee()
    }

    override fun initViewObservable() {
        super.initViewObservable()
        viewModel.contactList.observe(this, Observer {
            addressBookAdapter.setData(it)
        })

        viewModel.searchLiveEvent.observe(this, Observer {
            if (it){
                binding.recyclerView.removeItemDecoration(stickinessDecoration)
            }else{
                if (binding.recyclerView.itemDecorationCount == 0) {
                    binding.recyclerView.addItemDecoration(stickinessDecoration)
                }
            }

        })
    }

    private fun initOnClick() {
        binding.companyCl.setOnClickListener {
            ARouter
                .getInstance()
                .build(ARouterPath.Workbench.PAGER_ORGANIZATION_CONTAINER)
                .navigation()
        }
    }

    private fun initSideBar() {
        binding.sideBar.setOnSelectIndexItemListener(object : SideBar.OnSelectIndexItemListener{
            override fun onSelectIndexItem(index: String?) {
                if(index == null){
                    return
                }
                val first = addressBookAdapter.getData().firstOrNull{
                    if (it is AddressBookTitleViewModel){
                        it.content == index
                    }
                    return@firstOrNull false
                } ?: return
                addressBookAdapter.getData().indexOf(first)
                smoothScrollToPosition(addressBookAdapter.getData().indexOf(first))
            }
        })
    }


    /**
     * 将指定item滚动到顶部
     * @param index Int
     */
    private fun smoothScrollToPosition(index : Int){
        position = index
        val firstItem: Int = manager.findFirstVisibleItemPosition()
        val lastItem: Int = manager.findLastVisibleItemPosition()
        if (index <= firstItem) {
            // 当要置顶的项在当前显示的第一个项的前面时
            binding.recyclerView.smoothScrollToPosition(index)
        } else if (index <= lastItem) {
            // 当要置顶的项已经在屏幕上显示时
            val top: Int = binding.recyclerView.getChildAt(index - firstItem).top
            binding.recyclerView.smoothScrollBy(0, top)
        } else {
            // 当要置顶的项在当前显示的最后一项的后面时
            binding.recyclerView.smoothScrollToPosition(index)
            // 这里这个变量是用在RecyclerView滚动监听里面的
            move = true
        }
    }

    private fun initRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = manager
            if (itemDecorationCount == 0) {
                addItemDecoration(stickinessDecoration)
            }
            adapter = addressBookAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    // 在这里进行第二次滚动
                    if (move) {
                        move = false
                        val n: Int = position - manager.findFirstVisibleItemPosition()
                        if (0 <= n && n < recyclerView.childCount) {
                            val top = recyclerView.getChildAt(n).top
                            recyclerView.smoothScrollBy(0, top)
                        }
                    }
                }
            })
        }
    }


    fun showCallPopUp(employee: Employee){
        XPopup
            .Builder(this)
            .enableDrag(false)
            .dismissOnTouchOutside(false)
            .isDestroyOnDismiss(true)
            .asCustom(CallPopup(this,employee.mobile).apply {
                setOnClickListener(object : CallPopup.OnClickListener{
                    override fun onClick(mobile : String) {
                        PhoneUtils.dial(mobile)
                        this@apply.dismiss()
                    }
                })
            })
            .show()

    }

}

