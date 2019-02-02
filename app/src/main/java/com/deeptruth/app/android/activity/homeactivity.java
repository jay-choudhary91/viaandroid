package com.deeptruth.app.android.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.deeptruth.app.android.BuildConfig;
import com.deeptruth.app.android.R;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.fragments.audiocomposerfragment;
import com.deeptruth.app.android.fragments.audioreaderfragment;
import com.deeptruth.app.android.fragments.basefragment;
import com.deeptruth.app.android.fragments.bottombarfragment;
import com.deeptruth.app.android.fragments.bottombarrederfrag;
import com.deeptruth.app.android.fragments.composeoptionspagerfragment;
import com.deeptruth.app.android.fragments.fragmentgraphicaldrawer;
import com.deeptruth.app.android.fragments.fragmentmedialist;
import com.deeptruth.app.android.fragments.framemetricssettings;
import com.deeptruth.app.android.fragments.imagecomposerfragment;
import com.deeptruth.app.android.fragments.imagereaderfragment;
import com.deeptruth.app.android.fragments.medialistreader;
import com.deeptruth.app.android.fragments.myfolderfragment;
import com.deeptruth.app.android.fragments.settingfragment;
import com.deeptruth.app.android.fragments.videoreaderfragment;
import com.deeptruth.app.android.fragments.videocomposerfragment;
import com.deeptruth.app.android.fragments.videoplayfragment;
import com.deeptruth.app.android.services.callservice;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.xdata;

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
    @BindView(R.id.img_backarrow)
    ImageView img_backbtn;
    @BindView(R.id.img_lefthandle)
    ImageView imglefthandle;
    @BindView(R.id.img_righthandle)
    ImageView imgrighthandle;

    @BindView(R.id.drawer_layout)
    DrawerLayout navigationdrawer;
    private ActionBarDrawerToggle drawertoggle;
    fragmentgraphicaldrawer graphicaldrawerfragment;
    private Handler myhandler;
    private Runnable myrunnable;

    private fragmentmedialist fragvideolist;
    private medialistreader fragmedialistreader;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        applicationviavideocomposer.setActivity(homeactivity.this);

        config.selectedmediatype=0;
        xdata.getinstance().saveSetting(config.selected_folder,config.dirallmedia);
        if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_reader))
        {
            fragmedialistreader=new medialistreader();
            replaceFragment(fragmedialistreader, false, true);
        }
        else
        {
            fragvideolist=new fragmentmedialist();
            replaceFragment(fragvideolist, false, true);
        }

        drawertoggle = new ActionBarDrawerToggle(
                this, navigationdrawer, R.string.drawer_open, R.string.drawer_close){

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                imglefthandle.setVisibility(View.VISIBLE);
                imgrighthandle.setVisibility(View.GONE);
                // layout_bottom.setVisibility(View.VISIBLE);
            }
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                imglefthandle.setVisibility(View.GONE);
                imgrighthandle.setVisibility(View.VISIBLE);
            }
        };
        navigationdrawer.addDrawerListener(drawertoggle);
        drawertoggle.syncState();
        navigationdrawer.setScrimColor(getResources().getColor(android.R.color.transparent));

        imgaddicon.setOnClickListener(this);
        imgsettingsicon.setOnClickListener(this);
        img_back.setOnClickListener(this);
        imguploadicon.setOnClickListener(this);
        img_cancel.setOnClickListener(this);
        imgshareicon.setOnClickListener(this);
        img_menu.setOnClickListener(this);
        img_help.setOnClickListener(this);
        img_backbtn.setOnClickListener(this);
        imglefthandle.setOnClickListener(this);
        imgrighthandle.setOnClickListener(this);

        if(graphicaldrawerfragment == null)
        {
            //fragmentgraphic  = new graphicalfragment();
            graphicaldrawerfragment =new fragmentgraphicaldrawer();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_graphic_drawer_container,graphicaldrawerfragment);
            transaction.commit();
        }

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


        new Handler().postDelayed(new Runnable()
        {
            public void run()
            {
                setdrawerdata();
            }
        }, 2000);



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
        if((getcurrentfragment() instanceof  videocomposerfragment || getcurrentfragment() instanceof  audiocomposerfragment  || getcurrentfragment() instanceof settingfragment))
        {
            txt_title.setText(txt);
        }
        else if( getcurrentfragment() instanceof framemetricssettings ||
                getcurrentfragment() instanceof imagecomposerfragment
                || getcurrentfragment() instanceof videoplayfragment
                || getcurrentfragment() instanceof videoreaderfragment
                || getcurrentfragment() instanceof fragmentmedialist || getcurrentfragment() instanceof bottombarfragment
                || getcurrentfragment() instanceof bottombarrederfrag || getcurrentfragment() instanceof audioreaderfragment
                )
        {
            txt_title.setText("");
        }
    }
