package com.cryptoserver.composer.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
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
import android.widget.TextView;

import com.cryptoserver.composer.BuildConfig;
import com.cryptoserver.composer.R;
import com.cryptoserver.composer.adapter.videoframeadapter;
import com.cryptoserver.composer.applicationviavideocomposer;
import com.cryptoserver.composer.database.databasemanager;
import com.cryptoserver.composer.interfaces.adapteritemclick;
import com.cryptoserver.composer.models.arraycontainer;
import com.cryptoserver.composer.models.metadatahash;
import com.cryptoserver.composer.models.metricmodel;
import com.cryptoserver.composer.models.videomodel;
import com.cryptoserver.composer.models.wavevisualizer;
import com.cryptoserver.composer.utils.common;
import com.cryptoserver.composer.utils.config;
import com.cryptoserver.composer.utils.md5;
import com.cryptoserver.composer.utils.progressdialog;
import com.cryptoserver.composer.utils.sha;
import com.cryptoserver.composer.utils.videocontrollerview;
import com.cryptoserver.composer.utils.xdata;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by devesh on 21/8/18.
 */

public class composervideoplayerfragment extends basefragment implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener,MediaPlayer.OnCompletionListener, View.OnTouchListener,videocontrollerview.MediaPlayerControl, View.OnClickListener {

    @BindView(R.id.layout_drawer)
    LinearLayout layout_drawer;
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
    @BindView(R.id.relativedrawerlayout)
    RelativeLayout relativedrawerlayout;

    private String mediafilepath = null;
    private SurfaceView videoSurface;
    private MediaPlayer player;
    private videocontrollerview controller;
    private View rootview = null;
    private String selectedmetrics = "";
    private ImageView handleimageview, righthandle;
    private LinearLayout linearLayout;
    private String keytype = config.prefs_md5;
    private long currentframenumber = 0, playerposition = 0;
    private long frameduration = 15;
    private int REQUESTCODE_PICK = 201;
    private static final int request_read_external_storage = 1;
    private Uri selectedvideouri = null;
    private boolean issurafcedestroyed = false;
    private boolean isscrubbing = true;
    private ArrayList<arraycontainer> metricmainarraylist = new ArrayList<>();
    private Handler myHandler;
    private Runnable myRunnable;
    int controllerheight;
    RelativeLayout rlhandle;

    private Handler mymideahandler = new Handler();
    private Runnable mymediarunnable;
    private long videoduration = 0, framesegment = 0, currentvideoduration = 0, maxincreasevideoduration = 0, currentvideodurationseconds = 0, lastgetframe = 0;
    private boolean suspendframequeue = false, suspendbitmapqueue = false, isnewvideofound = false;
    private boolean isdraweropen = false;
    private String selectedhaeshes = "";
    private ArrayList<videomodel> mainvideoframes = new ArrayList<>();
    private ArrayList<videomodel> mvideoframes = new ArrayList<>();
    private ArrayList<videomodel> mallframes = new ArrayList<>();
    private ArrayList<videomodel> mmetricsitems = new ArrayList<>();
    private ArrayList<videomodel> mhashesitems = new ArrayList<>();
    private videoframeadapter mmetricesadapter, mhashesadapter;
    private graphicalfragment fragmentgraphic;
    private boolean isinbackground = false;
    private LinearLayoutManager mLayoutManager;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    public int selectedsection = 1;
    public boolean isvideocompleted = false;
    public int flingactionmindstvac;
    private final int flingactionmindspdvac = 10;
    String[] soundamplitudealuearray;
    private Visualizer mVisualizer;
    ArrayList<wavevisualizer> wavevisualizerslist = new ArrayList<>();
    private BroadcastReceiver getmetadatabroadcastreceiver,getencryptionmetadatabroadcastreceiver;
    DrawerLayout mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    public int getlayoutid() {
        return R.layout.full_screen_video_composer;
    }

    public composervideoplayerfragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        if (rootview == null) {
            rootview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, rootview);

            gethelper().setrecordingrunning(false);
            videoSurface = (SurfaceView) findViewById(R.id.videoSurface);
            linearLayout = rootview.findViewById(R.id.content);
            handleimageview = rootview.findViewById(R.id.handle);
            righthandle = rootview.findViewById(R.id.righthandle);
            rlhandle =(RelativeLayout) rootview.findViewById(R.id.rlhandle);

