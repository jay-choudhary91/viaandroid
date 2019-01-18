package com.cryptoserver.composer.fragments;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.cryptoserver.composer.BuildConfig;
import com.cryptoserver.composer.R;
import com.cryptoserver.composer.adapter.framebitmapadapter;
import com.cryptoserver.composer.adapter.videoframeadapter;
import com.cryptoserver.composer.applicationviavideocomposer;
import com.cryptoserver.composer.database.databasemanager;
import com.cryptoserver.composer.interfaces.adapteritemclick;
import com.cryptoserver.composer.models.arraycontainer;
import com.cryptoserver.composer.models.frame;
import com.cryptoserver.composer.models.metadatahash;
import com.cryptoserver.composer.models.metricmodel;
import com.cryptoserver.composer.models.videomodel;
import com.cryptoserver.composer.models.wavevisualizer;
import com.cryptoserver.composer.utils.FullDrawerLayout;
import com.cryptoserver.composer.utils.NoScrollRecycler;
import com.cryptoserver.composer.utils.centerlayoutmanager;
import com.cryptoserver.composer.utils.circularImageview;
import com.cryptoserver.composer.utils.common;
import com.cryptoserver.composer.utils.config;
import com.cryptoserver.composer.utils.md5;
import com.cryptoserver.composer.utils.progressdialog;
import com.cryptoserver.composer.utils.sha;
import com.cryptoserver.composer.utils.videocontrollerview;
import com.cryptoserver.composer.utils.xdata;
import com.cryptoserver.composer.videotrimmer.utils.backgroundexecutor;
import com.cryptoserver.composer.views.customfonttextview;
import com.cryptoserver.composer.views.customseekbar;
import com.github.mikephil.charting.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by devesh on 21/8/18.
 */

