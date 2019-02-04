package com.deeptruth.app.android.fragments;

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
import android.graphics.Matrix;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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

import com.deeptruth.app.android.BuildConfig;
import com.deeptruth.app.android.R;
import com.deeptruth.app.android.adapter.framebitmapadapter;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.database.databasemanager;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.models.arraycontainer;
import com.deeptruth.app.android.models.frame;
import com.deeptruth.app.android.models.metadatahash;
import com.deeptruth.app.android.models.metricmodel;
import com.deeptruth.app.android.models.videomodel;
import com.deeptruth.app.android.models.wavevisualizer;
import com.deeptruth.app.android.utils.NoScrollRecycler;
import com.deeptruth.app.android.utils.centerlayoutmanager;
import com.deeptruth.app.android.utils.circularImageview;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.progressdialog;
import com.deeptruth.app.android.utils.videocontrollerview;
import com.deeptruth.app.android.utils.xdata;
import com.deeptruth.app.android.videotrimmer.utils.backgroundexecutor;
import com.deeptruth.app.android.views.customfonttextview;
import com.deeptruth.app.android.views.customseekbar;
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
import org.json.JSONTokener;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Iterator;
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
    @BindView(R.id.layout_scrubberview)
    RelativeLayout layout_scrubberview;

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
    @BindView(R.id.scrub_layout)
    RelativeLayout scrublayout;
    @BindView(R.id.layout_validating)
    LinearLayout layout_validating;
    @BindView(R.id.txt_section_validating_secondary)
    TextView txt_section_validating_secondary;
    @BindView(R.id.layout_dtls)
    LinearLayout layout_dtls;

    @BindView(R.id.layout_seekbartiming)
    RelativeLayout layout_seekbartiming;
    @BindView(R.id.dividerline)
    View dividerline;
    @BindView(R.id.layoutpause)
    RelativeLayout layoutpause;

    GoogleMap mgooglemap;
    Surface surfacetexture = null;

    boolean ismediaplayer = false;
    String medianame = "",medianotes = "",mediafolder = "",mediatransectionid = "",latitude = "", longitude = "",screenheight = "",screenwidth = "",
            mediastartdevicedate = "",mediatime = "",mediasize="",lastsavedangle="";
    private float currentDegree = 0f;
    private BroadcastReceiver getmetadatabroadcastreceiver;
    int targetheight=0,previousheight=0,targetwidth=0,previouswidth=0, previouswidthpercentage=0,scrubberviewwidth=0;
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
    ArrayList<wavevisualizer> wavevisualizerslist =new ArrayList<>();
    ArrayList<String> addhashvaluelist = new ArrayList<>();
    int count = 0;
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
            gethelper().drawerenabledisable(false);

            videotextureview = (TextureView) findViewById(R.id.videotextureview);
            linearLayout=rootview.findViewById(R.id.content);
            showcontrollers=rootview.findViewById(R.id.video_container);
            scurraberverticalbar=rootview.findViewById(R.id.scrubberverticalbar);
            mFormatBuilder = new StringBuilder();
            mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
            videotextureview.setSurfaceTextureListener(this);

            mediaseekbar.setPadding(0,0,0,0);
            mheightview = getContext().getResources().getDimensionPixelOffset(R.dimen.frames_video_height);

            playpausebutton.setImageResource(R.drawable.play_btn);

            frameduration=common.checkframeduration();
            keytype=common.checkkey();

            edt_medianame.setEnabled(false);
            edt_medianame.setClickable(false);
            edt_medianame.setFocusable(false);
            edt_medianame.setFocusableInTouchMode(false);

            edt_medianotes.setEnabled(false);
            edt_medianotes.setClickable(false);
            edt_medianotes.setFocusable(false);
            edt_medianotes.setFocusableInTouchMode(false);

            txt_section_validating_secondary.setText(config.caution);
            txt_section_validating_secondary.setBackgroundColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.yellow_background));

            /*mediaseekbar.setThumb(applicationviavideocomposer.getactivity().getResources().getDrawable(
                    R.drawable.custom_thumb));*/

            mediaseekbar.setThumbOffset(0);
            mediaseekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                private int mProgressAtStartTracking=0;
                private final int SENSITIVITY=0;
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
                        p.setMargins(thumbRect.centerX()+2,0, 0, 0);
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
                    mProgressAtStartTracking = seekBar.getProgress();
                    if(Math.abs(mProgressAtStartTracking - seekBar.getProgress()) <= SENSITIVITY){
                        // react to thumb click
                        if(layout_validating.getVisibility() == View.VISIBLE)
                        {
                            layout_validating.setVisibility(View.GONE);
                        }
                        else
                        {
                            layout_validating.setVisibility(View.VISIBLE);
                        }
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


                                    if(player !=null)
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

            img_dotmenu.setOnClickListener(new setonClick());
            img_folder.setOnClickListener(new setonClick());
            img_camera.setOnClickListener(new setonClick());
            img_arrow_back.setOnClickListener(new setonClick());
            videotextureview.setOnClickListener(new setonClick());
            img_delete_media.setOnClickListener(new setonClick());
            imgpause.setOnClickListener(new setonClick());
            imgpause.setVisibility(View.GONE);

            img_dotmenu.setVisibility(View.VISIBLE);
            img_folder.setVisibility(View.VISIBLE);
            img_arrow_back.setVisibility(View.VISIBLE);

            if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_reader))
            {
                img_camera.setVisibility(View.GONE);
            }
            else
            {
                img_camera.setVisibility(View.VISIBLE);
            }

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
            layout_dtls.setOnClickListener(this);

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
                    playpausebutton.setVisibility(View.VISIBLE);
                    recenterplaypause();
                    Log.e("previousheight",""+previousheight);
                }
            });


            txtslotencyption.setOnClickListener(new setonClick());
            txtslotmeta.setOnClickListener(new setonClick());
            txtslotmedia.setOnClickListener(new setonClick());
            resetButtonViews(txtslotmedia, txtslotmeta, txtslotencyption);

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

            /*if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_reader))
            {

            }
            else if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_composer))
            {

                Thread thread = new Thread() {
                    public void run() {
                        fetchmetadatafromdb();
                    }
                };
                thread.start();

            }*/
            //detuct keyboard is open or not
            showcontrollers.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int heightDiff = showcontrollers.getRootView().getHeight() - showcontrollers.getHeight();
                    if (heightDiff > dpToPx(applicationviavideocomposer.getactivity().getApplication(), 200)) { // if more than 200 dp, it's probably a keyboard...
                        // ... do something here
                        Log.e("ikeyboardshowing","iskeyboardshowing....true");
                        layout_halfscrnimg.setVisibility(View.GONE);
                        layout_mediatype.setVisibility(View.GONE);
                        layout_footer.setVisibility(View.GONE);

                    }else
                    {
                        if(layout_halfscrnimg.getVisibility()==View.GONE){
                            layout_mediatype.setVisibility(View.VISIBLE);
                            layout_halfscrnimg.setVisibility(View.VISIBLE);
                            layout_footer.setVisibility(View.VISIBLE);
                            edt_medianame.setFocusable(false);
                            edt_medianotes.setFocusable(false);
                        }

                        Log.e("ikeyboardshowing","iskeyboardshowing....false");
                    }
                }
            });
            registerbroadcastreciver();
            Log.e("oncreate","oncreate");

            loadmap();
        }
        return rootview;
    }

    @Override
    public void initviews(View parent, Bundle savedInstanceState) {
        super.initviews(parent, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        ButterKnife.bind(this, parent);
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
                if(scrubberviewwidth == 0)
                {
                    scrubberviewwidth = layout_scrubberview.getWidth();
                    scrollview_meta.setVisibility(View.INVISIBLE);

                    scrollView_encyrption.setVisibility(View.INVISIBLE);
                    Thread thread = new Thread() {
                        public void run() {
                            getbitmap(scrubberviewwidth);
                            //getframesbitmap();
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
        issurafcedestroyed=true;
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

    public void recenterplaypause()
    {
        videotextureview.post(new Runnable() {
            @Override
            public void run() {
                RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                playpausebutton.setLayoutParams(params);
            }
        });
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
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    edt_medianame.setClickable(true);
                    edt_medianame.setEnabled(true);
                    edt_medianame.setFocusable(true);
                    edt_medianame.setFocusableInTouchMode(true);
                    edt_medianame.setSelection(edt_medianame.getText().length());
                    edt_medianame.requestFocus();
                    break;
                case R.id.img_edit_notes:
                    InputMethodManager immn = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    immn.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    edt_medianotes.setClickable(true);
                    edt_medianotes.setEnabled(true);
                    edt_medianotes.setFocusable(true);
                    edt_medianotes.setFocusableInTouchMode(true);
                    edt_medianotes.setSelection(edt_medianotes.getText().length());
                    edt_medianotes.requestFocus();

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

                case R.id.img_arrow_back:
                    gethelper().onBack();
                    break;
                case R.id.img_fullscreen:
                    if(layout_photodetails.getVisibility()==View.VISIBLE){
                        gethelper().drawerenabledisable(true);
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
                        recenterplaypause();
                    } else{
                        gethelper().drawerenabledisable(false);
                        collapse(videotextureview,100,previousheight);
                        layout_photodetails.setVisibility(View.VISIBLE);
                        tab_layout.setVisibility(View.VISIBLE);
                        scrollview_detail.setVisibility(View.VISIBLE);
                        layout_mediatype.setVisibility(View.VISIBLE);
                        totalduration.setVisibility(View.VISIBLE);
                        time_current.setVisibility(View.VISIBLE);
                        layout_footer.setVisibility(View.VISIBLE);
                        layoutcustomcontroller.setBackgroundColor(getResources().getColor(R.color.white));
                        updatetextureviewsize((previouswidth- previouswidthpercentage),previousheight);
                        layoutcustomcontroller.setVisibility(View.VISIBLE);
                        layout_seekbartiming.getResources().getColor(R.color.white);
                        dividerline.setVisibility(View.GONE);
                        playpausebutton.setVisibility(View.VISIBLE);
                        imgpause.setVisibility(View.GONE);
                        collapseimg_view();
                        img_fullscreen.setImageResource(R.drawable.ic_full_screen_mode);
                        resetButtonViews(txtslotmedia, txtslotmeta, txtslotencyption);
                        recenterplaypause();
                    }
                    break;

                case R.id.videotextureview:
                    Log.e("ontouch","ontouch");

                    if(layout_photodetails.getVisibility()==View.GONE){
                        if(layout_footer.getVisibility()==(View.GONE)){
                            setbottomimgview();
                            recenterplaypause();
                            img_fullscreen.setVisibility(View.VISIBLE);
                            img_fullscreen.setImageResource(R.drawable.ic_info_mode);
                            layout_mediatype.setVisibility(View.VISIBLE);
                            layout_footer.setVisibility(View.VISIBLE);
                            playpausebutton.setVisibility(View.VISIBLE);
                        } else {
                           /*
                            setbottomimgview();*/
                            playpausebutton.setVisibility(View.GONE);
                            img_fullscreen.setVisibility(View.GONE);
                            img_fullscreen.setImageResource(R.drawable.ic_info_mode);
                            layout_mediatype.setVisibility(View.GONE);
                            layout_footer.setVisibility(View.GONE);
                        }

                    } else {
                        collapseimg_view();
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
                        if(layout_photodetails.getVisibility()==View.GONE){
                            layoutpause.setBackgroundColor(getResources().getColor(R.color.whitetransparent));
                            layout_seekbartiming.setBackgroundColor(getResources().getColor(R.color.whitetransparent));
                            layoutcustomcontroller.setBackgroundColor(getResources().getColor(R.color.transparent));
                            layoutcustomcontroller.requestLayout();
                            layout_footer.setVisibility(View.GONE);
                            totalduration.setVisibility(View.VISIBLE);
                            time_current.setVisibility(View.VISIBLE);
                            videotextureview.setClickable(false);
                            playpausebutton.setVisibility(View.GONE);
                            img_fullscreen.setVisibility(View.INVISIBLE);
                            layoutcustomcontroller.setVisibility(View.VISIBLE);
                            imgpause.setVisibility(View.VISIBLE);
                            dividerline.setVisibility(View.GONE);
                        }
                        start();
                    }
                    break;

                case R.id.img_pause:
                    if(player.isPlaying()){
                        pause();
                        if(layout_photodetails.getVisibility()==View.GONE){
                            videotextureview.setClickable(true);
                            playpausebutton.setImageResource(R.drawable.play_btn);
                            img_share_media.setVisibility(View.VISIBLE);
                            img_delete_media.setVisibility(View.VISIBLE);
                            playpausebutton.setVisibility(View.VISIBLE);
                            img_fullscreen.setVisibility(View.VISIBLE);
                            layout_footer.setVisibility(View.VISIBLE);
                            layoutcustomcontroller.setVisibility(View.GONE);
                            totalduration.setVisibility(View.VISIBLE);
                            time_current.setVisibility(View.VISIBLE);
                            imgpause.setVisibility(View.GONE);
                        }
                    }
                    break;

                case R.id.layout_dtls:
                    Log.e("ontouch","ontouchscrollview");
                    if(layout_halfscrnimg.getVisibility() == View.GONE){
                        view.clearFocus();
                        InputMethodManager immm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        immm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void fetchmetadatafromdb() {
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
                    String mediacompleteddate="";
                    if (cur != null && cur.getCount() > 0 && cur.moveToFirst())
                    {
                        do{
                            mediacompleteddate = "" + cur.getString(cur.getColumnIndex("videocompletedevicedate"));
                            mediastartdevicedate = "" + cur.getString(cur.getColumnIndex("videostartdevicedate"));
                            medianame = "" + cur.getString(cur.getColumnIndex("media_name"));
                            medianotes =  "" + cur.getString(cur.getColumnIndex("media_notes"));
                            mediafolder =  "" + cur.getString(cur.getColumnIndex("media_folder"));
                            mediatransectionid = "" + cur.getString(cur.getColumnIndex("videostarttransactionid"));

                        }while(cur.moveToNext());
                    }

                    if (!mediacompleteddate.isEmpty())
                    {
                        final String finalMediacompleteddate = mediacompleteddate;
                        applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    SimpleDateFormat formatted = null;
                                    Date mediadate = null;
                                    if(finalMediacompleteddate.contains("T"))
                                    {
                                        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH);
                                        mediadate = format.parse(finalMediacompleteddate);
                                    }
                                    else
                                    {
                                        DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                        mediadate = format.parse(finalMediacompleteddate);
                                    }

                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTime(mediadate);
                                    int decreaseseconds=player.getDuration()/1000;
                                    calendar.add(Calendar.SECOND, -decreaseseconds);
                                    Date startdate = calendar.getTime();
                                    formatted = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
                                    String startformatteddate=formatted.format(startdate);
                                    String endformatteddate=formatted.format(mediadate);
                                    final String filecreateddate = new SimpleDateFormat("MM-dd-yyyy").format(startdate);
                                    final String createdtime = new SimpleDateFormat("hh:mm:ss aa").format(startdate);
                                    txt_starttime.setText(startformatteddate);
                                    txt_endtime.setText(endformatteddate);
                                    txt_title_actionbarcomposer.setText(filecreateddate);
                                    txt_createdtime.setText(createdtime);
                                }catch (Exception e)
                                {
                                    e.printStackTrace();
                                }

                            }
                        });

                        ArrayList<metadatahash> mitemlist=mdbhelper.getmediametadatabyfilename(common.getfilename(mediafilepath));
                        if(metricmainarraylist.size()>0){

                            for(int i=0;i<mitemlist.size();i++)
                            {
                                String sequencehash = mitemlist.get(i).getSequencehash();
                                String hashmethod = mitemlist.get(i).getHashmethod();
                                String videostarttransactionid = mitemlist.get(i).getVideostarttransactionid();
                                String serverdictionaryhash = mitemlist.get(i).getValuehash();
                                String color = mitemlist.get(i).getColor();
                                String latency = mitemlist.get(i).getLatency();
                                metricmainarraylist.set(i,new arraycontainer(hashmethod,videostarttransactionid,
                                        sequencehash,serverdictionaryhash,color,latency));
                            }

                        }else{

                            for(int i=0;i<mitemlist.size();i++)
                            {
                                String metricdata=mitemlist.get(i).getMetricdata();
                                String sequencehash = mitemlist.get(i).getSequencehash();
                                String hashmethod = mitemlist.get(i).getHashmethod();
                                String videostarttransactionid = mitemlist.get(i).getVideostarttransactionid();
                                String serverdictionaryhash = mitemlist.get(i).getValuehash();
                                String color = mitemlist.get(i).getColor();
                                String latency = mitemlist.get(i).getLatency();
                                parsemetadata(metricdata,hashmethod,videostarttransactionid,sequencehash,serverdictionaryhash,color,latency);
                            }

                            applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    edt_medianotes.setText(medianotes);
                                    edt_medianame.setText(medianame);
                                }
                            });
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

    public void parsemetadata(String metadata,String hashmethod,String videostarttransactionid,String hashvalue,String metahash,
                              String color,String latency) {
        try {

            Object json = new JSONTokener(metadata).nextValue();
            JSONObject jsonobject=null;
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
                        color,latency));
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
                    metricmainarraylist.add(new arraycontainer(metricItemArraylist,hashmethod,videostarttransactionid,hashvalue,
                            metahash,color,latency));
                }
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
                            fetchmetadatafromdb();
                    }
                };
                thread.start();
            }
        };
        getActivity().registerReceiver(getmetadatabroadcastreceiver, intentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            applicationviavideocomposer.getactivity().unregisterReceiver(getmetadatabroadcastreceiver);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        Log.e("onstop","onstop");
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

            setmetriceshashesdata();
            Log.e("onprepared","onprepared");
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

    public void setmetriceshashesdata()
    {
        if(myHandler != null && myRunnable != null)
            myHandler.removeCallbacks(myRunnable);

        myHandler=new Handler();
        myRunnable = new Runnable() {
            @Override
            public void run() {
                if(metricmainarraylist.size() == 0)
                    fetchmetadatafromdb();

                  if(arraycontainerformetric != null)
                  {
                      common.setgraphicalblockchainvalue(config.blockchainid,arraycontainerformetric.getVideostarttransactionid(),true);
                      common.setgraphicalblockchainvalue(config.hashformula,arraycontainerformetric.getHashmethod(),true);
                      common.setgraphicalblockchainvalue(config.datahash,arraycontainerformetric.getValuehash(),true);
                      common.setgraphicalblockchainvalue(config.matrichash,arraycontainerformetric.getMetahash(),true);

                      common.setspannable(getResources().getString(R.string.blockchain_id)," "+ arraycontainerformetric.getVideostarttransactionid(), txt_blockchainid);
                      common.setspannable(getResources().getString(R.string.block_id)," "+ arraycontainerformetric.getHashmethod(), txt_blockid);
                      common.setspannable(getResources().getString(R.string.block_number)," "+ arraycontainerformetric.getValuehash(), txt_blocknumber);
                      common.setspannable(getResources().getString(R.string.metrichash)," "+arraycontainerformetric.getMetahash(), txt_metahash);

                      txt_section_validating_secondary.setText(config.verified);
                      if(arraycontainerformetric.getColor().equalsIgnoreCase(config.color_green))
                      {
                          txt_section_validating_secondary.setBackgroundColor(applicationviavideocomposer.
                                  getactivity().getResources().getColor(R.color.green_background));
                      }
                      else if(arraycontainerformetric.getColor().equalsIgnoreCase(config.color_red))
                      {
                          txt_section_validating_secondary.setBackgroundColor(applicationviavideocomposer.
                                  getactivity().getResources().getColor(R.color.red));
                      }
                      else if(arraycontainerformetric.getColor().equalsIgnoreCase(config.color_yellow))
                      {
                          txt_section_validating_secondary.setBackgroundColor(applicationviavideocomposer.
                                  getactivity().getResources().getColor(R.color.green_background));
                      }

                      ArrayList<metricmodel> metricItemArraylist = arraycontainerformetric.getMetricItemArraylist();
                      for(int j=0;j<metricItemArraylist.size();j++)
                      {
                          common.setgraphicalitems(metricItemArraylist.get(j).getMetricTrackKeyName(),
                                  metricItemArraylist.get(j).getMetricTrackValue(),true);

                          if(scrollview_meta.getVisibility() == View.VISIBLE)
                            setmetadatavalue(metricItemArraylist.get(j));
                      }
                  }
                  setmetricesgraphicaldata();

                myHandler.postDelayed(this, 1500);
            }
        };
        myHandler.post(myRunnable);
    }

    public void setmetricesgraphicaldata()
    {
        long currentduration=maxincreasevideoduration;
        long percentage = 0;
        int totalframes = metricmainarraylist.size();
        int count = 0;

        if(videoduration > 0 && currentduration > 0)
               percentage = (currentduration*100)/videoduration ;

        if(percentage > 0)
            count =(int) (totalframes*percentage)/100;

        if(metricmainarraylist.size() == 0 || currentduration == 0)
            return;

        if(isvideocompleted)
            count=metricmainarraylist.size()-1;

        if(count < metricmainarraylist.size())
        {
            arraycontainerformetric = new arraycontainer();
            arraycontainerformetric = metricmainarraylist.get(count);
            metricmainarraylist.get(position).setIsupdated(true);
        }

        if(((! latitude.trim().isEmpty()) && (! latitude.equalsIgnoreCase("NA"))) &&
                (! longitude.trim().isEmpty()) && (! longitude.equalsIgnoreCase("NA")))
            populateUserCurrentLocation(new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude)));
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

            if(layout_footer.getVisibility()==View.GONE && layout_photodetails.getVisibility()==View.GONE){
                showcontrollers();
                layoutcustomcontroller.setVisibility(View.GONE);
            }else{
                showcontrollers();
                layoutcustomcontroller.setVisibility(View.VISIBLE);
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(player != null && controller!= null)
                    {
                        player.seekTo(0);
                        wavevisualizerslist.clear();
                        controller.setProgress(0,true);
                        playpausebutton.setImageResource(R.drawable.play_btn);

                       /* RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        relativeParams.setMargins(0, 0,0, 0);
                        recyview_frames.setLayoutParams(relativeParams);*/
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
            }

            tvsize.setText(common.filesize(mediafilepath));
            playpausebutton.setImageResource(R.drawable.play_btn);
            position = 0;
        }
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
        if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_reader)){
            if(((! latitude.trim().isEmpty()) && (! latitude.equalsIgnoreCase("NA"))) &&
                    (! longitude.trim().isEmpty()) && (! longitude.equalsIgnoreCase("NA")))
                drawmappoints(new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude)));
            Log.e("drawpoints","drawvalues");
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
        isvideocompleted=false;
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
                   LongSparseArray<Bitmap> thumbnailList = new LongSparseArray<>();

                   MediaMetadataRetriever mediametadataretriever = new MediaMetadataRetriever();
                   mediametadataretriever.setDataSource(getContext(), uri);

                   // Retrieve media data
                   long videoLengthInMs = Integer.parseInt(mediametadataretriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) * 1000;

                   // Set thumbnail properties (Thumbs are squares)

                   if (mheightview == 50)
                       mheightview = mheightview * 2;

                   final int thumbWidth = mheightview;
                   final int thumbHeight = mheightview;

                   int numThumbs = (int) Math.ceil(((float) viewwidth) / thumbWidth);

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
                                   if (adapter != null) {
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
          });
    }

    public void showcontrollers(){
        videotextureview.setClickable(true);
        layout_mediatype.setVisibility(View.VISIBLE);
        playpausebutton.setImageResource(R.drawable.play_btn);
        playpausebutton.setVisibility(View.VISIBLE);
        img_fullscreen.setVisibility(View.VISIBLE);
        layout_footer.setVisibility(View.VISIBLE);
        img_share_media.setVisibility(View.VISIBLE);
        img_delete_media.setVisibility(View.VISIBLE);
        imgpause.setVisibility(View.GONE);
    }

    public void setbottomimgview(){
        RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM );
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, R.id.layout_halfscrnimg);
        img_fullscreen.setLayoutParams(params);
    }
    public void collapseimg_view(){
        RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.ABOVE,R.id.layout_customcontroller );
        img_fullscreen.setLayoutParams(params);
    }

    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }
}
