package com.cryptoserver.composer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cryptoserver.composer.BuildConfig;
import com.cryptoserver.composer.R;
import com.cryptoserver.composer.fragments.footerpagerfragment;
import com.cryptoserver.composer.fragments.headerpagerfragment;
import com.cryptoserver.composer.models.intro;
import com.cryptoserver.composer.utils.config;
import com.cryptoserver.composer.utils.xdata;
import com.cryptoserver.composer.views.pageranimation;
import com.cryptoserver.composer.views.pagercustomduration;

import java.util.Date;

public class splashactivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //ImageView img_image=(ImageView)findViewById(R.id.img_image);
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
                    Intent intent=new Intent(splashactivity.this,introscreenactivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        },1500);
    }


     /*int[] color = {getResources().getColor(R.color.dark_blue_solid),getResources().getColor(R.color.blue)};
        float[] position = {0, 1};
        Shader.TileMode tile_mode = Shader.TileMode.CLAMP;
        LinearGradient lin_grad = new LinearGradient(0, 0, 0, 35,color,position, tile_mode);
        Shader shader_gradient = lin_grad;
        txt_deeptruth.getPaint().setShader(shader_gradient);*/
    //ImageView img_image=(ImageView)findViewById(R.id.img_image);
}
