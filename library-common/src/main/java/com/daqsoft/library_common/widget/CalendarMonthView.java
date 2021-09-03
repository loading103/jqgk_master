package com.daqsoft.library_common.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.core.content.ContextCompat;

import com.daqsoft.library_common.R;
import com.daqsoft.mvvmfoundation.utils.ContextUtils;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.MonthView;

import java.util.List;

public class CalendarMonthView extends MonthView {

    private int mRadius;


    private Paint mTextPaint = new Paint();
    private Paint mSchemeBasicPaint = new Paint();
    private float mRadio;
    private int mPadding;
    private float mSchemeBaseLine;

    public CalendarMonthView(Context context) {
        super(context);

        mTextPaint.setTextSize(dipToPx(context, 8));
        mTextPaint.setColor(0xffffffff);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setFakeBoldText(true);

        mSchemeBasicPaint.setAntiAlias(true);
        mSchemeBasicPaint.setStyle(Paint.Style.FILL);
        mSchemeBasicPaint.setTextAlign(Paint.Align.CENTER);
        mSchemeBasicPaint.setColor(0xffed5353);
        mSchemeBasicPaint.setFakeBoldText(true);
        mRadio = dipToPx(getContext(), 7);
        mPadding = dipToPx(getContext(), 4);
        Paint.FontMetrics metrics = mSchemeBasicPaint.getFontMetrics();
        mSchemeBaseLine = mRadio - metrics.descent + (metrics.bottom - metrics.top) / 2 + dipToPx(getContext(), 1);

    }

    @Override
    protected void onPreviewHook() {
        mRadius = Math.min(mItemWidth, mItemHeight) / 5 * 2;
    }

    @Override
    protected void onLoopStart(int x, int y) {

    }

    @Override
    protected boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme) {
        int cx = x + mItemWidth / 2;
        int cy = y + mItemHeight / 2;
        canvas.drawCircle(cx, cy, mRadius, mSelectedPaint);
        return true;
    }

    @Override
    protected void onDrawScheme(Canvas canvas, Calendar calendar, int x, int y) {

        if (calendar.getScheme().equals("休")){
            mSchemeTextPaint.setColor(getContext().getResources().getColor(R.color.color_999999));
            return;
        }

        List<Calendar.Scheme>  ss  = calendar.getSchemes();
        int r = dipToPx(getContext(), 2);
        if (ss != null && !ss.isEmpty()){

            if (ss.size() == 1){
                mSchemeBasicPaint.setColor(ss.get(0).getShcemeColor());
                canvas.drawCircle(
                        x + mItemWidth/2f ,
                        y + mItemHeight - r/2f,
                        r,
                        mSchemeBasicPaint);
            }else {
                mSchemeBasicPaint.setColor(ss.get(0).getShcemeColor());
                canvas.drawCircle(
                        x + mItemWidth/2f - 1.5f*r,
                        y + mItemHeight - r/2f,
                        r,
                        mSchemeBasicPaint);
                mSchemeBasicPaint.setColor(ss.get(1).getShcemeColor());
                canvas.drawCircle(
                        x + mItemWidth/2f + 1.5f*r,
                        y + mItemHeight - r/2f,
                        r,
                        mSchemeBasicPaint);
            }

        }

//        mSchemeBasicPaint.setColor(calendar.getSchemeColor());
//        canvas.drawRect(
//                x + mItemWidth - mPadding - mRadio / 2 - mRadio,
//                y + mPadding + mRadio,
//                x + mItemWidth - mPadding - mRadio / 2,
//                y + mPadding + mRadio + mRadio ,
//                mSchemeBasicPaint
//                );
//        canvas.drawCircle(x + mItemWidth - mPadding - mRadio / 2, y + mPadding + mRadio, mRadio, mSchemeBasicPaint);
//        canvas.drawText(calendar.getScheme(),
//                x + mItemWidth - mPadding - mRadio / 2 - getTextWidth(calendar.getScheme()) / 2,
//                y + mPadding + mSchemeBaseLine, mTextPaint);
    }

    private float getTextWidth(String text) {
        return mTextPaint.measureText(text);
    }

    @Override
    protected void onDrawText(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme, boolean isSelected) {
        float baselineY = mTextBaseLine + y;
        int cx = x + mItemWidth / 2;

        boolean isEnable = !onCalendarIntercept(calendar);

        if (isSelected) {
            canvas.drawText(
                    String.valueOf(calendar.getDay()),
                    cx,
                    baselineY,
                    mSelectTextPaint);
        } else if (hasScheme) {
            canvas.drawText(
                    String.valueOf(calendar.getDay()),
                    cx,
                    baselineY,
                    calendar.isCurrentDay() ? mCurDayTextPaint : calendar.isCurrentMonth()  && isEnable ? mSchemeTextPaint : mOtherMonthTextPaint);

        } else {
            canvas.drawText(
                    String.valueOf(calendar.getDay()),
                    cx,
                    baselineY,
                    calendar.isCurrentDay() ? mCurDayTextPaint : calendar.isCurrentMonth() && isEnable ? mCurMonthTextPaint : mOtherMonthTextPaint);
        }
    }

    private static int dipToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
