package com.cryptoserver.composer.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.cryptoserver.composer.models.metadatahash;
import com.cryptoserver.composer.models.metricmodel;
import com.cryptoserver.composer.models.videomodel;
import com.cryptoserver.composer.models.wavevisualizer;
import com.cryptoserver.composer.utils.circularImageview;
import com.cryptoserver.composer.utils.common;
import com.cryptoserver.composer.utils.config;
import com.cryptoserver.composer.utils.md5;
import com.cryptoserver.composer.utils.progressdialog;
import com.cryptoserver.composer.utils.sha;
import com.cryptoserver.composer.utils.visualizeraudiorecorder;
import com.cryptoserver.composer.utils.xdata;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class audioreaderfragment extends basefragment implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener,MediaPlayer.OnCompletionListener, View.OnTouchListener, View.OnClickListener,AdapterView.OnItemSelectedListener {

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
    LinearLayout layout_scrubberview;
    @BindView(R.id.frontview)
    RelativeLayout frontview;
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
    @BindView(R.id.fragment_graphic_container)
    FrameLayout fragment_graphic_container;
    @BindView(R.id.textfetchdata)
    TextView textfetchdata;

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
    @BindView(R.id.txt_createdtime)
    TextView txt_createdtime;
    @BindView(R.id.layout_mediatype)
    RelativeLayout layout_mediatype;
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
    @BindView(R.id.layout_starttime)
    LinearLayout layout_starttime;
    @BindView(R.id.layout_endtime)
    LinearLayout layout_endtime;
    @BindView(R.id.layout_duration)
    LinearLayout layout_duration;

    private String audiourl = null;
    private RelativeLayout showcontrollers;
    private MediaPlayer player;
    private View rootview = null;
    private String selectedmetrics="";
    private ImageView handleimageview,righthandle;
    private LinearLayout linearLayout;
    private String keytype = config.prefs_md5;
    private long currentframenumber =0,playerposition=0;
    private long frameduration =15, mframetorecordcount =0;
    private boolean ishashprocessing=false,isbitmapprocessing=false;
    private boolean islisttouched=false,islistdragging=false,isfromlistscroll=false;
    private int REQUESTCODE_PICK=201;
    private static final int request_read_external_storage = 1;
    private Uri selectedvideouri =null;
    private boolean issurafcedestroyed=false;
    private boolean isscrubbing=true;
    private Handler myHandler,handlerrecycler;
    private Runnable myRunnable,runnablerecycler;
    private long audioduration =0,maxincreasevideoduration=0, currentaudioduration =0, currentaudiodurationseconds =0;
    private boolean isdraweropen=false;
    private LinearLayoutManager mlinearlayoutmanager;
    private String selectedhaeshes="";
    private int lastmetricescount=0;
    private SurfaceHolder holder;
    private ArrayList<videomodel> mainvideoframes =new ArrayList<>();
    private ArrayList<videomodel> mvideoframes =new ArrayList<>();
    private ArrayList<videomodel> mallframes =new ArrayList<>();
    private ArrayList<videomodel> mmetricsitems =new ArrayList<>();
    private ArrayList<videomodel> mhashesitems =new ArrayList<>();
    private ArrayList<arraycontainer> metricmainarraylist = new ArrayList<>();
    private videoframeadapter mmetricesadapter,mhashesadapter;
    private framebitmapadapter adapter;
    private graphicalfragment fragmentgraphic;
    private Handler wavehandler;
    private Runnable waverunnable;
    boolean runmethod = false;
    private LinearLayoutManager mLayoutManager;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    public int selectedsection=1;
    public boolean imediacompleted =false;
    private Visualizer mVisualizer;
    visualizeraudiorecorder myvisualizerviewmedia;
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
    String soundamplitudealue = "";
    String[] soundamplitudealuearray ;
    ArrayList<wavevisualizer> wavevisualizerslist =new ArrayList<>();
    private BroadcastReceiver getmetadatabroadcastreceiver,getencryptionmetadatabroadcastreceiver;

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
            handleimageview=rootview.findViewById(R.id.handle);
            righthandle=rootview.findViewById(R.id.righthandle);
            playpausebutton = (circularImageview)rootview.findViewById(R.id.btn_playpause);
            mediaseekbar = (SeekBar) rootview.findViewById(R.id.mediacontroller_progress);
            time_current = (TextView) rootview.findViewById(R.id.time_current);
            time = (TextView) rootview.findViewById(R.id.time);
            rlcontrollerview = (RelativeLayout) rootview.findViewById(R.id.rl_controllerview);

            mFormatBuilder = new StringBuilder();
            mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

            myvisualizerviewmedia = (visualizeraudiorecorder) rootview.findViewById(R.id.myvisualizerviewmedia);

            myvisualizerviewmedia.setVisibility(View.VISIBLE);
            textfetchdata.setVisibility(View.VISIBLE);
            playpausebutton.setImageResource(R.drawable.play);

            showcontrollers=rootview.findViewById(R.id.video_container);
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

            frameduration= common.checkframeduration();
            keytype=common.checkkey();

            frontview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gethelper().updateactionbar(1);

                }
            });
            handleimageview.setOnTouchListener(this);
            righthandle.setOnTouchListener(this);

            txtSlot1.setOnClickListener(this);
            txtSlot2.setOnClickListener(this);
            txtSlot3.setOnClickListener(this);
            playpausebutton.setOnClickListener(this);

            img_dotmenu.setOnClickListener(this);
            img_folder.setOnClickListener(this);
            img_camera.setOnClickListener(this);
            img_arrow_back.setOnClickListener(this);

            img_dotmenu.setVisibility(View.VISIBLE);
            img_folder.setVisibility(View.VISIBLE);
            img_camera.setVisibility(View.VISIBLE);
            img_arrow_back.setVisibility(View.VISIBLE);

            handleimageview.setVisibility(View.GONE);
            righthandle.setVisibility(View.GONE);



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
            fragment_graphic_container.setVisibility(View.INVISIBLE);

            mediaseekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                long seeked_progess;

                @Override
                public void onProgressChanged(final SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    if(player!=null)
                    {
                        player.pause();
                        playpausebutton.setImageResource(R.drawable.play);
                    }
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                    player.seekTo(seekBar.getProgress());
                }
            });

            flingactionmindstvac=common.getdrawerswipearea();
            if(fragmentgraphic == null) {
                fragmentgraphic = new graphicalfragment();

                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.add(R.id.fragment_graphic_container, fragmentgraphic);
                transaction.commit();
            }


            //tabs_detail
            tab_layout.setVisibility(View.VISIBLE);
            txtslotmedia.setText(getResources().getString(R.string.audio));
            List<String> categories = new ArrayList<String>();
            categories.add("All Media");
            categories.add("Photo");
            categories.add("Video");
            categories.add("Audio");
            photospinner.setOnItemSelectedListener(this);
            img_share_media.setOnClickListener(this);
            img_edit_name.setOnClickListener(this);
            img_edit_notes.setOnClickListener(this);
            scrollview_detail.setVisibility(View.VISIBLE);
            tab_layout.setVisibility(View.VISIBLE);
            layout_footer.setVisibility(View.VISIBLE);
        //    layout_photodetails.setVisibility(View.VISIBLE);
            layout_mediatype.setVisibility(View.VISIBLE);
            layout_duration.setVisibility(View.VISIBLE);
            layout_endtime.setVisibility(View.VISIBLE);
            layout_starttime.setVisibility(View.VISIBLE);

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, categories);

            // Drop down layout style - list view with radio button
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // attaching data adaptermedialist to spinner
            photospinner.setAdapter(dataAdapter);

            txtslotencyption.setOnClickListener(this);
            txtslotmeta.setOnClickListener(this);
            txtslotmedia.setOnClickListener(this);
            resetButtonViews(txtslotmedia, txtslotmeta, txtslotencyption);

            setmetriceshashesdata();
            setupaudiodata();
            if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_reader))
            {
                getmediametadata();
                getmetadetareader();
            }

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
                if (audiourl != null && (!audiourl.isEmpty()))
                    common.shareimage(getActivity(), audiourl);
                break;

            case R.id.img_dotmenu:
                settingfragment settingfrag=new settingfragment();
                gethelper().addFragment(settingfrag, false, true);
                break;
            case R.id.img_folder:

                break;
            case R.id.img_camera:
                launchbottombarfragment();
                break;

            case R.id.img_arrow_back:
                gethelper().onBack();
                break;

            case R.id.btn_playpause:

                if(player.isPlaying()){
                    pause();
                }else{
                    start();
                }
        }
    }

    public void launchbottombarfragment()
    {
        bottombarfragment fragbottombar=new bottombarfragment();
        fragbottombar.setData(new adapteritemclick() {
            @Override
            public void onItemClicked(Object object) {

            }

            @Override
            public void onItemClicked(Object object, int type) {

            }
        });
        gethelper().replaceFragment(fragbottombar, false, true);
    }

    @Override
    public void oncurrentlocationchanged(Location location) {
        super.oncurrentlocationchanged(location);
    }

    public void resetButtonViews(TextView view1, TextView view2, TextView view3)
    {
        view1.setBackgroundResource(R.color.videolist_background);
        view1.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));

        view2.setBackgroundResource(R.color.white);
        view2.setTextColor(getActivity().getResources().getColor(R.color.videolist_background));

        view3.setBackgroundResource(R.color.white);
        view3.setTextColor(getActivity().getResources().getColor(R.color.videolist_background));
    }

    @Override
    public void setmetriceslistitems(ArrayList<metricmodel> mitems) {
        super.setmetriceslistitems(mitems);
        /*metricItemArraylist.clear();
        metricItemArraylist.addAll(mitems);
        itemMetricAdapter.notifyDataSetChanged();*/

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId())
        {
            case  R.id.handle:
                flingswipe.onTouchEvent(motionEvent);
                break;

            case  R.id.righthandle:
                flingswipe.onTouchEvent(motionEvent);
                break;
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
        handleimageview.setVisibility(View.GONE);
        linearLayout.setVisibility(View.VISIBLE);
        rightswipe.start();
        righthandle.setVisibility(View.VISIBLE);
        rightswipe.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                righthandle.setImageResource(R.drawable.righthandle);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                righthandle.setImageResource(R.drawable.lefthandle);
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
        righthandle.setVisibility(View.VISIBLE);
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
                    initAudio();
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

        try {
            if(playerposition > 0)
            {
                mp.seekTo((int)playerposition);
                player.seekTo((int)playerposition);
            }
            else
            {
                mp.seekTo(100);
            }

            if(fragmentgraphic != null){
                fragmentgraphic.setmediaplayer(true,null);
            }else{
                if(audiourl!=null && fragmentgraphic != null){
                    fragmentgraphic.setmediaplayer(true,null);
                }
            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }

        frontview.setVisibility(View.GONE);
    }

    adapteritemclick mitemclick=new adapteritemclick() {
        @Override
        public void onItemClicked(Object object) {
        }

        @Override
        public void onItemClicked(Object object, int type) {

        }

    };

    public void start() {
        if(player != null)
        {
            if(selectedvideouri!= null){
                fragmentgraphic.setvisualizerwave();
                wavevisualizerslist.clear();
                playpausebutton.setImageResource(R.drawable.pause);
                player.start();
                hdlr.postDelayed(UpdateSongTime, 100);
                player.setOnCompletionListener(this);
            }
            else{
                if(audiourl!=null){
                    playpausebutton.setImageResource(R.drawable.pause);
                    player.start();
                    hdlr.postDelayed(UpdateSongTime, 100);
                    player.setOnCompletionListener(this);
                }
            }
        }
    }


    public void pause() {
        if(player != null){
                player.pause();
                playpausebutton.setImageResource(R.drawable.play);
        }
    }

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
                //destroyvideoplayer();
                /*framemetricssettings fragmatriclist=new framemetricssettings();
                gethelper().replaceFragment(fragmatriclist, false, true);*/
                break;
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
                    initAudio();
                    fragmentgraphic.setvisualizerwave();
                    wavevisualizerslist.clear();
                    setaudiodata();
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
                if(isdraweropen)
                {
                    if((recyview_hashes.getVisibility() == View.VISIBLE) && (! selectedhaeshes.trim().isEmpty()))
                    {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mhashesitems.add(new videomodel(selectedhaeshes));
                                mhashesadapter.notifyItemChanged(mhashesitems.size()-1);
                                selectedhaeshes="";
                            }
                        });

                    }

                    setmetricesgraphicaldata();

                    if((fragment_graphic_container.getVisibility() == View.VISIBLE))

                        graphicopen=true;
                }

                if(fragmentgraphic != null)
                    fragmentgraphic.setdrawerproperty(graphicopen);

                myHandler.postDelayed(this, 1000);
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

        Log.e("Current duration ",""+n+" "+currentduration+" "+metricmainarraylist.size());

        for(int i=0;i<n;i++)
        {
            if(! metricmainarraylist.get(i).isIsupdated())
            {
                metricmainarraylist.get(i).setIsupdated(true);
                double latt=0,longg=0;
                ArrayList<metricmodel> metricItemArraylist = metricmainarraylist.get(i).getMetricItemArraylist();

                fragmentgraphic.getencryptiondata(metricmainarraylist.get(i).getHashmethod(),metricmainarraylist.get(i).getVideostarttransactionid(),
                        metricmainarraylist.get(i).getValuehash(),metricmainarraylist.get(i).getMetahash());

                selectedmetrics=selectedmetrics+"\n";
                for(int j=0;j<metricItemArraylist.size();j++)
                {
                    selectedmetrics=selectedmetrics+"\n"+metricItemArraylist.get(j).getMetricTrackKeyName()+" - "+
                            metricItemArraylist.get(j).getMetricTrackValue();
                    common.setgraphicalitems(metricItemArraylist.get(j).getMetricTrackKeyName(),
                            metricItemArraylist.get(j).getMetricTrackValue(),true);

                    if(fragmentgraphic != null)
                    {
                        if (metricItemArraylist.get(j).getMetricTrackKeyName().equalsIgnoreCase("gpslatitude"))
                        {
                            if(! metricItemArraylist.get(j).getMetricTrackValue().equalsIgnoreCase("NA"))
                            {
                                latt=Double.parseDouble(metricItemArraylist.get(j).getMetricTrackValue());
                                if(longg != 0)
                                {
                                    if(fragmentgraphic != null)
                                    {
                                        fragmentgraphic.drawmappoints(new LatLng(latt,longg));
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
                                    if(fragmentgraphic != null)
                                    {
                                        fragmentgraphic.drawmappoints(new LatLng(latt,longg));
                                        latt=0;longg=0;

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        selectedmetrics=selectedmetrics+"\n";

        if((! selectedmetrics.toString().trim().isEmpty()))
        {
            mmetricsitems.add(new videomodel(selectedmetrics));
            mmetricesadapter.notifyItemChanged(mmetricsitems.size()-1);
            selectedmetrics="";
        }

        if(fragment_graphic_container .getVisibility() == View.VISIBLE)
        {
            if(fragmentgraphic != null){
                fragmentgraphic.setmetricesdata();
                fragmentgraphic.getvisualizerwavecomposer(wavevisualizerslist);
            }
        }
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
                        myvisualizerviewmedia.clear();
                        wavevisualizerslist.clear();

                    playpausebutton.setImageResource(R.drawable.play);
                }
            }
        },200);

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


                starttime = player.getCurrentPosition();
                mediaseekbar.setProgress(player.getCurrentPosition());

                if (time_current != null)
                    time_current.setText(stringForTime(starttime));
                hdlr.postDelayed(this, 100);
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
             starttime = player.getCurrentPosition();

             audioduration  = endtime;

             if (time != null)
                 time.setText(stringForTime(endtime));
             if (time_current != null)
                 time_current.setText(stringForTime(starttime));

             mediaseekbar.setMax(endtime);
             mediaseekbar.setProgress(starttime);
         }
     },100);
   }

   public void selectionmetadata(){
       selectedsection = 1;

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
       fragment_graphic_container.setVisibility(View.INVISIBLE);

   }

   public void setupaudiodata() {
       audiourl = xdata.getinstance().getSetting("selectedaudiourl");
       if (audiourl != null && (!audiourl.isEmpty())) {

           mvideoframes.clear();
           mainvideoframes.clear();
           mallframes.clear();
           audioduration = 0;
           playpausebutton.setImageResource(R.drawable.play);
           rlcontrollerview.setVisibility(View.VISIBLE);
           playerposition = 0;
           righthandle.setVisibility(View.VISIBLE);

           setupaudioplayer(Uri.parse(audiourl));
           Thread thread = new Thread() {
               public void run() {
                   if(! BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_reader))
                           getmediadata();
               }
           };
           thread.start();
       }
   }


    @Override
    public void onStart() {
        super.onStart();
        registerbroadcastreciver();
        registerbroadcastreciverforencryptionmetadata();
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            applicationviavideocomposer.getactivity().unregisterReceiver(getmetadatabroadcastreceiver);
            applicationviavideocomposer.getactivity().unregisterReceiver(getencryptionmetadatabroadcastreceiver);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void registerbroadcastreciver()
    {
      /* // IntentFilter intentFilter = null;
        *//*if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_reader))
        {
           // intentFilter = new IntentFilter(config.reader_service_getmetadata);
        }
        else*//*
        {
            IntentFilter intentFilter = new IntentFilter(config.composer_service_savemetadata);
        }*/

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
                            selectedhaeshes ="";
                        }
                    }
                });
            }
        }
    }

    public void getmediadata()
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

            Cursor cur = mdbhelper.getstartmediainfo(common.getfilename(audiourl));
            String completedate="";
            if (cur != null && cur.getCount() > 0 && cur.moveToFirst())
            {
                do{
                    completedate = "" + cur.getString(cur.getColumnIndex("videocompletedevicedate"));
                }while(cur.moveToNext());
            }

            if (!completedate.isEmpty()){

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textfetchdata.setVisibility(View.GONE);
                    }
                });

                ArrayList<metadatahash> mitemlist = mdbhelper.getmediametadatabyfilename(common.getfilename(audiourl));
               // metricmainarraylist.clear();
                String framelabel="";
                if(metricmainarraylist.size()>0){

                    for(int i=0;i<mitemlist.size();i++) {
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
                        selectedhaeshes = selectedhaeshes+"\n";
                        framelabel="Frame ";
                        if(i == mitemlist.size()-1)
                        {
                            framelabel="Last Frame ";
                        }
                        selectedhaeshes = selectedhaeshes+framelabel+mitemlist.get(i).getSequenceno()+" "+mitemlist.get(i).getHashmethod()+
                                ": "+mitemlist.get(i).getSequencehash();
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

    private void initAudio() {

        if(player != null){

            applicationviavideocomposer.getactivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
            myvisualizerviewmedia.setVisibility(View.VISIBLE);

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
            player
                    .setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            //mVisualizer.setEnabled(false);
                        }
                    });
            //mMediaPlayer.start();
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
                                Log.e("amplitudeValue=",""+ amplitude);
                                if(player != null && player.isPlaying()){

                                    if(player.getCurrentPosition()==0){
                                        fragmentgraphic.setvisualizerwave();
                                         myvisualizerviewmedia.clear();
                                         wavevisualizerslist.clear();
                                    }

                                    if(amplitude >=  35)
                                        amplitude = amplitude*2;

                                     int x= amplitude * 100;

                                    myvisualizerviewmedia.addAmplitude(x); // update the VisualizeView
                                    myvisualizerviewmedia.addAmplitude(50); // update the VisualizeView
                                    myvisualizerviewmedia.addAmplitude(50); // update the VisualizeView
                                    myvisualizerviewmedia.addAmplitude(50); // update the VisualizeView
                                    myvisualizerviewmedia.invalidate();
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}