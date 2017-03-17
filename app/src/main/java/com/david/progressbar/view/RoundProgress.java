package com.david.progressbar.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.david.progressbar.R;

/**
 * Created by David on 17/3/17.
 */

public class RoundProgress extends HorizontalProgress {

    private int mRadius = dp2px(30);
    private int mMaxPaintWidth;

    public RoundProgress(Context context) {
        this(context, null);
    }

    public RoundProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mReachHeight = mUnReachHeight * 2;

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.RoundProgress);
        mRadius = (int) typedArray.getDimension(R.styleable.RoundProgress_radius, mRadius);

        typedArray.recycle();


        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mMaxPaintWidth = Math.max(mReachHeight, mUnReachHeight);
        //默认四个Padding一致
        int except = mRadius * 2 + mMaxPaintWidth + getPaddingLeft() + getPaddingRight();

        int width = resolveSize(except, widthMeasureSpec);
        int height = resolveSize(except, heightMeasureSpec);

        int readWidth = Math.min(width, height);

        mRadius = (readWidth - getPaddingLeft() - getPaddingRight() - mMaxPaintWidth) / 2;

        setMeasuredDimension(readWidth, readWidth);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        String text = getProgress() + "%";
        float textWidth = mPaint.measureText(text);
        float textHeight = (mPaint.descent() + mPaint.ascent()) / 2;

        canvas.save();

        canvas.translate(getPaddingLeft() + mMaxPaintWidth / 2, getPaddingTop() + mMaxPaintWidth / 2);
        mPaint.setStyle(Paint.Style.STROKE);

        //draw unreadh bar
        mPaint.setColor(mUnReachColor);
        mPaint.setStrokeWidth(mUnReachHeight);
        canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);

        //draw reach bar 现在就只需要画一个圆弧，而不是一整个圆
        mPaint.setColor(mReachColor);
        mPaint.setStrokeWidth(mReachHeight);
        float angle = getProgress() * 1.0f / getMax() * 360;
        canvas.drawArc(new RectF(0, 0, mRadius * 2, mRadius * 2), 0, angle, false, mPaint);

        //draw text
        mPaint.setColor(mTextColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawText(text, mRadius - textWidth / 2, mRadius - textHeight, mPaint);

        canvas.restore();
    }
}
