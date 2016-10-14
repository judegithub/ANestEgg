package com.keeplearning.anestegg.widget;

import java.util.LinkedList;
import java.util.Queue;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.Scroller;

/**
 * 横向，可循环的View，仅用于TV上。未带回收功能，因为没成功
 */
public class CustomHorizontalListView extends ViewGroup {

	private final String TAG = "PlayRecommendView";

	private boolean mDataChanged = false;
	/**
	 * define whether is can cycle
	 */
	private boolean mIsCycle = false;

	/**
	 * 选中的view所在的位置
	 */
	private int mSelectedViewIndex = 0;
	private int mRealSelectedViewIndex = 0;
	private int mHorizontalSpacing = 10;
	private int mItemCount = 0;
	private int mOriginalChildCount = 0; 

	private ListAdapter mAdapter;
	private Scroller mScroller;
	private Rect mParentRect;

	private Queue<View> mRemovedViewQueue = new LinkedList<View>();
	private OnCustomItemClickListener mOnCustomItemClickListener;

	public CustomHorizontalListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public CustomHorizontalListView(Context context) {
		super(context);
		init();
	}

	public CustomHorizontalListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private synchronized void init() {
		setClickable(true);
		setFocusable(true);
		setFocusableInTouchMode(true);
		setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);

		mScroller = new Scroller(getContext());
	}

	private DataSetObserver mDataObserver = new DataSetObserver() {

		@Override
		public void onChanged() {
			synchronized (CustomHorizontalListView.this) {
				mDataChanged = true;
			}
			invalidate();
			requestLayout();
		}

		@Override
		public void onInvalidated() {
			synchronized (CustomHorizontalListView.this) {
				mDataChanged = true;
			}
			invalidate();
			requestLayout();
		}
	};

	public View getSelectedView() {
		return getChildAt(getSelectedViewIndex());
	}

	public int getSelectedViewIndex() {
		return mRealSelectedViewIndex;
	}

	public void setHorizontalSpaceing(int spacing) {
		mHorizontalSpacing = spacing;
	}

	public void setAdapter(ListAdapter adapter) {
		// scroll to original location
		mScroller.abortAnimation();
		mScroller.startScroll(mScroller.getCurrX(), 0, -mScroller.getCurrX(), 0);

		mOriginalChildCount = 0;
		mItemCount = 0;
		if (mAdapter != null) {
			mAdapter.unregisterDataSetObserver(mDataObserver);
		}
		mAdapter = adapter;
		if (mAdapter != null) {
			mItemCount = mAdapter.getCount();
			mAdapter.registerDataSetObserver(mDataObserver);
		}
	}
	
	private void addAndMeasureChild(final View child, int viewPos) {
		LayoutParams params = (LayoutParams) child.getLayoutParams();
		if (params == null) {
			params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		}

		addViewInLayout(child, viewPos, params, true);
		child.measure(MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.AT_MOST),
				MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.AT_MOST));
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
		setSelectedState(gainFocus);
	}

	private void setSelectedState(boolean isSelected) {
		// child view should implements OnCustomSelectedListener
		OnCustomSelectedListener listener = (OnCustomSelectedListener) getSelectedView();
		if (listener == null) {
			return;
		}
		listener.onSelect(isSelected);
	}

	public void setSelectedViewIndex(int index) {
		mSelectedViewIndex = index;
	}

	public void setIsCycle(boolean isCycle) {
		mIsCycle = isCycle;
	}

	public void setOnCustomItemClickListener(OnCustomItemClickListener listener) {
		mOnCustomItemClickListener = listener;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return commonKey(keyCode, 1, event);
	}
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private boolean commonKey(int keyCode, int count, KeyEvent event) {
		if (mItemCount == 0) {
			return false;
		}

		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT: {
			int previousViewIndex = mSelectedViewIndex - 1;
			if (previousViewIndex >= 0 || mIsCycle) {
				mScroller.abortAnimation();

				// for cycle
				if (previousViewIndex < 0) {
					previousViewIndex = mItemCount - 1;
					mSelectedViewIndex = mItemCount;
				}

				View selectedView = getSelectedView();
				int realPreviousIndex = mRealSelectedViewIndex - 1;
				View previousChild = getChildAt(realPreviousIndex);
				if (previousChild == null) {
					// add child
					previousChild = mAdapter.getView(previousViewIndex, mRemovedViewQueue.poll(), this);
					addAndMeasureChild(previousChild, 0);

					int previoudsViewRight = selectedView.getLeft() - mHorizontalSpacing;
					int previoudsViewLeft = previoudsViewRight - previousChild.getMeasuredWidth();

					previousChild.layout(previoudsViewLeft, 0, previoudsViewRight, previousChild.getMeasuredHeight());
					mRealSelectedViewIndex += 1;
				}

				boolean isPreviousViewInsideParent = isInsideParent(previousChild);
				if (!isPreviousViewInsideParent) {
					mScroller.startScroll(mScroller.getCurrX(), 0, -(previousChild.getMeasuredWidth() + mHorizontalSpacing), 0);

					// remove last view
//					mRemovedViewQueue.offer(getChildAt(getChildCount() - 1));
					removeViewInLayout(getChildAt(getChildCount() - 1));
				}

				setSelectedState(false);
				mSelectedViewIndex -= 1;
				if (mRealSelectedViewIndex > 0) {
					mRealSelectedViewIndex -= 1;
				} else {
					mRealSelectedViewIndex = 0;
				}
				setSelectedState(true);
			}

			int childCount = getChildCount();
			// keep same view size
			if (childCount < mOriginalChildCount) {
				int addViewIndex = mSelectedViewIndex;
				int addViewRight = getSelectedView().getLeft();
				int addViewLeft = 0;
				View child = null;

				for (int i = childCount; i < mOriginalChildCount && (addViewIndex < (mItemCount - 1) || mIsCycle); i++) {
					addViewIndex++;
					// for cycle
					if (addViewIndex >= mItemCount) {
						addViewIndex = 0;
					}

					child = mAdapter.getView(addViewIndex, mRemovedViewQueue.poll(), this);
					addAndMeasureChild(child, -1);

					addViewRight = addViewRight - mHorizontalSpacing;
					addViewLeft = addViewRight - child.getMeasuredWidth();
					child.layout(addViewLeft, 0, addViewRight, child.getMeasuredHeight());

					addViewRight = addViewLeft;
				}
			}
		}
			return true;

		case KeyEvent.KEYCODE_DPAD_RIGHT: {
			int nextViewIndex = mSelectedViewIndex + 1;

			if (nextViewIndex < mItemCount || mIsCycle) {
				mScroller.abortAnimation();

				// for cycle
				if (nextViewIndex >= mItemCount) {
					nextViewIndex = 0;
					mSelectedViewIndex = -1;
				}

				View selectedView = getSelectedView();

				int realNextIndex = mRealSelectedViewIndex + 1;
				View nextChild = getChildAt(realNextIndex);
				if (nextChild == null) {
					// 未添加下一个view，那么添加以下一个view
					nextChild = mAdapter.getView(nextViewIndex, mRemovedViewQueue.poll(), this);
					addAndMeasureChild(nextChild, -1);

					int nextChildLeft = selectedView.getRight() + mHorizontalSpacing;
					int nextChildRight = nextChildLeft + nextChild.getMeasuredWidth();
					nextChild.layout(nextChildLeft, 0, nextChildRight, nextChild.getMeasuredHeight());
				}

				boolean isNextChildInsideParent = isInsideParent(nextChild);
				if (!isNextChildInsideParent) {
					mScroller.startScroll(mScroller.getCurrX(), 0, mHorizontalSpacing + nextChild.getWidth(), 0);

					// remove first view
//					mRemovedViewQueue.offer(getChildAt(0));
					removeViewInLayout(getChildAt(0));
					mRealSelectedViewIndex -= 1;
				}

				setSelectedState(false);
				mSelectedViewIndex += 1;
				mRealSelectedViewIndex += 1;
				setSelectedState(true);

				int childCount = getChildCount();
				// keep same view size
				if (childCount < mOriginalChildCount) {
					int addViewIndex = mSelectedViewIndex;
					int addViewLeft = getSelectedView().getRight();
					int addViewRight = 0;
					View child = null;

					for (int i = childCount; i < mOriginalChildCount && (addViewIndex < (mItemCount - 1) || mIsCycle); i++) {
						addViewIndex++;
						// for cycle
						if (addViewIndex >= mItemCount) {
							addViewIndex = 0;
						}

						child = mAdapter.getView(addViewIndex, mRemovedViewQueue.poll(), this);
						addAndMeasureChild(child, -1);

						addViewLeft = addViewLeft + mHorizontalSpacing;
						addViewRight = addViewLeft + child.getMeasuredWidth();
						child.layout(addViewLeft, 0, addViewRight, child.getMeasuredHeight());

						addViewLeft = addViewRight;
					}
				}
			}
		}
			return true;

		case KeyEvent.KEYCODE_DPAD_CENTER:
		case KeyEvent.KEYCODE_ENTER: {
			if (mOnCustomItemClickListener != null) {
				View selectedView = getSelectedView();
				int id = 0;
				if (selectedView != null) {
					id = selectedView.getId();
				}
				mOnCustomItemClickListener.onItemClick(this, getSelectedView(), mSelectedViewIndex, id);
			}
		}
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void computeScroll() {
		// 先判断mScroller滚动是否完成
		if (mScroller.computeScrollOffset()) {
			// 这里调用View的scrollTo()完成实际的滚动
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			// 必须调用该方法，否则不一定能看到滚动效果
			postInvalidate();
		}
		super.computeScroll();
	}

	@Override
	protected synchronized void onLayout(boolean changed, int left, int top, int right, int bottom) {
		if (mDataChanged) {
			mDataChanged = false;
			layoutChildren();
		}
	}

	private void layoutChildren() {
		removeAllViewsInLayout();

		if (mItemCount == 0) {
			return;
		}

		// reset
		mRealSelectedViewIndex = 0;

		if (mSelectedViewIndex < 0 || mSelectedViewIndex >= mItemCount) {
			Log.w(TAG, " layoutChildren() selectedViewIndex error...");
			return;
		}

		int parentWidth = getMeasuredWidth();
		int nextViewLeft = 0;
		View child;

		for (int i = mSelectedViewIndex; ((i < mItemCount) || mIsCycle) && nextViewLeft < parentWidth; i++) {

			// for cycle
			if (i > (mItemCount - 1)) {
				i = 0;
			}

			child = mAdapter.getView(i, mRemovedViewQueue.poll(), this);
			addAndMeasureChild(child, -1);

			nextViewLeft += child.getMeasuredWidth() + mHorizontalSpacing;
		}

		mOriginalChildCount = getChildCount();

		positionItems();
	}

	private void positionItems() {
		int childCount = getChildCount();
		if (childCount > 0) {
			int left = 0;
			int right = 0;
			View child;
			for (int i = 0; i < childCount; i++) {
				child = getChildAt(i);

				right = left + mHorizontalSpacing + child.getMeasuredWidth();
				child.layout(left, 0, right, child.getMeasuredHeight());
				// left += childWidth + child.getPaddingRight();
				left = right;
			}
		}
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
	
	public interface OnCustomSelectedListener {
		public abstract void onSelect(boolean isSelected);
	}

	public interface OnCustomItemClickListener {
		void onItemClick(ViewGroup parent, View view, int position, long id);
	}

}
