package com.cryptoserver.composer.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.adapter.framebitmapadapter;
import com.cryptoserver.composer.adapter.videoframeadapter;
import com.cryptoserver.composer.applicationviavideocomposer;
import com.cryptoserver.composer.interfaces.adapteritemclick;
import com.cryptoserver.composer.models.frame;
import com.cryptoserver.composer.models.videomodel;
import com.cryptoserver.composer.utils.CenterLayoutManager;
import com.cryptoserver.composer.utils.common;
import com.cryptoserver.composer.utils.config;
import com.cryptoserver.composer.utils.md5;
import com.cryptoserver.composer.utils.progressdialog;
import com.cryptoserver.composer.utils.sha;
import com.cryptoserver.composer.utils.videocontrollerview;
import com.cryptoserver.composer.utils.xdata;

import org.bytedeco.javacpp.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

/**
 * Created by devesh on 21/8/18.
 */

public class videoplayerreaderfragment extends basefragment implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener,MediaPlayer.OnCompletionListener, View.OnTouchListener,videocontrollerview.MediaPlayerControl {

    @BindView(R.id.recyview_frames)
    RecyclerView recyview_frames;
    @BindView(R.id.layout_drawer)
    LinearLayout layout_drawer;
    @BindView(R.id.layout_scrubberview)
    RelativeLayout layout_scrubberview;
    @BindView(R.id.frontview)
    RelativeLayout frontview;
    RelativeLayout scurraberverticalbar;

