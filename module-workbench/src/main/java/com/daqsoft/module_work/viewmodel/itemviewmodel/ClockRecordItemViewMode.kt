package com.daqsoft.module_work.viewmodel.itemviewmodel

import android.app.Application
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.view.View
import androidx.databinding.ObservableField
import cn.iwgang.simplifyspan.SimplifySpanBuild
import cn.iwgang.simplifyspan.unit.SpecialTextUnit
import com.daqsoft.module_work.R
import com.daqsoft.module_work.viewmodel.AddressBookViewModel
import com.daqsoft.module_work.viewmodel.ClockViewMode
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel

/**
 * @package name：com.daqsoft.module_work.viewmodel.itemviewmodel
 * @date 10/5/2021 下午 8:24
 * @author zp
 * @describe 打卡记录 item viewModel
 */
class ClockRecordItemViewMode  (
    private val clockViewMode: ClockViewMode,
    val first : Boolean = false,
    val last : Boolean = false
) : ItemViewModel<ClockViewMode>(clockViewMode){

    val topLine = ObservableField(View.GONE)
    val bottomLine = ObservableField(View.GONE)
    var textColor = clockViewMode.getApplication<Application>().resources.getColor(R.color.color_59abff)
    val titleSpannable = ObservableField<SpannableStringBuilder>()


    init {

        if (first){
            topLine.set(View.GONE)
        }else{
            topLine.set(View.VISIBLE)
        }

        if (last){
            bottomLine.set(View.GONE)
            textColor = clockViewMode.getApplication<Application>().resources.getColor(R.color.color_fe7800)
        }else{
            bottomLine.set(View.VISIBLE)
            textColor = clockViewMode.getApplication<Application>().resources.getColor(R.color.color_59abff)
        }

        val ssb = SimplifySpanBuild()
            .append(SpecialTextUnit("上班打卡  09:00:00").setTextColor(textColor))
            .build()
        titleSpannable.set(ssb)

    }

}