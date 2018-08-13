package com.cryptoserver.composer.activity;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.utils.FullScreenMediaController;

/**
 * Created by root on 13/8/18.
 */

public class FullScreenVideoActivity extends AppCompatActivity {

    private VideoView videoView;
    private MediaController mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_screen_videoview);

        videoView = findViewById(R.id.videoView);

        String videopath =  getIntent().getStringExtra("videopath");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        getSupportActionBar().hide();

        videoView.setVideoPath(videopath);

        mediaController = new FullScreenMediaController(this);
        mediaController.setAnchorView(videoView);

        videoView.setMediaController(mediaController);
        videoView.start();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}