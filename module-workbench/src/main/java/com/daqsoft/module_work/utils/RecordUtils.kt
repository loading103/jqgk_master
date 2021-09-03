package com.daqsoft.module_work.utils

import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Handler
import android.os.Looper
import android.text.format.DateFormat
import com.daqsoft.library_base.utils.FileUtils
import java.io.File
import java.util.*


/**
 * @package name：com.daqsoft.module_task.util
 * @date 21/5/2021 下午 2:51
 * @author zp
 * @describe
 */
class RecordUtils {

    enum class Mode{
        RECORD,
        PLAY
    }

    enum class RecordStatus{
        START,
        RECORDING,
        STOP,
    }

    enum class PlayStatus{
        START,
        PLAYING,
        PAUSE,
        STOP,
    }

    companion object{
        const val MAX_LENGTH = 60
    }


    private var mMediaRecorder: MediaRecorder? = null
    private var mFileName: String? = null
    private var mFilePath: String? = null

    fun startRecord(){
        try {
            mMediaRecorder = MediaRecorder()
            mMediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
            mMediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
            mMediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            mMediaRecorder?.setOnInfoListener { mediaRecorder, what, extra ->
                if(what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED){
                    stopRecord()
                }
            }
            mFileName = DateFormat.format("yyyyMMdd_HHmmss", Calendar.getInstance(Locale.CHINA)).toString() + ".mp3"
            val recordDir  = File(FileUtils.getAppDirPath()+"/record")
            if (!recordDir.exists()) {
                recordDir.mkdirs()
            }
            mFilePath = recordDir.path + File.separator + mFileName
            mMediaRecorder?.setOutputFile(mFilePath)
            mMediaRecorder?.setMaxDuration(MAX_LENGTH * 1000)
            mMediaRecorder?.prepare()
            mMediaRecorder?.start()
            updateMicStatus()
            recordListener?.recordStart(mFilePath!!)
            recordListener?.recording()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopRecord() {
        try {
            mMediaRecorder?.stop()
            recordListener?.recordStop()
            mMediaRecorder?.release()
            mMediaRecorder = null
            mHandler?.removeCallbacks(mUpdateMicStatusTimer)
            mHandler = null
        }catch (e:Exception){
            e.printStackTrace()
        }finally {
            mMediaRecorder?.stop()
            mMediaRecorder?.release()
            mMediaRecorder = null
            mHandler?.removeCallbacks(mUpdateMicStatusTimer)
            mHandler = null
        }

    }


    private var mHandler : Handler? = null
    private var mUpdateMicStatusTimer = Runnable {
        updateMicStatus()
    }
    private fun updateMicStatus() {
        if (mHandler == null){
            mHandler = Handler(Looper.myLooper()!!)
        }
        if (mMediaRecorder != null) {
//            // 参考振幅为 1
//            val ratio = mMediaRecorder!!.maxAmplitude.toDouble() / 1
//            // 分贝
//            var db = 0.0
//            if (ratio > 1){
//                db = 20 * Math.log10(ratio)
//            }
                val db = mMediaRecorder!!.maxAmplitude * 1.0f / 32768
            recordListener?.updateMic(db.toDouble())
            mHandler?.postDelayed(mUpdateMicStatusTimer, 100)
        }
    }


    private var recordListener : RecordListener? = null

    fun setRecordListener(recordListener: RecordListener){
        this.recordListener = recordListener
    }

    interface RecordListener{
        fun recordStart(path:String)
        fun recording()
        fun recordStop()

        fun updateMic(db:Double)
    }




    private var mediaPlayer: MediaPlayer? = null


    /**
     * 初始化音频播放组件
     *
     * @param path
     */
    fun initPlayer(path: String) {
        try {
            stopPlay()
            mediaPlayer = MediaPlayer()
            mediaPlayer?.reset()
            mediaPlayer?.setDataSource(path)
            mediaPlayer?.setLooping(false)
            mediaPlayer?.setOnCompletionListener {
                stopPlay()
            }
            mediaPlayer?.prepareAsync()
            playListener?.playStart()
            mediaPlayer?.setOnPreparedListener {
                playOrPause()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 停止播放
     *
     * @param path
     */
    fun stopPlay() {
        playListener?.playStop()
        try {
            mediaPlayer?.stop()
            mediaPlayer?.reset()
            mediaPlayer = null
        } catch (e: Exception) {
            e.printStackTrace()
        }finally {
            mediaPlayer?.stop()
            mediaPlayer?.reset()
            mediaPlayer = null
        }
    }

    /**
     * 暂停/播放
     */
    fun playOrPause() {
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer!!.isPlaying) {
                    mediaPlayer!!.pause()
                    playListener?.playPause()
                } else {
                    mediaPlayer!!.start()
                    playListener?.playing()
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    private var playListener : PlayListener? = null

    fun setPlayListener(playListener: PlayListener){
        this.playListener = playListener
    }

    interface PlayListener{
        fun playStart()
        fun playing()
        fun playPause()
        fun playStop()
    }
}