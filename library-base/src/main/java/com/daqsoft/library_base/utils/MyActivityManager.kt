package com.daqsoft.library_base.utils

import android.app.Activity
import java.lang.ref.WeakReference

/**
 * @package name：com.daqsoft.library_base.utils
 * @date 11/6/2021 下午 5:56
 * @author zp
 * @describe
 */

object MyActivityManager {

    private var sCurrentActivityWeakRef: WeakReference<Activity>? = null

    private val activityUpdateLock = Any()

    fun setCurrentActivity(activity: Activity) {
        synchronized(activityUpdateLock) {
            sCurrentActivityWeakRef = WeakReference<Activity>(activity)
        }
    }

    fun getCurrentActivity(): Activity? {
        var currentActivity: Activity? = null
        synchronized(activityUpdateLock) {
            if (sCurrentActivityWeakRef != null) {
                currentActivity = sCurrentActivityWeakRef!!.get()
            }
        }
        return currentActivity
    }

}