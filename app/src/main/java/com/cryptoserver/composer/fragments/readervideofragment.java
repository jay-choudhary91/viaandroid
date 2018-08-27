package com.cryptoserver.composer.fragments;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.activity.homeactivity;
import com.cryptoserver.composer.adapter.videoframeadapter;
import com.cryptoserver.composer.interfaces.adapteritemclick;
import com.cryptoserver.composer.models.videomodel;
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
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

/**
 * Created by devesh on 21/8/18.
 */

public class readervideofragment extends basefragment implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, videocontrollerview.MediaPlayerControl {

    private String VIDEO_URL = null;

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
    long videoduration =0;
    boolean stopprevious=false;
    public int REQUESTCODE_PICK=201;
    @Override
    public int getlayoutid() {
        return R.layout.full_screen_videoview;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(rootview == null) {
            rootview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, rootview);

            videoSurface = (SurfaceView) findViewById(R.id.videoSurface);
            linearLayout=rootview.findViewById(R.id.content);
            handleimageview=rootview.findViewById(R.id.handle);
            righthandle=rootview.findViewById(R.id.righthandle);
            recyviewitem = (RecyclerView) rootview.findViewById(R.id.recyview_item);


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

        return rootview;
    }

    public void onRestart() {
        try {
            player = new MediaPlayer();
            if(controller != null)
                controller.removeAllViews();

            controller = new videocontrollerview(getActivity());

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
            player.setDisplay(holder);

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
        player.start();
        controller.show();
    }
    // End MediaPlayer.OnPreparedListener

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
                return player.getCurrentPosition();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int getDuration() {
        if(player != null && player.isPlaying())
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
                progressdialog.showwaitingdialog(getActivity());
                common.shareMedia(getActivity(),VIDEO_URL);
                break;
            case R.id.img_menu:
                opengallery();
                break;
        }
    }

    public  void opengallery()
    {
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
            Uri selectedimageuri;
            if (resultCode == RESULT_OK) {
                selectedimageuri = data.getData();
                // OI FILE Manager
                VIDEO_URL = common.getpath(getActivity(), selectedimageuri);


                if(VIDEO_URL == null){
                    common.showalert(getActivity(),getResources().getString(R.string.file_not_supported));

                    return;
                }
                setupVideoPlayer(selectedimageuri);

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
        }
    }

    public void setupVideoPlayer(Uri selectedimageuri)
    {
        try {
            SurfaceHolder videoHolder = videoSurface.getHolder();
            videoHolder.addCallback(this);

            player = new MediaPlayer();
            if(controller != null)
                controller.removeAllViews();

            controller = new videocontrollerview(getActivity());

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

            grabber.flush();

        }catch (Exception e)
        {
            e.printStackTrace();
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
