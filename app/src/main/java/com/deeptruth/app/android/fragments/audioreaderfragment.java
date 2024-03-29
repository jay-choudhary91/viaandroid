package com.deeptruth.app.android.fragments;


import android.content.Context;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

import com.bumptech.glide.Glide;
import com.deeptruth.app.android.BuildConfig;
import com.deeptruth.app.android.R;
import com.deeptruth.app.android.activity.baseactivity;
import com.deeptruth.app.android.adapter.encryptiondataadapter;
import com.deeptruth.app.android.adapter.folderdirectoryspinneradapter;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.database.databasemanager;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.interfaces.apiresponselistener;
import com.deeptruth.app.android.models.arraycontainer;
import com.deeptruth.app.android.models.customedittext;
import com.deeptruth.app.android.models.folder;
import com.deeptruth.app.android.models.metadatahash;
import com.deeptruth.app.android.models.metricmodel;
import com.deeptruth.app.android.utils.LinearLayoutManagerWithSmoothScroller;
import com.deeptruth.app.android.utils.circularImageview;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.progressdialog;
import com.deeptruth.app.android.utils.simpledivideritemdecoration;
import com.deeptruth.app.android.utils.taskresult;
import com.deeptruth.app.android.utils.xdata;
import com.github.mikephil.charting.utils.Utils;

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.VISIBLE;
import static android.widget.RelativeLayout.TRUE;

/**
 * A simple {@link Fragment} subclass.
 */
public class audioreaderfragment extends basefragment implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, View.OnTouchListener, View.OnClickListener ,customedittext.OnKeyListener{
    @BindView(R.id.img_dotmenu)
    ImageView img_dotmenu;
    @BindView(R.id.img_folder)
    ImageView img_folder;
    @BindView(R.id.img_camera)
    ImageView img_camera;
    @BindView(R.id.img_arrow_back)
    ImageView img_arrow_back;
    @BindView(R.id.layout_scrubberview)
    RelativeLayout layout_scrubberview;
    @BindView(R.id.linear_seekbarcolorview)
    LinearLayout linearseekbarcolorview;

    @BindView(R.id.txt_videoupdatetransactionid)
    TextView txt_blockchainid;
    @BindView(R.id.txt_hash_formula)
    TextView txt_blockid;
    @BindView(R.id.txt_data_hash)
    TextView txt_blocknumber;
    @BindView(R.id.txt_dictionary_hash)
    TextView txt_metahash;
    @BindView(R.id.scrollview_encyption)
    ScrollView scrollView_encyrption;
    @BindView(R.id.scrollview_meta)
    ScrollView scrollview_meta;

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
    @BindView(R.id.layout_audiodetails)
    public RelativeLayout layout_audiodetails;
    @BindView(R.id.scrollview_detail)
    ScrollView scrollview_detail;
    @BindView(R.id.edt_medianame)
    customedittext edt_medianame;
    @BindView(R.id.edt_medianotes)
    customedittext edt_medianotes;
    @BindView(R.id.tab_layout)
    LinearLayout tab_layout;
    @BindView(R.id.txt_title_actionbarcomposer)
    TextView txt_title_actionbarcomposer;
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

    @BindView(R.id.img_audiowave)
    ImageView img_audiowave;
    @BindView(R.id.img_verified)
    ImageView img_verified;
    @BindView(R.id.audio_container)
    RelativeLayout audiorootview;
    @BindView(R.id.layout_dtls)
    LinearLayout layout_dtls;
    @BindView(R.id.audio_downwordarrow)
    ImageView audio_downwordarrow;
    @BindView(R.id.rl_audio_downwordarrow)
    RelativeLayout rl_audio_downwordarrow;
    @BindView(R.id.img_fullscreen)
    ImageView img_fullscreen;
    @BindView(R.id.layout_audiowave)
    RelativeLayout layout_audiowave;
    @BindView(R.id.img_pause)
    ImageView img_pause;
    @BindView(R.id.layout_seekbartiming)
    RelativeLayout layout_seekbartiming;
    @BindView(R.id.layout_item_encryption)
    LinearLayout layout_item_encryption;
    @BindView(R.id.recycler_encryption)
    RecyclerView recycler_encryption;
    @BindView(R.id.metainfocontainer)
    FrameLayout metainfocontainer;
    @BindView(R.id.progressmediasync)
    ProgressBar progressmediasync;
    @BindView(R.id.img_colapseicon)
    ImageView img_colapseicon;

    private String mediafilepath = "",medianame = "",medianotes = "",mediaduration="",mediafolder = "",localkey = "",mediaid="0",
            thumbnailurl="",mediatoken="",sync_date="",keytype = config.prefs_md5;
    private int rootviewheight , audioviewheight,audiodetailviewheight ,mediatypeheight,starttime =0, endtime =0,
            flingactionmindspdvac = 10,flingactionmindstvac=0,currentprocessframe=0,footerheight=0,navigationbarheight = 0,updatemetaattempt=0;
    private long audioduration =0,maxincreasevideoduration=0, currentaudioduration =0,playerposition=0,frameduration =15;
    public boolean islastdragarrow = false,isplaypauswebtnshow = false,issurafcedestroyed=false,ismediacompleted =false;
    private MediaPlayer player;
    private View rootview = null;
    private LinearLayout linearLayout;
    private Uri selectedvideouri =null;
    private Handler myHandler;
    private Runnable myRunnable;

    private ArrayList<arraycontainer> metricmainarraylist = new ArrayList<>();
    private ArrayList<arraycontainer> encryptionarraylist = new ArrayList<>();
    private circularImageview playpausebutton;
    private TextView time_current, time;
    private SeekBar mediaseekbar;
    private Handler hdlr = new Handler();
    private RelativeLayout rlcontrollerview;
    private adapteritemclick mcontrollernavigator;
    private arraycontainer arraycontainerformetric =null;
    private encryptiondataadapter encryptionadapter;
    private GestureDetector detector;

    private metainformationfragment fragmentmetainformation;

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
           // setheadermargin();
            navigationbarheight =  common.getnavigationbarheight();
            setfooterlayout();
            gethelper().setdatacomposing(false);

            gethelper().setwindowfitxy(true);
            loadviewdata();

