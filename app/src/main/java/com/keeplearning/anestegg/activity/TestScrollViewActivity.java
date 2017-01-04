package com.keeplearning.anestegg.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.keeplearning.anestegg.R;

public class TestScrollViewActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_scroll_view);

        setupViews();
    }

    private void setupViews() {
        LinearLayout l1 = (LinearLayout) findViewById(R.id.test_scroll_view_linear_layout);
        LinearLayout.LayoutParams paramsH = new LinearLayout.LayoutParams(150, 80);
        Button btn;
        for (int i = 0; i < 20; i++) {
            btn = new Button(this);
            btn.setText("第" + i + "个");
            btn.setLayoutParams(paramsH);

            l1.addView(btn);
        }

        LinearLayout l2 = (LinearLayout) findViewById(R.id.test_scroll_view_linear_layout2);
        LinearLayout.LayoutParams paramsV = new LinearLayout.LayoutParams(300, 200);
        for (int i = 0; i < 20; i++) {
            btn = new Button(this);
            btn.setText("第" + i + "个");
            btn.setLayoutParams(paramsV);

            l2.addView(btn);
        }
    }
}
