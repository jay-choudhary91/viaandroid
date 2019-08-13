package com.deeptruth.app.android.fragments;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.deeptruth.app.android.BuildConfig;
import com.deeptruth.app.android.R;
import com.deeptruth.app.android.activity.baseactivity;
import com.deeptruth.app.android.adapter.encryptiondataadapter;
import com.deeptruth.app.android.adapter.folderdirectoryspinneradapter;
import com.deeptruth.app.android.adapter.framebitmapadapter;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.database.databasemanager;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.interfaces.apiresponselistener;
import com.deeptruth.app.android.models.arraycontainer;
import com.deeptruth.app.android.models.customedittext;
import com.deeptruth.app.android.models.folder;
import com.deeptruth.app.android.models.frame;
import com.deeptruth.app.android.models.metadatahash;
import com.deeptruth.app.android.models.metricmodel;
import com.deeptruth.app.android.models.videomodel;
import com.deeptruth.app.android.sensor.Orientation;
import com.deeptruth.app.android.utils.LinearLayoutManagerWithSmoothScroller;
import com.deeptruth.app.android.utils.ScrubberLinearLayoutManagerWithSmoothScroller;
import com.deeptruth.app.android.utils.circularImageview;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.progressdialog;
import com.deeptruth.app.android.utils.simpledivideritemdecoration;
import com.deeptruth.app.android.utils.taskresult;
import com.deeptruth.app.android.utils.videocontrollerview;
import com.deeptruth.app.android.utils.xdata;
import com.deeptruth.app.android.videotrimmer.utils.backgroundexecutor;
import com.deeptruth.app.android.views.customseekbar;
import com.github.mikephil.charting.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.widget.RelativeLayout.TRUE;

/**
 * Created by devesh on 21/8/18.
 */

