package com.daqsoft.library_common.widget

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.library_common.R
import com.daqsoft.library_common.adapter.EmployeePopupAdapter
import com.daqsoft.library_common.bean.Employee
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.impl.PartShadowPopupView
import com.lxj.xpopup.util.XPopupUtils
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.library_common.widget
 * @date 26/5/2021 下午 5:02
 * @author zp
 * @describe 员工e
 */
class EmployeePopup (context: Context) : BottomPopupView(context) {

    private val employeePopupAdapter : EmployeePopupAdapter by lazy { EmployeePopupAdapter() }

    fun setData(data:List<Employee>){
        this.data = data
        employeePopupAdapter.setItems(data)
        employeePopupAdapter.notifyDataSetChanged()
    }

    private var data = listOf<Employee>()

    fun getData() = data

    override fun getImplLayoutId(): Int {
        return R.layout.layout_popup_top_serach
    }

    override fun onCreate() {
        super.onCreate()

        val search = findViewById<EditText>(R.id.search)
        search.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val searchData = if (p0.isNullOrEmpty()){
                    data
                }else{
                   val find = data.filter {
                       it.name.contains(p0.toString())
                   }
                    find
                }
                employeePopupAdapter.setItems(searchData)
                employeePopupAdapter.notifyDataSetChanged()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        initRecycleView()
    }

    private fun initRecycleView() {
        val recycleView = findViewById<RecyclerView>(R.id.recycle_view)
        recycleView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = employeePopupAdapter.apply {
                itemBinding = ItemBinding.of(ItemBinding.VAR_NONE, R.layout.recyclerview_employee_popup_item)
                setItems(data)
                setItemOnClickListener(object : EmployeePopupAdapter.ItemOnClickListener{
                    override fun onClick(position: Int, employee: Employee) {
                        itemOnClickListener?.onClick(position, employee)
                        postDelayed(Runnable {
                            if (popupInfo.autoDismiss){
                                dismiss()
                            }
                        },100)
                    }
                })
            }
        }
    }

    override fun getMaxHeight(): Int {
        return (XPopupUtils.getScreenHeight(context)*0.45f).toInt()
    }


    /**
     * 设置选中位置
     */
    fun setSelectedPosition(position : Int){
        employeePopupAdapter.setSelectedPosition(position)
    }


    private var itemOnClickListener : ItemOnClickListener? = null

    fun setItemOnClickListener(listener: ItemOnClickListener){
        this.itemOnClickListener = listener
    }

    interface ItemOnClickListener{
        fun onClick(position: Int,employee : Employee)
    }

}