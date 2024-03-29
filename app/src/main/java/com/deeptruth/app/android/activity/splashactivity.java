package com.deeptruth.app.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.deeptruth.app.android.BuildConfig;
import com.deeptruth.app.android.R;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.xdata;

import static android.widget.RelativeLayout.TRUE;

public class splashactivity extends Activity {

    ImageView img_fadefirst,img_logo,img_logotext;
    RelativeLayout rl_rootview;
    TextView txt_betaversion;
    LinearLayout layfooter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR,WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR);
        setContentView(R.layout.activity_splash);


        rl_rootview = (RelativeLayout) findViewById(R.id.rl_rootview);
        img_logotext= (ImageView) findViewById(R.id.img_logotext);
        img_logo= (ImageView) findViewById(R.id.img_logo);
        img_fadefirst= (ImageView) findViewById(R.id.img_fadefirst);
        txt_betaversion = (TextView) findViewById(R.id.txt_betaversion);
        txt_betaversion.setText(R.string.betaversion);
        layfooter = (LinearLayout) findViewById(R.id.footer);

        img_logotext.post(new Runnable() {
            @Override
            public void run() {
                imagesetmargins();
            }
        });

        try {

            common.clearnotification(splashactivity.this);
            Bundle bundle=getIntent().getExtras();
            xdata.getinstance().saveSetting(config.launchtype,"");
            if(bundle != null)
            {
                for (String key : bundle.keySet()) {
                    String value = bundle.get(key).toString();
                    if(value != null)
                    {
                        if(key.equals(config.launchtype))
                        {
                            xdata.getinstance().saveSetting(config.launchtype,value);
                            break;
                        }
                    }
                }
            }

            RequestOptions requestOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // because file name is always same
                    .skipMemoryCache(true);


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    img_fadefirst.setVisibility(View.VISIBLE);
                    Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fadein);
                    img_fadefirst.startAnimation(animFadeIn);

                    animFadeIn.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    img_logo.setVisibility(View.VISIBLE);
                                    img_logotext.setVisibility(View.VISIBLE);
                                    txt_betaversion.setVisibility(View.VISIBLE);
                                    Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),(R.anim.fadein));
                                    img_logo.startAnimation(animFadeIn);
                                    layfooter.startAnimation(animFadeIn);

                                    animFadeIn.setAnimationListener(new Animation.AnimationListener() {
                                        @Override
                                        public void onAnimationStart(Animation animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animation animation) {
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if(BuildConfig.FLAVOR.contains(config.build_flavor_reader))
                                                    {
                                                        Intent in=new Intent(splashactivity.this,homeactivity.class);
                                                        startActivity(in);
                                                        overridePendingTransition(R.anim.from_right_in,R.anim.from_left_out);
                                                        finish();
                                                    }
                                                    else
                                                    {
                                                        if(xdata.getinstance().getSetting(config.enableintroscreen).isEmpty() ||
                                                                xdata.getinstance().getSetting(config.enableintroscreen).
                                                                        equalsIgnoreCase("1"))
                                                        {
                                                            Intent intent=new Intent(splashactivity.this,
                                                                    introscreenactivity.class);
                                                            startActivity(intent);
                                                            overridePendingTransition(R.anim.from_right_in,R.anim.from_left_out);
                                                            finish();

                                                        }else{
                                                            Intent intent=new Intent(splashactivity.this,homeactivity.class);
                                                            startActivity(intent);
                                                            overridePendingTransition(R.anim.activityfadein, R.anim.activityfadeout);
                                                            finish();
                                                        }
                                                    }

                                                }
                                            },1000);
                                        }

                                        @Override
                                        public void onAnimationRepeat(Animation animation) {

                                        }
                                    });
                                }
                            },100);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                }
            },300);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void imagesetmargins(){

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int)getResources().getDimension(R.dimen.logimage_width),(int)getResources().getDimension(R.dimen.logoimage_height));
        layoutParams.setMargins(0,getscreenwidthheight(30),0,0);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL,TRUE);

        img_logo.setLayoutParams(layoutParams);

        RelativeLayout.LayoutParams layoutParamsimage = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,(int)getResources().getDimension(R.dimen.footer));
        layoutParamsimage.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,TRUE);
        layoutParamsimage.setMargins(0,0,0,getscreenwidthheight(10));
        layfooter.setLayoutParams(layoutParamsimage);
    }

    public int getscreenwidthheight(int percentage) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int percentageheight = (height / 100) * percentage;

        return percentageheight;
    }
}