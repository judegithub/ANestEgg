package com.keeplearning.anestegg.activity;

import android.os.Bundle;

import com.keeplearning.anestegg.R;
import com.keeplearning.anestegg.adapter.HorizontalListViewAdapter;
import com.keeplearning.anestegg.widget.CopyOfHorizontalListView;
import com.keeplearning.anestegg.widget.HorizontalListView;

public class TestHorizontalListViewActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_horizontal_list_view);

        setupViews();
    }

    private void setupViews() {
//        HorizontalListView horizontalListView = (HorizontalListView) findViewById(
//                R.id.test_horizontal_view);
        CopyOfHorizontalListView horizontalListView = (CopyOfHorizontalListView) findViewById(
                R.id.test_copy_of_horizontal_view);
        HorizontalListViewAdapter adapter = new HorizontalListViewAdapter(this);
        horizontalListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

}
