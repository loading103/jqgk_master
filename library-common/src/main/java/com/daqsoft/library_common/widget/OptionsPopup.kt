package com.daqsoft.library_common.widget

import android.content.Context
import android.widget.LinearLayout
import android.widget.TextView
import com.bigkoo.pickerview.listener.OnOptionsSelectChangeListener
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.bigkoo.pickerview.view.WheelOptions
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.library_common.R
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.util.XPopupUtils

/**
 * @package name：com.daqsoft.library_common.widget
 * @date 2/6/2021 下午 4:11
 * @author zp
 * @describe
 */
class OptionsPopup<T>(context: Context): BottomPopupView(context) {

    private var wheelOptions: WheelOptions<T>? = null

    override fun getImplLayoutId(): Int {
        return R.layout.layout_popup_options
    }

    override fun onCreate() {
        super.onCreate()

        val cancel = findViewById<TextView>(R.id.cancel)
        cancel.setOnClickListenerThrottleFirst {
            dismiss()
        }
        val determine = findViewById<TextView>(R.id.determine)
        determine.setOnClickListenerThrottleFirst {
            returnData()
            dismiss()
        }

        // ----滚轮布局
        val optionsPicker = findViewById<LinearLayout>(R.id.optionspicker)
        optionsPicker.setBackgroundColor(context.resources.getColor(R.color.white))

        wheelOptions = WheelOptions<T>(optionsPicker, true)
        wheelOptions?.setPicker(options1Items, options2Items, options3Items)
        reSetCurrentItems()

        optionsSelectChangeListener?.let {
            wheelOptions?.setOptionsSelectChangeListener(it)
        }
        wheelOptions?.setCyclic(false,false,false)

        wheelOptions?.setTextContentSize(16)
        wheelOptions?.setItemsVisible(7)
        wheelOptions?.setAlphaGradient(false)

        wheelOptions?.setTextColorOut(context.resources.getColor(R.color.color_999999))
        wheelOptions?.setTextColorCenter(context.resources.getColor(R.color.color_333333))

    }


    private fun reSetCurrentItems() {
        wheelOptions?.setCurrentItems(
            option1,
            option2,
            option3
        )
    }

    override fun getMaxHeight(): Int {
        return (XPopupUtils.getScreenHeight(context)*0.95f).toInt()
    }

    var optionsSelectChangeListener: OnOptionsSelectChangeListener ? = null

    fun setOnOptionsSelectChangeListener(listener: OnOptionsSelectChangeListener) {
        this.optionsSelectChangeListener = listener
    }

    var optionsSelectListener: OnOptionsSelectListener? = null
    fun setOnOptionsSelectListener(listener: OnOptionsSelectListener) {
        this.optionsSelectListener = listener
    }

    //抽离接口回调的方法
    private fun returnData() {
        if (optionsSelectListener != null) {
            val optionsCurrentItems = wheelOptions!!.currentItems
            optionsSelectListener!!.onOptionsSelect(
                optionsCurrentItems[0],
                optionsCurrentItems[1],
                optionsCurrentItems[2],
                this
            )
        }
    }


    var option1:Int = 0
    var option2:Int = 0
    var option3:Int = 0
    /**
     * 设置默认选中项
     *
     * @param option1
     */
    fun setSelectOptions(option1: Int = 0, option2: Int = 0, option3: Int = 0) {
        this.option1 = option1
        this.option2 = option2
        this.option3 = option3
        reSetCurrentItems()
    }


    var  options1Items: List<T> = arrayListOf()
    var  options2Items: List<List<T>>? = arrayListOf()
    var  options3Items: List<List<List<T>>>? = arrayListOf()


    fun setPicker(
        options1Items: List<T>,
        options2Items: List<List<T>>? = null,
        options3Items: List<List<List<T>>>? = null
    ) {

        this.options1Items = options1Items
        this.options2Items = options2Items
        this.options3Items = options3Items
        reSetCurrentItems()
    }

}