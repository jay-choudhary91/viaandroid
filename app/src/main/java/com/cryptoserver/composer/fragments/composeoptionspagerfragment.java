package com.cryptoserver.composer.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
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
import com.cryptoserver.composer.BuildConfig;
import com.cryptoserver.composer.R;
import com.cryptoserver.composer.applicationviavideocomposer;
import com.cryptoserver.composer.interfaces.adapteritemclick;
import com.cryptoserver.composer.models.permissions;
import com.cryptoserver.composer.models.video;
import com.cryptoserver.composer.utils.common;
import com.cryptoserver.composer.utils.config;
import com.cryptoserver.composer.views.pageranimation;
import com.cryptoserver.composer.views.pagercustomduration;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by root on 6/11/18.
 */

public class composeoptionspagerfragment extends basefragment implements View.OnClickListener {

    @BindView(R.id.pager_footer)
    pagercustomduration pagerfooter;

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

    videocomposerfragment fragvideocomposer=null;
    audiocomposerfragment fragaudiocomposer=null;
    imagecomposerfragment fragimgcapture=null;

    View rootview=null;
    int pageritems = 3,currentselectedcomposer=0;

    private int flingactionmindstvac;
    private  final int flingactionmindspdvac = 100;

    private Runnable doafterallpermissionsgranted;
    private static final int request_permissions = 1;
    ArrayList<permissions> permissionslist =new ArrayList<>();

    ArrayList<String> imagearraylist =new ArrayList<>();
    ArrayList<String> videoarraylist =new ArrayList<>();
    ArrayList<String> audioarraylist =new ArrayList<>();
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
                permissionslist.add(new permissions(Manifest.permission.CAMERA,false,false));
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
                doafterallpermissionsgranted();
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
                        doafterallpermissionsgranted();
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

    private void doafterallpermissionsgranted() {
        if(fragvideocomposer == null)
        {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    initviewpager();
                }
            },10);
        }
    }

    public void initviewpager()
    {
        mrecordimagebutton.setImageResource(R.drawable.img_startrecord);
        mrecordimagebutton.setBackgroundResource(R.drawable.shape_recorder_off);

        flingactionmindstvac=common.getcomposerswipearea();
        //pagerfooter.setPageTransformer(false, new pageranimation());

        //pagerheader.setAdapter(headeradapter);
        pagerfooter.setAdapter(new footerpageradapter(getChildFragmentManager()));

        pagerfooter.setOffscreenPageLimit(3);
        int margin=(int)dipToPixels(applicationviavideocomposer.getactivity(),180);
        pagerfooter.setPageMargin(0-margin);

        pagerfooter.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Log.e("position2 ",""+position);
                if(position != currentselectedcomposer)
                {
                    currentselectedcomposer=position;
                    showselectedfragment();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        pagerfooter.setOnItemClickListener(new pagercustomduration.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                pagerfooter.setCurrentItem(position,true);
            }
        });

        currentselectedcomposer=config.selectedmediatype;
        showselectedfragment();
        getlatestmediafromdirectory();
        getlatestaudiofromdirectory();
    }

    public float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.img_video_capture:
                if(currentselectedcomposer == 0)
                {
                    if(fragvideocomposer != null)
                        fragvideocomposer.startstopvideo();
                }
                else if(currentselectedcomposer == 1)
                {
                    if(fragimgcapture != null)
                        fragimgcapture.takePicture();

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
                config.selectedmediatype=pagerfooter.getCurrentItem();
                medialistitemaddbroadcast();
                gethelper().onBack();
                break;
        }
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

        currentselectedcomposer--;
        showselectedfragment();
    }

    public void swiperighttoleft()
    {
        if(currentselectedcomposer == 2)
            return;

        currentselectedcomposer++;
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
                pagerfooter.setCurrentItem(currentselectedcomposer,true);
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
                    config.selectedmediatype=pagerfooter.getCurrentItem();
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
                gethelper().onBack();
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
            long file_size = file.length();
            if(file_size >= 0)
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
                    long file_size = file.length();
                    if(file_size >= 0)
                    {
                        video videoobj=new video();
                        videoobj.setPath(file.getAbsolutePath());

                        MediaExtractor extractor = new MediaExtractor();
                        try {
                            if(videoobj.getPath().contains(".jpg") || videoobj.getPath().contains(".png") || videoobj.getPath().contains(".jpeg"))
                            {
                                imagearraylist.add(videoobj.getPath());
                            }
                            else
                            {
                                //Adjust data source as per the requirement if file, URI, etc.
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
        }

        if(mediapath != null && (! mediapath.trim().isEmpty()))
        {
            Uri uri= FileProvider.getUriForFile(applicationviavideocomposer.getactivity(),
                    BuildConfig.APPLICATION_ID + ".provider", new File(mediapath));
            Glide.with(applicationviavideocomposer.getactivity()).load(uri).thumbnail(0.1f)
                    .transition(GenericTransitionOptions.with(R.anim.fadein)).
                    into(img_mediathumbnail);
        }
    }

    private class footerpageradapter extends FragmentPagerAdapter {

        public footerpageradapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {

                case 0: return composefooterpagerfragment.newInstance("VIDEO");
                case 1: return composefooterpagerfragment.newInstance("PHOTO");
                case 2: return composefooterpagerfragment.newInstance("AUDIO");
                default: return composefooterpagerfragment.newInstance("VIDEO");
            }
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return pageritems;
        }
    }
}
