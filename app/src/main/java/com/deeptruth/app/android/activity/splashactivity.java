package com.deeptruth.app.android.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.widget.LinearLayout;

import com.deeptruth.app.android.BuildConfig;
import com.deeptruth.app.android.R;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.xdata;

public class splashactivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.rootview);

        //ImageView img_image=(ImageView)findViewById(R.id.img_image);

        linearLayout.post(new Runnable() {
            @Override
            public void run() {
                getstatusbarheight();
            }
        });




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


    public void getstatusbarheight(){
        Rect rectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int statusBarHeight = rectangle.top;

        xdata.getinstance().saveSetting("statusbarheight", ""+statusBarHeight);

    }



    /*@Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }*/


     /*int[] color = {getResources().getColor(R.color.dark_blue_solid),getResources().getColor(R.color.blue)};
        float[] position = {0, 1};
        Shader.TileMode tile_mode = Shader.TileMode.CLAMP;
        LinearGradient lin_grad = new LinearGradient(0, 0, 0, 35,color,position, tile_mode);
        Shader shader_gradient = lin_grad;
        txt_deeptruth.getPaint().setShader(shader_gradient);*/
    //ImageView img_image=(ImageView)findViewById(R.id.img_image);
}
