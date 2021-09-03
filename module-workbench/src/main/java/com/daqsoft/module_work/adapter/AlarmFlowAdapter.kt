package com.daqsoft.module_work.adapter

import android.graphics.drawable.AnimationDrawable
import android.os.Build
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import com.daqsoft.library_base.utils.GlideEngine
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.library_common.adapter.GridImageAdapter
import com.daqsoft.library_common.warrper.FullyGridLayoutManager
import com.daqsoft.module_work.R
import com.daqsoft.module_work.databinding.RecycleviewAlarmDetailsFlowItemBinding
import com.daqsoft.module_work.databinding.RecycleviewAlarmHandleAudioItemBinding
import com.daqsoft.module_work.viewmodel.itemviewmodel.AddressBookItemViewModel
import com.daqsoft.module_work.viewmodel.itemviewmodel.AlarmDetailsFlowItemViewModel
import com.daqsoft.module_work.widget.ExpandTextView
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.utils.dp
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.decoration.GridSpacingItemDecoration
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.tools.DateUtils
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.module_work.adapter
 * @date 1/6/2021 上午 11:36
 * @author zp
 * @describe
 */
class AlarmFlowAdapter : BindingRecyclerViewAdapter<ItemViewModel<*>>() {



    var currentPlay : AlarmHandleAudioAdapter ? = null

    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: ItemViewModel<*>
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)
        val itemBinding = binding as RecycleviewAlarmDetailsFlowItemBinding
        val itemViewModel = item as AlarmDetailsFlowItemViewModel


        itemBinding.remark.collapseEnable = true
        itemBinding.remark.collapseText = ""
        itemBinding.remark.expandText = ""
        itemBinding.arrow.isVisible = position != 0
        itemBinding.icon.setImageResource(if (position == 0) R.mipmap.renwu_shijian else R.mipmap.renwu_liucheng)


        itemBinding.remark.isVisible = position != 0

        val annexData = itemViewModel.handleFlow.files.filter { it.type != 4 }?.map {
            val localMedia = LocalMedia()
            localMedia.path = it.url
            localMedia.mimeType = PictureMimeType.MIME_TYPE_AUDIO

            localMedia.mimeType = when (it.type) {
                2 -> PictureMimeType.MIME_TYPE_VIDEO
                1 -> PictureMimeType.MIME_TYPE_IMAGE
                else -> PictureMimeType.MIME_TYPE_IMAGE
            }


            localMedia.duration = if (it.time.isNullOrBlank()) 0L else it.time!!.toLong()*1000
            return@map localMedia
        }

        val audioData = itemViewModel.handleFlow.files.filter { it.type == 4 }?.map {
            val localMedia = LocalMedia()
            localMedia.path = it.url
            localMedia.mimeType = PictureMimeType.MIME_TYPE_AUDIO
            localMedia.duration = if (it.time.isNullOrBlank()) 0L else it.time!!.toLong()*1000
            return@map localMedia
        }


        if (!itemViewModel.handleFlow.files.isNullOrEmpty()){
            // 有附件
            itemBinding.remark.setText(itemViewModel.remarkObservable.get(),itemViewModel.handleFlow.txtExpandState,null)

            if(!annexData.isNullOrEmpty()){
                itemBinding.annex.apply {
                    val spanCount = 4
                    layoutManager =
                        FullyGridLayoutManager(
                            itemBinding.annex.context,
                            spanCount,
                            GridLayoutManager.VERTICAL,
                            false
                        )
                    if (itemDecorationCount == 0) {
                        val spacing = 0.dp
                        val itemDecoration = GridSpacingItemDecoration(spanCount, spacing, true)
                        addItemDecoration(itemDecoration)
                    }
                    adapter = GridImageAdapter(itemBinding.annex.context, null).apply {
                        setPureDisplay(true)
                        setList(annexData)
                        setSelectMax(annexData.size)
                        setOnItemClickListener { v, position ->
                            onClickListener?.preview(position,annexData)
                        }
                    }
                }
            }


            if(!audioData.isNullOrEmpty()){
                binding.audio.apply {
                    layoutManager = FullyGridLayoutManager(context,5, GridLayoutManager.VERTICAL,false)
                    adapter = AlarmHandleAudioAdapter().apply {
                        this@apply.itemBinding = ItemBinding.of(ItemBinding.VAR_NONE, R.layout.recycleview_alarm_handle_audio_item)
                        setItems(audioData)
                        setPureDisplay(true)
                        setItemOnClickListener(object : AlarmHandleAudioAdapter.ItemOnClickListener{
                            override fun delete(position: Int, item: LocalMedia) {

                            }

                            override fun play(position: Int, item: LocalMedia) {
                                currentPlay = this@apply
                                onClickListener?.play(position,item)
                            }

                        })
                    }
                }
            }



            binding.arrow.setOnClickListenerThrottleFirst{
                itemViewModel.handleFlow.expandState = !itemViewModel.handleFlow.expandState
                itemViewModel.handleFlow.txtExpandState  = !itemViewModel.handleFlow.txtExpandState

                binding.arrow.rotation = if (itemViewModel.handleFlow.expandState) 180f else 0f
                itemBinding.remark.setChanged(itemViewModel.handleFlow.txtExpandState)

                if(!annexData.isNullOrEmpty()){
                    binding.annexGroup.isVisible = itemViewModel.handleFlow.expandState
                }else{
                    binding.annexGroup.isVisible = false
                }

                if (!audioData.isNullOrEmpty()){
                    binding.audioGroup.isVisible = itemViewModel.handleFlow.expandState
                }else{
                    binding.audioGroup.isVisible = false
                }

            }

        }else{
            // 无附件
            itemBinding.remark.setText(itemViewModel.remarkObservable.get(),itemViewModel.handleFlow.txtExpandState,object : ExpandTextView.Callback{
                override fun onExpand() {
                }

                override fun onCollapse() {
                }

                override fun onLoss() {
                    binding.arrow.post {
                        binding.arrow.isVisible = false
                    }
                }

                override fun onExpandClick() {
                }

                override fun onCollapseClick() {
                }
            })

            binding.arrow.setOnClickListenerThrottleFirst{
                itemViewModel.handleFlow.txtExpandState  = !itemViewModel.handleFlow.txtExpandState
                binding.arrow.rotation = if (itemViewModel.handleFlow.txtExpandState) 180f else 0f
                itemBinding.remark.setChanged(itemViewModel.handleFlow.txtExpandState)
            }

        }

    }

    fun releaseAnimation(){
        currentPlay?.releaseAnimation()
    }


    private var onClickListener : OnClickListener ? = null

    fun setOnClickListener(onClickListener : OnClickListener){
        this.onClickListener = onClickListener
    }

    interface OnClickListener{

        fun preview(position: Int,data:List<LocalMedia>)

        fun play(position: Int,data: LocalMedia)
    }

}