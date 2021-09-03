package com.daqsoft.module_work.warrper

import android.content.Context
import android.view.View
import com.daqsoft.module_work.R
import com.kingja.loadsir.callback.Callback

/**
 * @package name：com.daqsoft.module_work.warrper
 * @date 28/7/2021 下午 4:23
 * @author zp
 * @describe
 */
class NotJoinedCallback : Callback() {

    override fun onCreateView(): Int {
        return R.layout.layout_not_joined
    }


    override fun onReloadEvent(context: Context?, view: View?): Boolean {
        return true
    }
}