package com.cryptoserver.composer.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cryptoserver.composer.BuildConfig;
import com.cryptoserver.composer.R;
import com.cryptoserver.composer.applicationviavideocomposer;
import com.cryptoserver.composer.fragments.audiocomposerfragment;
import com.cryptoserver.composer.fragments.audioreaderfragment;
import com.cryptoserver.composer.fragments.basefragment;
import com.cryptoserver.composer.fragments.bottombarfragment;
import com.cryptoserver.composer.fragments.bottombarrederfrag;
import com.cryptoserver.composer.fragments.composervideoplayerfragment;
import com.cryptoserver.composer.fragments.fragmentmedialist;
import com.cryptoserver.composer.fragments.fragmentsettings;
import com.cryptoserver.composer.fragments.imagecomposerfragment;
import com.cryptoserver.composer.fragments.imagereaderfragment;
import com.cryptoserver.composer.fragments.readermedialist;
import com.cryptoserver.composer.fragments.videoreaderfragment;
import com.cryptoserver.composer.fragments.videocomposerfragment;
import com.cryptoserver.composer.fragments.videoplayfragment;
import com.cryptoserver.composer.services.callservice;
import com.cryptoserver.composer.utils.config;
import com.cryptoserver.composer.utils.xdata;

import butterknife.BindView;
import butterknife.ButterKnife;

public class homeactivity extends locationawareactivity implements View.OnClickListener {

    @BindView(R.id.txt_title)
    TextView txt_title;
    @BindView(R.id.img_add_icon)
    ImageView imgaddicon;
    @BindView(R.id.img_setting)
    ImageView imgsettingsicon;
    @BindView(R.id.img_back)
    ImageView img_back;
    @BindView(R.id.img_upload_icon)
    ImageView imguploadicon;

    @BindView(R.id.img_share_icon)
    ImageView imgshareicon;
    @BindView(R.id.img_cancel)
    ImageView img_cancel;
    @BindView(R.id.img_menu)
    ImageView img_menu;
    @BindView(R.id.img_help)
    ImageView img_help;
    @BindView(R.id.actionbar)
    RelativeLayout actionbar;
    @BindView(R.id.fragment_container)
    FrameLayout fragment_container;

    private bottombarfragment fragbottombar;
    private readermedialist fragreadermedialist;
    private fragmentmedialist fragvideolist;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        applicationviavideocomposer.setActivity(homeactivity.this);

        if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_reader))
        {
            /*fragreadermedialist=new readermedialist();
            replaceFragment(fragreadermedialist,false,true);*/

            bottombarrederfrag bottombarrederfrag = new bottombarrederfrag();
            replaceFragment(bottombarrederfrag, false, true);
        }
        else
        {
            fragvideolist=new fragmentmedialist();
            replaceFragment(fragvideolist, false, true);
        }

        imgaddicon.setOnClickListener(this);
        imgsettingsicon.setOnClickListener(this);
        img_back.setOnClickListener(this);
        imguploadicon.setOnClickListener(this);
        img_cancel.setOnClickListener(this);
        imgshareicon.setOnClickListener(this);
        img_menu.setOnClickListener(this);

        actionbar.post(new Runnable() {
            @Override
            public void run() {
                xdata.getinstance().saveSetting("actionbarheight",""+actionbar.getHeight());
            }
        });

        callservice mService = new callservice();
        Intent mIntent = new Intent(homeactivity.this, callservice.class);

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
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
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
        if((getcurrentfragment() instanceof  videocomposerfragment || getcurrentfragment() instanceof  audiocomposerfragment))
        {
            txt_title.setText(txt);
        }
        else if( getcurrentfragment() instanceof fragmentsettings ||
                getcurrentfragment() instanceof imagecomposerfragment
                || getcurrentfragment() instanceof videoplayfragment
                || getcurrentfragment() instanceof videoreaderfragment
                || getcurrentfragment() instanceof composervideoplayerfragment
                || getcurrentfragment() instanceof fragmentmedialist || getcurrentfragment() instanceof bottombarfragment
                || getcurrentfragment() instanceof bottombarrederfrag || getcurrentfragment() instanceof audioreaderfragment
                || getcurrentfragment() instanceof imagereaderfragment || getcurrentfragment() instanceof readermedialist
                )
        {
            txt_title.setText("");
        }
    }
