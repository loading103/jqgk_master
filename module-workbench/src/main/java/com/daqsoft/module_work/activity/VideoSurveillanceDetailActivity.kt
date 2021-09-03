package com.daqsoft.module_work.activity

import android.Manifest
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.GsonUtils
import com.bumptech.glide.Glide
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.FileUtils
import com.daqsoft.library_base.utils.ImageLoader
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.module_work.BR
import com.daqsoft.module_work.R
import com.daqsoft.module_work.databinding.ActivityVideoSurveillanceDetailBinding
import com.daqsoft.module_work.repository.pojo.vo.VideoSurveillanceList
import com.daqsoft.module_work.viewmodel.VideoSurveillanceDetailViewModel
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import com.google.gson.reflect.TypeToken
import com.lxj.xpopup.XPopup
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.listener.GSYVideoShotListener
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import dagger.hilt.android.AndroidEntryPoint


/**
 * @package name：com.daqsoft.module_work.activity
 * @date 14/7/2021 下午 3:30
 * @author zp
 * @describe
 */

@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_VIDEO_SURVEILLANCE_DETAIL)
class VideoSurveillanceDetailActivity : AppBaseActivity<ActivityVideoSurveillanceDetailBinding, VideoSurveillanceDetailViewModel>(){


    @JvmField
    @Autowired
    var itemJson : String ? = null

    var item : VideoSurveillanceList? = null

    var orientationUtils: OrientationUtils? = null

    override fun initParam() {
        super.initParam()

        if (!itemJson.isNullOrEmpty()){
            val type = object : TypeToken<VideoSurveillanceList>() {}.type
            item =  GsonUtils.fromJson<VideoSurveillanceList>(itemJson, type)
        }

    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_video_surveillance_detail
    }

    override fun initViewModel(): VideoSurveillanceDetailViewModel? {
        return viewModels<VideoSurveillanceDetailViewModel>().value
    }

    override fun initView() {
        super.initView()
        initVideoPlayer()

        initOnClick()
    }

    private fun initOnClick() {
        binding.screenshots.setOnClickListenerThrottleFirst{
            screenshots()
        }
    }

    private fun initVideoPlayer() {
        val source = item?.mediaUrl

        binding.videoPlayer.apply {
            titleTextView.text = item?.name
            backButton.setOnClickListenerThrottleFirst{
                this@VideoSurveillanceDetailActivity.finish()
            }
            setUp(source, true, null)
            //设置旋转
            orientationUtils = OrientationUtils(this@VideoSurveillanceDetailActivity, this)
            // 播放
            startPlayLogic()
        }
    }

    private fun screenshots(){
        requestPermission(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            callback = {
                binding.videoPlayer.taskShotPic(GSYVideoShotListener { bitmap ->
                    if (bitmap != null) {
                        FileUtils.saveBitmap("截图",bitmap,callback = { b, s ->
                            if (b){
                                Glide.with(this).load(s).into(binding.saveImageMatrix)
                                binding.viewScreenshot.setOnClickListenerThrottleFirst{
                                    previewPicture(s!!)
                                }
                                binding.saveImageCl.isVisible = true
                                binding.saveImageCl.animation = moveToViewLocation()
                                binding.saveImageCl.postDelayed({
                                    binding.saveImageCl.isVisible = false
                                    binding.saveImageCl.animation = moveToViewBottom()
                                },3000)
                            }else{
                                ToastUtils.showShort("图片保存失败")
                            }
                        })
                    }
                },true)
            } )

    }

    private fun previewPicture(path:String) {
        XPopup.Builder(this)
            .asImageViewer(binding.saveImageMatrix, path, ImageLoader())
            .show()
    }


    override fun onPause() {
        super.onPause()
        binding.videoPlayer.onVideoPause()
    }

    override fun onResume() {
        super.onResume()
        binding.videoPlayer.onVideoResume(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        //释放所有
        binding.videoPlayer.release()
        orientationUtils?.releaseListener()
    }



    fun moveToViewBottom(): TranslateAnimation {
        val mHiddenAction = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
            0.0f, Animation.RELATIVE_TO_SELF, 1.0f
        )
        mHiddenAction.duration = 500
        return mHiddenAction
    }


    fun moveToViewLocation(): TranslateAnimation? {
        val mHiddenAction = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
            1.0f, Animation.RELATIVE_TO_SELF, 0.0f
        )
        mHiddenAction.duration = 500
        return mHiddenAction
    }


}