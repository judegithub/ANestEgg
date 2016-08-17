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

public class CustomViewLikeAdapterView extends AdapterView<ListAdapter> {

    private final String TAG = "CustomViewLikeAdapterView";

    /**
     * When arrow scrolling, ListView will never scroll more than this factor
     * times the height of the list.
     */
    private static final float MAX_SCROLL_FACTOR = 0.33f;

    private int mMaxX = Integer.MAX_VALUE;
    private int mDisplayOffset = 0;
    private int mLeftViewIndex = INVALID_POSITION;
    private int mRightViewIndex = 0;
    private int mSelectedPosition = INVALID_POSITION;
    private int mFirstPosition = 0;

    protected int mCurrentX;
    protected int mNextX;

    private boolean mDataChanged = false;
    private boolean mAreAllItemsSelectable = true;

    private Queue<View> mRemovedViewQueue = new LinkedList<View>();

    protected Scroller mScroller;

    protected ListAdapter mAdapter;

    private DataSetObserver mDataObserver = new DataSetObserver() {

        @Override
        public void onChanged() {
            Log.d("DBG", TAG + " onChanged()....");
            synchronized (CustomViewLikeAdapterView.this) {
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

    public CustomViewLikeAdapterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public CustomViewLikeAdapterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomViewLikeAdapterView(Context context) {
        super(context);
        init();
    }

    private void init() {
        mScroller = new Scroller(getContext());
    }

    private void reset() {
        mSelectedPosition = INVALID_POSITION;
        mFirstPosition = 0;
    }

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

    @Override
    public ListAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public View getSelectedView() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setSelection(int position) {
        // TODO Auto-generated method stub
    }

    /**
     * @return The maximum amount a list view will scroll in response to
     *   an arrow event.
     */
    public int getMaxScrollAmount() {
//        return (int) (MAX_SCROLL_FACTOR * (mBottom - mTop));
        return (int) (MAX_SCROLL_FACTOR * (getRight() - getLeft()));
    }

    @Override
    protected synchronized void onLayout(boolean changed, int left, int top, int right,
            int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        layoutChildren();

        Log.d("DBG", TAG + " onLayout()" + " changed: " + changed + " left: " + left + " top: "
                + top + " right: "
                + right + " bottom: " + bottom);
    }

    private void layoutChildren() {
        if (mAdapter == null) {
            return;
        }

        Log.d("DBG", TAG + " onLayout() mDataChanged: " + mDataChanged);
        if (mDataChanged) {
            Log.d("DBG", TAG + " onLayout() mCurrentX: " + mCurrentX);
            int oldCurrentX = mCurrentX;
            init();
            removeAllViewsInLayout();
            mNextX = oldCurrentX;
            mDataChanged = false;
        }

        if (mScroller.computeScrollOffset()) {
            int scrollx = mScroller.getCurrX();
            mNextX = scrollx;
        }

        if (mNextX <= 0) {
            mNextX = 0;
            mScroller.forceFinished(true);
        } else if (mNextX >= mMaxX) {
            mNextX = mMaxX;
            mScroller.forceFinished(true);
        }

        int dx = mCurrentX - mNextX;
        Log.d("DBG", TAG + " layoutChildren() dx: " + dx);
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
    }

    private void removeNonVisibleItems(final int dx) {
        View child = getChildAt(0);
        while (child != null && child.getRight() + dx <= 0) {
            mDisplayOffset += child.getMeasuredWidth();
            mRemovedViewQueue.offer(child);
            removeViewInLayout(child);
            mLeftViewIndex++;
            child = getChildAt(0);
        }

        child = getChildAt(getChildCount() - 1);
        while (child != null && child.getLeft() + dx >= getWidth()) {
            mRemovedViewQueue.offer(child);
            removeViewInLayout(child);
            mRightViewIndex--;
            child = getChildAt(getChildCount() - 1);
        }
    }

    private void fillList(final int dx) {
        Log.d("DBG", TAG + " fillList() dx: " + dx);
        int edge = 0;
        View child = getChildAt(getChildCount() - 1);
        Log.d("DBG", TAG + " fillList() first child: " + child);
        if (child != null) {
            edge = child.getRight();
        }
        fillListRight(edge, dx);

        edge = 0;
        child = getChildAt(0);
        Log.d("DBG", TAG + " fillList() second child: " + child);
        if (child != null) {
            edge = child.getLeft();
        }
        fillListLeft(edge, dx);
    }

    private void fillListRight(int rightEdge, final int dx) {
        Log.d("DBG", TAG + " fillListRight() rightEdge: " + rightEdge
            + " mRightViewIndex: " + mRightViewIndex
        );
        while (rightEdge + dx < getWidth() && mRightViewIndex < mAdapter.getCount()) {
            Log.d("DBG", TAG + " fillListLeft() "
                    + " mRightViewIndex: " + mRightViewIndex
            );
            View child = mAdapter.getView(mRightViewIndex, mRemovedViewQueue.poll(), this);
            addAndMeasureChild(child, -1);
            rightEdge += child.getMeasuredWidth();

            if (mRightViewIndex == mAdapter.getCount() - 1) {
                mMaxX = mCurrentX + rightEdge - getWidth();
            }

            if (mMaxX < 0) {
                mMaxX = 0;
            }
            mRightViewIndex++;
        }
    }

    private void fillListLeft(int leftEdge, final int dx) {
        Log.d("DBG", TAG + " fillListLeft() "
                + " leftEdge: " + leftEdge
                + " mLeftViewIndex: " + mLeftViewIndex
        );
        while (leftEdge + dx > 0 && mLeftViewIndex >= 0) {
            Log.d("DBG", TAG + " fillListLeft() " +
                    " mLeftViewIndex: " + mLeftViewIndex
            );
            View child = mAdapter.getView(mLeftViewIndex, mRemovedViewQueue.poll(), this);
            addAndMeasureChild(child, 0);
            leftEdge -= child.getMeasuredWidth();
            mLeftViewIndex--;
            mDisplayOffset -= child.getMeasuredWidth();
        }
    }

    private void positionItems(final int dx) {
        int childCount = getChildCount();
        if (childCount > 0) {
            mDisplayOffset += dx;
            int left = mDisplayOffset;
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

    private void addAndMeasureChild(final View child, int viewPos) {
        LayoutParams params = child.getLayoutParams();
        if (params == null) {
            params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        }

        addViewInLayout(child, viewPos, params, true);
        child.measure(MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.AT_MOST));
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        Log.d("DBG", TAG + " onKeyDown() keyCode: " + keyCode);
//        return commonKey(keyCode, 1, event);
//    }

//    private boolean commonKey(int keyCode, int count, KeyEvent event) {
//        if (mAdapter == null) {
//            return false;
//        }
//        if (mDataChanged) {
//            layoutChildren();
//        }
//        boolean handled = false;
//        switch (keyCode) {
//            case KeyEvent.KEYCODE_DPAD_LEFT:
//                while (count-- > 0) {
//                    if (arrowScroll(FOCUS_LEFT)) {
//                        handled = true;
//                    } else {
//                        break;
//                    }
//                }
//                break;
//        }
//
//        if (handled) {
//            return true;
//        }
//
//        int action = event.getAction();
//        switch (action) {
//            case KeyEvent.ACTION_DOWN:
//                return super.onKeyDown(keyCode, event);
//            default: // shouldn't happen
//                return false;
//        }
//    }

//    /**
//     * Scrolls to the next or previous item if possible.
//     *
//     * @param direction either {@link View#FOCUS_LEFT} or {@link View#FOCUS_RIGHT}
//     *
//     * @return whether selection was moved
//     */
//    boolean arrowScroll(int direction) {
//        try {
//            mInLayout = true;
//            final boolean handled = arrowScrollImpl(direction);
//            return handled;
//        } finally {
//            mInLayout = false;
//        }
//    }

//    /**
//     * Handle an arrow scroll going up or down.  Take into account whether items are selectable,
//     * whether there are focusable items etc.
//     *
//     * @param direction Either {@link View#FOCUS_LEFT} or {@link View#FOCUS_RIGHT}.
//     * @return Whether any scrolling, selection or focus change occured.
//     */
//    private boolean arrowScrollImpl(int direction) {
//        if (getChildCount() <= 0) {
//            return false;
//        }
//        View selectedView = getSelectedView();
//        int selectedPos = mSelectedPosition;
//        int nextSelectedPosition = nextSelectedPositionForDirection(selectedView, selectedPos, direction);
//        int amountToScroll = amountToScroll(direction, nextSelectedPosition);
//        // if we are moving focus, we may OVERRIDE the default behavior
//        final ArrowScrollFocusResult focusResult = mItemsCanFocus ? arrowScrollFocused(direction) : null;
//        if (focusResult != null) {
//            nextSelectedPosition = focusResult.getSelectedPosition();
//            amountToScroll = focusResult.getAmountToScroll();
//        }
//        boolean needToRedraw = focusResult != null;
//        if (nextSelectedPosition != INVALID_POSITION) {
//            handleNewSelectionChange(selectedView, direction, nextSelectedPosition, focusResult != null);
//            setSelectedPositionInt(nextSelectedPosition);
//            setNextSelectedPositionInt(nextSelectedPosition);
//            selectedView = getSelectedView();
//            selectedPos = nextSelectedPosition;
//            if (mItemsCanFocus && focusResult == null) {
//                // there was no new view found to take focus, make sure we
//                // don't leave focus with the old selection
//                final View focused = getFocusedChild();
//                if (focused != null) {
//                    focused.clearFocus();
//                }
//            }
//            needToRedraw = true;
//            checkSelectionChanged();
//        }
//        if (amountToScroll > 0) {
//            scrollListItemsBy((direction == View.FOCUS_UP) ? amountToScroll : -amountToScroll);
//            needToRedraw = true;
//        }
//        // if we didn't find a new focusable, make sure any existing focused
//        // item that was panned off screen gives up focus.
//        if (mItemsCanFocus && (focusResult == null)
//                && selectedView != null && selectedView.hasFocus()) {
//            final View focused = selectedView.findFocus();
//            if (focused != null) {
//                if (!isViewAncestorOf(focused, this) || distanceToView(focused) > 0) {
//                    focused.clearFocus();
//                }
//            }
//        }
//        // if  the current selection is panned off, we need to remove the selection
//        if (nextSelectedPosition == INVALID_POSITION && selectedView != null
//                && !isViewAncestorOf(selectedView, this)) {
//            selectedView = null;
//            hideSelector();
//            // but we don't want to set the ressurect position (that would make subsequent
//            // unhandled key events bring back the item we just scrolled off!)
//            mResurrectToPosition = INVALID_POSITION;
//        }
//        if (needToRedraw) {
//            if (selectedView != null) {
//                positionSelectorLikeFocus(selectedPos, selectedView);
//                mSelectedTop = selectedView.getTop();
//            }
//            if (!awakenScrollBars()) {
//                invalidate();
//            }
//            invokeOnItemScrollListener();
//            return true;
//        }
//        return false;
//    }

//    /**
//     * Used by {@link #arrowScrollImpl(int)} to help determine the next selected position
//     * to move to. This return a position in the direction given if the selected item
//     * is fully visible.
//     *
//     * @param selectedView Current selected view to move from
//     * @param selectedPos Current selected position to move from
//     * @param direction Direction to move in
//     * @return Desired selected position after moving in the given direction
//     */
//    private final int nextSelectedPositionForDirection(
//            View selectedView, int selectedPos, int direction) {
//        int nextSelected;
//        if (direction == View.FOCUS_RIGHT) {
////            final int listBottom = getHeight() - mListPadding.bottom;
////            if (selectedView != null && selectedView.getBottom() <= listBottom) {
////                nextSelected = selectedPos != INVALID_POSITION && selectedPos >= mFirstPosition ?
////                        selectedPos + 1 :
////                        mFirstPosition;
////            } else {
////                return INVALID_POSITION;
////            }
//            // TODO 注意，还需要减去父控件和子控件的padding
//            final int listRight = getRight();
//            if (selectedView != null && selectedView.getRight() <= listRight) {
//                nextSelected = selectedPos != INVALID_POSITION && selectedPos >= mFirstPosition ?
//                        selectedPos + 1 :
//                        mFirstPosition;
//            } else {
//                return INVALID_POSITION;
//            }
//        } else { // left
////            final int listTop = mListPadding.top;
////            if (selectedView != null && selectedView.getTop() >= listTop) {
////                final int lastPos = mFirstPosition + getChildCount() - 1;
////                nextSelected = selectedPos != INVALID_POSITION && selectedPos <= lastPos ?
////                        selectedPos - 1 :
////                        lastPos;
////            } else {
////                return INVALID_POSITION;
////            }
//            final int listLeft = getLeft();
//            if (selectedView != null && selectedView.getLeft() >= listLeft) {
//                final int lastPos = mFirstPosition + getChildCount() - 1;
//                nextSelected = selectedPos != INVALID_POSITION && selectedPos <= lastPos ?
//                        selectedPos - 1 :
//                        lastPos;
//            } else {
//                return INVALID_POSITION;
//            }
//        }
//        if (nextSelected < 0 || nextSelected >= mAdapter.getCount()) {
//            return INVALID_POSITION;
//        }
//        return lookForSelectablePosition(nextSelected, direction == View.FOCUS_DOWN);
//    }

//    /**
//     * Find a position that can be selected (i.e., is not a separator).
//     *
//     * @param position The starting position to look at.
//     * @param lookRight Whether to look right for other positions.
//     * @return The next selectable position starting at position and then searching either left or
//     *         right. Returns {@link #INVALID_POSITION} if nothing can be found.
//     */
//    int lookForSelectablePosition(int position, boolean lookRight) {
//        final ListAdapter adapter = mAdapter;
//        if (adapter == null || isInTouchMode()) {
//            return INVALID_POSITION;
//        }
//        final int count = adapter.getCount();
//        if (!mAreAllItemsSelectable) {
//            if (lookRight) {
//                position = Math.max(0, position);
//                while (position < count && !adapter.isEnabled(position)) {
//                    position++;
//                }
//            } else {
//                position = Math.min(position, count - 1);
//                while (position >= 0 && !adapter.isEnabled(position)) {
//                    position--;
//                }
//            }
//        }
//        if (position < 0 || position >= count) {
//            return INVALID_POSITION;
//        }
//        return position;
//    }

//    /**
//     * Determine how much we need to scroll in order to get the next selected view
//     * visible, with a fading edge showing below as applicable.  The amount is
//     * capped at {@link #getMaxScrollAmount()} .
//     *
//     * @param direction either {@link View#FOCUS_LEFT} or
//     *        {@link View#FOCUS_RIGHT}.
//     * @param nextSelectedPosition The position of the next selection, or
//     *        {@link #INVALID_POSITION} if there is no next selectable position
//     * @return The amount to scroll. Note: this is always positive!  Direction
//     *         needs to be taken into account when actually scrolling.
//     */
//    private int amountToScroll(int direction, int nextSelectedPosition) {
////        final int listBottom = getHeight() - mListPadding.bottom;
////        final int listTop = mListPadding.top;
//        // TODO 注意要减去各种padding
//        final int listRight = getRight();
//        final int listLeft = getLeft();
//        int numChildren = getChildCount();
////        if (direction == View.FOCUS_DOWN) {
//        if (direction == View.FOCUS_RIGHT) {
//            int indexToMakeVisible = numChildren - 1;
//            if (nextSelectedPosition != INVALID_POSITION) {
//                indexToMakeVisible = nextSelectedPosition - mFirstPosition;
//            }
//            while (numChildren <= indexToMakeVisible) {
//                // Child to view is not attached yet.
//                addViewBelow(getChildAt(numChildren - 1), mFirstPosition + numChildren - 1);
//                numChildren++;
//            }
//            final int positionToMakeVisible = mFirstPosition + indexToMakeVisible;
//            final View viewToMakeVisible = getChildAt(indexToMakeVisible);
//            int goalBottom = listBottom;
//            if (positionToMakeVisible < mItemCount - 1) {
//                goalBottom -= getArrowScrollPreviewLength();
//            }
//            if (viewToMakeVisible.getBottom() <= goalBottom) {
//                // item is fully visible.
//                return 0;
//            }
//            if (nextSelectedPosition != INVALID_POSITION
//                    && (goalBottom - viewToMakeVisible.getTop()) >= getMaxScrollAmount()) {
//                // item already has enough of it visible, changing selection is good enough
//                return 0;
//            }
//            int amountToScroll = (viewToMakeVisible.getBottom() - goalBottom);
//            if ((mFirstPosition + numChildren) == mItemCount) {
//                // last is last in list -> make sure we don't scroll past it
//                final int max = getChildAt(numChildren - 1).getBottom() - listBottom;
//                amountToScroll = Math.min(amountToScroll, max);
//            }
//            return Math.min(amountToScroll, getMaxScrollAmount());
//        } else {
//            int indexToMakeVisible = 0;
//            if (nextSelectedPosition != INVALID_POSITION) {
//                indexToMakeVisible = nextSelectedPosition - mFirstPosition;
//            }
//            while (indexToMakeVisible < 0) {
//                // Child to view is not attached yet.
//                addViewAbove(getChildAt(0), mFirstPosition);
//                mFirstPosition--;
//                indexToMakeVisible = nextSelectedPosition - mFirstPosition;
//            }
//            final int positionToMakeVisible = mFirstPosition + indexToMakeVisible;
//            final View viewToMakeVisible = getChildAt(indexToMakeVisible);
//            int goalTop = listTop;
//            if (positionToMakeVisible > 0) {
//                goalTop += getArrowScrollPreviewLength();
//            }
//            if (viewToMakeVisible.getTop() >= goalTop) {
//                // item is fully visible.
//                return 0;
//            }
//            if (nextSelectedPosition != INVALID_POSITION &&
//                    (viewToMakeVisible.getBottom() - goalTop) >= getMaxScrollAmount()) {
//                // item already has enough of it visible, changing selection is good enough
//                return 0;
//            }
//            int amountToScroll = (goalTop - viewToMakeVisible.getTop());
//            if (mFirstPosition == 0) {
//                // first is first in list -> make sure we don't scroll past it
//                final int max = listTop - getChildAt(0).getTop();
//                amountToScroll = Math.min(amountToScroll,  max);
//            }
//            return Math.min(amountToScroll, getMaxScrollAmount());
//        }
//    }

}
