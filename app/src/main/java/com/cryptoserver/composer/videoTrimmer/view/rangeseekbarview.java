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
package com.cryptoserver.composer.videoTrimmer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.videoTrimmer.interfaces.onrangeseekbarlistener;

import java.util.ArrayList;
import java.util.List;


public class rangeseekbarview extends View {

    private static final String tag = rangeseekbarview.class.getSimpleName();

    private int mheighttimeline;
    private List<thumb> mthumbs;
    private List<onrangeseekbarlistener> mlisteners;
    private float mmaxwidth;
    private float mthumbwidth;
    private float mthumbheight;
    private int mviewwidth;
    private float mpixelrangemin;
    private float mpixelrangemax;
    private float mscalerangemax;
    private boolean mfirstrun;

    private final Paint mshadow = new Paint();
    private final Paint mline = new Paint();

    public rangeseekbarview(@NonNull Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public rangeseekbarview(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mthumbs = thumb.initthumbs(getResources());
        mthumbwidth = thumb.getwidthbitmap(mthumbs);
        mthumbheight = thumb.getheightbitmap(mthumbs);

        mscalerangemax = 100;
        mheighttimeline = getContext().getResources().getDimensionPixelOffset(R.dimen.frames_video_height);

        setFocusable(true);
        setFocusableInTouchMode(true);

        mfirstrun = true;

        int shadowcolor = ContextCompat.getColor(getContext(), R.color.shadow_color);
        mshadow.setAntiAlias(true);
        mshadow.setColor(shadowcolor);
        mshadow.setAlpha(177);

        int linecolor = ContextCompat.getColor(getContext(), R.color.line_color);
        mline.setAntiAlias(true);
        mline.setColor(linecolor);
       // mline.setAlpha(200);
    }

    public void initmaxwidth() {
        mmaxwidth = mthumbs.get(1).getpos() - mthumbs.get(0).getpos();

        onseekstop(this, 0, mthumbs.get(0).getval());
        onseekstop(this, 1, mthumbs.get(1).getval());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int minW = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mviewwidth = resolveSizeAndState(minW, widthMeasureSpec, 1);
        }

        int minH = getPaddingBottom() + getPaddingTop() + (int) mthumbheight + mheighttimeline;
        int viewHeight = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            viewHeight = resolveSizeAndState(minH, heightMeasureSpec, 1);
        }

        setMeasuredDimension(mviewwidth, viewHeight);

        mpixelrangemin = 0;
        mpixelrangemax = mviewwidth - mthumbwidth;

        if (mfirstrun) {
            for (int i = 0; i < mthumbs.size(); i++) {
                thumb th = mthumbs.get(i);
                th.setval(mscalerangemax * i);
                th.setpos(mpixelrangemax * i);
            }
            // Fire listener callback
            oncreate(this, currentThumb, getthumbvalue(currentThumb));
            mfirstrun = false;
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        drawshadow(canvas);
        drawthumbs(canvas);
    }

