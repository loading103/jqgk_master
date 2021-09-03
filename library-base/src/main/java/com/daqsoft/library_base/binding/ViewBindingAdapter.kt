package com.daqsoft.library_base.binding

import android.view.View
import androidx.databinding.BindingAdapter
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.jakewharton.rxbinding4.view.longClicks
import com.ruffian.library.widget.RTextView
import com.ruffian.library.widget.iface.RHelper

/**
 * @package name：com.daqsoft.library_base.viewbinding
 * @date 14/5/2021 下午 4:29
 * @author zp
 * @describe
 */
class ViewBindingAdapter {


    companion object{

        @JvmStatic
        @BindingAdapter(value = ["backgroundNormal"], requireAll = false)
        fun setRTextViewBackgroundNormal(view: View, res : Int) {
            if (view is RHelper<*>){
                try {
                    view.helper.backgroundColorNormal = view.context.resources.getColor(res)
                }catch (e:Exception){
                    view.helper.backgroundColorNormal = res
                }
            }
        }
    }
}