//(getcurrentfragment() instanceof fragmentmedialist)
    @Override
    public void updateactionbar(int showHide, int color) {
        actionbar.setBackgroundColor(color);
        getWindow().setStatusBarColor(color);
    }

    @Override
    public void updateactionbar(int showHide) {
        if(showHide == 0)
        {
            actionbar.setVisibility(View.GONE);
        }
        else
        {
            actionbar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void registerUsageSystem() {

    }

    @Override
    public void registerUsageIow() {

    }

    @Override
    public void registerUsageIrq() {

    }


    @Override
    public void onfragmentbackstackchanged() {
        super.onfragmentbackstackchanged();
        basefragment fragment = getcurrentfragment();
        img_back.setVisibility(View.GONE);
        img_cancel.setVisibility(View.GONE);
        img_menu.setVisibility(View.GONE);
        img_help.setVisibility(View.GONE);
        actionbar.setVisibility(View.VISIBLE);
        imgshareicon.setVisibility(View.GONE);
        imgaddicon.setVisibility(View.GONE);
        imguploadicon.setVisibility(View.GONE);
        imgsettingsicon.setVisibility(View.GONE);
        updateheader("");

        {
            RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            params.addRule(RelativeLayout.BELOW, R.id.actionbar);
            fragment_container.setLayoutParams(params);
        }
         if (fragment instanceof videocomposerfragment) {
            img_menu.setVisibility(View.VISIBLE);
            img_help.setVisibility(View.VISIBLE);
            updateactionbar(1,applicationviavideocomposer.getactivity().getResources().getColor(R.color.actionbar_solid_normal));
            RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            fragment_container.setLayoutParams(params);

        }
        else if(fragment instanceof fragmentsettings){
            img_back.setVisibility(View.VISIBLE);
            img_cancel.setVisibility(View.VISIBLE);
            updateactionbar(1,applicationviavideocomposer.getactivity().getResources().getColor(R.color.actionbar_solid));

        }
        else if(fragment instanceof videoplayfragment){
            img_menu.setVisibility(View.VISIBLE);
            imgshareicon.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            fragment_container.setLayoutParams(params);
        }
        else if(fragment instanceof videoreaderfragment){
            imgsettingsicon.setVisibility(View.VISIBLE);
            img_menu.setVisibility(View.VISIBLE);
            imgaddicon.setVisibility(View.VISIBLE);
            //imgshareicon.setVisibility(View.VISIBLE);
            imgsettingsicon.setEnabled(true);
            updateactionbar(1,applicationviavideocomposer.getactivity().getResources().getColor(R.color.actionbar_solid));
            RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            fragment_container.setLayoutParams(params);
        }
         else if(fragment instanceof fragmentmedialist){
             imgaddicon.setVisibility(View.VISIBLE);
             imgsettingsicon.setVisibility(View.VISIBLE);
             imguploadicon.setVisibility(View.GONE);
             imgsettingsicon.setEnabled(true);
             updateactionbar(1,applicationviavideocomposer.getactivity().getResources().getColor(R.color.actionbar_solid));

             RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                     RelativeLayout.LayoutParams.MATCH_PARENT);
             fragment_container.setLayoutParams(params);
         }
        else if(fragment instanceof composervideoplayerfragment){
            imgshareicon.setVisibility(View.VISIBLE);
            img_menu.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            fragment_container.setLayoutParams(params);

        }
         else if(fragment instanceof imagecomposerfragment){
             img_menu.setVisibility(View.VISIBLE);
             img_help.setVisibility(View.VISIBLE);
             updateactionbar(1,applicationviavideocomposer.getactivity().getResources().getColor(R.color.actionbar_solid_normal));
             RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                     RelativeLayout.LayoutParams.MATCH_PARENT);
             fragment_container.setLayoutParams(params);

         }
         else if(fragment instanceof audiocomposerfragment){
             img_menu.setVisibility(View.VISIBLE);
             img_help.setVisibility(View.VISIBLE);
             updateactionbar(1,applicationviavideocomposer.getactivity().getResources().getColor(R.color.actionbar_solid_normal));
             RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                     RelativeLayout.LayoutParams.MATCH_PARENT);
             fragment_container.setLayoutParams(params);
         }else if(fragment instanceof audioreaderfragment){
             if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_reader))
             {
                 imgsettingsicon.setVisibility(View.VISIBLE);
                 img_menu.setVisibility(View.VISIBLE);
                 imgshareicon.setVisibility(View.VISIBLE);
                 imgsettingsicon.setEnabled(true);
                 updateactionbar(1,applicationviavideocomposer.getactivity().getResources().getColor(R.color.actionbar_solid));
                 RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                         RelativeLayout.LayoutParams.MATCH_PARENT);
                 fragment_container.setLayoutParams(params);
             }else{
                 imgshareicon.setVisibility(View.VISIBLE);
                 img_menu.setVisibility(View.VISIBLE);
                 RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                         RelativeLayout.LayoutParams.MATCH_PARENT);
                 fragment_container.setLayoutParams(params);
             }
         } else if(fragment instanceof imagereaderfragment){
             if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_reader))
             {
                 imgsettingsicon.setVisibility(View.VISIBLE);
                 img_menu.setVisibility(View.VISIBLE);
                 imgshareicon.setVisibility(View.VISIBLE);
                 imgsettingsicon.setEnabled(true);
                 updateactionbar(1,applicationviavideocomposer.getactivity().getResources().getColor(R.color.actionbar_solid));
                 RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                         RelativeLayout.LayoutParams.MATCH_PARENT);
                 fragment_container.setLayoutParams(params);
             }else{
                 img_menu.setVisibility(View.VISIBLE);
                 imgshareicon.setVisibility(View.VISIBLE);
                 imgsettingsicon.setEnabled(true);
                 updateactionbar(1,applicationviavideocomposer.getactivity().getResources().getColor(R.color.actionbar_solid));
                 RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                         RelativeLayout.LayoutParams.MATCH_PARENT);
                 fragment_container.setLayoutParams(params);
             }

         }
         else if(fragment instanceof bottombarrederfrag){
             updateactionbar(1,applicationviavideocomposer.getactivity().getResources().getColor(R.color.actionbar_solid));
             RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                     RelativeLayout.LayoutParams.MATCH_PARENT);
             fragment_container.setLayoutParams(params);
         }
         else if(fragment instanceof readermedialist){
             img_menu.setVisibility(View.VISIBLE);
             imgsettingsicon.setVisibility(View.VISIBLE);
             imguploadicon.setVisibility(View.GONE);
             imgsettingsicon.setEnabled(true);
             updateactionbar(1,applicationviavideocomposer.getactivity().getResources().getColor(R.color.actionbar_solid));

             RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                     RelativeLayout.LayoutParams.MATCH_PARENT);
             fragment_container.setLayoutParams(params);
         }else if(fragment instanceof bottombarfragment){

         }
        else
        {
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                getcurrentfragment().onHeaderBtnClick(R.id.img_back);
                break;
            case R.id.img_cancel:
                getcurrentfragment().onHeaderBtnClick(R.id.img_cancel);
                break;
            case R.id.img_share_icon:
                getcurrentfragment().onHeaderBtnClick(R.id.img_share_icon);
                break;
            case R.id.img_add_icon:
                getcurrentfragment().onHeaderBtnClick(R.id.img_add_icon);
                break;
            case R.id.img_setting:
                getcurrentfragment().onHeaderBtnClick(R.id.img_setting);
                break;
            case R.id.img_upload_icon:
                getcurrentfragment().onHeaderBtnClick(R.id.img_upload_icon);
                break;
            case R.id.img_menu:
                getcurrentfragment().onHeaderBtnClick(R.id.img_menu);
                break;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
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