            mDrawer = (DrawerLayout) rootview.findViewById(R.id.drawer_layout);
            mDrawerToggle = new ActionBarDrawerToggle(
                    getActivity(), mDrawer, R.string.drawer_open, R.string.drawer_close);
            // Where do I put this?
            mDrawerToggle.syncState();
            mDrawer.setScrimColor(getResources().getColor(android.R.color.transparent));

            textfetchdata.setVisibility(View.VISIBLE);
            {
                mhashesadapter = new videoframeadapter(applicationviavideocomposer.getactivity(), mmetricsitems, new adapteritemclick() {
                    @Override
                    public void onItemClicked(Object object) {

                    }

                    @Override
                    public void onItemClicked(Object object, int type) {

                    }
                });
                mLayoutManager = new LinearLayoutManager(applicationviavideocomposer.getactivity());
                recyview_metrices.setLayoutManager(mLayoutManager);
                recyview_metrices.setItemAnimator(new DefaultItemAnimator());
                recyview_metrices.setAdapter(mhashesadapter);
                implementscrolllistener();
            }
            flingactionmindstvac = common.getdrawerswipearea();
            {
                mmetricesadapter = new videoframeadapter(applicationviavideocomposer.getactivity(), mhashesitems, new adapteritemclick() {
                    @Override
                    public void onItemClicked(Object object) {

                    }

                    @Override
                    public void onItemClicked(Object object, int type) {

                    }
                });
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(applicationviavideocomposer.getactivity());
                recyview_hashes.setLayoutManager(mLayoutManager);
                recyview_hashes.setItemAnimator(new DefaultItemAnimator());
                recyview_hashes.setAdapter(mmetricesadapter);
            }

            frameduration = checkframeduration();
            keytype = checkkey();

            SurfaceHolder videoHolder = videoSurface.getHolder();
            videoHolder.addCallback(this);

            handleimageview.setOnTouchListener(this);
            righthandle.setOnTouchListener(this);
            //navigationdrawer.setOnTouchListener(this);

            txtSlot1.setOnClickListener(this);
            txtSlot2.setOnClickListener(this);
            txtSlot3.setOnClickListener(this);

