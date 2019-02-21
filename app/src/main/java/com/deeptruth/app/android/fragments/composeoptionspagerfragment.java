package com.deeptruth.app.android.fragments;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.deeptruth.app.android.BuildConfig;
import com.deeptruth.app.android.R;
import com.deeptruth.app.android.activity.locationawareactivity;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.database.databasemanager;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.models.permissions;
import com.deeptruth.app.android.sensor.Orientation;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.xdata;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.droidsonroids.gif.GifDrawable;

/**
 * Created by root on 6/11/18.
 */

public class composeoptionspagerfragment extends basefragment implements View.OnClickListener, Orientation.Listener {

    @BindView(R.id.txt_timer)
    TextView txt_timer;

    @BindView(R.id.img_video_capture)
    ImageView recordstartstopbutton;
    @BindView(R.id.img_rotate_camera)
    ImageView imgrotatecamera;
    @BindView(R.id.layout_bottom)
    RelativeLayout layoutbottom;
    @BindView(R.id.tab_container)
    FrameLayout tab_container;
    @BindView(R.id.img_mediathumbnail)
    ImageView img_mediathumbnail;
    @BindView(R.id.layout_no_gps_wifi)
    LinearLayout layout_no_gps_wifi;
    @BindView(R.id.layout_validating)
    LinearLayout layout_validating;
    @BindView(R.id.layout_section_heading)
    RelativeLayout layout_section_heading;
    @BindView(R.id.txt_section_validating)
    TextView txt_section_validating;
    @BindView(R.id.txt_section_validating_secondary)
    TextView txt_section_validating_secondary;
    @BindView(R.id.shimmer_view_container)
    ShimmerFrameLayout shimmer_view_container;
    @BindView(R.id.txt_mediatype_a)
    TextView txt_mediatype_a;
    @BindView(R.id.txt_mediatype_b)
    TextView txt_mediatype_b;
    @BindView(R.id.txt_mediatype_c)
    TextView txt_mediatype_c;
    @BindView(R.id.layout_mediatype)
    RelativeLayout layout_mediatype;

    videocomposerfragment fragvideocomposer=null;
    audiocomposerfragment fragaudiocomposer=null;
    imagecomposerfragment fragimgcapture=null;

    View rootview=null;
    int currentselectedcomposer=0;

    private int flingactionmindstvac;
    private  final int flingactionmindspdvac = 100;

    private Runnable doafterallpermissionsgranted;
    private static final int request_permissions = 1;
    ArrayList<permissions> permissionslist =new ArrayList<>();

    ArrayList<String> imagearraylist =new ArrayList<>();
    ArrayList<String> videoarraylist =new ArrayList<>();
    ArrayList<String> audioarraylist =new ArrayList<>();
    private Handler myHandler;
    private Runnable myRunnable;
    private boolean showwarningsection=true;
    private CountDownTimer countertimer;

    GifDrawable gifdrawable;
    private Orientation mOrientation;
    private static final int ORIENTATION_0 = 0;
    private static final int ORIENTATION_90 = 3;
    private static final int ORIENTATION_270 = 1;

