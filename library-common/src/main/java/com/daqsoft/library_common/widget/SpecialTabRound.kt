package com.daqsoft.library_common.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.daqsoft.library_common.R
import com.ruffian.library.widget.RTextView
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem
import java.util.*

/**
 * @package name：com.daqsoft.library_common.widget
 * @date 18/5/2021 下午 1:46
 * @author zp
 * @describe
 */
class SpecialTabRound : BaseTabItem {

    companion object{
        const val DRAWABLE = "drawable"
        const val URL = "url"
    }
    private var mode : String = DRAWABLE


    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, style: Int) : super(context, attrs, style)

    private val mIcon: ImageView

    private var mDefaultDrawable: Drawable = ContextCompat.getDrawable(context, R.mipmap.nav_default_icon)!!
    private var mCheckedDrawable: Drawable = ContextCompat.getDrawable(context, R.mipmap.nav_default_icon)!!

    private var mDefaultUrl: String = ""
    private var mCheckedUrl : String = ""



    private var mChecked: Boolean = false

    init {
        inflate(context, R.layout.layout_special_tab_round, this)
        mIcon = findViewById(R.id.icon)
    }


    fun initDrawable(@DrawableRes drawableRes: Int, @DrawableRes checkedDrawableRes: Int) {
        mode = SpecialTab.DRAWABLE
        mDefaultDrawable = ContextCompat.getDrawable(context, drawableRes)!!
        mCheckedDrawable = ContextCompat.getDrawable(context, checkedDrawableRes)!!
    }


    fun initUrl(url:String,checkedUrl:String){
        mode = SpecialTab.URL
        mDefaultUrl = url
        mCheckedUrl = checkedUrl
    }

    override fun setChecked(checked: Boolean) {
        if (checked) {
            when(mode){
                SpecialTab.DRAWABLE ->  mIcon.setImageDrawable(mCheckedDrawable)
                SpecialTab.URL -> Glide.with(context).load(mCheckedUrl).placeholder(R.mipmap.nav_default_icon).into(mIcon)
            }
        } else {
            when(mode){
                SpecialTab.DRAWABLE ->  mIcon.setImageDrawable(mDefaultDrawable)
                SpecialTab.URL -> Glide.with(context).load(mDefaultUrl).placeholder(R.mipmap.nav_default_icon).into(mIcon)
            }
        }
        mChecked = checked
    }

    override fun getTitle(): String {
        return ""
    }

    override fun setMessageNumber(number: Int) {
    }

    override fun setHasMessage(hasMessage: Boolean) {
    }

    override fun setSelectedDrawable(drawable: Drawable) {
//        mCheckedDrawable = drawable
//        if (mChecked) {
//            mIcon.setImageDrawable(drawable)
//        }
    }

    override fun setDefaultDrawable(drawable: Drawable) {
//        mDefaultDrawable = drawable
//        if (!mChecked) {
//            mIcon.setImageDrawable(drawable)
//        }
    }

    override fun setTitle(title: String?) {
    }

}