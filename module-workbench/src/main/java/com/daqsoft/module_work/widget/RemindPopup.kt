package com.daqsoft.module_work.widget

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.module_work.R
import com.lxj.xpopup.core.CenterPopupView

/**
 * @package name：com.daqsoft.module_task.widget
 * @date 26/5/2021 上午 10:05
 * @author zp
 * @describe   无效报警  popup
 */
class RemindPopup(
    context: Context,
    val title:String,
    val content:String
) : CenterPopupView(context) {


    override fun getImplLayoutId(): Int {
        return R.layout.layout_popup_remind
    }

    override fun onCreate() {
        super.onCreate()

        val close = findViewById<ImageView>(R.id.close)
        close.setOnClickListenerThrottleFirst {
            dismiss()
        }


        val determine = findViewById<TextView>(R.id.determine)
        determine.setOnClickListenerThrottleFirst {
            onClickListener?.determine()
        }

        val cancel = findViewById<TextView>(R.id.cancel)
        cancel.setOnClickListenerThrottleFirst {
            dismiss()
        }

        val title = findViewById<TextView>(R.id.title)
        title.text = this.title

        val content = findViewById<TextView>(R.id.content)
        content.text = this.content
        content.setOnClickListenerThrottleFirst {
            dismiss()
        }
    }

    private var onClickListener : OnClickListener? = null

    fun setOnClickListener(listener: OnClickListener){
        this.onClickListener = listener
    }

    interface OnClickListener{

        fun determine()

    }

}
