package com.deeptruth.app.android.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.deeptruth.app.android.enumclasses.cryptomediatypepagerenum;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.sensor.Orientation;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.xdata;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import kotlin.text.Regex;

import static android.widget.RelativeLayout.TRUE;

/**
 * Created by root on 6/11/18.
 */

public class composeoptionspagerfragment extends basefragment implements View.OnClickListener, Orientation.Listener, View.OnTouchListener {

    @BindView(R.id.txt_timer)
    TextView txt_timer;
    @BindView(R.id.layout_recorder)
    RelativeLayout layout_recorder;
    @BindView(R.id.img_rotate_camera)
    ImageView imgrotatecamera;
    @BindView(R.id.layout_bottom)
    RelativeLayout layoutbottom;
    @BindView(R.id.tab_container)
    FrameLayout tab_container;
    @BindView(R.id.img_mediathumbnail)
    ImageView img_mediathumbnail;
    @BindView(R.id.layout_mediatype)
    RelativeLayout layout_mediatype;
    @BindView(R.id.parentview)
    RelativeLayout parentview;
    @BindView(R.id.txt_encrypting)
    TextView txt_encrypting;
    @BindView(R.id.base_view)
    ViewGroup mParent;
    @BindView(R.id.layout_seekbarzoom)
    RelativeLayout layout_seekbarzoom;
    @BindView(R.id.txt_zoomlevel)
    TextView txt_zoomlevel;
    @BindView(R.id.viewpager)
    ViewPager pagermediatype;

    @BindView(R.id.layout_encryption)
    RelativeLayout layout_encryption;

    videocomposerfragment fragvideocomposer=null;
    audiocomposerfragment fragaudiocomposer=null;
    imagecomposerfragment fragimgcapture=null;
    boolean isfragmentload = false;

    View rootview=null;
    int currentselectedcomposer=0,layoutbottomheight=0,layoutmediatypeheight=0;
    int startrecorder=1,stoprecorder=2,fragmentswipe=3,updatemediathumbnail=4,adjustzoom=5;
    private int flingactionmindstvac,footerlayoutheight=0;
    private  final int flingactionmindspdvac = 100;

    private Runnable doafterallpermissionsgranted;
    private static final int request_permissions = 1;
    ArrayList<String> imagearraylist =new ArrayList<>();
    ArrayList<String> videoarraylist =new ArrayList<>();
    ArrayList<String> audioarraylist =new ArrayList<>();
    private Orientation mOrientation;
    int navigationbarheight = 0;
    int parentviewwidth=0;
    private AlphaAnimation blinkencryptionanimation;
    private Date initialdate;
    private String[] transparentarray=common.gettransparencyvalues();
    GradientDrawable gradientDrawablebutton;
    private volatile boolean iscircle = true;
    Typeface fontfaceregular, fontfacebold;
    private String OPEN_SANS_BOLD = "fonts/OpenSans-Bold.ttf";
    private String OPEN_SANS_REGULAR = "fonts/OpenSans-Regular.ttf";
    private Animator currentAnimator;
    private int shortAnimationDuration;
    boolean zoominout = false;

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
        if(rootview==null)
        {
            rootview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, rootview);
            initialdate =new Date();
            txt_zoomlevel.setOnClickListener(this);
            layout_recorder.setOnClickListener(this);
            imgrotatecamera.setOnClickListener(this);
            img_mediathumbnail.setOnClickListener(this);
            layoutbottom.setOnTouchListener(this);

            fontfaceregular =Typeface.createFromAsset(applicationviavideocomposer.getactivity().getAssets(), OPEN_SANS_REGULAR);
            fontfacebold =Typeface.createFromAsset(applicationviavideocomposer.getactivity().getAssets(), OPEN_SANS_BOLD);
            gethelper().drawerenabledisable(false);

            navigationbarheight =  common.getnavigationbarheight();
            setfooterlayout();
            setactionbartransparency(65);

