package com.deeptruth.app.android.fragments;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

import com.bumptech.glide.Glide;
import com.deeptruth.app.android.BuildConfig;
import com.deeptruth.app.android.R;
import com.deeptruth.app.android.adapter.folderdirectoryspinneradapter;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.database.databasemanager;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.models.arraycontainer;
import com.deeptruth.app.android.models.folder;
import com.deeptruth.app.android.models.metadatahash;
import com.deeptruth.app.android.models.metricmodel;
import com.deeptruth.app.android.models.wavevisualizer;
import com.deeptruth.app.android.utils.circularImageview;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.progressdialog;
import com.deeptruth.app.android.utils.xdata;
import com.deeptruth.app.android.views.customfonttextview;
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
 * A simple {@link Fragment} subclass.
 */
public class audioreaderfragment extends basefragment implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, View.OnTouchListener, View.OnClickListener {

    @BindView(R.id.img_delete_media)
    ImageView img_delete_media;
    @BindView(R.id.img_dotmenu)
    ImageView img_dotmenu;
    @BindView(R.id.img_folder)
    ImageView img_folder;
    @BindView(R.id.img_camera)
    ImageView img_camera;
    @BindView(R.id.img_arrow_back)
    ImageView img_arrow_back;
    @BindView(R.id.layout_drawer)
    LinearLayout layout_drawer;
    @BindView(R.id.layout_scrubberview)
    RelativeLayout layout_scrubberview;
    @BindView(R.id.linear_seekbarcolorview)
    LinearLayout linearseekbarcolorview;

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
    @BindView(R.id.txt_createdtime)
    TextView txt_createdtime;
    @BindView(R.id.layout_mediatype)
    RelativeLayout layout_mediatype;
    @BindView(R.id.layout_photodetails)
    RelativeLayout layout_photodetails;
    @BindView(R.id.scrollview_detail)
    ScrollView scrollview_detail;
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
    @BindView(R.id.layout_photoreader)
    RelativeLayout layout_photoreader;

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
    @BindView(R.id.txt_title_actionbarcomposer)
    TextView txt_title_actionbarcomposer;

    @BindView(R.id.layout_googlemap)
    LinearLayout layout_googlemap;
    @BindView(R.id.googlemap)
    FrameLayout googlemap;
    @BindView(R.id.img_compass)
    ImageView img_compass;
    @BindView(R.id.img_audiowave)
    ImageView img_audiowave;
    @BindView(R.id.img_verified)
    ImageView img_verified;
    @BindView(R.id.audio_container)
     RelativeLayout audiorootview;
    @BindView(R.id.layout_dtls)
    LinearLayout layout_dtls;

    GoogleMap mgooglemap;

    private String audiourl = null;

    private MediaPlayer player;
    private View rootview = null;
    private String selectedmetrics="";
    private ImageView righthandle;
    private LinearLayout linearLayout;
    private String keytype = config.prefs_md5;
    private long currentframenumber =0,playerposition=0;
    private long frameduration =15, mframetorecordcount =0;
    private Uri selectedvideouri =null;
    private boolean issurafcedestroyed=false;
    private Handler myHandler,handlerrecycler;
    private Runnable myRunnable,runnablerecycler;
    private long audioduration =0,maxincreasevideoduration=0, currentaudioduration =0, currentaudiodurationseconds =0;
    private boolean isdraweropen=false;
    private ArrayList<arraycontainer> metricmainarraylist = new ArrayList<>();
    public boolean imediacompleted =false;
    circularImageview playpausebutton;
    private TextView songName, time_current, time;
    StringBuilder mFormatBuilder;
    Formatter mFormatter;
    private SeekBar mediaseekbar;
    private int ontime =0, starttime =0, endtime =0, fTime = 5000, bTime = 5000;
    private Handler hdlr = new Handler();
    RelativeLayout rlcontrollerview;
    public int flingactionmindstvac;
    private  final int flingactionmindspdvac = 10;
    ArrayList<wavevisualizer> wavevisualizerslist =new ArrayList<>();
    private BroadcastReceiver getmetadatabroadcastreceiver;
    private float currentDegree = 0f;
    boolean ismediaplayer = false;
    String medianame = "",medianotes = "",mediafolder = "",mediatransectionid = "",latitude = "", longitude = "",screenheight = "",screenwidth = "",
            mediastartdevicedate = "",mediatime = "",mediasize="",lastsavedangle="",thumbnailurl="";
    adapteritemclick mcontrollernavigator;
    arraycontainer arraycontainerformetric =null;

