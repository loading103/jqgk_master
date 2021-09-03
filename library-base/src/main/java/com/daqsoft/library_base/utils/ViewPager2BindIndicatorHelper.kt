package com.daqsoft.library_base.utils

import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import androidx.viewpager2.widget.ViewPager2
import net.lucode.hackware.magicindicator.MagicIndicator

/**
 * @package name：com.daqsoft.library_base.utils
 * @date 13/5/2021 上午 9:27
 * @author zp
 * @describe
 */
class ViewPager2BindIndicatorHelper {

    companion object {
        fun bind(magicIndicator: MagicIndicator, viewPager2: ViewPager2) {
            viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    magicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels)
                }

                override fun onPageSelected(position: Int) {
                    magicIndicator.onPageSelected(position)
                }

                override fun onPageScrollStateChanged(state: Int) {
                    magicIndicator.onPageScrollStateChanged(state)
                }
            })
        }
    }
}