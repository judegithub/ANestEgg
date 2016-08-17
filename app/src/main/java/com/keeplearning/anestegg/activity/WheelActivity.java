package com.keeplearning.anestegg.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.keeplearning.anestegg.R;
import com.keeplearning.anestegg.widget.MyViewGroup;

public class WheelActivity extends BaseActivity {
	private final String TAG = "WheelActivity";

	private MyViewGroup myViewGroup;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wheel);
		myViewGroup = (MyViewGroup) findViewById(R.id.myviewGroup);

	}

	public void scroll(View v) {
		myViewGroup.beginScroll();
	}

}