package com.cryptoserver.composer.fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.utils.common;
import com.cryptoserver.composer.utils.progressdialog;
import com.cryptoserver.composer.videoTrimmer.hglvideotrimmer;
import com.cryptoserver.composer.videoTrimmer.interfaces.onhglvideolistener;
import com.cryptoserver.composer.videoTrimmer.interfaces.ontrimvideolistener;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by devesh on 14/5/18.
 */

public class videoplayfragment extends basefragment implements View.OnClickListener, ontrimvideolistener, onhglvideolistener {

    @BindView(R.id.timeLine)
    hglvideotrimmer mvideotrimmer;

    private progressdialog mprogressdialog;
    View rootview = null;
    String videopath;
    int videoduration;

    @Override
    public int getlayoutid() {

        return R.layout.activity_trimmer;

    }

    @Override
    public void initviews(View parent, Bundle savedInstanceState) {
        super.initviews(parent, savedInstanceState);

        gethelper().updateheader("");
        ButterKnife.bind(this, parent);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(rootview == null) {
            rootview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, rootview);

           // videoduration = (int)common.getvideoduration(videopath);
            if (mvideotrimmer != null) {
                mvideotrimmer.setMaxDuration(videoduration);
                mvideotrimmer.setOnTrimVideoListener(this);
                mvideotrimmer.setOnHgLVideoListener(this);
                mvideotrimmer.setVideoURI(Uri.parse(videopath));
                mvideotrimmer.setVideoInformationVisibility(true);
            }
        }
        return rootview;
    }

    @Override
    public void onClick(View view) {

    }


    @Override
    public void ontrimstarted() {
    }

    @Override
    public void getresult(Uri uri) {

    }

    @Override
    public void getresult(final String filePath) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(filePath != null){
                    //progressdialog.showwaitingdialog(getActivity());
                    String selectedvideopath = filePath;
                    common.shareMedia(getActivity(),selectedvideopath);
                }
           }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        progressdialog.dismisswaitdialog();
    }

    @Override
    public void cancelaction() {
        mvideotrimmer.destroy();
    }

    @Override
    public void onerror(final String message) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onvideoprepared() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
               // Toast.makeText(getactivity(), "onvideoprepared", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onHeaderBtnClick(int btnid) {
        super.onHeaderBtnClick(btnid);
        switch (btnid)
        {
            case R.id.img_share_icon:

                if(mvideotrimmer != null)
                {
                    //progressdialog.showwaitingdialog(getActivity());
                    mvideotrimmer.onSaveClicked();
                }

                break;

            case R.id.img_menu:
                gethelper().onBack();
                break;
        }
    }

    public void setdata(String videoPath, int duration)
    {
        this.videopath =videoPath;
        this.videoduration =  duration;
    }
}

