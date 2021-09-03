package com.daqsoft.library_base.utils

import android.os.Handler
import android.os.Looper
import com.daqsoft.library_base.wrapper.loadsircallback.EmptyCallback
import com.daqsoft.library_base.wrapper.loadsircallback.ErrorCallback
import com.daqsoft.library_base.wrapper.loadsircallback.LoadingCallback
import com.kingja.loadsir.callback.Callback
import com.kingja.loadsir.callback.SuccessCallback
import com.kingja.loadsir.core.LoadService

/**
 * @package name：com.daqsoft.library_base.utils
 * @date 14/5/2021 下午 1:40
 * @author zp
 * @describe
 */
class LoadSirUtil {

    companion object {

        fun postLoading(loadService: LoadService<*>, delay: Long = 0L) {
            postCallback(loadService,LoadingCallback::class.java,delay)
        }

        fun postEmpty(loadService: LoadService<*>, delay: Long = 0L) {
            postCallback(loadService, EmptyCallback::class.java,delay)
        }

        fun postError(loadService: LoadService<*>, delay: Long = 0L) {
            postCallback(loadService, ErrorCallback::class.java,delay)
        }

        fun postSuccess(loadService: LoadService<*>, delay: Long = 0L) {
            postCallback(loadService,SuccessCallback::class.java,delay)
        }


        fun postCallback(loadService: LoadService<*>, clazz: Class<out Callback?>?, delay: Long = 0L) {
            Handler(Looper.getMainLooper()).postDelayed({
                loadService.showCallback(clazz)
            }, delay)
        }
    }
}