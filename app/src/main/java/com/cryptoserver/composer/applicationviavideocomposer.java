package com.cryptoserver.composer;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.cryptoserver.composer.utils.xdata;


/**
 * Created by devesh on 26/4/18.
 */

public class applicationviavideocomposer extends Application {
    public static Context mcontext;
    public static Activity mactvitiy;

    @Override
    public void onCreate() {
        super.onCreate();
       /* Fabric.with(this, new Crashlytics());
        mcontext = this;
        xdata.getinstance().init(this);*/
        xdata.getinstance().init(this);
    }

    public static Context getappcontext() {
        return mcontext;

    }

    public static Activity getactivity() {
        return mactvitiy;

    }

    public static void setActivity(Activity activity)
    {
        mactvitiy =activity;
    }
}