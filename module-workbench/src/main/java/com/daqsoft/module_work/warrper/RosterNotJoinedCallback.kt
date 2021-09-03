package com.daqsoft.module_work.warrper

import android.content.Context
import android.view.View
import com.daqsoft.module_work.R
import com.kingja.loadsir.callback.Callback

/**
 * @package name：com.daqsoft.module_work.warrper
 * @date 16/7/2021 上午 11:34
 * @author zp
 * @describe
 */
class RosterNotJoinedCallback : Callback() {
    override fun onCreateView(): Int {
        return R.layout.layout_roster_not_joined
    }


    override fun onReloadEvent(context: Context?, view: View?): Boolean {
        return true
    }
}