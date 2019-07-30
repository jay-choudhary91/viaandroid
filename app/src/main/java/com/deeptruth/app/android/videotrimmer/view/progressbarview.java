/*
 * MIT License
 *
 * Copyright (c) 2016 Knowledge, education for life.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.deeptruth.app.android.videotrimmer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.videotrimmer.interfaces.onprogressvideolistener;
import com.deeptruth.app.android.videotrimmer.interfaces.onrangeseekbarlistener;

public class progressbarview extends View implements onrangeseekbarlistener, onprogressvideolistener {

    private int mprogressheight;
    private int mviewwidth;

    private final Paint mbackgroundcolor = new Paint();
    private final Paint mprogresscolor = new Paint();

    private Rect mbackgroundrect;
    private Rect mprogressrect;

    public progressbarview(@NonNull Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public progressbarview(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        int lineprogress = ContextCompat.getColor(getContext(), R.color.yellow);
        int linebackground = ContextCompat.getColor(getContext(), R.color.yellow);

        mprogressheight = getContext().getResources().getDimensionPixelOffset(R.dimen.progress_video_line_height);

        mbackgroundcolor.setAntiAlias(true);
        mbackgroundcolor.setColor(linebackground);

        mprogresscolor.setAntiAlias(true);
        mprogresscolor.setColor(lineprogress);
    }

    @Override
    protected void onMeasure(int widthmeasurespec, int heightmeasurespec) {
        super.onMeasure(widthmeasurespec, heightmeasurespec);

        int minw = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mviewwidth = resolveSizeAndState(minw, widthmeasurespec, 1);
        }

        int minh = getPaddingBottom() + getPaddingTop() + mprogressheight;
        int viewHeight = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            viewHeight = resolveSizeAndState(minh, heightmeasurespec, 1);
        }

        setMeasuredDimension(mviewwidth, viewHeight);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        drawlinebackground(canvas);
        drawlineprogress(canvas);
    }

    private void drawlinebackground(@NonNull Canvas canvas) {
        if (mbackgroundrect != null) {
            canvas.drawRect(mbackgroundrect, mbackgroundcolor);
        }
    }

    private void drawlineprogress(@NonNull Canvas canvas) {
        if (mprogressrect != null) {
            canvas.drawRect(mprogressrect, mprogresscolor);
        }
    }



    private void updatebackgroundrect(int index, float value) {

        if (mbackgroundrect == null) {
            mbackgroundrect = new Rect(0, 0, mviewwidth, mprogressheight);
        }

        int newValue = (int) ((mviewwidth * value) / 100);
        if (index == 0) {
            mbackgroundrect = new Rect(newValue, mbackgroundrect.top, mbackgroundrect.right, mbackgroundrect.bottom);
        } else {
            mbackgroundrect = new Rect(mbackgroundrect.left, mbackgroundrect.top, newValue, mbackgroundrect.bottom);
        }

        updateprogress(0, 0, 0.0f);
    }



    @Override
    public void updateprogress(int time, int max, float scale) {

        if (scale == 0) {
            mprogressrect = new Rect(0, mbackgroundrect.top, 0, mbackgroundrect.bottom);
        } else {
            int newValue = (int) ((mviewwidth * scale) / 100);
            mprogressrect = new Rect(mbackgroundrect.left, mbackgroundrect.top, newValue, mbackgroundrect.bottom);
        }

        invalidate();
    }

    @Override
    public void oncreate(rangeseekbarview rangeseekbarview, int index, float value) {
        updatebackgroundrect(index, value);
    }

    @Override
    public void onseek(rangeseekbarview rangeseekbarview, int index, float value) {
        updatebackgroundrect(index, value);
    }

    @Override
    public void onseekstart(rangeseekbarview rangeseekbarview, int index, float value) {
        updatebackgroundrect(index, value);
    }

    @Override
    public void onseekstop(rangeseekbarview rangeseekbarview, int index, float value) {
        updatebackgroundrect(index, value);
    }
}
