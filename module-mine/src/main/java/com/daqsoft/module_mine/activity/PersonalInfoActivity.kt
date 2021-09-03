package com.daqsoft.module_mine.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.Observable
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.PhoneUtils
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.GlideEngine
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.library_common.bean.Employee
import com.daqsoft.library_common.widget.CallPopup
import com.daqsoft.module_mine.BR
import com.daqsoft.module_mine.R
import com.daqsoft.module_mine.databinding.ActivityPersonalInfoBinding
import com.daqsoft.module_mine.viewmodel.PersonalInfoViewModel
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.lxj.xpopup.XPopup
import dagger.hilt.android.AndroidEntryPoint

/**
 * @package name：com.daqsoft.module_mine.activity
 * @date 17/11/2020 下午 2:23
 * @author zp
 * @describe 个人信息页面
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Mine.PAGER_PERSONAL_INFO)
class PersonalInfoActivity : AppBaseActivity<ActivityPersonalInfoBinding, PersonalInfoViewModel>() {

    @JvmField
    @Autowired
    var id : String = ""


    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_personal_info
    }

    override fun initViewModel(): PersonalInfoViewModel? {
        return viewModels<PersonalInfoViewModel>().value
    }


    override fun initView() {
        super.initView()

        binding.mobile.setOnClickListenerThrottleFirst{
            showCallPopUp(viewModel.employee.get()!!.mobile)
        }

    }

    override fun initData() {
        super.initData()
        viewModel.getEmployeeDetail(id)
    }

    override fun initViewObservable() {
        super.initViewObservable()

//        // 头像
//        viewModel.avatarLiveData.observe(this, Observer {
//            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
//                callback = { jumpAlbum() } )
//        })
//
//        // 生日
//        LiveEventBus.get(LEBKeyGlobal.MODIFY_BIRTHDAY_KEY,String::class.java).observe( this,Observer {
//            initData()
//        })
//
//        // 地址
//        LiveEventBus.get(LEBKeyGlobal.MODIFY_ADDRESS_KEY,String::class.java).observe( this,Observer {
//            initData()
//        })
//
//        // 兴趣
//        LiveEventBus.get(LEBKeyGlobal.MODIFY_INTEREST_KEY,String::class.java).observe( this,Observer {
//            initData()
//        })


        viewModel.employee.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback(){
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                runOnUiThread {
                    if (viewModel.employee.get()!!.appSwitch == 1) {
                        binding.mobile.isEnabled = false
                        binding.mobile.setTextColor(resources.getColor(R.color.color_666666))
                        binding.mobile.setCompoundDrawables(null, null, null, null)
                    } else {
                        binding.mobile.isEnabled = true
                        binding.mobile.setTextColor(resources.getColor(R.color.color_59abff))
                        val drawable = resources.getDrawable(R.mipmap.gerenzhuye_dadianhua)
                        drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
                        binding.mobile.setCompoundDrawables(drawable, null, null, null)
                    }
                }
            }

        })
    }

    /**
     * 跳转相册
     */
    private fun jumpAlbum() {
//        var intent = Intent()
//        intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
//        startActivityForResult(intent, ALBUM_REQUEST_CODE)
            PictureSelector
                .create(this)
                // 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .openGallery(PictureMimeType.ofImage())
                // 外部传入图片加载引擎，必传项
                .imageEngine(GlideEngine.createGlideEngine())
                // 最大图片选择数量
                .maxSelectNum(1)
                // 最小选择数量
                .minSelectNum(1)
                // 每行显示个数
                .imageSpanCount(4)
                // 设置相册Activity方向，不设置默认使用系统
        //            .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                // 是否显示原图控制按钮，如果设置为true则用户可以自由选择是否使用原图，压缩、裁剪功能将会失效
                .isOriginalImageControl(true)
                // 多选 or 单选
                .selectionMode(PictureConfig.SINGLE)
                // 单选模式下是否直接返回，PictureConfig.SINGLE模式下有效
                .isSingleDirectReturn(false)
                // 是否可预览图片
                .isPreviewImage(true)
                // 是否可预览视频
                .isPreviewVideo(true)
                // 是否可播放音频
                .enablePreviewAudio(true)
                // 是否显示拍照按钮
                .isCamera(true)
                // 图片列表点击 缩放效果 默认true
                .isZoomAnim(true)
                // 图片压缩后输出质量 0~ 100
                .compressQuality(80)
                //同步false或异步true 压缩 默认同步
                .synOrAsy(true)
                // 是否显示gif图片
                .isGif(false)
                // 裁剪输出质量 默认100
                .cutOutQuality(90)
                // 是否裁剪
        //            .isEnableCrop(true)
                // 小于100kb的图片不压缩
                .minimumCompressSize(100)
                // 是否传入已选图片
                .selectionData(null)
                //结果回调onActivityResult code
                .forResult(PictureConfig.CHOOSE_REQUEST)
    }


    fun showCallPopUp(mobile : String){
        XPopup
            .Builder(this)
            .enableDrag(false)
            .dismissOnTouchOutside(false)
            .isDestroyOnDismiss(true)
            .asCustom(CallPopup(this,mobile).apply {
                setOnClickListener(object : CallPopup.OnClickListener{
                    override fun onClick(mobile: String) {
                        PhoneUtils.dial(mobile)
                        this@apply.dismiss()
                    }
                })
            })
            .show()

    }
}