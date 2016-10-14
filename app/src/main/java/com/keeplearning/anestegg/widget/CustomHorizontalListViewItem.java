package com.keeplearning.anestegg.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.keeplearning.anestegg.R;


public class CustomHorizontalListViewItem extends LinearLayout implements CustomHorizontalListView.OnCustomSelectedListener {

	private CustomMarqueeTextView mTextView;

	public CustomHorizontalListViewItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CustomHorizontalListViewItem(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		setOrientation(VERTICAL);
		LayoutInflater.from(context).inflate(R.layout.item_custom_horizontal_list_view, this, true);

		mTextView = (CustomMarqueeTextView) findViewById(R.id.item_text_view);
		mTextView.setIsFocused(false);
	}

	public void setName(String name) {
		mTextView.setText(name);
	}

	@Override
	public void onSelect(boolean isSelected) {
		mTextView.setIsFocused(isSelected);

		if (isSelected) {
			mTextView.setTextColor(Color.YELLOW);
		} else {
			mTextView.setTextColor(Color.BLACK);
		}
	}

}
