package com.cryptoserver.composer.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by devesh on 9/10/18.
 */

public class VisualizerViewMidea extends View {


    private byte[] mBytes;
    private float[] mPoints;
    private Rect mRect = new Rect();
    private Paint mForePaint = new Paint();

    public VisualizerViewMidea(Context context) {
        super(context);
        init();
    }

    public VisualizerViewMidea(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VisualizerViewMidea(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mBytes = null;
        mForePaint.setStrokeWidth(1f);
        mForePaint.setAntiAlias(true);
        mForePaint.setColor(Color.rgb(0, 128, 255));
    }

    public void updateVisualizer(byte[] bytes) {
        mBytes = bytes;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBytes == null) {
            return;
        }
        if (mPoints == null || mPoints.length < mBytes.length * 4) {
            mPoints = new float[mBytes.length * 4];
        }
        mRect.set(0, 0, getWidth(), getHeight());
        for (int i = 0; i < mBytes.length - 1; i++) {
            mPoints[i * 4] = mRect.width() * i / (mBytes.length - 1);
            mPoints[i * 4 + 1] = mRect.height() / 2
                    + ((byte) (mBytes[i] + 128)) * (mRect.height() / 2) / 128;
            mPoints[i * 4 + 2] = mRect.width() * (i + 1) / (mBytes.length - 1);
            mPoints[i * 4 + 3] = mRect.height() / 2
                    + ((byte) (mBytes[i + 1] + 128)) * (mRect.height() / 2)
                    / 128;
        }
        canvas.drawLines(mPoints, mForePaint);
    }


    /*@Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        *//*if (!active) {
            return;
        }*//*
        *//*DownloadService downloadService = DownloadServiceImpl.getInstance();
        if (downloadService != null && downloadService.getPlayerState() != PlayerState.STARTED) {
            return;
        }*//*

        if (mBytes == null) {
            return;
        }

        if (mPoints == null || mPoints.length < mBytes.length * 4) {
            mPoints = new float[mBytes.length * 4];
        }

        int w = getWidth();
        int h = getHeight();

        for (int i = 0; i < mBytes.length - 1; i++) {
            mPoints[i * 4] = w * i / (mBytes.length - 1);
            mPoints[i * 4 + 1] = h / 2 + ((byte) (mBytes[i] + 128)) * (h / 2) / 128;
            mPoints[i * 4 + 2] = w * (i + 1) / (mBytes.length - 1);
            mPoints[i * 4 + 3] = h / 2 + ((byte) (mBytes[i + 1] + 128)) * (h / 2) / 128;
        }

        canvas.drawLines(mPoints, mForePaint);
    }*/

}
