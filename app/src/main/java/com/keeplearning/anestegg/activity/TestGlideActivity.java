package com.keeplearning.anestegg.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.keeplearning.anestegg.R;

/**
 * 需要在 build.gradle上添加依赖，如：compile 'com.github.bumptech.glide:glide:3.6.1'
 */
public class TestGlideActivity extends BaseActivity {

    private final String TAG = "TestGlideActivity";

    private ImageView mImageView;

    private int mIndex = 0;
    private String mPicUrl = "http://image.cnwest.com/attachement/jpg/site1/20150722/001372d8a0ca1719359649.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_glide);

        setupViews();
    }

    private void setupViews() {
        mImageView = (ImageView) findViewById(R.id.test_glide_image_view);
        mImageView.setVisibility(View.INVISIBLE);
//        Glide.with(getBaseContext())
//                .load("http://image.cnwest.com/attachement/jpg/site1/20150722/001372d8a0ca1719359649.jpg")
//                .placeholder(R.drawable.item_default_pic)
////                .bitmapTransform()
//                .into(new SimpleTarget<GlideDrawable>() {
//                    @Override
//                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
//
//                        Log.d("DBG", TAG + " setupViews() onResourceReady...");
//                    }
//                });


        Glide.with(this)
                .load(mPicUrl)
                .centerCrop()
                .crossFade()
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        Log.d("DBG", TAG + " onResourceReady()......");
//                        mImageView.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                .into(mImageView);

//        new SimpleTarget<Bitmap>(440, 329) {
//            @Override
//            public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
//                // setImageBitmap(bitmap) on CircleImageView
//            }
//        }
    }

    public void change(View v) {
        if (mIndex == 0) {
            mPicUrl = "http://image.tianjimedia.com/uploadImages/2015/288/03/02547F277V58.jpg";
//            mPicUrl = "http://image.baidu.com/search/detail?ct=503316480&z=&tn=baiduimagedetail&ipn=d&word=%E5%81%A5%E8%BA%AB%E7%BE%8E%E5%A5%B3&step_word=&ie=utf-8&in=&cl=2&lm=-1&st=-1&cs=997260929,473909858&os=514900902,159917481&simid=3341488292,272948748&pn=868&rn=1&di=44854518940&ln=1960&fr=&fmq=1476946570261_R&ic=0&s=undefined&se=&sme=&tab=0&width=&height=&face=undefined&is=0,0&istype=2&ist=&jit=&bdtype=0&adpicid=0&pi=0&gsm=32a&objurl=http%3A%2F%2Fimage84.360doc.com%2FDownloadImg%2F2015%2F04%2F0121%2F51883441_38.gif&rpstart=0&rpnum=0&adpicid=0";
            Glide.with(this)
                    .load(mPicUrl)
                    .centerCrop()
                    .crossFade()
                    .into(mImageView);

            mIndex = 1;
        } else {
            mPicUrl = "http://image.cnwest.com/attachement/jpg/site1/20150722/001372d8a0ca1719359649.jpg";
            Glide.with(this)
                    .load(mPicUrl)
                    .centerCrop()
                    .crossFade()
                    .into(mImageView);


            mIndex = 0;
        }
    }
}
