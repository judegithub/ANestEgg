package com.keeplearning.anestegg.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.keeplearning.anestegg.R;
import com.keeplearning.anestegg.widget.ScrollTextView;

public class TestScrollTextViewActivity extends BaseActivity {

    private final String TAG = "TestScrollTextViewActivity";

    private final static int WHAT_SCROLL = 0;
    private final static int WHAT_INIT = 1;

    private int DELAY_TIME_MILLIS = 5 * 1000;

    private boolean isScroll = false;

    private ScrollTextView mScrollTextView;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case WHAT_SCROLL:
                    isScroll = true;
                    mScrollTextView.fling();
                    break;

                case WHAT_INIT:
                    mScrollTextView.setText(mScrollTextView.getText().toString());
                    handler.sendEmptyMessageDelayed(WHAT_SCROLL, DELAY_TIME_MILLIS);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_scroll_text_view);

        setupViews();
    }

    private void setupViews() {
        mScrollTextView = (ScrollTextView) findViewById(R.id.test_scroll_text_view);
        mScrollTextView.setText("观自在菩萨，行深般若波罗蜜多时，照见五蕴皆空，度一切苦厄。舍利子，色不异空，空不异色，色即是空，空即是色，受想行识，亦复如是。舍利子，是诸法空相，不生不灭，不垢不净，不增不减。是故空中无色，无受想行识，无眼耳鼻舌身意，无色声香味触法，无眼界，乃至无意识界，无无明，亦无无明尽，乃至无老死，亦无老死尽。无苦集灭道，无智亦无得。以无所得故。菩提萨埵，依般若波罗蜜多故，心无挂碍。无挂碍故，无有恐怖，远离颠倒梦想，究竟涅盘。三世诸佛，依般若波罗蜜多故，得阿耨多罗三藐三菩提。故知般若波罗蜜多，是大神咒，是大明咒，是无上咒，是无等等咒，能除一切苦，真实不虚。故说般若波罗蜜多咒，即说咒曰：揭谛揭谛，波罗揭谛，波罗僧揭谛，菩提萨婆诃。");
        mScrollTextView.canScroll(true);
        mScrollTextView.setScrollFinishCallBack(new ScrollTextView.ScrollFinishCallBack() {
            @Override
            public void isFinished(boolean isFinished) {
                Log.d("DBG", TAG + " isFinished() isFinished: " + isFinished
                        + " isScroll: " + isScroll
                );
                if (isFinished && isScroll) {
                    isScroll = false;
                    handler.sendEmptyMessageDelayed(WHAT_INIT, DELAY_TIME_MILLIS);
                }
            }
        });

        handler.sendEmptyMessageDelayed(WHAT_SCROLL, DELAY_TIME_MILLIS);
    }

}