            rlhandle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (player != null && (!(mDrawer.isDrawerOpen(GravityCompat.START))))
                              hideshowcontroller();

                }
            });

            resetButtonViews(txtSlot1, txtSlot2, txtSlot3);
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

            mediafilepath = xdata.getinstance().getSetting("selectedvideourl");

            if (mediafilepath != null && (!mediafilepath.isEmpty())) {
                mvideoframes.clear();
                mainvideoframes.clear();
                mallframes.clear();
                txt_metrics.setText("");
                txt_hashes.setText("");
                selectedhaeshes = "";
                selectedmetrics = "";
                isnewvideofound = true;

                if (fragmentgraphic == null) {
                    fragmentgraphic = new graphicalfragment();

                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                    transaction.add(R.id.fragment_graphic_container, fragmentgraphic);
                    transaction.commit();
                }

                setupVideoPlayer();
                gethash();

                setmetriceshashesdata();
            } else {
                Log.e("VideoURI ", "" + "Null");
                gethelper().onBack();
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
                visibleItemCount = mLayoutManager.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();
                if ((visibleItemCount + pastVisiblesItems) == totalItemCount) {
                    if (selectedmetrics.toString().trim().length() > 0) {
                        mmetricsitems.add(new videomodel(selectedmetrics));
                        mmetricesadapter.notifyItemChanged(mmetricsitems.size() - 1);
                        selectedmetrics = "";
                    }
                }
            }
        });
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

    public void setdata(String VIDEO_URL) {
        //this.mediafilepath = mediafilepath;
    }


    public void gethash() {
        if (!xdata.getinstance().getSetting(config.framecount).trim().isEmpty())

            frameduration = Integer.parseInt(xdata.getinstance().getSetting(config.framecount));


        if (xdata.getinstance().getSetting(config.hashtype).equalsIgnoreCase(config.prefs_md5) ||
                xdata.getinstance().getSetting(config.hashtype).trim().isEmpty()) {
            keytype = config.prefs_md5;
        } else if (xdata.getinstance().getSetting(config.hashtype).equalsIgnoreCase(config.prefs_md5_salt)) {
            keytype = config.prefs_md5_salt;
        } else if (xdata.getinstance().getSetting(config.hashtype).equalsIgnoreCase(config.prefs_sha)) {
            keytype = config.prefs_sha;
        } else if (xdata.getinstance().getSetting(config.hashtype).equalsIgnoreCase(config.prefs_sha_salt)) {
            keytype = config.prefs_sha_salt;
        }

        if (mediafilepath != null && (!mediafilepath.isEmpty())) {
            currentframenumber = 0;
            mvideoframes.clear();
            mallframes.clear();
            Thread thread = new Thread() {
                public void run() {
                    getmediadata();
                }
            };
            thread.start();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_slot1:
                if (selectedsection != 1) {
                    selectedsection = 1;
                    scrollview_metrices.setVisibility(View.INVISIBLE);
                    scrollview_hashes.setVisibility(View.INVISIBLE);
                    fragment_graphic_container.setVisibility(View.INVISIBLE);

                    recyview_hashes.setVisibility(View.VISIBLE);
                    recyview_metrices.setVisibility(View.INVISIBLE);

                    txt_metrics.setVisibility(View.INVISIBLE);
                    txt_hashes.setVisibility(View.INVISIBLE);
                    txt_metrics.setVisibility(View.INVISIBLE);
                    resetButtonViews(txtSlot1, txtSlot2, txtSlot3);
                }

                break;

            case R.id.txt_slot2:
                if (selectedsection != 2) {
                    selectedsection = 2;
                    scrollview_metrices.setVisibility(View.INVISIBLE);
                    scrollview_hashes.setVisibility(View.INVISIBLE);
                    fragment_graphic_container.setVisibility(View.INVISIBLE);

                    txt_hashes.setVisibility(View.INVISIBLE);
                    txt_metrics.setVisibility(View.INVISIBLE);

                    recyview_metrices.setVisibility(View.VISIBLE);
                    recyview_hashes.setVisibility(View.INVISIBLE);

                    resetButtonViews(txtSlot2, txtSlot1, txtSlot3);
                }

                break;

            case R.id.txt_slot3:
                if (selectedsection != 3) {
                    selectedsection = 3;
                    fragment_graphic_container.setVisibility(View.VISIBLE);
                    scrollview_metrices.setVisibility(View.INVISIBLE);
                    scrollview_hashes.setVisibility(View.INVISIBLE);
                    recyview_metrices.setVisibility(View.INVISIBLE);
                    recyview_hashes.setVisibility(View.INVISIBLE);
                    txt_hashes.setVisibility(View.INVISIBLE);
                    txt_metrics.setVisibility(View.INVISIBLE);
                    resetButtonViews(txtSlot3, txtSlot1, txtSlot2);
                }
                break;
        }
    }

    @Override
    public void oncurrentlocationchanged(Location location) {
        super.oncurrentlocationchanged(location);
    }

    public void resetButtonViews(TextView view1, TextView view2, TextView view3) {
        view1.setBackgroundResource(R.color.videolist_background);
        view1.setTextColor(ContextCompat.getColor(applicationviavideocomposer.getactivity(), R.color.white));

        view2.setBackgroundResource(R.color.white);
        view2.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.videolist_background));

        view3.setBackgroundResource(R.color.white);
        view3.setTextColor(applicationviavideocomposer.getactivity().getResources().getColor(R.color.videolist_background));
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
        switch (view.getId()) {
            /*case R.id.handle:
                flingswipe.onTouchEvent(motionEvent);
                break;

            case R.id.righthandle:
                flingswipe.onTouchEvent(motionEvent);
                break;*/
            /*case R.id.drawer_layout: {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (player != null && (!(navigationdrawer.isDrawerOpen(GravityCompat.START)))) {
                            hideshowcontroller();
                        }
                        break;
                }
            }
            break;*/
        }
        return true;
    }

    GestureDetector flingswipe = new GestureDetector(applicationviavideocomposer.getactivity(), new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onFling(MotionEvent fstMsnEvtPsgVal, MotionEvent lstMsnEvtPsgVal, float flingActionXcoSpdPsgVal,
                               float flingActionYcoSpdPsgVal) {
            if (fstMsnEvtPsgVal.getX() - lstMsnEvtPsgVal.getX() > flingactionmindstvac && Math.abs(flingActionXcoSpdPsgVal) >
                    flingactionmindspdvac) {
                // TskTdo :=> On Right to Left fling
                swiperighttoleft();
                return false;
            } else if (lstMsnEvtPsgVal.getX() - fstMsnEvtPsgVal.getX() > flingactionmindstvac && Math.abs(flingActionXcoSpdPsgVal) >
                    flingactionmindspdvac) {
                // TskTdo :=> On Left to Right fling
                swipelefttoright();
                return false;
            }

            if (fstMsnEvtPsgVal.getY() - lstMsnEvtPsgVal.getY() > flingactionmindstvac && Math.abs(flingActionYcoSpdPsgVal) >
                    flingactionmindspdvac) {
                // TskTdo :=> On Bottom to Top fling

                return false;
            } else if (lstMsnEvtPsgVal.getY() - fstMsnEvtPsgVal.getY() > flingactionmindstvac && Math.abs(flingActionYcoSpdPsgVal) >
                    flingactionmindspdvac) {
                // TskTdo :=> On Top to Bottom fling

                return false;
            }
            return false;
        }
    });

    public void swipelefttoright() {
        isdraweropen = true;
        Animation rightswipe = AnimationUtils.loadAnimation(applicationviavideocomposer.getactivity(), R.anim.right_slide);
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

    public void swiperighttoleft() {
        isdraweropen = false;
        Animation leftswipe = AnimationUtils.loadAnimation(applicationviavideocomposer.getactivity(), R.anim.left_slide);
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

    public void hideshowcontroller() {
        if (mDrawer.isDrawerOpen(GravityCompat.START))
            return;

        if (controller != null && controller.controllersview != null) {
            if (controller.controllersview != null && controller.controllersview.getVisibility() == View.VISIBLE) {
                handleimageview.setVisibility(View.GONE);
                gethelper().updateactionbar(0);
                controller.controllersview.setVisibility(View.GONE);

            } else {
                handleimageview.setVisibility(View.VISIBLE);
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
        if (myHandler != null && myRunnable != null)
            myHandler.removeCallbacks(myRunnable);
        destroyvideoplayer();
    }

    public void destroyvideoplayer() {
        if (player != null) {
            playerposition = player.getCurrentPosition();
            player.pause();
            player.stop();
            player.release();
            player = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        progressdialog.dismisswaitdialog();
    }

    @Override
    public void onResume() {
        super.onResume();
        isinbackground = false;
        try {
            if (!issurafcedestroyed)
                return;

            player = new MediaPlayer();
            if (controller != null)
                controller.removeAllViews();

            controller = new videocontrollerview(applicationviavideocomposer.getactivity(), mitemclick, isscrubbing);
            mediafilepath = xdata.getinstance().getSetting("selectedvideourl");
            if (mediafilepath != null && (!mediafilepath.isEmpty())) {
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                Uri uri= FileProvider.getUriForFile(applicationviavideocomposer.getactivity(),
                        BuildConfig.APPLICATION_ID + ".provider", new File(mediafilepath));
                player.setDataSource(applicationviavideocomposer.getactivity(), uri);
                player.prepareAsync();
                player.setOnPreparedListener(this);
                player.setOnCompletionListener(this);

                if (player != null) {
                    changeactionbarcolor();
                    initAudio();
                    fragmentgraphic.setvisualizerwave();
                    wavevisualizerslist.clear();
                }


                if (!keytype.equalsIgnoreCase(checkkey()) || (frameduration != checkframeduration())) {
                    frameduration = checkframeduration();
                    keytype = checkkey();
                    mvideoframes.clear();
                    mainvideoframes.clear();
                    mallframes.clear();
                    isnewvideofound = true;
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
        if (player != null) {
            //holder.setFixedSize(1000,500);
            player.setDisplay(holder);
        }
        issurafcedestroyed = false;

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //   playerposition=0;
        if (player != null) {
            playerposition = player.getCurrentPosition();
            player.pause();
        }
        issurafcedestroyed = true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        isvideocompleted = false;
        maxincreasevideoduration = 0;
        controller.setMediaPlayer(this);
        controller.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));
        videoduration = mp.getDuration();

        try {
            if (playerposition > 0) {
                mp.seekTo((int) playerposition);
                player.seekTo((int) playerposition);
            } else {
                mp.seekTo(100);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        controller.show();
        controller.post(new Runnable() {
            @Override
            public void run() {
                controllerheight = controller.getHeight();
                Log.e("getheight",""+controllerheight);

                RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                layoutParams.setMargins(0 ,0,0,controllerheight);
                relativedrawerlayout.setLayoutParams(layoutParams);

            }
        });

        if (fragmentgraphic != null)
            fragmentgraphic.setmediaplayer(true, null);
    }

    adapteritemclick mitemclick = new adapteritemclick() {
        @Override
        public void onItemClicked(Object object) {
           hideshowcontroller();
        }

        @Override
        public void onItemClicked(Object object, int type) {

        }
    };

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
            if (player != null) {
                if (player.getCurrentPosition() > maxincreasevideoduration)
                    maxincreasevideoduration = player.getCurrentPosition();

                return player.getCurrentPosition();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int getDuration() {
        if (player != null)
            return player.getDuration();
        return 0;
    }

    @Override
    public boolean isPlaying() {
        if (player != null)
            return player.isPlaying();

        return false;
    }

    @Override
    public void pause() {
        if (player != null)
            player.pause();
    }

    @Override
    public void seekTo(int i) {
        if (player != null)
            player.seekTo(i);
    }

    @Override
    public void start() {
        if (player != null) {
            player.start();
            player.setOnCompletionListener(this);
        }

    }

    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    public void toggleFullScreen() {
    }

    public String getkeyvalue(byte[] data) {
        String value = "";
        String salt = "";

        switch (keytype) {
            case config.prefs_md5:
                value = md5.calculatebytemd5(data);
                break;

            case config.prefs_md5_salt:
                salt = xdata.getinstance().getSetting(config.prefs_md5_salt);
                if (!salt.trim().isEmpty()) {
                    byte[] saltbytes = salt.getBytes();
                    try {
                        ByteArrayOutputStream outputstream = new ByteArrayOutputStream();
                        outputstream.write(saltbytes);
                        outputstream.write(data);
                        byte updatedarray[] = outputstream.toByteArray();
                        value = md5.calculatebytemd5(updatedarray);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    value = md5.calculatebytemd5(data);
                }

                break;
            case config.prefs_sha:
                value = sha.sha1(data);
                break;
            case config.prefs_sha_salt:
                salt = xdata.getinstance().getSetting(config.prefs_sha_salt);
                if (!salt.trim().isEmpty()) {
                    byte[] saltbytes = salt.getBytes();
                    try {
                        ByteArrayOutputStream outputstream = new ByteArrayOutputStream();
                        outputstream.write(saltbytes);
                        outputstream.write(data);
                        byte updatedarray[] = outputstream.toByteArray();
                        value = sha.sha1(updatedarray);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    value = sha.sha1(data);
                }
                break;
        }
        return value;
    }


    @Override
    public void onHeaderBtnClick(int btnid) {
        super.onHeaderBtnClick(btnid);
        switch (btnid) {
            case R.id.img_share_icon:
                if (mediafilepath != null && (!mediafilepath.isEmpty())) {
                    common.sharevideo(applicationviavideocomposer.getactivity(), mediafilepath);
                }
                break;

            case R.id.img_menu:
                gethelper().onBack();
                break;

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

    public void opengallery() {
        destroyvideoplayer();
        Intent intent;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        } else {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.INTERNAL_CONTENT_URI);
        }
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUESTCODE_PICK);
    }

    public int checkframeduration() {
        int frameduration = 15;

        if (!xdata.getinstance().getSetting(config.framecount).trim().isEmpty())
            frameduration = Integer.parseInt(xdata.getinstance().getSetting(config.framecount));

        return frameduration;
    }

    public String checkkey() {
        String key = "";
        if (xdata.getinstance().getSetting(config.hashtype).equalsIgnoreCase(config.prefs_md5) ||
                xdata.getinstance().getSetting(config.hashtype).trim().isEmpty()) {
            key = config.prefs_md5;
        } else if (xdata.getinstance().getSetting(config.hashtype).equalsIgnoreCase(config.prefs_md5_salt)) {
            key = config.prefs_md5_salt;
        } else if (xdata.getinstance().getSetting(config.hashtype).equalsIgnoreCase(config.prefs_sha)) {
            key = config.prefs_sha;
        } else if (xdata.getinstance().getSetting(config.hashtype).equalsIgnoreCase(config.prefs_sha_salt)) {
            key = config.prefs_sha_salt;
        }
        return key;
    }


    public void setupVideoPlayer() {
        try {
            player = new MediaPlayer();
            if (controller != null)
                controller.removeAllViews();

            controller = new videocontrollerview(applicationviavideocomposer.getactivity(), mitemclick, isscrubbing);
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            Uri uri= FileProvider.getUriForFile(applicationviavideocomposer.getactivity(),
                    BuildConfig.APPLICATION_ID + ".provider", new File(mediafilepath));
            player.setDataSource(applicationviavideocomposer.getactivity(), uri);
            player.prepareAsync();
            player.setOnPreparedListener(this);
            player.setOnCompletionListener(this);
            if (player != null) {
                changeactionbarcolor();
                initAudio();
                fragmentgraphic.setvisualizerwave();
                wavevisualizerslist.clear();
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

    public void changeactionbarcolor() {
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
                if(! isinbackground)
                {
                    boolean graphicopen=false;
                    if(mDrawer.isDrawerOpen(GravityCompat.START))
                    {
                        if((recyview_hashes.getVisibility() == View.VISIBLE) && (! selectedhaeshes.trim().isEmpty()))
                        {
                            applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    mhashesitems.add(new videomodel(selectedhaeshes));
                                    mhashesadapter.notifyItemChanged(mhashesitems.size()-1);
                                    selectedhaeshes="";

                                }
                            });
                        }

                        if((fragment_graphic_container.getVisibility() == View.VISIBLE))
                            graphicopen=true;

                        setmetricesgraphicaldata();
                    }
                    if(fragmentgraphic != null)
                        fragmentgraphic.setdrawerproperty(graphicopen);
                }
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

        for(int i=0;i<n;i++)
        {
            if(! metricmainarraylist.get(i).isIsupdated())
            {
                metricmainarraylist.get(i).setIsupdated(true);
                double latt=0,longg=0;
                ArrayList<metricmodel> metricItemArraylist = metricmainarraylist.get(i).getMetricItemArraylist();

                fragmentgraphic.getencryptiondata(metricmainarraylist.get(i).getHashmethod(),metricmainarraylist.get(i).getVideostarttransactionid(),
                        metricmainarraylist.get(i).getValuehash(),metricmainarraylist.get(i).getMetahash());

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
            if(fragmentgraphic != null){
                fragmentgraphic.setmetricesdata();
                fragmentgraphic.getvisualizerwavecomposer(wavevisualizerslist);

            }
        }
    }

    /*public void setmetricesgraphicaldata()
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

        for(int i=0;i<n;i++)
        {
            if(! metricmainarraylist.get(i).isIsupdated())
            {
                metricmainarraylist.get(i).setIsupdated(true);
                double latt=0,longg=0;
                ArrayList<metricmodel> metricItemArraylist = metricmainarraylist.get(i).getMetricItemArraylist();

                fragmentgraphic.getencryptiondata(metricmainarraylist.get(i).getHashmethod(),metricmainarraylist.get(i).getVideostarttransactionid(),
                        metricmainarraylist.get(i).getValuehash(),metricmainarraylist.get(i).getMetahash());

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
            if(fragmentgraphic != null){
                fragmentgraphic.setmetricesdata();
                fragmentgraphic.getvisualizerwavecomposer(wavevisualizerslist);

            }
        }
    }*/



    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        isvideocompleted=true;
        controller.setplaypauuse();
        maxincreasevideoduration=videoduration;
        currentvideoduration = videoduration;
        currentvideodurationseconds = currentvideoduration / 1000;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(player != null && controller!= null)
                {
                    player.seekTo(0);
                    wavevisualizerslist.clear();
                    controller.setProgress(0,false);
                }
            }
        },200);

    }

    private void initAudio() {

        if(player != null){

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
            player
                    .setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            //mVisualizer.setEnabled(false);
                        }
                    });
            //mMediaPlayer.start();
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
        isinbackground = true;
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
        }).start();
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
                                        fragmentgraphic.setvisualizerwave();
                                        wavevisualizerslist.clear();
                                    }

                                    if(amplitude >=  35)
                                        amplitude = amplitude*2;

                                    int x= amplitude * 100;

                                    wavevisualizerslist.add(new wavevisualizer(x,true));
                                }
                                //Log.e("waveform=",""+rms);
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
}
