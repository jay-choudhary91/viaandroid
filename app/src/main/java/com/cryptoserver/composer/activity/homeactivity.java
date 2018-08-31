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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cryptoserver.composer.BuildConfig;
import com.cryptoserver.composer.R;
import com.cryptoserver.composer.applicationviavideocomposer;
import com.cryptoserver.composer.fragments.basefragment;
import com.cryptoserver.composer.fragments.fragmentsettings;
import com.cryptoserver.composer.fragments.fragmentvideolist;
import com.cryptoserver.composer.fragments.videoplayercomposerfragment;
import com.cryptoserver.composer.fragments.videoplayerreaderfragment;
import com.cryptoserver.composer.fragments.videocomposerfragment;
import com.cryptoserver.composer.fragments.videoplayfragment;
import com.cryptoserver.composer.services.callservice;
import com.cryptoserver.composer.utils.config;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        applicationviavideocomposer.setActivity(homeactivity.this);

        if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_reader))
        {
            videoplayerreaderfragment frag=new videoplayerreaderfragment();
            replaceFragment(frag, false, true);
        }
        else
        {
            videocomposerfragment frag=new videocomposerfragment();
            frag.setData(true);
            replaceFragment(frag, false, true);
            /*Intent in=new Intent(homeactivity.this,CameraActivity.class);
            startActivity(in);*/
        }

        imgaddicon.setOnClickListener(this);
        imgsettingsicon.setOnClickListener(this);
        img_back.setOnClickListener(this);
        imguploadicon.setOnClickListener(this);
        img_cancel.setOnClickListener(this);
        imgshareicon.setOnClickListener(this);
        img_menu.setOnClickListener(this);

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
    public int getlayoutid() {
        return R.layout.activity_homeactivity;
    }
    @Override

    public void launchHome() {

    }

    @Override
    public void updateheader(String txt) {
        if((getcurrentfragment() instanceof  videocomposerfragment))
        {
            txt_title.setText(txt);
        }
        else if((getcurrentfragment() instanceof fragmentvideolist) || getcurrentfragment() instanceof fragmentsettings
                || getcurrentfragment() instanceof videoplayfragment
                || getcurrentfragment() instanceof videoplayerreaderfragment || getcurrentfragment() instanceof videoplayercomposerfragment)
        {
            txt_title.setText("");
        }
    }

    @Override
    public void updateActionBar(int showHide, String color) {

    }

    @Override
    public void updateActionBar(int showHide) {
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
    public void onfragmentbackstackchanged() {
        super.onfragmentbackstackchanged();
        basefragment fragment = getcurrentfragment();
        img_back.setVisibility(View.GONE);
        img_cancel.setVisibility(View.GONE);
        img_menu.setVisibility(View.GONE);
        img_help.setVisibility(View.GONE);
        actionbar.setVisibility(View.VISIBLE);

        if (fragment instanceof fragmentvideolist) {
            imgaddicon.setVisibility(View.VISIBLE);
            imgsettingsicon.setVisibility(View.VISIBLE);
            imguploadicon.setVisibility(View.VISIBLE);
            imgsettingsicon.setEnabled(true);
            imgshareicon.setVisibility(View.GONE);
            updateheader("");
        }
        else if (fragment instanceof videocomposerfragment) {
            imgaddicon.setVisibility(View.GONE);
            imgsettingsicon.setVisibility(View.GONE);
            imguploadicon.setVisibility(View.GONE);
            img_menu.setVisibility(View.VISIBLE);
            img_help.setVisibility(View.VISIBLE);
            imgshareicon.setVisibility(View.GONE);

        }
        else if(fragment instanceof fragmentsettings){
            img_back.setVisibility(View.VISIBLE);
            img_cancel.setVisibility(View.VISIBLE);
            imgaddicon.setVisibility(View.GONE);
            imgsettingsicon.setVisibility(View.GONE);
            imguploadicon.setVisibility(View.GONE);
            imgshareicon.setVisibility(View.GONE);
            updateheader("");

        }
        else if(fragment instanceof videoplayfragment){
            img_back.setVisibility(View.GONE);
            img_cancel.setVisibility(View.GONE);
            imgaddicon.setVisibility(View.GONE);
            imgsettingsicon.setVisibility(View.GONE);
            imguploadicon.setVisibility(View.GONE);
            img_menu.setVisibility(View.VISIBLE);
            imgshareicon.setVisibility(View.VISIBLE);
            updateheader("");

        }
        else if(fragment instanceof videoplayerreaderfragment){
            img_back.setVisibility(View.GONE);
            img_cancel.setVisibility(View.GONE);
            imgaddicon.setVisibility(View.GONE);
            imgsettingsicon.setVisibility(View.VISIBLE);
            imguploadicon.setVisibility(View.GONE);
            img_menu.setVisibility(View.VISIBLE);
            imgshareicon.setVisibility(View.VISIBLE);
            updateheader("");
            imgsettingsicon.setEnabled(true);

        }else if(fragment instanceof videoplayercomposerfragment){
            img_back.setVisibility(View.GONE);
            img_cancel.setVisibility(View.GONE);
            imgaddicon.setVisibility(View.GONE);
            imgsettingsicon.setVisibility(View.GONE);
            imguploadicon.setVisibility(View.GONE);
            imgshareicon.setVisibility(View.VISIBLE);
            updateheader("");
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
            {
                videocomposerfragment fragment=new videocomposerfragment();
                addFragment(fragment, false, true);
            }
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
