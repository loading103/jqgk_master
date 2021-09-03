package com.daqsoft.library_common.widget

import android.content.Context
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.library_common.R
import com.lxj.xpopup.core.BottomPopupView

/**
 * @describe 迟到
 */
class RemarkPopup : BottomPopupView {


    var title:String = ""
    var hint:String = ""
    var determine:String = ""


    constructor(
        context: Context,
        title: String = "",
        hint:String = "",
        determine:String = ""
    ):super(context){

        this.title = title
        this.hint = hint
        this.determine = determine
    }

    override fun getImplLayoutId(): Int {
        return R.layout.layout_popup_remark
    }

    override fun onCreate() {
        super.onCreate()

        val title = findViewById<TextView>(R.id.title)
        title.text = this.title

        val close = findViewById<ImageView>(R.id.close)
        close.setOnClickListenerThrottleFirst {
            dismiss()
        }

        val remark = findViewById<EditText>(R.id.remark)
        remark.hint =  this.hint

        val determine = findViewById<TextView>(R.id.determine)
        determine.text = this.determine
        determine.setOnClickListenerThrottleFirst {
            onClickListener?.onClick(remark.text.toString())
        }
    }



    private var onClickListener : OnClickListener? = null

    fun setOnClickListener(listener: OnClickListener){
        this.onClickListener = listener
    }

    interface OnClickListener{

        fun onClick(remark : String)

    }

}