    private int currentThumb = 0;

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent ev) {
        final thumb mThumb;
        final thumb mThumb2;
        final float coordinate = ev.getX();
        final int action = ev.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                // Remember where we started
                currentThumb = getclosestthumb(coordinate);

                if (currentThumb == -1) {
                    return false;
                }

                mThumb = mthumbs.get(currentThumb);
                mThumb.setlasttouchx(coordinate);
                onseekstart(this, currentThumb, mThumb.getval());
                return true;
            }
            case MotionEvent.ACTION_UP: {

                if (currentThumb == -1) {
                    return false;
                }

                mThumb = mthumbs.get(currentThumb);
                onseekstop(this, currentThumb, mThumb.getval());
                return true;
            }

            case MotionEvent.ACTION_MOVE: {
                mThumb = mthumbs.get(currentThumb);
                mThumb2 = mthumbs.get(currentThumb == 0 ? 1 : 0);
                // Calculate the distance moved
                final float dx = coordinate - mThumb.getlasttouchx();
                final float newX = mThumb.getpos() + dx;
                if (currentThumb == 0) {

                    if ((newX + mThumb.getwidthbitmap()) >= mThumb2.getpos()) {
                        mThumb.setpos(mThumb2.getpos() - mThumb.getwidthbitmap());
                    } else if (newX <= mpixelrangemin) {
                        mThumb.setpos(mpixelrangemin);
                    } else {
                        //Check if thumb is not out of max width
                        checkpositionthumb(mThumb, mThumb2, dx, true);
                        // Move the object
                        mThumb.setpos(mThumb.getpos() + dx);

                        // Remember this touch position for the next move event
                        mThumb.setlasttouchx(coordinate);
                    }

                } else {
                    if (newX <= mThumb2.getpos() + mThumb2.getwidthbitmap()) {
                        mThumb.setpos(mThumb2.getpos() + mThumb.getwidthbitmap());
                    } else if (newX >= mpixelrangemax) {
                        mThumb.setpos(mpixelrangemax);
                    } else {
                        //Check if thumb is not out of max width
                        checkpositionthumb(mThumb2, mThumb, dx, false);
                        // Move the object
                        mThumb.setpos(mThumb.getpos() + dx);
                        // Remember this touch position for the next move event
                        mThumb.setlasttouchx(coordinate);
                    }
                }

                setthumbpos(currentThumb, mThumb.getpos());

                // Invalidate to request a redraw
                invalidate();
                return true;
            }
        }
        return false;
    }

    private void checkpositionthumb(@NonNull thumb mThumbLeft, @NonNull thumb mThumbRight, float dx, boolean isLeftMove) {
        if (isLeftMove && dx < 0) {
            if ((mThumbRight.getpos() - (mThumbLeft.getpos() + dx)) > mmaxwidth) {
                mThumbRight.setpos(mThumbLeft.getpos() + dx + mmaxwidth);
                setthumbpos(1, mThumbRight.getpos());
            }
        } else if (!isLeftMove && dx > 0) {
            if (((mThumbRight.getpos() + dx) - mThumbLeft.getpos()) > mmaxwidth) {
                mThumbLeft.setpos(mThumbRight.getpos() + dx - mmaxwidth);
                setthumbpos(0, mThumbLeft.getpos());
            }
        }
    }

    private int getunstuckfrom(int index) {
        int unstuck = 0;
        float lastVal = mthumbs.get(index).getval();
        for (int i = index - 1; i >= 0; i--) {
            thumb th = mthumbs.get(i);
            if (th.getval() != lastVal)
                return i + 1;
        }
        return unstuck;
    }

    private float pixeltoscale(int index, float pixelValue) {
        float scale = (pixelValue * 100) / mpixelrangemax;
        if (index == 0) {
            float pxThumb = (scale * mthumbwidth) / 100;
            return scale + (pxThumb * 100) / mpixelrangemax;
        } else {
            float pxThumb = ((100 - scale) * mthumbwidth) / 100;
            return scale - (pxThumb * 100) / mpixelrangemax;
        }
    }

    private float scaletopixel(int index, float scaleValue) {
        float px = (scaleValue * mpixelrangemax) / 100;
        if (index == 0) {
            float pxThumb = (scaleValue * mthumbwidth) / 100;
            return px - pxThumb;
        } else {
            float pxThumb = ((100 - scaleValue) * mthumbwidth) / 100;
            return px + pxThumb;
        }
    }

    private void calculatethumbvalue(int index) {
        if (index < mthumbs.size() && !mthumbs.isEmpty()) {
            thumb th = mthumbs.get(index);
            th.setval(pixeltoscale(index, th.getpos()));
            onseek(this, index, th.getval());
        }
    }

    private void calculatethumbpos(int index) {
        if (index < mthumbs.size() && !mthumbs.isEmpty()) {
            thumb th = mthumbs.get(index);
            th.setpos(scaletopixel(index, th.getval()));
        }
    }

    private float getthumbvalue(int index) {
        return mthumbs.get(index).getval();
    }

    public void setThumbValue(int index, float value) {
        mthumbs.get(index).setval(value);
        calculatethumbpos(index);
        // Tell the view we want a complete redraw
        invalidate();
    }

    private void setthumbpos(int index, float pos) {
        mthumbs.get(index).setpos(pos);
        calculatethumbvalue(index);
        // Tell the view we want a complete redraw
        invalidate();
    }

    private int getclosestthumb(float coordinate) {
        int closest = -1;
        if (!mthumbs.isEmpty()) {
            for (int i = 0; i < mthumbs.size(); i++) {
                // Find thumb closest to x coordinate
                final float tcoordinate = mthumbs.get(i).getpos() + mthumbwidth;
                if (coordinate >= mthumbs.get(i).getpos() && coordinate <= tcoordinate) {
                    closest = mthumbs.get(i).getindex();
                }
            }
        }
        return closest;
    }

    private void drawshadow(@NonNull Canvas canvas) {
        if (!mthumbs.isEmpty()) {

            for (thumb th : mthumbs) {
                if (th.getindex() == 0) {
                    final float x = th.getpos() + getPaddingLeft();
                    if (x > mpixelrangemin) {
                        Rect mRect = new Rect((int) mthumbwidth, 0, (int) (x + mthumbwidth), mheighttimeline);
                        canvas.drawRect(mRect, mshadow);
                    }
                } else {
                    final float x = th.getpos() - getPaddingRight();
                    if (x < mpixelrangemax) {
                        Rect mRect = new Rect((int) x, 0, (int) (mviewwidth - mthumbwidth), mheighttimeline);
                        canvas.drawRect(mRect, mshadow);
                    }
                }
            }
        }
    }

    private void drawthumbs(@NonNull Canvas canvas) {

        if (!mthumbs.isEmpty()) {
            for (thumb th : mthumbs) {
                if (th.getindex() == 0) {
                    canvas.drawBitmap(th.getbitmap(), th.getpos() + getPaddingLeft(), getPaddingTop() + mheighttimeline, null);
                } else {
                    canvas.drawBitmap(th.getbitmap(), th.getpos() - getPaddingRight(), getPaddingTop() + mheighttimeline, null);
                }
            }
        }
    }

    public void addonrangeseekbarlistener(onrangeseekbarlistener listener) {

        if (mlisteners == null) {
            mlisteners = new ArrayList<>();
        }

        mlisteners.add(listener);
    }

    private void oncreate(rangeseekbarview rangeseekbarview, int index, float value) {
        if (mlisteners == null)
            return;

        for (onrangeseekbarlistener item : mlisteners) {
            item.oncreate(rangeseekbarview, index, value);
        }
    }

    private void onseek(rangeseekbarview rangeseekbarview, int index, float value) {
        if (mlisteners == null)
            return;

        for (onrangeseekbarlistener item : mlisteners) {
            item.onseek(rangeseekbarview, index, value);
        }
    }

    private void onseekstart(rangeseekbarview rangeseekbarview, int index, float value) {
        if (mlisteners == null)
            return;

        for (onrangeseekbarlistener item : mlisteners) {
            item.onseekstart(rangeseekbarview, index, value);
        }
    }

    private void onseekstop(rangeseekbarview rangeseekbarview, int index, float value) {
        if (mlisteners == null)
            return;

        for (onrangeseekbarlistener item : mlisteners) {
            item.onseekstop(rangeseekbarview, index, value);
        }
    }

    public List<thumb> getthumbs() {
        return mthumbs;
    }


}
