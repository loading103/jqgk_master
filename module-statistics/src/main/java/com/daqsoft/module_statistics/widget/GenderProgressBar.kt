package com.daqsoft.module_statistics.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import com.blankj.utilcode.util.ScreenUtils
import com.daqsoft.module_statistics.R
import com.daqsoft.mvvmfoundation.utils.dp
import com.daqsoft.mvvmfoundation.utils.sp
import kotlin.math.abs
import kotlin.math.max


/**
 * @package name：com.daqsoft.module_mine.widget
 * @date 13/11/2020 下午 4:19
 * @author zp
 * @describe
 */
class GenderProgressBar : View {


    // 屏幕的宽
    private var screenWidth = ScreenUtils.getScreenWidth()

    /**
     * 进度背景宽
     */
    var progressBackgroundWidth = 4.dp

    /**
     * 进度背景色
     */
    var progressBackgroundColor = Color.BLACK

    /**
     * 进度条宽
     */
    var progressWidth = 4.dp

    /**
     * 进度颜色
     */
    var progressColor = Color.WHITE

    /**
     * 当前进度
     */
    private var progress = 0f
    /**
     * 总进度
     */
    private var total = 0f

    /**
     * 文字颜色
     */
    var textColor = Color.BLACK

    /**
     * 文字内容
     */
    var textContent = "0%"

    private val paint : Paint = Paint()
    
    constructor(context: Context?) : super(context){
        initView(context,null)
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        initView(context,attrs)
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){
        initView(context,attrs)
    }


    private fun initView(context: Context?, attrs: AttributeSet?){

        if (attrs == null){
            return
        }
        
        val array = getContext().obtainStyledAttributes(attrs, R.styleable.GenderProgressBar)
        progressBackgroundWidth = array.getDimensionPixelSize(R.styleable.GenderProgressBar_progress_background_width, 4.dp)
        progressBackgroundColor = array.getColor(R.styleable.GenderProgressBar_progress_background_color, Color.BLACK)
        progressWidth = array.getDimensionPixelSize(R.styleable.GenderProgressBar_progress_width, 4.dp)
        progressColor = array.getColor(R.styleable.GenderProgressBar_progress_color, Color.WHITE)
        textColor = array.getColor(R.styleable.GenderProgressBar_text_color, Color.BLACK)
        progress = array.getFloat(R.styleable.GenderProgressBar_current_progress, 50f)
        total = array.getFloat(R.styleable.GenderProgressBar_total_progress, 100f)
        array.recycle()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthModel = MeasureSpec.getMode(widthMeasureSpec)
        val heightModel = MeasureSpec.getMode(heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val width = if (widthModel == MeasureSpec.EXACTLY) {
            widthSize
        } else {
            screenWidth * 0.5.toInt()
        }
        val height = if (heightModel == MeasureSpec.EXACTLY) {
            heightSize
        } else {
            widthSize * 0.5.toInt()
        }

        setMeasuredDimension(width, height)

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        onDrawProgress(canvas)
        onDrawText(canvas)
    }


    /**
     * 绘制文字
     * @param canvas Canvas?
     */
    private fun onDrawText(canvas: Canvas?){

        paint.color = textColor
        paint.isAntiAlias = true


        paint.textSize = 16.sp.toFloat()
        paint.typeface = Typeface.DEFAULT_BOLD
        val progressText = if (total == 0f){
            0
        }else{
            Math.round((progress/total) * 100)
        }.toString()
        val progressTextWidth = paint.measureText(progressText)
        val progressFontMetrics : Paint.FontMetrics = paint.fontMetrics
        val progressTextHigh = progressFontMetrics.descent - progressFontMetrics.ascent

        paint.textSize = 12.sp.toFloat()
        paint.typeface = Typeface.DEFAULT
        val percentageText = "%"
        val percentageTextWidth = paint.measureText(percentageText)
        val percentageFontMetrics : Paint.FontMetrics = paint.fontMetrics
        val percentageTextHigh = percentageFontMetrics.descent - percentageFontMetrics.ascent

        val textX =  ( width - progressTextWidth - percentageTextWidth) / 2
        val textY: Float = (height - (max(progressBackgroundWidth, progressWidth) * 2))  - max(progressTextHigh, percentageTextHigh)  / 2

        paint.textSize = 16.sp.toFloat()
        paint.typeface = Typeface.DEFAULT_BOLD
        canvas?.drawText(
            progressText,
            textX,
            textY ,
            paint
        )

        paint.textSize = 12.sp.toFloat()
        paint.typeface = Typeface.DEFAULT
        canvas?.drawText(
            percentageText,
            textX+percentageTextWidth+progressTextWidth/2,
            textY ,
            paint
        )

        paint.reset()

    }


    /**
     * 绘制进度
     * @param canvas Canvas?
     */
    private fun onDrawProgress(canvas: Canvas?){

        paint.isAntiAlias = true
        paint.strokeWidth = progressBackgroundWidth.toFloat()
        paint.style = Paint.Style.STROKE
        paint.color = progressBackgroundColor
        paint.strokeCap = Paint.Cap.SQUARE

        //绘制背景圆
        val rectF = RectF(
            (progressBackgroundWidth / 2).toFloat(),
            (progressBackgroundWidth / 2).toFloat(),
            (width - progressBackgroundWidth / 2).toFloat(),
            (height - progressBackgroundWidth / 2).toFloat()
        )
        canvas?.drawArc(rectF, 0f, 360f, false, paint)


        paint.color = progressColor

        val progressAngle = if(total == 0f){
            0f
        }else{
            (progress * 360 )/total
        }

        canvas?.drawArc(rectF, -90f, Math.min(progressAngle,360f), false, paint)

        paint.reset()


    }

    /**
     * 设置进度
     */
    fun setProgress(progress:Float,total:Float){
        this.total = total
        this.progress = progress
    }
}