    public audioreaderfragment() {
    }

    @Override
    public int getlayoutid() {

        return R.layout.fragment_audiotabreaderfrag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        if(rootview == null) {
            rootview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, rootview);

            gethelper().setrecordingrunning(false);
            linearLayout=rootview.findViewById(R.id.content);
         //   righthandle=rootview.findViewById(R.id.righthandle);
            playpausebutton = (circularImageview)rootview.findViewById(R.id.btn_playpause);
            mediaseekbar = (SeekBar) rootview.findViewById(R.id.mediacontroller_progress);
            time_current = (TextView) rootview.findViewById(R.id.time_current);
            time = (TextView) rootview.findViewById(R.id.time);
            rlcontrollerview = (RelativeLayout) rootview.findViewById(R.id.rl_controllerview);

            mediaseekbar.setPadding(0,0,0,0);

            mFormatBuilder = new StringBuilder();
            mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
            playpausebutton.setImageResource(R.drawable.play_btn);

            frameduration= common.checkframeduration();
            keytype=common.checkkey();

            playpausebutton.setOnClickListener(this);

            img_dotmenu.setOnClickListener(this);
            img_folder.setOnClickListener(this);
            img_camera.setOnClickListener(this);
            img_arrow_back.setOnClickListener(this);
            img_delete_media.setOnClickListener(this);

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

            mediaseekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                private int mProgressAtStartTracking=0;
                private final int SENSITIVITY=0;
                @Override
                public void onProgressChanged(final SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    mProgressAtStartTracking = seekBar.getProgress();
                    if(Math.abs(mProgressAtStartTracking - seekBar.getProgress()) <= SENSITIVITY){
                        // react to thumb click

                    }
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    player.seekTo(seekBar.getProgress());
                }
            });

            mediaseekbar.post(new Runnable() {
                @Override
                public void run() {

                    RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,mediaseekbar.getHeight());
                    parms.setMargins(15,5,15,0);
                    linearseekbarcolorview.setLayoutParams(parms);

                    Log.e("linearseekbarcolorview",""+mediaseekbar.getHeight());
                }
            });

            //tabs_detail
            tab_layout.setVisibility(View.VISIBLE);
            txtslotmedia.setText(getResources().getString(R.string.audio));
            img_share_media.setOnClickListener(this);
            img_edit_name.setOnClickListener(this);
            img_edit_notes.setOnClickListener(this);
            rlcontrollerview.setOnClickListener(this);
            scrollview_detail.setVisibility(View.VISIBLE);
            tab_layout.setVisibility(View.VISIBLE);
            layout_footer.setVisibility(View.VISIBLE);
            layout_photodetails.setVisibility(View.VISIBLE);
            layout_mediatype.setVisibility(View.VISIBLE);
            layout_duration.setVisibility(View.VISIBLE);
            layout_endtime.setVisibility(View.VISIBLE);
            layout_starttime.setVisibility(View.VISIBLE);


            edt_medianame.setEnabled(false);
            edt_medianame.setClickable(false);
            edt_medianame.setFocusable(false);
            edt_medianame.setFocusableInTouchMode(false);

            edt_medianotes.setEnabled(false);
            edt_medianotes.setClickable(false);
            edt_medianotes.setFocusable(false);
            edt_medianotes.setFocusableInTouchMode(false);

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
                        // if (arraymediaitemlist.size() > 0) {
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
                        //  if (arraymediaitemlist.size() > 0) {
                        String renamevalue = edt_medianame.getText().toString();
                        editabletext();


                        edt_medianame.setKeyListener(null);
                    }
                    // }
                    return false;
                }
            });
            layout_dtls.setOnClickListener(this);

            txtslotencyption.setOnClickListener(this);
            txtslotmeta.setOnClickListener(this);
            txtslotmedia.setOnClickListener(this);
            resetButtonViews(txtslotmedia, txtslotmeta, txtslotencyption);

            audiorootview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int heightDiff = audiorootview.getRootView().getHeight() - audiorootview.getHeight();
                    if (heightDiff > dpToPx(applicationviavideocomposer.getactivity().getApplication(), 200)) { // if more than 200 dp, it's probably a keyboard...
                        // ... do something here
                        Log.e("ikeyboardshowing","iskeyboardshowing....true");
                        rlcontrollerview.setVisibility(View.GONE);
                        layout_footer.setVisibility(View.GONE);

                    }else
                    {
                        if(rlcontrollerview.getVisibility()==View.GONE){
                            rlcontrollerview.setVisibility(View.VISIBLE);
                            layout_footer.setVisibility(View.VISIBLE);
                            edt_medianame.setFocusable(false);
                            edt_medianotes.setFocusable(false);
                        }

                        Log.e("ikeyboardshowing","iskeyboardshowing....false");
                    }
                }
            });
            loadmap();
            setmetriceshashesdata();
            setupaudiodata();
        }
        return rootview;
    }

    @Override
    public void initviews(View parent, Bundle savedInstanceState) {
        super.initviews(parent, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        ButterKnife.bind(this, parent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
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
                if (audiourl != null && (!audiourl.isEmpty()))
                    common.shareimage(getActivity(), audiourl);
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
                gethelper().onBack();
                break;

            case R.id.img_delete_media:
                showalertdialog(getActivity().getResources().getString(R.string.dlt_cnfm_audio));
                break;

            case R.id.rl_controllerview:
                Log.e("ontouch","ontouch");

                if(playpausebutton.getVisibility() == View.VISIBLE ){
                    playpausebutton.setVisibility(View.GONE);

                }else{
                    playpausebutton.setVisibility(View.VISIBLE);
                }

                break;
            case R.id.layout_dtls:
                Log.e("ontouch","ontouchscrollview");
                if(rlcontrollerview.getVisibility() == View.GONE){
                    view.clearFocus();
                    InputMethodManager immm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    immm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                }
                break;


            case R.id.btn_playpause:

                if(player.isPlaying()){
                    pause();
                }else{
                    start();
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
                            mcontrollernavigator.onItemClicked(audiourl,2);

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

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId())
        {
           /*  case  R.id.handle:
                flingswipe.onTouchEvent(motionEvent);
                break;

            case  R.id.righthandle:
                flingswipe.onTouchEvent(motionEvent);
                break;*/
        }
        return true;
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
        linearLayout.setVisibility(View.VISIBLE);
        rightswipe.start();
     //   righthandle.setVisibility(View.VISIBLE);
        rightswipe.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
               // righthandle.setImageResource(R.drawable.righthandle);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
             //   righthandle.setImageResource(R.drawable.lefthandle);
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
        leftswipe.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
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
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            if(! issurafcedestroyed)
                return;

            player = new MediaPlayer();
            if(audiourl != null && (! audiourl.isEmpty()))
            {
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.setDataSource(applicationviavideocomposer.getactivity(),selectedvideouri);
                player.prepareAsync();
                player.setOnPreparedListener(this);
                player.setOnCompletionListener(this);

                if(player!=null){
                    changeactionbarcolor();
                }

                if(! keytype.equalsIgnoreCase(common.checkkey()) || (frameduration != common.checkframeduration()))
                {
                    frameduration=common.checkframeduration();
                    keytype=common.checkkey();
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

    // Implement SurfaceHolder.Callback
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (player != null)
        {
            //holder.setFixedSize(1000,500);
            player.setDisplay(holder);
        }
        issurafcedestroyed=false;

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //   playerposition=0;
        if (player != null) {
            playerposition=player.getCurrentPosition();
            player.pause();
        }
        issurafcedestroyed=true;
    }

    @Override
    public void onPrepared(MediaPlayer mp)
    {
        imediacompleted =false;
        maxincreasevideoduration=0;

        audioduration =mp.getDuration();
        txt_duration.setText( common.gettimestring(mp.getDuration()));

        try {

            setaudiodata();

            if(playerposition > 0)
            {
                mp.seekTo((int)playerposition);
                player.seekTo((int)playerposition);
            }
            else
            {
                mp.seekTo(100);
            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }

        getmediadata();
    }

    public void start() {
        if(player != null)
        {
            if(audiourl!=null)
            {
                playpausebutton.setImageResource(R.drawable.pause);
                player.start();
                hdlr.postDelayed(seekbarupdatorhandler, 10);
                player.setOnCompletionListener(this);
            }
        }
    }


    public void pause() {
        if(player != null){
                player.pause();
                playpausebutton.setImageResource(R.drawable.play_btn);
        }
    }

    @Override
    public void onHeaderBtnClick(int btnid) {
        super.onHeaderBtnClick(btnid);
        switch (btnid){
            case R.id.img_share_icon:
                if(audiourl != null && (! audiourl.isEmpty()))
                    common.shareaudio(getActivity(), audiourl);
                break;
            case R.id.img_menu:

                gethelper().onBack();

                break;
            case R.id.img_setting:

                break;
        }
    }

    public void parsemetadata(String metadata,String hashmethod,String videostarttransactionid,String hashvalue,String metahash,
                              String color) {
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
                metricmainarraylist.add(new arraycontainer(metricItemArraylist,hashmethod,videostarttransactionid,hashvalue,metahash,color));
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
                    metricmainarraylist.add(new arraycontainer(metricItemArraylist,hashmethod,videostarttransactionid,hashvalue,metahash,color));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setupaudioplayer(final Uri selecteduri)
    {
        try {
            player = new MediaPlayer();

            if(selecteduri!=null){
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.setDataSource(applicationviavideocomposer.getactivity(),selecteduri);

                player.prepareAsync();
                player.setOnPreparedListener(this);
                player.setOnCompletionListener(this);

                if(player!=null){
                    changeactionbarcolor();
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
                (R.color.dark_blue_solid));
    }


    public void setmetriceshashesdata()
    {
        if(myHandler != null && myRunnable != null)
            myHandler.removeCallbacks(myRunnable);

        myHandler=new Handler();
        myRunnable = new Runnable() {
            @Override
            public void run() {

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
        currentduration=currentduration/1000;

        if(metricmainarraylist.size() == 0 || currentduration == 0)
            return;

        currentduration=(currentduration*30);
        currentduration=currentduration/frameduration;

        int n=(int)currentduration;
        if(n> metricmainarraylist.size() || imediacompleted)
            n=metricmainarraylist.size();

        for(int i=0; i < n ;i++)
        {
            if (! metricmainarraylist.get(i).isIsupdated())
            {
                arraycontainerformetric = new arraycontainer();
                arraycontainerformetric = metricmainarraylist.get(i);
                selectedmetrics = selectedmetrics + "\n";
            }
        }
        if (((!latitude.trim().isEmpty()) && (!latitude.equalsIgnoreCase("NA"))) &&
                (!longitude.trim().isEmpty()) && (!longitude.equalsIgnoreCase("NA")))
            populateUserCurrentLocation(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)));
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        imediacompleted =true;
        maxincreasevideoduration= audioduration;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(player != null )
                {
                    player.seekTo(0);
                        wavevisualizerslist.clear();

                    playpausebutton.setImageResource(R.drawable.play_btn);
                }
            }
        },200);

    }

    private Runnable seekbarupdatorhandler = new Runnable() {
        @Override
        public void run() {
            if(player != null )
            {
                if(player.getCurrentPosition() > maxincreasevideoduration)
                    maxincreasevideoduration=player.getCurrentPosition();

                if(currentaudioduration == 0 || (player.getCurrentPosition() > currentaudioduration))
                {
                    currentaudioduration =player.getCurrentPosition();  // suppose its on 4th pos means 4000
                    currentaudiodurationseconds = currentaudioduration /1000;  // Its 4
                }

                starttime = player.getCurrentPosition();
                mediaseekbar.setProgress(player.getCurrentPosition());

                if (time_current != null)
                    time_current.setText(stringForTime(starttime));

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
       endtime = player.getDuration();
       starttime = player.getCurrentPosition();

       audioduration  = endtime;

       if (time != null)
           time.setText(stringForTime(endtime));
       if (time_current != null)
           time_current.setText(stringForTime(starttime));

       mediaseekbar.setMax(endtime);
       mediaseekbar.setProgress(starttime);
   }

   public void setupaudiodata() {
       audiourl = xdata.getinstance().getSetting("selectedaudiourl");
       if (audiourl != null && (!audiourl.isEmpty())) {

           audioduration = 0;
           playpausebutton.setImageResource(R.drawable.play_btn);
           rlcontrollerview.setVisibility(View.VISIBLE);
           playerposition = 0;
        //   righthandle.setVisibility(View.VISIBLE);

           setupaudioplayer(Uri.parse(audiourl));
           tvsize.setText(common.filesize(audiourl));
       }
   }


    @Override
    public void onStart() {
        super.onStart();
        registerbroadcastreciver();
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
    }

    public void registerbroadcastreciver()
    {
        IntentFilter intentFilter = new IntentFilter(config.composer_service_savemetadata);
        getmetadatabroadcastreceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Thread thread = new Thread() {
                    public void run() {
                               getmediadata();
                    }
                };
                thread.start();
            }
        };
        getActivity().registerReceiver(getmetadatabroadcastreceiver, intentFilter);
    }

    public void getmediametadata()
    {
        if(audiourl != null && (! audiourl.isEmpty())) {
            File file = new File(audiourl);
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
                    Cursor mediainfocursor = mdbhelper.getmediainfobyfilename(common.getfilename(audiourl));
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
                                        String color = "" + metadatacursor.getString(metadatacursor.getColumnIndex("color"));


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
                                            metricmainarraylist.add(new arraycontainer(metricItemArraylist,hashmethod,
                                                    videostarttransactionid,sequencehash,metahash,color));
                                        }catch (Exception e)
                                        {
                                            e.printStackTrace();
                                        }
                                    } while (metadatacursor.moveToNext());
                                }
                            }
                        }
                    }

                   /* applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setseekbarlayoutcolor();
                        }
                    });*/

                    try {
                        mdbhelper.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public void getmediadata()
    {
        try {
            databasemanager mdbhelper = null;
            String audiostatus = "";
            if (mdbhelper == null) {
                mdbhelper = new databasemanager(applicationviavideocomposer.getactivity());
                mdbhelper.createDatabase();
            }

            try {
                mdbhelper.open();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Cursor cur = mdbhelper.getstartmediainfo(common.getfilename(audiourl));
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
                    thumbnailurl = "" + cur.getString(cur.getColumnIndex("thumbnailurl"));

                }while(cur.moveToNext());
            }

            if (!mediacompleteddate.isEmpty()){


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


                img_verified.setVisibility(View.VISIBLE);
                ArrayList<metadatahash> mitemlist=mdbhelper.getmediametadatabyfilename(common.getfilename(audiourl));
                if(metricmainarraylist.size()>0){

                    for(int i=0;i<mitemlist.size();i++)
                    {
                        String sequencehash = mitemlist.get(i).getSequencehash();
                        String hashmethod = mitemlist.get(i).getHashmethod();
                        String videostarttransactionid = mitemlist.get(i).getVideostarttransactionid();
                        String serverdictionaryhash = mitemlist.get(i).getValuehash();
                        String color = mitemlist.get(i).getColor();
                        metricmainarraylist.set(i,new arraycontainer(hashmethod,videostarttransactionid,sequencehash,
                                serverdictionaryhash,color));
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

                        parsemetadata(metricdata,hashmethod,videostarttransactionid,sequencehash,serverdictionaryhash,color);
                    }


                  //  txt_duration.setText("00:00:02:17.3");


                    if((!mediastartdevicedate.isEmpty()) && (!mediacompleteddate.isEmpty() && mediacompleteddate!= null)){

                        applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if(! thumbnailurl.trim().isEmpty() && new File(thumbnailurl).exists())
                                {
                                    Uri uri= FileProvider.getUriForFile(applicationviavideocomposer.getactivity(),
                                            BuildConfig.APPLICATION_ID + ".provider", new File(thumbnailurl));
                                    Glide.with(applicationviavideocomposer.getactivity()).
                                            load(uri).
                                            thumbnail(0.1f).
                                            into(img_audiowave);
                                }
                            }
                        });
                    }
                    applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            edt_medianame.setText(medianame);
                        }
                    });

                    if(!medianotes.isEmpty()){
                        applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                edt_medianotes.setText(medianotes);
                            }
                        });
                    }
                }

                /*applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setseekbarlayoutcolor();
                    }
                });*/

                try
                {
                    mdbhelper.close();
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else
            {

            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
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
                                if(! folderpath.equalsIgnoreCase(new File(audiourl).getParent()))
                                {
                                    common.copyfile(new File(audiourl),new File(folderpath));
                                    //common.delete(new File(sourcefilepath));
                                    updatefilemediafolderdirectory(audiourl,folderpath);
                                }
                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                            applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressdialog.dismisswaitdialog();
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

    public void updatefilemediafolderdirectory(String sourcefile,String destinationmediafolder)
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

        try
        {
            mdbhelper.updatefilemediafolderdir(sourcefile,destinationmediafolder);

            mdbhelper.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void getmetadetareader(){
        myHandler=new Handler();
        myRunnable = new Runnable() {
            @Override
            public void run() {

                if(metricmainarraylist.size() == 0)
                    getmediametadata();

                myHandler.postDelayed(this, 3000);
            }
        };
        myHandler.post(myRunnable);
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

    public void setdata(adapteritemclick mcontrollernavigator) {
        this.mcontrollernavigator = mcontrollernavigator;
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

               /* if(i % 2 == 0){
                    view.setBackgroundResource(R.color.black);
                }else{
                    view.setBackgroundResource(R.color.red);
                    //
                }*/
            Log.e("setcolorcount =", ""+ i);
            linearseekbarcolorview.addView(view);

        }
    }
}