package com.deeptruth.app.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.deeptruth.app.android.BuildConfig;
import com.deeptruth.app.android.R;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.gifdrawableimagetarget;
import com.deeptruth.app.android.utils.xdata;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class splashactivity extends Activity {

    ImageView img_imagedeep;
    GifDrawable gifDrawable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR,WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR);
       // requestWindowFeature(Window.FEATURE_NO_TITLE);
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


           // img_imagedeep.lo
           /* gifDrawable.setLoopCount(1);
            gifDrawable.setSpeed(1.0f);
            img_imagedeep.setImageDrawable(gifDrawable);
            img_imagedeep.setAlpha(0f);
           // img_imagedeep.setVisibility(View.VISIBLE);
            img_imagedeep.animate()
             .setDuration(100)
             .alpha(1.0f)
             .setListener(null);*/



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
                }
            }
        },2000);
    }
    public void getstatusbarheight() {
        Rect rectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int statusBarHeight = rectangle.top;
        if (statusBarHeight != 0) {
            xdata.getinstance().saveSetting("statusbarheight", "" + statusBarHeight);
        }
    }
}
