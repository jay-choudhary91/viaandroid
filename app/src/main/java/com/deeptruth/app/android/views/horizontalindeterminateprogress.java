package com.deeptruth.app.android.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by ${matraex} on 16/5/19.
 */

public class horizontalindeterminateprogress extends FrameLayout {
    private ImageView mProgressImage;
    private TranslateAnimation mProgressAnimation;

    public horizontalindeterminateprogress(Context context, AttributeSet attrs) {
        super(context, attrs);

        mProgressImage = new ImageView(getContext());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        mProgressImage.setLayoutParams(layoutParams);
        addView(mProgressImage);
    }

    public void setBackgroundAsTile(int tileImageResId) {
        Bitmap tileBitmap = BitmapFactory.decodeResource(getResources(), tileImageResId);
        BitmapDrawable tileRepeatedBitmap = new BitmapDrawable(getResources(), tileBitmap);
        tileRepeatedBitmap.setTileModeX(Shader.TileMode.REPEAT);

        initAnimation(tileBitmap.getWidth());

        mProgressImage.setBackground(tileRepeatedBitmap);
    }

    private void initAnimation(int tileImageWidth) {
        if(mProgressAnimation == null)
        {
            LayoutParams layoutParams = (LayoutParams) mProgressImage.getLayoutParams();
            layoutParams.setMargins(-tileImageWidth, 0, 0, 0);

            // *HACK* tileImageWidth-3 is used because of *lags*(slow pause) in the moment
            // of animation END-RESTART.
            mProgressAnimation = new TranslateAnimation(0, tileImageWidth - 3, 0, 0);
            mProgressAnimation.setInterpolator(new LinearInterpolator());
            mProgressAnimation.setDuration(1000);
            mProgressAnimation.setRepeatCount(Animation.INFINITE);
        }

    }

    public void startAnimation() {
        if(mProgressAnimation != null)
            mProgressImage.startAnimation(mProgressAnimation);
    }

    public void stopAnimation() {
        if(mProgressAnimation != null && mProgressAnimation.hasStarted())
            mProgressAnimation.cancel();
    }
}