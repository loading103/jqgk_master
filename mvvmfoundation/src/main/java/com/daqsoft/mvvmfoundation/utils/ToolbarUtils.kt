package com.daqsoft.mvvmfoundation.utils

import android.content.res.Resources
import android.util.TypedValue

/**
 * @package name：com.daqsoft.mvvmfoundation.utils
 * @date 22/4/2021 下午 3:25
 * @author zp
 * @describe
 */
object ToolbarUtils {

    /**
     * 获取状态栏高度
     * @return Int
     */
    fun getStatusBarHeight(): Int {
        val resources: Resources = ContextUtils.getContext().resources
        val resourceId: Int = resources.getIdentifier("status_bar_height", "dimen", "android")
        val height: Int = resources.getDimensionPixelSize(resourceId)
        return height
    }

    /**
     * 获取Toolbar 高度  系统默认 56dp
     * @return Int
     */
    fun getToolbarHeight(): Int {
        val typedValue = TypedValue()
        if (ContextUtils.getContext().theme.resolveAttribute(
                android.R.attr.actionBarSize,
                typedValue,
                true
            )){
            return TypedValue.complexToDimensionPixelSize(
                typedValue.data,
                ContextUtils.getContext().resources.displayMetrics
            )
        }
        return 56.toFloat().dp
    }
}