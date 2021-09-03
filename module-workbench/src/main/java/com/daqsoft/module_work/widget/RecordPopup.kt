package com.daqsoft.module_work.widget

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.daqsoft.module_work.R
import com.daqsoft.module_work.utils.RecordUtils
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.tools.MediaUtils
import com.lxj.xpopup.core.BottomPopupView
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * @package name：com.daqsoft.module_task.widget
 * @date 21/5/2021 上午 11:05
 * @author zp
 * @describe
 */
class RecordPopup(context: Context)  : BottomPopupView(context), RecordUtils.RecordListener,RecordUtils.PlayListener {

    lateinit var status : ImageView

    lateinit var tips : TextView

    lateinit var cancel : TextView

    lateinit var determine : TextView

    lateinit var waveVoiceView: LineWaveVoiceView

    var mode : RecordUtils.Mode = RecordUtils.Mode.RECORD

    var recordStatus :  RecordUtils.RecordStatus = RecordUtils.RecordStatus.STOP

    var playStatus :  RecordUtils.PlayStatus = RecordUtils.PlayStatus.STOP

    var recordFilePath : String ? = null

    var recordDuration : Long = 0L

    val recordUtils : RecordUtils by lazy(LazyThreadSafetyMode.SYNCHRONIZED ) { RecordUtils() }


    override fun getImplLayoutId(): Int {
        return R.layout.layout_popup_record
    }
    override fun onCreate() {
        super.onCreate()

        recordUtils.setRecordListener(this)
        recordUtils.setPlayListener(this)

        tips = findViewById<TextView>(R.id.tips)

        status = findViewById<ImageView>(R.id.status)
        status.setOnClickListener {
            recordOrPlay()
        }

        cancel = findViewById<TextView>(R.id.cancel)
        cancel.setOnClickListener {

            recordUtils.stopRecord()
            recordUtils.stopPlay()

            recordFilePath?.let {
                val file = File(it)
                if (file.exists() && file.isFile){
                    file.delete()
                }
            }
            reset()
            dismiss()
        }

        determine = findViewById<TextView>(R.id.determine)
        determine.setOnClickListener {

            recordUtils.stopRecord()
            recordUtils.stopPlay()


            recordFilePath?.let {
                val localMedia = LocalMedia()
                localMedia.path = it
                localMedia.androidQToPath = it
                localMedia.realPath = it
                localMedia.mimeType = PictureMimeType.MIME_TYPE_AUDIO
                val mediaExtraInfo = MediaUtils.getAudioSize(it)
                localMedia.duration = mediaExtraInfo.duration
                onClickListener?.determine(localMedia)
            }
            dismiss()
        }

        waveVoiceView =  findViewById<LineWaveVoiceView>(R.id.wave_voice)
    }


    private fun recordOrPlay(){
        when(mode){
            RecordUtils.Mode.RECORD->{
                when(recordStatus){
                    RecordUtils.RecordStatus.STOP ->{
                        recordUtils.startRecord()
                    }
                    RecordUtils.RecordStatus.RECORDING ->{
                        recordUtils.stopRecord()
                    }
                    RecordUtils.RecordStatus.START ->{

                    }
                }
            }
            RecordUtils.Mode.PLAY->{
                when(playStatus){
                    RecordUtils.PlayStatus.STOP ->{
                        if (!recordFilePath.isNullOrBlank()){
                            recordUtils.initPlayer(recordFilePath!!)
                        }
                    }
                    RecordUtils.PlayStatus.PLAYING ->{
                        recordUtils.playOrPause()
                    }
                    RecordUtils.PlayStatus.PAUSE ->{
                        recordUtils.playOrPause()
                    }
                    RecordUtils.PlayStatus.START ->{

                    }
                }
            }
        }
    }

    private var onClickListener : OnClickListener? = null

    fun setOnClickListener(listener: OnClickListener){
        this.onClickListener = listener
    }

    interface OnClickListener{

        fun determine(localMedia: LocalMedia)

    }

    override fun recordStart(path: String) {
        status.setImageResource(R.mipmap.sjcl_icon_ks)
        recordStatus = RecordUtils.RecordStatus.START
        recordFilePath = path
        waveVoiceView.visibility = View.VISIBLE
        tips.visibility = View.INVISIBLE
        interval()
    }

    override fun recording() {
        status.setImageResource(R.mipmap.sjcl_tz)
        recordStatus = RecordUtils.RecordStatus.RECORDING
    }

    override fun recordStop() {
        status.setImageResource(R.mipmap.sjcl_bf)
        recordStatus = RecordUtils.RecordStatus.STOP
        mode = RecordUtils.Mode.PLAY
        waveVoiceView.stopRecord()
        waveVoiceView.visibility = View.INVISIBLE
        tips.visibility = View.VISIBLE
        tips.text = "点击开始播放"
        stopInterval()
    }

    override fun updateMic(db: Double) {
        waveVoiceView.refreshElement(db.toFloat())
    }


    override fun playStart() {
        status.setImageResource(R.mipmap.sjcl_bf)
        playStatus = RecordUtils.PlayStatus.START
    }

    override fun playing() {
        status.setImageResource(R.mipmap.sjcl_tz)
        playStatus = RecordUtils.PlayStatus.PLAYING
        countdown()
    }

    override fun playPause() {
        status.setImageResource(R.mipmap.sjcl_bf)
        playStatus = RecordUtils.PlayStatus.PAUSE
        tips.text = "点击继续播放"
        stopInterval()
    }

    override fun playStop() {
        status.setImageResource(R.mipmap.sjcl_bf)
        playStatus = RecordUtils.PlayStatus.STOP
        tips.text = "点击开始播放"
        stopInterval()
        duration = recordDuration
    }



    fun reset(){
        status.setImageResource(R.mipmap.sjcl_icon_ks)
        mode = RecordUtils.Mode.RECORD
        recordStatus = RecordUtils.RecordStatus.STOP
        playStatus = RecordUtils.PlayStatus.STOP
        recordFilePath = null
        stopInterval()
        duration = 0L
        waveVoiceView.visibility = View.INVISIBLE
        tips.visibility = View.VISIBLE
        tips.text = "点击开始录音"
    }




    var disposable : Disposable? = null
    var duration = 0L
    fun interval(){
        disposable = Observable
            .interval(0,1, TimeUnit.SECONDS)
            .take(RecordUtils.MAX_LENGTH.toLong())
            .compose(RxUtils.schedulersTransformer())
            .subscribe {
                if (duration >= 60){
                    recordUtils.stopRecord()
                }
                if (duration >= 50){
                    waveVoiceView.setText("${60-duration}s")
                }else{
                    waveVoiceView.setText("")
                }
                duration++
                recordDuration = duration
            }
    }

    fun countdown(){
        disposable = Observable
            .interval(0,1, TimeUnit.SECONDS)
            .take(duration)
            .compose(RxUtils.schedulersTransformer())
            .subscribe {
                tips.text = "点击暂停播放 ${duration}s"
                duration--
            }
    }

    fun stopInterval(){
        disposable?.let {
            it.dispose()
            disposable = null
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        recordUtils.stopRecord()
        recordUtils.stopPlay()
    }
}