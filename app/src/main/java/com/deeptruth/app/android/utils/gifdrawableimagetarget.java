package com.deeptruth.app.android.utils;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.ImageViewTarget;

/**
 * Created by devesh on 27/2/19.
 */

public class gifdrawableimagetarget extends ImageViewTarget<Drawable> {


        private int mLoopCount = GifDrawable.LOOP_FOREVER;

        public gifdrawableimagetarget(ImageView view, int loopCount) {
            super(view);
            mLoopCount = loopCount;
        }

        public gifdrawableimagetarget(ImageView view, int loopCount, boolean waitForLayout) {
            super(view, waitForLayout);
            mLoopCount = loopCount;
        }

        @Override
        protected void setResource(@Nullable Drawable resource) {
            if (resource instanceof GifDrawable) {
                ((GifDrawable) resource).setLoopCount(mLoopCount);
            }
            view.setImageDrawable(resource);
        }

}
