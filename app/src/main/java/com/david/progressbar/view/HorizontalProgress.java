package com.david.progressbar.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

import com.david.progressbar.R;

/**
 * Created by David on 17/3/17.
 * 水平进度条
 */

public class HorizontalProgress extends ProgressBar {
    private static final int DEFAULT_TEXT_SIZE = 10;//sp
    private static final int DEFAULT_TEXT_COLOR = 0xFFF00D1;//sp
    private static final int DEFAULT_UNREACH_COLOR = 0xFFD2A3EA;//sp
    private static final int DEFAULT_UNREACH_HEIGHT = 2;//dp
    private static final int DEFAULT_REACH_COLOR = DEFAULT_TEXT_COLOR;
    private static final int DEFAULT_REACH_HEIGHT = 2;//dp
    private static final int DEFAULT_TEXT_OFFSET = 10;


    protected int mTextSize = sp2px(DEFAULT_TEXT_SIZE);
    protected int mTextColor = DEFAULT_TEXT_COLOR;
    protected int mUnReachColor = DEFAULT_UNREACH_COLOR;
    protected int mUnReachHeight = dp2px(DEFAULT_UNREACH_HEIGHT);
    protected int mReachColor = DEFAULT_REACH_COLOR;
    protected int mReachHeight = dp2px(DEFAULT_REACH_HEIGHT);
    protected int mTextOffset = dp2px(DEFAULT_TEXT_OFFSET);

    protected Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    //控件宽度减去Padding值得到的真实控件长度，在onMeasure时赋值，onDraw时使用
    protected int mRealWidth;


    public HorizontalProgress(Context context) {
        this(context, null);
    }

    public HorizontalProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getStyleAttrs(attrs);
    }

    /**
     * 获取自定义属性
     *
     * @param attrs
     */
    private void getStyleAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.HorizontalProgress);
        mTextSize = (int) typedArray.getDimension(R.styleable.HorizontalProgress_progress_text_size, mTextSize);
        mTextColor = typedArray.getColor(R.styleable.HorizontalProgress_progress_text_color, mTextColor);
        mTextOffset = (int) typedArray.getDimension(R.styleable.HorizontalProgress_progress_text_offset, mTextOffset);

        mUnReachColor = typedArray.getColor(R.styleable.HorizontalProgress_progress_unreach_color, mUnReachColor);
        mUnReachHeight = (int) typedArray.getDimension(R.styleable.HorizontalProgress_progress_unreach_height, mUnReachHeight);

        mReachColor = typedArray.getColor(R.styleable.HorizontalProgress_progress_reach_color, mReachColor);
        mReachHeight = (int) typedArray.getDimension(R.styleable.HorizontalProgress_progress_reach_height, mReachHeight);

        typedArray.recycle();

        mPaint.setTextSize(mTextSize);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //宽度，进度条不支持wrap_content，必须给一个确定的宽度，所以不用像高度一样去进行判断
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthValue = MeasureSpec.getSize(widthMeasureSpec);

        //判断高度
        int heightValue = measurHeight(heightMeasureSpec);

        //确定了宽和高
        setMeasuredDimension(widthValue, heightValue);

        mRealWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
    }

    //对于三种模式的处理过程
    private int measurHeight(int heightMeasureSpec) {
        int result = 0;
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int value = MeasureSpec.getSize(heightMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = value;
        } else {
            //获取字体的高度
            int textHeight = (int) (mPaint.descent() - mPaint.ascent());
            result = getPaddingTop() + getPaddingBottom() + Math.max(Math.max(mReachHeight, mUnReachHeight), Math.abs(textHeight));
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, value);
            }
        }
        return result;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getPaddingLeft(), getHeight() / 2);

        //draw Reach Bar(文字左侧bar)
        //如果已绘制长度加上文本宽度加上offSet宽度大于了mRealWidth的时候就不需要再去画未绘制区域了
        boolean noNeedUnReach = false;

        //获取字体的长度
        String text = getProgress() + "%";
        int textWidth = (int) mPaint.measureText(text);

        float radio = getProgress() * 1.0f / getMax();
        float progressX = radio * mRealWidth;

        if (progressX + textWidth > mRealWidth) {
            //重置，保证文字显示正确
            progressX = mRealWidth - textWidth;
            noNeedUnReach = true;
        }

        float endX = progressX - mTextOffset / 2;
        if (endX > 0) {
            mPaint.setColor(mReachColor);
            mPaint.setStrokeWidth(mReachHeight);
            canvas.drawLine(0, 0, endX, 0, mPaint);
        }

        //draw text
        mPaint.setColor(mTextColor);
        int y = (int) (-(mPaint.descent() + mPaint.ascent()) / 2);
        canvas.drawText(text, progressX, y, mPaint);

        //draw unReachBar(文字右侧bar)
        if (!noNeedUnReach) {
            float start = progressX + mTextOffset / 2 + textWidth;
            mPaint.setColor(mUnReachColor);
            mPaint.setStrokeWidth(mUnReachHeight);
            canvas.drawLine(start, 0, mRealWidth, 0, mPaint);

        }

        canvas.restore();
    }

    public int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, getResources().getDisplayMetrics());
    }

    private int sp2px(int spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, getResources().getDisplayMetrics());
    }
}