    @Override
    public int getlayoutid() {
        return R.layout.fragment_composeoptionspager;
    }
    @Override
    public void initviews(View parent, Bundle savedInstanceState) {
        super.initviews(parent, savedInstanceState);
        ButterKnife.bind(this,parent);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(rootview==null) {
            rootview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, rootview);

            recordstartstopbutton.setOnClickListener(this);
            imgrotatecamera.setOnClickListener(this);
            img_mediathumbnail.setOnClickListener(this);
            txt_mediatype_a.setOnClickListener(this);
            txt_mediatype_b.setOnClickListener(this);
            txt_mediatype_c.setOnClickListener(this);
            shimmer_view_container.startShimmer();

            try {
                gifdrawable = new GifDrawable(getResources(), R.drawable.recorder_transparent);
                gifdrawable.setLoopCount(0);
                gifdrawable.setSpeed(1.0f);
                recordstartstopbutton.setImageDrawable(gifdrawable);
                recordstartstopbutton.setAlpha(0f);
                recordstartstopbutton.animate().alpha(1.0f).setDuration(100).setListener(null);
                gifdrawable.pause();
                gifdrawable.seekTo(0);
            } catch (Exception e) {
                e.printStackTrace();
            }

            mOrientation = new Orientation(applicationviavideocomposer.getactivity());
        }
        return rootview;
    }

    @Override
    public void onStop() {
        super.onStop();
        if(gifdrawable!= null && gifdrawable.isPlaying())
            gifdrawable.stop();

        if(mOrientation != null)
            mOrientation.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();

        if(mOrientation != null)
            mOrientation.startListening(this);

        if (doafterallpermissionsgranted != null) {
            doafterallpermissionsgranted.run();
            doafterallpermissionsgranted = null;
        } else {
            if(permissionslist.size() == 0)
            {
                permissionslist.add(new permissions(Manifest.permission.ACCESS_COARSE_LOCATION,false,false));
                permissionslist.add(new permissions(Manifest.permission.ACCESS_FINE_LOCATION,false,false));
            }
            List<String> deniedpermissions = new ArrayList<>();
            for(int i=0;i<permissionslist.size();i++)
            {
                permissionslist.get(i).setIspermissionallowed(true);
                if (ContextCompat.checkSelfPermission(getActivity(), permissionslist.get(i).getPermissionname()) != PackageManager.PERMISSION_GRANTED)
                {
                    if(! permissionslist.get(i).isIspermissionskiped())
                    {
                        deniedpermissions.add(permissionslist.get(i).getPermissionname());
                        permissionslist.get(i).setIspermissionallowed(false);
                        break;
                    }
                }
            }

            if (deniedpermissions.isEmpty()) {
                // All permissions are granted
                doafterpermissionsgranteddenied();
            } else {
                //String[] array = new String[deniedpermissions.size()];

                String[] array = new String[1];
                array = deniedpermissions.toArray(array);
                final String[] finalArray = array;

                common.showcustompermissiondialog(applicationviavideocomposer.getactivity(), new adapteritemclick() {
                    @Override
                    public void onItemClicked(Object object) {
                    }

                    @Override
                    public void onItemClicked(Object object, int type) {
                        if(type == 0)
                        {
                            if(finalArray.length > 0)
                            {
                                for(int i=0;i<permissionslist.size();i++)
                                {
                                    if(finalArray[0].equalsIgnoreCase(permissionslist.get(i).getPermissionname()))
                                    {
                                        permissionslist.get(i).setIspermissionskiped(true);
                                        break;
                                    }
                                }
                            }
                            doafterpermissionsgranteddenied();
                        }
                        else if(type == 1)
                        {
                            if(finalArray.length > 0)
                                ActivityCompat.requestPermissions(getActivity(), finalArray, request_permissions);
                        }
                    }
                },finalArray[0]);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == request_permissions) {
            boolean permissionsallgranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    permissionsallgranted = false;
                    break;
                }
            }
            if (permissionsallgranted) {
                doafterallpermissionsgranted = new Runnable() {
                    @Override
                    public void run() {
                        doafterpermissionsgranteddenied();
                    }
                };
            } else {
                doafterallpermissionsgranted = new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), R.string.permissions_denied_exit, Toast.LENGTH_SHORT).show();
                        //gethelper().onBack();
                    }
                };
            }
        }
    }

    private void doafterpermissionsgranteddenied() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(fragaudiocomposer == null && locationawareactivity.checkCameraPermission(applicationviavideocomposer.getactivity()))
                {
                    runhandler();
                    initviewpager();
                }
            }
        },10);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(shimmer_view_container != null)
            shimmer_view_container.stopShimmer();

        if(myHandler != null && myRunnable != null)
            myHandler.removeCallbacks(myRunnable);

        if(countertimer != null)
            countertimer.cancel();
    }

    public void showwarningsection(boolean showwarning)
    {
        if(showwarning)
        {
            layout_section_heading.setVisibility(View.VISIBLE);
            showwarningsection=true;
        }
        else
        {
            layout_section_heading.setVisibility(View.GONE);
            showwarningsection=false;
        }
    }

    public void visiblewarningcontrollers(){

        if(fragvideocomposer != null && fragvideocomposer.isvideorecording)
        {
            layout_validating.setVisibility(View.GONE);
            fragvideocomposer.showwarningorclosebutton();
            fragvideocomposer.showwarningsection(showwarningsection);
            if(layout_no_gps_wifi != null)
                layout_no_gps_wifi.setVisibility(View.VISIBLE);
        }
        else if(fragaudiocomposer != null && fragaudiocomposer.isaudiorecording)
        {
            layout_validating.setVisibility(View.GONE);
            fragaudiocomposer.showwarningorclosebutton();
            fragaudiocomposer.showwarningsection(showwarningsection);
            if(layout_no_gps_wifi != null)
                layout_no_gps_wifi.setVisibility(View.VISIBLE);
        }
        else
        {
            layout_validating.setVisibility(View.GONE);
            hidewarningsection();
        }

        /*if(fragimgcapture != null)
        {
            fragimgcapture.showwarningorclosebutton();
            fragimgcapture.showwarningsection(showwarningsection);
        }*/
    }

    public void hidewarningsection()
    {
        if(layout_no_gps_wifi != null)
            layout_no_gps_wifi.setVisibility(View.GONE);

        if(fragvideocomposer != null)
            fragvideocomposer.hideallsection();

        if(fragaudiocomposer != null)
            fragaudiocomposer.hideallsection();

        if(fragimgcapture != null)
            fragimgcapture.hideallsection();
    }

    public void validatingcontrollers(){

        if(fragvideocomposer != null && fragvideocomposer.isvideorecording)
        {
            hidewarningsection();
            layout_validating.setVisibility(View.VISIBLE);
        }
        else if(fragaudiocomposer != null && fragaudiocomposer.isaudiorecording)
        {
            hidewarningsection();
            layout_validating.setVisibility(View.VISIBLE);
        }
        else
        {
            hidewarningsection();
            layout_validating.setVisibility(View.GONE);
        }

        /*if(fragimgcapture != null)
        {
            fragimgcapture.showwarningorclosebutton();
            fragimgcapture.showwarningsection(showwarningsection);
        }*/
    }

    public void runhandler()
    {
        if(myHandler != null && myRunnable != null)
            myHandler.removeCallbacks(myRunnable);

        myHandler =new Handler();
        myRunnable = new Runnable() {
            @Override
            public void run() {

                try {
                    if(xdata.getinstance().getSetting("wificonnected").equalsIgnoreCase("0") ||
                            xdata.getinstance().getSetting("gpsenabled").equalsIgnoreCase("0"))
                    {
                        txt_section_validating.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.yellow_background));
                        visiblewarningcontrollers();
                    }
                    else
                    {
                        txt_section_validating_secondary.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.dark_blue_solid_a));
                        validatingcontrollers();
                    }
                    /*else if(! locationawareactivity.checkPermission(applicationviavideocomposer.getactivity()))
                    {
                        if(permissionslist.size() >= 2)
                        {
                            if(permissionslist.get(0).isIspermissionskiped() || permissionslist.get(1).isIspermissionskiped())
                                visiblewarningcontrollers();
                        }
                    }*/

                    /*int rotation = applicationviavideocomposer.getactivity().getWindowManager().getDefaultDisplay().getRotation();
                    Display display = ((WindowManager)applicationviavideocomposer.getactivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                    int screenOrientation = display.getRotation();
                    float rotationangle = 0;

                    switch (screenOrientation)
                    {
                        default:
                        case ORIENTATION_0: // Portrait
                            rotationangle=0;
                            // do smth.
                            break;
                        case ORIENTATION_90: // Landscape right
                            rotationangle=-90;
                            // do smth.
                            break;
                        case ORIENTATION_270: // Landscape left
                            // do smth.
                            rotationangle=90;
                            break;
                    }*/



                } catch (Exception e) {
                    e.printStackTrace();
                }
                myHandler.postDelayed(this, 1000);
            }
        };
        myHandler.post(myRunnable);
    }

    public void initviewpager()
    {
      //  recordstartstopbutton.setImageResource(R.drawable.img_startrecord);
     //   recordstartstopbutton.setBackgroundResource(R.drawable.shape_recorder_off);

        flingactionmindstvac=common.getcomposerswipearea();

        currentselectedcomposer=config.selectedmediatype;
        if(currentselectedcomposer == 0)
        {
            showselecteditemincenter(txt_mediatype_a,1);
        }
        else if(currentselectedcomposer == 2)
        {
            showselecteditemincenter(txt_mediatype_c,3);
        }

        showselectedfragment();
        getlatestthumbnailfromdirectory();
    }

    public void showselecteditemincenter(TextView textView,int sectionnumber)
    {
        String selectedvalue=textView.getText().toString();
        if(textView.getText().toString().startsWith(config.item_video))
        {
            currentselectedcomposer=0;
        }
        else if(textView.getText().toString().startsWith(config.item_photo))
        {
            currentselectedcomposer=1;
        }
        else if(textView.getText().toString().startsWith(config.item_audio))
        {
            currentselectedcomposer=2;
        }

        if(sectionnumber == 1)
        {
            textView.setText(txt_mediatype_c.getText().toString());
            txt_mediatype_c.setText(txt_mediatype_b.getText().toString());
        }
        else if(sectionnumber == 3)
        {
            textView.setText(txt_mediatype_a.getText().toString());
            txt_mediatype_a.setText(txt_mediatype_b.getText().toString());
        }
        txt_mediatype_b.setText(selectedvalue);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.txt_mediatype_a:
                showselecteditemincenter(txt_mediatype_a,1);
                showselectedfragment();
                break;
            case R.id.txt_mediatype_b:

                break;
            case R.id.txt_mediatype_c:
                showselecteditemincenter(txt_mediatype_c,3);
                showselectedfragment();
                break;

            case R.id.img_video_capture:
                recordstartstopbutton.setEnabled(false);
                new Handler().postDelayed(new Runnable()
                {
                    public void run()
                    {
                        recordstartstopbutton.setEnabled(true);
                    }
                }, 1000);

                if(currentselectedcomposer == 0)
                {
                    if(fragvideocomposer != null)
                        fragvideocomposer.startstopvideo();
                }
                else if(currentselectedcomposer == 1)
                {
                    if(fragimgcapture != null)
                    {
                        if(fragimgcapture.isbrustmodeenabled())
                        {
                            startbrustcameratimer();
                        }
                        else
                        {
                            if(fragimgcapture != null)
                                fragimgcapture.takePicture();
                        }
                    }
                }
                else if(currentselectedcomposer == 2)
                {

                    try {
                        if(fragaudiocomposer != null)
                            fragaudiocomposer.startstopaudiorecording();
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.img_rotate_camera:
                if(currentselectedcomposer == 0)
                {
                    if(fragvideocomposer != null)
                        fragvideocomposer.switchCamera();
                }
                else if(currentselectedcomposer == 1)
                {
                    if(fragimgcapture != null)
                        fragimgcapture.switchCamera();
                }
                break;

            case R.id.img_mediathumbnail:
                config.selectedmediatype=currentselectedcomposer;
                medialistitemaddbroadcast();
                //gethelper().onBack();
                fragmentmedialist fragmatriclist=new fragmentmedialist();
                fragmatriclist.shouldlaunchcomposer(false);
                gethelper().replaceFragment(fragmatriclist, true, false);
                break;
        }
    }

    public void startbrustcameratimer()
    {
        if(countertimer != null)
            countertimer.cancel();

        txt_timer.setVisibility(View.VISIBLE);

        countertimer=new CountDownTimer(21000, 1000) {

            public void onTick(long millisUntilFinished) {
                Log.e("Timer running", " Tick");
                millisUntilFinished=millisUntilFinished-10000;
                int lefttime=(int)(millisUntilFinished/1000);
                if(lefttime == 0)
                {
                    if(countertimer != null)
                        countertimer.cancel();

                    cameracaptureeffect();
                }
                else
                {
                    txt_timer.setText(""+lefttime);
                }
            }

            public void onFinish() {
                if(countertimer != null)
                    countertimer.cancel();
            }
        }.start();
    }

    private void cameracaptureeffect() {
        ObjectAnimator animation = ObjectAnimator.ofInt(txt_timer, "backgroundColor", Color.WHITE);
        animation.setDuration(50);
        animation.setEvaluator(new ArgbEvaluator());
        animation.setRepeatCount(Animation.RELATIVE_TO_SELF);
        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                txt_timer.setVisibility(View.GONE);

                if(fragimgcapture != null)
                    fragimgcapture.takePicture();
            }

            @Override
            public void onAnimationEnd(Animator animation, boolean isReverse) {

            }
        });
        animation.start();
    }

    public void medialistitemaddbroadcast()
    {
        Intent intent = new Intent(config.broadcast_medialistnewitem);
        applicationviavideocomposer.getactivity().sendBroadcast(intent);
    }


    GestureDetector flingswipegesture = new GestureDetector(applicationviavideocomposer.getactivity(), new GestureDetector.SimpleOnGestureListener()
    {
        @Override
        public boolean onFling(MotionEvent fstMsnEvtPsgVal, MotionEvent lstMsnEvtPsgVal, float flingActionXcoSpdPsgVal,
                               float flingActionYcoSpdPsgVal)
        {
            if(fstMsnEvtPsgVal.getX() - lstMsnEvtPsgVal.getX() > flingactionmindstvac && Math.abs(flingActionXcoSpdPsgVal) >
                    flingactionmindspdvac)
            {
                // TskTdo :=> On Right to Left fling
                swiperighttoleft();
                return false;
            }
            else if (lstMsnEvtPsgVal.getX() - fstMsnEvtPsgVal.getX() > flingactionmindstvac && Math.abs(flingActionXcoSpdPsgVal) >
                    flingactionmindspdvac)
            {
                // TskTdo :=> On Left to Right fling
                swipelefttoright();
                return false;
            }

            if(fstMsnEvtPsgVal.getY() - lstMsnEvtPsgVal.getY() > flingactionmindstvac && Math.abs(flingActionYcoSpdPsgVal) >
                    flingactionmindspdvac)
            {
                // TskTdo :=> On Bottom to Top fling

                return false;
            }
            else if (lstMsnEvtPsgVal.getY() - fstMsnEvtPsgVal.getY() > flingactionmindstvac && Math.abs(flingActionYcoSpdPsgVal) >
                    flingactionmindspdvac)
            {
                // TskTdo :=> On Top to Bottom fling

                return false;
            }
            return false;
        }
    });

    public void swipelefttoright()
    {
        if(currentselectedcomposer == 0)
            return;
        Log.e("currentselectedcomposer",""+currentselectedcomposer);
        currentselectedcomposer--;
        showselectedfragment();
    }

    public void swiperighttoleft()
    {
        if(currentselectedcomposer == 2)
            return;

        currentselectedcomposer++;
        Log.e("currentselectedcomposer",""+currentselectedcomposer);
        showselectedfragment();
    }

    public void showselectedfragment()
    {

        switch (currentselectedcomposer)
        {
            case 0:
                imgrotatecamera.setVisibility(View.VISIBLE);
                showhideactionbottombaricon(1);
                if(fragvideocomposer == null)
                    fragvideocomposer=new videocomposerfragment();

                fragvideocomposer.setData(false, mitemclick,layoutbottom);
                gethelper().replacetabfragment(fragvideocomposer,false,true);
            break;

            case 1:
                imgrotatecamera.setVisibility(View.VISIBLE);
                showhideactionbottombaricon(1);
                if(fragimgcapture == null)
                    fragimgcapture=new imagecomposerfragment();

                fragimgcapture.setData(mitemclick,layoutbottom);
                gethelper().replacetabfragment(fragimgcapture,false,true);

            break;

            case 2:
                imgrotatecamera.setVisibility(View.GONE);
                showhideactionbottombaricon(3);
                if(fragaudiocomposer == null)
                    fragaudiocomposer=new audiocomposerfragment();

                fragaudiocomposer.setData(mitemclick,layoutbottom);
                gethelper().replacetabfragment(fragaudiocomposer,false,true);
            break;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setimagethumbnail();

                if(gifdrawable!= null && gifdrawable.isPlaying())
                {
                    gifdrawable.pause();
                    gifdrawable.seekTo(0);
                }

                String mediatype="";
                switch (currentselectedcomposer)
                {
                    case 0:
                        mediatype=config.item_video;
                        break;

                    case 1:
                        mediatype=config.item_photo;
                        break;

                    case 2:
                        mediatype=config.item_audio;
                        break;
                }
                if(txt_mediatype_a.getText().toString().startsWith(mediatype))
                {
                    showselecteditemincenter(txt_mediatype_a,1);
                }
                else if(txt_mediatype_c.getText().toString().startsWith(mediatype))
                {
                    showselecteditemincenter(txt_mediatype_c,3);
                }

            }
        },50);


    }

    adapteritemclick mitemclick=new adapteritemclick() {
        @Override
        public void onItemClicked(Object object) {

        }

        @Override
        public void onItemClicked(Object object, int type) {
            if(type == 1) // for video record start,audio record start and image capture button click
            {
                if(gifdrawable!= null && (! gifdrawable.isPlaying()))
                    gifdrawable.reset();

                if(currentselectedcomposer == 0)
                {
                    showhideactionbottombaricon(0);
                }
                else if(currentselectedcomposer == 2)
                {
                   showhideactionbottombaricon(2);
                }

              // setimagerecordstart();
            }
            else if(type == 2) // for video record stop,audio record stop and image captured button click
            {
                if(gifdrawable!= null && gifdrawable.isPlaying())
                {
                    gifdrawable.pause();
                    gifdrawable.seekTo(0);
                }

                if(currentselectedcomposer == 0)
                {
                    showhideactionbottombaricon(1);
                }
                else if(currentselectedcomposer == 2)
                {
                    showhideactionbottombaricon(3);
                }
            }
            else if(type == 3) // For swipe gesture to change fragment
            {
                MotionEvent motionevent=(MotionEvent)object;
                flingswipegesture.onTouchEvent(motionevent);
            }
            else if(type == 4) // Back the fragment and navigate to media list
            {
                try {
                    config.selectedmediatype=currentselectedcomposer;
                }catch (Exception e)
                {
                    e.printStackTrace();
                }

                /*currentselectedcomposer=config.selectedmediatype;
                showselectedfragment();*/
                getlatestthumbnailfromdirectory();
            }
            else if(type == 5) // For swipe gesture to change fragment
            {
                showwarningsection(true);
            }
            else if(type == 6) // For swipe gesture to change fragment
            {
                showwarningsection(false);
            }
            else if(type == 9)
            {
                layoutbottom.setVisibility(View.INVISIBLE);
                layout_mediatype.setVisibility(View.INVISIBLE);
            }
            else if(type == 10)
            {
                layoutbottom.setVisibility(View.VISIBLE);
                layout_mediatype.setVisibility(View.VISIBLE);
            }
        }
    };

    public void getlatestthumbnailfromdirectory()
    {
        audioarraylist.clear();
        videoarraylist.clear();
        imagearraylist.clear();

        databasemanager mdbhelper=null;
        if (mdbhelper == null) {
            mdbhelper = new databasemanager(applicationviavideocomposer.getactivity());
            mdbhelper.createDatabase();
        }

        try {
            mdbhelper.open();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        Cursor cursor = mdbhelper.getallmediabyfolder(xdata.getinstance().getSetting(config.selected_folder));
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst())
        {
            do{
                String mediafilepath = "" + cursor.getString(cursor.getColumnIndex("mediafilepath"));
                String thumbnailurl = "" + cursor.getString(cursor.getColumnIndex("thumbnailurl"));

                if(mediafilepath.contains(".mp4"))
                {
                    videoarraylist.add(mediafilepath);
                }
                else if(mediafilepath.contains(".jpg") || mediafilepath.contains(".png") ||
                        mediafilepath.contains(".jpeg"))
                {
                    imagearraylist.add(mediafilepath);
                }
                else if(mediafilepath.contains(".m4a"))
                {
                    audioarraylist.add(thumbnailurl);
                }

                if(videoarraylist.size() > 0 && imagearraylist.size() > 0 && audioarraylist.size() > 0)
                    break;

            }while(cursor.moveToNext());
        }

        try
        {
            mdbhelper.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            setimagethumbnail();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void setimagethumbnail()
    {
        String mediapath="";
        img_mediathumbnail.setImageResource(0);
        img_mediathumbnail.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.transparent));

        if(currentselectedcomposer == 0)
        {
            if(videoarraylist.size() > 0)
            {
                setthumbnailimage(videoarraylist.get(0));
            }
            else
            {
                img_mediathumbnail.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.dark_blue_solid_a));
            }
        }
        else if(currentselectedcomposer == 1)
        {
            if(imagearraylist.size() > 0)
            {
                setthumbnailimage(imagearraylist.get(0));
            }
            else
            {
                img_mediathumbnail.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.dark_blue_solid_a));
            }
        }
        else if(currentselectedcomposer == 2)
        {
            if(audioarraylist.size() > 0)
            {
                setthumbnailimage(audioarraylist.get(0));
                img_mediathumbnail.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.dark_blue_solid_a));
            }
            else
            {
                img_mediathumbnail.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.dark_blue_solid_a));
            }
        }
    }

    public void setthumbnailimage(String mediapath)
    {
        Uri uri= FileProvider.getUriForFile(applicationviavideocomposer.getactivity(),
                BuildConfig.APPLICATION_ID + ".provider", new File(mediapath));
        Glide.with(applicationviavideocomposer.getactivity()).load(uri).thumbnail(0.1f)
                .transition(GenericTransitionOptions.with(R.anim.fadein)).
                into(img_mediathumbnail);
    }

    public void setthumbnailimageplaceholder(int placeholder)
    {
        Glide.with(applicationviavideocomposer.getactivity()).
                load(placeholder).
                thumbnail(0.1f).
                into(img_mediathumbnail);
    }

    public void showhideactionbottombaricon(int i){
        if(i == 0){
            img_mediathumbnail.setVisibility(View.INVISIBLE);
            imgrotatecamera.setVisibility(View.INVISIBLE);
            layout_mediatype.setVisibility(View.INVISIBLE);
        } else if(i == 1){
            img_mediathumbnail.setVisibility(View.VISIBLE);
            imgrotatecamera.setVisibility(View.VISIBLE);
            layout_mediatype.setVisibility(View.VISIBLE);
        } else if( i == 2){
            layout_mediatype.setVisibility(View.INVISIBLE);
            img_mediathumbnail.setVisibility(View.INVISIBLE);
        }
        else if( i == 3){
            layout_mediatype.setVisibility(View.VISIBLE);
            img_mediathumbnail.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onOrientationChanged(float pitch, float roll) {

    }

    @Override
    public void onOrientationChanged(float[] adjustedRotationMatrix, float[] orientation) {
        float roll = orientation[2] * -57;
        float rotateangle=3600;
        if(roll < -45 && roll >= -120)
        {
            rotateangle=-90;
        }
        else if(roll >= -40 && roll <= 45)
        {
            rotateangle=0;
        }
        else if(roll > 45 && roll <= 120)
        {
            rotateangle=90;
        }

        if(rotateangle != 3600)
        {
            if(imgrotatecamera != null)
                imgrotatecamera.setRotation(rotateangle);

            if(img_mediathumbnail != null)
                img_mediathumbnail.setRotation(rotateangle);

            if(fragvideocomposer != null)
                fragvideocomposer.changeiconsorientation(rotateangle);

            if(fragimgcapture != null)
                fragimgcapture.changeiconsorientation(rotateangle);

            if(fragaudiocomposer != null)
                fragaudiocomposer.changeiconsorientation(rotateangle);

        }
            /*if(imgrotatecamera != null)
                imgrotatecamera.setRotation(roll);

            if(img_mediathumbnail != null)
                img_mediathumbnail.setRotation(roll);*/
    }
}
