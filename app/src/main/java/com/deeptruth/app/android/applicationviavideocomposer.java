package com.deeptruth.app.android;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.support.multidex.MultiDex;

import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.xdata;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class applicationviavideocomposer extends Application {
    public static Context mcontext;
    public static Activity mactvitiy;

    public static Typeface regularfonttype = null;
    public static Typeface semiboldfonttype = null;
    public static Typeface boldfonttype = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        mcontext = this;
        xdata.getinstance().init(this);
        xdata.getinstance().saveSetting(xdata.developer_mode, "0");

        //common.setting_check(xdata.xapi_url, "http://console.dev.crypto-servers.com/xapi.php?");
        common.setting_check(xdata.xapi_url, "http://dev.api.deeptruth.com/xapi.php?");  // Make changes on 2019-06-03 (matraex)
        //common.setting_check(xdata.xapi_url, "http://prod.api.deeptruth.com/xapi.php?");  // Make changes on 2019-06-03 (videolock production)
        common.setting_check(xdata.app_paid_level, "0");
        common.setting_check(xdata.developer_mode, "0");// 0 - false, 1 - true
        common.setting_check(xdata.unpaid_video_record_length, "300");

        regularfonttype = Typeface.createFromAsset(mcontext.getAssets(), "fonts/OpenSans-Regular.ttf");
        semiboldfonttype = Typeface.createFromAsset(mcontext.getAssets(), "fonts/OpenSans-Semibold.ttf");
        boldfonttype = Typeface.createFromAsset(mcontext.getAssets(), "fonts/OpenSans-Bold.ttf");
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