public class videoreaderfragment extends basefragment implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback,View.OnTouchListener,TextureView.SurfaceTextureListener,
     MediaPlayer.OnVideoSizeChangedListener,MediaPlayer.OnBufferingUpdateListener,Orientation.Listener, customedittext.OnKeyListener
{

    @BindView(R.id.img_dotmenu)
    ImageView img_dotmenu;
    @BindView(R.id.img_folder)
    ImageView img_folder;
    @BindView(R.id.img_camera)
    ImageView img_camera;
    @BindView(R.id.img_arrow_back)
    ImageView img_arrow_back;

    @BindView(R.id.recyview_frames)
    RecyclerView recyview_frames;
    @BindView(R.id.layout_scrubberview)
    RelativeLayout layout_scrubberview;
    @BindView(R.id.videoSurfaceContainer)
    FrameLayout videoSurfaceContainer;

    //tabdetails

    @BindView(R.id.spinner)
    Spinner spinnermediafolder;
    @BindView(R.id.txt_slot4)
    TextView txtslotmedia;
    @BindView(R.id.txt_slot5)
    TextView txtslotmeta;
    @BindView(R.id.txt_slot6)
    TextView txtslotencyption;
    @BindView(R.id.img_share_media)
    ImageView img_share_media;
    @BindView(R.id.img_edit_name)
    ImageView img_edit_name;
    @BindView(R.id.img_edit_notes)
    ImageView img_edit_notes;
    @BindView(R.id.layout_videoreader)
    RelativeLayout layout_videoreader;
    @BindView(R.id.txt_createdtime)
    TextView txt_createdtime;
    @BindView(R.id.layout_halfscrnimg)
    RelativeLayout layout_halfscrnimg;
    @BindView(R.id.layout_mediatype)
    LinearLayout layout_mediatype;
    @BindView(R.id.layout_videodetails)
    public RelativeLayout layout_videodetails;
    @BindView(R.id.scrollview_detail)
    ScrollView scrollview_detail;
    @BindView(R.id.img_fullscreen)
    ImageView img_fullscreen;
    @BindView(R.id.edt_medianame)
    customedittext edt_medianame;
    @BindView(R.id.edt_medianotes)
    customedittext edt_medianotes;
    @BindView(R.id.tab_layout)
    LinearLayout tab_layout;
    @BindView(R.id.scrollview_encyption)
    ScrollView scrollView_encyrption;
    @BindView(R.id.scrollview_meta)
    ScrollView scrollview_meta;
    @BindView(R.id.txt_videoupdatetransactionid)
    TextView txt_blockchainid;
    @BindView(R.id.txt_hash_formula)
    TextView txt_blockid;
    @BindView(R.id.txt_data_hash)
    TextView txt_blocknumber;
    @BindView(R.id.txt_dictionary_hash)
    TextView txt_metahash;
    @BindView(R.id.txt_size)
    TextView tvsize;
    @BindView(R.id.txt_date)
    TextView tvdate;
    @BindView(R.id.txt_time)
    TextView tvtime;
    @BindView(R.id.layout_date)
    LinearLayout layout_date;
    @BindView(R.id.layout_time)
    LinearLayout layout_time;
    @BindView(R.id.layout_duration)
    LinearLayout layout_duration;
    @BindView(R.id.layout_endtime)
    LinearLayout layout_endtime;
    @BindView(R.id.layout_starttime)
    LinearLayout layout_starttime;
    @BindView(R.id.txt_duration)
    TextView txt_duration;
    @BindView(R.id.txt_endtime)
    TextView txt_endtime;
    @BindView(R.id.txt_starttime)
    TextView txt_starttime;
    @BindView(R.id.mediacontroller_progress)
    customseekbar mediaseekbar;
    @BindView(R.id.time_current)
    TextView time_current;
    @BindView(R.id.video_endtime)
    TextView totalduration;
    @BindView(R.id.layout_customcontroller)
    LinearLayout layoutcustomcontroller;
    @BindView(R.id.layout_backgroundcontroller)
    RelativeLayout layoutbackgroundcontroller;
    @BindView(R.id.linear_seekbarcolorview)
    LinearLayout linearseekbarcolorview;
    @BindView(R.id.rl_videotextureview)
    RelativeLayout rl_videotextureview;

    @BindView(R.id.btn_playpause)
    circularImageview playpausebutton;
    @BindView(R.id.txt_title_actionbarcomposer)
    TextView txt_title_actionbarcomposer;
    @BindView(R.id.txt_mediatimethumb)
    TextView txt_mediatimethumb;
    @BindView(R.id.layout_progressline)
    RelativeLayout layout_progressline;

    @BindView(R.id.img_pause)
    ImageView imgpause;
    @BindView(R.id.scrub_layout)
    RelativeLayout scrublayout;
    @BindView(R.id.layout_validating)
    LinearLayout layout_validating;
    @BindView(R.id.txt_section_validating_secondary)
    TextView txt_section_validating_secondary;
    @BindView(R.id.layout_dtls)
    LinearLayout layout_dtls;
    @BindView(R.id.layout_item_encryption)
    LinearLayout layout_item_encryption;
    @BindView(R.id.recycler_encryption)
    RecyclerView recycler_encryption;

    @BindView(R.id.layout_seekbartiming)
    RelativeLayout layout_seekbartiming;
    @BindView(R.id.dividerline)
    View dividerline;
    @BindView(R.id.layoutpause)
    RelativeLayout layoutpause;
    @BindView(R.id.img_scanover)
    ImageView img_scanover;
    @BindView(R.id.videotextureview)
    TextureView videotextureview;
    @BindView(R.id.video_container)
    RelativeLayout showcontrollers;
    @BindView(R.id.scrubberverticalbar)
    RelativeLayout scurraberverticalbar;
    boolean istrue = false ;
    @BindView(R.id.video_downwordarrow)
    ImageView videodownwordarrow;
    @BindView(R.id.img_colapseicon)
    ImageView img_colapseicon;

    @BindView(R.id.rl_video_downwordarrow)
    RelativeLayout rl_video_downwordarrow;
    @BindView(R.id.metainfocontainer)
    FrameLayout metainfocontainer;
    @BindView(R.id.progressmediasync)
    ProgressBar progressmediasync;

    int footerheight=0;
    int lastrotatedangle =-1,videorotatedangle=-1;
    boolean flag = true;
    int scrubberviewpercentage = 10, actionbarpercentage = 8,controllerheightpercentage = 23,pauselayoutpercentage = 7;
    Surface surfacetexture = null;


    public boolean islastdragarrow = false;
    String medianame = "",medianotes = "",mediaduration="",mediafolder = "",localkey = "",thumbnailurl="",mediaid = "",mediatoken="",sync_date="";
    int targetheight=0,previousheight=0,targetwidth=0,previouswidth=0, previouswidthpercentage=0,scrubberviewwidth=0;
    private Handler hdlr = new Handler();
    StringBuilder mFormatBuilder;
    Formatter mFormatter;
    private framebitmapadapter adapter;
    private String mediafilepath = null;
    private MediaPlayer player;
    private videocontrollerview controller;
    private View rootview = null;
    private long playerposition=0;
    private Handler myHandler;
    private Runnable myRunnable;
    private long videoduration =0,maxincreasevideoduration=0;
    private ArrayList<videomodel> mainvideoframes =new ArrayList<>();
    private ArrayList<videomodel> mvideoframes =new ArrayList<>();
    private ArrayList<videomodel> mallframes =new ArrayList<>();
    private ArrayList<frame> mbitmaplist =new ArrayList<>();
    private ArrayList<arraycontainer> metricmainarraylist = new ArrayList<>();
    private ArrayList<arraycontainer> encryptionarraylist = new ArrayList<>();
    boolean runmethod = false;
    public boolean isvideocompleted=false,isfragmentstopped=false;
    public int flingactionmindstvac;
    private long currentmediaduration =0;
    private int videostarttime =0, endtime =0;
    private int devicemodeportrait=0,devicemodereverseportrait=2,devicemodelandscapeleft=1,
            devicemodelandscaperight=3;
    int mheightview = 0,updatemetaattempt=0;
    int navigationbarheight = 0;
    GestureDetector detector;
    arraycontainer arraycontainerformetric =null;
    adapteritemclick mcontrollernavigator;
    int currentprocessframe=0;
    int rootviewheight,videoviewheight,detailviewheight;
    encryptiondataadapter encryptionadapter;
    private TranslateAnimation validationbaranimation;
    boolean mIsScrolling = true;
    private Orientation mOrientation;
    boolean isplaypauswebtnshow = false;
    int layoutpauseheight = 0;
    metainformationfragment fragmentmetainformation;
    folderdirectoryspinneradapter folderspinneradapter;
    boolean isplaying = false;

    @Override
    public int getlayoutid() {
        return R.layout.full_screen_videoview;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        if(rootview == null) {
            rootview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, rootview);

            txt_section_validating_secondary.setVisibility(View.INVISIBLE);
            //setheadermargin();
            navigationbarheight =  common.getnavigationbarheight();

            setfooterlayout();
            gethelper().setdatacomposing(false);

            showcontrollers.post(new Runnable() {
                @Override
                public void run() {
                    rootviewheight = showcontrollers.getHeight();
                    videoviewheight = ((rootviewheight *60)/100);
                    layout_halfscrnimg.getLayoutParams().height = videoviewheight;
                    layout_halfscrnimg.setVisibility(View.VISIBLE);
                    layout_halfscrnimg.requestLayout();

                    detailviewheight = (rootviewheight -videoviewheight);
                    layout_videodetails.getLayoutParams().height = detailviewheight;
                    layout_videodetails.setVisibility(View.VISIBLE);
                    layout_videodetails.requestLayout();
                    loadviewdata();
                    // mOrientation = new Orientation(applicationviavideocomposer.getactivity());
                    gethelper().setwindowfitxy(true);

                    img_fullscreen.getPaddingBottom();
                    RelativeLayout.LayoutParams imageview  = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    int bottommargin= imageview.bottomMargin;
                }
            });

            metainfocontainer.setVisibility(View.VISIBLE);

            recyview_frames.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();
                    switch (action)
                    {
                        case MotionEvent.ACTION_DOWN:
                            // Disallow Drawer to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            break;

                        case MotionEvent.ACTION_UP:
                            // Allow Drawer to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }

                    // Handle seekbar touch events.
                    v.onTouchEvent(event);
                    return true;
                }
            });

            mediaseekbar.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();
                    switch (action)
                    {
                        case MotionEvent.ACTION_DOWN:
                            // Disallow Drawer to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            break;

                        case MotionEvent.ACTION_UP:
                            // Allow Drawer to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }

                    // Handle seekbar touch events.
                    v.onTouchEvent(event);
                    return true;
                }
            });


            detector = new GestureDetector(applicationviavideocomposer.getactivity(),new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                        float distanceX, float distanceY) {
                    if(e2.getY() > 0)
                    {
                        islastdragarrow =false;
                        int lovermovementlimit = rootviewheight - footerheight;
                        // int uppermovementlimit = (rootviewheight /100)*35;
                        if(layout_halfscrnimg.getLayoutParams().height < lovermovementlimit){
                            gethelper().drawerenabledisable(false);
                            mIsScrolling = true;
                            float height = layout_halfscrnimg.getLayoutParams().height + e2.getY();
                            //setanimation(layouttop,height,e2.getY(),false);
                            layout_halfscrnimg.getLayoutParams().height = (int)height;
                            layout_halfscrnimg.requestLayout();
                            layout_halfscrnimg.animate().setDuration(500);

                            float videotextureviewheight = videotextureview.getHeight() + e2.getY();
                            int finalvideotextureviewheight = (int)videotextureviewheight;

                            int scrubberheightgesture =  common.getviewheight(scrubberviewpercentage) -50;

                            setheadermargin(common.getviewheight(actionbarpercentage),scrubberheightgesture,finalvideotextureviewheight,true);

                            float bottomlayoutheight = layout_videodetails.getLayoutParams().height - e2.getY();
                            layout_videodetails.getLayoutParams().height = (int) bottomlayoutheight;
                            layout_videodetails.requestLayout();
                            layout_videodetails.animate().setDuration(500);

                        }else{

                            islastdragarrow =true;
                            gethelper().drawerenabledisable(true);
                            //videodownwordarrow.setImageResource(R.drawable.handle_up_arrow);

                            if(layout_videodetails.getLayoutParams().height<=0){
                                layout_videodetails.getLayoutParams().height = layout_videodetails.getHeight();
                                layout_videodetails.requestLayout();
                            }

                        }
                    }
                    else
                    {
                        int uppermovementlimit = (rootviewheight /100)*50;
                        islastdragarrow =false;
                        gethelper().drawerenabledisable(false);

                        if(layout_halfscrnimg.getLayoutParams().height >= uppermovementlimit) {
                            mIsScrolling = true;
                            float height = layout_halfscrnimg.getLayoutParams().height - (Math.abs(e2.getY()));
                            layout_halfscrnimg.getLayoutParams().height = (int) height;
                            layout_halfscrnimg.requestLayout();
                            layout_halfscrnimg.animate().setDuration(500);

                            float videotextureviewheight = videotextureview.getHeight() + e2.getY();
                            int finalvideotextureviewheight = (int)videotextureviewheight;
                            int scrubberheightgesture =  common.getviewheight(scrubberviewpercentage) -50;

                            setheadermargin(common.getviewheight(actionbarpercentage),scrubberheightgesture,finalvideotextureviewheight,true);

                            if(layout_videodetails.getLayoutParams().height > 0){

                                float bottomlayoutheight = layout_videodetails.getLayoutParams().height + (Math.abs(e2.getY()));
                                layout_videodetails.getLayoutParams().height = (int) bottomlayoutheight;
                                layout_videodetails.requestLayout();
                                layout_videodetails.animate().setDuration(500);

                            }
                        }else{
                            islastdragarrow =false;
                            //videodownwordarrow.setImageResource(R.drawable.handle_down_arrow);
                        }
                    }
                    return false;
                }



                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {

                    if(rootviewheight > layout_halfscrnimg.getLayoutParams().height && !islastdragarrow){
                        islastdragarrow =true;
                        gethelper().drawerenabledisable(true);
                        layout_halfscrnimg.getLayoutParams().height = rootviewheight;

                        RelativeLayout.LayoutParams paramsvideotextureview  = new RelativeLayout.LayoutParams(targetwidth,rootviewheight+navigationbarheight);
                        paramsvideotextureview.setMargins(0,common.getviewheight(actionbarpercentage)-20,0,common.getviewheight(scrubberviewpercentage)-50);
                        videotextureview.setLayoutParams(paramsvideotextureview);

                        videotextureview.post(new Runnable() {
                            @Override
                            public void run() {
                                updatesurfaceviewsize();
                            }
                        });

                        layout_halfscrnimg.requestLayout();
                        layout_halfscrnimg.animate().setDuration(500);

                       // videodownwordarrow.setImageResource(R.drawable.handle_up_arrow);

                    }else{
                        islastdragarrow =false;
                        gethelper().drawerenabledisable(false);
                        layout_halfscrnimg.getLayoutParams().height = videoviewheight;
                        layout_videodetails.getLayoutParams().height = detailviewheight;
                        RelativeLayout.LayoutParams paramsvideotextureview  = new RelativeLayout.LayoutParams(targetwidth,videoviewheight);
                        paramsvideotextureview.setMargins(0,common.getviewheight(actionbarpercentage)-1,0,common.getviewheight(scrubberviewpercentage)-50);
                        videotextureview.setLayoutParams(paramsvideotextureview);

                        videotextureview.post(new Runnable() {
                            @Override
                            public void run() {
                                updatesurfaceviewsize();
                            }
                        });
                       // videodownwordarrow.setImageResource(R.drawable.handle_down_arrow);
                    }
                    return super.onSingleTapConfirmed(e);
                }
                @Override
                public boolean onDown(MotionEvent e) {
                    return false;
                }
            });

            videodownwordarrow.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    switch (view.getId())
                    {
                        case  R.id.video_downwordarrow:

                            if (detector.onTouchEvent(event)) {
                                return true;
                            }

                            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                                if(mIsScrolling ) {

                                    if(!islastdragarrow)
                                       updatesurfaceviewsize();
                                    return true;
                                };
                            }
                            break;
                    }

                    return false;
                }
            });
        }

        return rootview;
    }

    public void loadviewdata()
    {
        gethelper().setrecordingrunning(false);
        gethelper().drawerenabledisable(false);

        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        videotextureview.setSurfaceTextureListener(this);

        layout_item_encryption.setVisibility(View.GONE);
        recycler_encryption.setVisibility(View.VISIBLE);
        img_share_media.setVisibility(View.VISIBLE);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(applicationviavideocomposer.getactivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recycler_encryption.setLayoutManager(new LinearLayoutManagerWithSmoothScroller(getActivity()));
        recycler_encryption.addItemDecoration(new simpledivideritemdecoration(applicationviavideocomposer.getactivity()));

        encryptionadapter = new encryptiondataadapter(encryptionarraylist, applicationviavideocomposer.getactivity());
        recycler_encryption.setAdapter(encryptionadapter);


        mediaseekbar.setPadding(0,0,0,0);

        mheightview = getContext().getResources().getDimensionPixelOffset(R.dimen.frames_video_height);

        videoSurfaceContainer.post(new Runnable() {
            @Override
            public void run() {
                previousheight = videoSurfaceContainer.getHeight();
                previouswidth = videoSurfaceContainer.getWidth();
                previouswidthpercentage = (previouswidth*20)/100;
               // playpausebutton.setVisibility(View.VISIBLE);
               // recenterplaypause();
            }
        });

        playpausebutton.setImageResource(R.drawable.play_btn);

        edt_medianame.setEnabled(false);
        edt_medianame.setClickable(false);
        edt_medianame.setFocusable(false);
        edt_medianame.setFocusableInTouchMode(false);

       // edt_medianotes.setEnabled(false);
        edt_medianotes.setClickable(false);
        edt_medianotes.setFocusable(false);
        edt_medianotes.setFocusableInTouchMode(false);

        mediaseekbar.setThumbOffset(-1);

        final RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mediaseekbar.getLayoutParams();
        mediaseekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(final SeekBar seekBar, int progress, boolean fromUser) {
                   int processframe = 0,scrubberprogress = 0;
                   int progresspercentage = (progress * 100) / mediaseekbar.getMax();

                   if (progresspercentage > 0)
                       processframe = (int) (metricmainarraylist.size() * progresspercentage) / 100;

                   if (progresspercentage > 0)
                       scrubberprogress = (int) (mbitmaplist.size() * progresspercentage) / 100;

                   if (isvideocompleted){
                       processframe = metricmainarraylist.size() - 1;
                       scrubberprogress = mbitmaplist.size() - 1;
                   }

                   if (currentprocessframe != processframe) {
                       currentprocessframe = processframe;

                       if (processframe < metricmainarraylist.size() && metricmainarraylist.size() > 0) {
                           arraycontainerformetric = new arraycontainer();
                           arraycontainerformetric = metricmainarraylist.get(processframe);
                       }

                       if(encryptionarraylist.size() == 0 && arraycontainerformetric != null)
                           encryptionarraylist.add(arraycontainerformetric);

                       if(encryptionarraylist.size() > 0)
                       {
                           encryptionarraylist.set(0,arraycontainerformetric);
                           encryptionadapter.notifyDataSetChanged();
                       }

                       if (recyview_frames != null && recyview_frames != null)
                               recyview_frames.smoothScrollToPosition(scrubberprogress);
                   }

                   if(isvideocompleted)
                       currentprocessframe = metricmainarraylist.size();

                   gethelper().setcurrentmediaposition(currentprocessframe);
                   if(fragmentmetainformation != null)
                        fragmentmetainformation.setcurrentmediaposition(currentprocessframe);

                   layout_progressline.setVisibility(View.VISIBLE);
                   RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
                           RelativeLayout.LayoutParams.WRAP_CONTENT,
                           RelativeLayout.LayoutParams.WRAP_CONTENT);
                   p.addRule(RelativeLayout.ABOVE, seekBar.getId());
                   Rect thumbRect = mediaseekbar.getSeekBarThumb().getBounds();
                   p.setMargins((int) (thumbRect.centerX() - common.dpToPx(getActivity(), 19)), 0, -50, 0);
                   layout_progressline.setLayoutParams(p);
                   txt_mediatimethumb.setText(common.gettimestring(seekBar.getProgress()));
                   txt_mediatimethumb.setVisibility(View.VISIBLE);

        }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                player.seekTo(seekBar.getProgress());
                maxincreasevideoduration=player.getCurrentPosition();
            }
        });

        recyview_frames.post(new Runnable() {
            @Override
            public void run()
            {
                adapter = new framebitmapadapter(getActivity(), mbitmaplist,recyview_frames.getWidth(),
                new adapteritemclick() {
                    @Override
                    public void onItemClicked(Object object) {

                    }

                    @Override
                    public void onItemClicked(Object object, int type) {

                    }
                 });

                final LinearLayoutManager mlinearlayoutmanager = new LinearLayoutManager(applicationviavideocomposer.getactivity());
                mlinearlayoutmanager.setOrientation(LinearLayoutManager.HORIZONTAL);
                recyview_frames.setLayoutManager(new ScrubberLinearLayoutManagerWithSmoothScroller(getActivity(),LinearLayoutManager.HORIZONTAL,false
                ));
                // LinearLayoutManager mlinearlayoutmanager = new centerlayoutmanager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                //recyview_frames.setLayoutManager(mlinearlayoutmanager);
                recyview_frames.setItemAnimator(new DefaultItemAnimator());
                recyview_frames.setAdapter(adapter);

            }
        });


        flingactionmindstvac = common.getdrawerswipearea();
        img_dotmenu.setOnClickListener(new setonClick());
        img_folder.setOnClickListener(new setonClick());
        img_camera.setOnClickListener(new setonClick());
        img_arrow_back.setOnClickListener(new setonClick());
        videotextureview.setOnClickListener(new setonClick());
        imgpause.setOnClickListener(new setonClick());
        img_share_media.setOnClickListener(new setonClick());
        img_edit_name.setOnClickListener(new setonClick());
        img_edit_notes.setOnClickListener(new setonClick());
        img_fullscreen.setOnClickListener(new setonClick());
        playpausebutton.setOnClickListener(new setonClick());
        layout_dtls.setOnClickListener(new setonClick());
        img_colapseicon.setOnClickListener(new setonClick());

        imgpause.setVisibility(View.GONE);
        img_dotmenu.setVisibility(View.VISIBLE);
        img_folder.setVisibility(View.VISIBLE);
        img_arrow_back.setVisibility(View.VISIBLE);
        txtslotmedia.setText(getResources().getString(R.string.video));
        scrollview_detail.setVisibility(View.VISIBLE);
        tab_layout.setVisibility(View.VISIBLE);
        img_fullscreen.setVisibility(View.VISIBLE);
        layout_videodetails.setVisibility(View.VISIBLE);
        layout_mediatype.setVisibility(View.VISIBLE);
        layout_date.setVisibility(View.GONE);
        layout_time.setVisibility(View.GONE);
        layout_duration.setVisibility(View.VISIBLE);
        layout_starttime.setVisibility(View.VISIBLE);
        layout_endtime.setVisibility(View.VISIBLE);
        if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_reader))
        {
            img_camera.setVisibility(View.GONE);
        }
        else
        {
            img_camera.setVisibility(View.VISIBLE);
        }
        mediaseekbar.post(new Runnable() {
            @Override
            public void run() {
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) layout_scrubberview.getLayoutParams();
                RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,mediaseekbar.getHeight());
                parms.setMargins(lp.leftMargin,0,lp.rightMargin,0);
                parms.addRule(RelativeLayout.BELOW,R.id.layout_scrubberview);
                linearseekbarcolorview.setLayoutParams(parms);
                progressmediasync.setLayoutParams(parms);
            }
        });

        layout_videoreader.post(new Runnable() {
            @Override
            public void run() {
                targetheight= layout_videoreader.getHeight();
                targetwidth = layout_videoreader.getWidth();
                int totalwidth= targetwidth + 100;

                final AlphaAnimation alphanimation = new AlphaAnimation(0.0f, 1.0f);
                alphanimation.setDuration(1000); //You can manage the time of the blink with this parameter
                alphanimation.setStartOffset(1000);
                alphanimation.setRepeatMode(1);

                final AlphaAnimation fadeout_animation = new AlphaAnimation(1.0f, 0.0f);
                fadeout_animation.setDuration(1000); //You can manage the time of the blink with this parameter
                fadeout_animation.setStartOffset(1000);
                fadeout_animation.setRepeatMode(1);

                Animation.AnimationListener alphalistener=new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        txt_section_validating_secondary.startAnimation(fadeout_animation);
                        //fadeoutcontrollers();
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                };
                alphanimation.setAnimationListener(alphalistener);
                Animation.AnimationListener fadeoutlistener=new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        txt_section_validating_secondary.startAnimation(alphanimation);
                        //fadeoutcontrollers();
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                };
                fadeout_animation.setAnimationListener(fadeoutlistener);

                validationbaranimation = new TranslateAnimation(-totalwidth, totalwidth ,0.0f, 0.0f);
                validationbaranimation.setDuration(4000);
                validationbaranimation.setRepeatCount(Animation.INFINITE);
                validationbaranimation.setRepeatMode(ValueAnimator.RESTART);
                img_scanover.startAnimation(validationbaranimation);

                Animation.AnimationListener translatelistener=new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        txt_section_validating_secondary.startAnimation(alphanimation);
                    }
                    @Override
                    public void onAnimationEnd(Animation animation) {
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {
                      //  txt_section_validating_secondary.startAnimation(alphanimation);
                    }
                };
                validationbaranimation.setAnimationListener(translatelistener);
            }
        });


        txtslotencyption.setOnClickListener(new setonClick());
        txtslotmeta.setOnClickListener(new setonClick());
        txtslotmedia.setOnClickListener(new setonClick());
        resetButtonViews(txtslotmedia, txtslotmeta, txtslotencyption);
        mediafilepath = xdata.getinstance().getSetting(config.selectedvideourl);

        edt_medianame.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    edt_medianame.setKeyListener(null);
                    v.setFocusable(false);
                    editabletext();
                    gethelper().setwindowfitxy(true);
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edt_medianame.getWindowToken(), 0);

                    callupdatemetaapi(mediaid,edt_medianame.getText().toString().trim(),edt_medianotes.getText().toString().trim());
                }
            }
        });

        edt_medianotes.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    v.setFocusable(false);
                    gethelper().setwindowfitxy(true);
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edt_medianame.getWindowToken(), 0);

                    callupdatemetaapi(mediaid,edt_medianame.getText().toString().trim(),edt_medianotes.getText().toString().trim());
                }
            }
        });

        edt_medianame.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId & EditorInfo.IME_MASK_ACTION) != 0) {
                    edt_medianame.setEnabled(false);
                    edt_medianame.setClickable(false);
                    edt_medianame.setFocusable(false);
                    edt_medianame.setFocusableInTouchMode(false);
                    editabletext();
                    showvideoplayer();
                    return true;
                }
                else {
                    return false;
                }
            }
        });

        edt_medianotes.setOnKeyboardHidden(new customedittext.OnKeyboardHidden() {
            @Override
            public void onKeyboardHidden() {
                edt_medianame.setEnabled(false);
                hidefocus(edt_medianotes);
            }
        });

        edt_medianame.setOnKeyboardHidden(new customedittext.OnKeyboardHidden() {
            @Override
            public void onKeyboardHidden() {

                hidefocus(edt_medianame);
            }
        });
        edt_medianotes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.edt_medianotes) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void initviews(View parent, Bundle savedInstanceState) {
        super.initviews(parent, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        ButterKnife.bind(this, parent);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    public void setdata(adapteritemclick mcontrollernavigator) {
        this.mcontrollernavigator = mcontrollernavigator;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, final int width, int height) {
        surfacetexture = new Surface(surface);
        setupvideodata();

        layout_scrubberview.post(new Runnable() {
            @Override
            public void run() {
                if(scrubberviewwidth == 0)
                {
                    scrubberviewwidth = layout_scrubberview.getWidth();
                    scrollview_meta.setVisibility(View.INVISIBLE);

                    scrollView_encyrption.setVisibility(View.INVISIBLE);
                    Thread thread = new Thread() {
                        public void run() {
                            getbitmap(scrubberviewwidth);
                        }
                    };
                    thread.start();
                }
            }
        });
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (player != null) {
            playerposition=player.getCurrentPosition();
            player.pause();
        }
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
         if(flag){
             setheadermargin(common.getviewheight(actionbarpercentage),common.getviewheight(scrubberviewpercentage),0,false);
             flag = false;
         }
    }

    public void recenterplaypause(final int topheight, final int visibility)
    {
        videotextureview.post(new Runnable() {
            @Override
            public void run() {
                RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);

                   params.addRule(RelativeLayout.CENTER_HORIZONTAL,TRUE);
                   params.setMargins(0,topheight,0,0);
                   playpausebutton.setLayoutParams(params);

                   if(visibility == 0)
                        playpausebutton.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public void onOrientationChanged(float pitch, float roll) {

    }

    @Override
    public void onOrientationChanged(float[] adjustedRotationMatrix, float[] orientation) {
        float pitch = orientation[1] * -57;

        if(videorotatedangle == -1 || videorotatedangle == 90)
            return;

        if(pitch <= -50)
            return;

        float roll = orientation[2] * -57;
        float rotateangle=3600;
        if(roll < -45 && roll >= -120)
        {
            rotateangle=-90;  // Landscape right side
        }
        else if(roll >= -40 && roll <= 45)
        {
            rotateangle=0;     // Portrait 90
        }
        else if(roll > 45 && roll <= 120)
        {
            rotateangle=90;    // Landscape left side
        }
        else if(roll < 45 && roll < -120)
        {
            rotateangle=180;
        }

        if(rotateangle != 3600 && layout_videodetails != null)
        {
            if(layout_videodetails.getVisibility() == View.GONE && lastrotatedangle != rotateangle)
            {
                lastrotatedangle=(int)rotateangle;

                // if device in portrait mode then angle will be 90 0 -90
                // if device in landscape left side angle will be 0 -90 180
                // if device in landscape right side angle will be 180 90 0

                Display display = ((WindowManager) applicationviavideocomposer.getactivity().
                        getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                int devicerotation = display.getRotation();
                //Toast.makeText(applicationviavideocomposer.getactivity(),""+lastrotatedangle+" "+devicerotation,Toast.LENGTH_SHORT).show();

                if(devicerotation == 0)  // Portrait
                {
                    // if device in portrait mode then angle will be 90 0 -90
                    if(rotateangle == 90)
                    {
                        changedevicemode(devicemodelandscapeleft);
                    }
                    else if(rotateangle == 0)
                    {
                        changedevicemode(devicemodeportrait);
                    }
                    else if(rotateangle == -90)
                    {
                        changedevicemode(devicemodelandscaperight);
                    }
                }
                else if(devicerotation == 1)  // Landscape left
                {
                    // if device in landscape left side angle will be 0 -90 180
                    if(rotateangle == 0)
                    {
                        changedevicemode(devicemodelandscapeleft);
                    }
                    else if(rotateangle == -90)
                    {
                        changedevicemode(devicemodeportrait);
                    }
                    else if(rotateangle == 180)
                    {
                        changedevicemode(devicemodelandscaperight);
                    }
                }
                else if(devicerotation == 2)   // Reverse portrait
                {

                }
                else if(devicerotation == 3) // Landscape right
                {
                    // if device in landscape right side angle will be 180 90 0
                    if(rotateangle == 180)
                    {
                        changedevicemode(devicemodelandscapeleft);
                    }
                    else if(rotateangle == 90)
                    {
                        changedevicemode(devicemodeportrait);
                    }
                    else if(rotateangle == 0)
                    {
                        changedevicemode(devicemodelandscaperight);
                    }
                }
            }
        }
    }

    public void changedevicemode(int mode)
    {
        if(mode == devicemodeportrait)
        {
            applicationviavideocomposer.getactivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        else if(mode == devicemodelandscapeleft)
        {
            applicationviavideocomposer.getactivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        else if(mode == devicemodereverseportrait)
        {
            applicationviavideocomposer.getactivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
        }
        else if(mode == devicemodelandscaperight)
        {
            applicationviavideocomposer.getactivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        }
    }

    public class setonClick implements View.OnClickListener
    {

        @Override
        public void onClick(View view) {
            switch (view.getId())
            {
                case R.id.txt_slot4:
                    resetButtonViews(txtslotmedia, txtslotmeta, txtslotencyption);
                    scrollview_detail.setVisibility(View.VISIBLE);
                    scrollView_encyrption.setVisibility(View.GONE);
                    scrollview_meta.setVisibility(View.GONE);
                    break;

                case R.id.txt_slot5:
                    showvideoplayer();
                    resetButtonViews(txtslotmeta, txtslotmedia, txtslotencyption);
                    scrollview_meta.setVisibility(View.VISIBLE);
                    scrollView_encyrption.setVisibility(View.INVISIBLE);
                    scrollview_detail.setVisibility(View.INVISIBLE);
                    if(fragmentmetainformation == null)
                    {
                        fragmentmetainformation=new metainformationfragment();
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.metainfocontainer,fragmentmetainformation);
                        transaction.commit();
                    }

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(fragmentmetainformation != null)
                            {
                                fragmentmetainformation.setdatacomposing(false,xdata.getinstance().getSetting(config.selectedvideourl));
                                fragmentmetainformation.setcurrentmediaposition(currentprocessframe);
                            }
                        }
                    },500);
                    break;
                case R.id.txt_slot6:
                    showvideoplayer();
                    resetButtonViews(txtslotencyption, txtslotmedia, txtslotmeta);
                    scrollView_encyrption.setVisibility(View.VISIBLE);
                    scrollview_detail.setVisibility(View.INVISIBLE);
                    scrollview_meta.setVisibility(View.INVISIBLE);
                    break;
                case R.id.img_edit_name:
                    visiblefocus(edt_medianame);
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 1);
                    break;
                case R.id.img_edit_notes:
                    visiblefocus(edt_medianotes);
                    InputMethodManager imn = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imn.toggleSoftInput(InputMethodManager.SHOW_FORCED, 1);
                    break;
                case R.id.img_share_media:
                    img_share_media.setEnabled(false);
                    new Handler().postDelayed(new Runnable()
                    {
                        public void run()
                        {
                            img_share_media.setEnabled(true);
                        }
                    }, 1500);
                    if (mediafilepath != null && (!mediafilepath.isEmpty())){
                        //gethelper().showsharepopupsub(mediafilepath,"video",mediatoken);
                        baseactivity.getinstance().preparesharedialogfragment(mediafilepath,mediatoken,config.type_video,thumbnailurl);
                    }
                   // common.sharevideo(getActivity(), mediafilepath);
                    break;

                case R.id.img_dotmenu:
                    settingfragment settingfrag=new settingfragment();
                    gethelper().addFragment(settingfrag, false, true);
                    break;
                case R.id.img_folder:
                    myfolderfragment folderfragment=new myfolderfragment();
                    gethelper().addFragment(folderfragment, false, true);
                    break;
                case R.id.img_camera:
                    launchbottombarfragment();
                    break;

                case R.id.img_arrow_back:
                    try {
                        img_arrow_back.setClickable(false);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    gethelper().onBack();
                    break;
                case R.id.img_fullscreen:
                    if(layout_videodetails.getVisibility()==View.VISIBLE)
                    {
                        resizeviewtofullscreen();
                    }
                    else
                    {
                        resizeviewtohalfscreen();
                    }
                    break;

                case R.id.img_colapseicon:
                    resizeviewtohalfscreen();
                    break;

                case R.id.videotextureview:

                    if(layout_videodetails.getVisibility()==View.GONE)  // Full screen view
                    {
                        if(layout_mediatype.getVisibility()==View.GONE)  // Action bar is hidden
                        {
                            if(player != null && (! player.isPlaying()))  // Player is pause
                            {
                                gethelper().updateactionbar(1);
                                img_fullscreen.setVisibility(View.VISIBLE);
                                layoutbackgroundcontroller.setVisibility(View.VISIBLE);
                                gethelper().drawerenabledisable(true);
                                layout_mediatype.setVisibility(View.VISIBLE);
                                img_fullscreen.setImageResource(R.drawable.ic_info_mode);
                                layoutpause.setVisibility(View.VISIBLE);

                                if(!isplaypauswebtnshow){
                                      playpausebutton.setVisibility(View.VISIBLE);

                                }else{
                                    getpausebtnheight();
                                }
                            }
                            else   // Player is playing
                            {
                                gethelper().updateactionbar(0);
                                gethelper().drawerenabledisable(true);
                                img_fullscreen.setVisibility(View.GONE);
                                layout_mediatype.setVisibility(View.GONE);
                                playpausebutton.setVisibility(View.GONE);
                                if(layoutbackgroundcontroller.getVisibility() == View.VISIBLE && layoutpause.getVisibility() == View.VISIBLE){
                                    layoutbackgroundcontroller.setVisibility(View.GONE);
                                    imgpause.setVisibility(View.GONE);
                                    layoutpause.setVisibility(View.GONE);
                                    gethelper().drawerenabledisable(false);

                                }else{

                                    layoutbackgroundcontroller.setVisibility(View.VISIBLE);
                                    imgpause.setVisibility(View.VISIBLE);
                                    layoutpause.setVisibility(View.VISIBLE);
                                    gethelper().drawerenabledisable(true);
                                    getcontrollerheight();
                                }
                            }
                        }
                        else  // Action bar is showing
                        {
                            playpausebutton.setVisibility(View.GONE);
                            img_fullscreen.setVisibility(View.GONE);
                            img_fullscreen.setImageResource(R.drawable.ic_info_mode);

                            if(player != null && (! player.isPlaying()))
                            {
                                gethelper().updateactionbar(0);
                                layout_mediatype.setVisibility(View.GONE);
                                gethelper().drawerenabledisable(false);
                                layoutbackgroundcontroller.setVisibility(View.GONE);
                                layoutpause.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {

                        if(playpausebutton.getVisibility() == View.GONE ){
                            playpausebutton.setVisibility(View.VISIBLE);
                        }else{
                            playpausebutton.setVisibility(View.GONE);
                        }

                        if(player != null && !player.isPlaying()){
                            playpausebutton.setVisibility(View.VISIBLE);
                        }

                        if(islastdragarrow){
                            return;
                        }else{
                            gethelper().drawerenabledisable(false);
                            img_fullscreen.setVisibility(View.VISIBLE);
                            img_fullscreen.setImageResource(R.drawable.ic_full_screen_mode);
                            //playpausebutton.setVisibility(View.VISIBLE);
                            imgpause.setVisibility(View.GONE);
                        }
                    }

                    break;

                case R.id.btn_playpause:
                    if(player != null && player.isPlaying()){
                        pause();
                    }else{
                        if(layout_videodetails.getVisibility()==View.GONE){
                            //layout_halfscrnimg.getLayoutParams().height = rootviewheight + Integer.parseInt(xdata.getinstance().getSetting("statusbarheight"));
                            isplaypauswebtnshow = true;
                            gethelper().updateactionbar(0);
                            layout_mediatype.setVisibility(View.GONE);
                            layoutpause.setVisibility(View.VISIBLE);
                            layoutcustomcontroller.requestLayout();
                            playpausebutton.setVisibility(View.GONE);
                            img_fullscreen.setVisibility(View.GONE);
                            img_colapseicon.setVisibility(View.VISIBLE);
                            layoutbackgroundcontroller.setVisibility(View.VISIBLE);
                            imgpause.setVisibility(View.VISIBLE);
                            imgpause.setImageResource(R.drawable.ic_pause);
                            gethelper().drawerenabledisable(true);
                            getcontrollerheight();
                            backgroundcolor();
                        }

                        playpausebutton.setVisibility(View.GONE);
                        start();
                    }
                    break;

                case R.id.img_pause:

                    if(layout_videodetails.getVisibility()==View.GONE){
                        if(player != null && player.isPlaying()){
                            pause();
                            imgpause.setImageResource(R.drawable.ic_play);
                         /*   playpausebutton.setImageResource(R.drawable.play_btn);*/
                            playpausebutton.setVisibility(View.GONE);
                            img_fullscreen.setVisibility(View.VISIBLE);
                            img_colapseicon.setVisibility(View.GONE);
                            img_fullscreen.setImageResource(R.drawable.ic_info_mode);
                            layoutbackgroundcontroller.setVisibility(View.VISIBLE);
                            totalduration.setVisibility(View.VISIBLE);
                            time_current.setVisibility(View.VISIBLE);
                            gethelper().updateactionbar(1);
                            imgpause.setVisibility(View.VISIBLE);
                            getpausebtnheight();
                            layout_mediatype.setVisibility(View.VISIBLE);
                            getcontrollerheight();
                        }else{
                            start();
                            imgpause.setImageResource(R.drawable.ic_pause);
                           /* playpausebutton.setImageResource(R.drawable.play_btn);*/
                            playpausebutton.setVisibility(View.GONE);
                            img_fullscreen.setVisibility(View.GONE);
                            img_colapseicon.setVisibility(View.VISIBLE);
                            img_fullscreen.setImageResource(R.drawable.ic_info_mode);
                            layout_mediatype.setVisibility(View.GONE);
                            layoutbackgroundcontroller.setVisibility(View.VISIBLE);
                            totalduration.setVisibility(View.VISIBLE);
                            time_current.setVisibility(View.VISIBLE);
                            gethelper().updateactionbar(0);
                            imgpause.setVisibility(View.VISIBLE);
                            getpausebtnheight();
                            getcontrollerheight();
                        }
                    }
                    break;

                case R.id.layout_dtls:
                    if(layout_halfscrnimg.getVisibility() == View.GONE){
                        layout_halfscrnimg.setVisibility(View.VISIBLE);
                        removeheadermargin();
                        view.clearFocus();
                        InputMethodManager immm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        immm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    break;
            }
        }
    }

    public void resizeviewtofullscreen()
    {
        xdata.getinstance().saveSetting("fullscreen", "" + "fullscreen");
        layout_halfscrnimg.getLayoutParams().height = (rootviewheight-navigationbarheight);

        layout_videodetails.getLayoutParams().height = 0;
        gethelper().drawerenabledisable(true);
        gethelper().updateactionbar(1);
        setheadermargin(0,0,(rootviewheight-navigationbarheight),true);
        layout_videodetails.setVisibility(View.GONE);
        scrollview_detail.setVisibility(View.GONE);
        scrollview_meta.setVisibility(View.GONE);
        scrollView_encyrption.setVisibility(View.GONE);
        tab_layout.setVisibility(View.GONE);
        layout_mediatype.setVisibility(View.VISIBLE);
        layoutbackgroundcontroller.setVisibility(View.VISIBLE);
        setbottomimgview();
        videodownwordarrow.setVisibility(View.GONE);
        rl_video_downwordarrow.setVisibility(View.GONE);
        backgroundcolor();
        islastdragarrow =false;
        if(player.isPlaying()){
            img_fullscreen.setVisibility(View.GONE);
            img_fullscreen.setImageResource(R.drawable.ic_info_mode);
            layoutpause.setVisibility(View.VISIBLE);
            imgpause.setVisibility(View.VISIBLE);
            imgpause.setImageResource(R.drawable.ic_pause);
            img_colapseicon.setVisibility(View.VISIBLE);
            playpausebutton.setVisibility(View.GONE);
            gethelper().updateactionbar(0);
            layout_mediatype.setVisibility(View.GONE);
            getcontrollerheight();
        }else{
            isplaypauswebtnshow = false;
            img_fullscreen.setVisibility(View.VISIBLE);
            img_fullscreen.setImageResource(R.drawable.ic_info_mode);
            layoutpause.setVisibility(View.VISIBLE);
            imgpause.setVisibility(View.GONE);
            img_colapseicon.setVisibility(View.GONE);
            playpausebutton.setVisibility(View.VISIBLE);
        }
    }


    public void resizeviewtohalfscreen()
    {
        removeheaderpadding();
        xdata.getinstance().saveSetting("fullscreen", "" + "halfscreen");
        layout_halfscrnimg.getLayoutParams().height = videoviewheight;
        layout_videodetails.getLayoutParams().height = detailviewheight;
        gethelper().drawerenabledisable(false);
        gethelper().updateactionbar(1);
        playpausebutton.setVisibility(View.GONE);
        layout_videodetails.setVisibility(View.VISIBLE);
        tab_layout.setVisibility(View.VISIBLE);
        scrollview_detail.setVisibility(View.VISIBLE);
        layout_mediatype.setVisibility(View.VISIBLE);
        totalduration.setVisibility(View.VISIBLE);
        time_current.setVisibility(View.VISIBLE);
        layoutcustomcontroller.setBackgroundColor(getResources().getColor(R.color.white));
        //updatetextureviewsize((previouswidth- previouswidthpercentage),previousheight);
        setheadermargin(common.getviewheight(actionbarpercentage),common.getviewheight(scrubberviewpercentage),0,false);
        layoutbackgroundcontroller.setVisibility(View.VISIBLE);
        layout_seekbartiming.getResources().getColor(R.color.white);
        dividerline.setVisibility(View.GONE);

        imgpause.setVisibility(View.GONE);
        img_colapseicon.setVisibility(View.GONE);
        collapseimg_view();
        img_fullscreen.setImageResource(R.drawable.ic_full_screen_mode);
        img_fullscreen.setVisibility(View.VISIBLE);
        resetButtonViews(txtslotmedia, txtslotmeta, txtslotencyption);
        videodownwordarrow.setVisibility(View.VISIBLE);
        rl_video_downwordarrow.setVisibility(View.VISIBLE);
    }

    public void launchbottombarfragment()
    {
        composeoptionspagerfragment fragbottombar=new composeoptionspagerfragment();
        gethelper().replaceFragment(fragbottombar, false, true);
    }

    @Override
    public void oncurrentlocationchanged(Location location) {
        super.oncurrentlocationchanged(location);
    }

    public void resetButtonViews(TextView view1, TextView view2, TextView view3)
    {
        if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            Drawable drawable1 = (Drawable) view1.getBackground();
            drawable1.setColorFilter(getResources().getColor(R.color.blue), PorterDuff.Mode.MULTIPLY);
            view1.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));

            Drawable drawable2 = (Drawable) view2.getBackground();
            drawable2.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
            view2.setTextColor(ContextCompat.getColor(getActivity(),R.color.blue));

            Drawable drawable3 = (Drawable) view3.getBackground();
            drawable3.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
            view3.setTextColor(ContextCompat.getColor(getActivity(),R.color.blue));
        }
    }

    @Override
    public void setmetriceslistitems(ArrayList<metricmodel> mitems) {
        super.setmetriceslistitems(mitems);

    }

    public void onRestart() {
        if( layout_videodetails != null && layout_videodetails.getVisibility() == View.GONE){
             layout_mediatype.setVisibility(View.VISIBLE);
             playpausebutton.setVisibility(View.VISIBLE);
             gethelper().updateactionbar(1);
             img_fullscreen.setVisibility(View.VISIBLE);
             setbottomimgview();
             img_fullscreen.setImageResource(R.drawable.ic_info_mode);
             layoutbackgroundcontroller.setVisibility(View.VISIBLE);
             layoutpause.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(myHandler != null && myRunnable != null)
            myHandler.removeCallbacks(myRunnable);
        destroyvideoplayer();
    }

    public void destroyvideoplayer()
    {
        if(player != null)
        {
            playerposition=player.getCurrentPosition();
            player.pause();
            player.stop();
            player.release();
            player=null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        pause();

        progressdialog.dismisswaitdialog();
        if(validationbaranimation != null)
        {
            validationbaranimation.cancel();
            validationbaranimation.reset();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(mOrientation != null)
            mOrientation.startListening(this);

        isfragmentstopped=false;

        if(validationbaranimation != null)
            img_scanover.startAnimation(validationbaranimation);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void getmediastartinfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    databasemanager mdbhelper = new databasemanager(applicationviavideocomposer.getactivity());
                    mdbhelper.createDatabase();

                    try {
                        mdbhelper.open();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Cursor cur = mdbhelper.getstartmediainfo(common.getfilename(mediafilepath));
                    String mediastartdevicedate="";
                    if (cur != null && cur.getCount() > 0 && cur.moveToFirst())
                    {
                        do{
                            //mediacompleteddate = "" + cur.getString(cur.getColumnIndex("videocompletedevicedate"));
                            mediastartdevicedate = "" + cur.getString(cur.getColumnIndex("videostartdevicedate"));
                            medianame = "" + cur.getString(cur.getColumnIndex("media_name"));
                            mediatoken = "" + cur.getString(cur.getColumnIndex("token"));
                            medianotes =  "" + cur.getString(cur.getColumnIndex("media_notes"));
                            mediafolder =  "" + cur.getString(cur.getColumnIndex("media_folder"));
                            mediaduration =  "" + cur.getString(cur.getColumnIndex("mediaduration"));
                            localkey = "" + cur.getString(cur.getColumnIndex("localkey"));
                            thumbnailurl = "" + cur.getString(cur.getColumnIndex("thumbnailurl"));
                            sync_date = "" + cur.getString(cur.getColumnIndex("sync_date"));
                            mediaid = "" + cur.getString(cur.getColumnIndex("videoid"));

                        }while(cur.moveToNext());
                    }

                    if (!mediastartdevicedate.isEmpty())
                    {
                        final String finalMediacompleteddate = mediastartdevicedate;
                        applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run()
                            {
                                try {
                                    Date startdate = null;
                                    if(finalMediacompleteddate.contains("T"))
                                    {
                                        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH);
                                        startdate = format.parse(finalMediacompleteddate);
                                    }
                                    else
                                    {
                                        DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                        startdate = format.parse(finalMediacompleteddate);
                                    }
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTime(startdate);
                                    if(player != null)
                                    {

                                        if(txt_createdtime.getText().toString().trim().length() == 0)
                                        {
                                            edt_medianame.setText(medianame);
                                            edt_medianotes.setText(medianotes);
                                        }

                                        int increaseseconds=player.getDuration()/1000;
                                        calendar.add(Calendar.SECOND, increaseseconds);
                                        Date enddate = calendar.getTime();
                                        DateFormat datee = new SimpleDateFormat("z",Locale.getDefault());
                                        String localTime = datee.format(enddate);
                                        txt_starttime.setText(common.parsedateformat(startdate) + " "+ common.parsetimeformat(startdate) +" " +
                                                localTime);
                                        txt_duration.setText(mediaduration);
                                        txt_endtime.setText(common.parsedateformat(enddate) + " "+ common.parsetimeformat(enddate) +" " +
                                                localTime);
                                        txt_createdtime.setText(common.parsetimeformat(startdate));

                                    }
                                    if(mediafolder.trim().length() > 0 && folderspinneradapter == null)
                                        setfolderspinner();

                                }catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        });


                        try
                        {
                            mdbhelper.close();
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void getmediametadata()
    {

        try {
            databasemanager mdbhelper = new databasemanager(applicationviavideocomposer.getactivity());
            mdbhelper.createDatabase();

            try {
                mdbhelper.open();
            } catch (Exception e) {
                e.printStackTrace();
            }

            ArrayList<metadatahash> mitemlist = mdbhelper.getmediametadatabyfilename(common.getfilename(mediafilepath));
            if (mitemlist.size() > 0)
            {
                for (int i = 0; i < mitemlist.size(); i++)
                {
                    if(metricmainarraylist.size() == i)
                    {
                        String metricdata = mitemlist.get(i).getMetricdata();
                        String sequencehash = mitemlist.get(i).getSequencehash();
                        String hashmethod = mitemlist.get(i).getHashmethod();
                        String videostarttransactionid = mitemlist.get(i).getVideostarttransactionid();
                        String serverdictionaryhash = mitemlist.get(i).getValuehash();
                        String color = mitemlist.get(i).getColor();
                        String latency = mitemlist.get(i).getLatency();
                        String sequenceno = mitemlist.get(i).getSequenceno();
                        parsemetadata(metricdata, hashmethod, videostarttransactionid, sequencehash, serverdictionaryhash, color,
                                latency, sequenceno);
                    }
                    else
                    {
                        String sequencehash = mitemlist.get(i).getSequencehash();
                        String hashmethod = mitemlist.get(i).getHashmethod();
                        String videostarttransactionid = mitemlist.get(i).getVideostarttransactionid();
                        String serverdictionaryhash = mitemlist.get(i).getValuehash();
                        String color = mitemlist.get(i).getColor();
                        String latency = mitemlist.get(i).getLatency();
                        String sequenceno = mitemlist.get(i).getSequenceno();
                        metricmainarraylist.set(i, new arraycontainer(hashmethod, videostarttransactionid,
                                sequencehash, serverdictionaryhash, color, latency));
                    }

                }
            }

            try
            {
                mdbhelper.close();
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }

        applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setseekbarlayoutcolor();
                if(metricmainarraylist != null && metricmainarraylist.size() > 0)
                {
                    arraycontainerformetric = new arraycontainer();
                    arraycontainerformetric = metricmainarraylist.get(0);

                    if(encryptionarraylist.size() == 0)
                        encryptionarraylist.add(metricmainarraylist.get(0));

                    if(encryptionarraylist.size() > 0)
                    {
                        encryptionarraylist.set(0,metricmainarraylist.get(0));
                        encryptionadapter.notifyDataSetChanged();
                    }
                }
                encryptionadapter.notifyDataSetChanged();
            }
        });
    }

    public void setfolderspinner()
    {
        final List<folder> folderitem=common.getalldirfolders();
        setspinnerpopupwindowheight(folderitem.size());
        folderspinneradapter = new folderdirectoryspinneradapter(applicationviavideocomposer.getactivity(),
                R.layout.row_myfolderspinneradapter,folderitem);

        int selectedposition=0;

        final File foldername = new File(mediafolder);

        for(int i=0;i<folderitem.size();i++)
        {
            if(folderitem.get(i).getFoldername().equalsIgnoreCase(foldername.getName()))
            {
                selectedposition=i;
                break;
            }
        }

        spinnermediafolder.setAdapter(folderspinneradapter);
        spinnermediafolder.setSelection(selectedposition,true);
        spinnermediafolder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id)
            {
                if(! folderitem.get(position).getFoldername().equalsIgnoreCase(foldername.getName()))
                {
                    progressdialog.showwaitingdialog(applicationviavideocomposer.getactivity());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                String folderpath=folderitem.get(position).getFolderdir();
                                if(! folderpath.equalsIgnoreCase(new File(mediafilepath).getParent()))
                                {
                                    if(common.movemediafile(new File(mediafilepath),new File(folderpath)))
                                    {
                                        File destinationmediafile = new File(folderpath + File.separator + new File(mediafilepath).getName());
                                        updatefilemediafolderdirectory(mediafilepath,destinationmediafile.getAbsolutePath(),folderpath);
                                        mediafilepath=destinationmediafile.getAbsolutePath();
                                        xdata.getinstance().saveSetting(config.selectedvideourl,mediafilepath);
                                    }
                                }
                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                            applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run()
                                {
                                    progressdialog.dismisswaitdialog();
                                    if(mcontrollernavigator != null)
                                        mcontrollernavigator.onItemClicked(mediafilepath,3);

                                    loadviewdata();
                                }
                            });
                        }
                    }).start();
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void updatefilemediafolderdirectory(String sourcefile,String destinationfilepath,String destinationmediafolder)
    {
        databasemanager mdbhelper = new databasemanager(applicationviavideocomposer.getactivity());
        mdbhelper.createDatabase();

        try {
            mdbhelper.open();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            mdbhelper.updatefilemediafolderdir(sourcefile,destinationfilepath,destinationmediafolder);
            mdbhelper.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void parsemetadata(String metadata,String hashmethod,String videostarttransactionid,String hashvalue,String metahash,
                              String color,String latency,String sequenceno) {
        try {

            Object json = new JSONTokener(metadata).nextValue();
            JSONObject jsonobject;
            if(json instanceof JSONObject)
            {
                jsonobject=new JSONObject(metadata);
                ArrayList<metricmodel> metricItemArraylist = new ArrayList<>();
                Iterator<String> myIter = jsonobject.keys();
                while (myIter.hasNext()) {
                    String key = myIter.next();
                    String value = jsonobject.optString(key);
                    metricmodel model = new metricmodel();
                    model.setMetricTrackKeyName(key);
                    model.setMetricTrackValue(value);
                    metricItemArraylist.add(model);
                }
                metricmainarraylist.add(new arraycontainer(metricItemArraylist,hashmethod,videostarttransactionid,hashvalue,metahash,
                        color,latency,sequenceno));
            }
            else if(json instanceof JSONArray)
            {
                JSONArray array = new JSONArray(metadata);
                for (int j = 0; j < array.length(); j++) {
                    ArrayList<metricmodel> metricItemArraylist = new ArrayList<>();
                    JSONObject object = array.getJSONObject(j);
                    Iterator<String> myIter = object.keys();
                    while (myIter.hasNext()) {
                        String key = myIter.next();
                        String value = object.optString(key);
                        metricmodel model = new metricmodel();
                        model.setMetricTrackKeyName(key);
                        model.setMetricTrackValue(value);
                        metricItemArraylist.add(model);
                    }
                    metricmainarraylist.add(new arraycontainer(metricItemArraylist,hashmethod,videostarttransactionid,hashvalue,metahash,
                            color,latency,sequenceno));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mOrientation != null)
            mOrientation.stopListening();

    }

    public class setonmediaprepared implements MediaPlayer.OnPreparedListener
    {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            isvideocompleted=false;
            maxincreasevideoduration=0;
            //controller.setMediaPlayer(new setonmediacontroller());
            controller.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));
            videoduration=mediaPlayer.getDuration();
            if(player != null)
            {
                setvideodata();
                hdlr.postDelayed(updatetimestream, 10);
            }


            try {
                if(playerposition > 0)
                {
                    mediaPlayer.seekTo((int)playerposition);
                    player.seekTo((int)playerposition);
                }
                else
                {
                    mediaPlayer.seekTo(100);
                }

            }catch (Exception e)
            {
                e.printStackTrace();
            }

            setmetriceshashesdata();
        }
    }

    adapteritemclick mitemclick=new adapteritemclick() {
        @Override
        public void onItemClicked(Object object) {
        }

        @Override
        public void onItemClicked(Object object, int type) {

        }
    };

    public void setupVideoPlayer(final Uri selecteduri)
    {
        try {
            player = new MediaPlayer();
            if(controller != null)
                controller.removeAllViews();

            controller = new videocontrollerview(applicationviavideocomposer.getactivity(),mitemclick,true);
            if(selecteduri!=null){
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.reset();
                player.setDataSource(applicationviavideocomposer.getactivity(),selecteduri);
                player.setSurface(surfacetexture);
                player.prepareAsync();
                player.setOnPreparedListener(new setonmediaprepared());
                player.setOnCompletionListener(new setonmediacompletion());
                player.setOnVideoSizeChangedListener(this);
                player.setOnBufferingUpdateListener(this);

            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setmetriceshashesdata()
    {
        if(myHandler != null && myRunnable != null)
            myHandler.removeCallbacks(myRunnable);

        myHandler=new Handler();
        myRunnable = new Runnable() {
            @Override
            public void run() {
                if(updatemetaattempt == 0 || updatemetaattempt >= 3 &&
                        ((sync_date.trim().isEmpty()) || sync_date.equalsIgnoreCase("0")))
                {
                    updatemetaattempt=0;
                    getmediastartinfo();
                    getmediametadata();
                }
                updatemetaattempt++;

                if(arraycontainerformetric != null)
                {
                  common.setgraphicalblockchainvalue(config.blockchainid, arraycontainerformetric.getVideostarttransactionid(), true);
                  common.setgraphicalblockchainvalue(config.hashformula, arraycontainerformetric.getHashmethod(), true);
                  common.setgraphicalblockchainvalue(config.datahash, arraycontainerformetric.getValuehash(), true);
                  common.setgraphicalblockchainvalue(config.matrichash, arraycontainerformetric.getMetahash(), true);

                  common.setspannable(getResources().getString(R.string.blockchain_id), " " + arraycontainerformetric.getVideostarttransactionid(), txt_blockchainid);
                  common.setspannable(getResources().getString(R.string.hash_formula), " " + arraycontainerformetric.getHashmethod(), txt_blockid);
                  common.setspannable(getResources().getString(R.string.mediahash), " " + arraycontainerformetric.getValuehash(), txt_blocknumber);
                  common.setspannable(getResources().getString(R.string.metrichash), " " + arraycontainerformetric.getMetahash(), txt_metahash);
                }

                if(currentprocessframe > 0 && metricmainarraylist.size() > 0 && currentprocessframe < metricmainarraylist.size())
                {
                    String data="";
                    for(int i=0;i<currentprocessframe;i++)
                    {
                        if(data.trim().isEmpty())
                        {
                            data=metricmainarraylist.get(i).getLatency();
                        }
                        else
                        {
                            data=data+","+metricmainarraylist.get(i).getLatency();
                        }
                    }
                    xdata.getinstance().saveSetting(config.currentlatency,data);
                }

                //setmetricesgraphicaldata();
                myHandler.postDelayed(this, 1000);
            }
        };
        myHandler.post(myRunnable);
    }

    public class setonmediacompletion implements MediaPlayer.OnCompletionListener
    {

        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            gethelper().setcurrentmediaposition((int) (metricmainarraylist.size() * 100) / 100);
            isvideocompleted=true;
            controller.setplaypauuse();
            maxincreasevideoduration=videoduration;
            isplaying = false;
            layout_validating.setVisibility(View.GONE);
          //  visualizer.setEnabled(false);
           /* playpausebutton.setImageResource(R.drawable.play_btn);
            playpausebutton.setVisibility(View.VISIBLE);*/
            mediaseekbar.setProgress(player.getCurrentPosition());
            img_colapseicon.setVisibility(View.GONE);


            if(layout_videodetails.getVisibility()==View.GONE){
                fullscreen_showcontrollers();
                isplaypauswebtnshow = false;
            }else{

                if(!islastdragarrow){
                    showcontrollers();
                    layoutbackgroundcontroller.setVisibility(View.VISIBLE);
                }
                if(islastdragarrow == true){
                    isplaypauswebtnshow = true;
                    playpausebutton.setImageResource(R.drawable.play_btn);
                    playpausebutton.setVisibility(View.VISIBLE);
                }
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(player != null && controller!= null && (! isfragmentstopped))
                    {
                        player.seekTo(0);
                        controller.setProgress(0,true);
                        playpausebutton.setImageResource(R.drawable.play_btn);
                        recyview_frames.scrollToPosition(0);
                    }
                }
            },200);
        }
    }

    public void setupvideodata() {

        if (mediafilepath != null && (!mediafilepath.isEmpty())) {

            if(mediafilepath == null || (mediafilepath.trim().isEmpty()))
            {
                common.showalert(getActivity(),getResources().getString(R.string.file_doesnot_exist));
                return;
            }

            if(! (new File(mediafilepath).exists()))
            {
                common.showalert(getActivity(),getResources().getString(R.string.file_doesnot_exist));
                return;
            }

            File file=new File(mediafilepath);
            long file_size = file.length();
            if(file_size == 0)
            {
                common.showalert(getActivity(),getResources().getString(R.string.file_is_empty));
                return;
            }
            if (mbitmaplist.size() != 0)
                runmethod = false;

            scurraberverticalbar.setVisibility(View.GONE);
            Uri uri= FileProvider.getUriForFile(applicationviavideocomposer.getactivity(),
                    BuildConfig.APPLICATION_ID + ".provider", new File(mediafilepath));
            setupVideoPlayer(uri);
            videoduration = 0;
            playerposition = 0;
            if (mediafilepath != null && (!mediafilepath.isEmpty())) {
                mvideoframes.clear();
                mainvideoframes.clear();
                mallframes.clear();
            }

            tvsize.setText(common.filesize(mediafilepath));
            playpausebutton.setImageResource(R.drawable.play_btn);
        }
    }
    //https://stackoverflow.com/questions/32905939/how-to-customize-the-polyline-in-google-map/46559529

    public void resetmedianamenotes()
    {
        edt_medianame.setText(medianame);
        edt_medianotes.setText(medianotes);
    }

    public void callupdatemetaapi(String mediaid,String title,String description)
    {
        if(!gethelper().isuserlogin()){
            Toast.makeText(applicationviavideocomposer.getactivity(),applicationviavideocomposer.getactivity()
                    .getResources().getString(R.string.login_here),Toast.LENGTH_SHORT).show();
            resetmedianamenotes();
            gethelper().redirecttologin();
            return;
        }

        if(mediaid.trim().isEmpty() || mediaid.equalsIgnoreCase("0")){
            Toast.makeText(applicationviavideocomposer.getactivity(),applicationviavideocomposer.getactivity()
                    .getResources().getString(R.string.invalid_empty_mediaid),Toast.LENGTH_SHORT).show();
            resetmedianamenotes();
            return;
        }

        HashMap<String,String> requestparams=new HashMap<>();
        requestparams.put("type",config.type_video);
        requestparams.put("action","updatemeta");
        requestparams.put("id",mediaid);
        requestparams.put("authtoken",xdata.getinstance().getSetting(config.authtoken));
        requestparams.put("title",title);
        requestparams.put("description",description);
        progressdialog.showwaitingdialog(getActivity());
        gethelper().xapipost_send(applicationviavideocomposer.getactivity(),requestparams, new apiresponselistener() {
            @Override
            public void onResponse(taskresult response) {
                progressdialog.dismisswaitdialog();
                if(response.isSuccess())
                {
                    try {
                        JSONObject object=new JSONObject(response.getData().toString());
                        if(object.has("success"))
                        {
                            if(object.getString("success").equalsIgnoreCase("true") || object.getString("success").equalsIgnoreCase("1"))
                            {
                                Toast.makeText(getActivity(), applicationviavideocomposer.getactivity()
                                        .getResources().getString(R.string.metadata_updated), Toast.LENGTH_SHORT).show();
                            }
                        }
                        if(object.has("error"))
                        {
                            Toast.makeText(getActivity(), object.getString("error"), Toast.LENGTH_SHORT).show();
                            //resetmedianamenotes();
                        }

                        medianame=edt_medianame.getText().toString().trim();
                        medianotes=edt_medianotes.getText().toString().trim();
                        if(! localkey.isEmpty())
                            updatemediainfo(localkey,edt_medianame.getText().toString().trim(),
                                    edt_medianotes.getText().toString().trim().trim());

                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Toast.makeText(getActivity(), applicationviavideocomposer.getactivity().getResources().getString(R.string.json_parsing_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void updatemediainfo(String localkey,String medianame,String medianotes)
    {
        databasemanager mdbhelper = new databasemanager(applicationviavideocomposer.getactivity());
        mdbhelper.createDatabase();

        try {
            mdbhelper.open();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        mdbhelper.updatemediainfobylocalkey(localkey,medianame,medianotes);
        try
        {
            mdbhelper.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        if(mcontrollernavigator != null)
            mcontrollernavigator.onItemClicked(null,1);
    }

    public void editabletext(){
        Editable editableText=  edt_medianame.getEditableText();
        if(editableText !=null) {
            edt_medianame.setInputType(InputType.TYPE_CLASS_TEXT);
            edt_medianame.setEllipsize(TextUtils.TruncateAt.END);
            edt_medianame.setSingleLine();
        }
        else
        {
            edt_medianame.setEnabled(false);
            edt_medianame.setClickable(false);
            edt_medianame.setKeyListener(null);
        }
    }

    public void start() {
        isvideocompleted=false;
        if(player != null)
        {
            if(mediafilepath!=null)
            {
                isplaying=true;
                playpausebutton.setImageResource(R.drawable.pausebutton);
                player.start();

            }
        }
    }
    public void pause() {
        if(player != null && player.isPlaying()){
            playpausebutton.setImageResource(R.drawable.play_btn);
            player.pause();
        }
    }

    private Runnable updatetimestream = new Runnable() {
        @Override
        public void run() {
            if(player != null ){

                if(player.getCurrentPosition() > maxincreasevideoduration)
                    maxincreasevideoduration=player.getCurrentPosition();

                if(currentmediaduration == 0 || (player.getCurrentPosition() > currentmediaduration))
                    currentmediaduration =player.getCurrentPosition();  // suppose its on 4th pos means 4000

                videostarttime = player.getCurrentPosition();
                mediaseekbar.setProgress(player.getCurrentPosition());

                if (time_current != null)
                    time_current.setText(common.gettimestring(videostarttime));

                if(arraycontainerformetric != null && layout_halfscrnimg.getVisibility() == View.VISIBLE && (!gethelper().isdraweropened()) && (player.isPlaying()))
                {
                    String color = "white";
                    if (arraycontainerformetric.getColor() != null && (!arraycontainerformetric.getColor().isEmpty()))
                        color = arraycontainerformetric.getColor();

                    switch (color) {
                        case "green":
                            txt_section_validating_secondary.setText(config.validating);
                            try {
                                DrawableCompat.setTint(img_scanover.getDrawable(), ContextCompat.getColor(applicationviavideocomposer.getactivity()
                                        , R.color.scanover_green));
                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                            layout_validating.setVisibility(View.VISIBLE);

                            break;
                        case "white":
                                layout_validating.setVisibility(View.GONE);
                            break;
                        case "red":
                            txt_section_validating_secondary.setText(config.invalid);
                            try {
                                DrawableCompat.setTint(img_scanover.getDrawable(), ContextCompat.getColor(applicationviavideocomposer.getactivity()
                                        , R.color.scanover_red));
                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                            layout_validating.setVisibility(View.VISIBLE);

                            break;
                        case "yellow":
                            txt_section_validating_secondary.setText(config.caution);
                            try {
                                DrawableCompat.setTint(img_scanover.getDrawable(), ContextCompat.getColor(applicationviavideocomposer.getactivity()
                                        , R.color.scanover_yellow));
                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                            layout_validating.setVisibility(View.VISIBLE);

                            break;
                    }
                }
                else
                {
                        layout_validating.setVisibility(View.GONE);
                }

                hdlr.postDelayed(this, 10);
            }
        }
    };

    public void setvideodata(){

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(player != null)
                {
                    try
                    {
                        endtime = player.getDuration();
                        videostarttime = player.getCurrentPosition();

                        if (totalduration != null)
                            totalduration.setText(common.gettimestring(endtime));

                        if  (time_current != null)
                            time_current.setText(common.gettimestring(videostarttime));

                        mediaseekbar.setMax(endtime);
                        mediaseekbar.setProgress(videostarttime);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
            }
        },10);
    }


     public void setplaypuasebtnondrag(){

         int buttonheight = playpausebutton.getHeight();
         int layoutvideotextureviewheight = rl_videotextureview.getHeight();
         int centerheightoflayout = layoutvideotextureviewheight - (common.getviewheight(actionbarpercentage) +
                 common.getviewheight(scrubberviewpercentage));
         int halfcenterheightoflayout = (centerheightoflayout/2);
         double res = (buttonheight / 100.0f) * 70;
         int lastvalue = (halfcenterheightoflayout+common.getviewheight(actionbarpercentage)) - (int)res;
         recenterplaypause(lastvalue ,0);
     }

    public void updatesurfaceviewsize(){

        int surfaceView_Width = videotextureview.getWidth();
        int surfaceView_Height = videotextureview.getHeight();

        float video_Width=0,video_Height=0;
        if(player != null)
        {
            video_Width = player.getVideoWidth();
            video_Height = player.getVideoHeight();
        }

        float ratio_width = surfaceView_Width/video_Width;
        float ratio_height = surfaceView_Height/video_Height;
        float aspectratio = video_Width/video_Height;
        ViewGroup.LayoutParams layoutParams = videotextureview.getLayoutParams();
        if (ratio_width > ratio_height){
            layoutParams.width = (int) (surfaceView_Height * aspectratio);
            layoutParams.height = surfaceView_Height;
        }else{
            layoutParams.width = surfaceView_Width;
            layoutParams.height = (int) (surfaceView_Width / aspectratio);
        }
        videotextureview.setLayoutParams(layoutParams);
        videotextureview.setVisibility(View.VISIBLE);
        int buttonheight = playpausebutton.getHeight();
        int layoutvideotextureviewheight = rl_videotextureview.getHeight();
        int centerheightoflayout = layoutvideotextureviewheight - (common.getviewheight(actionbarpercentage) + common.getviewheight(scrubberviewpercentage));
        int halfcenterheightoflayout = (centerheightoflayout/2);
        //int percentageheight = (buttonheight / 100) * 75;
        double res = (buttonheight / 100.0f) * 70;
        int lastvalue = (halfcenterheightoflayout+common.getviewheight(actionbarpercentage)) - (int)res;
        recenterplaypause(lastvalue ,0);
    }

    public void updatesurfaceviewsizefullscreen(int surfaceView_Width,int surfaceView_Height){

        ViewGroup.LayoutParams layoutParams = videotextureview.getLayoutParams();
        layoutParams.width = surfaceView_Width;
        layoutParams.height = surfaceView_Height;
        videotextureview.setLayoutParams(layoutParams);
        int buttonheight = playpausebutton.getHeight();
        int centerheight = (surfaceView_Height + navigationbarheight)/2;
        int finalmargin = centerheight -  buttonheight/2;
        recenterplaypause(finalmargin,1);
    }

    private void getbitmap(final int viewwidth)
    {
        backgroundexecutor.execute(new backgroundexecutor.task("", 0L, "") {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void execute() {

           Uri uri = FileProvider.getUriForFile(applicationviavideocomposer.getactivity(),
                   BuildConfig.APPLICATION_ID + ".provider", new File(mediafilepath));

           if (uri != null) {
               try {
                   MediaMetadataRetriever mediametadataretriever = new MediaMetadataRetriever();
                   mediametadataretriever.setDataSource(getContext(), uri);

                   // Retrieve media data
                   long videoLengthInMs = Integer.parseInt(mediametadataretriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) * 1000;

                   String rotation = mediametadataretriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
                   if(rotation != null && (! rotation.equalsIgnoreCase("null")) && (! rotation.isEmpty()))
                       videorotatedangle =Integer.parseInt(rotation);

                   String time = mediametadataretriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                   long timeInmillisec = Long.parseLong( time );
                   long duration = timeInmillisec / 1000;
                   long hours = duration / 3600;
                   long minutes = (duration - hours * 3600) / 60;
                   long seconds = duration - (hours * 3600 + minutes * 60);

                   // Set thumbnail properties (Thumbs are squares)

                    int thumbWidthwithoutmultipay =(int)getActivity().getResources().getDimension(R.dimen.play_screen_bar_height);

                    final int thumbWidth = (int)(thumbWidthwithoutmultipay * 1.5);

                    final int thumbHeight = (int)getActivity().getResources().getDimension(R.dimen.play_screen_bar_height);

                    int numThumbs = (int) Math.ceil(((float) viewwidth) / thumbWidth);

                   //90 portrait
                   // left landscape 0
                   // right landscape 180
                   // reverse portrait 270


                    if(seconds <= 3 && minutes == 0 && hours == 0){
                        numThumbs = 10;
                    }else{
                        numThumbs = 15;
                   }
                   final long interval = videoLengthInMs / numThumbs;
                   for (int i = 0; i < numThumbs; ++i) {
                       Bitmap bitmap = mediametadataretriever.getFrameAtTime(i * interval, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                       // TODO: bitmap might be null here, hence throwing NullPointerException. You were right
                       try {
                           bitmap = Bitmap.createScaledBitmap(bitmap, thumbWidth, thumbHeight, false);

                           mbitmaplist.add(i, new frame(i, bitmap, false));

                           applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                               @Override
                               public void run() {
                                   if (adapter != null)
                                       adapter.notifyDataSetChanged();

                                       scurraberverticalbar.setVisibility(View.GONE);
                               }
                           });

                       } catch (Exception e) {
                           e.printStackTrace();
                       }
                   }

                   if (mediametadataretriever != null)
                          mediametadataretriever.release();

               } catch (final Throwable e) {
                   Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
               }
           }
            }
          });
    }

    public void showcontrollers() {
        videotextureview.setClickable(true);
        layout_mediatype.setVisibility(View.VISIBLE);
        playpausebutton.setImageResource(R.drawable.play_btn);
        playpausebutton.setVisibility(View.VISIBLE);
        img_fullscreen.setVisibility(View.VISIBLE);
        imgpause.setVisibility(View.GONE);
        img_colapseicon.setVisibility(View.GONE);
    }

    public void fullscreen_showcontrollers() {
        //layout_halfscrnimg.getLayoutParams().height = rootviewheight;
        videotextureview.setClickable(true);
        layout_mediatype.setVisibility(View.VISIBLE);
       // common.slidetodown(layout_mediatype); // visible actionbar
        layout_mediatype.setVisibility(View.VISIBLE);
        playpausebutton.setImageResource(R.drawable.play_btn);
        playpausebutton.setVisibility(View.VISIBLE);
        img_fullscreen.setVisibility(View.VISIBLE);
        imgpause.setVisibility(View.GONE);
        img_colapseicon.setVisibility(View.GONE);
        gethelper().updateactionbar(1);
        setbottomimgview();
        layoutbackgroundcontroller.setVisibility(View.VISIBLE);
        img_fullscreen.setImageResource(R.drawable.ic_info_mode);
        gethelper().setdrawerheightonfullscreen(0);
        imgpause.setImageResource(R.drawable.ic_pause);
    }

    public void setbottomimgview(){
        RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,0,0, (int) -getResources().getDimension(R.dimen.margin_20dp));
        params.addRule(RelativeLayout.ABOVE,R.id.layout_backgroundcontroller);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        img_fullscreen.setLayoutParams(params);
    }
    public void collapseimg_view(){
        RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.ABOVE,R.id.layout_backgroundcontroller );
        params.setMargins(0,0,0,(int) -getResources().getDimension(R.dimen.margin_15dp));
        img_fullscreen.setLayoutParams(params);
    }

    private void setseekbarlayoutcolor(){
        try
        {
            linearseekbarcolorview.removeAllViews();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        int sectioncount=0;
        String lastcolor="";
        ArrayList<String> colorsectioncount=new ArrayList<>();
        if(metricmainarraylist !=  null){
            for(int i=0 ; i<metricmainarraylist.size();i++)
            {
                if(metricmainarraylist.get(i) != null)
                {
                    String color=metricmainarraylist.get(i).getColor();
                    sectioncount++;
                    if(color.trim().isEmpty())
                        color=config.color_transparent;

                    if(! lastcolor.equalsIgnoreCase(color))
                    {
                        sectioncount=0;
                        sectioncount++;
                        colorsectioncount.add(color+","+sectioncount);
                    }
                    else
                    {
                        colorsectioncount.set(colorsectioncount.size()-1,color+","+sectioncount);
                    }
                    lastcolor=color;
                }
            }

            for(int i=0;i<colorsectioncount.size();i++)
            {
                String item=colorsectioncount.get(i);
                if(! item.trim().isEmpty())
                {
                    String[] itemarray=item.split(",");
                    if(itemarray.length >= 2)
                    {
                        String writecolor=itemarray[0];
                        String weight=itemarray[1];
                        if(! weight.trim().isEmpty())
                        {
                            linearseekbarcolorview.addView(getmediaseekbarbackgroundview(weight,writecolor));
                        }
                    }
                }
            }
        }
        progressmediasync.setVisibility(View.VISIBLE);
        gethelper().setdatacomposing(false);
    }

    @Override
    public void showhideviewondrawer(boolean drawershown) {
        super.showhideviewondrawer(drawershown);

        if (drawershown) {

            if(!islastdragarrow){
                if (player != null && player.isPlaying()){
                    img_colapseicon.setVisibility(View.GONE);
                    if(imgpause.getVisibility()==View.GONE && layoutbackgroundcontroller.getVisibility() == View.GONE){
                        layoutbackgroundcontroller.setVisibility(View.GONE);
                        imgpause.setVisibility(View.GONE);
                        layoutpause.setVisibility(View.GONE);
                    }else{
                        layoutbackgroundcontroller.setVisibility(View.VISIBLE);
                        imgpause.setVisibility(View.VISIBLE);
                        layoutpause.setVisibility(View.VISIBLE);
                    }
                }else {
                    gethelper().updateactionbar(0);
                    layout_mediatype.setVisibility(View.GONE);
                    videodownwordarrow.setVisibility(View.GONE);
                    rl_video_downwordarrow.setVisibility(View.GONE);
                    playpausebutton.setVisibility(View.GONE);
                    img_fullscreen.setVisibility(View.GONE);

                    if(imgpause.getVisibility()==View.VISIBLE){
                        layoutbackgroundcontroller.setVisibility(View.VISIBLE);
                        imgpause.setVisibility(View.VISIBLE);
                        layoutpause.setVisibility(View.VISIBLE);
                        getcontrollerheight();
                    }else{
                        gethelper().setdrawerheightonfullscreen(0);
                        imgpause.setVisibility(View.GONE);
                        layoutbackgroundcontroller.setVisibility(View.GONE);
                    }

                }
            }else{
                gethelper().updateactionbar(0);
            }
        } else {

            if (!islastdragarrow) {
                if (player != null && player.isPlaying()) {
                    img_colapseicon.setVisibility(View.VISIBLE);

                    if(imgpause.getVisibility()==View.GONE && layoutbackgroundcontroller.getVisibility() == View.GONE){
                        layoutbackgroundcontroller.setVisibility(View.GONE);
                        imgpause.setVisibility(View.GONE);
                        layoutpause.setVisibility(View.GONE);
                        gethelper().updateactionbar(0);
                        layout_mediatype.setVisibility(View.GONE);
                    }else{
                        layoutbackgroundcontroller.setVisibility(View.VISIBLE);
                        imgpause.setVisibility(View.VISIBLE);
                        layoutpause.setVisibility(View.VISIBLE);
                        gethelper().updateactionbar(0);
                        layout_mediatype.setVisibility(View.GONE);
                    }
                } else {

                        gethelper().updateactionbar(1);
                        img_fullscreen.setImageResource(R.drawable.ic_info_mode);
                        img_fullscreen.setVisibility(View.VISIBLE);
                        layoutbackgroundcontroller.setVisibility(View.VISIBLE);
                        layout_mediatype.setVisibility(View.VISIBLE);

                        if(imgpause.getVisibility()==View.VISIBLE){
                            imgpause.setImageResource(R.drawable.ic_play);
                            imgpause.setVisibility(View.VISIBLE);
                            playpausebutton.setVisibility(View.GONE);
                            getcontrollerheight();
                            getpausebtnheight();
                        }else{
                            playpausebutton.setVisibility(View.VISIBLE);
                            setbottomimgview();
                        }
                }
            }else{
                gethelper().updateactionbar(1);
            }
        }
    }

    public void setheadermargin(final int headerheight, final int scrubberheight, int layoutheight, boolean ifheightset){
        if(ifheightset)
        {
            RelativeLayout.LayoutParams paramsvideotextureview  = new RelativeLayout.LayoutParams(targetwidth,(rootviewheight-navigationbarheight));
            paramsvideotextureview.setMargins(0,headerheight,0,scrubberheight);
            videotextureview.setLayoutParams(paramsvideotextureview);
            videotextureview.post(new Runnable() {
                @Override
                public void run() {
                    if(headerheight == 0 && scrubberheight == 0){
                        updatesurfaceviewsizefullscreen(videotextureview.getWidth(),videotextureview.getHeight());
                    }else{
                        setplaypuasebtnondrag();
                    }
                }
            });

        }else{
            RelativeLayout.LayoutParams paramsvideotextureview  = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
            paramsvideotextureview.setMargins(0,headerheight-1,0,scrubberheight-50);
            videotextureview.setLayoutParams(paramsvideotextureview);
            videotextureview.post(new Runnable() {
                @Override
                public void run() {
                    updatesurfaceviewsize();
                }
            });
        }

    }

    public void setfooterlayout()
    {
            CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.MATCH_PARENT);
            params.setMargins(0,0,0,navigationbarheight);
            layout_videoreader.setLayoutParams(params);
    }

    public void hidefocus(EditText edittext){
        edittext.setClickable(false);
        edittext.setFocusable(false);
        edittext.setFocusableInTouchMode(false);
        layout_halfscrnimg.setVisibility(View.VISIBLE);
        if(isplaying)
            layout_validating.setVisibility(View.VISIBLE);

        removeheadermargin();
    }

    public void removeheaderpadding(){
        RelativeLayout.LayoutParams params  = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW,R.id.layout_halfscrnimg);
        layout_videodetails.setPadding(0,0,0,footerheight);
        layout_videodetails.setLayoutParams(params);
        layout_videodetails.requestLayout();
    }
    public void setheadermargin(){
        RelativeLayout.LayoutParams params  = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,(detailviewheight-navigationbarheight)+common.getviewheight(actionbarpercentage));
        params.setMargins(0,common.getviewheight(actionbarpercentage)+20,0,0);
        layout_videodetails.setPadding(0,0,0,0);
        layout_videodetails.setLayoutParams(params);
        layout_videodetails.requestLayout();
    }

    public void removeheadermargin(){
        RelativeLayout.LayoutParams params  = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW,R.id.layout_halfscrnimg);
        params.setMargins(0,0,0,0);

        layout_videodetails.setPadding(0,0,0,(footerheight));
        layout_videodetails.setLayoutParams(params);
        layout_videodetails.requestLayout();
    }

    public void visiblefocus(EditText edittext){
        layout_halfscrnimg.setVisibility(View.GONE);
        layout_validating.setVisibility(View.GONE);

        setheadermargin();
        edittext.setClickable(true);
        edittext.setEnabled(true);
        edittext.setFocusable(true);
        edittext.setFocusableInTouchMode(true);
        edittext.setSelection(edittext.getText().length());
        edittext.requestFocus();

    }

    public void showvideoplayer(){
        if(layout_halfscrnimg.getVisibility() == View.GONE){
            hidekeyboard();
            layout_halfscrnimg.setVisibility(View.VISIBLE);
            if(isplaying)
                layout_validating.setVisibility(View.VISIBLE);

            removeheadermargin();
        }
    }

    public void  setspinnerpopupwindowheight(int size){
        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);
            // Get private mPopup member variable and try cast to ListPopupWindow
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(spinnermediafolder);
            // Set popupWindow height to 500px
            if(size>3){
                popupWindow.setHeight(300);
            }else{
                popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        }
        catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            // silently fail...
        }
    }

    public void getcontrollerheight(){
        gethelper().setdrawerheightonfullscreen(common.getviewheight(controllerheightpercentage));
    }

    public void backgroundcolor(){
        layoutpause.setBackgroundColor(getResources().getColor(R.color.whitetransparent));
        layout_seekbartiming.setBackgroundColor(getResources().getColor(R.color.whitetransparent));
        layoutcustomcontroller.setBackgroundColor(getResources().getColor(R.color.transparent));
    }

    public void getpausebtnheight(){
        layoutpauseheight = common.getviewheight(pauselayoutpercentage);
        setbottomimgview();
    }
}
