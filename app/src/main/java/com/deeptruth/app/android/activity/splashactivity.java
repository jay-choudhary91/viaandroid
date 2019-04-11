package com.deeptruth.app.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.deeptruth.app.android.BuildConfig;
import com.deeptruth.app.android.R;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.gifdrawableimagetarget;
import com.deeptruth.app.android.utils.xdata;

import pl.droidsonroids.gif.GifDrawable;

public class splashactivity extends Activity {

    ImageView img_imagedeep;
    GifDrawable gifDrawable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR,WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR);
        setContentView(R.layout.activity_splash);
        img_imagedeep= (ImageView) findViewById(R.id.img_imagedeep);

        try {

            RequestOptions requestOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // because file name is always same
                    .skipMemoryCache(true);

            Glide.with(this)
                    .load(R.drawable.intro_thumb)
                    .apply(requestOptions)
                    .into( new gifdrawableimagetarget(img_imagedeep,1));

        } catch (Exception e) {
            e.printStackTrace();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_reader))
                {
                    Intent in=new Intent(splashactivity.this,homeactivity.class);
                    startActivity(in);
                    finish();
                }
                else
                {

                    if(xdata.getinstance().getSetting(config.enableintroscreen).isEmpty() || xdata.getinstance().getSetting(config.enableintroscreen).equalsIgnoreCase("yes"))
                    {
                        if(xdata.getinstance().getSetting(config.enableintroscreen).isEmpty())
                            xdata.getinstance().saveSetting(config.enableintroscreen,"no");

                        Intent intent=new Intent(splashactivity.this,introscreenactivity.class);
                        startActivity(intent);
                        finish();

                    }else{
                        Intent intent=new Intent(splashactivity.this,homeactivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.activityfadein, R.anim.activityfadeout);
                        finish();
                    }

                    // Login portion commented on app launch till furthure notice.
                   /* if(xdata.getinstance().getSetting(config.authtoken).trim().isEmpty())
                    {
                        Intent intent=new Intent(splashactivity.this,signinactivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        if(xdata.getinstance().getSetting(config.enableintroscreen).isEmpty() || xdata.getinstance().getSetting(config.enableintroscreen).equalsIgnoreCase("yes"))
                        {
                            if(xdata.getinstance().getSetting(config.enableintroscreen).isEmpty())
                                xdata.getinstance().saveSetting(config.enableintroscreen,"no");

                            Intent intent=new Intent(splashactivity.this,introscreenactivity.class);
                            startActivity(intent);
                            finish();

                        }else{
                            Intent intent=new Intent(splashactivity.this,homeactivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.activityfadein, R.anim.activityfadeout);
                            finish();
                        }
                    }*/
                }
            }
        },2000);
    }
}
