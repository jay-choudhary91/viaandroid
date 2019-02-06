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
import android.graphics.Color;
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
import android.view.Gravity;
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
import com.deeptruth.app.android.adapter.folderdirectoryspinneradapter;
import com.deeptruth.app.android.adapter.framebitmapadapter;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.database.databasemanager;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.models.arraycontainer;
import com.deeptruth.app.android.models.folder;
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
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by devesh on 21/8/18.
 */

public class videoreaderfragment extends basefragment implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback,View.OnTouchListener,TextureView.SurfaceTextureListener,
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
    @BindView(R.id.linear_seekbarcolorview)
    LinearLayout linearseekbarcolorview;

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
            mediastartdevicedate = "",lastsavedangle="";
    private float currentDegree = 0f;
    private BroadcastReceiver getmetadatabroadcastreceiver;
    int targetheight=0,previousheight=0,targetwidth=0,previouswidth=0, previouswidthpercentage=0,scrubberviewwidth=0;
    private Handler hdlr = new Handler();
    StringBuilder mFormatBuilder;
    Formatter mFormatter;
    private framebitmapadapter adapter;
    private RelativeLayout scurraberverticalbar;
    private String mediafilepath = null;
    private RelativeLayout showcontrollers;
    private TextureView videotextureview;
    private MediaPlayer player;
    private videocontrollerview controller;
    private View rootview = null;
    private long playerposition=0;
    private Uri selectedvideouri =null;
    private Handler myHandler;
    private Runnable myRunnable;
    private long videoduration =0,maxincreasevideoduration=0;
    private ArrayList<videomodel> mainvideoframes =new ArrayList<>();
    private ArrayList<videomodel> mvideoframes =new ArrayList<>();
    private ArrayList<videomodel> mallframes =new ArrayList<>();
    private ArrayList<frame> mbitmaplist =new ArrayList<>();
    private ArrayList<videomodel> mhashesitems =new ArrayList<>();
    private ArrayList<arraycontainer> metricmainarraylist = new ArrayList<>();
    boolean runmethod = false;
    public boolean isvideocompleted=false;
    public int flingactionmindstvac;
    ArrayList<wavevisualizer> wavevisualizerslist =new ArrayList<>();
    private long currentaudioduration =0;
    private int videostarttime =0, endtime =0;
    int position=0;
    String latency = "";
    int mheightview = 0;

    private float videoheight, videowidth;

    arraycontainer arraycontainerformetric =null;
    adapteritemclick mcontrollernavigator;
    int currentprocessframe=0;
    @Override
    public int getlayoutid() {
        return R.layout.full_screen_videoview;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        if(rootview == null) {
            rootview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, rootview);
            loadviewdata();
            loadmap();
        }
        return rootview;
    }

    public void loadviewdata()
    {
        gethelper().setrecordingrunning(false);
        gethelper().drawerenabledisable(false,layout_footer,layout_mediatype,playpausebutton,layoutcustomcontroller,img_fullscreen);

        videotextureview = (TextureView) findViewById(R.id.videotextureview);
        showcontrollers=rootview.findViewById(R.id.video_container);
        scurraberverticalbar=rootview.findViewById(R.id.scrubberverticalbar);
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        videotextureview.setSurfaceTextureListener(this);

        mediaseekbar.setPadding(0,0,0,0);
        mheightview = getContext().getResources().getDimensionPixelOffset(R.dimen.frames_video_height);

        playpausebutton.setImageResource(R.drawable.play_btn);

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

        mediaseekbar.setThumbOffset(0);
        mediaseekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int mProgressAtStartTracking=0;
            private final int SENSITIVITY=0;
            @Override
            public void onProgressChanged(final SeekBar seekBar, int progress, boolean fromUser)
            {
                if(progress > 0)
                {
                    //Log.e("Max current",""+mediaseekbar.getMax()+" "+mediaseekbar.getProgress());
                    int progresspercentage = (progress*100)/mediaseekbar.getMax();
                    if(progresspercentage > 0)
                        currentprocessframe =(int) (metricmainarraylist.size()*progresspercentage)/100;

                    if(isvideocompleted)
                        currentprocessframe=metricmainarraylist.size()-1;

                    if(currentprocessframe < metricmainarraylist.size())
                    {
                        arraycontainerformetric = new arraycontainer();
                        arraycontainerformetric = metricmainarraylist.get(currentprocessframe);
                    }

                    layout_progressline.setVisibility(View.VISIBLE);
                    RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    p.addRule(RelativeLayout.ABOVE, seekBar.getId());
                    Rect thumbRect = mediaseekbar.getSeekBarThumb().getBounds();
                    p.setMargins(thumbRect.centerX()+5,0, 0, 0);
                    layout_progressline.setLayoutParams(p);
                    txt_mediatimethumb.setText(stringForTime(player.getCurrentPosition()));
                    txt_mediatimethumb.setVisibility(View.VISIBLE);
                }
                else
                {
                    layout_progressline.setVisibility(View.GONE);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mProgressAtStartTracking = seekBar.getProgress();
                /*if(Math.abs(mProgressAtStartTracking - seekBar.getProgress()) <= SENSITIVITY){
                    if(layout_validating.getVisibility() == View.VISIBLE)
                    {
                        layout_validating.setVisibility(View.GONE);
                    }
                    else
                    {
                        layout_validating.setVisibility(View.VISIBLE);
                    }
                }*/
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
                LinearLayoutManager mlinearlayoutmanager = new centerlayoutmanager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                recyview_frames.setLayoutManager(mlinearlayoutmanager);
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
        img_delete_media.setOnClickListener(new setonClick());
        imgpause.setOnClickListener(new setonClick());
        img_share_media.setOnClickListener(new setonClick());
        img_edit_name.setOnClickListener(new setonClick());
        img_edit_notes.setOnClickListener(new setonClick());
        img_fullscreen.setOnClickListener(new setonClick());
        playpausebutton.setOnClickListener(new setonClick());
        layout_dtls.setOnClickListener(this);

        imgpause.setVisibility(View.GONE);
        img_dotmenu.setVisibility(View.VISIBLE);
        img_folder.setVisibility(View.VISIBLE);
        img_arrow_back.setVisibility(View.VISIBLE);
        txtslotmedia.setText(getResources().getString(R.string.video));
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
                RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,mediaseekbar.getHeight());
                parms.setMargins(28,0,30,0);
                parms.addRule(RelativeLayout.BELOW,R.id.layout_scrubberview);
                linearseekbarcolorview.setLayoutParams(parms);
            }
        });

        layout_videoreader.post(new Runnable() {
            @Override
            public void run() {
                targetheight= layout_videoreader.getHeight();
                targetwidth = layout_videoreader.getWidth();
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
            }
        });

        txtslotencyption.setOnClickListener(new setonClick());
        txtslotmeta.setOnClickListener(new setonClick());
        txtslotmedia.setOnClickListener(new setonClick());
        resetButtonViews(txtslotmedia, txtslotmeta, txtslotencyption);
        mediafilepath = xdata.getinstance().getSetting("selectedvideourl");
        edt_medianotes.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    v.setFocusable(false);
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edt_medianame.getWindowToken(), 0);
                    // if (arraymediaitemlist.size() > 0) {
                    String medianotes = edt_medianotes.getText().toString();

                    if(!mediatransectionid.isEmpty())
                        updatemediainfo(mediatransectionid,edt_medianame.getText().toString(),medianotes);
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
                    String renamevalue = edt_medianame.getText().toString();
                    if(!mediatransectionid.isEmpty())
                        updatemediainfo(mediatransectionid,renamevalue,edt_medianotes.getText().toString());

                    editabletext();
                }
            }
        });

        showcontrollers.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = showcontrollers.getRootView().getHeight() - showcontrollers.getHeight();
                if (heightDiff > dpToPx(applicationviavideocomposer.getactivity().getApplication(), 200)) { // if more than 200 dp, it's probably a keyboard...
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
                }
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
                    try {
                        img_arrow_back.setClickable(false);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    gethelper().onBack();
                    break;
                case R.id.img_fullscreen:
                    if(layout_photodetails.getVisibility()==View.VISIBLE){
                        gethelper().drawerenabledisable(true,layout_footer,layout_mediatype,playpausebutton,null,img_fullscreen);
                        expand(videotextureview,targetheight);
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
                        gethelper().drawerenabledisable(false,layout_footer,layout_mediatype,playpausebutton,null,img_fullscreen);
                        collapse(videotextureview,previousheight);
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
                            gethelper().drawerenabledisable(true,null,layout_mediatype,null,layoutcustomcontroller,null);
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
                            gethelper().drawerenabledisable(true,layout_footer,layout_mediatype,playpausebutton,null,img_fullscreen);
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
        registerbroadcastreciver();
    }

    public void fetchmetadatafromdb() {
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

                                    if(mediafolder.trim().length() > 0)
                                        setfolderspinner();

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

                        applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setseekbarlayoutcolor();
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

    public void setfolderspinner()
    {
        final List<folder> folderitem=common.getalldirfolders();
        folderdirectoryspinneradapter adapter = new folderdirectoryspinneradapter(applicationviavideocomposer.getactivity(),
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

        spinnermediafolder.setAdapter(adapter);
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
                                        xdata.getinstance().saveSetting("selectedvideourl",mediafilepath);
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
                              String color,String latency) {
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
        applicationviavideocomposer.getactivity().registerReceiver(getmetadatabroadcastreceiver, intentFilter);
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
            if(player!=null)
            {
                setvideodata();
                // changeactionbarcolor();
                // initAudio();
                // fragmentgraphic.setvisualizerwave();
                wavevisualizerslist.clear();

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
                player.reset();
                player.setDataSource(applicationviavideocomposer.getactivity(),selecteduri);
                player.setSurface(surfacetexture);
                player.prepareAsync();
                Log.e("time","onprepare");
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
                if(metricmainarraylist.size() == 0)
                    fetchmetadatafromdb();

                  if(arraycontainerformetric != null)
                  {
                      common.setgraphicalblockchainvalue(config.blockchainid, arraycontainerformetric.getVideostarttransactionid(), true);
                      common.setgraphicalblockchainvalue(config.hashformula, arraycontainerformetric.getHashmethod(), true);
                      common.setgraphicalblockchainvalue(config.datahash, arraycontainerformetric.getValuehash(), true);
                      common.setgraphicalblockchainvalue(config.matrichash, arraycontainerformetric.getMetahash(), true);

                      common.setspannable(getResources().getString(R.string.blockchain_id), " " + arraycontainerformetric.getVideostarttransactionid(), txt_blockchainid);
                      common.setspannable(getResources().getString(R.string.block_id), " " + arraycontainerformetric.getHashmethod(), txt_blockid);
                      common.setspannable(getResources().getString(R.string.block_number), " " + arraycontainerformetric.getValuehash(), txt_blocknumber);
                      common.setspannable(getResources().getString(R.string.metrichash), " " + arraycontainerformetric.getMetahash(), txt_metahash);

                          ArrayList<metricmodel> metricItemArraylist = arraycontainerformetric.getMetricItemArraylist();

                          for (int j = 0; j < metricItemArraylist.size(); j++) {
                              common.setgraphicalitems(metricItemArraylist.get(j).getMetricTrackKeyName(),
                                      metricItemArraylist.get(j).getMetricTrackValue(), true);

                              if (scrollview_meta.getVisibility() == View.VISIBLE)
                                  setmetadatavalue(metricItemArraylist.get(j));
                          }

                          if(((! latitude.trim().isEmpty()) && (! latitude.equalsIgnoreCase("NA"))) &&
                                  (! longitude.trim().isEmpty()) && (! longitude.equalsIgnoreCase("NA")))
                              populateUserCurrentLocation(new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude)));
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
                    xdata.getinstance().saveSetting(config.latency,data);
                }
                  //setmetricesgraphicaldata();

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
            maxincreasevideoduration=videoduration;
            playpausebutton.setImageResource(R.drawable.play_btn);
            playpausebutton.setVisibility(View.VISIBLE);
            mediaseekbar.setProgress(player.getCurrentPosition());

            if(layout_footer.getVisibility()==View.GONE && layout_photodetails.getVisibility()==View.GONE){
                showcontrollers();
                gethelper().drawerenabledisable(true,layout_footer,layout_mediatype,playpausebutton,null,img_fullscreen);
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
            position = 0;
        }
    }

    public void expand(final View v, int targetHeight) {

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
        valueAnimator.setDuration(100);
        valueAnimator.start();
    }

    public void collapse(final View v, int targetHeight) {

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
        valueAnimator.setDuration(100);
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

    public void updatemediainfo(String transactionid,String medianame,String medianotes)
    {
        databasemanager mdbhelper = new databasemanager(applicationviavideocomposer.getactivity());
        mdbhelper.createDatabase();

        try {
            mdbhelper.open();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        mdbhelper.updatemediainfofromstarttransactionid(transactionid,medianame,medianotes);
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
                    currentaudioduration =player.getCurrentPosition();  // suppose its on 4th pos means 4000

                videostarttime = player.getCurrentPosition();
                mediaseekbar.setProgress(player.getCurrentPosition());

                if (time_current != null)
                    time_current.setText(stringForTime(videostarttime));

                if(arraycontainerformetric != null)
                {
                    String color = "white";
                    if (arraycontainerformetric.getColor() != null && (!arraycontainerformetric.getColor().isEmpty()))
                        color = arraycontainerformetric.getColor();

                    switch (color) {
                        case "green":
                            layout_validating.setVisibility(View.VISIBLE);
                            txt_section_validating_secondary.setText(config.verified);
                            txt_section_validating_secondary.setBackgroundColor(Color.parseColor(arraycontainerformetric.getColor()));
                            break;
                        case "white":
                            layout_validating.setVisibility(View.GONE);
                            break;
                        case "red":
                            layout_validating.setVisibility(View.VISIBLE);
                            txt_section_validating_secondary.setText(config.caution);
                            txt_section_validating_secondary.setBackgroundColor(Color.parseColor(arraycontainerformetric.getColor()));
                            break;
                        case "yellow":
                            layout_validating.setVisibility(View.VISIBLE);
                            txt_section_validating_secondary.setText(config.caution);
                            txt_section_validating_secondary.setBackgroundColor(Color.parseColor(arraycontainerformetric.getColor()));
                            break;
                    }
                }

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

    public void setvideodata(){

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                endtime = player.getDuration();
                Log.e("endtime",""+endtime);
                videostarttime = player.getCurrentPosition();
                Log.e("videostarttime",""+videostarttime);

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


    private void setseekbarlayoutcolor(){
        try
        {
            linearseekbarcolorview.removeAllViews();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        for(int i=0 ; i<metricmainarraylist.size();i++)
        {
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,1.0f);
                param.leftMargin=0;

                View view = new View(getActivity());
                view.setLayoutParams(param);


                if(!metricmainarraylist.get(i).getColor().isEmpty() && metricmainarraylist.get(i).getColor() != null){
                    view.setBackgroundColor(Color.parseColor(metricmainarraylist.get(i).getColor()));
                }else{
                    view.setBackgroundColor(Color.parseColor("white"));
                }


                if(!metricmainarraylist.get(i).getLatency().isEmpty() && metricmainarraylist.get(i).getLatency() != null){
                   if(latency.isEmpty()){
                       latency = metricmainarraylist.get(i).getLatency();
                   }else{
                       latency = latency + "," + metricmainarraylist.get(i).getLatency();
                   }
                }
               /* if(i % 2 == 0){
                    view.setBackgroundResource(R.color.black);
                }else{
                    view.setBackgroundResource(R.color.red);
                    //
                }*/
                Log.e("setcolorcount =", ""+ i);
                linearseekbarcolorview.addView(view);
        }
        xdata.getinstance().saveSetting(config.latency,latency);
    }
}



