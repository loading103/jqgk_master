package com.daqsoft.module_main.uitls

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.AppUtils
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_main.activity.MainActivity
import com.daqsoft.module_main.repository.pojo.vo.MyNotificationExtra
import com.google.gson.Gson

/**
 * @package name：com.daqsoft.module_main.uitls
 * @date 8/1/2021 上午 10:09
 * @author zp
 * @describe
 */
object NotifyMessageOpenHelper {

    /**
     * 处理跳转
     * @param context Context
     * @param message NotificationMessage?
     */
    fun handleJump(context: Context,message : String?){
        val extra = Gson().fromJson(message,MyNotificationExtra::class.java)
        if (AppUtils.isAppRunning(AppUtils.getAppPackageName())) {
            if (!AppUtils.isAppForeground(AppUtils.getAppPackageName())) {
                val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                val taskInfoList = activityManager.getRunningTasks(100)
                for (taskInfo in taskInfoList) {
                    if (taskInfo.topActivity!!.packageName == AppUtils.getAppPackageName()) {
                        activityManager.moveTaskToFront(taskInfo.id, 0)
                        break
                    }
                }
            }
            if (ActivityUtils.isActivityExists(AppUtils.getAppPackageName(), MainActivity::class.java.name)){
                pageJump(extra)
            }else{
                var launchIntent =  context.packageManager.getLaunchIntentForPackage(AppUtils.getAppPackageName())
                if (launchIntent == null){
                    launchIntent = Intent(Intent.ACTION_MAIN)
                    launchIntent.component = ComponentName(AppUtils.getAppPackageName(), "com.daqsoft.module_main.activity.MainActivity")
                    launchIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                }
                launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                val bundle = Bundle()
                bundle.putParcelable("notifyExtra",extra)
                launchIntent.putExtra("notifyBundle",bundle)
                context.startActivity(launchIntent)
            }

        } else {
            var launchIntent =  context.packageManager.getLaunchIntentForPackage(AppUtils.getAppPackageName())
            if (launchIntent == null){
                launchIntent = Intent(Intent.ACTION_MAIN)
                launchIntent.component = ComponentName(AppUtils.getAppPackageName(), "com.daqsoft.module_main.activity.MainActivity")
                launchIntent.addCategory(Intent.CATEGORY_LAUNCHER)
            }
            launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
            val bundle = Bundle()
            bundle.putParcelable("notifyExtra",extra)
            launchIntent.putExtra("notifyBundle",bundle)
            context.startActivity(launchIntent)
        }
    }


    /**
     * 页面跳转
     * @param extra MyNotificationExtra
     */
    fun pageJump(extra: MyNotificationExtra){
        when(extra.businessType){
            1.toString() ->{
                ARouter
                    .getInstance()
                    .build(ARouterPath.Workbench.PAGER_ALARM_DETAILS)
                    .withString("from", ARouterPath.Task.PAGER_TASK_LIST)
                    .withString("id", extra.dataId)
                    .withInt("state",2)
                    .navigation()
            }
        }

    }

}