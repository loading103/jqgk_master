package com.daqsoft.library_base.viewadapter

import androidx.databinding.BindingAdapter
import com.ruffian.library.widget.RTextView

/**
 * @package name：com.daqsoft.library_base.viewadapter
 * @date 11/5/2021 上午 9:09
 * @author zp
 * @describe
 */
class ViewAdapter {

    companion object {

        @JvmStatic
        @BindingAdapter(value = ["textColorNormal"], requireAll = false)
        fun setTextColorNormal(rTextView : RTextView, res: Int) {
            rTextView.helper.textColorNormal = res
        }

    }
}