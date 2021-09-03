package com.daqsoft.module_work.repository.pojo.vo

import com.daqsoft.module_work.R
import com.daqsoft.module_work.widget.tree.LayoutItemType

/**
 * @package name：com.daqsoft.module_work.repository.pojo.vo
 * @date 21/7/2021 上午 11:52
 * @author zp
 * @describe
 */
data class VideoSurveillanceGroup(
    val children: List<VideoSurveillanceGroup>?,
    val id: String,
    val name: String,
    val parentId: Int,


    var isSelected : Boolean = false
) : LayoutItemType {
    override fun getLayoutId(): Int {
        return R.layout.recycleview_video_surveillance_group_item
    }
}

