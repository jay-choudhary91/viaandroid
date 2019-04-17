package com.deeptruth.app.android.utils;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;


public class worldanalogclock extends vectorworldanalogclock {

    private void init(){
        //use this for the default Analog Clock (recommended)
        initializeSimple();

        //or use this if you want to use your own vector assets (not recommended)
        //initializeCustom(faceResourceId, hourResourceId, minuteResourceId, secondResourceId);
    }

    //mandatory constructor
    public worldanalogclock(Context ctx) {
        super(ctx);
        init();
    }

    // the other constructors are in case you want to add the view in XML

    public worldanalogclock(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public worldanalogclock(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public worldanalogclock(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }
}
