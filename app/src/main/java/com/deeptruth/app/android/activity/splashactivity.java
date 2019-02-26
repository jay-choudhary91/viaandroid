package com.deeptruth.app.android.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.deeptruth.app.android.BuildConfig;
import com.deeptruth.app.android.R;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.xdata;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;

public class splashactivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        ImageView img_imagedeep=(ImageView)findViewById(R.id.img_imagedeep);

        try {
            GifDrawable gifDrawable = new GifDrawable(getResources(), R.drawable.intro_thumb);
            gifDrawable.setLoopCount(1);
            gifDrawable.setSpeed(1.0f);
            img_imagedeep.setImageDrawable(gifDrawable);
         //   img_imagedeep.setAlpha(0f);
            img_imagedeep.setVisibility(View.VISIBLE);
            img_imagedeep.animate()    // .alpha(1.0f)   .setListener(null);
                    .setDuration(100);

        } catch (Exception e) {
            e.printStackTrace();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getstatusbarheight();
                if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_reader))
                {
                    Intent in=new Intent(splashactivity.this,homeactivity.class);
                    startActivity(in);
                    finish();
                }
                else
                {
                    Intent intent=new Intent(splashactivity.this,introscreenactivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        },1500);
    }


    public void getstatusbarheight(){
        Rect rectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int statusBarHeight = rectangle.top;

        xdata.getinstance().saveSetting("statusbarheight", ""+statusBarHeight);

    }
}
