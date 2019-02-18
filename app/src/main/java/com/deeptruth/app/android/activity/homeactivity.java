package com.deeptruth.app.android.activity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.fragments.audiocomposerfragment;
import com.deeptruth.app.android.fragments.audioreaderfragment;
import com.deeptruth.app.android.fragments.basefragment;
import com.deeptruth.app.android.fragments.composeoptionspagerfragment;
import com.deeptruth.app.android.fragments.fragmentgraphicaldrawer;
import com.deeptruth.app.android.fragments.fragmentmedialist;
import com.deeptruth.app.android.fragments.framemetricssettings;
import com.deeptruth.app.android.fragments.imagecomposerfragment;
import com.deeptruth.app.android.fragments.imagereaderfragment;
import com.deeptruth.app.android.fragments.myfolderfragment;
import com.deeptruth.app.android.fragments.settingfragment;
import com.deeptruth.app.android.fragments.videoreaderfragment;
import com.deeptruth.app.android.fragments.videocomposerfragment;
import com.deeptruth.app.android.fragments.videoplayfragment;
import com.deeptruth.app.android.services.callservice;
import com.deeptruth.app.android.utils.appdialog;
import com.deeptruth.app.android.utils.circularImageview;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.xdata;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class homeactivity extends locationawareactivity implements View.OnClickListener, View.OnTouchListener {

    @BindView(R.id.fragment_container)
    FrameLayout fragment_container;
    @BindView(R.id.img_lefthandle)
    ImageView imglefthandle;
    @BindView(R.id.img_righthandle)
    ImageView imgrighthandle;

    @BindView(R.id.rootview)
    RelativeLayout rootview;

    @BindView(R.id.drawer_layout)
    DrawerLayout navigationdrawer;
    private ActionBarDrawerToggle drawertoggle;
    fragmentgraphicaldrawer graphicaldrawerfragment;
    private Handler myhandler;
    private Runnable myrunnable;

    RelativeLayout layoutbottom;
    LinearLayout layoutheader;
    circularImageview playpausebutton;
    RelativeLayout layoutcustomcontroller;
    ImageView img_fullscreen;
    Date lastdateselection=null;
    boolean isviewtouched=false;

    private fragmentmedialist fragvideolist;
    @SuppressLint("RestrictedApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        applicationviavideocomposer.setActivity(homeactivity.this);
        config.selectedmediatype=0;
        xdata.getinstance().saveSetting(config.selected_folder,config.dirallmedia);
        fragvideolist=new fragmentmedialist();
        fragvideolist.shouldlaunchcomposer(true);
        replaceFragment(fragvideolist, false, true);

        drawertoggle = new ActionBarDrawerToggle(
                this, navigationdrawer, R.string.drawer_open, R.string.drawer_close){

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);

                getcurrentfragment().showhideviewondrawer(false);

              if(getcurrentfragment() instanceof fragmentmedialist){
                  imglefthandle.setVisibility(View.GONE);
              }else{
                  imglefthandle.setVisibility(View.VISIBLE);
                  imgrighthandle.setVisibility(View.GONE);
              }
            }
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                imglefthandle.setVisibility(View.GONE);
                imgrighthandle.setVisibility(View.VISIBLE);

                getcurrentfragment().showhideviewondrawer(true);

            }
        };
        navigationdrawer.addDrawerListener(drawertoggle);
        drawertoggle.syncState();
        navigationdrawer.setScrimColor(getResources().getColor(android.R.color.transparent));

        imglefthandle.setOnClickListener(this);
        imgrighthandle.setOnClickListener(this);
        fragment_container.setOnTouchListener(this);

        if(graphicaldrawerfragment == null)
        {
            //fragmentgraphic  = new graphicalfragment();
            graphicaldrawerfragment =new fragmentgraphicaldrawer();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_graphic_drawer_container,graphicaldrawerfragment);
            transaction.commit();
        }

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
        }, 100);



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
        if(imglefthandle.getVisibility()==View.GONE && navigationdrawer.isDrawerOpen(GravityCompat.START)){
            navigationdrawer.closeDrawers();
            imgrighthandle.setVisibility(View.GONE);
            imglefthandle.setVisibility(View.GONE);
        }
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
    public void updateactionbar(int showHide, int color) {
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
    public void updateactionbar(int fullscreenmode) {
        if(fullscreenmode == 0)   // Enable full screen mode
        {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE
                            // Set the content to appear under the system bars so that the
                            // content doesn't resize when the system bars hide and show.
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            // Hide the nav bar and status bar
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    rootview.setPadding(0,0,0,0);
                }
            },100);

//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
  //          getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR,WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR);
        }
        else        // Disable full screen mode
        {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    int statusbarheight=Integer.parseInt(xdata.getinstance().getSetting("statusbarheight"));
                    rootview.setPadding(0,statusbarheight,0,0);
                }
            },800);

          //  getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
          //  getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
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
        imglefthandle.setVisibility(View.GONE);
        imgrighthandle.setVisibility(View.GONE);
        updateheader("");

        {
            RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            params.addRule(RelativeLayout.BELOW, R.id.actionbar);
            fragment_container.setLayoutParams(params);
        }
         if (fragment instanceof videocomposerfragment) {
             drawerenabledisable(true);
             updateactionbar(0);
         }
        else if(fragment instanceof framemetricssettings){

        }
        else if(fragment instanceof videoreaderfragment){

            try
            {
                View view=((videoreaderfragment) getcurrentfragment()).layout_photodetails;
                if(view != null)
                {
                    if(view.getVisibility() == View.VISIBLE)
                    {
                        Log.e("viewgone","gone");
                    }
                    else
                    {
                        imglefthandle.setVisibility(View.VISIBLE);
                     Log.e("visiblity","visiblity");
                    }
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }

             updateactionbar(1);

         }
         else if(fragment instanceof fragmentmedialist){
             updateactionbar(1);
         }
         else if(fragment instanceof imagecomposerfragment){
             drawerenabledisable(true);
             updateactionbar(0);
         }
         else if(fragment instanceof audiocomposerfragment){
             drawerenabledisable(true);
             updateactionbar(0);
         }
         else if(fragment instanceof audioreaderfragment){
             updateactionbar(1);
         } else if(fragment instanceof imagereaderfragment){

             try
             {
                 View view=((imagereaderfragment) getcurrentfragment()).layout_photodetails;
                 if(view != null)
                 {
                     if(view.getVisibility() == View.VISIBLE)
                     {
                         Log.e("viewgone","gone");
                     }
                     else
                     {
                         imglefthandle.setVisibility(View.VISIBLE);
                         Log.e("visiblity","visiblity");
                     }
                 }
             }catch (Exception e)
             {
                 e.printStackTrace();
             }
             updateactionbar(1);

         }
         else if(fragment instanceof settingfragment)
         {
             updateactionbar(1);
         }
         else if(fragment instanceof composeoptionspagerfragment)
         {

         }
         else if(fragment instanceof myfolderfragment)
         {

         }
        else
        {
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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


    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                isviewtouched = true;
                lastdateselection = new Date();
                Log.d("user touch","on touch" + isviewtouched);
                break;

            case MotionEvent.ACTION_UP:
                isviewtouched = false;
                Log.d("on touch end ","on touch end" + isviewtouched);
                break;
        }
        return false;
    }
}