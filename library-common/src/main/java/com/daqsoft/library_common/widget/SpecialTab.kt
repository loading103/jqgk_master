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
import com.bumptech.glide.request.transition.Transition
import com.daqsoft.library_common.R
import com.ruffian.library.widget.RTextView
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem
import retrofit2.http.Url
import java.util.*

/**
 * @package name：com.daqsoft.library_common.widget
 * @date 18/5/2021 下午 1:46
 * @author zp
 * @describe
 */
class SpecialTab: BaseTabItem {

    companion object{
        const val DRAWABLE = "drawable"
        const val URL = "url"
    }
    private var mode : String = DRAWABLE


    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, style: Int) : super(context, attrs, style)

    private val mIcon: ImageView
    private val mTitle: TextView
    private val mMessages: RTextView

    private var mDefaultDrawable: Drawable = ContextCompat.getDrawable(context, R.mipmap.nav_default_icon)!!
    private var mCheckedDrawable: Drawable = ContextCompat.getDrawable(context, R.mipmap.nav_default_icon)!!

    private var mDefaultTextColor = resources.getColor(R.color.color_282d42)
    private var mCheckedTextColor = resources.getColor(R.color.color_59abff)


    private var mDefaultUrl: String = ""
    private var mCheckedUrl : String = ""


    private var mChecked: Boolean = false

    init {
        inflate(context, R.layout.layout_special_tab, this)
        mIcon = findViewById(R.id.icon)
        mTitle = findViewById(R.id.title)
        mMessages = findViewById(R.id.messages)
        setRoundMessageViewNumber(0)
    }


    fun initDrawable(@DrawableRes drawableRes: Int, @DrawableRes checkedDrawableRes: Int) {
        mode = DRAWABLE
        mDefaultDrawable = ContextCompat.getDrawable(context, drawableRes)!!
        mCheckedDrawable = ContextCompat.getDrawable(context, checkedDrawableRes)!!
    }

    fun initUrl(url:String,checkedUrl:String){
        mode = URL
        mDefaultUrl = url
        mCheckedUrl = checkedUrl
    }

    override fun setChecked(checked: Boolean) {
        if (checked) {
            when(mode){
                DRAWABLE ->  mIcon.setImageDrawable(mCheckedDrawable)
                URL -> Glide.with(context).load(mCheckedUrl).placeholder(R.mipmap.nav_default_icon).into(mIcon)
            }
            mTitle.setTextColor(mCheckedTextColor)
        } else {
            when(mode){
                DRAWABLE ->  mIcon.setImageDrawable(mDefaultDrawable)
                URL -> Glide.with(context).load(mDefaultUrl).placeholder(R.mipmap.nav_default_icon).into(mIcon)
            }
            mTitle.setTextColor(mDefaultTextColor)
        }
        mChecked = checked
    }

    override fun getTitle(): String {
        return mTitle.text.toString()
    }

    override fun setMessageNumber(number: Int) {
        setRoundMessageViewNumber(number)

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
        mTitle.text = title
    }

    private fun setRoundMessageViewNumber(number: Int) {

        if (number == 0){
            mMessages.visibility = View.INVISIBLE
            return
        }

        mMessages.visibility = View.VISIBLE
        if (number > 0) {
            if (number < 10) {
                mMessages.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9f)
            } else {
                mMessages.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 7f)
            }
            if (number <= 99) {
                mMessages.text = String.format(Locale.ENGLISH, "%d", number)
            } else {
                mMessages.text = String.format(Locale.ENGLISH, "%d+", 99)
            }
            return
        }

        if (number < 0){
            mMessages.text = ""
        }
    }
}