            parentview.post(new Runnable() {
                @Override
                public void run() {

                    parentviewwidth=parentview.getWidth();
                }
            });
            layoutbottom.post(new Runnable() {
                @Override
                public void run() {
                    footerlayoutheight=layoutbottom.getHeight();
                    layoutbottomheight=layoutbottom.getHeight();
                    layout_mediatype.post(new Runnable() {
                        @Override
                        public void run() {
                            layoutmediatypeheight=layout_mediatype.getHeight();
                        }
                    });
                }
            });

            mOrientation = new Orientation(applicationviavideocomposer.getactivity());
            gradientDrawablebutton = new GradientDrawable();
            gradientDrawablebutton.setCornerRadius(360.0f);
            gradientDrawablebutton.setShape(GradientDrawable.RECTANGLE);
            mParent.setBackground(gradientDrawablebutton);

            pagermediatype.setAdapter(new CustomPagerAdapter(applicationviavideocomposer.getactivity()));
            pagermediatype.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                    currentselectedcomposer=position;
                    int currentpagerpos=position;
                    isfragmentload = false;
                    if(currentpagerpos == 0)
                        currentpagerpos=1;
                    else if(currentpagerpos == 1)
                        currentpagerpos=2;
                    else if(currentpagerpos == 2)
                        currentpagerpos=3;

