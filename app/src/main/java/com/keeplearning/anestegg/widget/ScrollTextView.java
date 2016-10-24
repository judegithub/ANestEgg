package com.keeplearning.anestegg.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;
import android.widget.TextView;

/**
 *
 *
 * 调用 fling() 开始滚动
 * 调用 canScroll() 设置是否滚动
 * 实现 ScrollFinishCallBack 回调接口可以实现循环动画
 *
 */
public class ScrollTextView extends TextView {

    private int duration = 12 * 1000;
    private Scroller mScroller;
    private int mLastY = 0;
    private int lineCount = 4;

    private boolean canScroll = false;

    private ScrollFinishCallBack scrollFinishCallBack;

    public ScrollTextView(Context context) {
        super(context);
        init(context);
    }

    public ScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ScrollTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public ScrollTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        this.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        this.setMarqueeRepeatLimit(-1);

        mScroller = new Scroller(context, new LinearInterpolator());
    }

    @Override
    public boolean isFocused() {
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();

        if (mScroller != null && mScroller.computeScrollOffset()) {
            scrollBy(0, mLastY - mScroller.getCurrY());
            mLastY = mScroller.getCurrY();
            postInvalidate();
        }

        if(null != scrollFinishCallBack) {
            scrollFinishCallBack.isFinished(canScroll && mScroller.isFinished());
        }
    }

    /**
     * 设置是否可以滚动
     * 默认不滚动
     * @param canScroll
     */
    public void canScroll(boolean canScroll) {
        this.canScroll = canScroll;
    }

    public void fling() {
        int count = getLineCount();
        if(count <= lineCount || !canScroll) {
            return;
        }
        final int maxY = (getLineCount() * getLineHeight() + getPaddingTop() + getPaddingBottom())
                - getHeight();

        mLastY = 0;
        mScroller.startScroll(0, 0, 0, -maxY, duration);

        invalidate();
    }

    /**
     * 设置大于多少行的时候才能滚动
     * 默认值为4
     * @param count
     */
    public void setLineCount(int count) {
        this.lineCount = count;
    }

    /**
     * 设置动画持续时间
     * 时间为文字的行数*持续时间
     * 默认20秒
     * @param duration
     */
    public void setDuration(int duration) {
        this.duration = lineCount * duration;
    }

    public void setScrollFinishCallBack(ScrollFinishCallBack callBack) {
        this.scrollFinishCallBack = callBack;
    }

    public interface ScrollFinishCallBack {
        void isFinished(boolean isFinished);
    }

}
