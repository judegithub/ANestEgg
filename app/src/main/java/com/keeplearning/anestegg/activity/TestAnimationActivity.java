package com.keeplearning.anestegg.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.keeplearning.anestegg.R;
import com.keeplearning.anestegg.tools.AnimTools;

public class TestAnimationActivity extends BaseActivity {

    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_animation);

        setupViews();
    }

    private void setupViews() {
        mImageView = (ImageView) findViewById(R.id.test_animation_image_view);
    }

    public void shock(View v) {
        ObjectAnimator animator= AnimTools.shock(mImageView);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
            }
        });
        animator.start();
    }
}
