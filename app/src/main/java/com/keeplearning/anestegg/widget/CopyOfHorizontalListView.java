package com.keeplearning.anestegg.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.keeplearning.anestegg.R;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 修改HorizontalListView，可用于tv上，并且循环
 */
public class CopyOfHorizontalListView extends AdapterView<ListAdapter> {

    private final String TAG = "CopyOfHorizontalListView";

    public boolean mAlwaysOverrideTouch = true;
    private boolean mDataChanged = false;
    private boolean mIsCycle = true;
    private boolean mGainFocus = false;

    private int mLeftViewIndex = INVALID_POSITION;
    private int mRightViewIndex = 0;
    @Deprecated
    private int mMaxX = Integer.MAX_VALUE;
    private int mDisplayOffset = 0;
    /**
     * 选中的view所在的位置
     */
    private int mSelectedViewIndex = 0;
    private int mRemoveLeftViewTotal = 0;
    private int mRemoveRightViewTotal = 0;
    @Deprecated
    private int mOldChildCount = 0;
    @Deprecated
    private int mNewChildCount = 0;
    private int childCountDifferent = 0;
    /**
     * 用来记录按的是什么键，做对应的操作
     */
    private int keyCodeWhich = KeyEvent.KEYCODE_UNKNOWN;

    protected int mCurrentX;
    protected int mNextX;

    private View mOldSelectedView;

    protected ListAdapter mAdapter;
    protected Scroller mScroller;

    private GestureDetector mGesture;
    private Queue<View> mRemovedViewQueue = new LinkedList<View>();
    private OnItemSelectedListener mOnItemSelected;
    private OnItemClickListener mOnItemClicked;
    private OnItemLongClickListener mOnItemLongClicked;
    private  Rect mParentRect;

    private GestureDetector.OnGestureListener mOnGesture = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onDown(MotionEvent e) {
            return CopyOfHorizontalListView.this.onDown(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return CopyOfHorizontalListView.this.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.d("DBG", TAG + " onScroll() "
                    + " before mNextX: " + mNextX
                    + " distanceX: " + distanceX
            );
            synchronized (CopyOfHorizontalListView.this) {
                mNextX += (int) distanceX;
            }
            Log.d("DBG", TAG + " onScroll() "
                    + " after mNextX: " + mNextX
            );
            requestLayout();

            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.d("DBG", TAG + " onSingleTapConfirmed() "
            );
            int childCount = getChildCount();
            View child;
            for (int i = 0; i < childCount; i++) {
                child = getChildAt(i);
                if (isEventWithinView(e, child)) {
                    int position = mLeftViewIndex + 1 + i;

                    // 这是为了循环而修改的...
                    if (mIsCycle && position > (mAdapter.getCount() - 1)) {
                        position = position - mAdapter.getCount();
                    }

                    if (mOnItemClicked != null) {
                        mOnItemClicked.onItemClick(CopyOfHorizontalListView.this, child, position,
                                mAdapter.getItemId(position));
                    }
                    if (mOnItemSelected != null) {
                        mOnItemSelected.onItemSelected(CopyOfHorizontalListView.this, child, position,
                                mAdapter.getItemId(position));
                    }
                    break;
                }

            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                boolean eventWithinView = isEventWithinView(e, child);
                Log.d("DBG", " onLongPress() eventWithinView: " + eventWithinView);
                if (eventWithinView) {
                    if (mOnItemLongClicked != null) {
                        mOnItemLongClicked.onItemLongClick(CopyOfHorizontalListView.this, child, mLeftViewIndex + 1 + i,
                                mAdapter.getItemId(mLeftViewIndex + 1 + i));
                    }
                    break;
                }
            }
        }

        private boolean isEventWithinView(MotionEvent e, View child) {
            Rect viewRect = new Rect();
            int[] childPosition = new int[2];
            child.getLocationOnScreen(childPosition);
            int left = childPosition[0];
            int right = left + child.getWidth();
            int top = childPosition[1];
            int bottom = top + child.getHeight();
            viewRect.set(left, top, right, bottom);
            return viewRect.contains((int) e.getRawX(), (int) e.getRawY());
        }
    };


    public CopyOfHorizontalListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private synchronized void initView() {
        setClickable(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);

        mLeftViewIndex = INVALID_POSITION;
        mRightViewIndex = 0;
        mDisplayOffset = 0;
        mCurrentX = 0;
        mNextX = 0;
        mMaxX = Integer.MAX_VALUE;
        mScroller = new Scroller(getContext());
        mGesture = new GestureDetector(getContext(), mOnGesture);
    }

