package com.keeplearning.anestegg.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.keeplearning.anestegg.R;
import com.keeplearning.anestegg.adapter.TestRecyclerViewAdapter;
import com.keeplearning.anestegg.decoration.DividerGridItemDecoration;
import com.keeplearning.anestegg.decoration.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class TestRecyclerViewActivity extends BaseActivity {

    private RecyclerView mRecyclerView;
    private TestRecyclerViewAdapter mAdapter;

    private List<String> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_recycler_view);

        initData();
        
        setupViews();
    }

    private void initData() {
        mData = new ArrayList<String>();
        for (int i = 'A'; i < 'z'; i++) {
            mData.add("" + (char) i);
        }
    }

    private void setupViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.test_recycler_view);

//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
//                DividerItemDecoration.VERTICAL_LIST));

//        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
//        mRecyclerView.addItemDecoration(new DividerGridItemDecoration(this));

        // StaggeredGridLayoutManager构造的第二个参数传一个orientation，如果传入的是StaggeredGridLayoutManager.VERTICAL代表有多少列；那么传入的如果是StaggeredGridLayoutManager.HORIZONTAL就代表有多少行
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));

        mAdapter = new TestRecyclerViewAdapter(TestRecyclerViewActivity.this);
        mAdapter.setData(mData);
        mRecyclerView.setAdapter(mAdapter);

    }
}
