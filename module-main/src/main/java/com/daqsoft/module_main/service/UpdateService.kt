package com.daqsoft.module_main.service


import android.app.DownloadManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import android.os.IBinder
import android.widget.RemoteViews
import android.widget.Toast
import com.daqsoft.module_main.receiver.UpdateBroadcastReceiver
import com.luck.picture.lib.tools.SPUtils


/**
 * 应用版本更新服务
 *
 * @author 黄熙
 * @version 1.0.0
 * @date 2018/9/4 0004
 * @since JDK 1.8
 */
class UpdateService : Service() {

    companion object{
        const val PERMISSION = "com.daqsoft.travelCultureModule.permission.UpdateBroadcastReceiver"
    }

    /**
     * 下载地址
     */
    private var down_url: String? = ""

    private var app_name: String? = null

    /**
     * 版本更新广播
     */
    private var receiver: UpdateBroadcastReceiver? = null

    internal lateinit var contentView: RemoteViews

    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        try {
            app_name = intent.getStringExtra("app_name")
            down_url = intent.getStringExtra("updatepath")
            downLoad(down_url)
        } catch (e: Exception) {
            e.printStackTrace()
            stopSelf()
        }

        return super.onStartCommand(intent, flags, startId)

    }

    override fun onDestroy() {
        super.onDestroy()
        if (receiver != null)
            unregisterReceiver(receiver)
    }


    /**
     * 下载
     *
     * @param context
     * @param url
     */
    fun downLoad(url: String?) {
        if (url.isNullOrEmpty()) {
            return
        }
        try {
            val serviceString = Context.DOWNLOAD_SERVICE
            // 取得系统的下载服务
            val downloadManager = getSystemService(serviceString) as DownloadManager

            val uri = Uri.parse(url)
            // 创建下载请求对象
            val request = DownloadManager.Request(uri)
            request.allowScanningByMediaScanner()
            request.setVisibleInDownloadsUi(true)
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setMimeType("application/vnd.android.package-archive")
            request.setDestinationInExternalFilesDir( this ,  Environment.DIRECTORY_DOWNLOADS ,  "$app_name.apk" );
            val refernece = downloadManager.enqueue(request)
            SPUtils.getInstance().put("refernece", refernece)

            receiver = UpdateBroadcastReceiver()
            //注册广播接收者，监听下载状态
            registerReceiver(
                receiver,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
            )
        } catch (exception: Exception) {
            Toast.makeText(this, "更新失败", Toast.LENGTH_LONG).show()
        }
    }
}