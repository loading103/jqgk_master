package com.daqsoft.module_statistics.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.blankj.utilcode.util.ScreenUtils
import com.daqsoft.module_statistics.R
import com.daqsoft.module_statistics.repository.pojo.vo.MyLegend
import com.daqsoft.mvvmfoundation.utils.dp
import com.daqsoft.mvvmfoundation.utils.sp
import com.github.mikephil.charting.utils.Utils

/**
 * @package name：com.daqsoft.module_statistics.widget
 * @date 18/6/2021 下午 4:07
 * @author zp
 * @describe
 */
class ArcChart  : View {

    // 屏幕的宽
    private var screenWidth = ScreenUtils.getScreenWidth()

    // 数据
    private var chartData = listOf<MyLegend>()

    private val paint : Paint = Paint()

    private val radius = 24.dp
    private val space = 16.dp

    private var total = 0f

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

    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val size = Utils.convertDpToPixel(50f).toInt()
        setMeasuredDimension(
            Math.max(
                suggestedMinimumWidth,
                resolveSize(
                    size,
                    widthMeasureSpec
                )
            ),
            Math.max(
                suggestedMinimumHeight,
                resolveSize(
                    size,
                    heightMeasureSpec
                )
            )
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        onDrawProgress(canvas)

    }



    /**
     * 绘制进度
     * @param canvas Canvas?
     */
    private fun onDrawProgress(canvas: Canvas?){

        val centerX = width / 2
        val centerY = height / 2


        paint.isAntiAlias = true
        paint.strokeCap = Paint.Cap.SQUARE



        chartData.forEachIndexed { index, pair ->

            paint.style = Paint.Style.STROKE
            // 绘制背景
            paint.strokeWidth = 8.dp.toFloat()
            paint.color = context.resources.getColor(R.color.color_f4f7ff)

            val rectF = RectF()
            rectF.left = (centerX - radius - paint.strokeWidth / 2 - index * space).toFloat()
            rectF.top = (centerY - radius - paint.strokeWidth / 2 - index * space).toFloat()
            rectF.right =  (centerX + radius + paint.strokeWidth / 2 + index * space).toFloat()
            rectF.bottom = (centerY + radius + paint.strokeWidth / 2 + index * space).toFloat()
            canvas?.drawArc(rectF, -90f, -270f, false, paint)

            // 绘制进度
            paint.color = pair.color ?: Color.BLACK
            val progressAngle = if(total == 0f){
                0f
            }else{

                (pair.value.toFloat() * 270 )/total
            }
            canvas?.drawArc(rectF, -90f, -progressAngle, false, paint)

            // 绘制文字
            paint.style = Paint.Style.FILL
            paint.color =  context.resources.getColor(R.color.color_59abff)
            paint.strokeWidth = 0.dp.toFloat()
            paint.textSize = 12.sp.toFloat()
            val progressText = if (total == 0f){
                0
            }else{
                ((pair.value.toFloat()/total) * 100).toInt()
            }.toString() + "%"
            val progressFontMetrics : Paint.FontMetrics = paint.fontMetrics
            val progressTextHigh = progressFontMetrics.descent - progressFontMetrics.ascent

            canvas?.drawText(progressText, (centerX + space).toFloat(), rectF.top + 4.dp, paint)
        }

        paint.reset()
    }


    fun setArcChartData(list: List<MyLegend>){
        this.chartData = list
        this.total = list.map { it.value.toFloat() }.sum()
    }

}
