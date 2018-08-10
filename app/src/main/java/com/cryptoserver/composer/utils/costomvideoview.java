package com.cryptoserver.composer.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Created by devesh on 10/8/18.
 */

public class costomvideoview extends VideoView {
    private PlayPauseListener mListener;

    public costomvideoview(Context context) {
        super(context);
    }

    public costomvideoview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public costomvideoview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setPlayPauseListener(PlayPauseListener listener) {
        mListener = listener;
    }

    @Override
    public void pause() {
        super.pause();
        if (mListener != null) {
            mListener.onPause();
        }
    }

    @Override
    public void start() {
        super.start();
        if (mListener != null) {
            mListener.onPlay();
        }
    }

    public static interface PlayPauseListener {
        void onPlay();
        void onPause();
    }

}
