package com.cryptoserver.composer.activity;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.utils.Listeners;

import uk.co.jakelee.vidsta.VidstaPlayer;

/**
 * Created by root on 13/8/18.
 */

public class FullScreenVideoActivity extends AppCompatActivity  {

    private String VIDEO_URL = "http://www.quirksmode.org/html5/videos/big_buck_bunny.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_screen_videoview);

        VIDEO_URL =  getIntent().getStringExtra("videopath");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        VidstaPlayer player = (VidstaPlayer) findViewById(R.id.player);

        //player.setVideoSource(VIDEO_URL);
        player.setVideoSource(Uri.parse(VIDEO_URL));

        Listeners listeners = new Listeners((LinearLayout)findViewById(R.id.listenerMessages));
        player.setOnVideoBufferingListener(listeners.bufferingListener);
        player.setOnVideoErrorListener(listeners.errorListener);
        player.setOnVideoFinishedListener(listeners.finishedListener);
        player.setOnVideoPausedListener(listeners.pausedListener);
        player.setOnVideoRestartListener(listeners.restartListener);
        player.setOnVideoStartedListener(listeners.startedListener);
        player.setOnVideoStoppedListener(listeners.stoppedListener);
        player.setOnFullScreenClickListener(listeners.fullScreenListener);
        player.setPlayButtonDrawable(R.drawable.custom_video_play);
        player.setPauseButtonDrawable(R.drawable.custom_video_pause);
        player.setRetryButtonDrawable(R.drawable.custom_video_retry);
        player.setFullscreenEnterDrawable(R.drawable.custom_video_screen_fullscreen_enter);
        player.setFullscreenExitDrawable(R.drawable.custom_video_screen_fullscreen_exit);
        player.setPreviousButtonDrawable(R.drawable.custom_video_previous);
        player.setNextButtonDrawable(R.drawable.custom_video_next);
        player.setTextColor(Color.WHITE);
        player.setButtonTintColor(Color.BLACK);
        player.setAutoLoop(false);
        player.setAutoPlay(true);

    }
}