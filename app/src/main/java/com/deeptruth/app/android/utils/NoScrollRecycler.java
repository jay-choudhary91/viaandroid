package com.deeptruth.app.android.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class NoScrollRecycler extends RecyclerView {

    public NoScrollRecycler(Context context){
        super(context);
    }

    public NoScrollRecycler(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public NoScrollRecycler(Context context, AttributeSet attrs, int style){
        super(context, attrs, style);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){

        //Ignore scroll events.
        if(ev.getAction() == MotionEvent.ACTION_MOVE)
            return true;

        //Dispatch event for non-scroll actions, namely clicks!
        return super.dispatchTouchEvent(ev);
    }
}