                    for(int i = 0; i<= cryptomediatypepagerenum.values().length; i++)
                    {
                        if(pagermediatype != null)
                        {
                            View view= pagermediatype.getChildAt(i);
                            if(view != null)
                            {
                                TextView txt_mediatype=(TextView)view.findViewById(R.id.txt_mediatype);
                                if(currentpagerpos == i)
                                {
                                    txt_mediatype.setTextColor(applicationviavideocomposer.getactivity().getResources().
                                            getColor(R.color.wave_blue));
                                    txt_mediatype.setTypeface(fontfacebold, Typeface.BOLD);
                                    txt_mediatype.setTextSize(13f);
                                }
                                else
                                {
                                    txt_mediatype.setTextColor(Color.WHITE);
                                    txt_mediatype.setTypeface(fontfaceregular, Typeface.NORMAL);
                                    txt_mediatype.setTextSize(12f);
                                }
                            }
                        }
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showselectedfragment();
                        }
                    },200);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }

        return rootview;
    }

    @Override
    public void onPause() {
        super.onPause();
        gethelper().setisrecordrunning(false);
    }

    public class CustomPagerAdapter extends PagerAdapter {

        private Context mContext;

        public CustomPagerAdapter(Context context) {
            mContext = context;
        }

        @Override
        public Object instantiateItem(ViewGroup collection, final int position) {
            final cryptomediatypepagerenum enummediatype = cryptomediatypepagerenum.values()[position];
            LayoutInflater inflater = LayoutInflater.from(mContext);
            ViewGroup layout = (ViewGroup) inflater.inflate(enummediatype.getLayoutResId(), collection, false);
            final TextView txt_mediatype=(TextView)layout.findViewById(R.id.txt_mediatype);
            txt_mediatype.setText(enummediatype.getItemname());
            txt_mediatype.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("onClick ",""+ enummediatype.getItemposition());
                    if(enummediatype.getItemposition() == 1)
                    {
                        pagermediatype.setCurrentItem(0,true);
                    }
                    else if(enummediatype.getItemposition() == 2)
                    {
                        pagermediatype.setCurrentItem(1,true);
                    }
                    else if(enummediatype.getItemposition() == 3)
                    {
                        pagermediatype.setCurrentItem(2,true);
                    }
                }
            });
            collection.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public float getPageWidth(int position) {
            return 0.33f;
        }

        @Override
        public int getCount() {
            return cryptomediatypepagerenum.values().length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            cryptomediatypepagerenum enummediatype = cryptomediatypepagerenum.values()[position];
            return enummediatype.getItemname();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if(! iscircle)
            makecircle();

        if(mOrientation != null)
            mOrientation.stopListening();

        composecallback(null,stoprecorder);
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
            if (common.getlocationdeniedpermissions().isEmpty()) {
                // All permissions are granted
                doafterpermissionsgranteddenied();
            } else {
                //String[] array = new String[deniedpermissions.size()];

                String[] array = new String[common.getlocationdeniedpermissions().size()];
                array = common.getlocationdeniedpermissions().toArray(array);
                if(array.length > 0)
                {
                    final String[] finalArray = array;
                    common.showcustompermissiondialog(applicationviavideocomposer.getactivity(), new adapteritemclick() {
                        @Override
                        public void onItemClicked(Object object) {
                        }

                        @Override
                        public void onItemClicked(Object object, int type) {
                            if(type == 0)
                            {
                                doafterpermissionsgranteddenied();
                            }
                            else if(type == 1)
                            {
                                ActivityCompat.requestPermissions(getActivity(), finalArray, request_permissions);
                            }
                        }
                    },array[0]);
                }
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
                    try
                    {
                        initviewpager();
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        },10);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void initviewpager()
    {
        flingactionmindstvac=common.getcomposerswipearea();
        currentselectedcomposer=config.selectedmediatype;
        pagermediatype.setCurrentItem(currentselectedcomposer,true);

        getlatestthumbnailfromdirectory(false);

        int currentpagerpos=currentselectedcomposer;
        if(currentpagerpos == 0)
            currentpagerpos=1;
        else if(currentpagerpos == 1)
            currentpagerpos=2;
        else if(currentpagerpos == 2)
            currentpagerpos=3;

        if(currentpagerpos == 1)
        {
            for(int i=0;i<=4;i++)
            {
                if(pagermediatype != null)
                {
                    View view= pagermediatype.getChildAt(i);
                    if(view != null)
                    {
                        TextView txt_mediatype=(TextView)view.findViewById(R.id.txt_mediatype);
                        if(currentpagerpos == i)
                        {
                            txt_mediatype.setTextColor(applicationviavideocomposer.getactivity().getResources().
                                    getColor(R.color.wave_blue));
                            txt_mediatype.setTypeface(fontfacebold, Typeface.BOLD);
                            txt_mediatype.setTextSize(13f);
                        }
                        else
                        {
                            txt_mediatype.setTextColor(Color.WHITE);
                            txt_mediatype.setTypeface(fontfaceregular, Typeface.NORMAL);
                            txt_mediatype.setTextSize(12f);
                        }
                    }
                }
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showselectedfragment();
                }
            },200);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.txt_zoomlevel:
                if(currentselectedcomposer == 0 && fragvideocomposer != null)
                    fragvideocomposer.adjustzoom();
                else if(currentselectedcomposer == 1  && fragimgcapture != null)
                    fragimgcapture.adjustzoom();
                break;
            case R.id.layout_recorder:
                layout_recorder.setEnabled(false);
                new Handler().postDelayed(new Runnable()
                {
                    public void run()
                    {
                        layout_recorder.setEnabled(true);
                    }
                }, 1000);

                if(currentselectedcomposer == 0 || currentselectedcomposer == 2)
                {
                    if (iscircle) {
                        makesquare();
                    }
                    else {
                        makecircle();
                    }
                }

                if(currentselectedcomposer == 0)
                {
                    if(fragvideocomposer != null  && isfragmentload)
                        fragvideocomposer.startstopvideo();
                }
                else if(currentselectedcomposer == 1)
                {
                    if(fragimgcapture != null  && isfragmentload)
                    {
                        if(fragimgcapture != null && (! fragimgcapture.isimagecaptureprocessing))
                        {
                            txt_timer.setText("");
                            txt_timer.setVisibility(View.VISIBLE);
                            enableDisableView(parentview,true);

                            ObjectAnimator animation = ObjectAnimator.ofInt(txt_timer, "backgroundColor", Color.BLACK);
                            animation.setDuration(20);
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
                                }

                                @Override
                                public void onAnimationEnd(Animator animation, boolean isReverse) {
                                    txt_timer.setVisibility(View.GONE);
                                }
                            });
                            animation.start();

                            view.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.image_click));

                            if(fragimgcapture != null && (! fragimgcapture.isimagecaptureprocessing))
                                fragimgcapture.takePicture();
                        }
                    }
                }
                else if(currentselectedcomposer == 2)
                {
                    try {
                        if(fragaudiocomposer != null && isfragmentload)
                             fragaudiocomposer.startstopaudiorecording();
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.img_rotate_camera:
                imgrotatecamera.setEnabled(false);
                new Handler().postDelayed(new Runnable()
                {
                    public void run()
                    {
                        imgrotatecamera.setEnabled(true);
                    }
                }, 1000);
                if(currentselectedcomposer == 0)
                {
                    if(fragvideocomposer != null)
                        fragvideocomposer.switchCamera(imgrotatecamera);
                }
                else if(currentselectedcomposer == 1)
                {
                    if(fragimgcapture != null)
                        fragimgcapture.switchCamera(imgrotatecamera);
                }
                break;

            case R.id.img_mediathumbnail:
                config.selectedmediatype=currentselectedcomposer;
                medialistitemaddbroadcast();
                gethelper().switchtomedialist();
                break;
        }
    }

    public void enableDisableView(View view, boolean enabled) {
        view.setEnabled(enabled);
        if ( view instanceof ViewGroup ) {
            ViewGroup group = (ViewGroup)view;
            for ( int idx = 0 ; idx < group.getChildCount() ; idx++ ) {
                enableDisableView(group.getChildAt(idx), enabled);
            }
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
        Date currentdate=new Date();
        int seconddifference= (int) (Math.abs(initialdate.getTime()-currentdate.getTime())/1000);
        if(seconddifference >= 1)
        {
            if(currentselectedcomposer == 0)
                return;
            currentselectedcomposer--;
            pagermediatype.setCurrentItem(currentselectedcomposer,true);
            initialdate =new Date();
        }
    }

    public void swiperighttoleft()
    {
        Date currentdate=new Date();
        int seconddifference= (int) (Math.abs(initialdate.getTime()-currentdate.getTime())/1000);
        if(seconddifference >= 1)
        {
            if(currentselectedcomposer == 2)
                return;
            currentselectedcomposer++;
            pagermediatype.setCurrentItem(currentselectedcomposer,true);
            initialdate =new Date();
        }
    }

    public void showselectedfragment()
    {
        if (! iscircle)
            makecircle();

        gethelper().setisrecordrunning(false);
        xdata.getinstance().saveSetting(config.istravelleddistanceneeded,"false");
        txt_zoomlevel.setText("1.0 x");
        switch (currentselectedcomposer)
        {
            case 0:
                imgrotatecamera.setVisibility(View.VISIBLE);
                layout_seekbarzoom.setVisibility(View.VISIBLE);
                showhideactionbottombaricon(1);
                if(fragvideocomposer == null)
                    fragvideocomposer=new videocomposerfragment();

                fragvideocomposer.setData(false, mitemclick,layoutbottom,layout_seekbarzoom);
                gethelper().replacetabfragment(fragvideocomposer,false,true);
                isfragmentload = true;
            break;

            case 1:
                imgrotatecamera.setVisibility(View.VISIBLE);
                layout_seekbarzoom.setVisibility(View.VISIBLE);
                showhideactionbottombaricon(1);
                if(fragimgcapture == null)
                    fragimgcapture=new imagecomposerfragment();

                fragimgcapture.setData(mitemclick,layoutbottom,layout_seekbarzoom);
                gethelper().replacetabfragment(fragimgcapture,false,true);
                isfragmentload = true;
            break;

            case 2:
                imgrotatecamera.setVisibility(View.GONE);
                layout_seekbarzoom.setVisibility(View.GONE);
                showhideactionbottombaricon(3);
                if(fragaudiocomposer == null)
                    fragaudiocomposer=new audiocomposerfragment();

                fragaudiocomposer.setData(mitemclick,layoutbottom,layoutbottomheight);
                gethelper().replacetabfragment(fragaudiocomposer,false,true);
                isfragmentload = true;
            break;
        }

        setimagethumbnail(false);
    }

    adapteritemclick mitemclick=new adapteritemclick() {
        @Override
        public void onItemClicked(Object object) {

        }

        @Override
        public void onItemClicked(Object object, int type) {
            composecallback(object,type);
        }
    };

    public void composecallback(Object object,int type)
    {
        if(type == startrecorder) // for video record start,audio record start and image capture button click
        {
            gethelper().setisrecordrunning(true);
            if(currentselectedcomposer == 0)
            {
                showhideactionbottombaricon(0);
            }
            else if(currentselectedcomposer == 2)
            {
                showhideactionbottombaricon(2);
            }
            xdata.getinstance().saveSetting(config.istravelleddistanceneeded,"true");
            startblinkencryption();
        }
        else if(type == stoprecorder) // for video record stop,audio record stop and image captured button click
        {
            gethelper().setisrecordrunning(false);
            if(currentselectedcomposer == 0)
            {
                showhideactionbottombaricon(1);
            }
            else if(currentselectedcomposer == 2)
            {
                showhideactionbottombaricon(3);
            }
            xdata.getinstance().saveSetting(config.istravelleddistanceneeded,"false");
            stopblinkencryption();
        }
        else if(type == fragmentswipe) // For swipe gesture to change fragment
        {
            if(object != null)
            {
                MotionEvent motionevent=(MotionEvent)object;
                flingswipegesture.onTouchEvent(motionevent);
            }
        }
        else if(type == updatemediathumbnail) // Back the fragment and navigate to media list
        {
            try {
                config.selectedmediatype=currentselectedcomposer;
            }catch (Exception e)
            {
                e.printStackTrace();
            }
            getlatestthumbnailfromdirectory(true);
            xdata.getinstance().saveSetting(config.istravelleddistanceneeded,"false");
        }
        else if(type == adjustzoom)
        {
            txt_zoomlevel.setText(object.toString());
        }
    }

    public void startblinkencryption()
    {
        blinkencryptionanimation = new AlphaAnimation(0.0f, 1.0f);
        blinkencryptionanimation.setDuration(1000); //You can manage the time of the blink with this parameter
        blinkencryptionanimation.setStartOffset(1000);
        blinkencryptionanimation.setRepeatMode(Animation.REVERSE);
        blinkencryptionanimation.setRepeatCount(Animation.INFINITE);
        txt_encrypting.startAnimation(blinkencryptionanimation);
    }

    public void stopblinkencryption()
    {
        if(blinkencryptionanimation != null)
            blinkencryptionanimation.cancel();
    }

    public void getlatestthumbnailfromdirectory(boolean zoominoutimage)
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
            setimagethumbnail(zoominoutimage);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void setimagethumbnail(boolean zoominoutimage)
    {
        img_mediathumbnail.setImageResource(0);
        img_mediathumbnail.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.transparent));

        if(currentselectedcomposer == 0)
        {
            if(videoarraylist.size() > 0)
            {
               setthumbnailimage(videoarraylist.get(0),zoominoutimage);
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
                setthumbnailimage(imagearraylist.get(0),zoominoutimage);
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
                setthumbnailimage(audioarraylist.get(0),zoominoutimage);
                img_mediathumbnail.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.dark_blue_solid_a));
            }
            else
            {
                img_mediathumbnail.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.dark_blue_solid_a));
            }
        }
    }

    public void setthumbnailimage(String mediapath,boolean zoominoutimage)
    {
        Uri uri= FileProvider.getUriForFile(applicationviavideocomposer.getactivity(),
                BuildConfig.APPLICATION_ID + ".provider", new File(mediapath));
        if(! zoominoutimage){
          Glide.with(applicationviavideocomposer.getactivity()).load(uri).thumbnail(0.1f)
                .transition(GenericTransitionOptions.with(R.anim.fadein)).
                into(img_mediathumbnail);
        }else{
            zoomImageFromThumb(img_mediathumbnail,uri);
            shortAnimationDuration = 10; //zoomed up ....animation speed
        }
    }

    public void showhideactionbottombaricon(int i){
        if(i == 0){
            img_mediathumbnail.setVisibility(View.INVISIBLE);
            imgrotatecamera.setVisibility(View.INVISIBLE);
            layout_mediatype.setVisibility(View.GONE);
            txt_encrypting.setVisibility(View.VISIBLE);
            layout_encryption.setVisibility(View.VISIBLE);
        } else if(i == 1){
            img_mediathumbnail.setVisibility(View.VISIBLE);
            txt_encrypting.setVisibility(View.INVISIBLE);
            layout_encryption.setVisibility(View.INVISIBLE);
            imgrotatecamera.setVisibility(View.VISIBLE);
            layout_mediatype.setVisibility(View.VISIBLE);
        } else if( i == 2){
            layout_mediatype.setVisibility(View.GONE);
            img_mediathumbnail.setVisibility(View.INVISIBLE);
            txt_encrypting.setVisibility(View.VISIBLE);
            layout_encryption.setVisibility(View.VISIBLE);
        }
        else if( i == 3){
            layout_mediatype.setVisibility(View.VISIBLE);
            txt_encrypting.setVisibility(View.INVISIBLE);
            layout_encryption.setVisibility(View.INVISIBLE);
            img_mediathumbnail.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onOrientationChanged(float pitch, float roll) {

    }

    @Override
    public void onOrientationChanged(float[] adjustedRotationMatrix, float[] orientation) {
        float pitch = orientation[1] * -57;

        if(pitch <= -50)
            return;

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
        else if(roll < 45 && roll < -120)
        {
            rotateangle=180;
        }

        if(rotateangle != 3600)
        {
            if(txt_encrypting != null)
            {
                if( fragvideocomposer != null && (! fragvideocomposer.isvideorecording) && currentselectedcomposer == 0){
                    layout_encryption.setRotation(rotateangle);
                    layout_seekbarzoom.setRotation(rotateangle);
                }

                if(fragaudiocomposer != null && currentselectedcomposer == 2 && !fragaudiocomposer.isaudiorecording){
                    layout_encryption.setRotation(rotateangle);
                    layout_seekbarzoom.setRotation(rotateangle);
                }
            }

            if(imgrotatecamera != null)
                imgrotatecamera.setRotation(rotateangle);

            if(img_mediathumbnail != null)
                img_mediathumbnail.setRotation(rotateangle);

            if(fragimgcapture != null)
                fragimgcapture.changeiconsorientation(rotateangle);

            if(fragaudiocomposer != null)
                fragaudiocomposer.changeiconsorientation(rotateangle);

            if(fragvideocomposer != null)
                fragvideocomposer.changeiconsorientation(rotateangle);
            if(rotateangle == 90 || rotateangle == -90){
                txt_encrypting.setTextSize(8);
            }else{
                txt_encrypting.setTextSize(10);
            }

        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        switch (view.getId())
        {
            case  R.id.layout_bottom:
                try
                {
                    if(currentselectedcomposer == 0 && (fragvideocomposer != null && (fragvideocomposer.isvideorecording)))
                    {
                        return true;
                    }
                    if(currentselectedcomposer == 2 && (fragaudiocomposer != null && (fragaudiocomposer.isaudiorecording)))
                    {
                        return true;
                    }
                    if (motionEvent.getPointerCount() == 1)
                        mitemclick.onItemClicked(motionEvent,fragmentswipe);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        return true;
    }

    public void setfooterlayout()
    {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,TRUE);
        params.setMargins(0,0,0,navigationbarheight);
        parentview.setLayoutParams(params);
    }

    public void setactionbartransparency(int progress)
    {
        String colorString="#"+transparentarray[progress]+"004860";
        layoutbottom.setBackgroundColor(Color.parseColor(colorString));
    }

    private void makecircle()
    {
        ObjectAnimator cornerAnimation = ObjectAnimator.ofFloat(gradientDrawablebutton, "cornerRadius", 30.0f, 200.0f);
        Animator shiftAnimation = AnimatorInflater.loadAnimator(getActivity(), R.animator.slide_right_down);
        shiftAnimation.setTarget(mParent);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(300);
        animatorSet.playTogether(cornerAnimation, shiftAnimation);
        animatorSet.start();
        iscircle = !iscircle;
    }

    private void makesquare()
    {
        ObjectAnimator cornerAnimation = ObjectAnimator.ofFloat(gradientDrawablebutton, "cornerRadius",100f,10.0f);
        Animator shiftAnimation = AnimatorInflater.loadAnimator(getActivity(), R.animator.slide_left_up);
        shiftAnimation.setTarget(mParent);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(300);
        animatorSet.playTogether(cornerAnimation, shiftAnimation);
        animatorSet.start();
        iscircle = !iscircle;
    }

    private void zoomImageFromThumb(final ImageView thumbView, final Uri uri) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (currentAnimator != null) {
            currentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) findViewById(R.id.expanded_image);
        Glide.with(applicationviavideocomposer.getactivity()).load(uri).thumbnail(0.1f)
               .transition(GenericTransitionOptions.with(R.anim.fadein)).
                into(expandedImageView);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.container)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            Log.e("startscale",""+startScale);
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        expandedImageView.setVisibility(View.INVISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView,
                        View.SCALE_Y, startScale, 1f));
        set.setDuration(shortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                currentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                currentAnimator = null;
            }
        });
        set.start();
        currentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        Log.e("startscale",""+startScaleFinal);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setonmediathumbnail(expandedImageView,startBounds,startScaleFinal,thumbView,uri);
                expandedImageView.setVisibility(View.VISIBLE);
            }
        },1000);


    }

    public void setonmediathumbnail(final ImageView expandedImageView, Rect startBounds, float startScaleFinal, final ImageView thumbView, final Uri uri){
        if (currentAnimator != null) {
            currentAnimator.cancel();
        }
        // Animate the four positioning/sizing properties in parallel,
        // back to their original values.\
        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator
                .ofFloat(expandedImageView, View.X, startBounds.left))
                .with(ObjectAnimator
                        .ofFloat(expandedImageView,
                                View.Y,startBounds.top))
                .with(ObjectAnimator
                        .ofFloat(expandedImageView,
                                View.SCALE_X, startScaleFinal))
                .with(ObjectAnimator
                        .ofFloat(expandedImageView,
                                View.SCALE_Y, startScaleFinal));
        set.setDuration(400);//600 zoomed down to left corner ..animation speed
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
               thumbView.setAlpha(1f);
                Glide.with(applicationviavideocomposer.getactivity()).load(uri).thumbnail(0.1f)
                        .transition(GenericTransitionOptions.with(R.anim.fadein)).
                        into(thumbView);

                Animation out = AnimationUtils.loadAnimation(getActivity(),R.anim.fadeoutanimation);
                expandedImageView.startAnimation(out);
                expandedImageView.setVisibility(View.INVISIBLE);
                currentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                thumbView.setAlpha(1f);
                Glide.with(applicationviavideocomposer.getactivity()).load(uri).thumbnail(0.1f)
                        .transition(GenericTransitionOptions.with(R.anim.fadein)).
                        into(thumbView);

                Animation out = AnimationUtils.loadAnimation(getActivity(),R.anim.fadeoutanimation);
                expandedImageView.startAnimation(out);
                expandedImageView.setVisibility(View.INVISIBLE);
            }
        });
        set.start();
        currentAnimator = set;
    }
}