    @Override
    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener listener) {
        mOnItemSelected = listener;
    }

    @Override
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        mOnItemClicked = listener;
    }

    @Override
    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener listener) {
        mOnItemLongClicked = listener;
    }

    private DataSetObserver mDataObserver = new DataSetObserver() {

        @Override
        public void onChanged() {
            Log.d("DBG", TAG + " onChanged()....");
            synchronized (CopyOfHorizontalListView.this) {
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

    @Override
    public ListAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void setSelection(int position) {
        Log.d("DBG", TAG + " setSelection(): " + position);
        mSelectedViewIndex = position;
    }

    public int getSelection() {
        return mSelectedViewIndex;
    }

    public void setCycle(boolean isCycle) {
        mIsCycle = isCycle;
    }

    @Override
    public View getSelectedView() {
        Log.d("DBG", TAG + " getSelectedView() "
                + " mSelectedViewIndex: " + mSelectedViewIndex
        );
        return getChildAt(mSelectedViewIndex);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mDataObserver);
        }
        mAdapter = adapter;
        if (mAdapter != null) {
            mAdapter.registerDataSetObserver(mDataObserver);
        }
        reset();
    }

    private synchronized void reset() {
        initView();
        removeAllViewsInLayout();
        requestLayout();
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

    @Override
    protected synchronized void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        Log.d("DBG", TAG + " onLayout()"
                + " changed: " + changed
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

        Log.d("DBG", TAG + " layoutChildren() mDataChanged: " + mDataChanged
                + " mCurrentX: " + mCurrentX
        );
        if (mDataChanged) {
            int oldCurrentX = mCurrentX;
            initView();
            removeAllViewsInLayout();
            mNextX = oldCurrentX;
            mDataChanged = false;
        }

        if (mScroller.computeScrollOffset()) {
            int scrollx = mScroller.getCurrX();
            mNextX = scrollx;
            Log.d("DBG", TAG + " layoutChildren() mNextX: " + mNextX
            );
        }

        // 这是为了循环而修改的...
        if (!mIsCycle) {
            if (mNextX <= 0) {
                mNextX = 0;
                mScroller.forceFinished(true);
            } else if (mNextX >= mMaxX) {
                mNextX = mMaxX;
                mScroller.forceFinished(true);
            }
        }

        int dx = mCurrentX - mNextX;
        Log.d("DBG", TAG + " layoutChildren()"
                + " dx: " + dx
                + " mCurrentX: " + mCurrentX
                + " mNextX: " + mNextX
        );

        removeNonVisibleItems(dx);
        fillList(dx);
        positionItems(dx);

        mCurrentX = mNextX;

        if (!mScroller.isFinished()) {
            post(new Runnable() {
                @Override
                public void run() {
                    requestLayout();
                }
            });
        }

        if (mGainFocus) {
            setSelectState();
        }
    }

    private void setSelectState() {
        if (mOldSelectedView != null) {
            mOldSelectedView.setBackgroundResource(R.color.holo_blue_bright);
        }
        View selectedView = getSelectedView();
        Log.d("DBG", TAG + " setSelectState() "
                + " selectedView: " + selectedView
        );
        if (selectedView != null) {
            mOldSelectedView = selectedView;
            mOldSelectedView.setBackgroundResource(R.color.colorAccent);

            if (mOnItemSelected != null) {
                int position = mLeftViewIndex + 1 + mSelectedViewIndex;
                if (position > (mAdapter.getCount() - 1)) {
                    position = position - mAdapter.getCount();
                }
                mOnItemSelected.onItemSelected(CopyOfHorizontalListView.this, mOldSelectedView, position,
                        mAdapter.getItemId(position));
            }
        }
    }

    private void fillList(final int dx) {
        int edge = 0;
        View child = getChildAt(getChildCount() - 1);
        Log.d("DBG", TAG + " fillList() child: " + child);
        if (child != null) {
            edge = child.getRight();
        }
        fillListRight(edge, dx);

        edge = 0;
        child = getChildAt(0);
        if (child != null) {
            edge = child.getLeft();
        }
        fillListLeft(edge, dx);

        mNewChildCount = getChildCount();
        if (mOldChildCount != 0) {
            childCountDifferent = mNewChildCount - mOldChildCount;
        }
        mOldChildCount = mNewChildCount;
        Log.d("DBG", TAG + " fillList() childCount: " + mNewChildCount);

        Log.d("DBG", TAG + " fillList() "
                + " mSelectedViewIndex: " + mSelectedViewIndex
                + " mRemoveLeftViewTotal: " + mRemoveLeftViewTotal);
        if (keyCodeWhich == KeyEvent.KEYCODE_DPAD_LEFT) {
//            mSelectedViewIndex = mSelectedViewIndex - 1 - mRemoveLeftViewTotal;
        } else if (keyCodeWhich == KeyEvent.KEYCODE_DPAD_RIGHT) {
            mSelectedViewIndex = mSelectedViewIndex + 1 - mRemoveLeftViewTotal;
            Log.d("DBG", TAG + " fillList() mSelectedViewIndex: " + mSelectedViewIndex);
        }
        // reset
        keyCodeWhich = KeyEvent.KEYCODE_UNKNOWN;
        mRemoveLeftViewTotal = 0;
        mRemoveRightViewTotal = 0;
    }

    private void fillListRight(int rightEdge, final int dx) {
        Log.d("DBG", TAG + " fillListRight() "
                + " rightEdge: " + rightEdge
                + " mRightViewIndex: " + mRightViewIndex
        );
        View child;

        // 这是为了循环而修改的...
        while (mIsCycle ? (rightEdge + dx < getWidth()) : (rightEdge + dx < getWidth() && mRightViewIndex < mAdapter.getCount())) {
            if (mRightViewIndex == mAdapter.getCount()) {
                mRightViewIndex = 0;
            }

            child = mAdapter.getView(mRightViewIndex, mRemovedViewQueue.poll(), this);
            addAndMeasureChild(child, -1);
            rightEdge += child.getMeasuredWidth();

            // 这是为了循环而修改的...
            if (!mIsCycle) {
                if (mRightViewIndex == mAdapter.getCount() - 1) {
                    mMaxX = mCurrentX + rightEdge - getWidth();
                }
                Log.d("DBG", TAG + " fillListRight() add view "
                                + " mRightViewIndex: " + mRightViewIndex
                                + " mCurrentX: " + mCurrentX
//                    + " mMaxX: " + mMaxX
                );
                if (mMaxX < 0) {
                    mMaxX = 0;
                }
            }

            mRightViewIndex++;

            if (mRemoveRightViewTotal > 0) {
                mRemoveRightViewTotal--;
            }
        }
    }

    private void fillListLeft(int leftEdge, final int dx) {
        Log.d("DBG", TAG + " fillListLeft() leftEdge: " + leftEdge
                + " mLeftViewIndex: " + mLeftViewIndex
        );
        View child;
        // 这是为了循环而修改的...
        while (mIsCycle ? (leftEdge + dx > 0) : (leftEdge + dx > 0 && mLeftViewIndex >= 0)) {
            if (mLeftViewIndex < 0) {
                mLeftViewIndex = mAdapter.getCount() - 1;
            }

            child = mAdapter.getView(mLeftViewIndex, mRemovedViewQueue.poll(), this);
            addAndMeasureChild(child, 0);
            leftEdge -= child.getMeasuredWidth();
            mLeftViewIndex--;
            mDisplayOffset -= child.getMeasuredWidth();
            Log.d("DBG", TAG + " fillListLeft() add view "
                    + " leftEdge: " + leftEdge
                    + " mLeftViewIndex: " + mLeftViewIndex
                    + " mDisplayOffset: " + mDisplayOffset
            );

            if (mRemoveLeftViewTotal > 0) {
                mRemoveLeftViewTotal--;
            }
        }
    }

    private void removeNonVisibleItems(final int dx) {
        View child = getChildAt(0);
        while (child != null && child.getRight() + dx <= 0) {
            mDisplayOffset += child.getMeasuredWidth();
            mRemovedViewQueue.offer(child);
            removeViewInLayout(child);
            mLeftViewIndex++;

            // 这是为了循环而修改的...
            if (mIsCycle) {
                if (mLeftViewIndex == getAdapter().getCount()) {
                    mLeftViewIndex = 0;
                }
            }

            Log.d("DBG", TAG + " removeNonVisibleItems() remove left view mLeftViewIndex: " + mLeftViewIndex);
            child = getChildAt(0);
            mRemoveLeftViewTotal++;
        }

        child = getChildAt(getChildCount() - 1);
        while (child != null && child.getLeft() + dx >= getWidth()) {
            mRemovedViewQueue.offer(child);
            removeViewInLayout(child);
            mRightViewIndex--;

            // 这是为了循环而修改的...
            if (mIsCycle) {
                if (mRightViewIndex == 0) {
                    mRightViewIndex = getAdapter().getCount();
                }
            }

            Log.d("DBG", TAG + " removeNonVisibleItems() remove right view mRightViewIndex: " + mRightViewIndex);
            child = getChildAt(getChildCount() - 1);
            mRemoveRightViewTotal++;
        }
    }

    private void positionItems(final int dx) {
        int childCount = getChildCount();
        Log.d("DBG", TAG + " positionItems() childCount: " + childCount);
        if (childCount > 0) {
            mDisplayOffset += dx;
            int left = mDisplayOffset;
            Log.d("DBG", TAG + " positionItems() left: " + left);
            View child;
            int childWidth;
            for (int i = 0; i < childCount; i++) {
                child = getChildAt(i);
                childWidth = child.getMeasuredWidth();
                child.layout(left, 0, left + childWidth, child.getMeasuredHeight());
                left += childWidth + child.getPaddingRight();
            }
        }
    }

//    public synchronized void scrollTo(int x) {
//        Log.d("DBG", TAG + " scrollTo() x: " + x
//                + " mNextX: " + mNextX
//        );
//        mScroller.startScroll(mNextX, 0, x - mNextX, 0);
//        requestLayout();
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("DBG", TAG + " onKeyDown()"
                + " keyCode: " + keyCode
        );
        return commonKey(keyCode, 1, event);
    }

    private boolean commonKey(int keyCode, int count, KeyEvent event) {
        if (mAdapter == null) {
            return false;
        }
        if (mDataChanged) {
            layoutChildren();
        }

        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT: {

                keyCodeWhich = KeyEvent.KEYCODE_DPAD_LEFT;

                // 这是为了循环而修改的
                // 有时候，未到数据的第一个，但是已经mSelectedViewIndex - 1 < 0，所以需要加上
                // mLeftViewIndex != -1这个判断，mLeftViewIndex == -1 时候，已经是数据的第一个了
                if (mIsCycle || (mLeftViewIndex != -1 || (mSelectedViewIndex - 1) >= 0)) {
                    View leftChildAt = getChildAt(mSelectedViewIndex - 1);
                    boolean insideParent = isInsideParent(leftChildAt);
                    Log.d("DBG", TAG + " commonKey() left 前一个view是否在父控件内： " + insideParent);
                    if (insideParent) {
                        setSelection(mSelectedViewIndex - 1);
                        setSelectState();
                    } else {
                        synchronized (CopyOfHorizontalListView.this) {
                            if (leftChildAt == null) {
                                mNextX -= getChildAt(0).getMeasuredWidth();
                            } else {
                                mNextX -= leftChildAt.getMeasuredWidth();
                            }
                        }
                        Log.d("DBG", TAG + " commonKey() key left "
                                + " mNextX: " + mNextX
                        );
                        requestLayout();
                    }
                }
            }
                break;

            case KeyEvent.KEYCODE_DPAD_RIGHT: {
                Log.d("DBG", TAG + " commonKey() KEYCODE_DPAD_RIGHT"
//                        + " 第3个view的宽： " + getChildAt(2).getWidth()
//                        + " 第3个view的测量宽： " + getChildAt(2).getMeasuredWidth()
//                        + " 最后一个view的宽： " + getChildAt(getChildCount() - 1).getWidth()
                                + " 最后一个view的测量宽： " + getChildAt(getChildCount() - 1).getMeasuredWidth()
                                + " 最后一个view的左位置： " + getChildAt(getChildCount() - 1).getLeft()
                                + " 最后一个view的右位置： " + getChildAt(getChildCount() - 1).getRight()
                                + " 父控件的右边： " + getRight()
                );
                Log.d("DBG", TAG + " commonKey() KEYCODE_DPAD_RIGHT"
                        + " mSelectedViewIndex： " + mSelectedViewIndex
                        + " childCount： " + getChildCount()
                );

                keyCodeWhich = KeyEvent.KEYCODE_DPAD_RIGHT;

                // 这是为了循环而修改的
                // 有时候，未到数据的最后一个，但是已经(mSelectedViewIndex + 1) >= getChildCount()，所以需要加上
                // mRightViewIndex != mAdapter.getCount()这个判断，mRightViewIndex ＝= mAdapter.getCount() 时候，已经是数据的最后一个了
                if (mIsCycle || (((mSelectedViewIndex + 1) < getChildCount()) || (mRightViewIndex != mAdapter.getCount()))) {
                    View childAt;
                    childAt = getChildAt(mSelectedViewIndex + 1);
                    Log.d("DBG", TAG + " commonKey() right 是否在父控件内： " + isInsideParent(childAt));
                    if (isInsideParent(childAt)) {
                        setSelection(mSelectedViewIndex + 1);
                        setSelectState();
                    } else {
                        synchronized (CopyOfHorizontalListView.this) {
                            if (childAt == null) {
                                mNextX += getChildAt(getChildCount() - 1).getMeasuredWidth();
                            } else {
                                mNextX += childAt.getMeasuredWidth();
                            }
                        }
                        Log.d("DBG", TAG + " commonKey() key right "
                                + " mNextX: " + mNextX
                        );
                        requestLayout();

                    }
                }
            }
                break;

            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER: {
                LinearLayout selectedView = (LinearLayout) getSelectedView();
                TextView childAt = (TextView) selectedView.getChildAt(0);
                Toast.makeText(getContext(), "text: " + childAt.getText(), Toast.LENGTH_SHORT).show();
                Log.d("DBG", "comment key text: " + childAt.getText());
            }
                break;
        }

        return super.onKeyMultiple(keyCode, count, event);
    }

    private boolean isInsideParent(View child) {
        if (child == null) {
            return false;
        }

        if (mParentRect == null) {
            int[] parentPosition = new int[2];
            getLocationOnScreen(parentPosition);

            mParentRect = new Rect();
            mParentRect.set(getLeft(), parentPosition[1], getRight(), parentPosition[1] + getMeasuredHeight());
            Log.d("DBG", TAG + " isOutSideParent() " + " parent left: " + getLeft() + " parent top: " + getTop()
                    + " parent right: " + getRight() + " parent bottom: " + getBottom());
        }

        Rect viewRect = new Rect();
        int[] childPosition = new int[2];
        child.getLocationOnScreen(childPosition);
        int left = childPosition[0];
        int right = left + child.getWidth();
        int top = childPosition[1];
        int bottom = top + child.getHeight();
        viewRect.set(left, top, right, bottom);

        // return viewRect.contains((int) e.getRawX(), (int) e.getRawY());
        return mParentRect.contains(viewRect);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean handled = super.dispatchTouchEvent(ev);
        handled |= mGesture.onTouchEvent(ev);
        Log.d("DBG", TAG + " dispatchTouchEvent()"
                + " handled: " + handled
        );
        return handled;
    }

    protected boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d("DBG", TAG + " onFling() "
                + " velocityX: " + velocityX
                + " mNextX: " + mNextX
                + " mMaxX: " + mMaxX
        );
        synchronized (CopyOfHorizontalListView.this) {
            mScroller.fling(mNextX, 0, (int) -velocityX, 0, 0, mMaxX, 0, 0);
        }
        requestLayout();

        return true;
    }

    protected boolean onDown(MotionEvent e) {
        Log.d("DBG", TAG + " onDown() "
        );
        mScroller.forceFinished(true);
        return true;
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        Log.d("DBG", TAG + " onFocusChanged()... gainFocus: " + gainFocus);
        mGainFocus = gainFocus;
        refreshFocus(gainFocus);
    }

    private void refreshFocus(boolean gainFocus) {
        if (mOldSelectedView != null) {
            if (gainFocus) {
//				mOldSelectedView.setBackgroundResource(R.color.orange);

//                if (mCustomSelectedListener != null) {
//                    int position = mLeftViewIndex + 1 + mSelectedViewIndex;
//                    if (position > (mAdapter.getCount() - 1)) {
//                        position = position - mAdapter.getCount();
//                    }
//                    mCustomSelectedListener.onCustomItemSelected(CustomHorizontalView.this, mOldSelectedView, position,
//                            mAdapter.getItemId(position), true);
//                }
            } else {
//				mOldSelectedView.setBackgroundResource(R.color.edit_bg);

//                if (mCustomSelectedListener != null) {
//                    int position = mLeftViewIndex + 1 + mSelectedViewIndex;
//                    if (position > (mAdapter.getCount() - 1)) {
//                        position = position - mAdapter.getCount();
//                    }
//                    mCustomSelectedListener.onCustomItemSelected(CustomHorizontalView.this, mOldSelectedView, position,
//                            mAdapter.getItemId(position), false);
//                }
            }
        } else {
            if (gainFocus) {
                setSelectState();
            }
        }
    }


}