package com.daqsoft.module_main.uitls

import android.media.MediaPlayer
import android.text.TextUtils
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import timber.log.Timber
import java.io.IOException

/**
 * @package name：com.daqsoft.module_main.uitls
 * @date 1/7/2021 下午 1:56
 * @author zp
 * @describe
 */
object MediaPlayerUtils{

    private var mPlayer: MediaPlayer? = null
    // 当前播放的音频地址
    private var nowPlaySongUrl: String? = null

    /**
     * 播放音频
     *
     * @param songUrl 网络音频Url
     */
    fun player(songUrl: String) {
        if (TextUtils.equals(songUrl, nowPlaySongUrl)) {
            Timber.e("player: 重复的url")
            return
        }
        stopPlay()
        nowPlaySongUrl = songUrl
        if (mPlayer == null) {
            mPlayer = MediaPlayer()
        }
        try {
            Timber.e( "player: 当前要播放的歌曲Url === $songUrl")
            mPlayer!!.reset()
            //====这种方式只能http，https会抛IO异常
            mPlayer!!.setDataSource(songUrl)
            mPlayer!!.prepareAsync()
            mPlayer!!.setOnPreparedListener(object : MediaPlayer.OnPreparedListener {
                override fun onPrepared(mp: MediaPlayer) {
                    Timber.e( "onPrepared: 播放 " + mp.getDuration())
                    mp.start()
                }
            })
            mPlayer!!.setOnCompletionListener(object : MediaPlayer.OnCompletionListener {
                override fun onCompletion(mp: MediaPlayer?) {
                    nowPlaySongUrl = ""
                    mPlayer?.release()
                    if (mPlayer != null) mPlayer = null
                }
            })
        } catch (e: IOException) {
            e.printStackTrace()
            Timber.e(" 播放音乐异常")
            ToastUtils.showShortSafe(" 播放音乐异常")
        }
    }

    /**
     * 列表中的音频播放
     *
     *
     * 点击播放，再点击停止
     *
     * @param songUrl
     */
    fun playOrStop(songUrl: String) {
        if (mPlayer != null) {
            stopPlay()
        } else {
            player(songUrl)
        }
    }

    /**
     * 停止播放音频，lastSongUrl置空
     */
    fun stopPlay() {
        nowPlaySongUrl = ""
        try {
            mPlayer?.stop()
            mPlayer?.release()
            mPlayer = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}