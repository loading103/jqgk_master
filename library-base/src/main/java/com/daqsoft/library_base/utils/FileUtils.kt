package com.daqsoft.library_base.utils

import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import com.daqsoft.mvvmfoundation.utils.ContextUtils
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

/**
 * @package name：com.daqsoft.library_base.utils
 * @date 21/5/2021 下午 3:08
 * @author zp
 * @describe
 */
object FileUtils {

    private const val APP_DIR = "com.daqsoft.smartscenicmanager"

    /**
     * 获取 SD 路径
     */
    fun getSDCardPath():String{
        var sdDir : File?  = null
        val sdCardExist = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
        if(sdCardExist){
            if (Build.VERSION.SDK_INT>=29){
                sdDir = ContextUtils.getContext().getExternalFilesDir(null)
            }else {
                sdDir = Environment.getExternalStorageDirectory()
            }
        } else {
            sdDir = Environment.getRootDirectory()
        }
        return sdDir!!.path
    }


    /**
     * 获取 app 文件目录
     */
    fun getAppDirPath():String{
        val appDir = File(getSDCardPath())
        if (!appDir.exists()){
            appDir.mkdirs()
        }
        return appDir.path
    }




    fun saveBitmap(fileName:String,bitmap: Bitmap,callback: (Boolean,String?) -> Unit) {
        try {
            val parentFile = File(getAppDirPath() + "/screenshots")
            if (!parentFile.exists()) {
                parentFile.mkdirs()
            }
            val file = File(parentFile, "$fileName.jpg")
            val outputStream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush();
            outputStream.close();
            bitmap.recycle()
            callback(true,file.path)
        }catch (e:Exception){
            e.printStackTrace()
            callback(false,null)
        }
    }

}

