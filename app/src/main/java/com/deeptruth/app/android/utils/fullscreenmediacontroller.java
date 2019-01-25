package com.deeptruth.app.android.utils;

import android.content.Context;
import android.view.View;
import android.widget.MediaController;

/**
 * Created by root on 13/8/18.
 */

public class fullscreenmediacontroller extends MediaController {


    public fullscreenmediacontroller(Context context) {
        super(context);
    }

    @Override
    public void setAnchorView(View view) {

        super.setAnchorView(view);


    }
}