public class videoreaderfragment extends basefragment implements AdapterView.OnItemSelectedListener,View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback,View.OnTouchListener,TextureView.SurfaceTextureListener,
     MediaPlayer.OnVideoSizeChangedListener,MediaPlayer.OnBufferingUpdateListener
{

    @BindView(R.id.img_dotmenu)
    ImageView img_dotmenu;
    @BindView(R.id.img_folder)
    ImageView img_folder;
    @BindView(R.id.img_camera)
    ImageView img_camera;
    @BindView(R.id.img_arrow_back)
    ImageView img_arrow_back;
    @BindView(R.id.img_delete_media)
    ImageView img_delete_media;

    @BindView(R.id.recyview_frames)
    NoScrollRecycler recyview_frames;
    @BindView(R.id.layout_drawer)
    LinearLayout layout_drawer;
    @BindView(R.id.layout_scrubberview)
    RelativeLayout layout_scrubberview;
    @BindView(R.id.txt_slot1)
    TextView txtSlot1;
    @BindView(R.id.txt_slot2)
    TextView txtSlot2;
    @BindView(R.id.txt_slot3)
    TextView txtSlot3;
    @BindView(R.id.txt_hashes)
    TextView txt_hashes;
    @BindView(R.id.txt_metrics)
    TextView txt_metrics;
    @BindView(R.id.scrollview_metrices)
    ScrollView scrollview_metrices;
    @BindView(R.id.scrollview_hashes)
    ScrollView scrollview_hashes;
    @BindView(R.id.recyview_metrices)
    RecyclerView recyview_metrices;
    @BindView(R.id.recyview_item)
    RecyclerView recyview_hashes;
    @BindView(R.id.fragment_graphic_drawer_container)
    FrameLayout fragment_graphic_container;

    //tabdetails

    @BindView(R.id.spinner)
    Spinner photospinner;
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
    RelativeLayout layout_mediatype;
    @BindView(R.id.layout_videodetails)
    RelativeLayout layout_photodetails;
    @BindView(R.id.scrollview_detail)
    ScrollView scrollview_detail;
    @BindView(R.id.img_fullscreen)
    ImageView img_fullscreen;
    @BindView(R.id.edt_medianame)
    EditText edt_medianame;
    @BindView(R.id.edt_medianotes)
    EditText edt_medianotes;
    @BindView(R.id.layout_footer)
    RelativeLayout layout_footer;
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
    @BindView(R.id.time)
    TextView totalduration;
    @BindView(R.id.layout_customcontroller)
    LinearLayout layoutcustomcontroller;

    @BindView(R.id.txt_address)
    customfonttextview tvaddress;
    @BindView(R.id.txt_latitude)
    customfonttextview tvlatitude;
    @BindView(R.id.txt_longitude)
    customfonttextview tvlongitude;
    @BindView(R.id.txt_altitude)
    customfonttextview tvaltitude;
    @BindView(R.id.txt_speed)
    customfonttextview tvspeed;
    @BindView(R.id.txt_heading)
    customfonttextview tvheading;
    @BindView(R.id.txt_traveled)
    customfonttextview tvtraveled;
    @BindView(R.id.txt_xaxis)
    customfonttextview tvxaxis;
    @BindView(R.id.txt_yaxis)
    customfonttextview tvyaxis;
    @BindView(R.id.txt_zaxis)
    customfonttextview tvzaxis;
    @BindView(R.id.txt_phone)
    customfonttextview tvphone;
    @BindView(R.id.txt_network)
    customfonttextview tvnetwork;
    @BindView(R.id.txt_connection)
    customfonttextview tvconnection;
    @BindView(R.id.txt_version)
    customfonttextview tvversion;
    @BindView(R.id.txt_wifi)
    customfonttextview tvwifi;
    @BindView(R.id.txt_gps_accuracy)
    customfonttextview tvgpsaccuracy;
    @BindView(R.id.txt_screen)
    customfonttextview tvscreen;
    @BindView(R.id.txt_country)
    customfonttextview tvcountry;
    @BindView(R.id.txt_cpu_usage)
    customfonttextview tvcpuusage;
    @BindView(R.id.txt_brightness)
    customfonttextview tvbrightness;
    @BindView(R.id.txt_timezone)
    customfonttextview tvtimezone;
    @BindView(R.id.txt_memoryusage)
    customfonttextview tvmemoryusage;
    @BindView(R.id.txt_bluetooth)
    customfonttextview tvbluetooth;
    @BindView(R.id.txt_localtime)
    customfonttextview tvlocaltime;
    @BindView(R.id.txt_storagefree)
    customfonttextview tvstoragefree;
    @BindView(R.id.txt_language)
    customfonttextview tvlanguage;
    @BindView(R.id.txt_uptime)
    customfonttextview tvuptime;
    @BindView(R.id.txt_battery)
    customfonttextview tvbattery;
    @BindView(R.id.img_lefthandle)
    ImageView handleimageview;
    @BindView(R.id.btn_playpause)
    circularImageview playpausebutton;
    @BindView(R.id.txt_title_actionbarcomposer)
    TextView txt_title_actionbarcomposer;
    @BindView(R.id.txt_mediatimethumb)
    TextView txt_mediatimethumb;
    @BindView(R.id.layout_progressline)
    RelativeLayout layout_progressline;

    @BindView(R.id.layout_googlemap)
    LinearLayout layout_googlemap;
    @BindView(R.id.googlemap)
    FrameLayout googlemap;
    @BindView(R.id.img_compass)
    ImageView img_compass;
    @BindView(R.id.img_pause)
    ImageView imgpause;

    GoogleMap mgooglemap;
    Surface surfacetexture = null;

    boolean ismediaplayer = false;
    String medianame = "",medianotes = "",mediafolder = "",mediatransectionid = "",latitude = "", longitude = "",screenheight = "",screenwidth = "",
            mediadate = "",mediatime = "",mediasize="",lastsavedangle="";
    private float currentDegree = 0f;
    private BroadcastReceiver getmetadatabroadcastreceiver,getencryptionmetadatabroadcastreceiver;
    int targetheight,previousheight,targetwidth,previouswidth, previouswidthpercentage,scrubberviewwidth;
    private Handler hdlr = new Handler();
    StringBuilder mFormatBuilder;
    Formatter mFormatter;

    private RelativeLayout scurraberverticalbar;
    private String mediafilepath = null;
    private RelativeLayout showcontrollers;
    private TextureView videotextureview;
    private MediaPlayer player;
    private videocontrollerview controller;
    private View rootview = null;
    private String selectedmetrics="";
    private LinearLayout linearLayout;
    private String keytype =config.prefs_md5;
    private long currentframenumber =0,playerposition=0;
    private long frameduration =15, mframetorecordcount =0;
    private boolean ishashprocessing=false;
    private boolean islisttouched=false,islistdragging=false,isfromlistscroll=false;
    private int REQUESTCODE_PICK=201;
    private static final int request_read_external_storage = 1;
    private Uri selectedvideouri =null;
    private boolean issurafcedestroyed=false;
    private boolean isscrubbing=true;
    private Handler myHandler,handlerrecycler;
    private Runnable myRunnable,runnablerecycler;
    private long framecount=0;
    private long videoduration =0,framesegment=0,currentvideoduration=0,maxincreasevideoduration=0,currentvideodurationseconds=0,lastgetframe=0;
    private boolean isdraweropen=false;
    private LinearLayoutManager mlinearlayoutmanager;
    private String selectedhashes ="";
    private int lastmetricescount=0;
    private SurfaceHolder holder;
    private ArrayList<videomodel> mainvideoframes =new ArrayList<>();
    private ArrayList<videomodel> mvideoframes =new ArrayList<>();
    private ArrayList<videomodel> mallframes =new ArrayList<>();
    private ArrayList<frame> mbitmaplist =new ArrayList<>();
    private ArrayList<videomodel> mmetricsitems =new ArrayList<>();
    private ArrayList<videomodel> mhashesitems =new ArrayList<>();
    private ArrayList<arraycontainer> metricmainarraylist = new ArrayList<>();
    private videoframeadapter mmetricesadapter,mhashesadapter;
    private framebitmapadapter adapter;
    private Handler waveHandler;
    private Runnable waveRunnable;
    boolean runmethod = false;
    private LinearLayoutManager mLayoutManager;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    public int selectedsection=1;
    public boolean isvideocompleted=false;
    public int flingactionmindstvac;
    private  final int flingactionmindspdvac = 10;
    String[] soundamplitudealuearray ;
    private Visualizer mVisualizer;
    ArrayList<wavevisualizer> wavevisualizerslist =new ArrayList<>();
    ArrayList<String> addhashvaluelist = new ArrayList<>();
    private BroadcastReceiver coredatabroadcastreceiver;
    int count = 0;
    @BindView(R.id.textfetchdata)
    TextView textfetchdata;
    fragmentgraphicaldrawer  graphicaldrawerfragment;
    FullDrawerLayout navigationdrawer;
    private ActionBarDrawerToggle drawertoggle;
    private long audioduration =0, currentaudioduration =0, currentaudiodurationseconds =0;
    private int ontime =0, videostarttime =0, endtime =0, fTime = 5000, bTime = 5000;

    int position=0;
    metricmodel setmetricmodel;
    int mheightview = 0;

    private float videoheight, videowidth;

    arraycontainer arraycontainerformetric =null;
    adapteritemclick mcontrollernavigator;

    @Override
    public int getlayoutid() {
        return R.layout.full_screen_videoview;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        if(rootview == null) {
            rootview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, rootview);
            gethelper().setrecordingrunning(false);

            videotextureview = (TextureView) findViewById(R.id.videotextureview);
            linearLayout=rootview.findViewById(R.id.content);
            showcontrollers=rootview.findViewById(R.id.video_container);
            scurraberverticalbar=rootview.findViewById(R.id.scrubberverticalbar);
            mFormatBuilder = new StringBuilder();
            mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
            videotextureview.setSurfaceTextureListener(this);

            //drawer implementation
            navigationdrawer = (FullDrawerLayout) rootview.findViewById(R.id.drawer_layout);
            navigationdrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            drawertoggle = new ActionBarDrawerToggle(
                    getActivity(), navigationdrawer, R.string.drawer_open, R.string.drawer_close){

                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);
                    handleimageview.setVisibility(View.VISIBLE);
                }
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    handleimageview.setVisibility(View.GONE);
                }
            };
            navigationdrawer.addDrawerListener(drawertoggle);
            drawertoggle.syncState();
            navigationdrawer.setScrimColor(getResources().getColor(android.R.color.transparent));

            mediaseekbar.setPadding(0,0,0,0);

            mheightview = getContext().getResources().getDimensionPixelOffset(R.dimen.frames_video_height);;

            handleimageview.setVisibility(View.GONE);
            playpausebutton.setImageResource(R.drawable.play);
            textfetchdata.setVisibility(View.GONE);

            {
                mhashesadapter = new videoframeadapter(getActivity(), mmetricsitems, new adapteritemclick() {
                    @Override
                    public void onItemClicked(Object object) {

                    }

                    @Override
                    public void onItemClicked(Object object, int type) {

                    }
                });
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                recyview_metrices.setLayoutManager(mLayoutManager);
                recyview_metrices.setItemAnimator(new DefaultItemAnimator());
                recyview_metrices.setAdapter(mhashesadapter);
                implementscrolllistener();
            }

            {
                mmetricesadapter = new videoframeadapter(getActivity(), mhashesitems, new adapteritemclick() {
                    @Override
                    public void onItemClicked(Object object) {

                    }

                    @Override
                    public void onItemClicked(Object object, int type) {

                    }
                });
                mLayoutManager = new LinearLayoutManager(getActivity());
                recyview_hashes.setLayoutManager(mLayoutManager);
                recyview_hashes.setItemAnimator(new DefaultItemAnimator());
                recyview_hashes.setAdapter(mmetricesadapter);
            }

            frameduration=common.checkframeduration();
            keytype=common.checkkey();

            mediaseekbar.setThumb(applicationviavideocomposer.getactivity().getResources().getDrawable(
                    R.drawable.custom_thumb));

            mediaseekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                long seeked_progess;

                @Override
                public void onProgressChanged(final SeekBar seekBar, int progress, boolean fromUser)
                {
                    if(progress > 0)
                    {
                        layout_progressline.setVisibility(View.VISIBLE);
                        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);
                        p.addRule(RelativeLayout.ABOVE, seekBar.getId());
                        Rect thumbRect = mediaseekbar.getSeekBarThumb().getBounds();
                        p.setMargins(thumbRect.left,0, 0, 0);
                        Log.e("thumbleft ",""+thumbRect.left);
                        layout_progressline.setLayoutParams(p);
                        txt_mediatimethumb.setText(stringForTime(player.getCurrentPosition()));
                        txt_mediatimethumb.setVisibility(View.VISIBLE);
                        /*if(fromUser)
                        {
                            txt_mediatimethumb.setVisibility(View.VISIBLE);
                            final Animation animationFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.seekbarfadein);
                            animationFadeIn.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    txt_mediatimethumb.setVisibility(View.INVISIBLE);
                                    final Animation animationFadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.seekbarfadeout);
                                    txt_mediatimethumb.startAnimation(animationFadeOut);
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });
                            txt_mediatimethumb.startAnimation(animationFadeIn);
                        }*/
                    }
                    else
                    {
                        layout_progressline.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    if(player!=null)
                    {
                        /*player.pause();
                        playpausebutton.setImageResource(R.drawable.play);*/
                    }
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                    player.seekTo(seekBar.getProgress());
                    maxincreasevideoduration=player.getCurrentPosition();
                }
            });

            recyview_frames.post(new Runnable() {
                @Override
                public void run() {
                    adapter = new framebitmapadapter(getActivity(), mbitmaplist,recyview_frames.getWidth(),
                            new adapteritemclick() {
                                @Override
                                public void onItemClicked(Object object) {

                                }

                                @Override
                                public void onItemClicked(Object object, int type) {

                                }
                            });
                    mlinearlayoutmanager = new centerlayoutmanager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    recyview_frames.setLayoutManager(mlinearlayoutmanager);
                    recyview_frames.setItemAnimator(new DefaultItemAnimator());
                    recyview_frames.setAdapter(adapter);

                    recyview_frames.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);
                            Log.e("newstate",""+newState);
                            switch (newState) {
                                case RecyclerView.SCROLL_STATE_IDLE:
                                    System.out.println("The RecyclerView is not scrolling");
                                    disabletouchedevents();
                                    break;
                                case RecyclerView.SCROLL_STATE_DRAGGING:
                                    System.out.println("Scrolling now");
                                    if(handlerrecycler != null && runnablerecycler != null)
                                        handlerrecycler.removeCallbacks(runnablerecycler);

                                    islisttouched = true;
                                    islistdragging = true;
                                   /* RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                    relativeParams.setMargins(0, 0,0, 0);
                                    recyview_frames.setLayoutParams(relativeParams);*/
                                    break;
                                case RecyclerView.SCROLL_STATE_SETTLING:
                                    System.out.println("Scroll Settling");
                                    disabletouchedevents();
                                    break;

                            }
                        }

                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            Log.e("listtouched  dragging",""+islisttouched+" "+islistdragging);
                            if(islisttouched && islistdragging)
                            {
                                if(player != null && player.isPlaying())
                                {
                                    // player.pause();
                                    controller.setplaypauuse();
                                    Log.e("isplaying","is playing");
                                }
                                double  c=0;
                                int center = recyview_frames.getWidth()/2;
                                View centerView = recyview_frames.findChildViewUnder(center, recyview_frames.getTop());
                                int centerPos = recyview_frames.getChildAdapterPosition(centerView);
                                Log.e("centerposition",""+centerPos);
                                mlinearlayoutmanager.findLastVisibleItemPosition();

                                if(centerPos > 0)
                                    if(centerPos==1){
                                        centerPos = 0;
                                    }
                                if(mbitmaplist.size()>0) {
                                    double a=player.getDuration()/1000;
                                    double b=mbitmaplist.size()-2;
                                    Log.e("bitmap",""+(a/b));
                                    c=a/b;
                                    // a=(player.getDuration() / 1000) / (mbitmaplist.size() - 2);
                                }
                                double position=c*centerPos;
                                try {
                                    double currentduration=position*1000;

                                    Log.e("currentduration" ,"" + currentduration);


                                    if(player !=null && controller != null)
                                    {
                                        try {

                                            if(player.getDuration() <= (int)currentduration)
                                                currentduration = player.getDuration();

                                            player.seekTo((int) currentduration);
                                            Log.e("playerseekto",""+currentduration);

                                            mediaseekbar.setProgress((int) currentduration);
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
                        }
                    });
                }
            });

            flingactionmindstvac = common.getdrawerswipearea();

           /* handleimageview.setOnTouchListener(new setonTouch());
            righthandle.setOnTouchListener(new setonTouch());
            videotextureview.setOnTouchListener(new setonTouch());*/

            txtSlot1.setOnClickListener(new setonClick());
            txtSlot2.setOnClickListener(new setonClick());
            txtSlot3.setOnClickListener(new setonClick());

            img_dotmenu.setOnClickListener(new setonClick());
            img_folder.setOnClickListener(new setonClick());
            img_camera.setOnClickListener(new setonClick());
            img_arrow_back.setOnClickListener(new setonClick());
            handleimageview.setOnClickListener(new setonClick());
            videotextureview.setOnClickListener(new setonClick());
            img_delete_media.setOnClickListener(new setonClick());
            imgpause.setOnClickListener(new setonClick());
            imgpause.setVisibility(View.GONE);

            img_dotmenu.setVisibility(View.VISIBLE);
            img_folder.setVisibility(View.VISIBLE);
            img_camera.setVisibility(View.VISIBLE);
            img_arrow_back.setVisibility(View.VISIBLE);

            //tabs_detail
            txtslotmedia.setText(getResources().getString(R.string.video));
            photospinner.setOnItemSelectedListener(this);
            img_share_media.setOnClickListener(new setonClick());
            img_edit_name.setOnClickListener(new setonClick());
            img_edit_notes.setOnClickListener(new setonClick());
            img_fullscreen.setOnClickListener(new setonClick());
            playpausebutton.setOnClickListener(new setonClick());
            scrollview_detail.setVisibility(View.VISIBLE);
            tab_layout.setVisibility(View.VISIBLE);
            layout_footer.setVisibility(View.VISIBLE);
            img_fullscreen.setVisibility(View.VISIBLE);
            layout_photodetails.setVisibility(View.VISIBLE);
            layout_mediatype.setVisibility(View.VISIBLE);
            layout_date.setVisibility(View.GONE);
            layout_time.setVisibility(View.GONE);
            layout_duration.setVisibility(View.VISIBLE);
            layout_starttime.setVisibility(View.VISIBLE);
            layout_endtime.setVisibility(View.VISIBLE);

            layout_videoreader.post(new Runnable() {
                @Override
                public void run() {
                    targetheight= layout_videoreader.getHeight();
                    targetwidth = layout_videoreader.getWidth();

                    Log.e("targetheight",""+targetheight);
                }
            });

            videotextureview.post(new Runnable() {
                @Override
                public void run() {

                    previousheight = videotextureview.getHeight();
                    previouswidth = videotextureview.getWidth();
                    previouswidthpercentage = (previouswidth*20)/100;
                    Log.e("previousheight",""+previousheight);
                }
            });


            resetButtonViews(txtSlot1,txtSlot2,txtSlot3);
            txtSlot1.setVisibility(View.VISIBLE);
            txtSlot2.setVisibility(View.VISIBLE);
            txtSlot3.setVisibility(View.VISIBLE);
            txt_metrics.setVisibility(View.INVISIBLE);
            txt_hashes.setVisibility(View.INVISIBLE);
            recyview_hashes.setVisibility(View.VISIBLE);
            recyview_metrices.setVisibility(View.INVISIBLE);
            scrollview_metrices.setVisibility(View.INVISIBLE);
            scrollview_hashes.setVisibility(View.INVISIBLE);
            fragment_graphic_container.setVisibility(View.VISIBLE);

            txtslotencyption.setOnClickListener(new setonClick());
            txtslotmeta.setOnClickListener(new setonClick());
            txtslotmedia.setOnClickListener(new setonClick());
            resetButtonViews(txtslotmedia, txtslotmeta, txtslotencyption);

            if (graphicaldrawerfragment == null) {
                // fragmentgraphic = new graphicalfragment();
                graphicaldrawerfragment =new fragmentgraphicaldrawer();
                graphicaldrawerfragment.setphotocapture(true);
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.add(R.id.fragment_graphic_drawer_container, graphicaldrawerfragment);
                transaction.commit();
            }
            String[] items=common.getallfolders();
            if(items != null && items.length > 0)
            {
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, common.getallfolders());
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                photospinner.setAdapter(dataAdapter);
            }

            mediafilepath = xdata.getinstance().getSetting("selectedvideourl");

            edt_medianotes.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        v.setFocusable(false);
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(edt_medianame.getWindowToken(), 0);
                        // if (arrayvideolist.size() > 0) {
                        String medianotes = edt_medianotes.getText().toString();

                        if(!mediatransectionid.isEmpty())
                            updatemediainfo(mediatransectionid,edt_medianame.getText().toString(),medianotes,"allmedia");
                    }
                }
            });

            edt_medianame.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        edt_medianame.setKeyListener(null);
                        v.setFocusable(false);
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(edt_medianame.getWindowToken(), 0);
                        // if (arrayvideolist.size() > 0) {
                        String renamevalue = edt_medianame.getText().toString();
                        if(!mediatransectionid.isEmpty())
                            updatemediainfo(mediatransectionid,renamevalue,edt_medianotes.getText().toString(),"allmedia");

                        editabletext();
                        //   edt_medianame.setKeyListener(null);
                        //   }
                    }
                }
            });

            edt_medianame.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    if  ((actionId == EditorInfo.IME_ACTION_DONE)) {
                        /*  Log.i(TAG,"Here you can write the code");*/
                        //  if (arrayvideolist.size() > 0) {
                        String renamevalue = edt_medianame.getText().toString();
                        editabletext();
                        edt_medianame.setKeyListener(null);
                    }
                    // }
                    return false;
                }
            });



            if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_reader))
            {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getmediametadata();
                       // getframesbitmap();
                    }
                }).start();
                getmetadetareader();
            }
            else if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_composer))
            {

                Thread thread = new Thread() {
                    public void run() {
                        getmediadata();
                        //getframesbitmap();
                    }
                };
                thread.start();

                //registerbroadcastreciver();
                //registerbroadcastreciverforencryptionmetadata();
            }

            Log.e("oncreate","oncreate");

            loadmap();
            setmetriceshashesdata();
        }
        return rootview;
    }

    // Implement scroll listener
    private void implementscrolllistener() {
        recyview_metrices.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                /*visibleItemCount = mLayoutManager.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();
                if ((visibleItemCount + pastVisiblesItems) == totalItemCount) {
                    if(selectedmetrics.toString().trim().length() > 0)
                    {
                        mmetricsitems.add(new videomodel(selectedmetrics));
                        mmetricesadapter.notifyItemChanged(mmetricsitems.size()-1);
                        selectedmetrics="";
                    }
                }*/
            }
        });
    }

    public void disabletouchedevents()
    {
        if(handlerrecycler != null && runnablerecycler != null)
            handlerrecycler.removeCallbacks(runnablerecycler);

        handlerrecycler=new Handler();
        runnablerecycler = new Runnable() {
            @Override
            public void run() {
                islisttouched = false;
                islistdragging = false;
            }
        };
        handlerrecycler.postDelayed(runnablerecycler,1000);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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

                scrubberviewwidth = layout_scrubberview.getWidth();
                Thread thread = new Thread() {
                    public void run() {
                        getbitmap(scrubberviewwidth);
                        //getframesbitmap();
                    }
                };
                thread.start();
            }
        });



    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
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
        videowidth = width;
        videoheight = height;
        updatetextureviewsize((previouswidth- previouswidthpercentage),previousheight);
    }

    public class setonClick implements View.OnClickListener
    {

        @Override
        public void onClick(View view) {
            switch (view.getId())
            {
                case R.id.txt_slot1:
                    if(selectedsection != 1) {
                        selectedsection = 1;
                        scrollview_metrices.setVisibility(View.INVISIBLE);
                        scrollview_hashes.setVisibility(View.INVISIBLE);
                        fragment_graphic_container.setVisibility(View.INVISIBLE);

                        recyview_hashes.setVisibility(View.VISIBLE);
                        recyview_metrices.setVisibility(View.INVISIBLE);

                        txt_metrics.setVisibility(View.INVISIBLE);
                        txt_hashes.setVisibility(View.INVISIBLE);
                        txt_metrics.setVisibility(View.INVISIBLE);
                        resetButtonViews(txtSlot1,txtSlot2,txtSlot3);
                    }

                    break;

                case R.id.txt_slot2:
                    if(selectedsection != 2) {
                        selectedsection = 2;
                        scrollview_metrices.setVisibility(View.INVISIBLE);
                        scrollview_hashes.setVisibility(View.INVISIBLE);
                        fragment_graphic_container.setVisibility(View.INVISIBLE);
                        txt_hashes.setVisibility(View.INVISIBLE);
                        txt_metrics.setVisibility(View.INVISIBLE);
                        recyview_metrices.setVisibility(View.VISIBLE);
                        recyview_hashes.setVisibility(View.INVISIBLE);
                        resetButtonViews(txtSlot2,txtSlot1,txtSlot3);
                    }

                    break;

                case R.id.txt_slot3:
                    if(selectedsection != 3) {
                        selectedsection = 3;
                        fragment_graphic_container.setVisibility(View.VISIBLE);
                        scrollview_metrices.setVisibility(View.INVISIBLE);
                        scrollview_hashes.setVisibility(View.INVISIBLE);
                        recyview_metrices.setVisibility(View.INVISIBLE);
                        recyview_hashes.setVisibility(View.INVISIBLE);
                        txt_hashes.setVisibility(View.INVISIBLE);
                        txt_metrics.setVisibility(View.INVISIBLE);
                        resetButtonViews(txtSlot3,txtSlot1,txtSlot2);
                    }

                    break;

                case R.id.txt_slot4:
                    resetButtonViews(txtslotmedia, txtslotmeta, txtslotencyption);
                    scrollview_detail.setVisibility(View.VISIBLE);
                    scrollView_encyrption.setVisibility(View.INVISIBLE);
                    scrollview_meta.setVisibility(View.INVISIBLE);
                    break;

                case R.id.txt_slot5:
                    resetButtonViews(txtslotmeta, txtslotmedia, txtslotencyption);
                    scrollview_meta.setVisibility(View.VISIBLE);
                    scrollView_encyrption.setVisibility(View.INVISIBLE);
                    scrollview_detail.setVisibility(View.INVISIBLE);
                    break;
                case R.id.txt_slot6:
                    resetButtonViews(txtslotencyption, txtslotmedia, txtslotmeta);
                    scrollView_encyrption.setVisibility(View.VISIBLE);
                    scrollview_detail.setVisibility(View.INVISIBLE);
                    scrollview_meta.setVisibility(View.INVISIBLE);
                    break;
                case R.id.img_edit_name:
                    edt_medianame.setClickable(true);
                    edt_medianame.setEnabled(true);
                    edt_medianame.setFocusable(true);
                    edt_medianame.setFocusableInTouchMode(true);
                    edt_medianame.setSelection(edt_medianame.getText().length());
                    edt_medianame.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    break;
                case R.id.img_edit_notes:
                    edt_medianotes.setClickable(true);
                    edt_medianotes.setEnabled(true);
                    edt_medianotes.setFocusable(true);
                    edt_medianotes.setFocusableInTouchMode(true);
                    edt_medianotes.setSelection(edt_medianotes.getText().length());
                    edt_medianotes.requestFocus();
                    InputMethodManager immn = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    immn.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                    break;
                case R.id.img_share_media:
                    if (mediafilepath != null && (!mediafilepath.isEmpty()))
                        common.shareimage(getActivity(), mediafilepath);

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

                case R.id.img_delete_media:
                    showalertdialog(getActivity().getResources().getString(R.string.delete_confirm_video));
                    break;

                case R.id.img_lefthandle:
                    navigationdrawer.openDrawer(Gravity.START);
                    //handleimageview.setVisibility(View.GONE);
                    break;

                case R.id.img_arrow_back:
                    gethelper().onBack();
                    break;
                case R.id.img_fullscreen:
                    if(layout_photodetails.getVisibility()==View.VISIBLE){
                      //  navigationdrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                        navigationdrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                        handleimageview.setVisibility(View.VISIBLE);
                        expand(videotextureview,100,targetheight);
                        layout_photodetails.setVisibility(View.GONE);
                        scrollview_detail.setVisibility(View.GONE);
                        scrollview_meta.setVisibility(View.GONE);
                        scrollView_encyrption.setVisibility(View.GONE);
                        tab_layout.setVisibility(View.GONE);
                        updatetextureviewsize(targetwidth,targetheight);
                        layout_footer.setVisibility(View.GONE);
                        layout_mediatype.setVisibility(View.GONE);
                        layoutcustomcontroller.setVisibility(View.GONE);
                        playpausebutton.setVisibility(View.GONE);
                        imgpause.setVisibility(View.GONE);
                        img_fullscreen.setVisibility(View.INVISIBLE);

                    } else{
                        navigationdrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                        //navigationdrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                        handleimageview.setVisibility(View.GONE);
                        collapse(videotextureview,100,previousheight);
                        layout_photodetails.setVisibility(View.VISIBLE);
                        tab_layout.setVisibility(View.VISIBLE);
                        scrollview_detail.setVisibility(View.VISIBLE);
                        layout_footer.setVisibility(View.VISIBLE);
                        updatetextureviewsize((previouswidth- previouswidthpercentage),previousheight);
                        layoutcustomcontroller.setVisibility(View.VISIBLE);
                        layoutcustomcontroller.getResources().getColor(R.color.whitetransparent);
                        playpausebutton.setVisibility(View.VISIBLE);
                        imgpause.setVisibility(View.GONE);
                        img_fullscreen.setImageResource(R.drawable.ic_full_screen_mode);
                        resetButtonViews(txtslotmedia, txtslotmeta, txtslotencyption);
                    }
                    break;

                case R.id.videotextureview:
                    Log.e("ontouch","ontouch");

                    if(layout_photodetails.getVisibility()==View.GONE){
                        if(layout_footer.getVisibility()==(View.GONE)){
                            img_fullscreen.setVisibility(View.VISIBLE);
                            img_fullscreen.setImageResource(R.drawable.ic_info_mode);
                            layout_mediatype.setVisibility(View.VISIBLE);
                            layout_footer.setVisibility(View.VISIBLE);
                            playpausebutton.setVisibility(View.VISIBLE);
                        } else {
                            img_fullscreen.setVisibility(View.GONE);
                            img_fullscreen.setImageResource(R.drawable.ic_info_mode);
                            layout_mediatype.setVisibility(View.GONE);
                            layout_footer.setVisibility(View.GONE);
                        }

                    } else {
                        img_fullscreen.setVisibility(View.VISIBLE);
                        img_fullscreen.setImageResource(R.drawable.ic_full_screen_mode);
                        playpausebutton.setVisibility(View.VISIBLE);
                        imgpause.setVisibility(View.GONE);
                    }

                    break;

                case R.id.btn_playpause:
                    if(player.isPlaying()){
                        pause();
                    }else{
                        if(layout_footer.getVisibility()==View.VISIBLE && layout_photodetails.getVisibility()==View.GONE){
                            videotextureview.setClickable(false);
                            layout_footer.setVisibility(View.GONE);
                            playpausebutton.setVisibility(View.GONE);
                            img_fullscreen.setVisibility(View.GONE);
                            layoutcustomcontroller.setVisibility(View.VISIBLE);
                            imgpause.setVisibility(View.VISIBLE);
                        }
                        start();
                    }
                    break;

                case R.id.img_pause:
                    if(player.isPlaying()){
                        pause();
                        if(layout_footer.getVisibility()==View.GONE && layout_photodetails.getVisibility()==View.GONE){
                            videotextureview.setClickable(true);
                            playpausebutton.setImageResource(R.drawable.play_btn);
                            playpausebutton.setVisibility(View.VISIBLE);
                            img_fullscreen.setVisibility(View.VISIBLE);
                            layout_footer.setVisibility(View.VISIBLE);
                            layoutcustomcontroller.setVisibility(View.GONE);
                            imgpause.setVisibility(View.GONE);
                        }
                    }
                    break;
            }
        }
    }

    public void showalertdialog(String message){
        new AlertDialog.Builder(getActivity(),R.style.customdialogtheme)
                .setTitle("Alert!!")
                .setMessage(message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(mcontrollernavigator != null)
                            mcontrollernavigator.onItemClicked(mediafilepath,2);

                        gethelper().onBack();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
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
        /*metricItemArraylist.clear();
        metricItemArraylist.addAll(mitems);
        itemMetricAdapter.notifyDataSetChanged();*/

    }

    public class setonTouch implements View.OnTouchListener
    {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (view.getId())
            {
                /*case  R.id.handle:
                    flingswipe.onTouchEvent(motionEvent);
                    break;

                case  R.id.righthandle:
                    flingswipe.onTouchEvent(motionEvent);
                    break;*/
                case  R.id.videotextureview:
                {
                    switch (motionEvent.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            if(player != null && (! isdraweropen)) {
                                //hideshowcontroller();
                            }
                            break;
                    }
                }
                break;
            }
            return true;
        }
    }

    GestureDetector flingswipe = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener()
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
        isdraweropen=true;
        Animation rightswipe = AnimationUtils.loadAnimation(getActivity(), R.anim.right_slide);
        linearLayout.startAnimation(rightswipe);
        handleimageview.setVisibility(View.GONE);
        linearLayout.setVisibility(View.VISIBLE);
        rightswipe.start();

        // righthandle.setVisibility(View.VISIBLE);
        rightswipe.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
               // righthandle.setImageResource(R.drawable.righthandle);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
               // righthandle.setImageResource(R.drawable.lefthandle);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void swiperighttoleft()
    {
        isdraweropen=false;
        Animation leftswipe = AnimationUtils.loadAnimation(getActivity(), R.anim.left_slide);
        linearLayout.startAnimation(leftswipe);
        linearLayout.setVisibility(View.INVISIBLE);
        handleimageview.setVisibility(View.GONE);
        leftswipe.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                handleimageview.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void hideshowcontroller()
    {
        if(isdraweropen)
            return;

        if(controller != null && controller.controllersview!= null)
        {
            if(controller.controllersview.getVisibility() == View.VISIBLE)
            {
                layout_scrubberview.setVisibility(View.GONE);
                handleimageview.setVisibility(View.GONE);
                gethelper().updateactionbar(0);
                controller.controllersview.setVisibility(View.GONE);

            }
            else
            {
                layout_scrubberview.setVisibility(View.GONE);
                handleimageview.setVisibility(View.GONE);
                gethelper().updateactionbar(1);
                controller.controllersview.setVisibility(View.VISIBLE);
            }
        }
    }

    public void onRestart() {
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
        Log.e("ondestroy","ondestroy");
    }

    @Override
    public void onPause() {
        super.onPause();
        progressdialog.dismisswaitdialog();
        Log.e("onpause","onpause");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("onresume","onresume");
        try {
            if(! issurafcedestroyed)
                return;

            player = new MediaPlayer();
            if(controller != null)
                controller.removeAllViews();

            controller = new videocontrollerview(getActivity(),mitemclick,true);
            Uri uri= FileProvider.getUriForFile(applicationviavideocomposer.getactivity(),
                    BuildConfig.APPLICATION_ID + ".provider", new File(mediafilepath));
            selectedvideouri=uri;
            if(mediafilepath != null && (! mediafilepath.isEmpty()) && selectedvideouri !=null)
            {
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.setDataSource(applicationviavideocomposer.getactivity(),selectedvideouri);
                player.setSurface(surfacetexture);
                player.prepareAsync();
                player.setOnPreparedListener(new setonmediaprepared());
                player.setOnCompletionListener(new setonmediacompletion());

                if(player!=null){
                    //changeactionbarcolor();
                   // initAudio();
                    //fragmentgraphic.setvisualizerwave();
                    wavevisualizerslist.clear();
                    setaudiodata();
                }

                if(! keytype.equalsIgnoreCase(common.checkkey()) || (frameduration != common.checkframeduration()))
                {
                    frameduration=common.checkframeduration();
                    keytype=common.checkkey();
                    mvideoframes.clear();
                    mainvideoframes.clear();
                    mallframes.clear();
                }
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

    @Override
    public void onStart() {
        super.onStart();
        /*IntentFilter intentFilter = new IntentFilter(config.reader_service_getmetadata);
        coredatabroadcastreceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Thread thread = new Thread() {
                    public void run() {
                        if(mhashesitems.size() == 0)
                            getmediametadata();
                    }
                };
                thread.start();
            }
        };
        getActivity().registerReceiver(coredatabroadcastreceiver, intentFilter);*/
    }

    public void getmediadata() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    databasemanager mdbhelper = null;
                    if (mdbhelper == null) {
                        mdbhelper = new databasemanager(applicationviavideocomposer.getactivity());
                        mdbhelper.createDatabase();
                    }

                    try {
                        mdbhelper.open();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Cursor cur = mdbhelper.getstartmediainfo(common.getfilename(mediafilepath));
                    String completedate="";
                    if (cur != null && cur.getCount() > 0 && cur.moveToFirst())
                    {
                        do{
                            completedate = "" + cur.getString(cur.getColumnIndex("videocompletedevicedate"));
                            mediadate = "" + cur.getString(cur.getColumnIndex("videostartdevicedate"));
                            medianame = "" + cur.getString(cur.getColumnIndex("media_name"));
                            medianotes =  "" + cur.getString(cur.getColumnIndex("media_notes"));
                            mediafolder =  "" + cur.getString(cur.getColumnIndex("media_folder"));
                            mediatransectionid = "" + cur.getString(cur.getColumnIndex("videostarttransactionid"));


                        }while(cur.moveToNext());
                    }

                    if (!completedate.isEmpty()){

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textfetchdata.setVisibility(View.GONE);
                            }
                        });

                        String framelabel="";
                        ArrayList<metadatahash> mitemlist=mdbhelper.getmediametadatabyfilename(common.getfilename(mediafilepath));
                        if(metricmainarraylist.size()>0){

                            for(int i=0;i<mitemlist.size();i++)
                            {
                                String sequencehash = mitemlist.get(i).getSequencehash();
                                String hashmethod = mitemlist.get(i).getHashmethod();
                                String videostarttransactionid = mitemlist.get(i).getVideostarttransactionid();
                                String serverdictionaryhash = mitemlist.get(i).getValuehash();
                                metricmainarraylist.set(i,new arraycontainer(hashmethod,videostarttransactionid,sequencehash,serverdictionaryhash));
                            }

                        }else{

                            for(int i=0;i<mitemlist.size();i++)
                            {
                                String metricdata=mitemlist.get(i).getMetricdata();
                                String sequencehash = mitemlist.get(i).getSequencehash();
                                String hashmethod = mitemlist.get(i).getHashmethod();
                                String videostarttransactionid = mitemlist.get(i).getVideostarttransactionid();
                                String serverdictionaryhash = mitemlist.get(i).getValuehash();

                                parsemetadata(metricdata,hashmethod,videostarttransactionid,sequencehash,serverdictionaryhash);
                                // selectedhaeshes = selectedhaeshes+"\n";
                                framelabel="Frame ";
                                if(i == mitemlist.size()-1)
                                {
                                    framelabel="Last Frame ";
                                }
                                /*selectedhaeshes = selectedhaeshes+framelabel+mitemlist.get(i).getSequenceno()+" "+mitemlist.get(i).getHashmethod()+
                                        ": "+mitemlist.get(i).getSequencehash();*/
                            }


                           // txt_duration.setText("00:00:02:17.3");


                            if((!mediadate.isEmpty()&& mediadate != null) && (!completedate.isEmpty() && completedate!= null)){

                                DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH);
                                final Date startdate = format.parse(mediadate);
                                Date enddate = format.parse(completedate);
                                final String filecreateddate = new SimpleDateFormat("yyyy-MM-dd").format(startdate);
                                final String createdtime = new SimpleDateFormat("hh:mm:ss aa").format(startdate);
                                SimpleDateFormat spf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
                                final String starttime = spf.format(startdate);
                                Log.e("starttime",starttime);
                                final String endtime = spf.format(enddate);

                                applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        txt_starttime.setText(starttime);
                                        txt_endtime.setText(endtime);
                                        tvtime.setText(starttime);
                                        txt_createdtime.setText(createdtime);
                                        txt_title_actionbarcomposer.setText(filecreateddate);
                                    }
                                });
                            }
                            if(!medianame.isEmpty()){
                                int index =  medianame.lastIndexOf('.');
                                if(index >=0)
                                    medianame = medianame.substring(0, medianame.lastIndexOf('.'));


                                applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        edt_medianame.setText(medianame);
                                    }
                                });
                            }

                            if(!medianotes.isEmpty()){
                                applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        edt_medianotes.setText(medianotes);
                                    }
                                });
                            }
                        }
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
        if(mediafilepath != null && (! mediafilepath.isEmpty())) {
            File file = new File(mediafilepath);
            if (file.exists()) {
                databasemanager mdbhelper = null;
                if (mdbhelper == null) {
                    mdbhelper = new databasemanager(applicationviavideocomposer.getactivity());
                    mdbhelper.createDatabase();
                }

                try {
                    mdbhelper.open();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    Cursor mediainfocursor = mdbhelper.getmediainfobyfilename(common.getfilename(mediafilepath));
                    String videoid = "", videotoken = "",audiostatus ="";
                    if (mediainfocursor != null && mediainfocursor.getCount() > 0) {
                        if (mediainfocursor.moveToFirst()) {
                            do {
                                audiostatus = "" + mediainfocursor.getString(mediainfocursor.getColumnIndex("status"));
                                videoid = "" + mediainfocursor.getString(mediainfocursor.getColumnIndex("videoid"));
                            } while (mediainfocursor.moveToNext());
                        }
                    }

                    if(audiostatus.equalsIgnoreCase("complete") && metricmainarraylist.size() == 0){
                        if(! videoid.trim().isEmpty())
                        {
                            Cursor metadatacursor = mdbhelper.readallmetabyvideoid(videoid);
                            if (metadatacursor != null && metadatacursor.getCount() > 0) {
                                if (metadatacursor.moveToFirst()) {
                                    do {
                                        String sequencehash = "" + metadatacursor.getString(metadatacursor.getColumnIndex("sequencehash"));
                                        String sequenceno = "" + metadatacursor.getString(metadatacursor.getColumnIndex("sequenceno"));
                                        String hashmethod = "" + metadatacursor.getString(metadatacursor.getColumnIndex("hashmethod"));
                                        String metricdata = "" + metadatacursor.getString(metadatacursor.getColumnIndex("metricdata"));
                                        String videostarttransactionid = "" + metadatacursor.getString(metadatacursor.getColumnIndex("videostarttransactionid"));
                                        String metahash = "" + metadatacursor.getString(metadatacursor.getColumnIndex("metahash"));
                                        //selectedhashes=selectedhashes+"\n"+"Frame "+hashmethod+" "+sequenceno+": "+videoframehashvalue;

                                        try {
                                            ArrayList<metricmodel> metricItemArraylist = new ArrayList<>();
                                            JSONObject object=new JSONObject(metricdata);
                                            Iterator<String> myIter = object.keys();
                                            while (myIter.hasNext()) {
                                                String key = myIter.next();
                                                String value = object.optString(key);
                                                metricmodel model=new metricmodel();
                                                model.setMetricTrackKeyName(key);
                                                model.setMetricTrackValue(value);
                                                metricItemArraylist.add(model);
                                            }

                                            metricmainarraylist.add(new arraycontainer(metricItemArraylist,hashmethod,videostarttransactionid,sequencehash,metahash));
                                        }catch (Exception e)
                                        {
                                            e.printStackTrace();
                                        }
                                        if(mhashesitems.size() == (metadatacursor.getCount()-1))
                                        {
                                            mhashesitems.add(new videomodel("Last Frame "+hashmethod+" "+sequenceno+": "+sequencehash));
                                        }
                                        else
                                        {
                                            mhashesitems.add(new videomodel("Frame "+hashmethod+" "+sequenceno+": "+sequencehash));
                                        }

                                    } while (metadatacursor.moveToNext());
                                }
                            }
                        }
                    }


                    try {
                        mdbhelper.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }catch (Exception e)
                {
                    e.printStackTrace();
                }
                applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if(mhashesitems.size() !=0){
                            textfetchdata.setVisibility(View.GONE);
                            mhashesadapter.notifyItemChanged(mhashesitems.size()-1);
                            selectedhashes ="";
                        }

                    }
                });
            }
        }
    }

    public void parsemetadata(String metadata,String hashmethod,String videostarttransactionid,String hashvalue,String metahash) {
        try {

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
                metricmainarraylist.add(new arraycontainer(metricItemArraylist,hashmethod,videostarttransactionid,hashvalue,metahash));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerbroadcastreciver()
    {
        IntentFilter intentFilter = new IntentFilter(config.composer_service_savemetadata);
        getmetadatabroadcastreceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Thread thread = new Thread() {
                    public void run() {
                        if(mhashesitems.size() == 0)
                            getmediadata();
                    }
                };
                thread.start();
            }
        };
        getActivity().registerReceiver(getmetadatabroadcastreceiver, intentFilter);
    }

    public void registerbroadcastreciverforencryptionmetadata()
    {
        IntentFilter intentFilter = new IntentFilter(config.composer_service_getencryptionmetadata);
        getencryptionmetadatabroadcastreceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                getmediadata();
            }
        };
        getActivity().registerReceiver(getencryptionmetadatabroadcastreceiver, intentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            applicationviavideocomposer.getactivity().unregisterReceiver(coredatabroadcastreceiver);
            applicationviavideocomposer.getactivity().unregisterReceiver(getmetadatabroadcastreceiver);
            applicationviavideocomposer.getactivity().unregisterReceiver(getencryptionmetadatabroadcastreceiver);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        Log.e("onstop","onstop");
    }

    public class setonSurface implements SurfaceHolder.Callback
    {
        // Implement SurfaceHolder.Callback
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (player != null)
                player.setDisplay(holder);

            issurafcedestroyed=false;
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (player != null) {
                playerposition=player.getCurrentPosition();
                player.pause();
            }
            issurafcedestroyed=true;
        }
    }

    public class setonmediaprepared implements MediaPlayer.OnPreparedListener
    {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            isvideocompleted=false;
            maxincreasevideoduration=0;
            controller.setMediaPlayer(new setonmediacontroller());
            controller.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));
            videoduration=mediaPlayer.getDuration();
            txt_duration.setText( common.gettimestring(mediaPlayer.getDuration()) );

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

            //controller.show();

            /*if(fragmentgraphic != null)
                fragmentgraphic.setmediaplayer(true,null);*/

            Log.e("onprepared","onprepared");
        }
    }

    adapteritemclick mitemclick=new adapteritemclick() {
        @Override
        public void onItemClicked(Object object) {
           // hideshowcontroller();
        }

        @Override
        public void onItemClicked(Object object, int type) {

        }
    };

    public class setonmediacontroller implements videocontrollerview.MediaPlayerControl
    {

        // Implement VideoMediaController.MediaPlayerControl
        @Override
        public boolean canPause() {
            return true;
        }

        @Override
        public boolean canSeekBackward() {
            return true;
        }

        @Override
        public boolean canSeekForward() {
            return true;
        }

        @Override
        public int getBufferPercentage() {
            return 0;
        }

        @Override
        public int getCurrentPosition() {
            try {
                if(player != null)
                {
                    if(mbitmaplist.size() > 0 && (! islisttouched))
                    {
                        //setmargin();
                    }
                    if(player.getCurrentPosition() > maxincreasevideoduration)
                        maxincreasevideoduration=player.getCurrentPosition();

                    if(currentvideoduration == 0 || (player.getCurrentPosition() > currentvideoduration))
                    {
                        currentvideoduration=player.getCurrentPosition();  // suppose its on 4th pos means 4000
                        currentvideodurationseconds=currentvideoduration/1000;  // Its 4
                    }
                    return player.getCurrentPosition();
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }
            return 0;
        }

        @Override
        public int getDuration() {
            if(player != null)
                return player.getDuration();
            return 0;
        }

        @Override
        public boolean isPlaying() {
            if(player != null)
                return player.isPlaying();

            return false;
        }

        @Override
        public void pause() {
            if(player != null)
                player.pause();
        }

        @Override
        public void seekTo(int i) {
            if(player != null)
            {
                player.seekTo(i);
                //setmargin();
            }
        }

        @Override
        public void start() {
            if(player != null)
            {
                player.start();
                player.setOnCompletionListener(new setonmediacompletion());
            }
        }

        @Override
        public boolean isFullScreen() {
            return false;
        }

        @Override
        public void toggleFullScreen() {}
        public String getkeyvalue(byte[] data)
        {
            String value="";
            String salt="";

            switch (keytype)
            {
                case config.prefs_md5:
                    value= md5.calculatebytemd5(data);
                    break;

                case config.prefs_md5_salt:
                    salt= xdata.getinstance().getSetting(config.prefs_md5_salt);
                    if(! salt.trim().isEmpty())
                    {
                        byte[] saltbytes=salt.getBytes();
                        try {
                            ByteArrayOutputStream outputstream = new ByteArrayOutputStream( );
                            outputstream.write(saltbytes);
                            outputstream.write(data);
                            byte updatedarray[] = outputstream.toByteArray();
                            value= md5.calculatebytemd5(updatedarray);
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        value= md5.calculatebytemd5(data);
                    }

                    break;
                case config.prefs_sha:
                    value= sha.sha1(data);
                    break;
                case config.prefs_sha_salt:
                    salt= xdata.getinstance().getSetting(config.prefs_sha_salt);
                    if(! salt.trim().isEmpty())
                    {
                        byte[] saltbytes=salt.getBytes();
                        try {
                            ByteArrayOutputStream outputstream = new ByteArrayOutputStream( );
                            outputstream.write(saltbytes);
                            outputstream.write(data);
                            byte updatedarray[] = outputstream.toByteArray();
                            value= sha.sha1(updatedarray);
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        value= sha.sha1(data);
                    }
                    break;
            }
            return value;
        }
    }

    @Override
    public void onHeaderBtnClick(int btnid) {
        super.onHeaderBtnClick(btnid);
        switch (btnid){
            case R.id.img_share_icon:
                if(mediafilepath != null && (! mediafilepath.isEmpty()))
                    common.sharevideo(getActivity(), mediafilepath);
                break;
            case R.id.img_menu:
                gethelper().onBack();
                break;
            case R.id.img_setting:
              //  destroyvideoplayer();
                framemetricssettings fragmatriclist=new framemetricssettings();
                gethelper().replaceFragment(fragmatriclist, false, true);
                break;
        }
    }

    public void getframesbitmap()
    {

        mbitmaplist.add(new frame(0,null,true));

        MediaMetadataRetriever m_mediaMetadataRetriever = new MediaMetadataRetriever();
        Uri uri= FileProvider.getUriForFile(applicationviavideocomposer.getactivity(),
                BuildConfig.APPLICATION_ID + ".provider", new File(mediafilepath));
        m_mediaMetadataRetriever.setDataSource(applicationviavideocomposer.getactivity(),uri);
        String time = m_mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

        long timeInmillisec=0;
        if(time != null)
            timeInmillisec = Long.parseLong( time );

        long duration = timeInmillisec / 1000;
        long hours = duration / 3600;
        long minutes = (duration - hours * 3600) / 60;
        long seconds = duration - (hours * 3600 + minutes * 60);
        long n;

        if(minutes!=0)
            minutes = minutes*60;

        seconds = seconds + minutes;
        Log.e("videoseconds  =  ",""+seconds);
        if(seconds>60){
            n=30;
        }else if(seconds==1){
            n=1;
        }else if(seconds==2){
            n=2;
        } else{
            n=seconds/2;
        }

        Bitmap image = BitmapFactory.decodeResource(applicationviavideocomposer.getactivity().getResources(), R.drawable.imagebitmap);

        for(int j=1;j<=n;j++){

           // Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
           // Bitmap bmp = Bitmap.createBitmap(image,100, 100, false);
            Bitmap bitmap = Bitmap.createScaledBitmap(image, 100, 100, false);
            mbitmaplist.add(new frame(j,bitmap,false));

        }

        runmethod = true;
        if(mbitmaplist.size()!=0)
            mbitmaplist.add(new frame(0,null,true));


        for(int i=1;i<=n;i++)
        {
            Bitmap m_bitmap = null;
            try
            {
                m_mediaMetadataRetriever = new MediaMetadataRetriever();
                m_mediaMetadataRetriever.setDataSource(mediafilepath);
                m_bitmap = m_mediaMetadataRetriever.getFrameAtTime(i * 1000000);


                if(m_bitmap != null && runmethod)
                {
                    Log.e("Bitmap on ",""+i);
                    Bitmap bitmap = Bitmap.createScaledBitmap(m_bitmap, 100, 100, false);
                    /*mbitmaplist.add(new frame(i,bitmap,false));
                    mbitmaplist.set(i,bitmap);*/

                    mbitmaplist.set(i,new frame(i,bitmap,false));

                    applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(adapter != null){
                                adapter.notifyDataSetChanged();
                                scurraberverticalbar.setVisibility(View.GONE);
                                //runmethod = true;
                            }

                        }
                    });
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                if (m_mediaMetadataRetriever != null)
                    m_mediaMetadataRetriever.release();
            }
        }
    }

    public void setupVideoPlayer(final Uri selecteduri)
    {
        try {
            player = new MediaPlayer();
            if(controller != null)
                controller.removeAllViews();

            controller = new videocontrollerview(applicationviavideocomposer.getactivity(),mitemclick,true);

            if(selecteduri!=null){
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.setDataSource(applicationviavideocomposer.getactivity(),selecteduri);
                player.setSurface(surfacetexture);
                player.prepareAsync();
                player.setOnPreparedListener(new setonmediaprepared());
                player.setOnCompletionListener(new setonmediacompletion());
                player.setOnVideoSizeChangedListener(this);
                player.setOnBufferingUpdateListener(this);
                if(player!=null)
                {
                    setaudiodata();
                   // changeactionbarcolor();
                   // initAudio();
                   // fragmentgraphic.setvisualizerwave();
                    wavevisualizerslist.clear();

                }
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

    public void  changeactionbarcolor(){
        gethelper().updateactionbar(1, applicationviavideocomposer.getactivity().getResources().getColor
                (R.color.videoPlayer_header));
    }


    public void setmetriceshashesdata()
    {
        if(myHandler != null && myRunnable != null)
            myHandler.removeCallbacks(myRunnable);

        myHandler=new Handler();
        myRunnable = new Runnable() {
            @Override
            public void run() {
                boolean graphicopen=false;
                if(!isdraweropen)
                {
                    /*if((recyview_hashes.getVisibility() == View.VISIBLE) && (! selectedhashes.trim().isEmpty()))
                    {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mhashesitems.add(new videomodel(selectedhashes));
                                mhashesadapter.notifyItemChanged(mhashesitems.size()-1);
                                selectedhashes ="";
                            }
                        });

                    }
*/
                  if(arraycontainerformetric != null){
                      graphicaldrawerfragment.getencryptiondata(arraycontainerformetric.getHashmethod(),arraycontainerformetric.getVideostarttransactionid(),
                              arraycontainerformetric.getValuehash(),arraycontainerformetric.getMetahash());

                      common.setspannable(getResources().getString(R.string.blockchain_id)," "+ arraycontainerformetric.getVideostarttransactionid(), txt_blockchainid);
                      common.setspannable(getResources().getString(R.string.block_id)," "+ arraycontainerformetric.getHashmethod(), txt_blockid);
                      common.setspannable(getResources().getString(R.string.block_number)," "+ arraycontainerformetric.getValuehash(), txt_blocknumber);
                      common.setspannable(getResources().getString(R.string.metrichash)," "+arraycontainerformetric.getMetahash(), txt_metahash);

                      double latt=0,longg=0;

                      ArrayList<metricmodel> metricItemArraylist = arraycontainerformetric.getMetricItemArraylist();

                      for(int j=0;j<metricItemArraylist.size();j++)
                      {
                          /*selectedmetrics=selectedmetrics+"\n"+metricItemArraylist.get(j).getMetricTrackKeyName()+" - "+
                                  metricItemArraylist.get(j).getMetricTrackValue();*/
                          common.setgraphicalitems(metricItemArraylist.get(j).getMetricTrackKeyName(),
                                  metricItemArraylist.get(j).getMetricTrackValue(),true);

                          setmetadatavalue(metricItemArraylist.get(j));

                          if(graphicaldrawerfragment != null)
                          {
                              if (metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("gpslatitude"))
                              {

                                  if(! metricItemArraylist.get(j).getMetricTrackValue().equalsIgnoreCase("NA"))
                                  {
                                      latt=Double.parseDouble(metricItemArraylist.get(j).getMetricTrackValue());
                                      if(longg != 0)
                                      {
                                          if(graphicaldrawerfragment != null)
                                          {
                                              graphicaldrawerfragment.drawmappoints(new LatLng(latt,longg));
                                              latt=0;longg=0;
                                          }
                                      }
                                  }
                              }
                              if (metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("gpslongitude"))
                              {
                                  if(! metricItemArraylist.get(j).getMetricTrackValue().equalsIgnoreCase("NA"))
                                  {
                                      longg=Double.parseDouble(metricItemArraylist.get(j).getMetricTrackValue());
                                      if(latt != 0)
                                      {
                                          if(graphicaldrawerfragment != null)
                                          {
                                              graphicaldrawerfragment.drawmappoints(new LatLng(latt,longg));
                                              latt=0;longg=0;
                                          }
                                      }
                                  }
                              }

                          }
                      }
                  }

                  setmetricesgraphicaldata();

                    if((fragment_graphic_container.getVisibility() == View.VISIBLE))
                        graphicopen=true;
                }

               /* if(graphicaldrawerfragment != null)
                    graphicaldrawerfragment.setdrawerproperty(graphicopen);
*/
                myHandler.postDelayed(this, 3000);
            }
        };
        myHandler.post(myRunnable);
    }

    public void setmetricesgraphicaldata()
    {
        long currentduration=maxincreasevideoduration;
        //currentduration=currentduration/1000;
        long percentage = 0;
        int totalframes = metricmainarraylist.size();
        int count = 0;

        if(videoduration>0 && currentduration>0)
               percentage = (currentduration*100)/videoduration ;

        if(percentage>0)
            count =(int) (totalframes*percentage)/100;

        if(metricmainarraylist.size() == 0 || currentduration == 0)
            return;

        currentduration=(currentduration*30);
        currentduration=currentduration/frameduration;

        int n=(int)currentduration;

        if(position > metricmainarraylist.size() || isvideocompleted)
             position =metricmainarraylist.size();

        Log.e("Current duration ",""+n+" "+currentduration+" "+position);

        for(;position < count ;position++)
        {
            if(! metricmainarraylist.get(position).isIsupdated())
            {

                arraycontainerformetric = new arraycontainer();
                arraycontainerformetric = metricmainarraylist.get(position);
                metricmainarraylist.get(position).setIsupdated(true);

                /*if((! selectedmetrics.toString().trim().isEmpty()))
                {
                    mmetricsitems.add(new videomodel(selectedmetrics));
                    mmetricesadapter.notifyItemChanged(mmetricsitems.size()-1);
                    selectedmetrics="";
                }*/
            }else{
                arraycontainerformetric = null;
            }
        }

        if(((! latitude.trim().isEmpty()) && (! latitude.equalsIgnoreCase("NA"))) &&
                (! longitude.trim().isEmpty()) && (! longitude.equalsIgnoreCase("NA")))
            populateUserCurrentLocation(new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude)));


        if(fragment_graphic_container .getVisibility() == View.VISIBLE)
        {
            if(graphicaldrawerfragment != null){
                graphicaldrawerfragment.setmetricesdata();
                //graphicaldrawerfragment.getvisualizerwavecomposer(wavevisualizerslist);
            }
        }
    }

    public class setonmediacompletion implements MediaPlayer.OnCompletionListener
    {

        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            isvideocompleted=true;
            controller.setplaypauuse();
            currentvideoduration = videoduration;
            currentvideodurationseconds = currentvideoduration / 1000;
            maxincreasevideoduration=videoduration;
            playpausebutton.setImageResource(R.drawable.play_btn);
            playpausebutton.setVisibility(View.VISIBLE);
            mediaseekbar.setProgress(player.getCurrentPosition());

            if(layout_footer.getVisibility()==View.GONE){
                videotextureview.setClickable(true);
                layout_mediatype.setVisibility(View.VISIBLE);
                playpausebutton.setImageResource(R.drawable.play_btn);
                playpausebutton.setVisibility(View.VISIBLE);
                img_fullscreen.setVisibility(View.VISIBLE);
                layout_footer.setVisibility(View.VISIBLE);
                layoutcustomcontroller.setVisibility(View.GONE);
                imgpause.setVisibility(View.GONE);
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(player != null && controller!= null)
                    {
                        player.seekTo(0);
                        wavevisualizerslist.clear();
                        controller.setProgress(0,true);
                        playpausebutton.setImageResource(R.drawable.play);

                       /* RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        relativeParams.setMargins(0, 0,0, 0);
                        recyview_frames.setLayoutParams(relativeParams);*/
                    }
                }
            },200);
        }
    }

    public void setmargin(){
        if(mbitmaplist.size()>0 && player.getCurrentPosition()>0){
                double totalduration=player.getDuration()/1000;
                double totalbitmapframe=mbitmaplist.size()-2;
                double c=totalduration/totalbitmapframe;
                double leftmargin= 100/c;
                double currentpostion=(player.getCurrentPosition()/1000);
                double leftsidemargin=-((leftmargin)*(currentpostion));
                /*RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                relativeParams.setMargins((int)(leftsidemargin) , 0,0, 0);
                recyview_frames.requestLayout();
                recyview_frames.setLayoutParams(relativeParams);
                recyview_frames.scrollToPosition(0);*/

        }
    }


    private void initAudio() {

        if(player != null){

            try {
                applicationviavideocomposer.getactivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
                setupVisualizerFxAndUI();
                // Make sure the visualizer is enabled only when you actually want to
                // receive data, and
                // when it makes sense to receive data.
                mVisualizer.setEnabled(true);
                // When the stream ends, we don't need to collect any more data. We
                // don't do this in
                // setupVisualizerFxAndUI because we likely want to have more,
                // non-Visualizer related code
                // in this callback.
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        //mVisualizer.setEnabled(false);
                    }
                });
                //mMediaPlayer.start();
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }
    }

    private void setupVisualizerFxAndUI() {

        // Create the Visualizer object and attach it to our media player.
        int sessionid =  player.getAudioSessionId();

        Equalizer mEqualizer = new Equalizer(0, sessionid);
        mEqualizer.setEnabled(true); // need to enable equalizer

        mVisualizer = new Visualizer(sessionid);

        if(mVisualizer != null && Visualizer.getCaptureSizeRange() != null && Visualizer.getCaptureSizeRange().length > 0)
        {
            try {
                mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
                mVisualizer.setDataCaptureListener(
                        new Visualizer.OnDataCaptureListener() {
                            public void onWaveFormDataCapture(Visualizer visualizer,
                                                              byte[] bytes, int samplingRate) {

                                double rms = 0;
                                int[] formattedVizData = getFormattedData(bytes);
                                if (formattedVizData.length > 0) {
                                    for (int i = 0; i < formattedVizData.length; i++) {
                                        int val = formattedVizData[i];
                                        rms += val * val;
                                    }
                                    rms = Math.sqrt(rms / formattedVizData.length);
                                }

                                int amplitude = (int)rms;

                                if(player != null && player.isPlaying()){

                                    if(player.getCurrentPosition()==0){
                                        //fragmentgraphic.setvisualizerwave();
                                        wavevisualizerslist.clear();
                                    }

                                    if(amplitude >=  35)
                                        amplitude = amplitude*2;

                                    int x= amplitude * 100;

                                    wavevisualizerslist.add(new wavevisualizer(x,true));
                                }

                            }

                            public void onFftDataCapture(Visualizer visualizer,
                                                         byte[] bytes, int samplingRate) {
                            }

                        }, Visualizer.getMaxCaptureRate() / 2, true, false);
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }
    }

    public int[] getFormattedData(byte[] rawVizData) {
        int[] arraydata=new int[rawVizData.length];
        for (int i = 0; i < arraydata.length; i++) {
            // convert from unsigned 8 bit to signed 16 bit
            int tmp = ((int) rawVizData[i] & 0xFF) - 128;
            arraydata[i] = tmp;
        }
        return arraydata;
    }

    public void setupvideodata() {

        tvsize.setText(common.filesize(mediafilepath));
        playpausebutton.setImageResource(R.drawable.play);
        position = 0;
        if (mediafilepath.equalsIgnoreCase("blank")) {
            //frontview.setVisibility(View.VISIBLE);
        } else {
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

                frameduration = common.checkframeduration();
                keytype = common.checkkey();

                if (mbitmaplist.size() != 0)
                    runmethod = false;

                scurraberverticalbar.setVisibility(View.GONE);
                Uri uri= FileProvider.getUriForFile(applicationviavideocomposer.getactivity(),
                        BuildConfig.APPLICATION_ID + ".provider", new File(mediafilepath));
                setupVideoPlayer(uri);
                videoduration = 0;
                playerposition = 0;
               // righthandle.setVisibility(View.GONE);
                framecount = 0;
                if (mediafilepath != null && (!mediafilepath.isEmpty())) {
                    mvideoframes.clear();
                    mainvideoframes.clear();
                    mallframes.clear();
                    txt_metrics.setText("");
                    txt_hashes.setText("");
                }
            }
        }
    }
    public void getmetadetareader(){
        myHandler=new Handler();
        myRunnable = new Runnable() {
            @Override
            public void run() {

                getmediametadata();

                myHandler.postDelayed(this, 5000);
            }
        };
        myHandler.post(myRunnable);
    }

    public void expand(final View v, int duration, int targetHeight) {

        int prevHeight  = v.getHeight();

        v.setVisibility(View.VISIBLE);
        ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().height = (int) animation.getAnimatedValue();
                v.requestLayout();
            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

    public void collapse(final View v, int duration, int targetHeight) {

        int prevHeight  = v.getHeight();
        ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().height = (int) animation.getAnimatedValue();
                v.requestLayout();
            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

    public void setmetadatavalue(metricmodel metricItemArraylist){

        if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase(config.gpslatitude)){
            latitude = metricItemArraylist.getMetricTrackValue();
            common.setspannable(getResources().getString(R.string.latitude),"\n"+metricItemArraylist.getMetricTrackValue(), tvlatitude);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase(config.gpslongitude)){
            longitude = metricItemArraylist.getMetricTrackValue();
            common.setspannable(getResources().getString(R.string.longitude),"\n"+metricItemArraylist.getMetricTrackValue(), tvlongitude);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase(config.gpsaltitude)){
            common.setspannable(getResources().getString(R.string.altitude),"\n"+metricItemArraylist.getMetricTrackValue(), tvaltitude);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase(config.speed)){
            common.setspannable(getResources().getString(R.string.speed),"\n"+metricItemArraylist.getMetricTrackValue(), tvspeed);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase((config.heading))){
            common.setspannable(getResources().getString(R.string.heading),"\n"+metricItemArraylist.getMetricTrackValue(), tvheading);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase((config.distancetravelled))){
            common.setspannable(getResources().getString(R.string.traveled),"\n"+metricItemArraylist.getMetricTrackValue(), tvtraveled);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase((config.address))){
            common.setspannable("",metricItemArraylist.getMetricTrackValue(), tvaddress);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase(config.acceleration_x)){
            common.setspannable(getResources().getString(R.string.xaxis),"\n"+metricItemArraylist.getMetricTrackValue(), tvxaxis);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase(config.acceleration_y)){
            common.setspannable(getResources().getString(R.string.yaxis),"\n"+metricItemArraylist.getMetricTrackValue(), tvyaxis);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase(config.acceleration_z)){
            common.setspannable(getResources().getString(R.string.zaxis),"\n"+metricItemArraylist.getMetricTrackValue(), tvzaxis);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("phonetype")){
            common.setspannable(getResources().getString(R.string.phone),"\n"+metricItemArraylist.getMetricTrackValue(), tvphone);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("carrier")){
            common.setspannable(getResources().getString(R.string.network),"\n"+metricItemArraylist.getMetricTrackValue(), tvnetwork);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("connectionspeed")){
            common.setspannable(getResources().getString(R.string.connection),"\n"+metricItemArraylist.getMetricTrackValue(), tvconnection);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("osversion")){
            common.setspannable(getResources().getString(R.string.version),"\n"+metricItemArraylist.getMetricTrackValue(), tvversion);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("wifiname")){
            common.setspannable(getResources().getString(R.string.wifi),"\n"+metricItemArraylist.getMetricTrackValue(), tvwifi);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("gpsaccuracy")){
            common.setspannable(getResources().getString(R.string.gpsaccuracy),"\n"+metricItemArraylist.getMetricTrackValue(), tvgpsaccuracy);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("screenwidth")){
            screenwidth = metricItemArraylist.getMetricTrackValue();
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("screenheight")){
            screenheight = metricItemArraylist.getMetricTrackValue();
            common.setspannable(getResources().getString(R.string.screen),"\n"+screenwidth+"x"+screenheight, tvscreen);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("country")){
            common.setspannable(getResources().getString(R.string.country),"\n"+metricItemArraylist.getMetricTrackValue(), tvcountry);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("cpuusagesystem")){
            common.setspannable(getResources().getString(R.string.cpuusage),"\n"+metricItemArraylist.getMetricTrackValue(), tvcpuusage);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("brightness")){
            common.setspannable(getResources().getString(R.string.brightness),"\n"+metricItemArraylist.getMetricTrackValue(), tvbrightness);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("timezone")){
            common.setspannable(getResources().getString(R.string.timezone),"\n"+metricItemArraylist.getMetricTrackValue(), tvtimezone);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("memoryusage")){
            common.setspannable(getResources().getString(R.string.memoryusage),"\n"+metricItemArraylist.getMetricTrackValue(), tvmemoryusage);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("bluetoothonoff")){
            common.setspannable(getResources().getString(R.string.bluetooth),"\n"+metricItemArraylist.getMetricTrackValue(), tvbluetooth);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("devicetime")){
            common.setspannable(getResources().getString(R.string.localtime),"\n"+metricItemArraylist.getMetricTrackValue(), tvlocaltime);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("freespace")){
            common.setspannable(getResources().getString(R.string.storagefree),"\n"+metricItemArraylist.getMetricTrackValue(), tvstoragefree);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("devicelanguage")){
            common.setspannable(getResources().getString(R.string.language),"\n"+metricItemArraylist.getMetricTrackValue(), tvlanguage);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("systemuptime")){
            common.setspannable(getResources().getString(R.string.uptime),"\n"+metricItemArraylist.getMetricTrackValue(), tvuptime);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("battery")){
            common.setspannable(getResources().getString(R.string.battery),"\n"+metricItemArraylist.getMetricTrackValue(), tvbattery);
        }else if(metricItemArraylist.getMetricTrackKeyName().equalsIgnoreCase("deviceorientation")){

            String strdegree=xdata.getinstance().getSetting(config.orientation);
            if(! strdegree.equals(lastsavedangle))
            {
                if(strdegree.equalsIgnoreCase("NA"))
                    strdegree="0.0";

                int degree = Math.abs((int)Double.parseDouble(strdegree));
                rotatecompass(degree);
            }
            lastsavedangle=strdegree;
        }
    }

    private void loadmap() {
        SupportMapFragment mapFragment = new SupportMapFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.googlemap, mapFragment).commit();

        if (mgooglemap == null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    setMap(googleMap);
                }
            });
        } else {
            setMap(mgooglemap);
        }
    }

    private void setMap(GoogleMap googleMap) {
        this.mgooglemap = googleMap;
        this.mgooglemap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        /*points.add(new LatLng(26.235896,74.24235896));
        points.add(new LatLng(26.34235896,74.24235896));
        points.add(new LatLng(26.232435896,74.424235896));
        points.add(new LatLng(26.22235896,74.2325896));
        points.add(new LatLng(26.454235896,74.24235896));
        points.add(new LatLng(26.534235896,74.24235896));
        points.add(new LatLng(26.5565235896,74.42235896));
        points.add(new LatLng(26.67235896,74.23235896));
        points.add(new LatLng(26.878235896,74.22135896));
        points.add(new LatLng(26.45235896,74.23335896));
        points.add(new LatLng(26.33235896,74.22335896));
        polylineOptions.addAll(points);
        polylineOptions.color(Color.BLUE);
        polylineOptions.width(20);
        Polyline polyline =mgooglemap.addPolyline(polylineOptions);
        //polyline.setEndCap(new RoundCap());
        //polyline.setJointType(JointType.ROUND);
         polyline.setPattern(PATTERN_POLYLINE_DOTTED);*/

    }

    //https://stackoverflow.com/questions/32905939/how-to-customize-the-polyline-in-google-map/46559529

    private void populateUserCurrentLocation(final LatLng location) {
        // DeviceUser user = DeviceUserManager.getInstance().getUser();
        if (mgooglemap == null)
            return;

        googlemap.setVisibility(View.VISIBLE);


        //polyline.setPattern(PATTERN_POLYLINE_DOTTED);

        /*Polygon polygon = mgooglemap.addPolygon(new PolygonOptions()
                .clickable(true)
                .add(location));
        polygon.setTag("beta");


        String type = "";
        // Get the data object stored with the polygon.
        if (polygon.getTag() != null) {
            type = polygon.getTag().toString();
        }

        List<PatternItem> pattern = null;
        int strokeColor = COLOR_BLACK_ARGB;
        int fillColor = COLOR_WHITE_ARGB;

        switch (type) {
            // If no type is given, allow the API to use the default.
            case "alpha":
                // Apply a stroke pattern to render a dashed line, and define colors.
                pattern = PATTERN_POLYGON_ALPHA;
                strokeColor = COLOR_GREEN_ARGB;
                fillColor = COLOR_PURPLE_ARGB;
                break;
            case "beta":
                // Apply a stroke pattern to render a line of dots and dashes, and define colors.
                pattern = PATTERN_POLYGON_BETA;
                strokeColor = COLOR_ORANGE_ARGB;
                fillColor = COLOR_BLUE_ARGB;
                break;
        }

        polygon.setStrokePattern(pattern);
        polygon.setStrokeWidth(POLYGON_STROKE_WIDTH_PX);
        polygon.setStrokeColor(strokeColor);
        polygon.setFillColor(fillColor);*/

        mgooglemap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.latitude, location.longitude), 15));
        if (ActivityCompat.checkSelfPermission(applicationviavideocomposer.getactivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(applicationviavideocomposer.getactivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        } else {
            if(!ismediaplayer)
            {
                mgooglemap.setMyLocationEnabled(true);
            }
            else
            {
                mgooglemap.setMyLocationEnabled(false);
            }
            mgooglemap.getUiSettings().setZoomControlsEnabled(false);
            mgooglemap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }


    public void drawmappoints(LatLng latlng)
    {
        if(mgooglemap != null)
        {
            Log.e("GraphicalLatLng",""+latlng.latitude+" "+latlng.longitude);
            {
                    /*points.add(latlng);
                    polylineOptions.addAll(points);
                    polylineOptions.color(Color.BLUE);
                    polylineOptions.width(20);
                    Polyline polyline =mgooglemap.addPolyline(polylineOptions);
                    polyline.setStartCap(new RoundCap());
                    polyline.setEndCap(new RoundCap());
                    polyline.setJointType(JointType.DEFAULT);*/

                mgooglemap.addMarker(new MarkerOptions()
                        .position(latlng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.horizontalline)));
            }
                /*{
                    points.add(latlng);
                    polylineOptions.addAll(points);
                    polylineOptions.color(Color.WHITE);
                    polylineOptions.width(20);
                    Polyline polyline =mgooglemap.addPolyline(polylineOptions);
                    polyline.setStartCap(new RoundCap());
                    polyline.setEndCap(new RoundCap());
                    polyline.setJointType(JointType.ROUND);
                }*/
        }
    }
    public void rotatecompass(int degree)
    {
        RotateAnimation ra = new RotateAnimation(
                currentDegree,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        // how long the animation will take place
        ra.setDuration(210);
        ra.setFillAfter(true);
        img_compass.startAnimation(ra);
        currentDegree = -degree;
    }

    public void updatemediainfo(String transactionid,String medianame,String medianotes,String mediafolder)
    {
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
        mdbhelper.updatemediainfofromstarttransactionid(transactionid,medianame,medianotes,mediafolder);
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
        if(editableText!=null) {
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
        if(player != null)
        {
            if(selectedvideouri!= null){
                playpausebutton.setImageResource(R.drawable.pause);
                player.start();
                hdlr.postDelayed(UpdateSongTime, 10);
                //player.setOnCompletionListener(this);
            }
            else{
                if(mediafilepath!=null){
                    playpausebutton.setImageResource(R.drawable.pause);
                    player.start();
                    hdlr.postDelayed(UpdateSongTime, 10);
                    //player.setOnCompletionListener(this);
                }
            }
        }
    }
    public void pause() {
        if(player != null){
            playpausebutton.setImageResource(R.drawable.play_btn);
            player.pause();
        }
    }

    private Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {
            if(player != null ){

                if(player.getCurrentPosition() > maxincreasevideoduration)
                    maxincreasevideoduration=player.getCurrentPosition();

                if(currentaudioduration == 0 || (player.getCurrentPosition() > currentaudioduration))
                {
                    currentaudioduration =player.getCurrentPosition();  // suppose its on 4th pos means 4000
                    currentaudiodurationseconds = currentaudioduration /1000;  // Its 4
                }


                videostarttime = player.getCurrentPosition();
                mediaseekbar.setProgress(player.getCurrentPosition());

                if (time_current != null)
                    time_current.setText(stringForTime(videostarttime));

                hdlr.postDelayed(this, 10);
            }
        }
    };

    private String stringForTime(int timeMs) {

        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours   = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    public void setaudiodata(){

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                endtime = player.getDuration();
                videostarttime = player.getCurrentPosition();

                audioduration  = endtime;

                if (totalduration != null)
                    totalduration.setText(stringForTime(endtime));
                if  (time_current != null)
                    time_current.setText(stringForTime(videostarttime));

                mediaseekbar.setMax(endtime);
                mediaseekbar.setProgress(videostarttime);
            }
        },10);
    }

    private void updatetextureviewsize(int viewWidth, int viewHeight) {

        float scaleX = 1.0f;
        float scaleY = 1.0f;

        if (videowidth > viewWidth && videoheight > viewHeight) {
            scaleX = videowidth / viewWidth;
            scaleY = videoheight / viewHeight;
        } else if (videowidth < viewWidth && videoheight < viewHeight) {
            scaleY = viewWidth / videowidth;
            scaleX = viewHeight / videoheight;
        } else if (viewWidth > videowidth) {
            scaleY = (viewWidth / videowidth) / (viewHeight / videoheight);
        } else if (viewHeight > videoheight) {
            scaleX = (viewHeight / videoheight) / (viewWidth / videowidth);
        }

     // Calculate pivot points, in our case crop from center
        int pivotPointX = viewWidth / 2;
        int pivotPointY = viewHeight / 2;

        Matrix matrix = new Matrix();
        matrix.setScale(scaleX, scaleY, pivotPointX, pivotPointY);

        videotextureview.setTransform(matrix);
        //videotextureview.setLayoutParams(new FrameLayout.LayoutParams(viewWidth, viewHeight));
    }


    private void getbitmap(final int viewwidth) {
        backgroundexecutor.execute(new backgroundexecutor.task("", 0L, "") {
                                       @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                                       @Override
                                       public void execute() {

                                           Uri uri= FileProvider.getUriForFile(applicationviavideocomposer.getactivity(),
                                                   BuildConfig.APPLICATION_ID + ".provider", new File(mediafilepath));

                                           if(uri !=null){
                                               try {
                                                   LongSparseArray<Bitmap> thumbnailList = new LongSparseArray<>();

                                                   MediaMetadataRetriever mediametadataretriever = new MediaMetadataRetriever();
                                                   mediametadataretriever.setDataSource(getContext(), uri);

                                                   // Retrieve media data
                                                   long videoLengthInMs = Integer.parseInt(mediametadataretriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) * 1000;

                                                   // Set thumbnail properties (Thumbs are squares)

                                                   if(mheightview == 50)
                                                       mheightview = mheightview*2;

                                                   final int thumbWidth = mheightview;
                                                   final int thumbHeight = mheightview;

                                                   int numThumbs = (int) Math.ceil(((float) viewwidth) / thumbWidth);

                                                   final long interval = videoLengthInMs / numThumbs;

                                                   for (int i = 0; i < numThumbs; ++i) {
                                                       Bitmap bitmap = mediametadataretriever.getFrameAtTime(i * interval, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                                                       // TODO: bitmap might be null here, hence throwing NullPointerException. You were right
                                                       try {
                                                           bitmap = Bitmap.createScaledBitmap(bitmap, thumbWidth, thumbHeight, false);

                                                           mbitmaplist.add(i,new frame(i,bitmap,false));

                                                           applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                                                               @Override
                                                               public void run() {
                                                                   if(adapter != null){
                                                                       adapter.notifyDataSetChanged();
                                                                       scurraberverticalbar.setVisibility(View.GONE);
                                                                       //runmethod = true;
                                                                   }

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
                                   }
        );
    }
}
