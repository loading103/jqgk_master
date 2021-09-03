package com.daqsoft.library_base.global

/**
 * @package name：com.daqsoft.library_base.global
 * @date 30/11/2020 下午 4:13
 * @author zp
 * @describe
 */

object HttpGlobal {

    const val OSS_UPLOAD = "http://file.geeker.com.cn/upload"

    /****版本更新相关*****/
    object Update {
        const val URL = "http://app.daqsoft.com/appserives/Services.aspx"
        const val APP_ID = "58214"
        const val METHOD ="AppVersion"
        const val TOKEN ="daqsoft"
        const val APP_TYPE="1"
        const val VERSION_CODE =""
        const val isMustUpdate =false
    }
    /****版本更新相关*****/


    /**
     * 隐私协议
     */
    const val PRIVATE_XIEYI = "http://samc.daqsoft.com/command-dispatch/privacy"

}