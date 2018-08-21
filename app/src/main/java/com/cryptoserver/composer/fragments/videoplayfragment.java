package com.cryptoserver.composer.fragments;

import android.content.ContentValues;
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
    public void getresult(final Uri uri) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(uri != null){
                    ContentValues values = new ContentValues(3);
                    values.put(MediaStore.Video.Media.TITLE, "Via composer");
                    values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
                    values.put(MediaStore.Video.Media.DATA,uri.getPath());
                    getActivity().getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);

              //      common.showalert(getActivity(),getResources().getString(R.string.video_saved_at));
                   // Toast.makeText(getactivity(), getString(R.string.video_saved_at), Toast.LENGTH_LONG).show();
                }
           }
        });
        /*Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setDataAndType(uri, "video/mp4");
        startActivity(intent);*/
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

    public void setdata(String videoPath, int duration)
    {
        this.videopath =videoPath;
        this.videoduration =  duration;
    }

    public void savevideo(){

        mvideotrimmer.onSaveClicked();
    }
}

