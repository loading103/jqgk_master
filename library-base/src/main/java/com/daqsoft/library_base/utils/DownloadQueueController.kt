package com.daqsoft.library_base.utils

import com.liulishuo.okdownload.DownloadContext
import com.liulishuo.okdownload.DownloadContextListener
import com.liulishuo.okdownload.DownloadListener
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.kotlin.listener.createListener1
import timber.log.Timber
import java.io.File

/**
 * @package name：com.daqsoft.library_base.utils
 * @date 4/6/2021 上午 11:39
 * @author zp
 * @describe
 */
class DownloadQueueController {

    private val taskList = arrayListOf<DownloadTask>()
    private var downloadContext: DownloadContext? = null

    private var queueDir: File? = null

    /**
     *
     * @param list List<Pair<String, String>> pair.first：fileName  pair.second： url
     * @param listener DownloadContextListener
     */
    fun initTasks(list: List<Pair<String,String>>, listener: DownloadContextListener) {
        val parentFile = File(FileUtils.getAppDirPath() + "/download")
        if (!parentFile.exists()) {
            parentFile.mkdirs()
        }
        this.queueDir = parentFile
        val builder = DownloadContext.QueueSet()
            .setParentPathFile(parentFile)
            .setMinIntervalMillisCallbackProcess(100)
            .commit()
        list.forEachIndexed { index, pair ->
            val taskBuilder = DownloadTask.Builder(pair.second, parentFile).setFilename(pair.first)
            builder.bind(taskBuilder).addTag(index, pair.first)
        }
        builder.setListener(listener)
        this.downloadContext = builder.build().also { this.taskList.addAll(it.tasks) }
    }

    /**
     *
     * @param listener DownloadListener

    createListener1(
    taskStart = { task, model ->
    Timber.e("start")
    },
    retry = { task, cause ->
    Timber.e("retry")
    },
    connected = { task, blockCount, currentOffset, totalLength ->
    Timber.e("task")
    },
    progress = { task, currentOffset, totalLength ->
    Timber.e("progress :  ${currentOffset}/${totalLength}  ")
    },
    taskEnd = { task, cause, realCause, model ->
    Timber.e("task")
    })
     *
     * @param isSerial Boolean
     */
    fun start(listener : DownloadListener,isSerial: Boolean = false) {
        this.downloadContext?.start(listener,isSerial)
    }

    fun stop() {
        if (this.downloadContext?.isStarted == true) {
            this.downloadContext?.stop()
        }
    }

}