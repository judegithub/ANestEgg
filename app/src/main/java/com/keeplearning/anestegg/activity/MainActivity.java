package com.keeplearning.anestegg.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.keeplearning.anestegg.R;
import com.keeplearning.anestegg.adapter.HorizontalListViewAdapter;
import com.keeplearning.anestegg.widget.CopyOfHorizontalListView;
import com.keeplearning.anestegg.widget.CustomViewLikeAdapterView;
import com.keeplearning.anestegg.widget.HorizontalListView;
import com.keeplearning.anestegg.widget.TestLikeAdapterView;

public class MainActivity extends BaseActivity {

    private final String TAG = "MainActivity";

    private CustomViewLikeAdapterView mCustomLikeAdapterView;
    private HorizontalListViewAdapter mHlva;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupViews();
    }

    private void setupViews() {
//        mCustomLikeAdapterView = (CustomViewLikeAdapterView) findViewById(
//                R.id.main_custom_like_adapter_view);
//        mHlva = new HorizontalListViewAdapter(this);
//        mCustomLikeAdapterView.setAdapter(mHlva);
//        mHlva.notifyDataSetChanged();

//        TestLikeAdapterView mTestLikeAdapterView = (TestLikeAdapterView) findViewById(R.id.main_test_like_adapter_view);
//        mTestLikeAdapterView.setSelection(8);
//        mHlva = new HorizontalListViewAdapter(this);
//        mTestLikeAdapterView.setAdapter(mHlva);

//        HorizontalListView mHorizontalListView = (HorizontalListView) findViewById(
//                R.id.main_horizontal_view);
        CopyOfHorizontalListView mHorizontalListView = (CopyOfHorizontalListView) findViewById(
                R.id.main_horizontal_view);
        mHlva = new HorizontalListViewAdapter(this);
        mHorizontalListView.setAdapter(mHlva);
        mHlva.notifyDataSetChanged();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("DBG", TAG + " onKeyDown() keyCode: " + keyCode);
        return super.onKeyDown(keyCode, event);
    }
}
