package com.daqsoft.library_common.widget

import android.content.Context
import android.graphics.Typeface
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView

/**
 * @package name：com.daqsoft.module_task.widget
 * @date 25/5/2021 上午 11:19
 * @author zp
 * @describe
 */
class BoldPagerTitleView(context: Context) : SimplePagerTitleView(context) {

    override fun onSelected(index: Int, totalCount: Int) {
        super.onSelected(index, totalCount)
        this.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
    }

    override fun onDeselected(index: Int, totalCount: Int) {
        super.onDeselected(index, totalCount)
        this.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
    }
}