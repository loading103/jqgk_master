package com.daqsoft.module_work.widget

import android.content.Context
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.module_work.R
import com.lxj.xpopup.core.BottomPopupView

/**
 * @package name：com.daqsoft.module_task.widget
 * @date 26/5/2021 上午 9:59
 * @author zp
 * @describe 事件处理
 */
class HandlePopup(context: Context) : BottomPopupView(context) {

    override fun getImplLayoutId(): Int {
        return R.layout.layout_popup_handle_alarm
    }

    override fun onCreate() {
        super.onCreate()

        val close = findViewById<ImageView>(R.id.close)
        close.setOnClickListenerThrottleFirst {
            dismiss()
        }

        val content = findViewById<EditText>(R.id.content)


        val handle = findViewById<TextView>(R.id.handle)
        handle.setOnClickListenerThrottleFirst {
            onClickListener?.handle(content.text.toString())
        }

        val finish = findViewById<TextView>(R.id.finish)
        finish.setOnClickListenerThrottleFirst {
            onClickListener?.finish(content.text.toString())
        }
    }



    private var onClickListener : OnClickListener? = null

    fun setOnClickListener(listener: OnClickListener){
        this.onClickListener = listener
    }

    interface OnClickListener{

        fun handle(remark : String)

        fun finish(remark : String)

    }

}

