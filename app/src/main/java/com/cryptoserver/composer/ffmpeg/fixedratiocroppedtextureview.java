package com.cryptoserver.composer.ffmpeg;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.TextureView;

import com.cryptoserver.composer.ffmpeg.util.miscutils;


/**
 * Created by wanglei02 on 2016/1/6.
 */
public class fixedratiocroppedtextureview extends TextureView {
    private int mpreviewwidth;
    private int mpreviewheight;
    private int mcroppedwidthweight;
    private int mcroppedheightweight;

    public fixedratiocroppedtextureview(Context context) {
        super(context);
    }

    public fixedratiocroppedtextureview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public fixedratiocroppedtextureview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public fixedratiocroppedtextureview(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthmeasurespec, int heightmeasurespec) {
        super.onMeasure(widthmeasurespec, heightmeasurespec);
        int measuredwidth = getMeasuredWidth();
        int measuredheight = getMeasuredHeight();
        if (measuredwidth == 0 || measuredheight == 0) {
            return;
        }

        int width;
        int height;
        if (miscutils.isorientationlandscape(getContext())) {
            height = measuredheight;
            width = heighttowidth(measuredheight);
            if (width > measuredwidth) {
                width = measuredwidth;
                height = widthtoheight(width);
            }
        } else {
            width = measuredwidth;
            height = widthtoheight(measuredwidth);
            if (height > measuredheight) {
                height = measuredheight;
                width = heighttowidth(height);
            }
        }
        setMeasuredDimension(width, height);
    }

    private int widthtoheight(int width) {
        return width * mcroppedheightweight / mcroppedwidthweight;
    }

    private int heighttowidth(int height) {
        return height * mcroppedwidthweight / mcroppedheightweight;
    }

    @Override
    public void layout(int l, int t, int r, int b) {
        int actualpreviewwidth;
        int actualpreviewheight;
        int top;
        int left;
        if (miscutils.isorientationlandscape(getContext())) {
            actualpreviewheight = b - t;
            actualpreviewwidth = actualpreviewheight * mpreviewwidth / mpreviewheight;
            left = l + ((r - l) - actualpreviewwidth) / 2;
            top = t;
        } else {
            actualpreviewwidth = r - l;
            actualpreviewheight = actualpreviewwidth * mpreviewheight / mpreviewwidth;
            top = t + ((b - t) - actualpreviewheight) / 2;
            left = l;
        }
        super.layout(left, top, left + actualpreviewwidth, top + actualpreviewheight);
    }

    public void setpreviewsize(int previewwidth, int previewheight) {
        mpreviewwidth = previewwidth;
        mpreviewheight = previewheight;
    }

    public void setcroppedsizeweight(int widthweight, int heightweight) {
        this.mcroppedwidthweight = widthweight;
        this.mcroppedheightweight = heightweight;
    }
}
