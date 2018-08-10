package com.cryptoserver.composer.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.applicationviavideocomposer;
import com.cryptoserver.composer.fragments.basefragment;
import com.cryptoserver.composer.fragments.fragment_matrictracklist;
import com.cryptoserver.composer.fragments.fragmentsettings;
import com.cryptoserver.composer.fragments.fragmentvideocomposer;
import com.cryptoserver.composer.fragments.fragmentvideolist;
import com.cryptoserver.composer.fragments.writerappactivity;
import com.cryptoserver.composer.services.CallService;

import butterknife.BindView;
import butterknife.ButterKnife;

public class homeactivity extends LocationAwareActivity {

    @BindView(R.id.img_add_icon)
    ImageView imgaddicon;
    @BindView(R.id.img_setting)
    ImageView imgsettingsicon;
    @BindView(R.id.actionbar)
    RelativeLayout actionbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        applicationviavideocomposer.setActivity(homeactivity.this);

        fragmentvideolist frag=new fragmentvideolist();
        replaceFragment(frag, false, true);

        imgaddicon.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* fragmentvideocomposer frag=new fragmentvideocomposer();
                replaceFragment(frag, false, true);*/

                Intent in=new Intent(homeactivity.this,writerappactivity.class);
                startActivity(in);
            }
        });
        imgsettingsicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // actionbar.setVisibility(View.GONE);
                fragmentsettings fragsettings=new fragmentsettings();
                replaceFragment(fragsettings, false, true);
            }
        });

        CallService mService = new CallService();
        Intent mIntent = new Intent(homeactivity.this, CallService.class);

        if (!isMyServiceRunning(mService.getClass()))
            startService(mIntent);

    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }

    @Override
    public int getlayoutid() {
        return R.layout.activity_homeactivity;
    }
    @Override

    public void launchHome() {

    }

    @Override
    public void updateheader(String txt) {

    }

    @Override
    public void updateActionBar(int showHide, String color) {

    }

    @Override
    public void updateActionBar(int showHide) {

    }

    @Override
    public void onfragmentbackstackchanged() {
        super.onfragmentbackstackchanged();
        basefragment fragment = getcurrentfragment();

        if (fragment instanceof fragmentvideolist) {
            imgaddicon.setVisibility(View.VISIBLE);
            imgsettingsicon.setVisibility(View.VISIBLE);
        }
        else if (fragment instanceof fragmentvideocomposer) {
            imgaddicon.setVisibility(View.INVISIBLE);
            imgsettingsicon.setVisibility(View.INVISIBLE);
        }
        else if(fragment instanceof fragmentsettings){
           /* imgaddicon.setVisibility(View.INVISIBLE);
            imgsettingsicon.setVisibility(View.INVISIBLE);*/
        }
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        try
        {
            imgaddicon.setVisibility(View.VISIBLE);
            imgsettingsicon.setVisibility(View.VISIBLE);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }*/
}
