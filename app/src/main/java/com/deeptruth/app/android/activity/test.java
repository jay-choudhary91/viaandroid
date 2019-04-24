package com.deeptruth.app.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.deeptruth.app.android.BuildConfig;
import com.deeptruth.app.android.R;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.gifdrawableimagetarget;
import com.deeptruth.app.android.utils.xdata;
import com.facebook.network.connectionclass.ConnectionClassManager;
import com.facebook.network.connectionclass.ConnectionQuality;
import com.facebook.network.connectionclass.DeviceBandwidthSampler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import pl.droidsonroids.gif.GifDrawable;

public class test extends Activity {


    private static final String TAG = "test-Sample";

    private ConnectionClassManager mConnectionClassManager;
    private DeviceBandwidthSampler mDeviceBandwidthSampler;
    private ConnectionChangedListener mListener;
    //private String mURL = "http://connectionclass.parseapp.com/m100_hubble_4060.jpg";
    private String mURL = "https://m.media-amazon.com/images/S/aplus-media/vc/6a9569ab-cb8e-46d9-8aea-a7022e58c74a.jpg";
    private int mTries = 0;
    private ConnectionQuality mConnectionClass = ConnectionQuality.UNKNOWN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        Button btn_click= (Button) findViewById(R.id.btn_click);
        btn_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTries=0;
                mDeviceBandwidthSampler.startSampling();
                new DownloadImage().execute(mURL);
            }
        });

        mConnectionClassManager = ConnectionClassManager.getInstance();
        mDeviceBandwidthSampler = DeviceBandwidthSampler.getInstance();
        mListener = new ConnectionChangedListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mConnectionClassManager.remove(mListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mConnectionClassManager.register(mListener);
    }


    /**
     * Listener to update the UI upon connectionclass change.
     */
    private class ConnectionChangedListener
            implements ConnectionClassManager.ConnectionClassStateChangeListener {

        @Override
        public void onBandwidthStateChange(ConnectionQuality bandwidthState) {
            mConnectionClass = bandwidthState;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                   // Log.e("connection ",mConnectionClass.toString());
                    Log.e("connection "," "+mConnectionClassManager.getDownloadKBitsPerSecond());

                }
            });
        }
    }

    /**
     * AsyncTask for handling downloading and making calls to the timer.
     */
    private class DownloadImage extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(String... url) {
            String imageURL = url[0];
            try {
                // Open a stream to download the image from our URL.
                URLConnection connection = new URL(imageURL).openConnection();
                connection.setUseCaches(false);
                connection.connect();
                InputStream input = connection.getInputStream();
                try {
                    byte[] buffer = new byte[1024];

                    // Do some busy waiting while the stream is open.
                    while (input.read(buffer) != -1) {
                    }
                } finally {
                    input.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "Error while downloading image.");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            // Retry for up to 10 times until we find a ConnectionClass.
            if (mTries < 10) {
                mTries++;
                new DownloadImage().execute(mURL);
            }
            else
            {
                mDeviceBandwidthSampler.stopSampling();
            }

        }
    }

}
