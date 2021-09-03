package com.daqsoft.library_base.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

/**
 * @package name：com.daqsoft.library_base.utils
 * @date 15/1/2021 上午 10:01
 * @author zp
 * @describe
 */
object TimeUtils {


    /**
     * 一分钟
     */
    const val minute = 60 * 1000

    /**
     * 一小时
     */
    const val hour = 60 * minute

    /**
     * 一天
     */
    const val day = 24 * hour

    /**
     * 一月
     */
    const val month = 31 * day


    /**
     * 距现在多久
     */
    @SuppressLint("SimpleDateFormat")
    fun howLongAgo(format: String?, dateString: String?):String{
        if (format.isNullOrBlank() || dateString.isNullOrBlank()){
            return ""
        }
        val date  = SimpleDateFormat(format).apply {
            timeZone = TimeZone.getTimeZone("GMT+8:00")
        }.parse(dateString)!!
        val time = date.time
        val now = Date().time
        val diff = now - time

        // 小于一分钟
        if (diff < minute){
            return "刚刚"
        }

        // 大于一分钟 小于一小时
        if (diff > minute && diff < hour){
            return "${diff / minute}分钟前"
        }

        // 大于一小时 小于 一天
        if (diff > hour && diff < day){
            return "${diff / hour}小时前"
        }
        // 大于一天
        if(diff > day ){
            val dayAgo = diff / day
            if (dayAgo > 30){
                return ""
            }
            return "${dayAgo}天前"
        }

        return ""
    }



    fun stringToDate(date: String,formatStr: String): Date? {
        return try {
            val format = SimpleDateFormat(formatStr)
            format.parse(date)
        }catch (e:Exception){
            e.printStackTrace()
            null
        }
    }



    fun coverDateFormat(stamp: String, ff:String, tf:String): String {
        return try {
            val fromFormat  = SimpleDateFormat(ff)
            val fromDate = fromFormat.parse(stamp)
            val toFormat  = SimpleDateFormat(tf)
            toFormat.format(fromDate)
        }catch (e:Exception){
            e.printStackTrace()
            ""
        }
    }


    fun minuteToHour(m:Int):String{
        val hours: Int = m / 60
        val minutes: Int = m % 60
        return String.format("%d小时%02d分钟", hours, minutes)
    }



}