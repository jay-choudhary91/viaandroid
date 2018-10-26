package com.cryptoserver.composer.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.adapter.framebitmapadapter;
import com.cryptoserver.composer.adapter.videoframeadapter;
import com.cryptoserver.composer.applicationviavideocomposer;
import com.cryptoserver.composer.interfaces.adapteritemclick;
import com.cryptoserver.composer.metadata.MetaDataRead;
import com.cryptoserver.composer.models.arraycontainer;
import com.cryptoserver.composer.models.frame;
import com.cryptoserver.composer.models.metricmodel;
import com.cryptoserver.composer.models.videomodel;
import com.cryptoserver.composer.utils.centerlayoutmanager;
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
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

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

    private String VIDEO_URL = null;
    private SurfaceView videoSurface;
    private MediaPlayer player;
    private videocontrollerview controller;
    private View rootview = null;
    private String selectedmetrics="";
    private ImageView handleimageview,righthandle;
    private LinearLayout linearLayout;
    private String keytype =config.prefs_md5;
    private long currentframenumber =0,playerposition=0;
    private long frameduration =15;
    private int REQUESTCODE_PICK=201;
    private static final int request_read_external_storage = 1;
    private Uri selectedvideouri =null;
    private boolean issurafcedestroyed=false;
    private boolean isscrubbing=true;
    private ArrayList<arraycontainer> metricmainarraylist = new ArrayList<>();
    private Handler myHandler;
    private Runnable myRunnable;
    private long videoduration =0,framesegment=0,currentvideoduration=0,maxincreasevideoduration=0,currentvideodurationseconds=0,lastgetframe=0;
    private boolean suspendframequeue=false,suspendbitmapqueue = false,isnewvideofound=false;
    private boolean isdraweropen=false;
    private String selectedhaeshes="";
    private ArrayList<videomodel> mainvideoframes =new ArrayList<>();
    private ArrayList<videomodel> mvideoframes =new ArrayList<>();
    private ArrayList<videomodel> mallframes =new ArrayList<>();
    private ArrayList<videomodel> mmetricsitems =new ArrayList<>();
    private ArrayList<videomodel> mhashesitems =new ArrayList<>();
    private videoframeadapter mmetricesadapter,mhashesadapter;
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

            videoSurface = (SurfaceView) findViewById(R.id.videoSurface);
            linearLayout=rootview.findViewById(R.id.content);
            handleimageview=rootview.findViewById(R.id.handle);
            righthandle=rootview.findViewById(R.id.righthandle);

            {
                mhashesadapter = new videoframeadapter(applicationviavideocomposer.getactivity(), mmetricsitems, new adapteritemclick() {
                    @Override
                    public void onItemClicked(Object object) {

                    }

                    @Override
                    public void onItemClicked(Object object, int type) {

                    }
                });
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(applicationviavideocomposer.getactivity());
                recyview_metrices.setLayoutManager(mLayoutManager);
                recyview_metrices.setItemAnimator(new DefaultItemAnimator());
                recyview_metrices.setAdapter(mhashesadapter);
            }

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

            frameduration=checkframeduration();
            keytype=checkkey();

            SurfaceHolder videoHolder = videoSurface.getHolder();
            videoHolder.addCallback(this);

            handleimageview.setOnTouchListener(this);
            righthandle.setOnTouchListener(this);
            videoSurface.setOnTouchListener(this);

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
            fragment_graphic_container.setVisibility(View.INVISIBLE);

            if(VIDEO_URL != null && (! VIDEO_URL.isEmpty())){
                mvideoframes.clear();
                mainvideoframes.clear();
                mallframes.clear();
                txt_metrics.setText("");
                txt_hashes.setText("");
                selectedhaeshes="";
                selectedmetrics="";
                isnewvideofound=true;

                setupVideoPlayer();
                gethash();

                try {
                    String readmetadata= MetaDataRead.readmetadata(VIDEO_URL);
                    if(readmetadata != null)
                    {
                        metricmainarraylist.clear();
                        Log.e("Meta data values ",readmetadata);
                        JSONArray array=new JSONArray(readmetadata);

                        for(int j=0;j<array.length();j++)
                        {
                            ArrayList<metricmodel> metricItemArraylist = new ArrayList<>();
                            JSONArray subarray=array.getJSONArray(j);
                            for(int i=0;i<subarray.length();i++)
                            {

                                JSONObject object=subarray.getJSONObject(i);
                                Iterator<String> myIter = object.keys();
                                while (myIter.hasNext()) {
                                    String key = myIter.next();
                                    String value = object.optString(key);
                                    metricmodel model=new metricmodel();
                                    model.setMetricTrackKeyName(key);
                                    model.setMetricTrackValue(value);
                                    metricItemArraylist.add(model);
                                }
                            }
                            metricmainarraylist.add(new arraycontainer(metricItemArraylist));
                        }
                    }

                }catch (Exception e)
                {
                    e.printStackTrace();
                }


            }

            setmetriceshashesdata();

            if(fragmentgraphic == null) {
                fragmentgraphic = new graphicalfragment();

                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.add(R.id.fragment_graphic_container, fragmentgraphic);
                transaction.commit();
            }
        }
        return rootview;
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

                break;
        }
    }

    @Override
    public void oncurrentlocationchanged(Location location) {
        super.oncurrentlocationchanged(location);
    }

    public void resetButtonViews(TextView view1, TextView view2, TextView view3)
    {
        view1.setBackgroundResource(R.color.videolist_background);
        view1.setTextColor(ContextCompat.getColor(applicationviavideocomposer.getactivity(),R.color.white));

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
        switch (view.getId())
        {
            case  R.id.handle:
                flingswipe.onTouchEvent(motionEvent);
                break;

            case  R.id.righthandle:
                flingswipe.onTouchEvent(motionEvent);
                break;
            case  R.id.videoSurface:
                {
                    switch (motionEvent.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            if(player != null && (! isdraweropen)) {
                                hideshowcontroller();
                            }
                            break;
                    }
                }
                break;
        }
        return true;
    }

    GestureDetector flingswipe = new GestureDetector(applicationviavideocomposer.getactivity(), new GestureDetector.SimpleOnGestureListener()
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

    public void swiperighttoleft()
    {
        isdraweropen=false;
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

    public void hideshowcontroller()
    {
        if(isdraweropen)
            return;

        if(controller != null && controller.controllersview!= null)
        {
            if(controller.controllersview != null && controller.controllersview.getVisibility() == View.VISIBLE)
            {
                handleimageview.setVisibility(View.GONE);
                gethelper().updateactionbar(0);
                controller.controllersview.setVisibility(View.GONE);

            }
            else
            {
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

            controller = new videocontrollerview(applicationviavideocomposer.getactivity(),mitemclick,isscrubbing);
            if(VIDEO_URL != null && (! VIDEO_URL.isEmpty()))
            {
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.setDataSource(applicationviavideocomposer.getactivity(), Uri.parse(VIDEO_URL));
                player.prepareAsync();
                player.setOnPreparedListener(this);
                player.setOnCompletionListener(this);

                if(player!=null)
                   changeactionbarcolor();

                if(! keytype.equalsIgnoreCase(checkkey()) || (frameduration != checkframeduration()))
                {
                    frameduration=checkframeduration();
                    keytype=checkkey();
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
        maxincreasevideoduration=0;
        controller.setMediaPlayer(this);
        controller.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));
        videoduration=mp.getDuration();

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

        controller.show();

        if(fragmentgraphic != null)
            fragmentgraphic.setmediaplayer(true,player);

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
                if(player.getCurrentPosition() > maxincreasevideoduration)
                    maxincreasevideoduration=player.getCurrentPosition();

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
            player.seekTo(i);
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


    @Override
    public void onHeaderBtnClick(int btnid) {
        super.onHeaderBtnClick(btnid);
        switch (btnid){
            case R.id.img_share_icon:
                if(VIDEO_URL != null && (! VIDEO_URL.isEmpty())) {
                    common.shareMedia(applicationviavideocomposer.getactivity(),VIDEO_URL);
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

    public  void opengallery()
    {
        destroyvideoplayer();
        Intent intent;
        if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
        {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        }
        else
        {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.INTERNAL_CONTENT_URI);
        }
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra("return-data", true);
        startActivityForResult(intent,REQUESTCODE_PICK);
    }

    public int checkframeduration()
    {
        int frameduration=15;

        if(! xdata.getinstance().getSetting(config.framecount).trim().isEmpty())
            frameduration=Integer.parseInt(xdata.getinstance().getSetting(config.framecount));

        return frameduration;
    }

    public String checkkey()
    {
        String key="";
        if(xdata.getinstance().getSetting(config.hashtype).equalsIgnoreCase(config.prefs_md5) ||
                xdata.getinstance().getSetting(config.hashtype).trim().isEmpty())
        {
            key=config.prefs_md5;
        }
        else if(xdata.getinstance().getSetting(config.hashtype).equalsIgnoreCase(config.prefs_md5_salt))
        {
            key=config.prefs_md5_salt;
        }
        else if(xdata.getinstance().getSetting(config.hashtype).equalsIgnoreCase(config.prefs_sha))
        {
            key=config.prefs_sha;
        }
        else if(xdata.getinstance().getSetting(config.hashtype).equalsIgnoreCase(config.prefs_sha_salt))
        {
            key=config.prefs_sha_salt;
        }
        return key;
    }


    public void setupVideoPlayer()
    {
        try {
            player = new MediaPlayer();
            if(controller != null)
                controller.removeAllViews();

            controller = new videocontrollerview(applicationviavideocomposer.getactivity(),mitemclick,isscrubbing);

            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(applicationviavideocomposer.getactivity(), Uri.parse(VIDEO_URL));

            player.prepareAsync();
            player.setOnPreparedListener(this);
            player.setOnCompletionListener(this);
            if(player!=null)
                changeactionbarcolor();


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

            grabber.flush();

        }catch (Exception e)
        {
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
                boolean graphicopen=false;
                if(isdraweropen)
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
        if(n> metricmainarraylist.size())
            n=metricmainarraylist.size();

        for(int i=0;i<n;i++)
        {
            if(! metricmainarraylist.get(i).isIsupdated())
            {
                selectedmetrics="\n";
                metricmainarraylist.get(i).setIsupdated(true);
                ArrayList<metricmodel> metricItemArraylist = metricmainarraylist.get(i).getMetricItemArraylist();
                for(int j=0;j<metricItemArraylist.size();j++)
                {
                    selectedmetrics=selectedmetrics+"\n"+metricItemArraylist.get(j).getMetricTrackKeyName()+" - "+
                            metricItemArraylist.get(j).getMetricTrackValue();
                    common.setgraphicalitems(metricItemArraylist.get(j).getMetricTrackKeyName(),
                            metricItemArraylist.get(j).getMetricTrackValue(),true);
                }

                mmetricsitems.add(new videomodel(selectedmetrics));
                mmetricesadapter.notifyItemChanged(mmetricsitems.size()-1);
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
                    controller.setProgress();
                }
            }
        },200);

    }
}
