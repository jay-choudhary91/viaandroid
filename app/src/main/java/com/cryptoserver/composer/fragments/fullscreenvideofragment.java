package com.cryptoserver.composer.fragments;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.utils.videocontrollerview;
import com.cryptoserver.composer.videoTrimmer.interfaces.onhglvideolistener;
import com.cryptoserver.composer.videoTrimmer.interfaces.ontrimvideolistener;

import java.io.IOException;

import butterknife.ButterKnife;

/**
 * Created by devesh on 21/8/18.
 */

public class fullscreenvideofragment extends basefragment implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, videocontrollerview.MediaPlayerControl {

    private String VIDEO_URL = null;

    SurfaceView videoSurface;
    MediaPlayer player;
    videocontrollerview controller;
    View rootview = null;


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
            SurfaceHolder videoHolder = videoSurface.getHolder();
            videoHolder.addCallback(this);

            player = new MediaPlayer();
            controller = new videocontrollerview(getActivity());

            try {

                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.setDataSource(getActivity(), Uri.parse(VIDEO_URL));
                player.setOnPreparedListener(this);

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
        return rootview;
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

    // Implement SurfaceHolder.Callback
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        Log.e("surfaceChanged method =", "surfaceChanged" );
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        Log.e("surfaceCreated method =", "surfaceCreated" );
        player.setDisplay(holder);
        player.prepareAsync();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        Log.e("surfaceDestroyedmethod=", "surfaceDestroyed" );
        if (player != null) {

            Handler mHandler = new Handler();
                mHandler.removeCallbacks(new Runnable() {
                    @Override
                    public void run() {
                        controller.hide();
                        player.reset();
                        player.release();
                        player = null;
                    }
                });

        }
    }
    // End SurfaceHolder.Callback

    // Implement MediaPlayer.OnPreparedListener
    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.e("onPrepared method =", "onPrepared" );
        controller.setMediaPlayer(this);
        controller.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));
        player.start();
        controller.show();
    }
    // End MediaPlayer.OnPreparedListener

    // Implement VideoMediaController.MediaPlayerControl
    @Override
    public boolean canPause() {
        Log.e("canPause method =", "canPause" );
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        Log.e("canSeekBackward method=", "canSeekBackward" );
        return true;
    }

    @Override
    public boolean canSeekForward() {

        Log.e("canSeekForward method=", "canSeekForward" );
        return true;
    }

    @Override
    public int getBufferPercentage() {
        Log.e("getBufferPertag method=", "getBufferPercentage" );
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        try {
            Log.e("getCurrentPos method=", "getCurrentPosition" );
            return player.getCurrentPosition();
        }catch (Exception e)
        {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int getDuration() {

        Log.e("getDuration method =", "getDuration" );

        return player.getDuration();
    }

    @Override
    public boolean isPlaying() {
        Log.e("isPlaying method =", "isPlaying" );
        return player.isPlaying();
    }

    @Override
    public void pause() {
        Log.e("pause method =", "pause" );
        player.pause();
    }

    @Override
    public void seekTo(int i) {
        Log.e("seekTo method =", "seekTo" );
        player.seekTo(i);
    }

    @Override
    public void start() {
        Log.e("start method =", "start" );
        player.start();
    }

    @Override
    public boolean isFullScreen() {
        Log.e("isFullScreen method =", "isFullScreen" );
        return false;
    }

    @Override
    public void toggleFullScreen() {
        Log.e("toggleFullScreenmethod=", "toggleFullScreen" );

    }
    // End VideoMediaController.MediaPlayerControl

    public void setdata(String VIDEO_URL){
        this.VIDEO_URL = VIDEO_URL;
    }

    @Override
    public void onResume() {
        super.onResume();


        // player = new MediaPlayer();
    }
}
