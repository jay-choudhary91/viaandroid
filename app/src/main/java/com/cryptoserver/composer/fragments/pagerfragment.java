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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cryptoserver.composer.R;
import com.cryptoserver.composer.models.intro;


/**
 * Created by root on 26/7/18.
 */

public class pagerfragment extends Fragment {
    ImageView img_image,img_gif;
    WebView webview;
    View RootView;
    boolean isGifLoaded=false;
    int gifFile=0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(RootView == null)
        {
            RootView = inflater.inflate(R.layout.fragment_pager, container, false);
            TextView txt_title = (TextView) RootView.findViewById(R.id.txt_title);
            TextView txt_description = (TextView) RootView.findViewById(R.id.txt_description);
            TextView btn_start_record = (TextView) RootView.findViewById(R.id.btn_start_record);
            img_image = (ImageView) RootView.findViewById(R.id.img_image);
            img_gif = (ImageView) RootView.findViewById(R.id.img_gif);
            webview = (WebView) RootView.findViewById(R.id.webview);

            intro introobject=(intro)getArguments().getParcelable("object");

            txt_title.setText(introobject.getTitle());
            txt_description.setText(introobject.getDescription());
            //img_image.setImageResource(introobject.getImage());
            //tv.setBackgroundColor(Color.parseColor(getArguments().getString("color")));

            if((! isGifLoaded))
                loadAnimation();
        }

        return RootView;
    }


    public void loadAnimation()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isGifLoaded=true;
               // webview.loadUrl("file:///android_asset/globe.html");
                Glide.with(getActivity()).load(R.drawable.globe).diskCacheStrategy(DiskCacheStrategy.SOURCE).
                        fitCenter().
                        crossFade().
                        into(img_gif);

                /*Glide.with(getActivity()).load("file:///android_asset/globe.gif").diskCacheStrategy(DiskCacheStrategy.SOURCE).
                      fitCenter().
                      crossFade().
                      into(img_gif);*/

/*                Glide.with(getContext()).load("file:///android_asset/nicolas.html")
                        .asGif()
                        .fitCenter()
                        .crossFade()
                        .into(img_gif);*/
            }
        },100);
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(img_gif != null && (! isGifLoaded))
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
