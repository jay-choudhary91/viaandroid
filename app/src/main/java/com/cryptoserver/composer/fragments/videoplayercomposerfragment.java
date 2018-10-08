package com.cryptoserver.composer.fragments;

import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
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
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.adapter.videoframeadapter;
import com.cryptoserver.composer.applicationviavideocomposer;
import com.cryptoserver.composer.interfaces.adapteritemclick;
import com.cryptoserver.composer.models.frame;
import com.cryptoserver.composer.models.metricmodel;
import com.cryptoserver.composer.models.videomodel;
import com.cryptoserver.composer.utils.common;
import com.cryptoserver.composer.utils.config;
import com.cryptoserver.composer.utils.customffmpegframegrabber;
import com.cryptoserver.composer.utils.md5;
import com.cryptoserver.composer.utils.progressdialog;
import com.cryptoserver.composer.utils.sha;
import com.cryptoserver.composer.utils.videocontrollerview;
import com.cryptoserver.composer.utils.xdata;

import org.bytedeco.javacpp.avutil;
import org.bytedeco.javacv.Frame;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by devesh on 21/8/18.
 */

public class videoplayercomposerfragment extends basefragment implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener,MediaPlayer.OnCompletionListener, videocontrollerview.MediaPlayerControl, View.OnTouchListener, View.OnClickListener {


    @BindView(R.id.layout_drawer)
    LinearLayout layout_drawer;
    @BindView(R.id.recyview_metrices)
    RecyclerView recyview_metrices;
    @BindView(R.id.recyview_item)
    RecyclerView recyview_hashes;
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
    @BindView(R.id.fragment_graphic_container)
    FrameLayout fragment_graphic_container;

    private String VIDEO_URL = null;
    RelativeLayout showcontrollers;
    SurfaceView videoSurface;
    String selectedmetrics="";
    MediaPlayer player;
    videocontrollerview controller;
    View rootview = null;
    ImageView handleimageview,righthandle;
    LinearLayout linearLayout;
    String keytype =config.prefs_md5;
    long currentframenumber =0,playerposition=0;
    long frameduration =15, mframetorecordcount =0;
    ArrayList<videomodel> mmetricsitems =new ArrayList<>();
    ArrayList<videomodel> mhashesitems =new ArrayList<>();


    ArrayList<videomodel> mvideoframes =new ArrayList<>();
    ArrayList<videomodel> mallframes =new ArrayList<>();
    ArrayList<frame> mbitmaplist =new ArrayList<>();
    boolean ishashprocessing=false;
    boolean issurafcedestroyed=false;
    boolean isscrubbing=false;
    private ArrayList<metricmodel> metricItemArraylist = new ArrayList<>();
    ArrayList<videomodel> mainvideoframes =new ArrayList<>();
    private Handler myHandler;
    private Runnable myRunnable;
    long framecount=0;
    long videoduration =0,framesegment=0,currentvideoduration=0,currentvideodurationseconds=0,lastgetframe=0;
    boolean frameprocess=false,showlastframe = false;
    private boolean isdraweropen=false;
    String selectedhaeshes="";
    private int lastmetricescount=0;
    videoframeadapter mmetricesadapter,mhashesadapter;
    private graphicalfragment fragmentgraphic;

