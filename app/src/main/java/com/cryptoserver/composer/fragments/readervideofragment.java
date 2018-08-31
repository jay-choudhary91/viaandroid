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
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.cryptoserver.composer.views.pagercustomduration;

import org.bytedeco.javacpp.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.jcodec.common.tools.MainUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

/**
 * Created by devesh on 21/8/18.
 */

public class readervideofragment extends basefragment implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, videocontrollerview.MediaPlayerControl {

    @BindView(R.id.recyview_frames)
    RecyclerView recyview_frames;
    @BindView(R.id.layout_drawer)
    LinearLayout layout_drawer;
    @BindView(R.id.layout_scrubberview)
    RelativeLayout layout_scrubberview;

    View scurraberverticalbar;

    private String VIDEO_URL = null;
    RelativeLayout showcontrollers;
    SurfaceView videoSurface;
    MediaPlayer player;
    videocontrollerview controller;
    View rootview = null;
    ImageView handleimageview,righthandle;
    LinearLayout linearLayout;
    RecyclerView recyviewitem;
    String keytype ="md5";
    videoframeadapter madapter;
    long currentframenumber =0;
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
    @Override
    public int getlayoutid() {
        return R.layout.full_screen_videoview;
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
            recyviewitem = (RecyclerView) rootview.findViewById(R.id.recyview_item);
            showcontrollers=rootview.findViewById(R.id.video_container);
            scurraberverticalbar=rootview.findViewById(R.id.scrubberverticalbar);

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

        }


        handleimageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });

        righthandle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });
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

        videoSurface.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        /*touched = true;*/
                        if(righthandle.getVisibility() == View.GONE)
                            hideshowcontroller();

                        Log.e("user touch","on touch" );

                        break;

                    case MotionEvent.ACTION_UP:
                        /*touched = false;*/
                        Log.e("on touch end ","on touch end" );
                        break;
                }
                return false;
            }
        });
        return rootview;
    }

    public void hideshowcontroller()
    {
        if(righthandle.getVisibility() ==  (View.VISIBLE))
        {
            layout_scrubberview.setVisibility(View.GONE);
            righthandle.setVisibility(View.GONE);
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
            righthandle.setVisibility(View.VISIBLE);
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
        try {
            player = new MediaPlayer();
            if(controller != null)
                controller.removeAllViews();

            controller = new videocontrollerview(getActivity(),mitemclick);

            if(VIDEO_URL != null && (! VIDEO_URL.isEmpty())){
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.setDataSource(getActivity(), Uri.parse(VIDEO_URL));
                player.prepareAsync();
                player.setOnPreparedListener(this);
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
    public void onDestroy() {
        super.onDestroy();

        if(player != null && player.isPlaying())
        {
            player.pause();
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


    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (player != null) {
            player.pause();
         //   player.reset();
           // player.release();
            //player = null;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        controller.setMediaPlayer(this);
        controller.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));
        //mp.start();
        mp.seekTo(100);
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
                Log.e("getCurrentPosition ",""+player.getCurrentPosition());
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
                    progressdialog.showwaitingdialog(getActivity());
                    common.shareMedia(getActivity(),VIDEO_URL);
                }
                break;
            case R.id.img_menu:
                checkwritestoragepermission();
                break;
            case R.id.img_setting:
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

    public  void opengallery()
    {
        if(ishashprocessing)
        {
            Toast.makeText(getActivity(),"Currently hash process is running...",Toast.LENGTH_SHORT).show();
            return;
        }

        if(player != null)
            player.pause();

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
                selectedvideouri = data.getData();
                // OI FILE Manager
                VIDEO_URL = common.getpath(getActivity(), selectedvideouri);


                if(VIDEO_URL == null){
                    common.showalert(getActivity(),getResources().getString(R.string.file_not_supported));

                    return;
                }

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
                adapter.notifyDataSetChanged();
                scurraberverticalbar.setVisibility(View.VISIBLE);

                setupVideoPlayer(selectedvideouri);

                if(VIDEO_URL != null && (! VIDEO_URL.isEmpty())){
                    currentframenumber=0;
                    mvideoframes.clear();
                    mallframes.clear();
                    madapter.notifyDataSetChanged();
                    Thread thread = new Thread(){
                        public void run(){
                            getFramesBitmap();
                            setVideoAdapter();
                        }
                    };
                    thread.start();
                }
            }
        }
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
            }
        });
    }

    public void setupVideoPlayer(Uri selectedimageuri)
    {
        try {
            SurfaceHolder videoHolder = videoSurface.getHolder();
            videoHolder.addCallback(this);

            player = new MediaPlayer();
            if(controller != null)
                controller.removeAllViews();

            controller = new videocontrollerview(getActivity(),mitemclick);

            if(selectedimageuri!=null){
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.setDataSource(getActivity(), selectedimageuri);

                player.prepareAsync();
                player.setOnPreparedListener(this);
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
        currentframenumber = currentframenumber + frameduration;
       try
        {
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(VIDEO_URL);

            grabber.setPixelFormat(avutil.AV_PIX_FMT_RGB24);
            String format= common.getvideoformat(VIDEO_URL);
            if(format.equalsIgnoreCase("mp4"))
               grabber.setFormat(format);


           grabber.start();

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
                    mvideoframes.add(new videomodel("Frame ", keytype, currentframenumber,keyValue));
                    notifydata();

                    currentframenumber = currentframenumber + frameduration;
                }
                count++;
            }

            if(mallframes.size() > 0 && mvideoframes.size() > 0)
            {
                if(! mvideoframes.get(mvideoframes.size()-1).getkeyvalue().equals(mallframes.get(mallframes.size()-1).getkeyvalue()))
                {
                    mvideoframes.add(new videomodel("Last Frame ", mallframes.get(mallframes.size()-1).getkeytype()
                            , mallframes.get(mallframes.size()-1).getcurrentframenumber(), mallframes.get(mallframes.size()-1).getkeyvalue()));
                    notifydata();
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

}
