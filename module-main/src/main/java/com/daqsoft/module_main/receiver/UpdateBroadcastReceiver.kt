package com.daqsoft.module_main.receiver


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.daqsoft.mvvmfoundation.base.AppManager
import com.luck.picture.lib.tools.SPUtils
import timber.log.Timber
import java.io.File


/**
 * 广播实现自动安装的更新的APK
 * 应用版本更新服务
 *
 * @author 黄熙
 * @version 1.0.0
 * @date 2018/9/4 0004
 * @since JDK 1.8
 */

class UpdateBroadcastReceiver : BroadcastReceiver() {

    @SuppressLint("NewApi")
    override fun onReceive(context: Context, intent: Intent) {
        val myDwonloadID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        val refernece = SPUtils.getInstance().getLong("refernece", 0)
        if (refernece != myDwonloadID) {
            return
        }
        val dManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadFileUri = dManager.getUriForDownloadedFile(myDwonloadID)
        if (downloadFileUri != null) {
            installAPK(context, downloadFileUri)
        }
    }


    fun installAPK(context: Context, apk: Uri) {
        if (Build.VERSION.SDK_INT < 23) {
            val intents = Intent()
            intents.action = "android.intent.action.VIEW"
            intents.addCategory("android.intent.category.DEFAULT")
            intents.type = "application/vnd.android.package-archive"
            intents.data = apk
            intents.setDataAndType(apk, "application/vnd.android.package-archive")
            intents.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intents)
        } else {
            val file = queryDownloadedApk(context)
            if (file?.exists()!!) {
                openFile(file, context)
            }

        }
    }

    @SuppressLint("WrongConstant")
    fun openFile(file: File, context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startInstallO(context, file)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                startInstallN(context, file)
            } else {
                startInstall(context, file)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * android1.x-6.x
     */
    @Throws(Exception::class)
    private fun startInstall(context: Context, file: File) {
        val install = Intent(Intent.ACTION_VIEW)
        install.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(install)
    }

    /**
     * android7.x
     */
    @Throws(Exception::class)
    fun startInstallN(context: Context, file: File) {
        // 参数1 上下文, 参数2 在AndroidManifest中的android:authorities值, 参数3  共享的文件
        val uri =
            FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
        val install = Intent(Intent.ACTION_VIEW)
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        install.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        // 添加这一句表示对目标应用临时授权该Uri所代表的文件
        install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        install.setDataAndType(uri, "application/vnd.android.package-archive")
        context.startActivity(install)
    }

    /**
     * android8.x
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    fun startInstallO(context: Context, file: File) {
        try {
            val isGranted = context.packageManager.canRequestPackageInstalls()
            if (isGranted) {
                // 安装应用的逻辑(写自己的就可以)
                startInstallN(context, file)
            } else {
                AlertDialog.Builder(AppManager.currentActivity()).setCancelable(false)
                    .setTitle("安装应用需要打开未知来源权限，请去设置中开启权限").setPositiveButton("确定") { d, w ->
                        val packageURI = Uri.parse("package:" + context.packageName)
                        // 注意这个是8.0新API
                        val intent = Intent(
                            Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                            packageURI
                        )
                        AppManager.currentActivity()?.startActivityForResult(intent, 0x0008)
                    }.show()
            }
        } catch (e: Exception) {
            Timber.e(e.toString())
        }

    }


    private fun getMIMEType(var0: File): String? {
        var var1: String? = ""
        val var2 = var0.name
        val var3 = var2.substring(var2.lastIndexOf(".") + 1, var2.length).toLowerCase()
        var1 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(var3)
        return var1
    }


    /**
     * 通过downLoadId查询下载的apk，解决6.0以后安装的问题
     *
     * @param context
     * @return
     */
    fun queryDownloadedApk(context: Context): File? {
        var targetApkFile: File? = null
        val downloader = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = SPUtils.getInstance().getLong("refernece", -1)
        if (!downloadId.equals(-1)) {
            val query = DownloadManager.Query()
            query.setFilterById(downloadId)
            query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL)
            val cur = downloader.query(query)
            if (cur != null) {
                if (cur.moveToFirst()) {
                    val uriString =
                        cur.getString(cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                    if (!TextUtils.isEmpty(uriString)) {
                        targetApkFile = File(Uri.parse(uriString).path!!)
                    }
                }
                cur.close()
            }
        }
        return targetApkFile

    }
}

