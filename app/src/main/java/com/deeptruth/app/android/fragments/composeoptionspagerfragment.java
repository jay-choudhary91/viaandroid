package com.deeptruth.app.android.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
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
import com.deeptruth.app.android.BuildConfig;
import com.deeptruth.app.android.R;
import com.deeptruth.app.android.activity.locationawareactivity;
import com.deeptruth.app.android.adapter.adaptercomposemediatype;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.database.databasemanager;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.models.mediatype;
import com.deeptruth.app.android.sensor.Orientation;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.xdata;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.droidsonroids.gif.GifDrawable;

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
    @BindView(R.id.txt_mediatype_a)
    TextView txt_mediatype_a;
    @BindView(R.id.txt_mediatype_b)
    TextView txt_mediatype_b;
    @BindView(R.id.txt_mediatype_c)
    TextView txt_mediatype_c;
    @BindView(R.id.layout_mediatype)
    RelativeLayout layout_mediatype;
    @BindView(R.id.parentview)
    RelativeLayout parentview;
    @BindView(R.id.txt_space_a)
    TextView txt_space_a;
    @BindView(R.id.txt_space_b)
    TextView txt_space_b;
    @BindView(R.id.txt_encrypting)
    TextView txt_encrypting;
    @BindView(R.id.base_view)
    ViewGroup mParent;
    @BindView(R.id.layout_seekbarzoom)
    RelativeLayout layout_seekbarzoom;
    @BindView(R.id.txt_zoomlevel)
    TextView txt_zoomlevel;

    @BindView(R.id.layout_encryption)
    RelativeLayout layout_encryption;
    @BindView(R.id.centersnaprecyclerview)
    RecyclerView centersnaprecyclerview;

    videocomposerfragment fragvideocomposer=null;
    audiocomposerfragment fragaudiocomposer=null;
    imagecomposerfragment fragimgcapture=null;

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
    ArrayList<mediatype> mediatypeArrayList =new ArrayList<>();
    private Handler myHandler;
    private Runnable myRunnable;

    private CountDownTimer countertimer;

    private Orientation mOrientation;
    private static final int ORIENTATION_0 = 0;
    private static final int ORIENTATION_90 = 3;
    private static final int ORIENTATION_270 = 1;
    int navigationbarheight = 0;
    int parentviewwidth=0;
    private AlphaAnimation blinkencryptionanimation;
    private adaptercomposemediatype centersnapadapter;
    private Date initialdate;
    private String[] transparentarray=common.gettransparencyvalues();
    GradientDrawable gradientDrawablebutton;
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
        if(rootview==null)
        {/* gifdrawable = new GifDrawable(getResources(), R.drawable.recorder_transparent);
                gifdrawable.setLoopCount(0);
                gifdrawable.setSpeed(1.0f);
                recordstartstopbutton.setImageDrawable(gifdrawable);
                recordstartstopbutton.setAlpha(0f);
                recordstartstopbutton.animate().alpha(1.0f).setDuration(100).setListener(null);
                gifdrawable.pause();
                gifdrawable.seekTo(0);*/
            rootview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, rootview);

            initialdate =new Date();
            txt_zoomlevel.setOnClickListener(this);
            layout_recorder.setOnClickListener(this);
            imgrotatecamera.setOnClickListener(this);
            img_mediathumbnail.setOnClickListener(this);
            txt_mediatype_a.setOnClickListener(this);
            txt_mediatype_b.setOnClickListener(this);
            txt_mediatype_c.setOnClickListener(this);
            layoutbottom.setOnTouchListener(this);
            layout_mediatype.setOnTouchListener(this);

            navigationbarheight =  common.getnavigationbarheight();
            setfooterlayout();
            setactionbartransparency(65);

            try {


               /* gifdrawable = new GifDrawable(getResources(), R.drawable.recorder_transparent);
                gifdrawable.setLoopCount(0);
                gifdrawable.setSpeed(1.0f);
                recordstartstopbutton.setImageDrawable(gifdrawable);
                recordstartstopbutton.setAlpha(0f);
                recordstartstopbutton.animate().alpha(1.0f).setDuration(100).setListener(null);
                gifdrawable.pause();
                gifdrawable.seekTo(0);*/
            } catch (Exception e) {
                e.printStackTrace();
            }

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

        }

        return rootview;
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

    public void resetbuttonviews(final TextView textView1, final TextView textView2, final TextView textView3)
    {
        //textView1.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.wave_blue));
        textView2.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));
        textView3.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.white));

        Integer colorFrom = getResources().getColor(R.color.white);
        Integer colorTo = getResources().getColor(R.color.wave_blue);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                textView1.setTextColor((Integer)animator.getAnimatedValue());
            }
        });
        colorAnimation.setDuration(config.transition_fragment_millis_300);
        colorAnimation.start();

        ValueAnimator alphaAnimation = ValueAnimator.ofFloat(0.5f,1.0f);
        alphaAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                textView1.setAlpha((Float) animator.getAnimatedValue());
                textView2.setAlpha((Float) animator.getAnimatedValue());
                textView3.setAlpha((Float) animator.getAnimatedValue());
            }
        });
        alphaAnimation.setDuration(config.transition_fragment_millis_300);
        alphaAnimation.start();
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

        if(myHandler != null && myRunnable != null)
            myHandler.removeCallbacks(myRunnable);

        if(countertimer != null)
            countertimer.cancel();
    }



    public void runhandler()
    {
        if(myHandler != null && myRunnable != null)
            myHandler.removeCallbacks(myRunnable);

        myHandler =new Handler();
        myRunnable = new Runnable() {
            @Override
            public void run() {


                myHandler.postDelayed(this, 1000);
            }
        };
        myHandler.post(myRunnable);
    }

    adapteritemclick mlistitemclick=new adapteritemclick() {
        @Override
        public void onItemClicked(Object object) {

        }

        @Override
        public void onItemClicked(Object object, int position) {

            fetchrecyclerposition(position);
            //setrecyclerposition(position);
        }
    };

    public void fetchrecyclerposition(int position)
    {
        if(position == 2 || position == 3 || position == 4)
        {
            setrecyclerposition(position);
            if(position == 2)
            {
                currentselectedcomposer=0;
                showselectedfragment();
            }
            else if(position == 3)
            {
                currentselectedcomposer=1;
                showselectedfragment();
            }
            else if(position == 4)
            {
                currentselectedcomposer=2;
                showselectedfragment();
            }

        }
    }

    public void setrecyclerposition(int position)
    {
        if(position == 2 || position == 3 || position == 4)
        {
            int width=common.getScreenWidth(applicationviavideocomposer.getactivity());
            width=width/5;
            if(position == 2)
            {
                width=0;
            }
            else if(position == 3)
            {

            }
            else if(position == 4)
            {
                width=width*2;
            }
            int offset=(int)width;
            LinearLayoutManager layoutManager = ((LinearLayoutManager)centersnaprecyclerview.getLayoutManager());
            layoutManager.scrollToPositionWithOffset(0,-offset);
            changecenteritemcolor(position);
        }
    }

    public void changecenteritemcolor(int position)
    {
        for(int i=0;i<mediatypeArrayList.size();i++)
        {
            mediatypeArrayList.get(i).setIsmediaselected(false);
            if(position == i)
                mediatypeArrayList.get(i).setIsmediaselected(true);
        }
        centersnapadapter.notifyDataSetChanged();
    }

    private void setUpRecyclerView() {

        if(mediatypeArrayList.size() == 0)
        {
            mediatypeArrayList.clear();
            mediatypeArrayList.add(new mediatype("",""));
            mediatypeArrayList.add(new mediatype("",""));
            mediatypeArrayList.add(new mediatype("VIDEO",""));
            mediatypeArrayList.add(new mediatype("PHOTO",""));
            mediatypeArrayList.add(new mediatype("AUDIO",""));
            mediatypeArrayList.add(new mediatype("",""));
            mediatypeArrayList.add(new mediatype("",""));

            int width=common.getScreenWidth(applicationviavideocomposer.getactivity());
        /*final SpeedyLinearLayoutManager layoutmanager=new SpeedyLinearLayoutManager(applicationviavideocomposer.getactivity(),
                SpeedyLinearLayoutManager.HORIZONTAL, false);*/
            final LinearLayoutManager layoutmanager
                    = new LinearLayoutManager(applicationviavideocomposer.getactivity(), LinearLayoutManager.HORIZONTAL, false);
            centersnaprecyclerview.setLayoutManager(layoutmanager);
            centersnapadapter = new adaptercomposemediatype(applicationviavideocomposer.getactivity(), mediatypeArrayList,width,mlistitemclick);
            centersnaprecyclerview.setAdapter(centersnapadapter);
            final SnapHelper snapHelperCenter = new LinearSnapHelper();
            snapHelperCenter.attachToRecyclerView(centersnaprecyclerview);

            centersnaprecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    switch (newState) {
                        case RecyclerView.SCROLL_STATE_IDLE:
                            int center = common.getScreenWidth(applicationviavideocomposer.getactivity())/2;
                            View view =snapHelperCenter.findSnapView(layoutmanager);
                            //View centerView = centersnaprecyclerview.findChildViewUnder(center, centersnaprecyclerview.getTop());
                            int centerPos = centersnaprecyclerview.getChildAdapterPosition(view);
                            if(centerPos == 2 && currentselectedcomposer != 0)
                                fetchrecyclerposition(centerPos);

                            if(centerPos == 3 && currentselectedcomposer != 1)
                                fetchrecyclerposition(centerPos);

                            if(centerPos == 4 && currentselectedcomposer != 2)
                                fetchrecyclerposition(centerPos);
                            break;
                        case RecyclerView.SCROLL_STATE_DRAGGING:
                            break;
                        case RecyclerView.SCROLL_STATE_SETTLING:
                            break;
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                }
            });
        }
    }

    public void initviewpager()
    {
        flingactionmindstvac=common.getcomposerswipearea();
        currentselectedcomposer=config.selectedmediatype;

        parentview.post(new Runnable() {
            @Override
            public void run() {

                if(currentselectedcomposer == 0)
                {
                    fixbottomtabwidth(txt_space_a,parentviewwidth/5);
                    fixbottomtabwidth(txt_space_b,parentviewwidth/5);
                }
                else if(currentselectedcomposer == 1)
                {
                    fixbottomtabwidth(txt_space_a,0);
                    fixbottomtabwidth(txt_space_b,parentviewwidth/5);
                }
                else if(currentselectedcomposer == 2)
                {
                    fixbottomtabwidth(txt_space_a,0);
                    fixbottomtabwidth(txt_space_b,0);
                }

                fixbottomtabwidth(txt_mediatype_a,parentviewwidth/5);
                fixbottomtabwidth(txt_mediatype_b,parentviewwidth/5);
                fixbottomtabwidth(txt_mediatype_c,parentviewwidth/5);

                txt_space_a.setVisibility(View.VISIBLE);
                txt_space_b.setVisibility(View.VISIBLE);
                txt_mediatype_a.setVisibility(View.VISIBLE);
                txt_mediatype_b.setVisibility(View.VISIBLE);
                txt_mediatype_c.setVisibility(View.VISIBLE);

                setUpRecyclerView();
                //showselectedfragment();
                if(currentselectedcomposer == 0)
                    fetchrecyclerposition(2);

                if(currentselectedcomposer == 1)
                    fetchrecyclerposition(3);

                if(currentselectedcomposer == 2)
                    fetchrecyclerposition(4);

                final AlphaAnimation alphanimation = new AlphaAnimation(0.0f, 1.0f);
                alphanimation.setDuration(2000); //You can manage the time of the blink with this parameter
                alphanimation.setRepeatMode(1);

                Animation.AnimationListener alphalistener=new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        //fadeoutcontrollers();
                        txt_mediatype_a.setText(config.item_video);
                        txt_mediatype_b.setText(config.item_photo);
                        txt_mediatype_c.setText(config.item_audio);
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                };
                alphanimation.setAnimationListener(alphalistener);
                layout_mediatype.startAnimation(alphanimation);

                getlatestthumbnailfromdirectory();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.txt_zoomlevel:
                if(currentselectedcomposer == 0 && fragvideocomposer != null)
                {
                    fragvideocomposer.adjustzoom();
                }
                else if(currentselectedcomposer == 1  && fragimgcapture != null)
                {
                    fragimgcapture.adjustzoom();
                }

                break;
            case R.id.txt_mediatype_a:
                currentselectedcomposer=0;
                showselectedfragment();
                break;
            case R.id.txt_mediatype_b:
                currentselectedcomposer=1;
                showselectedfragment();
                break;
            case R.id.txt_mediatype_c:
                currentselectedcomposer=2;
                showselectedfragment();
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
                        if(fragimgcapture != null && (! fragimgcapture.isimagecaptureprocessing))
                        {
                            if(countertimer != null)
                                countertimer.cancel();

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

                        if (iscircle) {
                            makesquare();
                        }
                        else {
                            makecircle();
                        }


                        if(fragaudiocomposer != null)
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

    public void startbrustcameratimer()
    {
        if(countertimer != null)
            countertimer.cancel();

        txt_timer.setVisibility(View.VISIBLE);
        enableDisableView(parentview,false);
        countertimer=new CountDownTimer(21000, 1000)
        {
            public void onTick(long millisUntilFinished) {
                Log.e("Timer running", " Tick");
                millisUntilFinished=millisUntilFinished-10000;
                int lefttime=(int)(millisUntilFinished/1000);
                if(lefttime == 0)
                {
                    enableDisableView(parentview,true);

                    if(countertimer != null)
                        countertimer.cancel();

                    txt_timer.setText("");
                    txt_timer.setVisibility(View.GONE);
                    cameracaptureeffect();
                }
                else
                {
                    txt_timer.setText(""+lefttime);
                }
            }

            public void onFinish() {
                enableDisableView(parentview,true);
                if(countertimer != null)
                    countertimer.cancel();
            }
        }.start();
    }

    private void cameracaptureeffect() {

        txt_timer.setVisibility(View.GONE);

        if(fragimgcapture != null)
            fragimgcapture.takePicture();
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
            Log.e("currentselectedcomposer",""+currentselectedcomposer);
            currentselectedcomposer--;
            //showselectedfragment();
            if(currentselectedcomposer == 0)
                fetchrecyclerposition(2);

            if(currentselectedcomposer == 1)
                fetchrecyclerposition(3);

            if(currentselectedcomposer == 2)
                fetchrecyclerposition(4);

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
            Log.e("currentselectedcomposer",""+currentselectedcomposer);
            if(currentselectedcomposer == 0)
                fetchrecyclerposition(2);

            if(currentselectedcomposer == 1)
                fetchrecyclerposition(3);

            if(currentselectedcomposer == 2)
                fetchrecyclerposition(4);
            initialdate =new Date();
        }
    }

    public void showselectedfragment()
    {
        xdata.getinstance().saveSetting(config.istravelleddistanceneeded,"false");
        txt_zoomlevel.setText("1.0 x");
        switch (currentselectedcomposer)
        {
            case 0:
                if(txt_space_a.getWidth() == 0)
                    resizebottomtab(txt_space_a,0,parentviewwidth/5);

                if(txt_space_b.getWidth() == 0)
                    resizebottomtab(txt_space_b,0,parentviewwidth/5);

                imgrotatecamera.setVisibility(View.VISIBLE);
                layout_seekbarzoom.setVisibility(View.VISIBLE);
                resetbuttonviews(txt_mediatype_a,txt_mediatype_b,txt_mediatype_c);
                showhideactionbottombaricon(1);
                if(fragvideocomposer == null)
                    fragvideocomposer=new videocomposerfragment();

                fragvideocomposer.setData(false, mitemclick,layoutbottom);
                gethelper().replacetabfragment(fragvideocomposer,false,true);
            break;

            case 1:
                if(txt_space_a.getWidth() > 0)
                    resizebottomtab(txt_space_a,parentviewwidth/5,0);

                if(txt_space_b.getWidth() == 0)
                    resizebottomtab(txt_space_b,0,parentviewwidth/5);

                imgrotatecamera.setVisibility(View.VISIBLE);
                layout_seekbarzoom.setVisibility(View.VISIBLE);
                resetbuttonviews(txt_mediatype_b,txt_mediatype_a,txt_mediatype_c);
                showhideactionbottombaricon(1);
                if(fragimgcapture == null)
                    fragimgcapture=new imagecomposerfragment();

                fragimgcapture.setData(mitemclick,layoutbottom);
                gethelper().replacetabfragment(fragimgcapture,false,true);
            break;

            case 2:
                if(txt_space_a.getWidth() > 0)
                    resizebottomtab(txt_space_a,parentviewwidth/5,0);

                if(txt_space_b.getWidth() > 0)
                    resizebottomtab(txt_space_b,parentviewwidth/5,0);

                imgrotatecamera.setVisibility(View.GONE);
                layout_seekbarzoom.setVisibility(View.GONE);
                resetbuttonviews(txt_mediatype_c,txt_mediatype_a,txt_mediatype_b);
                showhideactionbottombaricon(3);
                if(fragaudiocomposer == null)
                    fragaudiocomposer=new audiocomposerfragment();

                fragaudiocomposer.setData(mitemclick,layoutbottom);
                gethelper().replacetabfragment(fragaudiocomposer,false,true);
            break;
        }

        setimagethumbnail();
    }

    public void resizebottomtab(final View view, int start, final int end)
    {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams lp = view.getLayoutParams();
                lp.width = value;
                view.setLayoutParams(lp);
            }
        });
        animator.setDuration(config.transition_fragment_millis_300);
        animator.start();
    }

    public void fixbottomtabwidth(final View view, int width)
    {
        LinearLayout.LayoutParams layoutparams = new LinearLayout.LayoutParams(width,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutparams);
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
            getlatestthumbnailfromdirectory();
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
            if(fragvideocomposer != null && (! fragvideocomposer.isvideorecording)) {

           }

            if(txt_encrypting != null)
            {
                if( fragvideocomposer != null && (! fragvideocomposer.isvideorecording)){
                    layout_encryption.setRotation(rotateangle);
                    layout_seekbarzoom.setRotation(rotateangle);
                }

                if(fragaudiocomposer != null && currentselectedcomposer == 2)
                    layout_encryption.setRotation(rotateangle);
                    layout_seekbarzoom.setRotation(rotateangle);
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
                txt_encrypting.setTextSize(9);
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
                    if (motionEvent.getPointerCount() == 1)
                        mitemclick.onItemClicked(motionEvent,fragmentswipe);

                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case  R.id.layout_mediatype:
                try
                {
                    if (motionEvent.getPointerCount() == 1)
                        mitemclick.onItemClicked(motionEvent,fragmentswipe);

                    return true;
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
        animatorSet.setDuration(800);
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
        animatorSet.setDuration(800);
        animatorSet.playTogether(cornerAnimation, shiftAnimation);
        animatorSet.start();
        iscircle = !iscircle;
    }
}
