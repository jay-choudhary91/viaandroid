package com.cryptoserver.composer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.activity.homeactivity;
import com.cryptoserver.composer.models.intro;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


/**
 * Created by root on 26/7/18.
 */

public class headerpagerfragment extends Fragment {
    GifImageView imggif;
    View rootview;
    boolean isgifloaded =false;
    intro introobject=null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(rootview == null)
        {
            rootview = inflater.inflate(R.layout.fragment_pager_header, container, false);
            imggif = (GifImageView) rootview.findViewById(R.id.img_gif);

            introobject=(intro)getArguments().getParcelable("object");

            if((introobject.getTitle().contains("Simply Secure")))
                loadAnimation();
        }

        return rootview;
    }


    public void loadAnimation()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isgifloaded =true;

                try {
                    GifDrawable gifDrawable = null;
                    try {
                        gifDrawable = new GifDrawable(getResources(), introobject.getImage());
                        //gifDrawable.setLoopCount(1000);
                        gifDrawable.setSpeed(0.9f);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    imggif.setImageDrawable(gifDrawable);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        },100);
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(imggif != null)
            loadAnimation();
    }


    public static headerpagerfragment newInstance(intro intro) {
        headerpagerfragment f = new headerpagerfragment();
        Bundle b = new Bundle();
        b.putParcelable("object", (Parcelable) intro);
        f.setArguments(b);
        return f;
    }
}