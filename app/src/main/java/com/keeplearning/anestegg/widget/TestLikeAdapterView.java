package com.keeplearning.anestegg.widget;


import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.Scroller;

import java.util.LinkedList;
import java.util.Queue;

public class TestLikeAdapterView extends AdapterView<ListAdapter> {

    private final String TAG = "TestLikeAdapterView";

    protected ListAdapter mAdapter;

    private boolean mDataChanged = false;

    private int mDisplayOffset = 0;
    private int mSelectedIndex = 0;

    private Queue<View> mRemovedViewQueue = new LinkedList<View>();

    protected Scroller mScroller;

    private DataSetObserver mDataObserver = new DataSetObserver() {

        @Override
        public void onChanged() {
            Log.d("DBG", TAG + " onChanged()....");
            synchronized (TestLikeAdapterView.this) {
                mDataChanged = true;
            }
            invalidate();
            requestLayout();
        }

        @Override
        public void onInvalidated() {
            Log.d("DBG", TAG + " onInvalidated()....");
            reset();
            invalidate();
            requestLayout();
        }
    };

    public TestLikeAdapterView(Context context) {
        super(context);
        init(context);
    }

    public TestLikeAdapterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TestLikeAdapterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mScroller = new Scroller(context);
    }

    private void reset() {

    }

    private int dx = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("DBG", TAG + " onKeyDown() keyCode: " + keyCode
            + " getScrollX(): " + getScrollX()
        );
//        return super.onKeyDown(keyCode, event);
        mScroller.startScroll(getScrollX(), 0, dx += 50, 0);
        invalidate();
        return true;
    }

    /**

     * Returns the adapter currently associated with this widget.
     *
     * @return The adapter used to provide this view's content.
     */
    @Override
    public ListAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * Sets the adapter that provides the data and the views to represent the data
     * in this widget.
     *
     * @param adapter The adapter to use to create this view's content.
     */
    @Override
    public void setAdapter(ListAdapter adapter) {
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mDataObserver);
        }
        mAdapter = adapter;
        mAdapter.registerDataSetObserver(mDataObserver);
//        reset();

        Log.d("DBG", TAG + " setAdapter()...");
        super.setFocusableInTouchMode(true);
        super.setFocusable(true);
    }

    /**
     * @return The view corresponding to the currently selected item, or null
     * if nothing is selected
     */
    @Override
    public View getSelectedView() {
        return null;
    }

    /**
     * Sets the currently selected item. To support accessibility subclasses that
     * override this method must invoke the overriden super method first.
     *
     * @param position Index (starting at 0) of the data item to be selected.
     */
    @Override
    public void setSelection(int position) {
        mSelectedIndex = position;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.d("DBG", TAG + " onLayout() changed: " + changed
            + " left: " + left
            + " top: " + top
            + " right: " + right
            + " bottom: " + bottom
        );

        layoutChildren();
    }

    private void layoutChildren() {
        if (mAdapter == null) {
            return;
        }

        fillList();
        positionItems();
    }

    private void fillList() {
        int edge = 0;
        int childCount = getChildCount();
        if (childCount > 0) {
            View lastChild = getChildAt(getChildCount() - 1);
            edge = lastChild.getRight();
        }

        View child;
        int viewIndex = mSelectedIndex;
        while(edge < getWidth() && viewIndex < mAdapter.getCount()) {
            child = mAdapter.getView(viewIndex, mRemovedViewQueue.poll(), this);
            addAndMeasureChild(child, -1);

            viewIndex++;
            edge += child.getMeasuredWidth(); // 是否需要加上margin padding等...
        }
    }

    private void addAndMeasureChild(final View child, int viewPos) {
        LayoutParams params = child.getLayoutParams();
        if (params == null) {
            params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        }

        addViewInLayout(child, viewPos, params, true);
        child.measure(MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.AT_MOST));
    }

    private void positionItems() {
        int childCount = getChildCount();
        Log.d("DBG", TAG + " positionItems() childCount: " + childCount);
        if (childCount > 0) {
            int left = mDisplayOffset;
            View child;
            int childWidth;
            for (int i = 0; i < childCount; i++) {
                child = getChildAt(i);
                childWidth = child.getMeasuredWidth();
                child.layout(left, 0, left + childWidth, child.getMeasuredHeight());
                left += childWidth + child.getPaddingRight() + 10;
            }
        }
    }

    @Override
    public void computeScroll() {
        // 第三步，重写computeScroll()方法，并在其内部完成平滑滚动的逻辑
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }
}
