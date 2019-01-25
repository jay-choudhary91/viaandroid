package com.deeptruth.app.android.fragments;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaExtractor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.deeptruth.app.android.BuildConfig;
import com.deeptruth.app.android.R;
import com.deeptruth.app.android.activity.locationawareactivity;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.models.permissions;
import com.deeptruth.app.android.models.video;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.xdata;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by root on 6/11/18.
 */

public class composeoptionspagerfragment extends basefragment implements View.OnClickListener {

    @BindView(R.id.txt_timer)
    TextView txt_timer;

    @BindView(R.id.img_video_capture)
    ImageView mrecordimagebutton;
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
    @BindView(R.id.layout_recorder)
    RelativeLayout layoutrecorder;
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
    @BindView(R.id.base_view)
    ViewGroup mParent;

    GradientDrawable gradientdrawable;
    private volatile boolean iscircle = true;
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

            mrecordimagebutton.setOnClickListener(this);
            imgrotatecamera.setOnClickListener(this);
            img_mediathumbnail.setOnClickListener(this);
            txt_mediatype_a.setOnClickListener(this);
            txt_mediatype_b.setOnClickListener(this);
            txt_mediatype_c.setOnClickListener(this);
            shimmer_view_container.startShimmer();
            gradientdrawable = new GradientDrawable();
            gradientdrawable.setCornerRadius(360.0f);
            gradientdrawable.setShape(GradientDrawable.RECTANGLE);
            mParent.setBackground(gradientdrawable);
        }
        return rootview;
    }

    @Override
    public void onResume() {
        super.onResume();
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

    private void makecircle() {

        ObjectAnimator cornerAnimation =
                ObjectAnimator.ofFloat(gradientdrawable, "cornerRadius", 30.0f, 200.0f);

        Animator shiftAnimation = AnimatorInflater.loadAnimator(getActivity(), R.animator.slide_right_down);
        shiftAnimation.setTarget(mParent);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(800);
        animatorSet.playTogether(cornerAnimation, shiftAnimation);
        animatorSet.start();
        iscircle = !iscircle;
    }

    private void makesquare() {

        ObjectAnimator cornerAnimation =
                ObjectAnimator.ofFloat(gradientdrawable, "cornerRadius",100f,10.0f);

        Animator shiftAnimation = AnimatorInflater.loadAnimator(getActivity(), R.animator.slide_left_up);
        shiftAnimation.setTarget(mParent);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(800);
        animatorSet.playTogether(cornerAnimation, shiftAnimation);
        animatorSet.start();
        iscircle = !iscircle;

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
      //  mrecordimagebutton.setImageResource(R.drawable.img_startrecord);
     //   mrecordimagebutton.setBackgroundResource(R.drawable.shape_recorder_off);

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
        getlatestmediafromdirectory();
        getlatestaudiofromdirectory();
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

                if(currentselectedcomposer == 0)
                {
                    if (iscircle) {
                        makesquare();
                    }
                    else {
                        makecircle();
                    }
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
                            view.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.image_click));
                        }
                        else
                        {
                            if(fragimgcapture != null)
                                fragimgcapture.takePicture();

                            view.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.image_click));
                        }
                    }
                }
                else if(currentselectedcomposer == 2)
                {
                    if (iscircle) {
                        makesquare();
                    }
                    else {
                        makecircle();
                    }
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
                gethelper().onBack();
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
                if(fragvideocomposer == null)
                    fragvideocomposer=new videocomposerfragment();

                fragvideocomposer.setData(false, mitemclick);
                gethelper().replacetabfragment(fragvideocomposer,false,true);
            break;

            case 1:
                imgrotatecamera.setVisibility(View.VISIBLE);
                if(fragimgcapture == null)
                    fragimgcapture=new imagecomposerfragment();

                fragimgcapture.setData(mitemclick);
                gethelper().replacetabfragment(fragimgcapture,false,true);

            break;

            case 2:
                imgrotatecamera.setVisibility(View.GONE);
                if(fragaudiocomposer == null)
                    fragaudiocomposer=new audiocomposerfragment();

                fragaudiocomposer.setData(mitemclick);
                gethelper().replacetabfragment(fragaudiocomposer,false,true);
            break;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               // setimagerecordstop();
                setimagethumbnail();
                if(! iscircle)
                    makecircle();

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
               // setimagerecordstart();
            }
            else if(type == 2) // for video record stop,audio record stop and image captured button click
            {
               // setimagerecordstop();
                /*if(currentselectedcomposer == 0 || currentselectedcomposer == 1)
                {
                    getlatestmediafromdirectory();
                }
                else if(currentselectedcomposer == 2)
                {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getlatestaudiofromdirectory();
                        }
                    },3000);
                }*/
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

                currentselectedcomposer=config.selectedmediatype;
                showselectedfragment();
                if(currentselectedcomposer == 0 || currentselectedcomposer == 1)
                {
                    getlatestmediafromdirectory();
                }
                else if(currentselectedcomposer == 2)
                {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getlatestaudiofromdirectory();
                        }
                    },3000);
                }

                //gethelper().onBack();
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

    public void getlatestaudiofromdirectory()
    {
        File videodir = new File(config.audiowavesdir);
        if(! videodir.exists())
            return;

        audioarraylist.clear();

        File[] files = videodir.listFiles();
        for (File file : files)
        {
            long filelength = file.length();
            int file_size = Integer.parseInt(String.valueOf(filelength/1024));
            if(file_size > 0)
            {
                video videoobj=new video();
                videoobj.setPath(file.getAbsolutePath());

                if(videoobj.getPath().contains(".jpg") || videoobj.getPath().contains(".png") || videoobj.getPath().contains(".jpeg"))
                    audioarraylist.add(videoobj.getPath());
            }
        }
        setimagethumbnail();
    }

    public void getlatestmediafromdirectory()
    {
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                File videodir = new File(config.videodir);
                if(! videodir.exists())
                    return;

                imagearraylist.clear();
                videoarraylist.clear();

                File[] files = videodir.listFiles();
                for (File file : files)
                {
                    long filelength = file.length();
                    int file_size = Integer.parseInt(String.valueOf(filelength/1024));
                    if(file_size > 0 && (! file.isDirectory()))
                    {
                        video videoobj=new video();
                        videoobj.setPath(file.getAbsolutePath());

                        MediaExtractor extractor = new MediaExtractor();
                        try {
                            if(videoobj.getPath().contains(".jpg") || videoobj.getPath().contains(".png") || videoobj.getPath().contains(".jpeg"))
                            {
                                imagearraylist.add(videoobj.getPath());
                            }
                            else if((! videoobj.getPath().contains(".pcm") && (! videoobj.getPath().contains(".m4a"))))
                            {
                                extractor.setDataSource(file.getAbsolutePath());
                                int numTracks = extractor.getTrackCount();
                                if(numTracks > 0)
                                {
                                    if(videoobj.getPath().contains(".mp4"))
                                        videoarraylist.add(videoobj.getPath());
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }finally {
                            //Release stuff
                            extractor.release();
                        }
                    }
                    else
                    {
                        try {
                            if(! file.isDirectory())
                                common.delete(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        setimagethumbnail();
                    }
                });
            }
        }).start();
    }

    public void setimagethumbnail()
    {
        String mediapath="";
        img_mediathumbnail.setImageResource(0);
        img_mediathumbnail.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.transparent));

        if(currentselectedcomposer == 0 && videoarraylist.size() > 0)
        {
            mediapath=videoarraylist.get(videoarraylist.size()-1);
        }
        else if(currentselectedcomposer == 1 && imagearraylist.size() > 0)
        {
            mediapath=imagearraylist.get(imagearraylist.size()-1);
        }
        else if(currentselectedcomposer == 2 && audioarraylist.size() > 0)
        {
            mediapath=audioarraylist.get(audioarraylist.size()-1);
            img_mediathumbnail.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.dark_blue_solid_a));

            RequestOptions options = new RequestOptions();
            options.centerCrop();
          //  options.override(150,100);

            Uri uri= FileProvider.getUriForFile(applicationviavideocomposer.getactivity(),
                    BuildConfig.APPLICATION_ID + ".provider", new File(mediapath));
            Glide.with(applicationviavideocomposer.getactivity()).load(uri).thumbnail(0.1f)
                    .transition(GenericTransitionOptions.with(R.anim.fadein)).
                    apply(options).
                    into(img_mediathumbnail);
        }

        if((currentselectedcomposer == 0 || currentselectedcomposer == 1) && mediapath != null && (! mediapath.trim().isEmpty()))
        {
            Uri uri= FileProvider.getUriForFile(applicationviavideocomposer.getactivity(),
                    BuildConfig.APPLICATION_ID + ".provider", new File(mediapath));
            Glide.with(applicationviavideocomposer.getactivity()).load(uri).thumbnail(0.1f)
                    .transition(GenericTransitionOptions.with(R.anim.fadein)).
                    into(img_mediathumbnail);
        }
    }
}
