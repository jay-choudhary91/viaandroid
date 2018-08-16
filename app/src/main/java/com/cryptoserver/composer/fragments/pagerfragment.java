package com.cryptoserver.composer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cryptoserver.composer.R;
import com.cryptoserver.composer.activity.homeactivity;
import com.cryptoserver.composer.models.intro;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


/**
 * Created by root on 26/7/18.
 */

public class pagerfragment extends Fragment {
    GifImageView imggif;
    View rootview;
    boolean isgifloaded =false;
    intro introobject=null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(rootview == null)
        {
            rootview = inflater.inflate(R.layout.fragment_pager, container, false);
            TextView txt_title = (TextView) rootview.findViewById(R.id.txt_title);
            TextView txt_description = (TextView) rootview.findViewById(R.id.txt_description);
            TextView btn_start_record = (TextView) rootview.findViewById(R.id.btn_start_record);
            imggif = (GifImageView) rootview.findViewById(R.id.img_gif);

            introobject=(intro)getArguments().getParcelable("object");

            txt_title.setText(introobject.getTitle());
            txt_description.setText(introobject.getDescription());

            if((introobject.getTitle().contains("Simply Secure")))
                loadAnimation();

            btn_start_record.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent in=new Intent(getActivity(),homeactivity.class);
                    getActivity().startActivity(in);
                    getActivity().finish();
                }
            });
        }

        return rootview;
    }


    public void loadAnimation()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isgifloaded =true;

                GifDrawable gifDrawable = null;
                try {
                    gifDrawable = new GifDrawable(getResources(), introobject.getImage());
                    //gifDrawable.setLoopCount(1000);
                    //gifDrawable.setSpeed(0.5f);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imggif.setImageDrawable(gifDrawable);
            }
        },100);
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(imggif != null)
            loadAnimation();
    }


    public static pagerfragment newInstance(intro intro) {
        pagerfragment f = new pagerfragment();
        Bundle b = new Bundle();
        b.putParcelable("object", (Parcelable) intro);
        f.setArguments(b);
        return f;
    }
}
