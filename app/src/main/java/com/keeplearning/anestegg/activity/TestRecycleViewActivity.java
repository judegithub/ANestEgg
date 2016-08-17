package com.keeplearning.anestegg.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.keeplearning.anestegg.R;
import com.keeplearning.anestegg.adapter.RecycleAdapter;
import com.keeplearning.anestegg.widget.DividerGridItemDecoration;
import com.keeplearning.anestegg.widget.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class TestRecycleViewActivity extends AppCompatActivity {

    private List<String> mDatas;

    private RecycleAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_recycle_view);

        setupViews();
    }

    private void setupViews() {
        mDatas = new ArrayList<String>();
        for (int i = 'A'; i < 'z'; i++)
        {
            mDatas.add("" + (char) i);
        }

        mAdapter = new RecycleAdapter(getBaseContext());
        mAdapter.setData(mDatas);

        RecyclerView mRecycleView = (RecyclerView) findViewById(R.id.test_recycle_view);
        mRecycleView.setFocusable(true);
        mRecycleView.setFocusableInTouchMode(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(linearLayoutManager.HORIZONTAL);
        mRecycleView.setLayoutManager(linearLayoutManager);

//        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
//        mRecycleView.setLayoutManager(gridLayoutManager);

//        mRecycleView.setLayoutManager(new StaggeredGridLayoutManager(4,
//                StaggeredGridLayoutManager.VERTICAL));
//        mRecycleView.setLayoutManager(new StaggeredGridLayoutManager(4,
//                StaggeredGridLayoutManager.HORIZONTAL));

        mRecycleView.setAdapter(mAdapter);

//        mRecycleView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL_LIST));
        mRecycleView.addItemDecoration(new DividerGridItemDecoration(getBaseContext()));
    }
}
