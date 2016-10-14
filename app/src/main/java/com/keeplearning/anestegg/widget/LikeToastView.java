package com.keeplearning.anestegg.widget;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.keeplearning.anestegg.R;

public class LikeToastView {

    private final String TAG = "LikeToastView";

    public static final int LENGTH_LONG = 2000;
    public static final int LENGTH_SHORT = 1000;

    private int mAnimStyleId = android.R.style.Animation_Toast;
    private int duration = 0;

    private String mToastContent = "";

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowParams;
    private View toastView;

    private Context mContext;
    private Handler mHandler;

    private final Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            removeView();
        }
    };

    private LikeToastView(Context context) {
        // Notice: we should get application context otherwise we will get error
        // "Activity has leaked window that was originally added"
        Context ctx = context.getApplicationContext();
        if (ctx == null) {
            ctx = context;
        }
        mContext = ctx;
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        init();
    }

    private void init() {
        mWindowParams = new WindowManager.LayoutParams();
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mWindowParams.alpha = 1.0f;
        mWindowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
//        mWindowParams.height = mContext.getResources().getDimensionPixelOffset(R.dimen.d_80dp);
        mWindowParams.height = 80;
        mWindowParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
//        mWindowParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
        mWindowParams.format = PixelFormat.TRANSLUCENT;
        mWindowParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        mWindowParams.setTitle(TAG);
        mWindowParams.packageName = mContext.getPackageName();
        mWindowParams.windowAnimations = mAnimStyleId;
//        mWindowParams.y = mContext.getResources().getDisplayMetrics().widthPixels / 5;
    }

    private View getDefaultToastView() {
        TextView view = new TextView(mContext);
        view.setText(mToastContent);
//        view.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        view.setGravity(Gravity.CENTER);
//        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, mContext.getResources().getDimensionPixelSize(R.dimen.d_19sp));
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
        view.setFocusable(false);
        view.setClickable(false);
        view.setFocusableInTouchMode(false);
        view.setTextColor(android.graphics.Color.WHITE);
//        Drawable drawable = mContext.getResources().getDrawable(
//                android.R.drawable.toast_frame);
        Drawable drawable = mContext.getResources().getDrawable(R.drawable.exit_view_bg);

        if (Build.VERSION.SDK_INT < 16) {
            view.setBackgroundDrawable(drawable);
        } else {
            view.setBackground(drawable);
        }
        return view;
    }

    public void show() {
        removeView();
        if (toastView == null) {
            toastView = getDefaultToastView();
        }
        mWindowParams.gravity = android.support.v4.view.GravityCompat.getAbsoluteGravity(
//              Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM,
                Gravity.BOTTOM,
                android.support.v4.view.ViewCompat.getLayoutDirection(toastView));
//        removeView();
        mWindowManager.addView(toastView, mWindowParams);
        if (mHandler == null) {
            mHandler = new Handler();
        }
        mHandler.postDelayed(timerRunnable, duration);
    }

    public void removeView() {
        if (toastView != null && toastView.getParent() != null) {
            mWindowManager.removeView(toastView);
            mHandler.removeCallbacks(timerRunnable);
        }
    }

    /**
     * @param context
     * @param content
     * @param duration
     * @return
     */
    public static LikeToastView makeText(Context context, String content,
                                         int duration) {
        LikeToastView view = new LikeToastView(context);
        view.setDuration(duration);
        view.setContent(content);
        return view;
    }

    /**
     * @param context
     * @param strId
     * @param duration
     * @return
     */
    public static LikeToastView makeText(Context context, int strId, int duration) {
        LikeToastView view = new LikeToastView(context);
        view.setDuration(duration);
        view.setContent(context.getString(strId));
        return view;
    }

    public LikeToastView setContent(String content) {
        this.mToastContent = content;
        return this;
    }

    public LikeToastView setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    public LikeToastView setAnimation(int animStyleId) {
        mAnimStyleId = animStyleId;
        mWindowParams.windowAnimations = mAnimStyleId;
        return this;
    }

    public LikeToastView setView(View view) {
        this.toastView = view;
        return this;
    }
}