//(getcurrentfragment() instanceof fragmentmedialist)
@Override
public void updateactionbar(int showHide, int color) {
    /*if(showHide == 0){ View decorView = getWindow().getDecorView();
        // show the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN; //hide status bar
        decorView.setSystemUiVisibility(uiOptions);
    }else{
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE; // show the status bar.
        decorView.setSystemUiVisibility(uiOptions);
    }*/
        actionbar.setBackgroundColor(color);
        getWindow().setStatusBarColor(color);
}

    @Override
    public void drawerenabledisable(boolean isenable) {
        if(isenable){
            navigationdrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            imglefthandle.setVisibility(View.VISIBLE);

        }else{
            navigationdrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            imglefthandle.setVisibility(View.GONE);
        }
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
        common.resetgraphicaldata();
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
        img_backbtn.setVisibility(View.GONE);
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
            actionbar.setVisibility(View.GONE);
            updateactionbar(1,applicationviavideocomposer.getactivity().getResources().getColor(R.color.dark_blue_solid));
            RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            fragment_container.setLayoutParams(params);
        }
        else if(fragment instanceof framemetricssettings){
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
            imgsettingsicon.setVisibility(View.GONE);
            img_menu.setVisibility(View.VISIBLE);
            imgaddicon.setVisibility(View.GONE);
            imgshareicon.setVisibility(View.VISIBLE);
             actionbar.setVisibility(View.GONE);
            imgsettingsicon.setEnabled(true);
            updateactionbar(1,applicationviavideocomposer.getactivity().getResources().getColor(R.color.dark_blue_solid));
            RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            fragment_container.setLayoutParams(params);
        }
         else if(fragment instanceof fragmentmedialist){
             imgaddicon.setVisibility(View.VISIBLE);
             imgsettingsicon.setVisibility(View.VISIBLE);
             imguploadicon.setVisibility(View.GONE);
             actionbar.setVisibility(View.GONE);
             imgsettingsicon.setEnabled(true);
             updateactionbar(1,applicationviavideocomposer.getactivity().getResources().getColor(R.color.dark_blue_solid));

             RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                     RelativeLayout.LayoutParams.MATCH_PARENT);
             fragment_container.setLayoutParams(params);
         }
         else if(fragment instanceof medialistreader){
             imgaddicon.setVisibility(View.VISIBLE);
             imgsettingsicon.setVisibility(View.VISIBLE);
             imguploadicon.setVisibility(View.GONE);
             actionbar.setVisibility(View.GONE);
             imgsettingsicon.setEnabled(true);
             updateactionbar(1,applicationviavideocomposer.getactivity().getResources().getColor(R.color.dark_blue_solid));

             drawerenabledisable(false);
             RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                     RelativeLayout.LayoutParams.MATCH_PARENT);
             fragment_container.setLayoutParams(params);
         }
         else if(fragment instanceof imagecomposerfragment){
             img_menu.setVisibility(View.VISIBLE);
             img_help.setVisibility(View.VISIBLE);
             actionbar.setVisibility(View.GONE);
             updateactionbar(0,applicationviavideocomposer.getactivity().getResources().getColor(R.color.dark_blue_solid));
             RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                     RelativeLayout.LayoutParams.MATCH_PARENT);
             fragment_container.setLayoutParams(params);

         }
         else if(fragment instanceof audiocomposerfragment){
             img_menu.setVisibility(View.VISIBLE);
             img_help.setVisibility(View.VISIBLE);
             actionbar.setVisibility(View.GONE);
             updateactionbar(1,applicationviavideocomposer.getactivity().getResources().getColor(R.color.dark_blue_solid));
             RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                     RelativeLayout.LayoutParams.MATCH_PARENT);
             fragment_container.setLayoutParams(params);
         }else if(fragment instanceof audioreaderfragment){
             actionbar.setVisibility(View.GONE);
             imgshareicon.setVisibility(View.VISIBLE);
             img_menu.setVisibility(View.VISIBLE);
             RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                     RelativeLayout.LayoutParams.MATCH_PARENT);
             fragment_container.setLayoutParams(params);
         } else if(fragment instanceof imagereaderfragment){
             actionbar.setVisibility(View.GONE);
             img_menu.setVisibility(View.VISIBLE);
             imgshareicon.setVisibility(View.VISIBLE);
             imgsettingsicon.setEnabled(true);
             updateactionbar(1,applicationviavideocomposer.getactivity().getResources().getColor(R.color.dark_blue_solid));
             RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                     RelativeLayout.LayoutParams.MATCH_PARENT);
             fragment_container.setLayoutParams(params);

         }
         else if(fragment instanceof bottombarrederfrag){
             updateactionbar(1,applicationviavideocomposer.getactivity().getResources().getColor(R.color.dark_blue_solid));
             RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                     RelativeLayout.LayoutParams.MATCH_PARENT);
             fragment_container.setLayoutParams(params);
         }else if(fragment instanceof settingfragment){
             img_menu.setVisibility(View.GONE);
             actionbar.setVisibility(View.GONE);
             imgsettingsicon.setVisibility(View.GONE);
             imguploadicon.setVisibility(View.GONE);
             img_backbtn.setVisibility(View.VISIBLE);
             imgsettingsicon.setEnabled(true);
             updateactionbar(1,applicationviavideocomposer.getactivity().getResources().getColor(R.color.dark_blue_solid));

             RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                     RelativeLayout.LayoutParams.MATCH_PARENT);
             fragment_container.setLayoutParams(params);
         }else if(fragment instanceof bottombarfragment){
             actionbar.setVisibility(View.GONE);
         }
         else if(fragment instanceof composeoptionspagerfragment){
             updateactionbar(0,applicationviavideocomposer.getactivity().getResources().getColor(R.color.dark_blue_solid));

             actionbar.setVisibility(View.GONE);
         }
         else if(fragment instanceof myfolderfragment){
             actionbar.setVisibility(View.GONE);
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
              //  imgshareicon.setEnabled(false);
                imgshareicon.setClickable(false);
                new Handler().postDelayed(new Runnable()
                {
                    public void run()
                    {
                        imgshareicon.setClickable(true);
                    }
                }, 150);

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
            case R.id.img_help:
                getcurrentfragment().onHeaderBtnClick(R.id.img_help);
                break;
            case R.id.img_backarrow:
                getcurrentfragment().onHeaderBtnClick(R.id.img_backarrow);
                break;
            case R.id.img_lefthandle:
                navigationdrawer.openDrawer(Gravity.START);
                break;

            case R.id.img_righthandle:
                navigationdrawer.closeDrawers();
                break;
        }
    }

    /*@Override
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
    }*/


    public void setdrawerdata(){

        if(myhandler != null && myhandler != null)
            myhandler.removeCallbacks(myrunnable);

        myhandler =new Handler();
        myrunnable = new Runnable() {
            @Override
            public void run() {
                if((graphicaldrawerfragment!= null))
                    graphicaldrawerfragment.setmetricesdata();

                myhandler.postDelayed(this, 5000);
            }
        };
        myhandler.post(myrunnable);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(myhandler != null && myhandler != null)
            myhandler.removeCallbacks(myrunnable);

    }
}