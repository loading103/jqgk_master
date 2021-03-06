package com.daqsoft.module_work.activity

import android.os.Bundle
import android.text.TextUtils
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.GlideEngine
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.library_common.adapter.GridImageAdapter
import com.daqsoft.library_common.warrper.FullyGridLayoutManager
import com.daqsoft.library_common.widget.RemarkPopup
import com.daqsoft.module_work.BR
import com.daqsoft.module_work.R
import com.daqsoft.module_work.adapter.SupplementCardApproveDetailAdapter
import com.daqsoft.module_work.databinding.ActivitySupplementCardApplyInfoBinding
import com.daqsoft.module_work.repository.pojo.vo.ApproveDetail
import com.daqsoft.module_work.viewmodel.SupplementCardApplyInfoViewModel
import com.daqsoft.mvvmfoundation.utils.dp
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.decoration.GridSpacingItemDecoration
import com.luck.picture.lib.entity.LocalMedia
import com.lxj.xpopup.XPopup
import dagger.hilt.android.AndroidEntryPoint
import me.tatarka.bindingcollectionadapter2.ItemBinding
import timber.log.Timber

/**
 * @ClassName    SupplementCardApplyInfoActivity
 * @Description
 * @Author       yuxc
 * @CreateDate   2021/7/8
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.SUPPLEMENT_CARD_APPLY_INFO)
class SupplementCardApplyInfoActivity : AppBaseActivity<ActivitySupplementCardApplyInfoBinding, SupplementCardApplyInfoViewModel>() {

    @JvmField
    @Autowired
    var approve : Boolean = false


    val imageAdapter : GridImageAdapter by lazy {
        GridImageAdapter(this, null).apply {
            setPureDisplay(true)
            setOnItemClickListener { v, position ->
                val selectList: List<LocalMedia> = imageAdapter.data
                if (selectList.isNotEmpty()) {
                    val media = selectList[position]
                    val mimeType = media.mimeType
                    val mediaType = PictureMimeType.getMimeType(mimeType)
                    when (mediaType) {
                        PictureConfig.TYPE_VIDEO -> {
                            // ????????????
                            PictureSelector
                                .create(this@SupplementCardApplyInfoActivity)
                                .themeStyle(R.style.picture_default_style)
                                .externalPictureVideo(if (TextUtils.isEmpty(media.androidQToPath)) media.path else media.androidQToPath)
                        }
                        PictureConfig.TYPE_AUDIO -> {
                            // ????????????
                            PictureSelector
                                .create(this@SupplementCardApplyInfoActivity)
                                .externalPictureAudio(if (PictureMimeType.isContent(media.path)) media.androidQToPath else media.path)
                        }
                        PictureConfig.TYPE_IMAGE -> {
                            // ???????????? ???????????????????????????
                            PictureSelector
                                .create(this@SupplementCardApplyInfoActivity)
                                .themeStyle(R.style.picture_default_style)
                                .isNotPreviewDownload(true)
                                .imageEngine(GlideEngine.createGlideEngine())
                                .openExternalPreview(position, selectList)
                        }
                    }
                }
            }
            setSelectMax(0)
        }
    }


    val approveAdapter: SupplementCardApproveDetailAdapter by lazy {
        SupplementCardApproveDetailAdapter().apply {
            itemBinding = ItemBinding.of(ItemBinding.VAR_NONE, R.layout.recycleview_supplement_card_approve_detail_item)

            val data = mutableListOf<ApproveDetail>()

            for (i in 0.. 4 ){

                val detail = ApproveDetail(
                    "??????${i}",
                    arrayListOf(Pair("??????${i}","")),
                    "????????????${i}"
                )

                data.add(detail)
            }


            setItems(data)
        }
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_supplement_card_apply_info
    }

    override fun initViewModel(): SupplementCardApplyInfoViewModel? {
        return viewModels<SupplementCardApplyInfoViewModel>().value
    }


    override fun initView() {
        super.initView()
        initImageRecyclerView()
        initApproveRecyclerView()
        initApprove()


    }

    private fun initApprove() {
        binding.approveLl.isVisible = approve

        binding.refuse.setOnClickListenerThrottleFirst{
            approvePopup(false)
        }

        binding.agree.setOnClickListenerThrottleFirst{
            approvePopup(true)
        }
    }

    private fun approvePopup(boolean: Boolean){
        XPopup
            .Builder(this)
            .isDestroyOnDismiss(true)
            .asCustom(RemarkPopup(this,"????????????","????????????????????????????????????","??????").apply {
                setOnClickListener(object : RemarkPopup.OnClickListener{
                    override fun onClick(remark: String) {
                        Timber.e("approvePopup ${boolean} ")
                        this@apply.dismiss()
                    }
                })
            })
            .show()
    }


    private fun initImageRecyclerView() {
        binding.image.apply {
            val spanCount = 4
            layoutManager = FullyGridLayoutManager(
                this@SupplementCardApplyInfoActivity,
                spanCount,
                GridLayoutManager.VERTICAL,
                false
            )
            if (itemDecorationCount == 0){
                val spacing = 0.dp
                val itemDecoration =  GridSpacingItemDecoration(spanCount,spacing, true)
                addItemDecoration(itemDecoration)
            }
            adapter = imageAdapter
        }

    }

    private fun initApproveRecyclerView() {
        binding.approve.apply {
            layoutManager = LinearLayoutManager(this@SupplementCardApplyInfoActivity)
            adapter = approveAdapter
        }
    }
}

