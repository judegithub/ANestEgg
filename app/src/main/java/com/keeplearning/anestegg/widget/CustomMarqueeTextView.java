package com.keeplearning.anestegg.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 设置setIsFocused true 即使无焦点也能marquee的TextView
 */
public class CustomMarqueeTextView extends TextView {

	private boolean mIsFocused = true;

	public CustomMarqueeTextView(Context context) {
		super(context);
	}

	public CustomMarqueeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomMarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean isFocused() {
		return mIsFocused;
	}

	public void setIsFocused(boolean isFocused) {
		mIsFocused = isFocused;
		setSelected(isFocused);
	}
}
