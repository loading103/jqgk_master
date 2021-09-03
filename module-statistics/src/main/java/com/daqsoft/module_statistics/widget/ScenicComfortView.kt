package com.daqsoft.module_statistics.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import com.blankj.utilcode.util.ScreenUtils
import com.daqsoft.mvvmfoundation.utils.dp
import com.daqsoft.mvvmfoundation.utils.sp


/**
 * @package name：com.daqsoft.module_statistics.widget
 * @date 8/6/2021 上午 10:06
 * @author zp
 * @describe
 */
class ScenicComfortView  : View {

   // 屏幕的宽
    private var screenWidth = ScreenUtils.getScreenWidth()

   // 画笔
    private var paint  : Paint = Paint()

    // 中心点圆坐标x
    private var mCenterX = 0f
    // 中心点圆坐标y
    private var mCenterY = 0f
    // 起始角度
    private var mStartAngle = 180f
    // 刻度半径
    private var calibrationRadius = 0f

    private var progress = 0f
    private var total = 0f

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


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
            widthSize * 0.6.toInt()
        }

        mCenterX = (width / 2).toFloat()
        mCenterY = (height - 20.dp).toFloat()
        setMeasuredDimension(width, height)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawText(canvas)
        drawCalibration(canvas)
        drawProgress(canvas)
        drawArc(canvas)
    }



    /**
     * 绘制文字
     */
    fun drawText(canvas: Canvas){
        paint.color = Color.WHITE
        paint.isAntiAlias = true
        paint.textSize = 32.sp.toFloat()
        paint.typeface = Typeface.DEFAULT_BOLD

        val text = if (total == 0f){
            0
        }else{
            ((progress/total) * 100).toInt()
        }.toString() + "%"
        val textWidth = paint.measureText(text)
        val textX =  ( width - textWidth) / 2
        val fontMetrics: Paint.FontMetrics = paint.fontMetrics
        val textY =  mCenterY

        canvas.drawText(
            text,
            textX,
            textY ,
            paint
        )
        paint.reset()
    }


    /**
     * 绘制刻度
     */
    fun drawCalibration(canvas: Canvas){
        paint.color = Color.parseColor("#7fffffff")
        paint.isAntiAlias = true
        paint.strokeWidth = 2.dp.toFloat()

        calibrationRadius = (width * 0.5 * 0.66).toFloat()
        val calibrationLength = 6.dp
        val calibrationLongLength = calibrationLength * 1.65


        for (i in -1 until 32) {
            if (i % 5 != 0) {
                // 短刻度
                val angle: Float = i * 6 + mStartAngle
                val point1 = getCoordinatePoint(calibrationRadius, angle)
                val point2 = getCoordinatePoint(calibrationRadius - calibrationLength, angle)
                canvas.drawLine(point1[0], point1[1], point2[0], point2[1], paint)
            }else{
                // 长刻度
                val angle: Float = i * 6 + mStartAngle
                val point1 = getCoordinatePoint(calibrationRadius, angle)
                val point2 = getCoordinatePoint((calibrationRadius - calibrationLongLength).toFloat(), angle)
                canvas.drawLine(point1[0], point1[1], point2[0], point2[1], paint)

            }
        }

        paint.reset()
    }

    /**
     * 依圆心坐标，半径，扇形角度，计算出扇形终射线与圆弧交叉点的xy坐标
     */
    private fun getCoordinatePoint(radius: Float, cirAngle: Float): FloatArray {
        val point = FloatArray(2)
        //将角度转换为弧度
        var arcAngle = Math.toRadians(cirAngle.toDouble())
        if (cirAngle < 90) {
            point[0] = (mCenterX + Math.cos(arcAngle) * radius).toFloat()
            point[1] = (mCenterY + Math.sin(arcAngle) * radius).toFloat()
        } else if (cirAngle == 90f) {
            point[0] = mCenterX
            point[1] = mCenterY + radius
        } else if (cirAngle > 90 && cirAngle < 180) {
            arcAngle = Math.PI * (180 - cirAngle) / 180.0
            point[0] = (mCenterX - Math.cos(arcAngle) * radius).toFloat()
            point[1] = (mCenterY + Math.sin(arcAngle) * radius).toFloat()
        } else if (cirAngle == 180f) {
            point[0] = mCenterX - radius
            point[1] = mCenterY
        } else if (cirAngle > 180 && cirAngle < 270) {
            arcAngle = Math.PI * (cirAngle - 180) / 180.0
            point[0] = (mCenterX - Math.cos(arcAngle) * radius).toFloat()
            point[1] = (mCenterY - Math.sin(arcAngle) * radius).toFloat()
        } else if (cirAngle == 270f) {
            point[0] = mCenterX
            point[1] = mCenterY - radius
        } else {
            arcAngle = Math.PI * (360 - cirAngle) / 180.0
            point[0] = (mCenterX + Math.cos(arcAngle) * radius).toFloat()
            point[1] = (mCenterY - Math.sin(arcAngle) * radius).toFloat()
        }
        return point
    }


    /**
     * 绘制进度
     */
    fun drawProgress(canvas: Canvas){
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 20.dp.toFloat()
        paint.isAntiAlias = true
        paint.color = Color.parseColor("#3fffffff")
        paint.strokeCap = Paint.Cap.ROUND
        val rectF = RectF()
        rectF.left = mCenterX - calibrationRadius - 8.dp - paint.strokeWidth / 2
        rectF.top = mCenterY - calibrationRadius - 8.dp - paint.strokeWidth / 2
        rectF.right = mCenterX + calibrationRadius +  8.dp + paint.strokeWidth / 2
        rectF.bottom = mCenterY + calibrationRadius + 8.dp + paint.strokeWidth / 2
        canvas.drawArc(rectF, 180f, 180f, false, paint)

        paint.color = Color.WHITE
        val progressAngle = if(total == 0f){
            0f
        }else{
            (progress * 180 )/total
        }
        canvas.drawArc(rectF, 180f,Math.min(progressAngle,180f), false, paint)
        paint.reset()
    }

    /**
     * 绘制弧线
     */
    fun drawArc(canvas: Canvas){

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1.dp.toFloat()
        paint.isAntiAlias = true
        paint.color = Color.parseColor("#21ffffff")
        paint.strokeCap = Paint.Cap.ROUND

        val rectF = RectF()
        rectF.left = mCenterX - calibrationRadius - 8.dp - 20.dp - 4.dp
        rectF.top = mCenterY - calibrationRadius  - 8.dp - 20.dp - 4.dp
        rectF.right = mCenterX + calibrationRadius +  8.dp + 20.dp + 4.dp
        rectF.bottom = mCenterY + calibrationRadius + 8.dp + 20.dp + 4.dp

        canvas.drawArc(rectF, 180f, 180f, false, paint)
        paint.reset()
    }


    /**
     * 设置进度
     */
    fun setProgress(progress:Float,total:Float){
        this.total = total
        val animator = ValueAnimator.ofFloat(0f, progress)
        animator.duration = 1000
        animator.interpolator = AccelerateInterpolator()
        animator.addUpdateListener { valueAnimator ->
            this.progress = valueAnimator.animatedValue as Float
            invalidate()
        }
        animator.start()
    }

}