    @Override
    public int getlayoutid() {
        return R.layout.full_screen_video_composer;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        if(rootview == null) {
            rootview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, rootview);

            gethelper().updateactionbar(1, applicationviavideocomposer.getactivity().getResources().getColor(R.color.videoPlayer_header));

            //gethelper().updateactionbar(0);
            videoSurface = (SurfaceView) findViewById(R.id.videoSurface);
            linearLayout=rootview.findViewById(R.id.content);
            handleimageview=rootview.findViewById(R.id.handle);
            righthandle=rootview.findViewById(R.id.righthandle);
            showcontrollers=rootview.findViewById(R.id.video_container);

        }

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
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            recyview_hashes.setLayoutManager(mLayoutManager);
            recyview_hashes.setItemAnimator(new DefaultItemAnimator());
            recyview_hashes.setAdapter(mmetricesadapter);
        }



        videoSurface.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        if(player !=null && (! isdraweropen))
                            hideshowcontroller();
                        break;
                }
                return false;
            }
        });

        setupVideoPlayer();
        gethash();

        handleimageview.setOnTouchListener(this);
        righthandle.setOnTouchListener(this);

        txtSlot1.setOnClickListener(this);
        txtSlot2.setOnClickListener(this);
        txtSlot3.setOnClickListener(this);

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

        setmetriceshashesdata();
        return rootview;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.txt_slot1:
                scrollview_metrices.setVisibility(View.INVISIBLE);
                scrollview_hashes.setVisibility(View.INVISIBLE);
                fragment_graphic_container.setVisibility(View.INVISIBLE);

                recyview_hashes.setVisibility(View.VISIBLE);
                recyview_metrices.setVisibility(View.INVISIBLE);

                txt_metrics.setVisibility(View.INVISIBLE);
                txt_hashes.setVisibility(View.INVISIBLE);
                txt_metrics.setVisibility(View.INVISIBLE);
                resetButtonViews(txtSlot1,txtSlot2,txtSlot3);
                break;

            case R.id.txt_slot2:
                scrollview_metrices.setVisibility(View.INVISIBLE);
                scrollview_hashes.setVisibility(View.INVISIBLE);
                fragment_graphic_container.setVisibility(View.INVISIBLE);

                txt_hashes.setVisibility(View.INVISIBLE);
                txt_metrics.setVisibility(View.INVISIBLE);

                recyview_metrices.setVisibility(View.VISIBLE);
                recyview_hashes.setVisibility(View.INVISIBLE);

                resetButtonViews(txtSlot2,txtSlot1,txtSlot3);
                break;

            case R.id.txt_slot3:
                fragment_graphic_container.setVisibility(View.VISIBLE);
                scrollview_metrices.setVisibility(View.INVISIBLE);
                scrollview_hashes.setVisibility(View.INVISIBLE);
                recyview_metrices.setVisibility(View.INVISIBLE);
                recyview_hashes.setVisibility(View.INVISIBLE);
                txt_hashes.setVisibility(View.INVISIBLE);
                txt_metrics.setVisibility(View.INVISIBLE);
                resetButtonViews(txtSlot3,txtSlot1,txtSlot2);

                if(fragmentgraphic == null)
                {
                    fragmentgraphic  = new graphicalfragment();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.add(R.id.fragment_graphic_container,fragmentgraphic);
                    transaction.commit();
                }
                break;
        }
    }

    @Override
    public void oncurrentlocationchanged(Location location) {
        super.oncurrentlocationchanged(location);
        if(fragmentgraphic != null)
            fragmentgraphic.locationupdate(location,"");
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
        private static final int flingactionmindstvac = 60;
        private static final int flingactionmindspdvac = 100;

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

    public void hideshowcontroller()
    {
        if(isdraweropen)
            return;

        if(handleimageview.getVisibility() ==  (View.VISIBLE))
        {
            handleimageview.setVisibility(View.GONE);
            gethelper().updateactionbar(0);
            try {
                if(controller != null)
                {
                    if(controller.controllersview.getVisibility() == View.VISIBLE)
                        controller.controllersview.setVisibility(View.GONE);
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            handleimageview.setVisibility(View.VISIBLE);
            gethelper().updateactionbar(1);
            try {
                if(controller != null)
                {
                    if(controller.controllersview.getVisibility() == View.GONE)
                        controller.controllersview.setVisibility(View.VISIBLE);
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void onRestart() {
        //gethelper().updateactionbar(1);
        //handleimageview.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(myHandler != null && myRunnable != null)
            myHandler.removeCallbacks(myRunnable);
        if(player != null)
        {
            player.pause();
            player.stop();
            player.release();
            player=null;

            //player.release();
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

        try {
            if(! issurafcedestroyed)
                return;

            player = new MediaPlayer();
            if(controller != null)
                controller.removeAllViews();

            controller = new videocontrollerview(getActivity(),mitemclick,isscrubbing);

            if(VIDEO_URL != null && (! VIDEO_URL.isEmpty())){
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.setDataSource(VIDEO_URL);
                player.prepareAsync();
                player.setOnPreparedListener(this);
                player.setOnCompletionListener(this);
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
        playerposition=0;
        if (player != null) {
            playerposition=player.getCurrentPosition();
            player.pause();
        }
        issurafcedestroyed=true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        controller.setMediaPlayer(this);
        controller.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));
        //mp.start();
        videoduration=mp.getDuration();
        try {
            if(playerposition > 0)
            {
                mp.seekTo((int)playerposition);
            }
            else
            {
                mp.seekTo(100);
            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        controller.show();

    }
    adapteritemclick mitemclick=new adapteritemclick() {
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
            if(player != null)
            {
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
        {
            return player.getDuration();
        }
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
            Log.e("seek to  ",""+i);
            player.seekTo(i);
        }

    }

    @Override
    public void start() {
        if(player != null)
            player.start();
    }

    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    public void toggleFullScreen() {

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
                if(VIDEO_URL != null && (! VIDEO_URL.isEmpty())) {
                    common.shareMedia(getActivity(),VIDEO_URL);
                }
                break;

            case R.id.img_menu:
                gethelper().onBack();
                break;

        }
    }

    public void setupVideoPlayer()
    {
        try {
            SurfaceHolder videoHolder = videoSurface.getHolder();
            videoHolder.addCallback(this);

            player = new MediaPlayer();
            if(controller != null)
                controller.removeAllViews();

            controller = new videocontrollerview(getActivity(),mitemclick,isscrubbing);

            if(VIDEO_URL!=null){
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.setDataSource(VIDEO_URL);

                player.prepareAsync();
                player.setOnPreparedListener(this);
                player.setOnCompletionListener(this);
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


    public void setVideoAdapter() {
        int count = 1;
        ishashprocessing=true;
        currentframenumber=0;
        currentframenumber = currentframenumber + frameduration;
        try
        {
            customffmpegframegrabber grabber = new customffmpegframegrabber(VIDEO_URL);

            grabber.setPixelFormat(avutil.AV_PIX_FMT_RGB24);
            String format= common.getvideoformat(VIDEO_URL);
            if(format.equalsIgnoreCase("mp4"))
                grabber.setFormat(format);


            grabber.start();
            videomodel lastframehash=null;
            for(int i = 0; i<grabber.getLengthInFrames(); i++){
                Frame frame = grabber.grabImage();
                if (frame == null)
                    break;

                ByteBuffer buffer= ((ByteBuffer) frame.image[0].position(0));
                byte[] byteData = new byte[buffer.remaining()];
                buffer.get(byteData);

                String keyValue= getkeyvalue(byteData);

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

                    ArrayList<metricmodel> mlist = gethelper().getmetricarraylist();
                    //metricItemArraylist.addAll(mlist);

                    for(int j=0;j<mlist.size();j++)
                    {
                        if(mlist.get(j).isSelected())
                        {
                            selectedmetrics=selectedmetrics+"\n"+mlist.get(j).getMetricTrackKeyName()+" - "
                                    +mlist.get(j).getMetricTrackValue();
                        }
                    }
                    if(! selectedmetrics.trim().isEmpty())
                        selectedmetrics=selectedmetrics+"\n\n";

                    currentframenumber = currentframenumber + frameduration;

                }
                else
                {
                    lastframehash=new videomodel("Last Frame ",keytype,count,keyValue);
                }
                count++;
            }

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

            ishashprocessing=false;
            grabber.flush();

        }catch (Exception e)
        {
            e.printStackTrace();
            ishashprocessing=false;
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
                boolean graphicopen=false;
                if(isdraweropen)
                {
                    if((recyview_hashes.getVisibility() == View.VISIBLE) && (! selectedhaeshes.trim().isEmpty()))
                    {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //txt_hashes.append(selectedhaeshes);
                                mhashesitems.add(new videomodel(selectedhaeshes));
                                mhashesadapter.notifyItemChanged(mhashesitems.size()-1);
                                selectedhaeshes="";
                            }
                        });

                    }

                    if((recyview_metrices.getVisibility() == View.VISIBLE) && (! selectedmetrics.trim().isEmpty()))
                    {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //txt_metrics.append(selectedmetrics);
                                mmetricsitems.add(new videomodel(selectedmetrics));
                                mmetricesadapter.notifyItemChanged(mmetricsitems.size()-1);
                                selectedmetrics="";
                            }
                        });
                    }

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

    public void setdata(String VIDEO_URL){
        this.VIDEO_URL = VIDEO_URL;
    }

    public void gethash(){
        if(! xdata.getinstance().getSetting(config.framecount).trim().isEmpty())

            frameduration=Integer.parseInt(xdata.getinstance().getSetting(config.framecount));


        if(xdata.getinstance().getSetting(config.hashtype).equalsIgnoreCase(config.prefs_md5) ||
                xdata.getinstance().getSetting(config.hashtype).trim().isEmpty())
        {
            keytype=config.prefs_md5;
        }
        else if(xdata.getinstance().getSetting(config.hashtype).equalsIgnoreCase(config.prefs_md5_salt))
        {
            keytype=config.prefs_md5_salt;
        }
        else if(xdata.getinstance().getSetting(config.hashtype).equalsIgnoreCase(config.prefs_sha))
        {
            keytype=config.prefs_sha;
        }
        else if(xdata.getinstance().getSetting(config.hashtype).equalsIgnoreCase(config.prefs_sha_salt))
        {
            keytype=config.prefs_sha_salt;
        }

        mbitmaplist.clear();



        if(VIDEO_URL != null && (! VIDEO_URL.isEmpty())){
            currentframenumber=0;
            mvideoframes.clear();
            mallframes.clear();
            Thread thread = new Thread(){
                public void run(){
                    setVideoAdapter();
                }
            };
            thread.start();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        controller.setplaypauuse();
        currentvideoduration = videoduration;
        currentvideodurationseconds = currentvideoduration / 1000;
    }

}
