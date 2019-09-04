package com.deeptruth.app.android;

import android.app.Activity;
import android.app.Application;
/*import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ProcessLifecycleOwner;*/
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.util.LruCache;
import android.widget.Toast;

import com.deeptruth.app.android.onedrive.onedrivedefaultcallback;
import com.deeptruth.app.android.services.insertmediadataservice;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.xdata;
import com.crashlytics.android.Crashlytics;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.onedrive.sdk.authentication.MSAAuthenticator;
import com.onedrive.sdk.concurrency.ICallback;
import com.onedrive.sdk.core.ClientException;
import com.onedrive.sdk.core.DefaultClientConfig;
import com.onedrive.sdk.core.IClientConfig;
import com.onedrive.sdk.extensions.IOneDriveClient;
import com.onedrive.sdk.extensions.OneDriveClient;
import com.onedrive.sdk.logger.LoggerLevel;

import java.util.concurrent.atomic.AtomicReference;

import io.fabric.sdk.android.Fabric;

//public class applicationviavideocomposer extends Application implements LifecycleObserver {
public class applicationviavideocomposer extends Application
{
    public static Context mcontext;
    public static Activity mactvitiy;

    public static Typeface regularfonttype = null;
    public static Typeface semiboldfonttype = null;
    public static Typeface boldfonttype = null;
    public static Typeface comfortaaregular = null;
    public static Typeface bahnschriftregular = null;
    public static boolean isactivitybecomefinish = false;

    public static FFmpeg ffmpeg;
    private static final int maximagecachesize = 300;
    private LruCache<String, Bitmap> mImageCache;
    private final AtomicReference<IOneDriveClient> mClient = new AtomicReference<>();
    private ConnectivityManager connectivitymanager;

    @Override
    public void onCreate() {
        super.onCreate();
        connectivitymanager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
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
        //ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

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
                 Log.e("ffmpeg","onSuccess");

                    if(! xdata.getinstance().getSetting(config.servicedata_liststart).trim().isEmpty())
                    {
                        // Start service for grab frames and insert frame data into database.
                        if (! common.isservicerunning(getApplicationContext(),insertmediadataservice.class))
                        {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                                startForegroundService(new Intent(getApplicationContext(), insertmediadataservice.class));
                            else
                                startService(new Intent(getApplicationContext(), insertmediadataservice.class));
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


    /*@OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void onAppBackgrounded()
    {
        if(BuildConfig.FLAVOR.contains(config.build_flavor_composer))
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

    }*/

    /**
     * Create the client configuration
     * @return the newly created configuration
     */
    private IClientConfig createConfig() {
        final MSAAuthenticator msaAuthenticator = new MSAAuthenticator() {
            @Override
            public String getClientId() {
                return "000000004C146A60";
            }

            @Override
            public String[] getScopes() {
                return new String[] {"onedrive.readwrite", "onedrive.appfolder", "wl.offline_access"};
            }
        };

        final IClientConfig config = DefaultClientConfig.createWithAuthenticator(msaAuthenticator);
        config.getLogger().setLoggingLevel(LoggerLevel.Debug);
        return config;
    }

    /**
     * Get an instance of the service
     *
     * @return The Service
     */
    public synchronized IOneDriveClient getOneDriveClient() {
        if (mClient.get() == null) {
            throw new UnsupportedOperationException("Unable to generate a new service object");
        }
        return mClient.get();
    }

    /**
     * Used to setup the Services
     * @param activity the current activity
     * @param serviceCreated the callback
     */
    public synchronized void createOneDriveClient(final Activity activity, final ICallback<Void> serviceCreated) {
        final onedrivedefaultcallback<IOneDriveClient> callback = new onedrivedefaultcallback<IOneDriveClient>(activity) {
            @Override
            public void success(final IOneDriveClient result) {
                mClient.set(result);
                serviceCreated.success(null);
            }

            @Override
            public void failure(final ClientException error) {
                serviceCreated.failure(error);
            }
        };
        new OneDriveClient
                .Builder()
                .fromConfig(createConfig())
                .loginAndBuildClient(activity, callback);
    }

    /**
     * Gets the image cache for this application
     *
     * @return the image loader
     */
    public synchronized LruCache<String, Bitmap> getImageCache() {
        if (mImageCache == null) {
            mImageCache = new LruCache<>(maximagecachesize);
        }
        return mImageCache;
    }
}