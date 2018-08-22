package com.cryptoserver.composer.fragments;

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
import com.cryptoserver.composer.utils.common;
import com.cryptoserver.composer.utils.videocontrollerview;

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
                player.prepareAsync();
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
    public void onRestart() {
        try {
            player = new MediaPlayer();
            controller.removeAllViews();
            controller = new videocontrollerview(getActivity());

            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(getActivity(), Uri.parse(VIDEO_URL));
            player.prepareAsync();
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
            player.reset();
            player.release();
            player = null;
        }
    }
    // End SurfaceHolder.Callback

    // Implement MediaPlayer.OnPreparedListener
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
    public void toggleFullScreen() {

    }

    public void setdata(String VIDEO_URL){
        this.VIDEO_URL = VIDEO_URL;
    }


    @Override
    public void onHeaderBtnClick(int btnid) {
        super.onHeaderBtnClick(btnid);

        switch (btnid){

            case R.id.img_share_icon:

                common.shareMedia(getActivity(),VIDEO_URL);


                break;


        }
    }
}
