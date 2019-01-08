package com.cryptoserver.composer.views;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

/**
 * Created by android-dev-17c on 8/11/2017.
 */

public class pagercustomduration extends ViewPager {

    int delay=500;
    private int childid=0;
    public pagercustomduration(Context context, AttributeSet attrs)
    {
        super( context, attrs );
        setMyScroller();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (childid > 0) {
            View scroll = findViewById(childid);
            if (scroll != null) {
                Rect rect = new Rect();
                scroll.getHitRect(rect);
                if (rect.contains((int) event.getX(), (int) event.getY())) {
                    return false;
                }
            }
        }
        return super.onInterceptTouchEvent(event);
    }

    public void setchildid(int childid) {
        this.childid = childid;
    }

    private void setMyScroller()
    {
        try
        {
            Class<?> viewpager = ViewPager.class;
            Field scroller = viewpager.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            scroller.set(this, new MyScroller(getContext()));
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public class MyScroller extends Scroller
    {
        public MyScroller(Context context)
        {
            super(context, new DecelerateInterpolator());
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration)
        {
            super.startScroll(startX, startY, dx, dy, delay /*1 secs*/);
        }
    }
}