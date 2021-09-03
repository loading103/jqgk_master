package com.daqsoft.module_work.adapter

import android.graphics.drawable.AnimationDrawable
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import com.daqsoft.library_base.utils.setOnClickListenerThrottleFirst
import com.daqsoft.module_work.databinding.RecycleviewAlarmHandleAudioItemBinding
import com.luck.picture.lib.entity.LocalMedia
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import java.util.concurrent.TimeUnit

/**
 * @package name：com.daqsoft.module_task.adapter
 * @date 20/5/2021 下午 5:37
 * @author zp
 * @describe
 */
class AlarmHandleAudioAdapter : BindingRecyclerViewAdapter<LocalMedia>() {


    var audioPlayAnimation : AnimationDrawable? = null


    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: LocalMedia
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)
        val itemBinding = binding as RecycleviewAlarmHandleAudioItemBinding

        itemBinding.delete.isVisible = !pureDisplay

        itemBinding.delete.setOnClickListenerThrottleFirst {
            itemOnClickListener?.delete(position, item)
        }

        itemBinding.speaker.setOnClickListenerThrottleFirst {
            itemOnClickListener?.play(position, item)
            audioPlayAnimation?.stop()
            audioPlayAnimation = itemBinding.volume.background as AnimationDrawable?
            audioPlayAnimation?.start()
        }

        itemBinding.duration.text = "${TimeUnit.MILLISECONDS.toSeconds(item.duration)}″"

    }

    fun startAnimation() {
        audioPlayAnimation?.start()
    }


    fun stopAnimation(){
        audioPlayAnimation?.selectDrawable(0)
        audioPlayAnimation?.stop()
    }

    fun releaseAnimation(){
        audioPlayAnimation?.selectDrawable(0)
        audioPlayAnimation?.stop()
        audioPlayAnimation = null
    }


    private var pureDisplay = false
    fun setPureDisplay(pureDisplay : Boolean){
        this.pureDisplay = pureDisplay
    }

    private var itemOnClickListener : ItemOnClickListener? = null

    fun setItemOnClickListener(listener: ItemOnClickListener){
        this.itemOnClickListener = listener
    }

    interface ItemOnClickListener{

        fun delete(position: Int,item : LocalMedia)

        fun play(position: Int,item : LocalMedia)
    }

}