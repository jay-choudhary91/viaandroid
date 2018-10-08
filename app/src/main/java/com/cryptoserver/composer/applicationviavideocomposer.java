package com.cryptoserver.composer;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.cryptoserver.composer.utils.xdata;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class applicationviavideocomposer extends Application {
    public static Context mcontext;
    public static Activity mactvitiy;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        mcontext = this;
        xdata.getinstance().init(this);
        xdata.getinstance().saveSetting(xdata.keybaseurl, "http://console.dev.crypto-servers.com/xapi.php?");
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
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