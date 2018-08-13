package com.cryptoserver.composer.utils;

import android.content.Context;
import android.view.View;
import android.widget.MediaController;

/**
 * Created by root on 13/8/18.
 */

public class FullScreenMediaController extends MediaController {


    public FullScreenMediaController(Context context) {
        super(context);
    }

    @Override
    public void setAnchorView(View view) {

        super.setAnchorView(view);


    }
}