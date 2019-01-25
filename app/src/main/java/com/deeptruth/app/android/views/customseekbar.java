package com.deeptruth.app.android.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

/**
 * Created by root on 17/1/19.
 */

public class customseekbar extends android.support.v7.widget.AppCompatSeekBar {

    public customseekbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    Drawable mThumb;

    @Override
    public void setThumb(Drawable thumb) {
        super.setThumb(thumb);
        mThumb = thumb;
    }

    public Drawable getSeekBarThumb() {
        return mThumb;
    }
}


