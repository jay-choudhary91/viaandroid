package com.deeptruth.app.android.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.util.AttributeSet;

import com.deeptruth.app.android.R;
import com.google.gson.internal.bind.SqlDateTypeAdapter;


public class circletosquare_animation extends android.support.v7.widget.AppCompatImageView {

    @Nullable
    private AnimatedVectorDrawableCompat circleToSquare;
    @Nullable
    private AnimatedVectorDrawableCompat squareToCircle;
    private boolean showingCircle = false;

    public circletosquare_animation(Context context) {
        super(context);
        init();
    }

    public circletosquare_animation(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public circletosquare_animation(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        showingCircle = true;
        circleToSquare = AnimatedVectorDrawableCompat.create(getContext(), R.drawable.avd_anim);
        squareToCircle = AnimatedVectorDrawableCompat.create(getContext(), R.drawable.avd_animm);
        setImageDrawable(circleToSquare);
    }

    public void morph() {
        AnimatedVectorDrawableCompat drawable = showingCircle ? circleToSquare : squareToCircle;
        setImageDrawable(drawable);
        if (drawable != null) {
            drawable.start();
        }
        showingCircle = !showingCircle;
    }

}
