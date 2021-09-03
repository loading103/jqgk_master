package com.daqsoft.library_common.bean

import com.contrarywind.interfaces.IPickerViewData

/**
 * @package name：com.daqsoft.library_common.bean
 * @date 2/6/2021 下午 3:13
 * @author zp
 * @describe
 */


data class Options(
    var value: String,
    var label: String,
    var children: List<Options>?
): IPickerViewData {
    override fun getPickerViewText(): String {
        return label
    }

}
