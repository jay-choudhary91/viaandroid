package com.deeptruth.app.android.views;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by android-dev-17c on 8/11/2017.
 */

public class pageranimation implements ViewPager.PageTransformer {

    public void transformPage(View view, float position) {
        if(position <= -1.0F || position >= 1.0F) {
            view.setAlpha(0.0F);
        } else if( position == 0.0F ) {
            view.setAlpha(1.0F);
        } else {
            // position is between -1.0F & 0.0F OR 0.0F & 1.0F
            view.setAlpha(1.0F - Math.abs(position));
        }
    }
}
