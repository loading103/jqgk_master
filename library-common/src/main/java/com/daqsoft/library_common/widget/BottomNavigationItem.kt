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
import com.daqsoft.library_common.R
import com.ruffian.library.widget.RTextView
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem
import java.util.*

/**
 * @package name：view
 * @date 10/5/2021 下午 4:18
 * @author zp
 * @describe
 */
class BottomNavigationItem : BaseTabItem {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, style: Int) : super(context, attrs, style)

    private val mIcon: ImageView
    private val mTitle: TextView
    private val mMessages: RTextView

    private lateinit var mDefaultDrawable: Drawable
    private lateinit var mCheckedDrawable: Drawable

    private var mDefaultTextColor = resources.getColor(R.color.color_282d42)
    private var mCheckedTextColor = resources.getColor(R.color.color_59abff)

    private var mChecked: Boolean = false

    init {
        inflate(context, R.layout.bottom_navigation_item, this)
        mIcon = findViewById(R.id.icon)
        mTitle = findViewById(R.id.title)
        mMessages = findViewById(R.id.messages)
        setRoundMessageViewNumber(0)
    }


    fun initialize(@DrawableRes drawableRes: Int, @DrawableRes checkedDrawableRes: Int) {
        mDefaultDrawable = ContextCompat.getDrawable(context, drawableRes)!!
        mCheckedDrawable = ContextCompat.getDrawable(context, checkedDrawableRes)!!
    }

    override fun setChecked(checked: Boolean) {
        if (checked) {
            mIcon.setImageDrawable(mCheckedDrawable)
            mTitle.setTextColor(mCheckedTextColor)

        } else {
            mIcon.setImageDrawable(mDefaultDrawable)
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
        mCheckedDrawable = drawable
        if (mChecked) {
            mIcon.setImageDrawable(drawable)
        }
    }

    override fun setDefaultDrawable(drawable: Drawable) {
        mDefaultDrawable = drawable
        if (!mChecked) {
            mIcon.setImageDrawable(drawable)
        }
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