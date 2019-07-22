package com.deeptruth.app.android;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.deeptruth.app.android.services.appbackgroundactionservice;
import com.deeptruth.app.android.services.insertmediadataservice;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.xdata;
import com.crashlytics.android.Crashlytics;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import io.fabric.sdk.android.Fabric;

public class applicationviavideocomposer extends Application implements LifecycleObserver {
    public static Context mcontext;
    public static Activity mactvitiy;

    public static Typeface regularfonttype = null;
    public static Typeface semiboldfonttype = null;
    public static Typeface boldfonttype = null;
    public static Typeface comfortaaregular = null;
    public static Typeface bahnschriftregular = null;
    public static boolean isactivitybecomefinish = false;

    public static FFmpeg ffmpeg;
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        mcontext = this;
        xdata.getinstance().init(this);
        xdata.getinstance().saveSetting(xdata.developer_mode, "0");

        //common.setting_check(xdata.xapi_url, "http://console.dev.crypto-servers.com/xapi.php?");
        common.setting_check(xdata.xapi_url, config.development_url);  // Make changes on 2019-06-03 (matraex)
        //common.setting_check(xdata.xapi_url, "http://prod.api.deeptruth.com/xapi.php?");  // Make changes on 2019-06-03 (videolock production)
        common.setting_check(xdata.app_paid_level, "0");
        common.setting_check(xdata.developer_mode, "0");// 0 - false, 1 - true
        common.setting_check(xdata.unpaid_media_record_length, "30"); // 30 seconds media record length as changes on 2019-07-22
        common.setting_check(xdata.unpaid_media_record_trim_count, "5"); // 5 media record allow for unpaid user as changes on 2019-07-22

        common.setting_check(config.enabledevelopment, "1");
        common.setting_check(config.enableproduction, "0");

        regularfonttype = Typeface.createFromAsset(mcontext.getAssets(), "fonts/Comfortaa-Regular.ttf");
        semiboldfonttype = Typeface.createFromAsset(mcontext.getAssets(), "fonts/Comfortaa-Bold.ttf");
        boldfonttype = Typeface.createFromAsset(mcontext.getAssets(), "fonts/Comfortaa-Bold.ttf");
        comfortaaregular = Typeface.createFromAsset(mcontext.getAssets(), "fonts/Comfortaa-Regular.ttf");
        bahnschriftregular = Typeface.createFromAsset(mcontext.getAssets(), "fonts/BAHNSCHRIFT-11.TTF");
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        try {
            if (ffmpeg == null) {
                Log.d("ffmpeg", "ffmpeg : is loading..");

                ffmpeg = FFmpeg.getInstance(getApplicationContext());
            }
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                    // showUnsupportedExceptionDialog();
                    Log.d("ffmpeg", " onFailure");
                }

                @Override
                public void onSuccess() {
                 Log.e("onsucess","onSuccess");

                    if(! xdata.getinstance().getSetting(config.servicedata_liststart).trim().isEmpty())
                    {
                        // Start service for grab frames and insert frame data into database.
                        if (! common.isservicerunning(getApplicationContext(),insertmediadataservice.class))
                        {
                            Intent intent = new Intent(getApplicationContext(), insertmediadataservice.class);
                            startService(intent);
                        }
                    }
                }
            });
        }catch (FFmpegNotSupportedException e) {
            //showUnsupportedExceptionDialog();
            Log.d("ffmpeg", "FFmpegNotSupportedException : " + e);
        } catch (Exception e) {
            Log.d("ffmpeg", "EXception no controlada : " + e);
        }
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

    public static void setisactivitybecomefinish(boolean isbecomefinish)
    {
        isactivitybecomefinish =isbecomefinish;
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void onAppBackgrounded() {
        if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_composer))
        {
            if(! isactivitybecomefinish)
            {
                if (common.isservicerunning(getApplicationContext(),appbackgroundactionservice.class))
                    stopService(new Intent(getBaseContext(), appbackgroundactionservice.class));

                startService(new Intent(getBaseContext(), appbackgroundactionservice.class));
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private void onAppForegrounded() {

    }

}