            audio_downwordarrow.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    switch (view.getId())
                    {
                        case  R.id.audio_downwordarrow:
                            detector.onTouchEvent(event);


                            break;
                    }

                    return false;
                }
            });

            audio_downwordarrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });



            detector = new GestureDetector(applicationviavideocomposer.getactivity(),new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                        float distanceX, float distanceY) {
                    if(e2.getY() > 0)
                    {
                        int lovermovementlimit = rootviewheight - (footerheight +30);
                        islastdragarrow =false;
                        gethelper().drawerenabledisable(false);
                       // int uppermovementlimit = (rootviewheight /100)*35;

                        if(rlcontrollerview.getLayoutParams().height < lovermovementlimit){

                            float height = rlcontrollerview.getLayoutParams().height + e2.getY();
                            //setanimation(layouttop,height,e2.getY(),false);
                            rlcontrollerview.getLayoutParams().height = (int)height;
                            rlcontrollerview.requestLayout();
                            rlcontrollerview.animate().setDuration(500);

                            float bottomlayoutheight = layout_audiodetails.getLayoutParams().height - e2.getY();
                            layout_audiodetails.getLayoutParams().height = (int) bottomlayoutheight;
                            layout_audiodetails.requestLayout();
                            layout_audiodetails.animate().setDuration(500);

                        }else{


                            islastdragarrow =true;
                            gethelper().drawerenabledisable(true);
                            if(layout_audiodetails.getLayoutParams().height<=0){
                                layout_audiodetails.getLayoutParams().height = layout_audiodetails.getHeight();
                                layout_audiodetails.requestLayout();
                            }
                        }
                    }
                    else
                    {
                        int uppermovementlimit = (rootviewheight /100)*35;
                        islastdragarrow =false;
                        gethelper().drawerenabledisable(false);
                        if(rlcontrollerview.getLayoutParams().height >= uppermovementlimit) {
                            float height = rlcontrollerview.getLayoutParams().height - (Math.abs(e2.getY()));
                            rlcontrollerview.getLayoutParams().height = (int) height;
                            rlcontrollerview.requestLayout();
                            rlcontrollerview.animate().setDuration(500);

                            if(layout_audiodetails.getLayoutParams().height > 0){

                                float bottomlayoutheight = layout_audiodetails.getLayoutParams().height + (Math.abs(e2.getY()));
                                layout_audiodetails.getLayoutParams().height = (int) bottomlayoutheight;
                                layout_audiodetails.requestLayout();
                                layout_audiodetails.animate().setDuration(500);
                            }
                        }else{
                        }
                    }


                    return false;
                }

                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {


                    if(rootviewheight > rlcontrollerview.getLayoutParams().height && !islastdragarrow){
                        islastdragarrow =true;
                        gethelper().drawerenabledisable(true);
                        rlcontrollerview.getLayoutParams().height = rootviewheight;
                        rlcontrollerview.requestLayout();
                        rlcontrollerview.animate().setDuration(500);

                    }else{
                        islastdragarrow =false;
                        gethelper().drawerenabledisable(false);
                        rlcontrollerview.getLayoutParams().height = audioviewheight;
                        layout_audiodetails.getLayoutParams().height = audiodetailviewheight;
                    }

                    return super.onSingleTapConfirmed(e);
                }

                @Override
                public boolean onDown(MotionEvent e) {
                    return false;
                }

            }
            );
        }
        return rootview;
    }

    public void loadviewdata()
    {
        gethelper().setrecordingrunning(false);

        layout_item_encryption.setVisibility(View.GONE);
        metainfocontainer.setVisibility(VISIBLE);
        recycler_encryption.setVisibility(View.VISIBLE);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(applicationviavideocomposer.getactivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler_encryption.setLayoutManager(new LinearLayoutManagerWithSmoothScroller(applicationviavideocomposer.getactivity()));
        recycler_encryption.addItemDecoration(new simpledivideritemdecoration(applicationviavideocomposer.getactivity()));

        encryptionadapter = new encryptiondataadapter(encryptionarraylist, applicationviavideocomposer.getactivity());
        recycler_encryption.setAdapter(encryptionadapter);

        linearLayout=rootview.findViewById(R.id.content);
        //   righthandle=rootview.findViewById(R.id.righthandle);
        playpausebutton = (circularImageview)rootview.findViewById(R.id.btn_playpause);
        mediaseekbar = (SeekBar) rootview.findViewById(R.id.mediacontroller_progress);
        time_current = (TextView) rootview.findViewById(R.id.time_current);
        time = (TextView) rootview.findViewById(R.id.time);
        rlcontrollerview = (RelativeLayout) rootview.findViewById(R.id.rl_controllerview);

        audiorootview.post(new Runnable() {
            @Override
            public void run() {
                rootviewheight = audiorootview.getHeight();
                mediatypeheight = layout_mediatype.getHeight();
                audioviewheight = ((rootviewheight * 60 )/100);
                rlcontrollerview.getLayoutParams().height = audioviewheight;
                rlcontrollerview.setVisibility(View.VISIBLE);
                rlcontrollerview.requestLayout();

                addbottommargin(mediatypeheight);
                audiodetailviewheight = (rootviewheight - (audioviewheight+navigationbarheight));
                layout_audiodetails.getLayoutParams().height = audiodetailviewheight;
                layout_audiodetails.setVisibility(View.VISIBLE);
                layout_audiodetails.requestLayout();


                setupaudiodata();
            }
        });
        recenterplaypause(0);
        mediaseekbar.setPadding(0,0,0,0);
        playpausebutton.setImageResource(R.drawable.play_btn);

        frameduration= common.checkframeduration();
        keytype=common.checkkey();

        playpausebutton.setOnClickListener(this);
        img_share_media.setVisibility(VISIBLE);
        img_dotmenu.setOnClickListener(this);
        img_folder.setOnClickListener(this);
        img_camera.setOnClickListener(this);
        img_arrow_back.setOnClickListener(this);
        img_fullscreen.setOnClickListener(this);
        img_pause.setOnClickListener(this);

        img_dotmenu.setVisibility(View.VISIBLE);
        img_folder.setVisibility(View.GONE);
        img_arrow_back.setVisibility(View.VISIBLE);

        if(BuildConfig.FLAVOR.contains(config.build_flavor_reader))
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
                if(progress > 0)
                {
                    int processframe=0;
                    int progresspercentage = (progress*100)/mediaseekbar.getMax();
                    if(progresspercentage > 0)
                        processframe =(int) (metricmainarraylist.size()*progresspercentage)/100;

                    if(ismediacompleted)
                        processframe=metricmainarraylist.size()-1;

                    if(currentprocessframe != processframe)
                    {
                        currentprocessframe=processframe;
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

                        if(encryptionadapter != null && recycler_encryption!= null)
                            recycler_encryption.smoothScrollToPosition(currentprocessframe);
                    }

                    gethelper().setcurrentmediaposition(currentprocessframe);
                     if(fragmentmetainformation != null)
                        fragmentmetainformation.setcurrentmediaposition(currentprocessframe);
                }
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
                RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        mediaseekbar.getHeight());
                parms.setMargins(20,0,20,0);
                linearseekbarcolorview.setLayoutParams(parms);
                progressmediasync.setLayoutParams(parms);
            }
        });

        audiorootview.post(new Runnable() {
            @Override
            public void run() {

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
        layout_audiodetails.setVisibility(View.VISIBLE);
        layout_mediatype.setVisibility(View.VISIBLE);
        layout_duration.setVisibility(View.VISIBLE);
        layout_endtime.setVisibility(View.VISIBLE);
        layout_starttime.setVisibility(View.VISIBLE);

        edt_medianame.setEnabled(false);
        edt_medianame.setClickable(false);
        edt_medianame.setFocusable(false);
        edt_medianame.setFocusableInTouchMode(false);

        edt_medianame.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId & EditorInfo.IME_MASK_ACTION) != 0) {
                    edt_medianame.setEnabled(false);
                    edt_medianame.setClickable(false);
                    edt_medianame.setFocusable(false);
                    edt_medianame.setFocusableInTouchMode(false);
                    edt_medianame.setKeyListener(null);
                    editabletext();
                    showaudioplayer();
                    return true;
                }
                else {
                    return false;
                }
            }
        });
        edt_medianotes.setClickable(false);
        edt_medianotes.setFocusable(false);
        edt_medianotes.setFocusableInTouchMode(false);

        edt_medianotes.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    v.setFocusable(false);
                    gethelper().setwindowfitxy(true);
                    InputMethodManager imm = (InputMethodManager) applicationviavideocomposer.getactivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edt_medianame.getWindowToken(), 0);

                    callupdatemetaapi(mediaid,edt_medianame.getText().toString().trim(),edt_medianotes.getText().toString().trim());
                }
            }
        });

        edt_medianame.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    edt_medianame.setKeyListener(null);
                    v.setFocusable(false);
                    gethelper().setwindowfitxy(true);
                    InputMethodManager imm = (InputMethodManager) applicationviavideocomposer.getactivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edt_medianame.getWindowToken(), 0);
                    // if (arraymediaitemlist.size() > 0) {
                    String renamevalue = edt_medianame.getText().toString();

                    callupdatemetaapi(mediaid,edt_medianame.getText().toString().trim(),edt_medianotes.getText().toString().trim());

                    editabletext();
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

        layout_dtls.setOnClickListener(this);
        txtslotencyption.setOnClickListener(this);
        txtslotmeta.setOnClickListener(this);
        txtslotmedia.setOnClickListener(this);
        img_colapseicon.setOnClickListener(this);
        resetButtonViews(txtslotmedia, txtslotmeta, txtslotencyption);

        setmetriceshashesdata();
    }

    @Override
    public void initviews(View parent, Bundle savedInstanceState) {
        super.initviews(parent, savedInstanceState);
        applicationviavideocomposer.getactivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
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
                showaudioplayer();
                resetButtonViews(txtslotmeta, txtslotmedia, txtslotencyption);
                scrollview_meta.setVisibility(View.VISIBLE);
                scrollView_encyrption.setVisibility(View.INVISIBLE);
                scrollview_detail.setVisibility(View.INVISIBLE);
                //metainfo fragment

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
                            fragmentmetainformation.setdatacomposing(false,xdata.getinstance().getSetting(config.selectedaudiourl));
                            fragmentmetainformation.setcurrentmediaposition(currentprocessframe);
                        }
                    }
                },500);

                break;
            case R.id.txt_slot6:
                showaudioplayer();
                resetButtonViews(txtslotencyption, txtslotmedia, txtslotmeta);
                scrollView_encyrption.setVisibility(View.VISIBLE);
                scrollview_detail.setVisibility(View.INVISIBLE);
                scrollview_meta.setVisibility(View.INVISIBLE);
                break;
            case R.id.img_edit_name:
                visiblefocus(edt_medianame);
                InputMethodManager imm = (InputMethodManager) applicationviavideocomposer.getactivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 1);
                break;
            case R.id.img_edit_notes:
                visiblefocus(edt_medianotes);
                InputMethodManager imn = (InputMethodManager) applicationviavideocomposer.getactivity().getSystemService(Context.INPUT_METHOD_SERVICE);
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
                if (mediafilepath != null && (!mediafilepath.isEmpty()) && (! thumbnailurl.trim().isEmpty()))
                    baseactivity.getinstance().preparesharedialogfragment(mediafilepath,mediatoken,config.type_audio,thumbnailurl);

                    //gethelper().showsharepopupsub(mediafilepath,"audio",mediatoken,false);
                    //common.shareaudio(applicationviavideocomposer.getactivity(), mediafilepath);
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
            case R.id.img_colapseicon:
                resizeviewtohalfscreen();
                break;

            case R.id.img_fullscreen:
                  if(layout_audiodetails.getVisibility() == View.VISIBLE){
                      removebottommargin();
                      setbottomimgview();
                      recenterplaypause(1);
                      rlcontrollerview.getLayoutParams().height = (rootviewheight - navigationbarheight);
                      layout_audiodetails.getLayoutParams().height = 0;
                      layout_audiodetails.setVisibility(View.GONE);
                      audio_downwordarrow.setVisibility(View.GONE);
                      rl_audio_downwordarrow.setVisibility(View.GONE);
                      gethelper().updateactionbar(1);
                      gethelper().drawerenabledisable(true);
                      layout_mediatype.setVisibility(View.VISIBLE);
                      layout_scrubberview.setVisibility(View.VISIBLE);
                      layout_scrubberview.setBackgroundColor(getResources().getColor(R.color.whitetransparent));

                      linearseekbarcolorview.setVisibility(View.VISIBLE);
                      mediaseekbar.setVisibility(View.VISIBLE);
                      layout_seekbartiming.setVisibility(View.VISIBLE);

                      if(player != null && player.isPlaying()){
                          isplaypauswebtnshow = true;
                          img_fullscreen.setVisibility(View.GONE);
                          playpausebutton.setVisibility(View.GONE);
                          img_pause.setImageResource(R.drawable.ic_pause);
                          img_colapseicon.setVisibility(View.VISIBLE);
                          img_pause.setVisibility(View.VISIBLE);
                      }else{
                          playpausebutton.setVisibility(View.VISIBLE);
                          img_fullscreen.setImageResource(R.drawable.ic_info_mode);
                          img_colapseicon.setVisibility(View.GONE);
                          img_fullscreen.setVisibility(View.VISIBLE);
                      }

                      islastdragarrow =false;

                  }else{
                      resizeviewtohalfscreen();
                  }
                break;
            case R.id.img_pause:

                if(layout_audiodetails.getVisibility()==View.GONE){
                    if(player != null && player.isPlaying()){
                        pause();
                        img_pause.setImageResource(R.drawable.ic_play);
                        playpausebutton.setVisibility(View.GONE);
                        img_colapseicon.setVisibility(View.GONE);
                        img_fullscreen.setImageResource(R.drawable.ic_info_mode);
                        img_fullscreen.setVisibility(View.VISIBLE);
                        layout_scrubberview.setVisibility(View.VISIBLE);
                        layout_scrubberview.setBackgroundColor(getResources().getColor(R.color.whitetransparent));
                        linearseekbarcolorview.setVisibility(View.VISIBLE);
                        mediaseekbar.setVisibility(View.VISIBLE);
                        layout_seekbartiming.setVisibility(View.VISIBLE);
                        img_pause.setVisibility(View.VISIBLE);
                        gethelper().updateactionbar(1);
                        setbottomimgview();
                        layout_mediatype.setVisibility(View.VISIBLE);
                        getcontrollerheight();
                    }else{
                        start();
                        img_pause.setImageResource(R.drawable.ic_pause);
                        layout_mediatype.setVisibility(View.GONE);
                        layout_scrubberview.setVisibility(View.VISIBLE);
                        img_colapseicon.setVisibility(View.VISIBLE);
                        linearseekbarcolorview.setVisibility(View.VISIBLE);
                        playpausebutton.setVisibility(View.GONE);
                        mediaseekbar.setVisibility(View.VISIBLE);
                        layout_seekbartiming.setVisibility(View.VISIBLE);
                        img_pause.setVisibility(VISIBLE);
                        img_fullscreen.setVisibility(View.GONE);
                        gethelper().updateactionbar(0);
                        gethelper().drawerenabledisable(true);
                        layout_scrubberview.setBackgroundColor(getResources().getColor(R.color.whitetransparent));
                        getcontrollerheight();
                    }
                }
                break;
            case R.id.audio_downwordarrow:

                break;

            case R.id.rl_controllerview:
                if(layout_audiodetails.getVisibility()==View.GONE)  // Full screen view
                {
                    if(layout_mediatype.getVisibility()==View.GONE)  // Action bar is hidden
                    {
                        setimageview();
                        gethelper().updateactionbar(1);

                        layout_scrubberview.setBackgroundColor(getResources().getColor(R.color.whitetransparent));
                        layout_mediatype.setVisibility(View.VISIBLE);

                        if(player != null && (! player.isPlaying()))  // Player is pause
                        {
                            gethelper().updateactionbar(1);
                            img_fullscreen.setVisibility(View.VISIBLE);
                            layout_scrubberview.setBackgroundColor(getResources().getColor(R.color.whitetransparent));
                            gethelper().drawerenabledisable(true);
                            layout_scrubberview.setVisibility(View.VISIBLE);
                            linearseekbarcolorview.setVisibility(View.VISIBLE);
                            mediaseekbar.setVisibility(View.VISIBLE);
                            layout_seekbartiming.setVisibility(View.VISIBLE);
                            gethelper().updateactionbar(1);
                            layout_scrubberview.setBackgroundColor(getResources().getColor(R.color.whitetransparent));
                            img_fullscreen.setImageResource(R.drawable.ic_info_mode);
                            layout_mediatype.setVisibility(View.VISIBLE);
                            setbottomimgview();

                            if(!isplaypauswebtnshow){
                                playpausebutton.setVisibility(View.VISIBLE);
                            }else{
                                img_pause.setVisibility(VISIBLE);
                            }
                        }
                        else   // Player is playing
                        {
                            gethelper().updateactionbar(0);
                            gethelper().drawerenabledisable(true);
                            layout_mediatype.setVisibility(View.GONE);
                            img_fullscreen.setVisibility(View.GONE);
                            audio_downwordarrow.setVisibility(View.GONE);
                            rl_audio_downwordarrow.setVisibility(View.GONE);
                            playpausebutton.setVisibility(View.GONE);
                            layout_scrubberview.setVisibility(View.VISIBLE);
                            linearseekbarcolorview.setVisibility(View.VISIBLE);
                            mediaseekbar.setVisibility(View.VISIBLE);
                            layout_seekbartiming.setVisibility(View.VISIBLE);
                            img_pause.setVisibility(View.VISIBLE);
                            layout_scrubberview.setBackgroundColor(getResources().getColor(R.color.whitetransparent));
                            getcontrollerheight();
                        }
                    }
                    else  // Action bar is showing
                    {
                        playpausebutton.setVisibility(View.GONE);
                        audio_downwordarrow.setVisibility(View.GONE);
                        rl_audio_downwordarrow.setVisibility(View.GONE);

                        img_fullscreen.setVisibility(View.GONE);
                        layout_mediatype.setVisibility(View.GONE);
                        img_fullscreen.setImageResource(R.drawable.ic_info_mode);

                        if(player != null && (! player.isPlaying()))
                        {
                            if(islastdragarrow){
                                return;
                            }else{
                                gethelper().updateactionbar(0);
                                layout_audiodetails.setVisibility(View.GONE);
                                img_fullscreen.setVisibility(View.GONE);
                                audio_downwordarrow.setVisibility(View.GONE);
                                rl_audio_downwordarrow.setVisibility(View.GONE);
                                layout_scrubberview.setVisibility(View.GONE);
                                gethelper().drawerenabledisable(false);

                            }
                        }
                        else
                        {
                            if(islastdragarrow){
                                return;
                            }else{
                                gethelper().updateactionbar(0);
                                layout_mediatype.setVisibility(View.GONE);
                                gethelper().drawerenabledisable(false);
                                layout_scrubberview.setVisibility(View.GONE);
                                audio_downwordarrow.setVisibility(View.GONE);

                            }
                        }
                    }
                }
                else {

                    if(playpausebutton.getVisibility() == View.GONE ){
                        playpausebutton.setVisibility(View.VISIBLE);
                    }else{
                        playpausebutton.setVisibility(View.GONE);
                    }

                    if(player != null && !player.isPlaying()){
                        playpausebutton.setVisibility(View.VISIBLE);
                    }

                    audio_downwordarrow.setVisibility(View.VISIBLE);
                    rl_audio_downwordarrow.setVisibility(View.VISIBLE);
                    img_fullscreen.setVisibility(View.VISIBLE);
                    img_fullscreen.setImageResource(R.drawable.ic_full_screen_mode);
                }

                break;
            case R.id.layout_dtls:
                if(rlcontrollerview.getVisibility() == View.GONE){
                    rlcontrollerview.setVisibility(View.VISIBLE);
                    img_verified.setVisibility(View.VISIBLE);
                    removeheadermargin();
                    view.clearFocus();
                    InputMethodManager immm = (InputMethodManager) applicationviavideocomposer.getactivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    immm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                break;


            case R.id.btn_playpause:

                if(player!= null && player.isPlaying()){
                    pause();
                }else{
                    if(layout_audiodetails.getVisibility()==View.GONE){
                           isplaypauswebtnshow = true;
                          img_pause.setImageResource(R.drawable.ic_pause);
                          layout_mediatype.setVisibility(View.GONE);
                          layout_scrubberview.setVisibility(View.VISIBLE);
                          linearseekbarcolorview.setVisibility(View.VISIBLE);
                          playpausebutton.setVisibility(View.GONE);
                          mediaseekbar.setVisibility(View.VISIBLE);
                          layout_seekbartiming.setVisibility(View.VISIBLE);
                          img_pause.setVisibility(VISIBLE);
                          img_colapseicon.setVisibility(View.VISIBLE);
                          img_fullscreen.setVisibility(View.GONE);
                          gethelper().updateactionbar(0);
                          gethelper().drawerenabledisable(true);
                          layout_scrubberview.setBackgroundColor(getResources().getColor(R.color.whitetransparent));
                          getcontrollerheight();

                    }
                    playpausebutton.setVisibility(View.GONE);
                    ismediacompleted =false;
                    start();
                }
        }
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
            view1.setTextColor(ContextCompat.getColor(applicationviavideocomposer.getactivity(),R.color.white));

            Drawable drawable2 = (Drawable) view2.getBackground();
            drawable2.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
            view2.setTextColor(ContextCompat.getColor(applicationviavideocomposer.getactivity(),R.color.blue));

            Drawable drawable3 = (Drawable) view3.getBackground();
            drawable3.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
            view3.setTextColor(ContextCompat.getColor(applicationviavideocomposer.getactivity(),R.color.blue));

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
             case  R.id.audio_downwordarrow:

                detector.onTouchEvent(motionEvent);
                break;
        }
        return true;
    }

    GestureDetector flingswipe = new GestureDetector(applicationviavideocomposer.getactivity(), new GestureDetector.SimpleOnGestureListener()
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
        Animation rightswipe = AnimationUtils.loadAnimation(applicationviavideocomposer.getactivity(), R.anim.right_slide);
        linearLayout.startAnimation(rightswipe);
        linearLayout.setVisibility(View.VISIBLE);
        rightswipe.start();
        rightswipe.setAnimationListener(new Animation.AnimationListener() {
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

    public void swiperighttoleft()
    {
        Animation leftswipe = AnimationUtils.loadAnimation(applicationviavideocomposer.getactivity(), R.anim.left_slide);
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
    public void onRestart() {
        if( layout_audiodetails != null && layout_audiodetails.getVisibility() == View.GONE){
            layout_mediatype.setVisibility(View.VISIBLE);
            playpausebutton.setVisibility(View.VISIBLE);
            gethelper().updateactionbar(1);
            layout_scrubberview.setVisibility(View.VISIBLE);
            mediaseekbar.setVisibility(View.VISIBLE);
            layout_seekbartiming.setVisibility(View.VISIBLE);
            img_fullscreen.setVisibility(VISIBLE);
            img_fullscreen.setImageResource(R.drawable.ic_info_mode);
            linearseekbarcolorview.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            if(! issurafcedestroyed)
                return;

            player = new MediaPlayer();
            if(mediafilepath != null && (! mediafilepath.isEmpty()))
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

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (player != null)
        {
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
        ismediacompleted =false;
        maxincreasevideoduration=0;

        audioduration =mp.getDuration();

        try {

            setaudiodata();
            hdlr.postDelayed(seekbarupdatorhandler, 10);

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
    }

    public void start() {
        if(player != null)
        {
            if(mediafilepath !=null)
            {
                playpausebutton.setImageResource(R.drawable.pausebutton);
                player.start();
                player.setOnCompletionListener(this);
            }
        }
    }


    public void pause() {
        if(player != null && player.isPlaying()){
                player.pause();
                playpausebutton.setImageResource(R.drawable.play_btn);
        }
    }

    public void parsemetadata(String metadata,String hashmethod,String videostarttransactionid,String hashvalue,String metahash,
                              String color,String latency,String sequenceno,String colorreason)  {
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
                        color,latency,sequenceno,colorreason));
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
                            color,latency,sequenceno,colorreason));
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
                    common.setgraphicalblockchainvalue(config.blockchainid,arraycontainerformetric.getVideostarttransactionid(),true);
                    common.setgraphicalblockchainvalue(config.hashformula,arraycontainerformetric.getHashmethod(),true);
                    common.setgraphicalblockchainvalue(config.datahash,arraycontainerformetric.getValuehash(),true);
                    common.setgraphicalblockchainvalue(config.matrichash,arraycontainerformetric.getMetahash(),true);

                    common.setspannable(getResources().getString(R.string.blockchain_id), " " + arraycontainerformetric.getVideostarttransactionid(), txt_blockchainid);
                    common.setspannable(getResources().getString(R.string.hash_formula), " " + arraycontainerformetric.getHashmethod(), txt_blockid);
                    common.setspannable(getResources().getString(R.string.mediahash), " " + arraycontainerformetric.getValuehash(), txt_blocknumber);
                    common.setspannable(getResources().getString(R.string.metrichash), " " + arraycontainerformetric.getMetahash(), txt_metahash);

                }
                myHandler.postDelayed(this, 1000);
            }
        };
        myHandler.post(myRunnable);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        gethelper().setcurrentmediaposition((int) (metricmainarraylist.size() * 100) / 100);
        ismediacompleted =true;
        isplaypauswebtnshow =false;
        maxincreasevideoduration= audioduration;
        img_colapseicon.setVisibility(View.GONE);

        if(layout_audiodetails.getVisibility() == View.GONE){
            playpausebutton.setVisibility(View.VISIBLE);
            layout_scrubberview.setVisibility(View.VISIBLE);
            linearseekbarcolorview.setVisibility(View.VISIBLE);
            mediaseekbar.setVisibility(View.VISIBLE);
            layout_seekbartiming.setVisibility(View.VISIBLE);
            layout_audiodetails.setVisibility(View.GONE);
            img_fullscreen.setVisibility(View.VISIBLE);
            gethelper().updateactionbar(1);
            layout_mediatype.setVisibility(View.VISIBLE);
            img_pause.setVisibility(View.GONE);
            playpausebutton.setImageResource(R.drawable.play_btn);
            gethelper().setdrawerheightonfullscreen(0);
            img_pause.setImageResource(R.drawable.ic_pause);
            setbottomimgview();

        }else{
            img_colapseicon.setVisibility(View.GONE);
            player.seekTo(0);
            playpausebutton.setVisibility(View.VISIBLE);
            playpausebutton.setImageResource(R.drawable.play_btn);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(player != null )
                {
                    player.seekTo(0);
                    playpausebutton.setVisibility(View.VISIBLE);
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
                    currentaudioduration =player.getCurrentPosition();  // suppose its on 4th pos means 4000

                starttime = player.getCurrentPosition();
                mediaseekbar.setProgress(player.getCurrentPosition());

                if (time_current != null)
                    time_current.setText(common.gettimestring(starttime));
                if(arraycontainerformetric != null && rlcontrollerview.getVisibility() == View.VISIBLE && (!gethelper().isdraweropened()))
                {
                    String color = "white";
                    if (arraycontainerformetric.getColor() != null && (!arraycontainerformetric.getColor().isEmpty()))
                        color = arraycontainerformetric.getColor();
                    switch (color) {
                        case "green":
                            img_verified.setVisibility(View.VISIBLE);
                            break;
                        case "white":
                            img_verified.setVisibility(View.GONE);
                            break;
                        case "red":
                            img_verified.setVisibility(View.VISIBLE);
                            break;
                        case "yellow":
                            img_verified.setVisibility(View.VISIBLE);
                            break;
                    }
                }
                else
                {
                    img_verified.setVisibility(View.GONE);
                }

                hdlr.postDelayed(this, 10);
            }
        }
    };

   public void setaudiodata(){
       endtime = player.getDuration();
       starttime = player.getCurrentPosition();

       audioduration  = endtime;

       if (time != null)
           time.setText(common.gettimestring(endtime));
       if (time_current != null)
           time_current.setText(common.gettimestring(starttime));

       mediaseekbar.setMax(endtime);
       mediaseekbar.setProgress(starttime);
   }

   public void setupaudiodata() {
       mediafilepath = xdata.getinstance().getSetting(config.selectedaudiourl);
       if (mediafilepath != null && (!mediafilepath.isEmpty())) {

           audioduration = 0;
           playpausebutton.setImageResource(R.drawable.play_btn);
           rlcontrollerview.setVisibility(View.VISIBLE);
           playerposition = 0;

           setupaudioplayer(Uri.parse(mediafilepath));
           tvsize.setText(common.filesize(mediafilepath));
       }
   }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

        if(myHandler != null && myRunnable != null)
            myHandler.removeCallbacks(myRunnable);

        destroyvideoplayer();
    }

    public void getmediastartinfo()
    {
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
            String mediastartdevicedate="";
            if (cur != null && cur.getCount() > 0 && cur.moveToFirst())
            {
                do{
                    mediastartdevicedate = "" + cur.getString(cur.getColumnIndex("videostartdevicedate"));
                    medianame = "" + cur.getString(cur.getColumnIndex("media_name"));
                    medianotes =  "" + cur.getString(cur.getColumnIndex("media_notes"));
                    mediatoken = "" + cur.getString(cur.getColumnIndex("token"));
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
                applicationviavideocomposer.getactivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run() {
                        try {
                            SimpleDateFormat formatted = null;
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

                            if(txt_createdtime.getText().toString().trim().length() == 0)
                            {
                                edt_medianame.setText(medianame);
                                edt_medianotes.setText(medianotes);
                            }

                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(startdate);
                            int increaseseconds=player.getDuration()/1000;
                            calendar.add(Calendar.SECOND, increaseseconds);
                            Date enddate = calendar.getTime();
                            DateFormat datee = new SimpleDateFormat("z",Locale.getDefault());
                            String localTime = datee.format(enddate);
                           /* formatted = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa",Locale.ENGLISH);
                            String startformatteddate=formatted.format(startdate);
                            String endformatteddate=formatted.format(enddate);
                            final String filecreateddate = new SimpleDateFormat("MM-dd-yyyy").format(startdate);
                            final String createdtime = new SimpleDateFormat("hh:mm:ss aa").format(startdate);*/
                            txt_starttime.setText(common.parsedateformat(startdate) + " "+ common.parsetimeformat(startdate) +" " + localTime);
                            txt_endtime.setText(common.parsedateformat(enddate) + " "+ common.parsetimeformat(enddate) +" " +  localTime);
                            txt_duration.setText(mediaduration);
                          //  txt_title_actionbarcomposer.setText(filecreateddate);
                            txt_createdtime.setText(common.parsedateformat(startdate) + " "+ common.parsetimeformat(startdate));
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
                        String colorreason = mitemlist.get(i).getColorreason();
                        parsemetadata(metricdata, hashmethod, videostarttransactionid, sequencehash, serverdictionaryhash, color,
                                latency, sequenceno,colorreason);
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
                        String colorreason = mitemlist.get(i).getColorreason();
                        metricmainarraylist.set(i, new arraycontainer(hashmethod, videostarttransactionid,
                                sequencehash, serverdictionaryhash, color, latency,colorreason));
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

                if(! thumbnailurl.trim().isEmpty() && new File(thumbnailurl).exists())
                {
                    Uri uri= FileProvider.getUriForFile(applicationviavideocomposer.getactivity(),
                            BuildConfig.APPLICATION_ID + ".provider", new File(thumbnailurl));
                    Glide.with(applicationviavideocomposer.getactivity()).
                            load(uri).
                            thumbnail(0.1f).
                            into(img_audiowave);
                }

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

        //mediaid="460123";

        if(mediaid.trim().isEmpty() || mediaid.equalsIgnoreCase("0")){
            Toast.makeText(applicationviavideocomposer.getactivity(),applicationviavideocomposer.getactivity()
                    .getResources().getString(R.string.invalid_empty_mediaid),Toast.LENGTH_SHORT).show();
            resetmedianamenotes();
            return;
        }

        HashMap<String,String> requestparams=new HashMap<>();
        requestparams.put("type",config.type_audio);
        requestparams.put("action","updatemeta");
        requestparams.put("id",mediaid);
        requestparams.put("authtoken",xdata.getinstance().getSetting(config.authtoken));
        requestparams.put("title",title);
        requestparams.put("description",description);
        progressdialog.showwaitingdialog(applicationviavideocomposer.getactivity());
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
                                Toast.makeText(applicationviavideocomposer.getactivity(), applicationviavideocomposer.getactivity()
                                        .getResources().getString(R.string.metadata_updated), Toast.LENGTH_SHORT).show();
                            }
                        }
                        if(object.has("error"))
                        {
                            Toast.makeText(applicationviavideocomposer.getactivity(), object.getString("error"), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(applicationviavideocomposer.getactivity(), applicationviavideocomposer.getactivity().getResources().getString(R.string.json_parsing_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void updatemediainfo(String localkey,String medianame,String medianotes)
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

    public void setdata(adapteritemclick mcontrollernavigator) {
        this.mcontrollernavigator = mcontrollernavigator;
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

        for(int i=0 ; i<metricmainarraylist.size();i++)
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

            int layoutheight =linearseekbarcolorview.getHeight();
            if(layoutheight == 0)
                setcolorseekbar();
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
        progressmediasync.setVisibility(View.VISIBLE);
        gethelper().setdatacomposing(false);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return false;
    }

    public void removebottommargin(){
        RelativeLayout.LayoutParams params  = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,0,0,0);
        layout_audiowave.setLayoutParams(params);
        layout_audiowave.requestLayout();
    }

    public void addbottommargin(int headerheight){
        RelativeLayout.LayoutParams params  = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ABOVE,R.id.layout_scrubberview);
        params.setMargins(0,headerheight,0,20);
        layout_audiowave.setLayoutParams(params);
        layout_audiowave.requestLayout();
    }

    public void setbottomimgview(){
        RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,0,0,38);
        params.addRule(RelativeLayout.ABOVE,R.id.layout_scrubberview);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        img_fullscreen.setLayoutParams(params);
    }
    public void setimageview(){
        RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM );
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        img_fullscreen.setLayoutParams(params);
    }

    public void setfooterlayout()
    {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,0,0,navigationbarheight);
        layout_photoreader.setLayoutParams(params);
    }

    @Override
    public void showhideviewondrawer(boolean drawershown) {
        super.showhideviewondrawer(drawershown);

        if(drawershown)
        {
            if(islastdragarrow){
                gethelper().updateactionbar(0);
                return;
            }else{
                if (player != null && player.isPlaying()){
                    layout_scrubberview.setVisibility(View.VISIBLE);
                    img_pause.setVisibility(View.VISIBLE);
                    img_colapseicon.setVisibility(View.GONE);
                }else {
                    rlcontrollerview.getLayoutParams().height = (rootviewheight - navigationbarheight);
                    gethelper().updateactionbar(0);
                    layout_mediatype.setVisibility(View.GONE);
                    layout_audiodetails.setVisibility(View.GONE);
                    playpausebutton.setVisibility(View.GONE);
                    img_fullscreen.setVisibility(View.GONE);

                    if(img_pause.getVisibility()==View.VISIBLE){
                        layout_scrubberview.setVisibility(View.VISIBLE);
                        linearseekbarcolorview.setVisibility(View.VISIBLE);
                        mediaseekbar.setVisibility(View.VISIBLE);
                        layout_seekbartiming.setVisibility(View.VISIBLE);
                        img_pause.setVisibility(View.VISIBLE);
                        getcontrollerheight();
                    }else{
                        gethelper().setdrawerheightonfullscreen(0);
                        layout_scrubberview.setVisibility(View.GONE);
                        linearseekbarcolorview.setVisibility(View.GONE);
                        mediaseekbar.setVisibility(View.GONE);
                        layout_seekbartiming.setVisibility(View.GONE);
                        img_pause.setVisibility(View.GONE);
                    }
                }
            }
        }
        else
        {
            if(!islastdragarrow){
                rlcontrollerview.getLayoutParams().height = (rootviewheight - navigationbarheight);

                if(player != null && player.isPlaying())
                {
                    layout_scrubberview.setVisibility(View.VISIBLE);
                    mediaseekbar.setVisibility(View.VISIBLE);
                    layout_seekbartiming.setVisibility(View.VISIBLE);
                    linearseekbarcolorview.setVisibility(View.VISIBLE);
                    img_pause.setVisibility(View.VISIBLE);
                    setseekbarlayoutcolor();
                    img_colapseicon.setVisibility(View.VISIBLE);
                }
                else
                {
                    gethelper().updateactionbar(1);
                    layout_mediatype.setVisibility(View.VISIBLE);
                    layout_scrubberview.setVisibility(View.GONE);
                    layout_mediatype.setVisibility(View.VISIBLE);
                    playpausebutton.setVisibility(View.VISIBLE);
                    img_fullscreen.setImageResource(R.drawable.ic_info_mode);
                    img_fullscreen.setVisibility(View.VISIBLE);
                    layout_scrubberview.setVisibility(View.VISIBLE);
                    mediaseekbar.setVisibility(View.VISIBLE);
                    layout_seekbartiming.setVisibility(View.VISIBLE);
                    linearseekbarcolorview.setVisibility(View.VISIBLE);

                    if(img_pause.getVisibility()==View.VISIBLE){
                        img_pause.setImageResource(R.drawable.ic_play);
                        img_pause.setVisibility(View.VISIBLE);
                        playpausebutton.setVisibility(View.GONE);
                        getcontrollerheight();
                        setbottomimgview();
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


    public void recenterplaypause(final int setvisiblety)
    {
        img_audiowave.post(new Runnable() {
            @Override
            public void run() {

                RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);

                params.addRule(RelativeLayout.CENTER_HORIZONTAL,TRUE);

                if(setvisiblety == 0){
                    params.addRule(RelativeLayout.CENTER_IN_PARENT,TRUE);
                    playpausebutton.setLayoutParams(params);
                    playpausebutton.setVisibility(View.VISIBLE);

                }else{

                    int imageviewheight = img_audiowave.getHeight();
                    int buttonheight = playpausebutton.getHeight();
                    int centerheight = (imageviewheight + navigationbarheight)/2;
                    int finalmargin = centerheight -  buttonheight/2;
                    params.addRule(RelativeLayout.CENTER_HORIZONTAL,TRUE);
                    params.setMargins(0,finalmargin,0,0);
                    playpausebutton.setLayoutParams(params);

                }
            }
        });
    }

    public void setcolorseekbar(){

        int mediaseekbarheight = mediaseekbar.getHeight();

        RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,mediaseekbarheight);
        parms.setMargins(20,0,20,0);
        linearseekbarcolorview.setLayoutParams(parms);
        progressmediasync.setLayoutParams(parms);
    }

    public void hidefocus(EditText edittext){
        edittext.setClickable(false);
        edittext.setFocusable(false);
        edittext.setFocusableInTouchMode(false);
        rlcontrollerview.setVisibility(View.VISIBLE);
        img_verified.setVisibility(View.VISIBLE);
        removeheadermargin();
    }

    public void removeheaderpadding(){
        RelativeLayout.LayoutParams params  = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW,R.id.rl_controllerview);
        layout_audiodetails.setPadding(0,0,0,footerheight);
        layout_audiodetails.setLayoutParams(params);
        layout_audiodetails.requestLayout();
    }

    public void setheadermargin(){
        RelativeLayout.LayoutParams params  = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, audiodetailviewheight + mediatypeheight);
        params.setMargins(0,mediatypeheight,0,0);
        layout_audiodetails.setPadding(0,0,0,0);
        layout_audiodetails.setLayoutParams(params);
        layout_audiodetails.requestLayout();
    }

    public void removeheadermargin(){
        RelativeLayout.LayoutParams params  = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW,R.id.rl_controllerview);
        params.setMargins(0,0,0,0);
        layout_audiodetails.setPadding(0,0,0,(footerheight));
        layout_audiodetails.setLayoutParams(params);
        layout_audiodetails.requestLayout();
    }

    public void visiblefocus(EditText edittext){
        rlcontrollerview.setVisibility(View.GONE);
        setheadermargin();
        // gethelper().setwindowfitxy(false);
        edittext.setClickable(true);
        edittext.setEnabled(true);
        edittext.setFocusable(true);
        edittext.setFocusableInTouchMode(true);
        edittext.setSelection(edittext.getText().length());
        edittext.requestFocus();
        img_verified.setVisibility(View.GONE);

    }

    public void showaudioplayer(){
        if(rlcontrollerview.getVisibility() == View.GONE){
            hidekeyboard();
            rlcontrollerview.setVisibility(View.VISIBLE);
            img_verified.setVisibility(View.GONE);
            removeheadermargin();
        }
    }

    public void getcontrollerheight(){
        layout_scrubberview.post(new Runnable() {
            @Override
            public void run() {
                int controllerheight = layout_scrubberview.getHeight();
                gethelper().setdrawerheightonfullscreen(controllerheight);
            }
        });
    }

    protected int calculateRMSLevel(byte[] audioData)
    { // audioData might be buffered data read from a data line
        long lSum = 0;

        for(int i=0; i<audioData.length; i++)

            lSum = lSum + audioData[i];

        double dAvg = lSum / audioData.length;

        double sumMeanSquare = 0d;

        for(int j=0; j<audioData.length; j++)

            sumMeanSquare = sumMeanSquare + Math.pow(audioData[j] - dAvg, 2d);

        double averageMeanSquare = sumMeanSquare / audioData.length;

        return (int)(Math.pow(averageMeanSquare,0.5d) + 0.2);

    }

    public void resizeviewtohalfscreen(){
        setbottomimgview();
        removeheaderpadding();
        recenterplaypause(0);
        img_fullscreen.setImageResource(R.drawable.ic_full_screen_mode);
        img_fullscreen.setVisibility(View.VISIBLE);
        addbottommargin(mediatypeheight);
        rlcontrollerview.getLayoutParams().height = audioviewheight;
        layout_audiodetails.getLayoutParams().height = audiodetailviewheight;
        layout_scrubberview.setBackgroundColor(getResources().getColor(R.color.whitetransparent));
        layout_audiodetails.setVisibility(View.VISIBLE);
        layout_mediatype.setVisibility(View.VISIBLE);
        layout_scrubberview.setVisibility(View.VISIBLE);
        linearseekbarcolorview.setVisibility(View.VISIBLE);
        mediaseekbar.setVisibility(View.VISIBLE);
        layout_seekbartiming.setVisibility(View.VISIBLE);
        audio_downwordarrow.setVisibility(View.VISIBLE);
        rl_audio_downwordarrow.setVisibility(View.VISIBLE);
        img_pause.setVisibility(View.GONE);
        img_colapseicon.setVisibility(View.GONE);
        gethelper().drawerenabledisable(false);
        gethelper().updateactionbar(1);
    }



}