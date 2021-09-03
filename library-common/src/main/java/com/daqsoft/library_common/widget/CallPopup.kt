package com.daqsoft.library_common.widget

import android.content.Context
import android.widget.TextView
import com.blankj.utilcode.util.RegexUtils
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.library_common.R
import com.daqsoft.library_common.bean.Employee
import com.lxj.xpopup.core.BottomPopupView

/**
 * @describe 拨号
 */
class CallPopup(context: Context, private val mobile : String) : BottomPopupView(context) {

    override fun getImplLayoutId(): Int {
        return R.layout.layout_popup_call
    }

    override fun onCreate() {
        super.onCreate()

        val call = findViewById<TextView>(R.id.call)
        var phone = mobile
        if(RegexUtils.isMobileSimple(mobile)){
            val chars = mobile.toCharArray()
            val sb = StringBuilder()
            chars.forEachIndexed { index, c ->
                if (index == 3 || index == 7){
                    sb.append(' ')
                }
                sb.append(c)
            }
            phone = sb.toString()
        }
        call.text = "呼叫 ${phone}"
        call.setOnClickListenerThrottleFirst {
            clickListener?.onClick(mobile)
        }

        val cancel = findViewById<TextView>(R.id.cancel)
        cancel.setOnClickListener {
            this.dismiss()
        }
    }



    private var clickListener : OnClickListener? = null

    fun setOnClickListener(listener: OnClickListener){
        this.clickListener = listener
    }

    interface OnClickListener{

        fun onClick(mobile : String)

    }

}