    private String VIDEO_URL = null;
    RelativeLayout showcontrollers;
    SurfaceView videoSurface;
    MediaPlayer player;
    videocontrollerview controller;
    View rootview = null;
    ImageView handleimageview,righthandle;
    LinearLayout linearLayout;
    RecyclerView recyviewitem;
    String keytype =config.prefs_md5;
    videoframeadapter madapter;
    long currentframenumber =0,playerposition=0;
    long frameduration =15, mframetorecordcount =0;
    ArrayList<videomodel> mvideoframes =new ArrayList<>();
    ArrayList<videomodel> mallframes =new ArrayList<>();
    ArrayList<frame> mbitmaplist =new ArrayList<>();
    long videoduration =0;
    boolean ishashprocessing=false;
    public int REQUESTCODE_PICK=201;
    private static final int request_read_external_storage = 1;
    framebitmapadapter adapter;
    Uri selectedvideouri =null;
    boolean issurafcedestroyed=false;
    @Override
    public int getlayoutid() {
        return R.layout.full_screen_videoview;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        if(rootview == null) {
            rootview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, rootview);
            frontview.setVisibility(View.VISIBLE);
            videoSurface = (SurfaceView) findViewById(R.id.videoSurface);
            linearLayout=rootview.findViewById(R.id.content);
            handleimageview=rootview.findViewById(R.id.handle);
            righthandle=rootview.findViewById(R.id.righthandle);
            recyviewitem = (RecyclerView) rootview.findViewById(R.id.recyview_item);
            showcontrollers=rootview.findViewById(R.id.video_container);
            scurraberverticalbar=rootview.findViewById(R.id.scrubberverticalbar);

            frameduration=checkframeduration();
            keytype=checkkey();

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
                    RecyclerView.LayoutManager mLayoutManager = new CenterLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    recyview_frames.setLayoutManager(mLayoutManager);
                    recyview_frames.setItemAnimator(new DefaultItemAnimator());
                    recyview_frames.setAdapter(adapter);
                }
            });

            SurfaceHolder videoHolder = videoSurface.getHolder();
            videoHolder.addCallback(this);

        }



        madapter = new videoframeadapter(getActivity(), mvideoframes, new adapteritemclick() {
            @Override
            public void onItemClicked(Object object) {

            }

            @Override
            public void onItemClicked(Object object, int type) {

            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyviewitem.setLayoutManager(mLayoutManager);
        recyviewitem.setItemAnimator(new DefaultItemAnimator());
        recyviewitem.setAdapter(madapter);

        righthandle.setVisibility(View.GONE);

        handleimageview.setOnTouchListener(this);
        righthandle.setOnTouchListener(this);
        videoSurface.setOnTouchListener(this);

        return rootview;
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
                            if(handleimageview.getVisibility() == View.GONE)
                                hideshowcontroller();
                            break;
                    }
                }
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
        if(handleimageview.getVisibility() ==  (View.VISIBLE))
        {
            layout_scrubberview.setVisibility(View.GONE);
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
            layout_scrubberview.setVisibility(View.VISIBLE);
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
        gethelper().updateactionbar(1);
        handleimageview.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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

            controller = new videocontrollerview(getActivity(),mitemclick);

            if(VIDEO_URL != null && (! VIDEO_URL.isEmpty())){
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.setDataSource(getActivity(), Uri.parse(VIDEO_URL));
                player.prepareAsync();
                player.setOnPreparedListener(this);
                player.setOnCompletionListener(this);

                if(! keytype.equalsIgnoreCase(checkkey()) || (frameduration != checkframeduration()))
                {
                    frameduration=checkframeduration();
                    keytype=checkkey();

                    mvideoframes.clear();
                    mallframes.clear();
                    madapter.notifyDataSetChanged();
                    Thread thread = new Thread(){
                        public void run(){
                            setVideoAdapter();
                        }
                    };
                    thread.start();
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

        /*try {
            if(player != null && player.getDuration() > 0)
            {
                int a=player.getDuration();
                int b=player.getCurrentPosition();


                if(player.getCurrentPosition() == 0)
                {
                    player.seekTo(player.getCurrentPosition());
                }
                else
                {
                    player.seekTo(100);
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }*/

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
    public void onPrepared(MediaPlayer mp) {
        controller.setMediaPlayer(this);
        controller.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));
        //mp.start();
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
        frontview.setVisibility(View.GONE);

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
               // Log.e("getCurrentPosition ",""+player.getCurrentPosition());
                if(mbitmaplist.size() > 0)
                {
                    int second=player.getCurrentPosition()/1000;
                    if(mbitmaplist.size() >= (second))
                    {
                        recyview_frames.scrollToPosition(second);
                        recyview_frames.smoothScrollToPosition(second);
                        //recyview_frames.scrollTo((720/2)+(second*50),0);
                    }
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
            Log.e("seek to ",""+i);
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
                    //progressdialog.showwaitingdialog(getActivity());
                    common.shareMedia(getActivity(),VIDEO_URL);
                }
                break;
            case R.id.img_menu:
                checkwritestoragepermission();
                break;
            case R.id.img_setting:
                if(ishashprocessing)
                {
                    Toast.makeText(getActivity(),"Currently hash process is running...",Toast.LENGTH_SHORT).show();
                    return;
                }
                /*if(player != null)
                    player.pause();*/


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
        if(ishashprocessing)
        {
            Toast.makeText(getActivity(),"Currently hash process is running...",Toast.LENGTH_SHORT).show();
            return;
        }

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODE_PICK) {

            if (resultCode == RESULT_OK) {
                layout_scrubberview.setVisibility(View.GONE);

                selectedvideouri = data.getData();

                try {
                    //VIDEO_URL=common.getUriRealPath(applicationviavideocomposer.getactivity(),selectedvideouri);
                    VIDEO_URL = common.getpath(getActivity(), selectedvideouri);
                }catch (Exception e)
                {
                    e.printStackTrace();
                    common.showalert(getActivity(),getResources().getString(R.string.file_uri_parse_error));
                    return;
                }

                if(VIDEO_URL == null || (VIDEO_URL.trim().isEmpty()))
                {
                    common.showalert(getActivity(),getResources().getString(R.string.file_doesnot_exist));
                    return;
                }

                if(! (new File(VIDEO_URL).exists()))
                {
                    common.showalert(getActivity(),getResources().getString(R.string.file_doesnot_exist));
                    return;
                }

                frameduration=checkframeduration();
                keytype=checkkey();

                mbitmaplist.clear();
                adapter.notifyDataSetChanged();

                layout_scrubberview.setVisibility(View.VISIBLE);
                playerposition=0;
                setupVideoPlayer(selectedvideouri);
                righthandle.setVisibility(View.VISIBLE);

                if(VIDEO_URL != null && (! VIDEO_URL.isEmpty())){
                    mvideoframes.clear();
                    mallframes.clear();
                    madapter.notifyDataSetChanged();
                    Thread thread = new Thread(){
                        public void run(){
                            try {
                                getFramesBitmap();
                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }

                            setVideoAdapter();
                        }
                    };
                    thread.start();
                }
            }
        }
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

    public void getFramesBitmap()
    {
        mbitmaplist.add(new frame(0,null,true));
        MediaMetadataRetriever m_mediaMetadataRetriever = new MediaMetadataRetriever();
        m_mediaMetadataRetriever.setDataSource(VIDEO_URL);
        String time = m_mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInmillisec = Long.parseLong( time );
        long duration = timeInmillisec / 1000;
        long hours = duration / 3600;
        long minutes = (duration - hours * 3600) / 60;
        long seconds = duration - (hours * 3600 + minutes * 60);


        for(int i=1;i<=seconds;i++)
        {

            Bitmap m_bitmap = null;

            try
            {
                m_mediaMetadataRetriever = new MediaMetadataRetriever();
                m_mediaMetadataRetriever.setDataSource(VIDEO_URL);
                m_bitmap = m_mediaMetadataRetriever.getFrameAtTime(i * 1000000);
                if(m_bitmap != null)
                {
                    Log.e("Bitmap on ",""+i);
                    mbitmaplist.add(new frame(i,m_bitmap,false));
                }
            }
            catch (Exception m_e)
            {
            }
            finally
            {
                if (m_mediaMetadataRetriever != null)
                    m_mediaMetadataRetriever.release();
            }
        }
        mbitmaplist.add(new frame(0,null,true));

        applicationviavideocomposer.getactivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                adapter.notifyDataSetChanged();
                scurraberverticalbar.setVisibility(View.VISIBLE);
            }
        });
    }

    public void setupVideoPlayer(Uri selectedimageuri)
    {
        try {
            player = new MediaPlayer();
            if(controller != null)
                controller.removeAllViews();

            controller = new videocontrollerview(getActivity(),mitemclick);

            if(selectedimageuri!=null){
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.setDataSource(getActivity(), selectedimageuri);

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
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(VIDEO_URL);

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
                    notifydata();

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
            }
            else
            {
                if(mvideoframes.size() > 0)
                    mvideoframes.get(mvideoframes.size()-1).settitle("Last Frame ");
            }

            notifydata();

            ishashprocessing=false;
            grabber.flush();

        }catch (Exception e)
        {
            e.printStackTrace();
            ishashprocessing=false;
        }
    }

    public void notifydata()
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mvideoframes.size() > 0)
                    madapter.notifyItemChanged(mvideoframes.size()-1);
            }
        });
    }


    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        controller.setplaypauuse();
    }

}