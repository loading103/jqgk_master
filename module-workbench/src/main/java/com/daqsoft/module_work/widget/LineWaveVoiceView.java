package com.daqsoft.module_work.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.blankj.utilcode.util.ConvertUtils;
import com.daqsoft.module_work.R;


import java.util.LinkedList;
import java.util.List;


public class LineWaveVoiceView extends View {
    private static final String TAG = LineWaveVoiceView.class.getSimpleName();

    private Paint paint;
    // 矩形波纹颜色
    private int lineColor;
    // 矩形波纹宽度
    private float lineWidth;
    private float lineSpace;
    private float textSize;
    private static final String DEFAULT_TEXT = " 0s ";
    private String text = DEFAULT_TEXT;
    private int textColor;
    private boolean isStart = false;

    // 默认矩形波纹的宽度  原则上从layout的attr获得
    private int LINE_W = ConvertUtils.dp2px(1);
    // 最小的矩形线高  线宽从lineWidth获得
    private int MIN_WAVE_H = ConvertUtils.dp2px(2);
    // 最高波峰
    private int MAX_WAVE_H = ConvertUtils.dp2px(9);
    // 线条间距
    private int LINE_S = ConvertUtils.dp2px(3);

    private int TEXT_LINE_S = ConvertUtils.dp2px(8);

    // 默认矩形波纹的高度，总共10个矩形，左右各有10个
    private int[] DEFAULT_WAVE_HEIGHT = {2, 3, 4, 3, 2, 2, 2, 2, 2, 2};
    private LinkedList<Integer> mWaveList = new LinkedList<>();

    // 右边波纹矩形的数据，10个矩形复用一个rectF
    private RectF rectRight = new RectF();
    //左边波纹矩形的数据
    private RectF rectLeft = new RectF();

    LinkedList<Integer> list = new LinkedList<>();

    //100ms更新一次
    private static final int UPDATE_INTERVAL_TIME = 100;

    public LineWaveVoiceView(Context context) {
        super(context);
    }

    public LineWaveVoiceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineWaveVoiceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        resetList(list, DEFAULT_WAVE_HEIGHT);
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.LineWaveVoiceView);
        lineColor = mTypedArray.getColor(R.styleable.LineWaveVoiceView_voiceLineColor, Color.parseColor("#2f96ff"));
        lineWidth = mTypedArray.getDimension(R.styleable.LineWaveVoiceView_voiceLineWidth, LINE_W);
        textSize = mTypedArray.getDimension(R.styleable.LineWaveVoiceView_voiceTextSize, 42);
        textColor = mTypedArray.getColor(R.styleable.LineWaveVoiceView_voiceTextColor, Color.parseColor("#5c5c66"));

        lineSpace = mTypedArray.getDimension(R.styleable.LineWaveVoiceView_voiceLineSpace, LINE_S);

        mTypedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int widthcentre = getWidth() / 2;
        int heightcentre = getHeight() / 2;

        //更新时间
        paint.setStrokeWidth(0);
        paint.setColor(textColor);
        paint.setTextSize(textSize);
        float textWidth = paint.measureText(text);
        canvas.drawText(text, widthcentre - textWidth / 2, heightcentre - (paint.ascent() + paint.descent())/2, paint);

        //更新左右两边的波纹矩形
        paint.setColor(lineColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(lineWidth);
        paint.setAntiAlias(true);
        for(int i = 0 ; i < 10 ; i++) {
            //右边矩形
            rectRight.left = widthcentre + i * lineSpace + textWidth / 2 + lineWidth + TEXT_LINE_S;
            rectRight.top = heightcentre - list.get(i) * lineWidth / 2;
            rectRight.right = widthcentre  + i * lineSpace + 2 * lineWidth + textWidth / 2 + TEXT_LINE_S;
            rectRight.bottom = heightcentre + list.get(i) * lineWidth /2;

            //左边矩形
            rectLeft.left = widthcentre - (i * lineSpace +  textWidth / 2 + 2 * lineWidth + TEXT_LINE_S);
            rectLeft.top = heightcentre - list.get(i) * lineWidth / 2;
            rectLeft.right = widthcentre  -( i * lineSpace + textWidth / 2 + lineWidth + TEXT_LINE_S );
            rectLeft.bottom = heightcentre + list.get(i) * lineWidth / 2;

            canvas.drawRoundRect(rectRight, 6, 6, paint);
            canvas.drawRoundRect(rectLeft, 6, 6, paint);
        }
    }

    public synchronized void refreshElement(float  maxAmp) {
        //wave 在 2 ~ 7 之间
        int waveH = MIN_WAVE_H + Math.round(maxAmp * (MAX_WAVE_H -2));
        list.add(0, waveH);
        list.removeLast();
        postInvalidate();
    }

    public synchronized void setText(String text) {
        this.text = text;
        postInvalidate();
    }


    public synchronized void stopRecord(){
        mWaveList.clear();
        resetList(list, DEFAULT_WAVE_HEIGHT);
        text = DEFAULT_TEXT;
        postInvalidate();
    }

    private void resetList(List list, int[] array) {
        list.clear();
        for(int i = 0 ; i < array.length; i++ ){
            list.add(array[i]);
        }
    }
}