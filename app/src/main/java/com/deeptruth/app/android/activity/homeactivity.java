package com.deeptruth.app.android.activity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
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
import android.widget.ScrollView;

import com.deeptruth.app.android.BuildConfig;
import com.deeptruth.app.android.R;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.database.databasemanager;
import com.deeptruth.app.android.fragments.appmanagementfragment;
import com.deeptruth.app.android.fragments.audiocomposerfragment;
import com.deeptruth.app.android.fragments.audioreaderfragment;
import com.deeptruth.app.android.fragments.basefragment;
import com.deeptruth.app.android.fragments.composeoptionspagerfragment;
import com.deeptruth.app.android.fragments.fetchsettingvaluefragment;
import com.deeptruth.app.android.fragments.fetchxapivaluefragment;
import com.deeptruth.app.android.fragments.fragment_xapi_detail;
import com.deeptruth.app.android.fragments.fragmentgraphicaldrawer;
import com.deeptruth.app.android.fragments.fragmentmedialist;
import com.deeptruth.app.android.fragments.framemetricssettings;
import com.deeptruth.app.android.fragments.imagecomposerfragment;
import com.deeptruth.app.android.fragments.imagereaderfragment;
import com.deeptruth.app.android.fragments.inapppurchasecontrollerfragment;
import com.deeptruth.app.android.fragments.myfolderfragment;
import com.deeptruth.app.android.fragments.settingfragment;
import com.deeptruth.app.android.fragments.synclogdetailsfragment;
import com.deeptruth.app.android.fragments.synclogfragment;
import com.deeptruth.app.android.fragments.videocomposerfragment;
import com.deeptruth.app.android.fragments.videoreaderfragment;
import com.deeptruth.app.android.interfaces.apiresponselistener;
import com.deeptruth.app.android.services.callservice;
import com.deeptruth.app.android.utils.circularImageview;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.progressdialog;
import com.deeptruth.app.android.utils.taskresult;
import com.deeptruth.app.android.utils.xdata;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class homeactivity extends locationawareactivity implements View.OnClickListener, View.OnTouchListener {

    @BindView(R.id.fragment_container)
    FrameLayout fragment_container;
    @BindView(R.id.navigation_drawer)
    FrameLayout navigation_drawer;
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

    Date lastdateselection=null;
    boolean isviewtouched=false;
    //callservice phonecallservice;
    private fragmentmedialist fragmedialist;
    boolean isdraweropen=false ,isdrawerrunning = false;
    int rootviewheight,navigationbarheight, finalheight ,imageheight;


    @SuppressLint("RestrictedApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        ButterKnife.bind(this);
        applicationviavideocomposer.setActivity(homeactivity.this);
        config.selectedmediatype=1;
        xdata.getinstance().saveSetting(config.selected_folder,config.dirallmedia);

        // Code for deeplink
        /*Uri data = getIntent().getData();
        if (data != null){

            try
            {
                String scheme = data.toString();
                String url =scheme.toString();
                int indexIdStart=url.lastIndexOf("/");
                int indexPreFixStart=url.indexOf("/",8);
                String Id = url.substring(indexIdStart+1,url.length());
                String Prefix = url.substring(indexPreFixStart+1,indexIdStart);

                Id=Id.replace(".","");
                if(Prefix.equalsIgnoreCase("s"))
                {
                    String a=Prefix;
                    String b=Prefix;
                }

            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }*/
        drawertoggle = new ActionBarDrawerToggle(this, navigationdrawer, R.string.drawer_open, R.string.drawer_close){

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);

                setdraweropen(false);
                getcurrentfragment().showhideviewondrawer(false);
                if(graphicaldrawerfragment != null)
                {
                    graphicaldrawerfragment.setdraweropen(false);
                    graphicaldrawerfragment.scrollview_meta.fullScroll(ScrollView.FOCUS_UP);
                }

                if(getcurrentfragment() instanceof fragmentmedialist){
                    imglefthandle.setVisibility(View.GONE);
                }else{
                    // imglefthandle.setVisibility(View.VISIBLE);
                    imgrighthandle.setVisibility(View.VISIBLE);
                    imgrighthandle.setImageResource(R.drawable.handle_right_arrow);
                    isdrawerrunning = false;
                }
            }
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                if(graphicaldrawerfragment != null)
                    graphicaldrawerfragment.setdraweropen(true);

                //graphicaldrawerfragment.scrollview_meta.pageScroll(ScrollView.FOCUS_UP);
                imglefthandle.setVisibility(View.GONE);
                imgrighthandle.setVisibility(View.VISIBLE);
                setdraweropen(true);
                getcurrentfragment().showhideviewondrawer(true);
                imgrighthandle.setImageResource(R.drawable.handle_left_arrow);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                if(slideOffset > 0.0)  // Moving to right
                {
                    isdrawerrunning = true;
                    imgrighthandle.setVisibility(View.VISIBLE);
                    imglefthandle.setVisibility(View.GONE);
                }
                else  // Moving to left
                {
                    isdrawerrunning = true;
                    imgrighthandle.setVisibility(View.GONE);
                    imglefthandle.setVisibility(View.VISIBLE);
                }
                super.onDrawerSlide(drawerView, slideOffset);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
                if (newState == DrawerLayout.STATE_SETTLING) {
                    if ( isdraweropen ==false) {
                        // starts opening
                        isdrawerrunning = true;
                        imglefthandle.setVisibility(View.GONE);
                        imgrighthandle.setVisibility(View.VISIBLE);
                        imgrighthandle.setImageResource(R.drawable.handle_right_arrow);
                        invalidateOptionsMenu();

                    } else {
                        isdrawerrunning = true;
                        imglefthandle.setVisibility(View.GONE);
                        imgrighthandle.setVisibility(View.VISIBLE);
                        imgrighthandle.setImageResource(R.drawable.handle_left_arrow);
                        invalidateOptionsMenu();

                        // closing drawer
                    }
                }
            }
        };

        navigationdrawer.addDrawerListener(drawertoggle);
        drawertoggle.syncState();
        navigationdrawer.setScrimColor(getResources().getColor(android.R.color.transparent));

        imglefthandle.setOnClickListener(this);
        imgrighthandle.setOnClickListener(this);
        fragment_container.setOnTouchListener(this);

        drawerenabledisable(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(BuildConfig.FLAVOR.contains(config.build_flavor_reader))
                        {
                            fragment_container.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.transparent));
                            fragmedialist =new fragmentmedialist();
                            fragmedialist.shouldlaunchcomposer(true);
                            replaceFragment(fragmedialist, false, true);
                        }
                        else if(BuildConfig.FLAVOR.contains(config.build_flavor_composer))
                        {
                            fragment_container.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.transparent));
                            launchcomposerfragment();
                        }

                        rootview.post(new Runnable() {
                            @Override
                            public void run() {
                                rootviewheight= rootview.getHeight();
                                navigationbarheight =  common.getnavigationbarheight();
                                finalheight= rootviewheight - navigationbarheight;
                                finalheight = finalheight/2;
                                drawerbutton();
                            }
                        });
                    }
                });
            }
        }).start();

        //detectphonecallservice();
        setdrawerdata();
        getupdatedmetadata();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(graphicaldrawerfragment == null)
                {
                        graphicaldrawerfragment =new fragmentgraphicaldrawer();
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.add(R.id.fragment_graphic_drawer_container,graphicaldrawerfragment);
                        transaction.commitAllowingStateLoss();
                }
            }
        },2000);
    }

    /*public void detectphonecallservice()
    {
        phonecallservice = new callservice();
        Intent intent = new Intent(homeactivity.this, callservice.class);
        if (!isMyServiceRunning(phonecallservice.getClass()))
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                startForegroundService(intent);
            else
                startService(intent);
        }
    }*/

    private boolean isMyServiceRunning(Class<?> serviceClass)
    {
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

        //showAlertDialog();

        /*if(! isactivitybecomefinish && (BuildConfig.FLAVOR.contains(config.build_flavor_composer)))
        {
            if (isMyServiceRunning(appbackgroundactionservice.class))
                stopService(new Intent(getBaseContext(), appbackgroundactionservice.class));

            if(needtoshowpopup)
            {
                needtoshowpopup=false;
                startService(new Intent(getBaseContext(), appbackgroundactionservice.class));
            }
        }*/

        /*if(phonecallservice != null)
        {
            Intent intent = new Intent(homeactivity.this, callservice.class);
            if (isMyServiceRunning(phonecallservice.getClass()))
                stopService(intent);
        }*/
    }

    public void showAlertDialog() {
        /** define onClickListener for dialog */
        DialogInterface.OnClickListener listener
                = new   DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // do some stuff eg: context.onCreate(super)
            }
        };

        /** create builder for dialog */
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(homeactivity.this,
                R.style.myDialog))
                .setCancelable(false)
                .setMessage("Message...")
                .setTitle("Title")
                .setPositiveButton("OK", listener);
        /** create dialog & set builder on it */
        Dialog dialog = builder.create();
        /** this required special permission but u can use aplication context */
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
        /** show dialog */
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RC_OVERLAY:
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    final boolean overlayEnabled = Settings.canDrawOverlays(this);
                }
                // Do something...
                break;
        }
    }*/

    @Override
    public int getlayoutid() {
        return R.layout.activity_homeactivity;
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
    public void hidedrawerbutton(boolean enable) {
        if(enable){
            imglefthandle.setVisibility(View.GONE);
        }else if((getcurrentfragment() instanceof videocomposerfragment || getcurrentfragment() instanceof imagecomposerfragment || getcurrentfragment() instanceof audiocomposerfragment) &&
                !navigationdrawer.isDrawerOpen(GravityCompat.START)){


            if(!isdrawerrunning)
                   imglefthandle.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void updateactionbar(int fullscreenmode) {
        if(fullscreenmode == 0)   // Enable full screen mode
        {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR,WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR);
        }
        else        // Disable full screen mode
        {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        }
    }

    @Override
    public void setdraweropen(boolean isdraweropen) {
        this.isdraweropen=isdraweropen;
    }

    @Override
    public boolean isdraweropened() {
        return isdraweropen;
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
        imglefthandle.setVisibility(View.GONE);
        imgrighthandle.setVisibility(View.GONE);
        updateheader("");
        setdrawerheightonfullscreen(0);

        {
            RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            params.addRule(RelativeLayout.BELOW, R.id.actionbar);
            fragment_container.setLayoutParams(params);
        }
         if (fragment instanceof videocomposerfragment) {
             updateactionbar(0);
             drawerenabledisable(true);
         }
        else if(fragment instanceof framemetricssettings){

        }
        else if(fragment instanceof videoreaderfragment){
             getstatusbarheight();
             common.resetgraphicaldata();
             setdatacomposing(false,xdata.getinstance().getSetting(config.selectedvideourl));
            try
            {
                View view=((videoreaderfragment) getcurrentfragment()).layout_videodetails;
                boolean islastdragarrow = ((videoreaderfragment) getcurrentfragment()).islastdragarrow;
                if(view != null)
                {
                    if(view.getVisibility() == View.VISIBLE && islastdragarrow == false)
                    {
                        Log.e("viewgone","gone");
                    }
                    else
                    {
                        imglefthandle.setVisibility(View.VISIBLE);
                        drawerenabledisable(true);

                        Log.e("visiblity","visiblity");
                    }
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }
             updateactionbar(1);
         }
         else if(fragment instanceof audioreaderfragment){
             common.resetgraphicaldata();
             try
             {
                 View view=((audioreaderfragment) getcurrentfragment()).layout_audiodetails;
                 boolean islastdragarrow = ((audioreaderfragment) getcurrentfragment()).islastdragarrow;
                 if(view != null)
                 {
                     if(view.getVisibility() == View.VISIBLE && islastdragarrow == false)
                     {
                         Log.e("viewgone","gone");
                     }
                     else
                     {
                         imglefthandle.setVisibility(View.VISIBLE);
                         drawerenabledisable(true);
                         Log.e("visiblity","visiblity");
                     }
                 }
             }catch (Exception e)
             {
                 e.printStackTrace();
             }
             updateactionbar(1);
             getstatusbarheight();
             setdatacomposing(false,xdata.getinstance().getSetting(config.selectedaudiourl));
         } else if(fragment instanceof imagereaderfragment){
             getstatusbarheight();
             common.resetgraphicaldata();
             setdatacomposing(false,xdata.getinstance().getSetting(config.selectedphotourl));
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
         else if(fragment instanceof fragmentmedialist){


        }
         else if(fragment instanceof imagecomposerfragment){
             drawerenabledisable(true);
             updateactionbar(0);
         }
         else if(fragment instanceof audiocomposerfragment){
             drawerenabledisable(true);
             updateactionbar(0);

         }
         else if(fragment instanceof settingfragment)
         {
             updateactionbar(1);
         }
         else if(fragment instanceof composeoptionspagerfragment)
         {
             setdatacomposing(true,"");
             common.resetgraphicaldata();
             updateactionbar(0);
         }
         else if(fragment instanceof myfolderfragment)
         {

         }/*else if(fragment instanceof fragmentrimvideo)
         {

         }*/else if(fragment instanceof appmanagementfragment)
         {

         }else if(fragment instanceof fetchsettingvaluefragment)
         {

         }else if(fragment instanceof fetchxapivaluefragment)
         {

         }else if(fragment instanceof fragment_xapi_detail || fragment instanceof synclogfragment || fragment instanceof synclogdetailsfragment)
         {

         }else if(fragment instanceof inapppurchasecontrollerfragment)
         {

         }
        else
        {
            finishactivity();
        }
    }

    @Override
    public void finishactivity() {
        applicationviavideocomposer.setisactivitybecomefinish(true);
        finish();
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


    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.activityfadein, R.anim.activityfadeout);
    }

    public void setdrawerdata(){

        if(myhandler != null && myhandler != null)
            myhandler.removeCallbacks(myrunnable);

        myhandler =new Handler();
        myrunnable = new Runnable() {
            @Override
            public void run()
            {
                try
                {
                    if(getcurrentfragment() instanceof composeoptionspagerfragment || getcurrentfragment() instanceof videocomposerfragment ||
                            getcurrentfragment() instanceof audiocomposerfragment || getcurrentfragment() instanceof imagecomposerfragment ||
                            getcurrentfragment() instanceof videoreaderfragment || getcurrentfragment() instanceof imagereaderfragment ||
                            getcurrentfragment() instanceof audioreaderfragment)
                    {
                        if((graphicaldrawerfragment!= null))
                            graphicaldrawerfragment.setmetricesdata();
                    }
                    
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
                myhandler.postDelayed(this, 1000);
            }
        };
        myhandler.post(myrunnable);
    }

    @Override
    public void updatezoomlevel(double latitude, double longitude) {
         if((graphicaldrawerfragment!= null))
             graphicaldrawerfragment.zoomgooglemap(latitude,longitude);
    }

    @Override
    public void setdatacomposing(boolean isdatacomposing) {
        if((graphicaldrawerfragment!= null))
            graphicaldrawerfragment.setdatacomposing(isdatacomposing);
    }

    @Override
    public void setdatacomposing(boolean isdatacomposing, String mediafilepath) {
         if((graphicaldrawerfragment!= null))
             graphicaldrawerfragment.setdatacomposing(isdatacomposing,mediafilepath);
    }

    @Override
    public void setcurrentmediaposition(int currentmediaposition) {
         if(graphicaldrawerfragment!= null)
            graphicaldrawerfragment.setcurrentmediaposition(currentmediaposition);
    }

    @Override
    public void setisrecordrunning(boolean isrecodrunning) {
         if(graphicaldrawerfragment!= null)
            graphicaldrawerfragment.setrecordrunning(isrecodrunning);
    }

    @Override
    public void setsoundwaveinformation(int ampletudevalue, int decibelvalue,boolean issoundwaveshow) {
         if(graphicaldrawerfragment!= null)
            graphicaldrawerfragment.setsoundinformation(ampletudevalue,decibelvalue,issoundwaveshow);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(myhandler != null && myhandler != null)
            myhandler.removeCallbacks(myrunnable);

        progressdialog.dismisswaitdialog();
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

    public void getstatusbarheight() {
        Rect rectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int statusBarHeight = rectangle.top;
        if (statusBarHeight != 0) {
            xdata.getinstance().saveSetting("statusbarheight", "" + statusBarHeight);
        }
    }

    @Override
    public void setwindowfitxy(boolean isfullscreen) {
        if(isfullscreen){
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            );
            // transparent
        }else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        }
    }

    public void drawerbutton(){
        imglefthandle.post(new Runnable() {
            @Override
            public void run() {
              imageheight =  imglefthandle.getHeight();
                if(imageheight<= 0){
                    imageheight = 90;
                }
            }
        });
        RelativeLayout.LayoutParams params  = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10,(finalheight-imageheight),0,0);
        Log.e("finalheight", ""+finalheight);
        imglefthandle.setLayoutParams(params);
        imglefthandle.requestLayout();
    }

    public void launchcomposerfragment()
    {
        if(xdata.getinstance().getSetting(config.launchtype).equalsIgnoreCase(config.launchtypemedialist))
        {
            fragmentmedialist fragment=new fragmentmedialist();
            replaceFragment(fragment, false, true);
        }
        else
        {
            composeoptionspagerfragment fragment=new composeoptionspagerfragment();
            replaceFragment(fragment, false, true);
        }
    }

    @Override
    public void setdrawerheightonfullscreen(int drawerheight) {
        if(drawerheight!=0){
            navigation_drawer.setPadding(0,0,0,drawerheight);
            imgrighthandle.setPadding(0,drawerheight,0,0);
        }else{
            navigation_drawer.setPadding(0,0,0,0);
            imgrighthandle.setPadding(0,0,0,0);
        }
        //  graphicaldrawerfragment.setdrawerheight(drawerheight);
    }

    public void getupdatedmetadata()
    {
        if((! isuserlogin() || (xdata.getinstance().getSetting(config.clientid).trim().isEmpty())))
            return;

        HashMap<String,String> requestparams=new HashMap<>();
        requestparams.put("type",config.type_media);
        requestparams.put("action","list");
        requestparams.put("sharemethod","public");
        requestparams.put("clientid",xdata.getinstance().getSetting(config.clientid));
        xapipost_send(applicationviavideocomposer.getactivity(),requestparams, new apiresponselistener() {
            @Override
            public void onResponse(final taskresult response)
            {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(response.isSuccess())
                        {
                            try {
                                JSONObject object=new JSONObject(response.getData().toString());
                                if(object.has("media"))
                                {
                                    JSONArray array=object.getJSONArray("media");
                                    if(array.length() > 0)
                                    {
                                        databasemanager mdbhelper = new databasemanager(getApplicationContext());
                                        try
                                        {
                                            mdbhelper.createDatabase();
                                            mdbhelper.open();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        try {
                                            for(int i=0;i<array.length();i++)
                                            {
                                                JSONObject jobject=array.getJSONObject(i);
                                                String mediatitle="",mediadescription="",mediakey="";
                                                if(jobject.has("mediatitle"))
                                                    mediatitle=jobject.getString("mediatitle");
                                                if(jobject.has("mediadescription"))
                                                    mediadescription=jobject.getString("mediadescription");
                                                if(jobject.has("mediakey"))
                                                    mediakey=jobject.getString("mediakey");

                                                mdbhelper.updatemediainfobymediakey(mediakey,mediatitle,mediadescription);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        try {
                                            mdbhelper.close();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        });
    }
}