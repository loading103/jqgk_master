package com.daqsoft.library_base.utils

import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.TextView
import androidx.paging.PagedList
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.library_base.R
import com.jakewharton.rxbinding4.view.clicks
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.collections.AsyncDiffPagedObservableList
import java.text.NumberFormat
import java.util.concurrent.TimeUnit


/**
 *  处理 {@link me.tatarka.bindingcollectionadapter2.PagedBindingRecyclerViewAdapters} 分页工具中 liveData 未工作问题
 *  ps ：这个地方非常非常非常神奇 按理说不需要单独处理 但是不知道为什么 liveData 就是不工作
 *  通过 BindingRecyclerViewAdapter.setLifecycleOwner() 设置了 lifecycleOwner 生命周期也无济于事
 *  暂时只能是在 activity/fragment 中监听数据后重新设置
 *  其他使用方式不变
 *
 * @receiver RecyclerView
 * @param items PagedList<T>
 * @param callback ItemCallback<T>
 */

fun <T : Any> RecyclerView.executePaging(items:PagedList<T>, callback : DiffUtil.ItemCallback<T>){
    var adapter: BindingRecyclerViewAdapter<T>? = null
    val oldAdapter = this.adapter
    if (oldAdapter == null) {
        adapter = BindingRecyclerViewAdapter<T>()
    } else {
        adapter = oldAdapter as BindingRecyclerViewAdapter<T>
    }
    var list: Any? = this.getTag(R.id.bindingcollectiondapter_list_id)
    var asyncDiff: AsyncDiffPagedObservableList<T>? = null
    if (list == null) {
        asyncDiff = AsyncDiffPagedObservableList<T>(callback)
        this.setTag(R.id.bindingcollectiondapter_list_id, list)
        adapter.setItems(list)
    } else {
        asyncDiff = list as AsyncDiffPagedObservableList<T>
    }
    asyncDiff.update(items)
}


fun View.setOnClickListenerThrottleFirst(callback : View.OnClickListener){
    this
        .clicks()
        .throttleFirst(1L, TimeUnit.SECONDS)
        .subscribe {
            callback.onClick(this)
        }
}


fun TextView.scrollingNumbers(num:Float){
    val animator = ValueAnimator.ofFloat(0f, num)
    animator.duration = 1000
    animator.interpolator = AccelerateInterpolator()
    animator.addUpdateListener { valueAnimator ->
        val  currentNum = valueAnimator.animatedValue as Float
        this.text = currentNum.formatGroupingUsed()
    }
    animator.start()
}

fun TextView.scrollingNumbers(num:Int){
    val animator = ValueAnimator.ofInt(0, num)
    animator.duration = 1000
    animator.interpolator = AccelerateInterpolator()
    animator.addUpdateListener { valueAnimator ->
        val  currentNum = valueAnimator.animatedValue as Int
        this.text = currentNum.formatGroupingUsed()
    }
    animator.start()
}


fun Float.formatGroupingUsed():String{
    val numberFormat = NumberFormat.getNumberInstance()
    numberFormat.isGroupingUsed = true
    numberFormat.minimumFractionDigits = 2
    numberFormat.maximumFractionDigits = 2
    return numberFormat.format(this)
}


fun Int.formatGroupingUsed():String{
    val numberFormat = NumberFormat.getNumberInstance()
    numberFormat.isGroupingUsed = true
    return numberFormat.format(this)
}

fun Long.formatGroupingUsed():String{
    val numberFormat = NumberFormat.getNumberInstance()
    numberFormat.isGroupingUsed = true
    return numberFormat.format(this)
}

fun String.percentCompareToZero(): Int {
    if(this.isNullOrBlank()){
        return 0
    }
    val lastChar = this.last()
    if (lastChar != '%' ){
        throw  Throwable("${this} 不是百分比数字")
    }
    val sub = this.replace("%","").toFloatOrNull() ?: throw Throwable("${this} 不是百分比数字")
    return sub.compareTo(0)
}