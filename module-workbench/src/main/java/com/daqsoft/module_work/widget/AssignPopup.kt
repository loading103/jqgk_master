package com.daqsoft.module_work.widget

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.blankj.utilcode.util.KeyboardUtils
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.library_common.bean.Employee
import com.daqsoft.library_common.widget.EmployeePopup
import com.daqsoft.module_work.R
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.interfaces.SimpleCallback

/**
 * @package name：com.daqsoft.module_task.widget
 * @date 26/5/2021 下午 4:31
 * @author zp
 * @describe  指派
 */
class AssignPopup(context: Context) : BottomPopupView(context) {

    private var assign : TextView ? = null
    private var assignArrow: ImageView ? = null

    var selectEmployee : Employee? = null
    private val employeePopup: EmployeePopup by lazy {
        EmployeePopup(context).apply {
            setItemOnClickListener(object : EmployeePopup.ItemOnClickListener {
                override fun onClick(position: Int, employee: Employee) {
                    selectEmployee = employee
                    assign?.text = employee.name
                    assignArrow?.setImageResource(R.mipmap.delete)
                }
            })
        }
    }

    override fun getImplLayoutId(): Int {
        return R.layout.layout_popup_assign_alarm
    }

    override fun onCreate() {
        super.onCreate()

        val close = findViewById<ImageView>(R.id.close)
        close.setOnClickListenerThrottleFirst {
            dismiss()
        }

        assign = findViewById<TextView>(R.id.assign)
        assign?.setOnClickListenerThrottleFirst {
            showEmployeePopup()
        }

        assignArrow = findViewById<ImageView>(R.id.assign_arrow)
        assignArrow?.setOnClickListenerThrottleFirst {
            if (selectEmployee != null){
                selectEmployee = null
                assign?.text = ""
                assignArrow?.setImageResource(R.mipmap.rwdb_jiantou)
            }
        }

        val content = findViewById<EditText>(R.id.content)

        val submit = findViewById<TextView>(R.id.submit)
        submit.setOnClickListenerThrottleFirst {
            if (selectEmployee == null){
                ToastUtils.showShortSafe("请选择指派人员")
                return@setOnClickListenerThrottleFirst
            }

            onClickListener?.submit(content.text.toString(), selectEmployee!!)
        }

        val cancel = findViewById<TextView>(R.id.cancel)
        cancel.setOnClickListenerThrottleFirst {
            dismiss()
        }

    }


    fun setEmployeeData(employeeList : List<Employee>){
        employeePopup.setData(employeeList)
    }

    /**
     * 员工
     */
    private fun showEmployeePopup() {
        KeyboardUtils.hideSoftInput(this)
        Handler(Looper.getMainLooper()).postDelayed({
            XPopup
                .Builder(context)
                .setPopupCallback(object : SimpleCallback() {
                    override fun onShow(popupView: BasePopupView?) {
                        assignArrow?.rotation = 270.toFloat()
                    }

                    override fun onDismiss(popupView: BasePopupView?) {
                        assignArrow?.rotation = 90.toFloat()
                    }
                })
                .asCustom(employeePopup)
                .show()
        },100)
    }


    private var onClickListener : OnClickListener? = null

    fun setOnClickListener(listener: OnClickListener){
        this.onClickListener = listener
    }

    interface OnClickListener{

        fun submit(remark : String,employee : Employee)


    }




}

