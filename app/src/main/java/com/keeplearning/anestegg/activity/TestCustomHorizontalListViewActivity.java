package com.keeplearning.anestegg.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.keeplearning.anestegg.R;
import com.keeplearning.anestegg.adapter.CustomHorizontalListViewAdapter;
import com.keeplearning.anestegg.widget.CustomHorizontalListView;

public class TestCustomHorizontalListViewActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_custom_horizontal_list_view);

        setupViews();
    }

    private void setupViews() {
        CustomHorizontalListView customHorizontalListView = (CustomHorizontalListView) findViewById(R.id.test_custom_horizontal_list_view);
        customHorizontalListView.setOnCustomItemClickListener(new CustomHorizontalListView.OnCustomItemClickListener() {

            @Override
            public void onItemClick(ViewGroup parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "您点击了第" + (position + 1), Toast.LENGTH_SHORT).show();
            }
        });
        customHorizontalListView.setIsCycle(true);
//        customHorizontalListView.setSelectedViewIndex(2);

        CustomHorizontalListViewAdapter adapter = new CustomHorizontalListViewAdapter(this);
        customHorizontalListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
