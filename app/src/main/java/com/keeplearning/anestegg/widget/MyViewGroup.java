package com.keeplearning.anestegg.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class MyViewGroup extends LinearLayout {

    private boolean s1 = true;
    Scroller mScroller = null;

    private int mScreenWidth, mScreenHeight;

    public MyViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context);

        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        mScreenWidth = wm.getDefaultDisplay().getWidth();
        mScreenHeight = wm.getDefaultDisplay().getHeight();

    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), 0);
            postInvalidate();
        }
    }

    public void beginScroll() {
        if (!s1) {
            mScroller.startScroll(0, 0, 0, 0, 1000);
            s1 = true;
        } else {
            mScroller.startScroll(0, 0, -mScreenWidth + 10, 0, 1000);
            s1 = false;
        }
        invalidate();
    }
}
