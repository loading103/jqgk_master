package com.daqsoft.module_work.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.daqsoft.library_base.base.AppBaseFragment
import com.daqsoft.library_base.utils.LoadSirUtil
import com.daqsoft.module_work.BR
import com.daqsoft.module_work.R
import com.daqsoft.module_work.adapter.SupplementCardDateShiftAdapter
import com.daqsoft.module_work.databinding.FragmentSupplementCardDateShiftBinding
import com.daqsoft.module_work.viewmodel.SupplementCardDateShiftViewModel
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView
import com.kingja.loadsir.callback.Callback
import com.kingja.loadsir.core.LoadSir
import dagger.hilt.android.AndroidEntryPoint
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.module_work.fragment
 * @date 8/7/2021 下午 2:24
 * @author zp
 * @describe  补卡日期选择 班次
 */
@AndroidEntryPoint
class SupplementCardDateShiftFragment : AppBaseFragment<FragmentSupplementCardDateShiftBinding, SupplementCardDateShiftViewModel>(){

    var calendar: Calendar? = null

    companion object{

        fun newInstance():SupplementCardDateShiftFragment {
            val args = Bundle()
            val fragment = SupplementCardDateShiftFragment()
            fragment.arguments = args
            return fragment
        }
    }

    val shiftAdapter : SupplementCardDateShiftAdapter by lazy{
        SupplementCardDateShiftAdapter().apply {
            itemBinding = ItemBinding.of(ItemBinding.VAR_NONE, R.layout.recycleview_supplement_card_shift_item)
            setItems(arrayListOf("早上 09:00-12:00","下午 13:00-16:00","下午 15:00-20:00","晚上 21:00-23:00"))
            setItemOnClickListener(object : SupplementCardDateShiftAdapter.ItemOnClickListener{
                override fun onClick(position: Int, item: String) {
                    onShiftSelectListener?.onShiftSelect(item)
                }
            })
        }
    }


    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return R.layout.fragment_supplement_card_date_shift
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initViewModel(): SupplementCardDateShiftViewModel? {
        return requireActivity().viewModels<SupplementCardDateShiftViewModel>().value
    }

    override fun initView() {
        super.initView()

        initLoadService()
        initRecycleView()

    }

    override fun initData() {
        super.initData()
        if (calendar == null){
            val today = java.util.Calendar.getInstance()
            val year = today.get(java.util.Calendar.YEAR)
            val month = today.get(java.util.Calendar.MONTH)+1
            val day = today.get(java.util.Calendar.DAY_OF_MONTH)
            calendar = Calendar().apply {
                setYear(year)
                setMonth(month)
                setDay(day)
            }
        }
        viewModel.getShift(calendar!!)
    }

    override fun initViewObservable() {
        super.initViewObservable()

        viewModel.shiftEvent.observe(this, Observer {
            shiftAdapter.setSelectedPosition(-1)
            shiftAdapter.setItems(arrayListOf("早上 09:00-12:00","下午 13:30-18:00"))
        })
    }


    private fun initLoadService() {
        loadService = LoadSir.getDefault().register(binding.recyclerView, Callback.OnReloadListener {
            LoadSirUtil.postLoading(loadService!!)
            initData()
        })
        LoadSirUtil.postLoading(loadService!!)
    }

    private fun initRecycleView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = shiftAdapter
        }
    }


    fun getShift(calendar: Calendar){
        this.calendar = calendar
        LoadSirUtil.postLoading(loadService!!)
        initData()
    }

    private var onShiftSelectListener : OnShiftSelectListener ? = null

    fun setOnCalendarSelectListener(listener: OnShiftSelectListener){
        this.onShiftSelectListener = listener
    }

    interface OnShiftSelectListener{
        fun onShiftSelect(item: String)
    }

}