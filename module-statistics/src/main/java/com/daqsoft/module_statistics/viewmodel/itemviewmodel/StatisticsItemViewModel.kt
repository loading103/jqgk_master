package com.daqsoft.module_statistics.viewmodel.itemviewmodel

import com.daqsoft.library_common.bean.AppMenu
import com.daqsoft.module_statistics.viewmodel.StatisticsViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel

/**
 * @package name：com.daqsoft.module_statistics.viewmodel.itemviewmodel
 * @date 9/6/2021 上午 9:55
 * @author zp
 * @describe
 */
class StatisticsItemViewModel (
    private val statisticsViewModel: StatisticsViewModel,
) : ItemViewModel<StatisticsViewModel>(statisticsViewModel) {

}