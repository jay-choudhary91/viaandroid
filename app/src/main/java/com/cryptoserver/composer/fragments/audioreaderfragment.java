package com.cryptoserver.composer.fragments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cryptoserver.composer.BuildConfig;
import com.cryptoserver.composer.R;
import com.cryptoserver.composer.adapter.framebitmapadapter;
import com.cryptoserver.composer.adapter.videoframeadapter;
import com.cryptoserver.composer.applicationviavideocomposer;
import com.cryptoserver.composer.interfaces.adapteritemclick;
import com.cryptoserver.composer.models.arraycontainer;
import com.cryptoserver.composer.models.metricmodel;
import com.cryptoserver.composer.models.videomodel;
import com.cryptoserver.composer.utils.VisualizerViewaudioMedia;
import com.cryptoserver.composer.utils.circularImageview;
import com.cryptoserver.composer.utils.common;
import com.cryptoserver.composer.utils.config;
import com.cryptoserver.composer.utils.customffmpegframegrabber;
import com.cryptoserver.composer.utils.md5;
import com.cryptoserver.composer.utils.progressdialog;
import com.cryptoserver.composer.utils.sha;
import com.cryptoserver.composer.utils.xdata;
import com.google.android.gms.maps.model.LatLng;

import org.bytedeco.javacpp.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
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
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class audioreaderfragment extends basefragment implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener,MediaPlayer.OnCompletionListener, View.OnTouchListener, View.OnClickListener {

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
    private boolean suspendframequeue=false,suspendbitmapqueue = false,isnewvideofound=false;
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
    private Handler waveHandler;
    private Runnable waveRunnable;
    boolean runmethod = false;
    private LinearLayoutManager mLayoutManager;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    public int selectedsection=1;
    public boolean isvideocompleted=false;
    private Visualizer mVisualizer;
    VisualizerViewaudioMedia myvisualizerviewmedia;
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

            myvisualizerviewmedia = (VisualizerViewaudioMedia) rootview.findViewById(R.id.myvisualizerviewmedia);
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
                    }
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                    player.seekTo(seekBar.getProgress());
                    player.start();
                }
            });
            if(!BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_reader))
            {
               setupaudiodata();
            }
            flingactionmindstvac=common.getdrawerswipearea();
            if(fragmentgraphic == null) {
                fragmentgraphic = new graphicalfragment();

                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.add(R.id.fragment_graphic_container, fragmentgraphic);
                transaction.commit();
            }
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
                visibleItemCount = mLayoutManager.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();
                if ((visibleItemCount + pastVisiblesItems) == totalItemCount) {
                    if(selectedmetrics.toString().trim().length() > 0)
                    {
                        mmetricsitems.add(new videomodel(selectedmetrics));
                        mmetricesadapter.notifyItemChanged(mmetricsitems.size()-1);
                        selectedmetrics="";
                    }
                }
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

            case R.id.btn_playpause:

                if(player.isPlaying()){
                    pause();
                }else{
                    start();
                }
        }
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
                    isnewvideofound=true;
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
    public void onStop() {
        super.onStop();
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
        isvideocompleted=false;
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

        }catch (Exception e)
        {
            e.printStackTrace();
        }

        frontview.setVisibility(View.GONE);

        if(fragmentgraphic != null && selectedvideouri!=null){
            fragmentgraphic.setmediaplayer(true,null);
        }else{
            if(audiourl!=null && fragmentgraphic != null){
                fragmentgraphic.setmediaplayer(true,null);
            }
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

    public void start() {
        if(player != null)
        {
            if(selectedvideouri!= null){
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
                if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_reader)) {
                    checkwritestoragepermission();
                }else{
                    gethelper().onBack();
                }
                break;
            case R.id.img_setting:
                destroyvideoplayer();
                fragmentsettings fragmatriclist=new fragmentsettings();
                gethelper().replaceFragment(fragmatriclist, false, true);
                break;
        }
    }


    private void checkwritestoragepermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED ) {
                opengallery();
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Toast.makeText(getActivity(), "app needs to be able to save videos", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        request_read_external_storage);
            }
        }
        else
        {
            opengallery();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == request_read_external_storage) {
            boolean permissionsallgranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    permissionsallgranted = false;
                    break;
                }
            }
            if (permissionsallgranted) {
                opengallery();
            }
        }
    }

    public  void opengallery()
    {
        destroyvideoplayer();
        Intent intent;
        if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
        {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        }
        else
        {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.INTERNAL_CONTENT_URI);
        }
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra("return-data", true);
        startActivityForResult(intent,REQUESTCODE_PICK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODE_PICK) {

            if (resultCode == RESULT_OK) {

                rlcontrollerview.setVisibility(View.VISIBLE);
                selectedvideouri = data.getData();
                selectionmetadata();

                try {
                    //audiourl=common.getUriRealPath(applicationviavideocomposer.getactivity(),selectedvideouri);
                    audiourl = common.getpath(getActivity(), selectedvideouri);
                }catch (Exception e)
                {
                    e.printStackTrace();
                    common.showalert(getActivity(),getResources().getString(R.string.file_uri_parse_error));
                    return;
                }

                if(audiourl == null || (audiourl.trim().isEmpty()))
                {
                    common.showalert(getActivity(),getResources().getString(R.string.file_doesnot_exist));
                    return;
                }

                if(! (new File(audiourl).exists()))
                {
                    common.showalert(getActivity(),getResources().getString(R.string.file_doesnot_exist));
                    return;
                }

                frameduration=common.checkframeduration();
                keytype=common.checkkey();

                mmetricsitems.clear();
                mmetricesadapter.notifyDataSetChanged();

                mhashesitems.clear();
                mhashesadapter.notifyDataSetChanged();

                selectedhaeshes="";
                selectedmetrics="";

                suspendframequeue=false;
                suspendbitmapqueue=false;

                if(ishashprocessing)
                    suspendframequeue=true;

                if(isbitmapprocessing)
                    suspendbitmapqueue=true;

                setupaudioplayer(selectedvideouri);
                audioduration =0;
                playpausebutton.setImageResource(R.drawable.play);
                rlcontrollerview.setVisibility(View.VISIBLE);
                playerposition=0;
                righthandle.setVisibility(View.VISIBLE);
                if(audiourl != null && (! audiourl.isEmpty())){
                    mvideoframes.clear();
                    mainvideoframes.clear();
                    mallframes.clear();
                    txt_metrics.setText("");
                    txt_hashes.setText("");
                    isnewvideofound=true;
                }
            }
        }
    }

    public void parsemetadata(String metadata)
    {
        try {

            JSONArray array=new JSONArray(metadata);
            metricmainarraylist.clear();
            for(int j=0;j<array.length();j++)
            {
                ArrayList<metricmodel> metricItemArraylist = new ArrayList<>();
                JSONObject object=array.getJSONObject(j);
                Iterator<String> myIter = object.keys();
                while (myIter.hasNext()) {
                    String key = myIter.next();
                    String value = object.optString(key);
                    metricmodel model=new metricmodel();
                    model.setMetricTrackKeyName(key);
                    model.setMetricTrackValue(value);
                    metricItemArraylist.add(model);
                }
                metricmainarraylist.add(new arraycontainer(metricItemArraylist));
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void getFramesBitmap()
    {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        runmethod = true;
        isbitmapprocessing=true;

        MediaMetadataRetriever m_mediaMetadataRetriever = new MediaMetadataRetriever();
        m_mediaMetadataRetriever.setDataSource(audiourl);

        String time = m_mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        String metadatawriter=m_mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_WRITER);
        String metadataAlbum=m_mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);

        if(metadatawriter != null && (! metadatawriter.trim().isEmpty()) && (! metadatawriter.equalsIgnoreCase("null")))
        {
            parsemetadata(metadatawriter);
        }
        else if(metadataAlbum != null && (! metadataAlbum.trim().isEmpty()) && (! metadataAlbum.equalsIgnoreCase("null")))
        {
            parsemetadata(metadataAlbum);
        }

        isbitmapprocessing=false;
        suspendbitmapqueue=false;

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

    public void setVideoAdapter() {
        int count = 1;
        ishashprocessing=true;
        currentframenumber=0;
        currentframenumber = currentframenumber + frameduration;
        try
        {
            customffmpegframegrabber grabber = new customffmpegframegrabber(new File(audiourl));
            grabber.start();
            videomodel lastframehash=null;
            int totalframes=grabber.getLengthInAudioFrames();

            for(int i = 0; i<grabber.getLengthInAudioFrames(); i++){
                Frame frame = grabber.grabAudio();
                if (frame == null)
                    break;

                ShortBuffer shortbuff= ((ShortBuffer) frame.samples[0].position(0));
                java.nio.ByteBuffer bb = java.nio.ByteBuffer.allocate(shortbuff.capacity() * 2);
                bb.asShortBuffer().put(shortbuff);
                byte[] byteData = bb.array();
                String keyValue= getkeyvalue(byteData);
                Log.e("hashes ",""+keyValue);

                /*Buffer outputBuffer = frame.samples[0];
                ByteBuffer byteBuffer=null;
                if (outputBuffer instanceof ByteBuffer) {
                    byteBuffer = (ByteBuffer) outputBuffer;
                } else if (outputBuffer instanceof CharBuffer) {
                    byteBuffer = ByteBuffer.allocate(outputBuffer.capacity());
                    byteBuffer.asCharBuffer().put((CharBuffer) outputBuffer);
                } else if (outputBuffer instanceof ShortBuffer) {
                    byteBuffer = ByteBuffer.allocate(outputBuffer.capacity() * 2);
                    byteBuffer.asShortBuffer().put((ShortBuffer) outputBuffer);
                } else if (outputBuffer instanceof IntBuffer) {
                    byteBuffer = ByteBuffer.allocate(outputBuffer.capacity() * 4);
                    byteBuffer.asIntBuffer().put((IntBuffer) outputBuffer);
                } else if (outputBuffer instanceof LongBuffer) {
                    byteBuffer = ByteBuffer.allocate(outputBuffer.capacity() * 8);
                    byteBuffer.asLongBuffer().put((LongBuffer) outputBuffer);
                } else if (outputBuffer instanceof FloatBuffer) {
                    byteBuffer = ByteBuffer.allocate(outputBuffer.capacity() * 4);
                    byteBuffer.asFloatBuffer().put((FloatBuffer) outputBuffer);
                } else if (outputBuffer instanceof DoubleBuffer) {
                    byteBuffer = ByteBuffer.allocate(outputBuffer.capacity() * 8);
                    byteBuffer.asDoubleBuffer().put((DoubleBuffer) outputBuffer);
                }

                byte[] byteData = new byte[byteBuffer.remaining()];
                byteBuffer.get(byteData);
                String keyValue= getkeyvalue(byteData);
                Log.e("hashes ",""+keyValue);*/

                if(fragmentgraphic != null)
                    fragmentgraphic.sethashesdata(keyValue);

                mallframes.add(new videomodel("Frame ", keytype,count,keyValue));
                if (count == currentframenumber) {
                    lastframehash=null;
                    mvideoframes.add(new videomodel("Frame ", keytype, currentframenumber,keyValue));
                    if(! selectedhaeshes.trim().isEmpty())
                        selectedhaeshes=selectedhaeshes+"\n";

                    selectedhaeshes=selectedhaeshes+mvideoframes.get(mvideoframes.size()-1).gettitle()
                            +" "+ mvideoframes.get(mvideoframes.size()-1).getcurrentframenumber()+" "+
                            mvideoframes.get(mvideoframes.size()-1).getkeytype()+":"+" "+
                            mvideoframes.get(mvideoframes.size()-1).getkeyvalue();

                    currentframenumber = currentframenumber + frameduration;
                }
                else
                {
                    lastframehash=new videomodel("Last Frame ",keytype,count,keyValue);
                }
                count++;
                if(suspendframequeue)
                {
                    selectedmetrics="";
                    selectedhaeshes="";
                    mhashesitems.clear();
                    mhashesadapter.notifyDataSetChanged();
                    break;
                }
            }

            if(! suspendframequeue)
            {
                if(lastframehash != null)
                {
                    mvideoframes.add(lastframehash);
                    if(! selectedhaeshes.trim().isEmpty())
                        selectedhaeshes=selectedhaeshes+"\n";

                    selectedhaeshes=selectedhaeshes+mvideoframes.get(mvideoframes.size()-1).gettitle()
                            +" "+ mvideoframes.get(mvideoframes.size()-1).getcurrentframenumber()+" "+
                            mvideoframes.get(mvideoframes.size()-1).getkeytype()+":"+" "+
                            mvideoframes.get(mvideoframes.size()-1).getkeyvalue();
                }
                else
                {
                    if(mvideoframes.size() > 1)
                    {
                        mvideoframes.get(mvideoframes.size()-1).settitle("Last Frame ");
                        if(! selectedhaeshes.trim().isEmpty())
                            selectedhaeshes=selectedhaeshes+"\n";

                        selectedhaeshes=selectedhaeshes+mvideoframes.get(mvideoframes.size()-1).gettitle()
                                +" "+ mvideoframes.get(mvideoframes.size()-1).getcurrentframenumber()+" "+
                                mvideoframes.get(mvideoframes.size()-1).getkeytype()+":"+" "+
                                mvideoframes.get(mvideoframes.size()-1).getkeyvalue();
                    }
                }
            }

            ishashprocessing=false;
            grabber.flush();

        }catch (Exception e)
        {
            e.printStackTrace();
            ishashprocessing=false;
        }

        suspendframequeue=false;
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
                if(isnewvideofound)
                {
                    if((! suspendframequeue) && (! suspendbitmapqueue))
                    {
                        isnewvideofound=false;
                        selectedmetrics="";
                        selectedhaeshes="";
                        mhashesitems.clear();
                        mhashesadapter.notifyDataSetChanged();

                        new Thread(){
                            public void run(){
                                setVideoAdapter();
                            }
                        }.start();
                        new Thread(){
                            public void run(){
                                getFramesBitmap();
                            }
                        }.start();
                    }
                }

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
        if(n> metricmainarraylist.size() || isvideocompleted)
            n=metricmainarraylist.size();

        Log.e("Current duration ",""+n+" "+currentduration+" "+metricmainarraylist.size());

        for(int i=0;i<n;i++)
        {
            if(! metricmainarraylist.get(i).isIsupdated())
            {
                metricmainarraylist.get(i).setIsupdated(true);
                double latt=0,longg=0;
                ArrayList<metricmodel> metricItemArraylist = metricmainarraylist.get(i).getMetricItemArraylist();
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


                selectedmetrics=selectedmetrics+"\n";

                if(mmetricsitems.size() == 0 && (! selectedmetrics.toString().trim().isEmpty()))
                {
                    mmetricsitems.add(new videomodel(selectedmetrics));
                    mmetricesadapter.notifyItemChanged(mmetricsitems.size()-1);
                }

                if((player != null) && (! player.isPlaying()) && (! selectedmetrics.toString().trim().isEmpty()))
                {
                    mmetricsitems.add(new videomodel(selectedmetrics));
                    mmetricesadapter.notifyItemChanged(mmetricsitems.size()-1);
                    selectedmetrics="";
                }
            }
        }

        if(fragment_graphic_container .getVisibility() == View.VISIBLE)
        {
            if(fragmentgraphic != null)
                fragmentgraphic.setmetricesdata();

        }

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        isvideocompleted=true;
        maxincreasevideoduration= audioduration;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(player != null )
                {
                    player.seekTo(0);
                    playpausebutton.setImageResource(R.drawable.play);
                }
            }
        },200);

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
        mVisualizer = new Visualizer(player.getAudioSessionId());

        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);

        mVisualizer.setDataCaptureListener(
                new Visualizer.OnDataCaptureListener() {
                    public void onWaveFormDataCapture(Visualizer visualizer,
                                                      byte[] bytes, int samplingRate) {

                        myvisualizerviewmedia.updateVisualizer(bytes);

                    }

                    public void onFftDataCapture(Visualizer visualizer,
                                                 byte[] bytes, int samplingRate) {
                    }
                }, Visualizer.getMaxCaptureRate() / 2, true, false);
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
           txt_metrics.setText("");
           txt_hashes.setText("");
           isnewvideofound = true;
           audioduration = 0;
           playpausebutton.setImageResource(R.drawable.play);
           rlcontrollerview.setVisibility(View.VISIBLE);
           playerposition = 0;
           righthandle.setVisibility(View.VISIBLE);
           setupaudioplayer(Uri.parse(audiourl));
       }
   }
}